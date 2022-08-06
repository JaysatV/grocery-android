package com.rice.mandi.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public OrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai this is Orders Fragment");
    }

    public LiveData<String> getText() {return mText;}
}