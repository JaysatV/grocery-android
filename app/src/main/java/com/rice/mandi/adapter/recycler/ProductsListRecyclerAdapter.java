package com.rice.mandi.adapter.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.adapter.recycler.models.ProductModel;
import com.rice.mandi.adapter.recycler.models.ProductsListModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductsListRecyclerAdapter extends RecyclerView.Adapter<ProductsListRecyclerAdapter.MyViewHolder> {

    View view;
    Context context;
    List<BrandDataClass> productsListModels;

    public ProductsListRecyclerAdapter(Context context, List<BrandDataClass> productsListModels) {
        this.context = context;
        this.productsListModels = productsListModels;
    }

    @NonNull
    @NotNull
    @Override
    public ProductsListRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_products_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductsListRecyclerAdapter.MyViewHolder holder, int position) {
        holder.ListTitle.setText(productsListModels.get(position).getBrand_name());

        ProductsListItemsRecyclerAdapter recyclerAdapter;
        List<ProductModel> dataList =new ArrayList<>();

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setNestedScrollingEnabled(false);
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());



        recyclerAdapter = new ProductsListItemsRecyclerAdapter(context, productsListModels.get(position).getBrand_products());

        holder.recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productsListModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView ListTitle;
        private RecyclerView recyclerView;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ListTitle = itemView.findViewById(R.id.list_title);
            recyclerView = itemView.findViewById(R.id.product_list_items_recyclerview);
        }
    }
}
