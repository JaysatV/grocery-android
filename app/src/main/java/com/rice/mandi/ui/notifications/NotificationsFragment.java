package com.rice.mandi.ui.notifications;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rice.mandi.R;
import com.rice.mandi.databinding.NotificationsFragmentBinding;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel mViewModel;
    private NotificationsFragmentBinding notificationsFragmentBinding;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        notificationsFragmentBinding = NotificationsFragmentBinding.inflate(inflater, container, false);
        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) getContext();
        bottomCartVisibilityInterface.changeVisible(View.INVISIBLE);

        return notificationsFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notificationsFragmentBinding = null;
    }
}