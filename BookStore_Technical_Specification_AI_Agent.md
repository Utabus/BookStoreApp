# BOOK STORE --- Technical Specification & AI Coding Agent Plan

**Project type:** Android Mobile Book Store\
**Purpose:** University project / coursework\
**Language:** Java\
**UI:** XML\
**Architecture:** MVVM\
**Backend:** Firebase\
**Target roles:** USER, ADMIN\
**Payment V1:** COD\
**Document status:** Implementation specification\
**Audience:** AI coding agent / Android developer

------------------------------------------------------------------------

# 1. Project Objective

Build a complete Android native application for selling books.

The application has two roles:

-   `USER`: browse books, search, filter, view details, manage cart,
    checkout, track orders, review books, manage profile.
-   `ADMIN`: dashboard, book CRUD, category CRUD, order management, user
    management.

The first implementation must prioritize:

1.  Correct functionality.
2.  Clean architecture.
3.  Firebase integration.
4.  Maintainable Java code.
5.  Clear separation between UI, ViewModel, Repository, and Firebase.
6.  No hard-coded business data.
7.  Safe Firebase Security Rules.
8.  Incremental implementation and testing.

This is a specification document. The AI coding agent must implement
according to this document and must not silently change business
requirements.

------------------------------------------------------------------------

# 2. Fixed Technology Stack

## 2.1 Android

-   Android Native
-   Java
-   XML layouts
-   Android Studio
-   Gradle

## 2.2 Architecture

Use MVVM:

``` text
Activity / Fragment
        |
        v
    ViewModel
        |
        v
    Repository
        |
        v
 Firebase SDK
```

Responsibilities:

### Activity / Fragment

Responsible for:

-   Rendering UI.
-   Receiving user interaction.
-   Observing ViewModel state.
-   Navigation.
-   Showing dialogs/toasts/snackbars.

Must NOT contain complex Firebase business logic.

### ViewModel

Responsible for:

-   UI state.
-   Calling repositories.
-   Coordinating screen-level operations.
-   Exposing `LiveData` or observable state.
-   Surviving configuration changes.

### Repository

Responsible for:

-   Firebase Authentication.
-   Firestore operations.
-   Storage operations.
-   FCM-related operations.
-   Mapping Firebase results to application models.

### Model

Responsible for representing application data.

------------------------------------------------------------------------

# 3. Firebase Services

Use:

-   Firebase Authentication
-   Cloud Firestore
-   Firebase Storage
-   Firebase Cloud Messaging

Firebase Authentication:

``` text
Email + Password
```

Firestore:

``` text
users
categories
books
orders
reviews
```

Storage:

``` text
books/{bookId}/...
users/{userId}/...
```

FCM:

-   Order created.
-   Order confirmed.
-   Order shipping.
-   Order delivered.

------------------------------------------------------------------------

# 4. User Roles

Exactly two roles are required:

``` text
USER
ADMIN
```

## USER permissions

A USER can:

-   Register.
-   Login.
-   Logout.
-   Edit profile.
-   View active books.
-   Search books.
-   Filter books.
-   Sort books.
-   View categories.
-   View book details.
-   Add books to cart.
-   Update cart quantity.
-   Remove cart items.
-   Checkout.
-   Create orders.
-   View own orders.
-   Cancel eligible orders.
-   Review purchased books.
-   Change password.

A USER must NOT:

-   Change their role.
-   Change another user's data.
-   Change book price.
-   Change book stock directly.
-   Create/update/delete categories.
-   Create/update/delete arbitrary books.
-   Read another user's private orders.

## ADMIN permissions

ADMIN can:

-   View dashboard.
-   CRUD books.
-   CRUD categories.
-   Manage orders.
-   Manage users.
-   Enable/disable users.
-   View sales statistics.

ADMIN must NOT:

-   Disable their own account through the user-management screen.
-   Bypass validation.
-   Modify immutable order history without an explicit business rule.

------------------------------------------------------------------------

# 5. Authentication Requirements

## 5.1 Register

Registration fields:

``` text
email
password
confirmPassword
fullName
phone
address
```

Validation:

-   Email is required.
-   Email must be valid.
-   Password is required.
-   Password must meet Firebase Authentication minimum requirements.
-   Confirm password must equal password.
-   Full name is required.
-   Phone is required.
-   Address is required.

