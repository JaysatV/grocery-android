package com.rice.mandi.adapter.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.ProductsDataClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeProductsRecyclerAdapter extends RecyclerView.Adapter<HomeProductsRecyclerAdapter.MyViewHolder> {

    View view;
    Context context;
    List<ProductsDataClass> productsDataList;

    public HomeProductsRecyclerAdapter(Context context, List<ProductsDataClass> productsDataList) {
        this.context = context;
        this.productsDataList = productsDataList;
    }

    @NonNull
    @NotNull
    @Override
    public HomeProductsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeProductsRecyclerAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return productsDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
