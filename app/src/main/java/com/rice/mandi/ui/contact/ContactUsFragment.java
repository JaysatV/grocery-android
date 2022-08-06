package com.rice.mandi.ui.contact;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.CustomersDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.databinding.ContactUsFragmentBinding;
import com.rice.mandi.helper.CustomerDetails;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;
import com.rice.mandi.interfaces.OnLogin;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsFragment extends Fragment {

    private ContactUsViewModel mViewModel;
    private ContactUsFragmentBinding contactUsFragmentBinding;
    Call call;
    TextView textView;
    RetrofitApiInterface retrofitApiInterface;
    private ProgressDialog progressDialog;
    Call<CustomersDataClass> call_customer;
    private CustomersDataClass customersDataClass;
    CustomerDetails customerDetails;
    OnLogin onLogin;
    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ContactUsViewModel.class);
        contactUsFragmentBinding = ContactUsFragmentBinding.inflate(inflater, container, false);
        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) getContext();
        bottomCartVisibilityInterface.changeVisible(View.INVISIBLE);
        textView = contactUsFragmentBinding.msg;
        Button send = contactUsFragmentBinding.sendMsg;
        retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);
        onLogin = (OnLogin) getContext();
        send.setOnClickListener(view -> {
            String phoneNumberWithCountryCode = "+918344477782";
            String message = textView.getText().toString();

            startActivity(
                    new Intent(Intent.ACTION_VIEW,
                            Uri.parse(
                                    String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                            )
                    )
            );
//            if (textView.getText().toString() != null && !textView.getText().toString().equals(""))
//                sendFeedback();
//            else showDialog();
        });
        return contactUsFragmentBinding.getRoot();
    }

    private void sendFeedback() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Orders Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        progressDialog.show();
        call = retrofitApiInterface.contactUs(new CustomerDetails(getContext()).getCustomerID(), textView.getText().toString());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(getContext(), "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                textView.setText("");
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
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
                    sendFeedback();
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

        });
        alertDialog.show();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ContactUsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contactUsFragmentBinding = null;
    }
}