Registration flow:

``` text
Register Screen
      |
      v
Validate Input
      |
      v
Firebase Authentication
      |
      v
Create Firebase UID
      |
      v
Create Firestore users/{uid}
      |
      v
Navigate to Home
```

Default role:

``` text
USER
```

The client must never allow a user to select `ADMIN` during
registration.

## 5.2 Login

Login fields:

``` text
email
password
```

Flow:

``` text
Login
  |
  v
Firebase Auth
  |
  v
Get current UID
  |
  v
Read users/{uid}
  |
  +---- role USER ----> User MainActivity
  |
  +---- role ADMIN ---> Admin MainActivity
```

## 5.3 Session

Use FirebaseAuth current user as the authentication source.

Splash screen checks:

``` text
FirebaseAuth.getCurrentUser()
```

If null:

``` text
Login
```

If not null:

``` text
Read user role
```

Then navigate to the correct main screen.

Do not store passwords locally.

------------------------------------------------------------------------

# 6. Firestore Data Model

## 6.1 users

Collection:

``` text
users/{uid}
```

Fields:

``` text
uid: String
email: String
fullName: String
phone: String
address: String
avatarUrl: String
role: String
isActive: Boolean
createdAt: Timestamp
updatedAt: Timestamp
```

Example:

``` json
{
  "uid": "firebase_uid",
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "phone": "0900000000",
  "address": "Ho Chi Minh City",
  "avatarUrl": "",
  "role": "USER",
  "isActive": true,
  "createdAt": "server timestamp",
  "updatedAt": "server timestamp"
}
```

------------------------------------------------------------------------

# 7. Category Model

Collection:

``` text
categories/{categoryId}
```

Fields:

``` text
id: String
name: String
description: String
imageUrl: String
isActive: Boolean
createdAt: Timestamp
updatedAt: Timestamp
```

Example categories:

``` text
Programming
Technology
Business
Novel
Education
Foreign Language
History
Self Development
```

Category deletion rule:

If a category is currently used by active books, the admin UI should
warn the admin and preferably prevent deletion.

------------------------------------------------------------------------

# 8. Book Model

Collection:

``` text
books/{bookId}
```

Fields:

``` text
id: String
title: String
author: String
description: String
price: double
salePrice: double
imageUrl: String
categoryId: String
categoryName: String
isbn: String
publisher: String
publishedYear: int
stock: int
soldCount: int
rating: double
reviewCount: int
isActive: Boolean
createdAt: Timestamp
updatedAt: Timestamp
```

Business rules:

-   `title` required.
-   `author` required.
-   `price > 0`.
-   `salePrice >= 0`.
-   If salePrice is used, it should not exceed price.
-   `stock >= 0`.
-   `publishedYear` must be reasonable.
-   Category is required.
-   Active books are visible to users.
-   Inactive books are hidden from normal user browsing.

Display price:

``` text
if salePrice > 0 and salePrice < price:
    display salePrice
else:
    display price
```

Never store a negative price.

------------------------------------------------------------------------

# 9. Cart Model

Cart is user-specific.

Recommended Firestore structure:

``` text
users/{uid}/cart/{bookId}
```

Fields:

``` text
bookId: String
title: String
imageUrl: String
unitPrice: double
quantity: int
createdAt: Timestamp
updatedAt: Timestamp
```

Use the effective price at the time the cart item is created.

However, checkout must re-read the latest book data before creating the
order.

Cart rules:

-   Quantity must be \>= 1.
-   Quantity must not exceed available stock.
-   User can only modify their own cart.
-   Removing an item deletes the cart document.
-   Cart total is calculated from selected items.

------------------------------------------------------------------------

# 10. Order Model

Collection:

``` text
orders/{orderId}
```

Fields:

``` text
id: String
userId: String
receiverName: String
phone: String
address: String
note: String
items: List<OrderItem>
subtotal: double
shippingFee: double
total: double
paymentMethod: String
status: String
createdAt: Timestamp
updatedAt: Timestamp
```

OrderItem:

``` text
bookId: String
title: String
imageUrl: String
quantity: int
unitPrice: double
subtotal: double
```

Important:

OrderItem stores a snapshot of:

-   title
-   image
-   price

Do not depend on the current Book document to render historical order
details.

------------------------------------------------------------------------

# 11. Order Status

Allowed statuses:

