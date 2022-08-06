package com.rice.mandi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai this is Profile Fragment");
    }

    public LiveData<String> getText() {return mText;}
}