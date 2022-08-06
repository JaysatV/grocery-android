package com.rice.mandi.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CartViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public CartViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai this is Cart Fragment");
    }

    public LiveData<String> getText() {return mText;}
}