``` text
PENDING
CONFIRMED
SHIPPING
DELIVERED
CANCELLED
```

Normal transition:

``` text
PENDING
   |
   v
CONFIRMED
   |
   v
SHIPPING
   |
   v
DELIVERED
```

Cancellation:

``` text
PENDING -> CANCELLED
```

The app must reject invalid transitions.

Example:

``` text
DELIVERED -> PENDING
```

must not be allowed.

Users can cancel only orders with:

``` text
PENDING
```

unless a later business rule explicitly changes this.

------------------------------------------------------------------------

# 12. Checkout

Checkout screen fields:

``` text
Receiver Name
Phone
Address
Note
Payment Method
```

V1 payment method:

``` text
COD
```

Shipping fee can be a configurable constant for V1.

Recommended:

``` text
SHIPPING_FEE = 30000
```

This must be stored in a centralized constant/configuration and not
duplicated across multiple classes.

Checkout flow:

``` text
Cart
 |
 v
Select Items
 |
 v
Checkout
 |
 v
Validate Receiver Data
 |
 v
Re-check current Book data
 |
 v
Check stock
 |
 v
Calculate price
 |
 v
Create Order
 |
 v
Decrease stock
 |
 v
Clear purchased cart items
 |
 v
Order Success
```

The implementation must minimize inconsistent state during checkout. For
stock and order creation, use Firestore transactions/batched writes
where appropriate.

Do not trust prices sent only from the local cart.

------------------------------------------------------------------------

# 13. Home Screen

Main sections:

``` text
Search bar

Banner

Categories

Featured Books

Best Sellers

New Books
```

Bottom navigation:

``` text
Home
Category
Cart
Orders
Profile
```

Book cards display:

``` text
Image
Title
Author
Price
Sale Price if applicable
Rating
```

------------------------------------------------------------------------

# 14. Search

Search by:

-   Book title.
-   Author.
-   ISBN.

V1 can implement practical client-side filtering when the dataset is
small.

If Firestore query limitations make a feature inefficient, do not
introduce an external search engine. Prefer a simple
Firestore-compatible implementation appropriate for a coursework
project.

Search UI:

``` text
SearchView / TextInput
RecyclerView
Empty state
```

Empty state:

``` text
No books found.
```

------------------------------------------------------------------------

# 15. Filter

Filters:

``` text
Category
Price range
Rating
Availability
```

Availability:

``` text
In stock
Out of stock
```

Filters can be combined where practical.

------------------------------------------------------------------------

# 16. Sort

Supported sort modes:

``` text
Newest
Price Low to High
Price High to Low
Best Seller
Highest Rating
```

The UI should expose sorting through a clear dialog/menu/bottom sheet.

------------------------------------------------------------------------

# 17. Book Detail Screen

Display:

``` text
Book image
Title
Author
Rating
Review count
Original price
Sale price
Stock
Description
Publisher
ISBN
Published year
Quantity selector
Add to Cart
Buy Now
```

Quantity controls:

``` text
[-] quantity [+]
```

Rules:

-   Minimum quantity = 1.
-   Maximum quantity = current stock.
-   Disable purchase when stock = 0.

------------------------------------------------------------------------

# 18. Review System

Collection:

``` text
reviews/{reviewId}
```

Fields:

``` text
id
userId
bookId
orderId
rating
comment
createdAt
updatedAt
```

Rules:

-   Rating is 1--5.
-   Comment can be optional or required depending on UI design; V1 may
    require a non-empty comment.
-   Only users with a delivered order containing the book can review.
-   A user should not create multiple reviews for the same order item
    unless explicitly supported.
-   User can edit/delete their own review.

After review changes:

``` text
Book.rating
Book.reviewCount
```

must remain consistent.

If maintaining aggregates becomes too complex, calculate ratings from
reviews for correctness in V1. Do not blindly trust a client-provided
rating.

------------------------------------------------------------------------

# 19. Profile

Profile screen:

``` text
Avatar
Full Name
Email
Phone
Address

Edit Profile
My Orders
My Reviews
Change Password
Logout
```

User can update:

``` text
fullName
phone
address
avatar
```

Email should be treated as the Firebase Authentication identity.

------------------------------------------------------------------------

# 20. Admin Dashboard

Dashboard cards:

``` text
Total Users
Total Books
Total Orders
Total Revenue
Pending Orders
```

