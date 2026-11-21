package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.Address;
import com.example.book_selling_app.repositories.AddressRepository;

import java.util.List;

public class AddressViewModel extends AndroidViewModel {

    private final AddressRepository addressRepository;

    public AddressViewModel(@NonNull Application application) {
        super(application);
        this.addressRepository = new AddressRepository(application);
    }

    public LiveData<Resource<List<Address>>> getAddressesForUser(int userId) {
        return addressRepository.getAddressesForUser(userId);
    }

    public LiveData<Resource<Address>> getDefaultAddress(int userId) {
        return addressRepository.getDefaultAddress(userId);
    }

    public LiveData<Resource<Long>> insertAddress(int userId, String name, String phone, String street, String label, boolean isDefault) {
        return addressRepository.insertAddress(userId, name, phone, street, label, isDefault);
    }

    public LiveData<Resource<Boolean>> updateAddress(int addressId, int userId, String name, String phone, String street, String label, boolean isDefault) {
        return addressRepository.updateAddress(addressId, userId, name, phone, street, label, isDefault);
    }

    public LiveData<Resource<Boolean>> deleteAddress(int addressId, int userId) {
        return addressRepository.deleteAddress(addressId, userId);
    }

    public LiveData<Resource<Boolean>> setDefaultAddress(int addressId, int userId) {
        return addressRepository.setDefaultAddress(addressId, userId);
    }
}
