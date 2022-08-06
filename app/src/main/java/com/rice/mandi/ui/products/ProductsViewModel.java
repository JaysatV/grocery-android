package com.rice.mandi.ui.products;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProductsViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public ProductsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai this is product fragment");
    }

    public LiveData<String> getText() { return mText;}
}