package com.rice.mandi.adapter.recycler;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.rice.mandi.R;
import com.rice.mandi.interfaces.OnChangeCartItems;
import com.rice.mandi.room.AppDatabase;
import com.rice.mandi.room.CartTableClass;
import com.rice.mandi.ui.cart.CartFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.MyViewHolder> {

    View view;
    Context context;
    List<CartTableClass> cartDataList;
    AppDatabase db;
    OnChangeCartItems onChangeCartItems;
    public CartRecyclerAdapter(Context context, List<CartTableClass> cartDataList, OnChangeCartItems onChangeCartItems) {
        this.context = context;
        this.cartDataList = cartDataList;
        db = AppDatabase.getDbInstance(context);
        this.onChangeCartItems = onChangeCartItems;
    }

    @NonNull
    @NotNull
    @Override
    public CartRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CartRecyclerAdapter.MyViewHolder holder, int position) {
        CartTableClass data = cartDataList.get(position);
        holder.CartItemName.setText(data.product_name);
        holder.CartItemWeight.setText(data.product_weight);
        holder.CartItemQuantity.setText("" + data.product_quantity);
        holder.CartItemRate.setText("Rs. " + data.product_rate);
        float total = data.product_quantity * Float.parseFloat(data.product_rate);
        holder.CartItemTotal.setText("Rs. " + total);
        holder.CartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                db.cartDAO().deleteItemFromCart(Integer.parseInt(data.product_id));

                                cartDataList.remove(data);
                                notifyDataSetChanged();
                                onChangeCartItems.onDeleteCartItem();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure want to remove?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView CartItemName, CartItemWeight, CartItemQuantity, CartItemRate, CartItemTotal;
        ImageView CartDelete;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            CartItemName = itemView.findViewById(R.id.cart_item_name);
            CartItemWeight = itemView.findViewById(R.id.cart_item_weight);
            CartItemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            CartItemRate = itemView.findViewById(R.id.cart_item_rate);
            CartItemTotal = itemView.findViewById(R.id.cart_item_total);
            CartDelete = itemView.findViewById(R.id.cart_delete);
        }
    }

    public float calcTotalAmount() {
        float total = 0;
        for (CartTableClass cartItem: cartDataList){
            total = total + (cartItem.product_quantity * Float.parseFloat(cartItem.product_rate));
        }
        return total;
    }



}
