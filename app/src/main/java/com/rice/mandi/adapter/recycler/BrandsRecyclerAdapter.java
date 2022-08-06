package com.rice.mandi.adapter.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.adapter.recycler.models.BrandModel;
import com.rice.mandi.interfaces.OnChangeFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;



public class BrandsRecyclerAdapter extends RecyclerView.Adapter<BrandsRecyclerAdapter.MyViewHolder> {

    private Context context;
    private List<BrandDataClass>  brandModelList;
    private View view;
    private OnChangeFragment onChangeFragment;

    public BrandsRecyclerAdapter(Context context, List<BrandDataClass> brandModelList) {
        this.context = context;
        this.brandModelList = brandModelList;
        this.onChangeFragment = (OnChangeFragment) context;
    }

    @NonNull
    @NotNull
    @Override
    public BrandsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_brand, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BrandsRecyclerAdapter.MyViewHolder holder, int position) {
        holder.BrandName.setText(brandModelList.get(position).getBrand_name());
        holder.ItemContainer.setOnClickListener(v -> onChangeFragment.onClickBrand(Integer.parseInt(brandModelList.get(position).getId())));
        Glide.with(context)
                .load(RetrofitApiClient.IMAGE_BASE_URL + "brands/b_" + brandModelList.get(position).getId() + ".jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.BrandImage);
    }

    @Override
    public int getItemCount() {
        return brandModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView BrandName;
        CardView ItemContainer;
        ImageView BrandImage;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            BrandName = itemView.findViewById(R.id.brand_name);
            ItemContainer = itemView.findViewById(R.id.item_container);
            BrandImage = itemView.findViewById(R.id.brand_image);
        }
    }
}
