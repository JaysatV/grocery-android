package com.rice.mandi.ui.brands;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.faltenreich.skeletonlayout.SkeletonLayout;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.adapter.recycler.BrandsRecyclerAdapter;
import com.rice.mandi.adapter.recycler.TopBrandsRecyclerAdapter;
import com.rice.mandi.adapter.recycler.models.BrandModel;
import com.rice.mandi.databinding.BrandsFragmentBinding;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private BrandsViewModel mViewModel;


    RecyclerView recyclerView;
    BrandsRecyclerAdapter recyclerAdapter;
    List<BrandModel> dataList;

    Call<List<BrandDataClass>> call_brands;
    private List<BrandDataClass> brandDataList;
    SwipeRefreshLayout swipeRefreshLayout;
    RetrofitApiInterface retrofitApiInterface;
    SkeletonLayout skeletonLayout;
    public static BrandsFragment newInstance() {
        return new BrandsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(BrandsViewModel.class);

        View view = inflater.inflate(R.layout.brands_fragment, container, false);
        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) getContext();
        bottomCartVisibilityInterface.changeVisible(View.INVISIBLE);
        skeletonLayout = view.findViewById(R.id.brand_skeletonLayout);

        skeletonLayout.showSkeleton();
        retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);
        recyclerView= view.findViewById(R.id.brand_recyclerview);
        dataList=new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = view.findViewById(R.id.brand_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        getBrands();
        return view;
    }

    private void getBrands() {
        call_brands = retrofitApiInterface.getAllBrands();
        call_brands.enqueue(new Callback<List<BrandDataClass>>() {
            @Override
            public void onResponse(Call<List<BrandDataClass>> call, Response<List<BrandDataClass>> response) {
                brandDataList = response.body();
                recyclerAdapter = new BrandsRecyclerAdapter(getContext(), brandDataList);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                skeletonLayout.showOriginal();
            }

            @Override
            public void onFailure(Call<List<BrandDataClass>> call, Throwable t) {
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
        mViewModel = new ViewModelProvider(this).get(BrandsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getBrands();
    }
}