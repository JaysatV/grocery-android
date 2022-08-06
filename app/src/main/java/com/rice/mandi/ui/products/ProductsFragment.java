package com.rice.mandi.ui.products;


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
import android.widget.Toast;

import com.faltenreich.skeletonlayout.SkeletonLayout;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.ProductsDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.adapter.recycler.ProductsListRecyclerAdapter;
import com.rice.mandi.adapter.recycler.models.ProductsListModel;
import com.rice.mandi.databinding.ProductsFragmentBinding;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private ProductsViewModel mViewModel;


    RecyclerView recyclerView;
    ProductsListRecyclerAdapter recyclerAdapter;
    List<ProductsListModel> dataList;
    Button ViewMore;
    private ProgressDialog progressDialog;
    Call<List<BrandDataClass>> call;
    private List<BrandDataClass> brandDataList;
    RetrofitApiInterface retrofitApiInterface;
    SkeletonLayout skeletonLayout;
    String selected_brand_id = "0";
    int product_page_count = 0;
    SwipeRefreshLayout swipeRefreshLayout;
    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);

        View view = inflater.inflate(R.layout.products_fragment, container, false);
        ViewMore  = view.findViewById(R.id.product_view_more);
        if (getArguments() != null) {
            selected_brand_id = getArguments().getString("brand_id");
            ViewMore.setVisibility(View.INVISIBLE);
        }
       // Toast.makeText(getContext(), "selected brand is " + selected_brand_id, Toast.LENGTH_SHORT).show();


        retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);
        skeletonLayout = view.findViewById(R.id.product_skeletonLayout);

        skeletonLayout.showSkeleton();
        recyclerView= view.findViewById(R.id.product_list_recyclerview);
        dataList=new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = view.findViewById(R.id.products_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);




        downloadData();


        ViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_page_count++;
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Fetching products Please wait..");
                progressDialog.setCancelable(false);
                progressDialog.show();
                call = retrofitApiInterface.getListingProducts(selected_brand_id, product_page_count);
                call.enqueue(new Callback<List<BrandDataClass>>() {
                    @Override
                    public void onResponse(Call<List<BrandDataClass>> call, Response<List<BrandDataClass>> response) {
                        progressDialog.dismiss();
                        brandDataList.addAll(response.body());

                        recyclerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<BrandDataClass>> call, Throwable t) {
                        progressDialog.dismiss();
                        if (t instanceof IOException) {
                            Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                        }
                        skeletonLayout.showOriginal();
                    }
                });
            }
        });
        return view;
    }

    private void downloadData() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching products Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        progressDialog.show();

        call = retrofitApiInterface.getListingProducts(selected_brand_id, product_page_count);
        call.enqueue(new Callback<List<BrandDataClass>>() {
            @Override
            public void onResponse(Call<List<BrandDataClass>> call, Response<List<BrandDataClass>> response) {
                progressDialog.dismiss();
                brandDataList = response.body();
                recyclerAdapter = new ProductsListRecyclerAdapter(getContext(), brandDataList);

                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                skeletonLayout.showOriginal();
            }

            @Override
            public void onFailure(Call<List<BrandDataClass>> call, Throwable t) {
                progressDialog.dismiss();
                if (t instanceof IOException) {
                    Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                }
                skeletonLayout.showOriginal();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        downloadData();
    }
}