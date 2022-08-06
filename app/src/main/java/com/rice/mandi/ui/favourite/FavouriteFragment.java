package com.rice.mandi.ui.favourite;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rice.mandi.R;
import com.rice.mandi.adapter.recycler.BrandsRecyclerAdapter;
import com.rice.mandi.adapter.recycler.FavouriteRecyclerAdapter;
import com.rice.mandi.adapter.recycler.models.BrandModel;
import com.rice.mandi.adapter.recycler.models.ProductModel;
import com.rice.mandi.databinding.FavouriteFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    private FavouriteViewModel mViewModel;
    private FavouriteFragmentBinding favouriteFragmentBinding;

    RecyclerView recyclerView;
    FavouriteRecyclerAdapter recyclerAdapter;
    List<ProductModel> dataList;

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(FavouriteViewModel.class);
        favouriteFragmentBinding = FavouriteFragmentBinding.inflate(inflater, container, false);

        recyclerView= favouriteFragmentBinding.favRecyclerview;
        dataList=new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ProductModel productModel = new ProductModel();
        productModel.setProduct_name("lskfak");

        dataList.add(productModel);
        dataList.add(productModel);
        dataList.add(productModel);
        dataList.add(productModel);
        dataList.add(productModel);
        dataList.add(productModel);
        dataList.add(productModel);
        recyclerAdapter = new FavouriteRecyclerAdapter(getContext(), dataList);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

        return favouriteFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavouriteViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        favouriteFragmentBinding = null;
    }
}