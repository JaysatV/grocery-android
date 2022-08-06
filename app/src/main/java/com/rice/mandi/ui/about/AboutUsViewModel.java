package com.rice.mandi.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutUsViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public AboutUsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai this is About Us Fragment");
    }

    public LiveData<String> getText() {return mText;}
}