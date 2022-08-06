package com.rice.mandi.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hai this is Notification Fragment");
    }

    public LiveData<String> getText() {return mText;}
}