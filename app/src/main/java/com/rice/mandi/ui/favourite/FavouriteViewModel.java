package com.rice.mandi.ui.favourite;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FavouriteViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public FavouriteViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai i am in Favourite Fragment");
    }

    public LiveData<String> getText() {return mText;}
}