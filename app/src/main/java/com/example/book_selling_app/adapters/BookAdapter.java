package com.example.book_selling_app.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemBookCardBinding;
import com.example.book_selling_app.models.Book;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    private final Context context;
    private List<Book> bookList;
    private final OnBookClickListener listener;
    private final NumberFormat currencyFormatter;

    public BookAdapter(Context context, List<Book> bookList, OnBookClickListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void updateData(List<Book> newBooks) {
        this.bookList = newBooks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookCardBinding binding = ItemBookCardBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.binding.tvBookTitle.setText(book.getTitle());
        holder.binding.tvBookAuthor.setText(book.getAuthor());
        holder.binding.tvRatingScore.setText(String.valueOf(book.getRating()));
        holder.binding.tvReviewCount.setText("(" + book.getReviewCount() + ")");

        holder.binding.tvBookPrice.setText(currencyFormatter.format(book.getPrice()) + " đ");

        if (book.getOriginalPrice() > book.getPrice()) {
            holder.binding.tvOriginalPrice.setVisibility(View.VISIBLE);
            holder.binding.tvOriginalPrice.setText(currencyFormatter.format(book.getOriginalPrice()) + " đ");
            holder.binding.tvOriginalPrice.setPaintFlags(
                    holder.binding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.binding.tvDiscountBadge.setVisibility(View.VISIBLE);
            holder.binding.tvDiscountBadge.setText("-" + book.getDiscount() + "%");
        } else {
            holder.binding.tvOriginalPrice.setVisibility(View.GONE);
            holder.binding.tvDiscountBadge.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .centerCrop()
                .into(holder.binding.imgBookCover);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final ItemBookCardBinding binding;

        BookViewHolder(ItemBookCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
