package com.rice.mandi.ui.orders;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rice.mandi.MainActivity;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.CustomersDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.OrderWithItems;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.adapter.recycler.OrdersRecyclerAdapter;
import com.rice.mandi.databinding.OrdersFragmentBinding;
import com.rice.mandi.helper.CustomerDetails;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;
import com.rice.mandi.interfaces.OnChangeHomeFragment;
import com.rice.mandi.interfaces.OnLogin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private OrdersViewModel mViewModel;
    private OrdersFragmentBinding ordersFragmentBinding;

    Call<List<OrderWithItems>> call_orders;
    private List<OrderWithItems> ordersList;

    Call<CustomersDataClass> call_customer;
    private CustomersDataClass customersDataClass;

    RetrofitApiInterface retrofitApiInterface;
    SwipeRefreshLayout swipeRefreshLayout;
    CustomerDetails customerDetails;
    private ProgressDialog progressDialog;
    RecyclerView recyclerView;
    OrdersRecyclerAdapter recyclerAdapter;
    OnChangeHomeFragment onChangeHomeFragment;
    OnLogin onLogin;

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        ordersFragmentBinding = OrdersFragmentBinding.inflate(inflater, container, false);
        retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);
        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) getContext();
        bottomCartVisibilityInterface.changeVisible(View.INVISIBLE);
        customerDetails = new CustomerDetails(getContext());
        onChangeHomeFragment = (OnChangeHomeFragment) getContext();
        recyclerView= ordersFragmentBinding.ordersRecyclerview;
        swipeRefreshLayout = ordersFragmentBinding.ordersRefresh;
        swipeRefreshLayout.setOnRefreshListener(this);
        ordersList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        onLogin = (OnLogin) getContext();
        if(customerDetails.getCustomerID().equals("")) {
            showDialog();
        }
        else{
            getAllOrders();
        }

        return ordersFragmentBinding.getRoot();
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
                    getAllOrders();
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

    private void getAllOrders() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Orders Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        progressDialog.show();
        call_orders = retrofitApiInterface.getCustomerOrders(customerDetails.getCustomerID());
        call_orders.enqueue(new Callback<List<OrderWithItems>>() {
            @Override
            public void onResponse(Call<List<OrderWithItems>> call, Response<List<OrderWithItems>> response) {
                ordersList = response.body();


                recyclerAdapter = new OrdersRecyclerAdapter(getContext(), ordersList);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<OrderWithItems>> call, Throwable t) {
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ordersFragmentBinding = null;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getAllOrders();
    }
}