Optional:

``` text
Best Selling Books
Recent Orders
```

Do not make dashboard statistics the first implementation priority.

------------------------------------------------------------------------

# 21. Admin Book Management

Screen:

``` text
Book List
+ Add Book
```

Each book supports:

``` text
View
Edit
Delete/Deactivate
```

Add/Edit fields:

``` text
Title
Author
Description
Price
Sale Price
Category
ISBN
Publisher
Published Year
Stock
Image
Active
```

Image flow:

``` text
Select image
    |
    v
Firebase Storage
    |
    v
Download URL
    |
    v
Firestore Book.imageUrl
```

Use Glide for image loading.

------------------------------------------------------------------------

# 22. Admin Category Management

CRUD:

``` text
Create
Read
Update
Delete
```

Fields:

``` text
Name
Description
Image
Active
```

Validate category name before save.

------------------------------------------------------------------------

# 23. Admin Order Management

Admin sees:

``` text
Order ID
Customer
Total
Payment
Status
Created Date
```

Admin can:

``` text
PENDING -> CONFIRMED
CONFIRMED -> SHIPPING
SHIPPING -> DELIVERED
PENDING -> CANCELLED
```

Admin must receive confirmation before destructive operations.

------------------------------------------------------------------------

# 24. Admin User Management

Admin can:

-   View users.
-   Search users.
-   Enable user.
-   Disable user.
-   View basic profile information.

Restrictions:

-   Cannot disable own account.
-   Cannot arbitrarily change their own role from the client UI.
-   Role changes must be tightly controlled by Security Rules.

------------------------------------------------------------------------

# 25. Notifications

Use Firebase Cloud Messaging.

Notification events:

``` text
ORDER_CREATED
ORDER_CONFIRMED
ORDER_SHIPPING
ORDER_DELIVERED
```

Example:

``` text
Your order #ORD001 has been confirmed.
```

For V1, notification implementation can be completed after the core
order flow is stable.

------------------------------------------------------------------------

# 26. Firebase Storage

Recommended paths:

``` text
books/{bookId}/{filename}
users/{userId}/{filename}
```

Storage rules:

-   Authenticated users can access permitted images.
-   Users can upload only their own avatar.
-   Admin can manage book images.
-   Do not expose unrestricted write access.

------------------------------------------------------------------------

# 27. Security Rules

Firestore rules must enforce ownership and roles.

Conceptual rules:

``` text
Authenticated USER:
    read active books
    read active categories
    read own user document
    update own profile
    read/write own cart
    read own orders
    create eligible reviews
    update/delete own reviews

ADMIN:
    manage books
    manage categories
    manage orders
    manage users
    read dashboard-related data
```

Never rely only on Android UI to protect admin features.

Security Rules are the real authorization boundary.

The coding agent must provide the Firestore Security Rules
file/configuration as part of the implementation.

------------------------------------------------------------------------

# 28. Android Package Structure

Recommended:

``` text
com.bookstore.app
|
+-- data
|   +-- model
|   |   +-- User.java
|   |   +-- Book.java
|   |   +-- Category.java
|   |   +-- CartItem.java
|   |   +-- Order.java
|   |   +-- OrderItem.java
|   |   +-- Review.java
|   |
|   +-- repository
|       +-- AuthRepository.java
|       +-- UserRepository.java
|       +-- BookRepository.java
|       +-- CategoryRepository.java
|       +-- CartRepository.java
|       +-- OrderRepository.java
|       +-- ReviewRepository.java
|
+-- ui
|   +-- auth
|   +-- user
|   |   +-- home
|   |   +-- category
|   |   +-- book
|   |   +-- cart
|   |   +-- checkout
|   |   +-- order
|   |   +-- review
|   |   +-- profile
|   |
|   +-- admin
|       +-- dashboard
|       +-- books
|       +-- categories
|       +-- orders
|       +-- users
|
+-- viewmodel
|
+-- adapter
|
+-- utils
|
+-- constants
|
+-- navigation
```

Use a consistent naming convention.

------------------------------------------------------------------------

# 29. Important Java Classes

Minimum expected classes:

## Models

``` text
User
Book
Category
CartItem
Order
OrderItem
Review
```

## Repositories

``` text
AuthRepository
UserRepository
BookRepository
CategoryRepository
CartRepository
OrderRepository
ReviewRepository
```

