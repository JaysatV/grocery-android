package com.rice.mandi.ui.about;

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
import com.rice.mandi.databinding.AboutUsFragmentBinding;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;

public class AboutUsFragment extends Fragment {

    private AboutUsViewModel mViewModel;
    private AboutUsFragmentBinding aboutUsFragmentBinding;

    public static AboutUsFragment newInstance() {
        return new AboutUsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AboutUsViewModel.class);
        aboutUsFragmentBinding = AboutUsFragmentBinding.inflate(inflater, container, false);
        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) getContext();
        bottomCartVisibilityInterface.changeVisible(View.INVISIBLE);

        return aboutUsFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AboutUsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        aboutUsFragmentBinding = null;
    }
}