package com.rice.mandi.Retrofit;




import com.rice.mandi.Retrofit.ModuleClasses.BannerDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.CategoriesDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.CustomersDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.OrderWithItems;
import com.rice.mandi.Retrofit.ModuleClasses.OrdersDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.ProductsDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.UnitsDataClass;
import com.rice.mandi.room.CartTableClass;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitApiInterface {


    @GET("brand")
    Call<List<BrandDataClass>>  getAllBrands();

    @GET("categories/brand/{brand_id}")
    Call<List<CategoriesDataClass>>  getBrandCategories(@Path("brand_id") String brand_id);

    @FormUrlEncoded
    @POST("categories")
    Call<CategoriesDataClass>  createCategory(@Field("cat_name") String cat_name, @Field("brand_id") String brand_id);

    @FormUrlEncoded
    @POST("brand")
    Call<BrandDataClass>  createBrand(@Field("brand_name") String brand_name);

    @GET("units")
    Call<List<UnitsDataClass>>  getAllUnits();

    @FormUrlEncoded
    @POST("units")
    Call<UnitsDataClass>  createUnit(@Field("unit_name") String unit_name);

    @GET("products/cust/{previous}")
    Call<List<ProductsDataClass>>  getAllProducts(@Path("previous") int previous);

    @GET("products/listing/{brand_id}/{previous}")
    Call<List<BrandDataClass>>  getListingProducts(@Path("brand_id") String brand_id, @Path("previous") int previous);

    /*@Headers({"content-type: application/json"})
    @POST("orders")
    Call<OrdersDataClass>  createOrder(@Body OrderWithItems ordersDataClass);*/

    @Headers({"content-type: application/json"})
    @POST("orders")
    Call<List<CartTableClass>>  createOrder(@Body OrderWithItems ordersDataClass);

    @FormUrlEncoded
    @POST("customers/check")
    Call<CustomersDataClass>  checkCustomer(@Field("customer_phone") String customer_phone, @Field("fcm_token") String fcm_token);


    @FormUrlEncoded
    @POST("customers/update")
    Call<Integer>  updateCustomer(@Field("customer_phone") String customer_phone, @Field("customer_name") String customer_name, @Field("customer_address") String customer_address);

    @GET("orders/get/{customer_id}")
    Call<List<OrderWithItems>>  getCustomerOrders(@Path("customer_id") String customer_id);

    @FormUrlEncoded
    @POST("fcm/cust")
    Call<Integer>  registerFCM(@Field("token") String token);

    @FormUrlEncoded
    @POST("customers/contact")
    Call  contactUs(@Field("id") String cust_id, @Field("message") String message);

    @GET("admin/banner")
    Call<List<BannerDataClass>>  getAllBanners();

    @FormUrlEncoded
    @POST("products")
    Call<ProductsDataClass>  addProduct(
            @Field("product_name") String product_name,
            @Field("brand_id") String brand_id,
            @Field("cat_id") String cat_id,
            @Field("unit_id") String unit_id,
            @Field("weight") String weight,
            @Field("rate") String rate

    );

}