## ViewModels

``` text
AuthViewModel
HomeViewModel
BookViewModel
CategoryViewModel
CartViewModel
CheckoutViewModel
OrderViewModel
ReviewViewModel
ProfileViewModel
AdminDashboardViewModel
AdminBookViewModel
AdminCategoryViewModel
AdminOrderViewModel
AdminUserViewModel
```

The agent may merge ViewModels only when two screens are genuinely
simple and closely related. Do not create a huge God ViewModel.

------------------------------------------------------------------------

# 30. UI Components

Expected adapters:

``` text
BookAdapter
CategoryAdapter
CartAdapter
OrderAdapter
ReviewAdapter
UserAdapter
```

Reusable views/components may include:

``` text
BookCard
LoadingView
EmptyStateView
ErrorStateView
```

Do not duplicate identical XML layouts unnecessarily.

------------------------------------------------------------------------

# 31. UI State

Every asynchronous screen should handle:

``` text
LOADING
SUCCESS
EMPTY
ERROR
```

Example conceptual state:

``` java
public class UiState<T> {
    // loading
    // success
    // empty
    // error
}
```

The exact implementation can use LiveData and separate state fields if
that is simpler.

The UI must not remain permanently stuck in loading after an error.

------------------------------------------------------------------------

# 32. Error Handling

Handle at minimum:

``` text
No internet
Firebase unavailable
Permission denied
Authentication failure
Invalid input
Book unavailable
Insufficient stock
Image upload failure
Image loading failure
Firestore query failure
Order creation failure
```

User-facing errors should be understandable.

Do not expose raw Firebase stack traces to users.

Example:

``` text
Technical:
PERMISSION_DENIED

UI:
You do not have permission to perform this action.
```

------------------------------------------------------------------------

# 33. Loading / Empty / Error UX

Every RecyclerView-based screen should have:

``` text
Loading state
Empty state
Error state
Content state
```

Example:

``` text
Loading:
ProgressBar

Empty:
No books found.

Error:
Unable to load books.
[Retry]
```

------------------------------------------------------------------------

# 34. Navigation

Main user navigation:

``` text
Splash
 |
 +--> Login
 |      |
 |      +--> Register
 |
 +--> User Main
        |
        +--> Home
        +--> Category
        +--> Cart
        +--> Orders
        +--> Profile
        |
        +--> Book Detail
        +--> Checkout
        +--> Order Detail
        +--> Review
```

Admin:

``` text
Admin Login
    |
    v
Admin Main
    |
    +--> Dashboard
    +--> Books
    +--> Categories
    +--> Orders
    +--> Users
```

If the user is already logged in, Splash must bypass Login.

------------------------------------------------------------------------

# 35. Resource and UI Rules

Use:

``` text
strings.xml
colors.xml
dimens.xml
themes.xml
```

Do not hard-code user-facing strings inside Java code.

Avoid magic numbers in Java.

Use:

``` text
dp
sp
```

appropriately in XML.

Images should have:

-   placeholder.
-   error fallback.
-   content description where appropriate.

------------------------------------------------------------------------

# 36. Dependency Guidelines

Expected dependencies include:

``` text
Firebase Authentication
Firebase Firestore
Firebase Storage
Firebase Messaging

AndroidX
Material Components
Lifecycle ViewModel
Lifecycle LiveData
RecyclerView
Navigation if used
Glide
```

Use current stable versions compatible with the selected Android Gradle
Plugin.

The agent must not blindly paste obsolete dependency versions.

------------------------------------------------------------------------

# 37. Image Loading

Use Glide.

Requirements:

``` text
placeholder
error image
cache
```

Do not manually download images with raw networking code.

------------------------------------------------------------------------

# 38. Money Handling

For display:

``` text
150000 -> 150.000 đ
```

Do not use floating-point arithmetic carelessly for financial
calculations.

For a coursework implementation, use a consistent representation such as
integer VND where possible:

``` text
150000
```

and format only at the UI layer.

Do not store:

``` text
"150.000đ"
```

as the actual price value.

------------------------------------------------------------------------

# 39. Date/Time

Use Firebase server timestamps for important server-side timestamps:

``` text
createdAt
updatedAt
```

Display dates using a centralized formatter.

Do not duplicate date formatting logic across Activities.

