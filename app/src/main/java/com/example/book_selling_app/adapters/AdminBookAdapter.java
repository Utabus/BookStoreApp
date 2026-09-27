package com.example.book_selling_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemAdminBookBinding;
import com.example.book_selling_app.models.Book;

import java.text.DecimalFormat;
import java.util.List;

public class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.BookViewHolder> {

    public interface OnBookActionListener {
        void onEdit(Book book);
        void onDelete(Book book);
    }

    private Context context;
    private List<Book> bookList;
    private OnBookActionListener listener;
    private DecimalFormat formatter = new DecimalFormat("#,### đ");

    public AdminBookAdapter(Context context, List<Book> bookList, OnBookActionListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
    }

    public void updateData(List<Book> newList) {
        this.bookList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminBookBinding binding = ItemAdminBookBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.binding.tvAdminBookTitle.setText(book.getTitle());
        holder.binding.tvAdminBookAuthor.setText(book.getAuthor() + " • " + (book.getCategoryName() != null ? book.getCategoryName() : "Sách"));
        holder.binding.tvAdminBookPrice.setText(formatter.format(book.getPrice()));
        holder.binding.tvAdminBookStock.setText("Kho: " + book.getStock());

        Glide.with(context)
                .load(book.getImageUrl())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .into(holder.binding.imgAdminBookCover);

        holder.binding.btnEditBook.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(book);
        });

        holder.binding.btnDeleteBook.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(book);
        });
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ItemAdminBookBinding binding;

        public BookViewHolder(@NonNull ItemAdminBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
