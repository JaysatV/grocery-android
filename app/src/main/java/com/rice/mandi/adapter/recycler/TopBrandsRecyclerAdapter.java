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
import com.rice.mandi.interfaces.OnChangeFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TopBrandsRecyclerAdapter extends RecyclerView.Adapter<TopBrandsRecyclerAdapter.MyViewHolder> {

    Context context;
    View view;
    List<BrandDataClass> brandDataList;
    private OnChangeFragment onChangeFragment;
    public TopBrandsRecyclerAdapter(Context context, List<BrandDataClass> brandDataList) {
        this.context = context;
        this.brandDataList = brandDataList;
        this.onChangeFragment = (OnChangeFragment) context;
    }

    @NonNull
    @NotNull
    @Override
    public TopBrandsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_top_brands, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TopBrandsRecyclerAdapter.MyViewHolder holder, int position) {
        holder.BrandName.setText(brandDataList.get(position).getBrand_name());
        holder.ItemContainer.setOnClickListener(v -> {
            onChangeFragment.onClickBrand(Integer.parseInt(brandDataList.get(position).getId())) ;
        });
        Glide.with(context)
                .load(RetrofitApiClient.IMAGE_BASE_URL + "brands/b_" + brandDataList.get(position).getId() + ".jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.TbImage);
    }

    @Override
    public int getItemCount() {
        return brandDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView TbImage;
        private TextView BrandName;
        CardView ItemContainer;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            TbImage = itemView.findViewById(R.id.tb_image);
            BrandName = itemView.findViewById(R.id.tb_brand_name);
            ItemContainer = itemView.findViewById(R.id.tp_item_container);
        }
    }
}