------------------------------------------------------------------------

# 40. Validation

Centralize common validation:

``` text
ValidationUtils
```

At minimum:

``` text
isValidEmail()
isValidPassword()
isValidPhone()
isNotEmpty()
isPositiveNumber()
```

UI should show field-level errors.

Example:

``` text
Email
[invalid@email]
Error: Invalid email address
```

------------------------------------------------------------------------

# 41. Cart and Stock Consistency

Important business rule:

The cart is not the final authority on price or stock.

At checkout:

1.  Read current book.
2.  Check `isActive`.
3.  Check current stock.
4.  Recalculate effective price.
5.  Validate quantity.
6.  Create order.
7.  Update stock.
8.  Remove purchased cart items.

Do not trust:

``` text
cart.unitPrice
cart.stock
```

without validating against current Firestore book data.

------------------------------------------------------------------------

# 42. Order Snapshot Rule

When creating an order, copy:

``` text
bookId
title
imageUrl
quantity
unitPrice
subtotal
```

into the order item.

This preserves historical data even if the admin later changes:

``` text
book title
book price
book image
```

------------------------------------------------------------------------

# 43. Review Eligibility

To verify that a user can review a book:

``` text
Find delivered order
where:
    order.userId == currentUser.uid
    order.status == DELIVERED
    order.items contains bookId
```

Only then allow review.

Never rely only on a hidden/disabled Review button as authorization.

------------------------------------------------------------------------

# 44. Admin Authorization

Do not implement:

``` java
if (userRole.equals("ADMIN")) {
    showAdminButton();
}
```

as the only security mechanism.

The UI check is for UX.

Firebase Security Rules must independently verify admin privileges.

------------------------------------------------------------------------

# 45. Seed / Demo Data

Provide a development mechanism for sample data.

Suggested:

``` text
10+ categories
20+ books
1 admin
2–3 users
several sample orders
```

Do not put production credentials into source code.

Admin account should be created through Firebase Authentication and
assigned admin role through a controlled development/setup process.

------------------------------------------------------------------------

# 46. Testing Requirements

## Authentication

Test:

``` text
Register valid
Register duplicate email
Invalid email
Weak password
Wrong password
Logout
Login after logout
```

## Book

Test:

``` text
Book list
Search
Filter
Sort
Book detail
Out of stock
Inactive book
```

## Cart

Test:

``` text
Add
Increase
Decrease
Remove
Multiple books
Insufficient stock
```

## Checkout

Test:

``` text
Valid checkout
Missing receiver
Invalid phone
Empty cart
Stock changed before checkout
```

## Order

Test:

``` text
Create order
View order
Cancel pending order
Invalid status transition
```

## Review

Test:

``` text
Eligible review
Non-eligible review
Rating validation
Edit
Delete
```

## Admin

Test:

``` text
Book CRUD
Category CRUD
Order status
User enable/disable
Unauthorized user access
```

------------------------------------------------------------------------

# 47. Non-functional Requirements

## Performance

-   RecyclerView for lists.
-   Glide caching.
-   Avoid unnecessary Firestore reads.
-   Do not reload the entire screen after every small UI change.
-   Use pagination if the dataset grows enough to require it.

## Reliability

-   No crash from Firebase failure.
-   Retry actions where appropriate.
-   Handle lifecycle changes safely.

## Security

-   Never store passwords.
-   Enforce Firestore rules.
-   Validate role server-side through rules.
-   Do not expose secrets.

## Maintainability

-   MVVM.
-   Repository layer.
-   Reusable adapters.
-   Centralized constants.
-   Centralized validation.
-   Centralized formatting.

------------------------------------------------------------------------

# 48. Git Workflow

Recommended branches:

``` text
main
develop
feature/auth
feature/books
feature/cart
feature/orders
feature/admin
feature/review
```

Suggested commits:

``` text
feat: setup android project
feat: implement firebase authentication
feat: implement book listing
feat: implement book detail
feat: implement cart
feat: implement checkout
feat: implement order management
feat: implement reviews
feat: implement admin dashboard
fix: handle firebase errors
```

The AI agent should not modify unrelated files without a reason.

------------------------------------------------------------------------

# 49. AI Agent Coding Rules

These rules are mandatory.

## Rule 1

Do not implement the entire project in one step.

Implement phase-by-phase.

## Rule 2

