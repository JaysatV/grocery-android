package com.rice.mandi.ui.brands;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BrandsViewModel extends ViewModel {

    private MutableLiveData<String> mtext;
    public BrandsViewModel() {
        mtext = new MutableLiveData<>();
        mtext.setValue("Hai i am from custom brands Fragment");
    }

    public LiveData<String> getText() {return mtext;}

}