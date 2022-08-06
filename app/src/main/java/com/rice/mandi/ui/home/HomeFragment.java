package com.rice.mandi.ui.home;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.faltenreich.skeletonlayout.SkeletonLayout;
import com.google.gson.Gson;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BannerDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.ProductsDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.adapter.recycler.HomeProductsRecyclerAdapter;
import com.rice.mandi.adapter.recycler.ProductsListItemsRecyclerAdapter;
import com.rice.mandi.adapter.recycler.ProductsListRecyclerAdapter;
import com.rice.mandi.adapter.recycler.TopBrandsRecyclerAdapter;
import com.rice.mandi.adapter.slider.SliderAdapter;
import com.rice.mandi.adapter.slider.SliderItem;

import com.rice.mandi.interfaces.BottomCartVisibilityInterface;
import com.rice.mandi.interfaces.OnChangeCartFragment;
import com.rice.mandi.room.AppDatabase;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private HomeViewModel homeViewModel;

    RecyclerView recyclerView;
    TopBrandsRecyclerAdapter recyclerAdapter;
    List<String> dataList;

    RecyclerView home_product_recyclerView;
    ProductsListItemsRecyclerAdapter productsListRecyclerAdapter;

    int product_page_count = 0;

    private ProgressDialog progressDialog;
    Call<List<ProductsDataClass>> call;
    private List<ProductsDataClass> productsDataList;

    Call<List<BrandDataClass>> call_brands;
    private List<BrandDataClass> brandDataList;
    SwipeRefreshLayout swipeRefreshLayout;
    RetrofitApiInterface retrofitApiInterface;
    SkeletonLayout skeletonLayout;
    SliderView sliderView;

    Button ViewMore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);




        skeletonLayout = view.findViewById(R.id.skeletonLayout);
        swipeRefreshLayout = view.findViewById(R.id.home_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        skeletonLayout.showSkeleton();
        sliderView = view.findViewById(R.id.imageSlider);






        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        recyclerView= view.findViewById(R.id.tb_recyclerview);
        dataList=new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());




        home_product_recyclerView= view.findViewById(R.id.home_products_recyclerview);
        RecyclerView.LayoutManager layoutManager1=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        home_product_recyclerView.setLayoutManager(layoutManager1);
        home_product_recyclerView.setHasFixedSize(true);
        home_product_recyclerView.setNestedScrollingEnabled(false);
        home_product_recyclerView.setItemAnimator(new DefaultItemAnimator());

        getBrands();
        getProducts();

        ViewMore  = view.findViewById(R.id.home_view_more);
        ViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_page_count++;
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Fetching products Please wait..");
                progressDialog.setCancelable(false);
                progressDialog.show();
                call = retrofitApiInterface.getAllProducts(product_page_count);
                call.enqueue(new Callback<List<ProductsDataClass>>() {
                    @Override
                    public void onResponse(Call<List<ProductsDataClass>> call, Response<List<ProductsDataClass>> response) {

                        productsDataList.addAll(response.body());
                        productsListRecyclerAdapter.setBRAND_VISIBILITY(true);
                        productsListRecyclerAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<ProductsDataClass>> call, Throwable t) {
                        if (t instanceof IOException) {
                            Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                    }
                });
            }
        });
        return view;
    }

    private void getBrands() {
        call_brands = retrofitApiInterface.getAllBrands();
        call_brands.enqueue(new Callback<List<BrandDataClass>>() {
            @Override
            public void onResponse(Call<List<BrandDataClass>> call, Response<List<BrandDataClass>> response) {
                brandDataList = response.body();
                recyclerAdapter = new TopBrandsRecyclerAdapter(getContext(), brandDataList);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<BrandDataClass>> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Call<List<BannerDataClass>> call_banner;
        call_banner = retrofitApiInterface.getAllBanners();
        call_banner.enqueue(new Callback<List<BannerDataClass>>() {
            @Override
            public void onResponse(Call<List<BannerDataClass>> call, Response<List<BannerDataClass>> response) {
                List<BannerDataClass> bannerDataClasses = response.body();
                SliderAdapter sliderAdapter = new SliderAdapter(getContext());
                for (BannerDataClass banner: bannerDataClasses) {

                    SliderItem sliderItem = new SliderItem();
                    sliderItem.setImg_url(RetrofitApiClient.IMAGE_BASE_URL + "banner/b_" + banner.getId() +".jpg");
                    sliderAdapter.addItem(sliderItem);
                    sliderView.setSliderAdapter(sliderAdapter);
                }


            }

            @Override
            public void onFailure(Call<List<BannerDataClass>> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getProducts() {
        call = retrofitApiInterface.getAllProducts(product_page_count);
        call.enqueue(new Callback<List<ProductsDataClass>>() {
            @Override
            public void onResponse(Call<List<ProductsDataClass>> call, Response<List<ProductsDataClass>> response) {
               productsDataList = response.body();

                productsListRecyclerAdapter = new ProductsListItemsRecyclerAdapter(getContext(), productsDataList);
                productsListRecyclerAdapter.setBRAND_VISIBILITY(true);
                home_product_recyclerView.setAdapter(productsListRecyclerAdapter);
                productsListRecyclerAdapter.notifyDataSetChanged();
                skeletonLayout.showOriginal();
            }

            @Override
            public void onFailure(Call<List<ProductsDataClass>> call, Throwable t) {
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
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getBrands();
        getProducts();
    }
}