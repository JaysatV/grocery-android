package com.rice.mandi.adapter.recycler;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.rice.mandi.R;
import com.rice.mandi.room.CartTableClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderItemsRecyclerAdapter extends RecyclerView.Adapter<OrderItemsRecyclerAdapter.MyViewHolder> {

    View view;
    Context context;
    List<CartTableClass> orderItemsList;

    public OrderItemsRecyclerAdapter(Context context, List<CartTableClass> orderItemsList) {
        this.context = context;
        this.orderItemsList = orderItemsList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        CartTableClass data = orderItemsList.get(position);
        holder.CartItemName.setText(data.getProduct_name());
        holder.CartItemWeight.setText(data.getProduct_weight());
        holder.CartItemQuantity.setText(data.getProduct_quantity() + "");
        holder.CartItemRate.setText(data.getProduct_rate());
        float total_amount = data.getProduct_quantity() * Float.parseFloat(data.getProduct_rate());
        holder.CartItemTotal.setText("Rs." + total_amount);
    }

    @Override
    public int getItemCount() {
        return orderItemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView CartItemName, CartItemWeight, CartItemQuantity, CartItemRate, CartItemTotal;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            CartItemName = itemView.findViewById(R.id.cart_item_name);
            CartItemWeight = itemView.findViewById(R.id.cart_item_weight);
            CartItemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            CartItemRate = itemView.findViewById(R.id.cart_item_rate);
            CartItemTotal = itemView.findViewById(R.id.cart_item_total);

        }
    }
}