Before coding a phase:

1.  Inspect current project.
2.  Inspect existing architecture.
3.  Identify files that must change.
4.  Implement only the requested phase.

## Rule 3

After coding:

1.  Build the project.
2.  Fix compilation errors.
3.  Check XML resources.
4.  Check imports.
5.  Check Firebase references.
6.  Report remaining runtime configuration requirements.

## Rule 4

Do not silently change business requirements.

If a requirement is technically impossible or conflicts with Firebase
limitations:

``` text
Explain problem
Propose solution
Wait for approval if the change affects business behavior
```

## Rule 5

Do not create fake APIs.

Do not create fake Firebase responses unless explicitly asked for
mock/demo mode.

## Rule 6

Do not replace Java with Kotlin.

## Rule 7

Do not replace XML with Compose.

## Rule 8

Do not introduce another backend.

## Rule 9

Do not add payment providers in V1.

## Rule 10

Do not put Firebase logic directly inside adapters.

------------------------------------------------------------------------

# 50. Implementation Phases

## Phase 0 --- Project Preparation

Deliver:

``` text
Android project
Java
XML
Gradle
Firebase configuration
Material theme
Package structure
Base utilities
```

Acceptance:

``` text
Project builds successfully.
App launches.
```

------------------------------------------------------------------------

## Phase 1 --- Authentication

Implement:

``` text
Splash
Login
Register
Logout
Session
Role routing
```

Deliver:

``` text
AuthRepository
AuthViewModel
User model
LoginActivity
RegisterActivity
SplashActivity
```

Acceptance:

``` text
User can register.
User can login.
Admin can login.
Wrong password shows error.
Logged-in user bypasses Login.
Logout returns to Login.
```

------------------------------------------------------------------------

## Phase 2 --- Firebase Data Layer

Implement:

``` text
Firestore models
Repositories
Firebase Storage
Security Rules
```

Acceptance:

``` text
Books can be read.
Categories can be read.
Admin-only writes are protected.
```

------------------------------------------------------------------------

## Phase 3 --- Book Browsing

Implement:

``` text
Home
Category
Book list
Book detail
Search
Filter
Sort
```

Acceptance:

``` text
User can browse books.
User can search.
User can filter.
User can sort.
Inactive books are hidden.
Out-of-stock books cannot be purchased.
```

------------------------------------------------------------------------

## Phase 4 --- Cart

Implement:

``` text
Add to cart
Remove
Quantity
Select
Total
```

Acceptance:

``` text
Cart persists for the logged-in user.
Quantity cannot exceed stock.
Cart total is correct.
```

------------------------------------------------------------------------

## Phase 5 --- Checkout

Implement:

``` text
Checkout screen
Receiver validation
COD
Order creation
Stock update
Cart cleanup
```

Acceptance:

``` text
Valid order can be created.
Invalid checkout is rejected.
Insufficient stock is rejected.
Order snapshot is correct.
Stock is updated.
Purchased cart items are removed.
```

------------------------------------------------------------------------

## Phase 6 --- Orders

Implement:

``` text
My Orders
Order Detail
Cancel
Status display
```

Acceptance:

``` text
User sees only their orders.
User can cancel PENDING.
User cannot perform invalid transitions.
```

------------------------------------------------------------------------

## Phase 7 --- Reviews

Implement:

``` text
Review list
Create review
Edit review
Delete review
Rating
```

Acceptance:

``` text
Only eligible buyers can review.
Rating is 1–5.
Review belongs to correct book/user/order.
```

------------------------------------------------------------------------

## Phase 8 --- Admin Dashboard

Implement:

``` text
Dashboard
Book CRUD
Category CRUD
```

Acceptance:

``` text
Admin can manage books.
Admin can manage categories.
Normal users cannot perform admin operations.
```

------------------------------------------------------------------------

## Phase 9 --- Admin Orders & Users

Implement:

``` text
Order management
User management
Enable/disable
```

Acceptance:

``` text
Admin can process orders.
Admin cannot disable themselves.
User cannot access admin operations.
```

------------------------------------------------------------------------

## Phase 10 --- Notifications

Implement:

``` text
Firebase Cloud Messaging
Order notifications
```

Acceptance:

``` text
User receives order status notifications.
```

------------------------------------------------------------------------

## Phase 11 --- Polish

Implement:

