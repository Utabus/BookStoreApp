package com.example.book_selling_app.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.models.Address;

import java.util.List;

public class AddressRepository {

    private final DatabaseHelper dbHelper;
    private final AppExecutors appExecutors;

    public AddressRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
    }

    public LiveData<Resource<List<Address>>> getAddressesForUser(int userId) {
        MutableLiveData<Resource<List<Address>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<Address> addresses = dbHelper.getAddressesForUser(userId);
                result.postValue(Resource.success(addresses));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tải sổ địa chỉ: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Address>> getDefaultAddress(int userId) {
        MutableLiveData<Resource<Address>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                Address address = dbHelper.getDefaultAddress(userId);
                result.postValue(Resource.success(address));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tìm địa chỉ mặc định: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Long>> insertAddress(int userId, String name, String phone, String street, String label, boolean isDefault) {
        MutableLiveData<Resource<Long>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                long id = dbHelper.insertAddress(userId, name, phone, street, label, isDefault);
                if (id > 0) {
                    result.postValue(Resource.success(id));
                } else {
                    result.postValue(Resource.error("Không thể lưu địa chỉ", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updateAddress(int addressId, int userId, String name, String phone, String street, String label, boolean isDefault) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.updateAddress(addressId, userId, name, phone, street, label, isDefault);
                if (rows > 0) {
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Không thể cập nhật địa chỉ", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> deleteAddress(int addressId, int userId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.deleteAddress(addressId, userId);
                if (rows > 0) {
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Không thể xóa địa chỉ", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> setDefaultAddress(int addressId, int userId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                boolean success = dbHelper.setDefaultAddress(addressId, userId);
                result.postValue(Resource.success(success));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }
}
