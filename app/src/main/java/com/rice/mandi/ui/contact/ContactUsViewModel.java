package com.rice.mandi.ui.contact;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactUsViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public ContactUsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai this is Contact Us Fragment");
    }

    public LiveData<String> getText() {return mText;}
}