``` text
Loading states
Empty states
Error states
Retry
Animations
Image placeholders
UI consistency
Accessibility improvements
```

------------------------------------------------------------------------

## Phase 12 --- Testing & Release

Perform:

``` text
Functional testing
Firebase Rules testing
UI testing
Crash testing
Data consistency testing
Release build
```

Deliver:

``` text
APK
Source code
Firebase configuration instructions
README
```

------------------------------------------------------------------------

# 51. Required Agent Output After Each Phase

After completing a phase, the AI agent must report:

``` text
PHASE:
Status: DONE / BLOCKED

Implemented:
- ...
- ...

Files created:
- ...

Files modified:
- ...

Firebase changes:
- ...

Dependencies added:
- ...

Tests performed:
- ...

Build status:
- SUCCESS / FAILED

Known issues:
- ...

Next phase:
- ...
```

Do not continue to the next major phase if the current phase does not
build.

------------------------------------------------------------------------

# 52. Final Definition of Done

The application is considered complete only when:

``` text
[ ] Android project builds
[ ] Java implementation
[ ] XML UI
[ ] MVVM architecture
[ ] Firebase Authentication
[ ] Firestore
[ ] Firebase Storage
[ ] FCM
[ ] User role
[ ] Admin role
[ ] Register
[ ] Login
[ ] Logout
[ ] Home
[ ] Category
[ ] Search
[ ] Filter
[ ] Sort
[ ] Book detail
[ ] Cart
[ ] Checkout
[ ] COD
[ ] Orders
[ ] Order status
[ ] Order cancellation
[ ] Review
[ ] Profile
[ ] Admin dashboard
[ ] Book CRUD
[ ] Category CRUD
[ ] Order management
[ ] User management
[ ] Firebase Security Rules
[ ] Loading state
[ ] Empty state
[ ] Error state
[ ] Validation
[ ] Image upload
[ ] Image loading
[ ] Notifications
[ ] Testing
[ ] Release APK
```

------------------------------------------------------------------------

# 53. First Prompt to AI Coding Agent

Use the following prompt to start implementation:

> You are the lead Android Java developer for the Book Store project.
>
> Read the complete technical specification in this document before
> changing any code.
>
> The project is Android Native using Java and XML. Use MVVM
> architecture. The backend is Firebase: Authentication, Firestore,
> Storage, and FCM.
>
> The application has USER and ADMIN roles.
>
> Do not use Kotlin. Do not use Jetpack Compose. Do not introduce
> ASP.NET, Spring Boot, Node.js, or another backend.
>
> Do not implement the entire application in one step.
>
> Start with Phase 0 only.
>
> For Phase 0:
>
> 1.  Inspect the existing Android project.
> 2.  Do not overwrite existing working code without checking it first.
> 3.  Configure Java/XML project structure.
> 4.  Configure required Gradle dependencies using current stable
>     compatible versions.
> 5.  Configure Firebase integration structure.
> 6.  Create the package structure.
> 7.  Create the base resources and theme.
> 8.  Make sure the application builds successfully.
> 9.  Do not implement business features yet.
>
> After implementation, report:
>
> -   Files created.
> -   Files modified.
> -   Dependencies added.
> -   Firebase setup required from the developer.
> -   Build result.
> -   Errors, if any.
> -   What remains for Phase 0.
>
> Stop after Phase 0. Do not start Phase 1 until explicitly instructed.

------------------------------------------------------------------------

# 54. Important Firebase Setup Note

The developer must manually configure the Firebase project and place the
generated Firebase Android configuration file into the Android project
as required by the Firebase setup.

Do not commit sensitive project configuration or credentials that should
not be public.

For a coursework repository, review Firebase configuration and Security
Rules before making the repository public.

------------------------------------------------------------------------

# 55. V1 Scope Decisions

The following decisions are intentional:

``` text
Backend:
Firebase

Authentication:
Email + Password

Roles:
USER + ADMIN

Payment:
COD

Online payment:
Not in V1

Architecture:
MVVM

Language:
Java

UI:
XML

Database:
Cloud Firestore

Images:
Firebase Storage

Notification:
Firebase Cloud Messaging

Local database:
Not required for V1

External search engine:
Not required

External payment gateway:
Not required
```

This keeps the project large enough for a complete coursework
application while avoiding unnecessary infrastructure complexity.
