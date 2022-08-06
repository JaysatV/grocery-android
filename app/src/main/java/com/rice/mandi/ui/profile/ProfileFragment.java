package com.rice.mandi.ui.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.CustomersDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.databinding.ProfileFragmentBinding;
import com.rice.mandi.helper.CustomerDetails;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;
import com.rice.mandi.interfaces.OnChangeHomeFragment;
import com.rice.mandi.interfaces.OnLogin;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private ProfileFragmentBinding profileFragmentBinding;

    EditText CustomerName, CustomerPhone, CustomerAddress;
    Button ActionSave;
    CustomerDetails customerDetails;
    RetrofitApiInterface retrofitApiInterface;
    Call<CustomersDataClass> call_customer;
    Call<Integer> update_customer;
    private CustomersDataClass customersDataClass;
    OnLogin onLogin;
    OnChangeHomeFragment onChangeHomeFragment;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileFragmentBinding = ProfileFragmentBinding.inflate(inflater, container, false);
        retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);
        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) getContext();
        bottomCartVisibilityInterface.changeVisible(View.INVISIBLE);
        customerDetails = new CustomerDetails(getContext());
        CustomerName = profileFragmentBinding.profileCustomerName;
        CustomerPhone = profileFragmentBinding.profileCustomerPhone;
        CustomerAddress = profileFragmentBinding.profileCustomerAddress;
        ActionSave = profileFragmentBinding.actionSave;

        ActionSave.setOnClickListener(view -> {


            update_customer = retrofitApiInterface.updateCustomer(customerDetails.getCustomerPhone(), CustomerName.getText().toString(), CustomerAddress.getText().toString());
            update_customer.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    customerDetails.setCustomerName(CustomerName.getText().toString());
                    customerDetails.setCustomerAddress(CustomerAddress.getText().toString());
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        });
        onLogin = (OnLogin) getContext();
        onChangeHomeFragment = (OnChangeHomeFragment) getContext();
        CustomerPhone.setText(customerDetails.getCustomerPhone());
        CustomerName.setText(customerDetails.getCustomerName());
        CustomerAddress.setText(customerDetails.getCustomerAddress());

        if(customerDetails.getCustomerID().equals("")) {
            showDialog();
            ActionSave.setText("Register");
        } else {
            ActionSave.setText("Update");
        }
        return profileFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profileFragmentBinding = null;
    }

    private void showDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_item_phone, null);
        AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
        EditText PhoneNumber = view.findViewById(R.id.dialog_customer_phone);
        Button ActionSubmit = view.findViewById(R.id.dialog_submit);
        Button ActionCancel = view.findViewById(R.id.dialog_cancel);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        ActionSubmit.setOnClickListener(v -> {
            String phone_number = PhoneNumber.getText().toString();
            call_customer = retrofitApiInterface.checkCustomer(phone_number, "");
            call_customer.enqueue(new Callback<CustomersDataClass>() {
                @Override
                public void onResponse(Call<CustomersDataClass> call, Response<CustomersDataClass> response) {
                    customersDataClass = response.body();
                    customerDetails.setCustomerPhone(customersDataClass.getCustomer_phone());
                    customerDetails.setCustomerId(customersDataClass.getId());
                    alertDialog.dismiss();
                    onLogin.onChangeLogin();
                }

                @Override
                public void onFailure(Call<CustomersDataClass> call, Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        });

        ActionCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
            onChangeHomeFragment.changeToHome();
        });
        alertDialog.show();
    }
}