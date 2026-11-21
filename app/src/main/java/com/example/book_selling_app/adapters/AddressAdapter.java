package com.example.book_selling_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.databinding.ItemAddressBinding;
import com.example.book_selling_app.models.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    public interface OnAddressActionListener {
        void onSelect(Address address);
        void onEdit(Address address);
        void onDelete(Address address);
        void onSetDefault(Address address);
    }

    private List<Address> addresses = new ArrayList<>();
    private final OnAddressActionListener listener;
    private final boolean isSelectMode;

    public AddressAdapter(boolean isSelectMode, OnAddressActionListener listener) {
        this.isSelectMode = isSelectMode;
        this.listener = listener;
    }

    public void setAddresses(List<Address> list) {
        this.addresses = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddressBinding binding = ItemAddressBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        holder.bind(addresses.get(position));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        private final ItemAddressBinding binding;

        public AddressViewHolder(@NonNull ItemAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Address address) {
            binding.tvRecipientName.setText(address.getRecipientName());
            binding.tvRecipientPhone.setText(address.getPhone());
            binding.tvStreetAddress.setText(address.getStreetAddress());
            binding.tvAddressLabel.setText(address.getLabel() != null ? address.getLabel() : "Nhà riêng");

            if (address.isDefault()) {
                binding.tvDefaultBadge.setVisibility(View.VISIBLE);
                binding.btnSetDefault.setVisibility(View.GONE);
            } else {
                binding.tvDefaultBadge.setVisibility(View.GONE);
                binding.btnSetDefault.setVisibility(View.VISIBLE);
            }

            // Click entire card
            binding.cardAddress.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSelect(address);
                }
            });

            binding.btnSetDefault.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSetDefault(address);
                }
            });

            binding.btnEditAddress.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(address);
                }
            });

            binding.btnDeleteAddress.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(address);
                }
            });
        }
    }
}
