
package com.rice.mandi.adapter.recycler;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rice.mandi.FullScreenImageActivity;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.ProductsDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.adapter.recycler.models.ProductModel;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;
import com.rice.mandi.interfaces.OnChangeCartFragment;
import com.rice.mandi.room.AppDatabase;
import com.rice.mandi.room.CartTableClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductsListItemsRecyclerAdapter extends RecyclerView.Adapter<ProductsListItemsRecyclerAdapter.MyViewHolder>  {

    View view;
    Context context;
    List<ProductsDataClass> productModelList;
    boolean BRAND_VISIBILITY = false;
    AppDatabase db;


    public void setBRAND_VISIBILITY(boolean BRAND_VISIBILITY) {
        this.BRAND_VISIBILITY = BRAND_VISIBILITY;
    }

    RecyclerView recyclerView;
    ProductsListItemsRecyclerAdapter recyclerAdapter;
    List<ProductModel> dataList;
    OnChangeCartFragment onChangeCartFragment;

    public ProductsListItemsRecyclerAdapter(Context context, List<ProductsDataClass> productModelList) {
        this.context = context;
        this.productModelList = productModelList;
        this.db = AppDatabase.getDbInstance(context);
        onChangeCartFragment = (OnChangeCartFragment) context;
    }



    @NonNull
    @NotNull
    @Override
    public ProductsListItemsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductsListItemsRecyclerAdapter.MyViewHolder holder, int position) {
        ProductsDataClass data= productModelList.get(position);
        holder.ProductName.setText(data.getProduct_name());
        holder.ProductBrand.setText(data.getBrand_name());
        holder.ProductBrand.setVisibility((BRAND_VISIBILITY ? View.VISIBLE : View.INVISIBLE));
        holder.ProductCat.setText(data.getCat_name());
        String weight = data.getWeight() + " " + data.getUnit_name();
        holder.ProductWeight.setText(weight);
        String rate = "Rs. " + data.getRate();
        holder.ProductRate.setText(rate);
        Glide.with(context).
                load(RetrofitApiClient.IMAGE_BASE_URL + "products/p_" + productModelList.get(position).getId() + ".jpg")
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(holder.ProductImage);
        holder.ProductImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra("img_url", RetrofitApiClient.IMAGE_BASE_URL + "products/p_" + productModelList.get(position).getId() + ".jpg");
            context.startActivity(intent);
        });
        int pid = Integer.parseInt(data.getId());
        if (db.cartDAO().getCount(pid) > 0) {
            int quantity = db.cartDAO().getQuantity(pid);
            holder.ActionAdd.setText("Added (" + quantity + ")");
            holder.isCartAdded = true;
            productModelList.get(position).setQuantity(quantity);
        }
        holder.ActionAdd.setOnClickListener(v -> {

            showBottomSheetDialog(holder, data, position);
        });



    }


    private void showBottomSheetDialog(MyViewHolder holder, ProductsDataClass data, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_quantity);
        EditText Quantity = bottomSheetDialog.findViewById(R.id.dialog_quantity);
        TextView BottomQuantity = bottomSheetDialog.findViewById(R.id.bottom_quantity);
        TextView BottomRate = bottomSheetDialog.findViewById(R.id.bottom_rate);
        TextView BottomTotal = bottomSheetDialog.findViewById(R.id.bottom_total);
        BottomRate.setText(data.getRate());
        BottomQuantity.setText(data.getQuantity() + "");
        if(!BottomQuantity.getText().toString().equals("")) {
            Float total = Integer.parseInt(BottomQuantity.getText().toString()) * Float.parseFloat(BottomRate.getText().toString());
            BottomTotal.setText("" + total);
        } else BottomTotal.setText("");

        Quantity.setText((data.getQuantity() == 0) ? "" : "" + data.getQuantity());
        Quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                BottomQuantity.setText(s.toString());
                if(!BottomQuantity.getText().toString().equals("")) {
                    Float total = Integer.parseInt(BottomQuantity.getText().toString()) * Float.parseFloat(BottomRate.getText().toString());
                    BottomTotal.setText("" + total);
                } else BottomTotal.setText("");
            }
        });
        Button QuantityAdd = bottomSheetDialog.findViewById(R.id.dialog_add);
        Button BtCancel = bottomSheetDialog.findViewById(R.id.dialog_close);
        Button BtRemove = bottomSheetDialog.findViewById(R.id.dialog_remove_item);
        CartTableClass cartTableClass = new CartTableClass();
        if (holder.isCartAdded) BtRemove.setVisibility(View.VISIBLE);
        else BtRemove.setVisibility(View.INVISIBLE);
        QuantityAdd.setOnClickListener(v->{

            if (!Quantity.getText().toString().equals("")) {
                if(Integer.parseInt(Quantity.getText().toString()) > 0) {
                    cartTableClass.product_id = data.getId();
                    cartTableClass.product_name = data.getProduct_name();
                    cartTableClass.purchase_from = data.getPurchase_from();
                    cartTableClass.product_quantity = Integer.parseInt(Quantity.getText().toString());
                    String weight = data.getWeight() + " " + data.getUnit_name();
                    cartTableClass.product_weight = weight;
                    cartTableClass.product_rate = data.getRate();
                    cartTableClass.order_id = "";
                    if (holder.isCartAdded) {
                        db.cartDAO().updateQuantity(Integer.parseInt(data.getId()), Integer.parseInt(Quantity.getText().toString()));
                        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) context;
                        bottomCartVisibilityInterface.onUpdateCart();
                    }
                    else {
                        db.cartDAO().addItemToCart(cartTableClass);
                        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) context;
                        bottomCartVisibilityInterface.onUpdateCart();
                    }
                    bottomSheetDialog.dismiss();
                    int quantity = Integer.parseInt(Quantity.getText().toString());
                    productModelList.get(position).setQuantity(quantity);
                    holder.ActionAdd.setText("Added (" + quantity + ")");
                    holder.isCartAdded = true;
                    //onChangeCartFragment.onChangeCart();
                } else Toast.makeText(context, "Invalid Quantity", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Invalid Quantity", Toast.LENGTH_SHORT).show();

        });
        BtCancel.setOnClickListener(v->bottomSheetDialog.dismiss());
        BtRemove.setOnClickListener(v->{
            holder.ActionAdd.setText("Add");
            holder.isCartAdded = false;
            bottomSheetDialog.dismiss();
            productModelList.get(position).setQuantity(0);
            db.cartDAO().deleteItemFromCart(Integer.parseInt(data.getId()));
            BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) context;
            bottomCartVisibilityInterface.onUpdateCart();
        });
        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ProductName, ProductBrand, ProductCat, ProductWeight, ProductRate;
        Button ActionAdd;
        boolean isCartAdded;
        ImageView ProductImage;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ProductName = itemView.findViewById(R.id.product_name);
            ProductBrand = itemView.findViewById(R.id.product_brand);
            ProductCat = itemView.findViewById(R.id.product_category);
            ProductWeight = itemView.findViewById(R.id.product_quantity);
            ProductRate = itemView.findViewById(R.id.product_rate);
            ActionAdd = itemView.findViewById(R.id.product_add);
            ProductImage = itemView.findViewById(R.id.product_image);
            isCartAdded = false;
        }
    }
}
