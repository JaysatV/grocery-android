package com.rice.mandi.ui.cart;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.OrderWithItems;
import com.rice.mandi.Retrofit.ModuleClasses.OrdersDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.adapter.recycler.BrandsRecyclerAdapter;
import com.rice.mandi.adapter.recycler.CartRecyclerAdapter;
import com.rice.mandi.adapter.recycler.models.BrandModel;
import com.rice.mandi.databinding.CartFragmentBinding;
import com.rice.mandi.helper.CustomerDetails;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;
import com.rice.mandi.interfaces.OnChangeCartItems;
import com.rice.mandi.interfaces.OnChangeHomeFragment;
import com.rice.mandi.room.AppDatabase;
import com.rice.mandi.room.CartTableClass;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
import dev.shreyaspatil.easyupipayment.exception.AppNotFoundException;
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
import dev.shreyaspatil.easyupipayment.model.TransactionDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements PaymentResultListener, OnChangeCartItems, PaymentStatusListener {

    private CartViewModel mViewModel;
    private CartFragmentBinding cartFragmentBinding;

    RecyclerView recyclerView;
    CartRecyclerAdapter recyclerAdapter;
    List<CartTableClass> dataList;

    TextView CartItemsTotal, DeliveryCharges, TotalAmount;
    Button ActionAdd, GetLocation;

    EditText CustomerName, CustomerPhone, CustomerAddress;
    RadioGroup radioGroup;
    AppDatabase db;
    String selected_payment_method = "";
    String lati = "", longi = "";

    Float total;
    float delivery_charges;
    float final_total;
    final int UPI_PAYMENT = 0;

    Call<List<CartTableClass>> call_orders;
    private List<CartTableClass> ordersData;

    RetrofitApiInterface retrofitApiInterface;
    CustomerDetails customerDetails;

    FusedLocationProviderClient fusedLocationProviderClient;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        db = AppDatabase.getDbInstance(getContext());
        dataList = new ArrayList<>();
        dataList = db.cartDAO().getAllCartData();
        BottomCartVisibilityInterface bottomCartVisibilityInterface = (BottomCartVisibilityInterface) getContext();
        bottomCartVisibilityInterface.changeVisible(View.INVISIBLE);
        if (dataList.size() > 0) {
            mViewModel = new ViewModelProvider(this).get(CartViewModel.class);
            cartFragmentBinding = CartFragmentBinding.inflate(inflater, container, false);
            retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);
            CartItemsTotal = cartFragmentBinding.cartItemsTotal;
            DeliveryCharges = cartFragmentBinding.deliveryCharges;
            TotalAmount = cartFragmentBinding.totalAmount;
            recyclerView = cartFragmentBinding.cartRecyclerview;
            ActionAdd = cartFragmentBinding.cartActionAdd;
            CustomerName = cartFragmentBinding.cartCustomerName;
            CustomerPhone = cartFragmentBinding.cartCustomerPhone;
            CustomerAddress = cartFragmentBinding.cartCustomerAddress;
            customerDetails = new CustomerDetails(getContext());
            CustomerName.setText(customerDetails.getCustomerName());
            CustomerPhone.setText(customerDetails.getCustomerPhone());
            CustomerAddress.setText(customerDetails.getCustomerAddress());
            radioGroup = cartFragmentBinding.radioGroup;


            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerAdapter = new CartRecyclerAdapter(getContext(), dataList, this);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();

            total = recyclerAdapter.calcTotalAmount();
            delivery_charges = 30;
            final_total = total + delivery_charges;
            CartItemsTotal.setText("Rs." + total);
            DeliveryCharges.setText("Rs. " + delivery_charges);

            TotalAmount.setText("Rs. " + final_total);

            ActionAdd.setOnClickListener(v -> {
//            Toast.makeText(getContext(), new Gson().toJson(result_list), Toast.LENGTH_LONG).show();

                if (final_total >= 500) {
                    makePayment();
                } else {
                    Toast.makeText(getContext(), "Order amount must be above Rs.500", Toast.LENGTH_SHORT).show();
                }

                //createOrder();
            });

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//                switch (checkedId) {
//                    case R.id.radio_cod:
//                        this.selected_payment_method = "COD";
//                        break;
//                    case R.id.radio_card:
//                        this.selected_payment_method = "CARD";
//                        break;
//                    case R.id.radio_net:
//                        this.selected_payment_method = "NET";
//                        break;
//                    default:
//                        this.selected_payment_method = "";
//
//                }

                switch (checkedId) {
                    case R.id.radio_cod:
                        this.selected_payment_method = "COD";
                        break;
                    case R.id.radio_upi:
                        this.selected_payment_method = "UPI";
                        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("upi_number", "7402390313");
                        clipboardManager.setPrimaryClip(clipData);

                        Toast.makeText(getContext(), "Phone Number Copied.. Use this for Payment!", Toast.LENGTH_SHORT).show();
                        break;
                   
                    default:
                        this.selected_payment_method = "";

                }
            });

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            GetLocation = cartFragmentBinding.cartGetLocation;

            GetLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //check condition
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //when both permission are granted
                        //call method
                        getCurrentLocation();
                    } else {
                        // When permission is not granted
                        //request permission
                        ActivityCompat.requestPermissions(getActivity(), new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 100);
                    }

                }
            });
            return cartFragmentBinding.getRoot();
        } else {
            View view = inflater.inflate(R.layout.empty_cart, container, false);
            Button BuyProduct = view.findViewById(R.id.empty_buy_products);
            List<CartTableClass> ctc = db.cartDAO().getAllCartData();

            BuyProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnChangeHomeFragment onChangeHomeFragment = (OnChangeHomeFragment) getContext();
                    onChangeHomeFragment.changeToHome();
                }
            });
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == RESULT_OK) || (requestCode == 11))
        {
            ArrayList<String> dataList = new ArrayList<>();
            if (data != null) {
                dataList.add(data.getStringExtra("response"));
            }else {
                dataList.add(data.getStringExtra("nothing"));
            }
            upiDataPaymentOperation(dataList);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        //check condition
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            //when permission granted
            getCurrentLocation();
        } else {
            //when permission are denided
            //display toast
            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }


    }

    private void getCurrentLocation() {
        //Initialize location Manager
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        //check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //when location service is enabled
            //get last location
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                //initialize location
                Location location = task.getResult();
                if (location != null) {
                     lati = String.valueOf(location.getLatitude());
                     longi = String.valueOf(location.getLongitude());
                    Toast.makeText(getContext(), "Location Fetched", Toast.LENGTH_SHORT).show();
                } else {
                    //when location result is null
                    //Initialize location request
                    LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);

                    //initialize location callback
                    LocationCallback locationCallback  = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                            Location location1 = locationResult.getLastLocation();
                            //set latitude and longitude
                            lati = String.valueOf(location1.getLatitude());
                            longi = String.valueOf(location1.getLongitude());
                            Toast.makeText(getContext(), "Location Fetched", Toast.LENGTH_SHORT).show();
                        }
                    };
                    //Request location update
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }
            });
        } else {
            //when location service is not enabled
            //open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        }
    }

    private void makePayment() {

        if(selected_payment_method.equals("COD")) {
            createOrder();
        }else if(selected_payment_method.equals("UPI")){


            createOrder();

//            EasyUpiPayment.Builder builder = new EasyUpiPayment.Builder(getActivity())
//                    .setPayeeVpa("7402390313@okbizaxis")
//                    .setPayeeName("Jaysat")
//                    .setPayeeMerchantCode("BCR2DN4T3DELDSAW")
//                    .setTransactionId("T20200902123456")
//                    .setTransactionRefId("T20200902154")
//                    .setDescription("Hello Des")
//                    .setAmount("1.00");
//            try {
//                EasyUpiPayment easyUpiPayment = builder.build();
//                easyUpiPayment.setPaymentStatusListener(this);
//                easyUpiPayment.startPayment();
//            } catch (AppNotFoundException e) {
//                e.printStackTrace();
//            }
//            Uri uri = Uri.parse("upi://pay").buildUpon()
//                    .appendQueryParameter("pa", "7402390313@okbizaxis")
//                    .appendQueryParameter("pn", "Sivakasi Market")
//                    .appendQueryParameter("mc", "BCR2DN4T3DELDSAW")
//                    .appendQueryParameter("tr", "1234567890")
//                    .appendQueryParameter("tn", "Sivakasi Mart")
//                    .appendQueryParameter("am", "1")
//                    .appendQueryParameter("cu", "INR")
//                    .build();

//            Uri uri = Uri.parse("upi://pay").buildUpon().build();
//            String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.nbu.paisa.user";
//            Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
//
//            upiPayIntent.setData(uri);
//
//            Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
//
//            if(null != chooser.resolveActivity(getActivity().getPackageManager())) {
//              startActivity(chooser);
//            } else {
//                Toast.makeText(getContext(), "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
//            }
        }



    }

    private void upiDataPaymentOperation(ArrayList<String> dataList) {
        String str = dataList.get(0);
        String PaymentCancel = "";
        if(str == null) str="discard";
        String status = "";
        String approvalRefno = "";
        String response[] = str.split("&");

        for(int i=0; i< response.length; i++) {
            String equalStr[] = response[i].split("=");
            if (equalStr.length>=2) {
                if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                    status = equalStr[1].toLowerCase();
                } else if(equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())){
                    approvalRefno = equalStr[1];
                }
            } else {
                PaymentCancel = "Payment cancelled by user.";
            }

            if(status.equals("success")){
                Toast.makeText(getContext(), "Transaction Successfull", Toast.LENGTH_SHORT).show();
            } else if ("Payment cancelled by user.".equals(PaymentCancel)){
                Toast.makeText(getContext(), PaymentCancel, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createOrder() {

        if (!CustomerName.getText().toString().equals("") && !CustomerPhone.getText().toString().equals("") && !CustomerAddress.getText().toString().equals("")) {
            if (!lati.equals("")) {
                OrdersDataClass ordersDataClass = new OrdersDataClass();
                ordersDataClass.setCustomer_id(new CustomerDetails(getContext()).getCustomerID());
                ordersDataClass.setCustomer_name(CustomerName.getText().toString());
                ordersDataClass.setCustomer_phone(CustomerPhone.getText().toString());
                ordersDataClass.setDelivery_address(CustomerAddress.getText().toString());
                ordersDataClass.setItems_total(total);
                ordersDataClass.setDelivery_charges(delivery_charges);
                ordersDataClass.setTotal_amount(final_total);
                ordersDataClass.setPayment_method(selected_payment_method);
                ordersDataClass.setPayment_status(0);
                ordersDataClass.setPayment_ref("");
                ordersDataClass.setOrder_status(1);
                ordersDataClass.setLati(lati);
                ordersDataClass.setLongi(longi);
                ordersDataClass.setToken(customerDetails.getCustomerFCM());

                List<CartTableClass> cartTableClasses = db.cartDAO().getAllCartData();

                List<CartTableClass> result_list = new ArrayList<>();
                for (CartTableClass data : cartTableClasses) {
                    CartTableClass temp = new CartTableClass();
                    temp.product_id = data.product_id;
                    temp.product_name = data.product_name;
                    temp.purchase_from = data.purchase_from;
                    temp.product_weight = data.product_weight;
                    temp.product_quantity = data.product_quantity;
                    temp.product_rate = data.product_rate;
                    temp.order_id = "";
                    result_list.add(temp);
                }
                OrderWithItems orderWithItems = new OrderWithItems();
                orderWithItems.setOrdersDataClass(ordersDataClass);
                orderWithItems.setOrderItems(result_list);
                call_orders = retrofitApiInterface.createOrder(orderWithItems);
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Place order Please wait..");
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                progressDialog.show();
                call_orders.enqueue(new Callback<List<CartTableClass>>() {
                    @Override
                    public void onResponse(Call<List<CartTableClass>> call, Response<List<CartTableClass>> response) {
                        ordersData = response.body();
                        Toast.makeText(getContext(), "உங்கள் ஆர்டர் பதிவு செய்யப்பட்டது", Toast.LENGTH_SHORT).show();
                        clearCart();
                        progressDialog.dismiss();
                        OnChangeHomeFragment onChangeHomeFragment = (OnChangeHomeFragment) getContext();
                        onChangeHomeFragment.changeToOrder();
                    }


                    @Override
                    public void onFailure(Call<List<CartTableClass>> call, Throwable t) {
                        if (t instanceof IOException) {
                            Toast.makeText( getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please select delivery location", Toast.LENGTH_SHORT).show();
            }

        } else
            Toast.makeText(getContext(), "சரியான வாடிக்கையாளர் தகவலை உள்ளிடவும்", Toast.LENGTH_SHORT).show();

    }

    public void clearCart() {
        CustomerName.setText("");
        CustomerPhone.setText("");
        CustomerAddress.setText("");
        db.cartDAO().deleteCartItems();
        dataList.clear();
        recyclerAdapter.notifyDataSetChanged();
        CartItemsTotal.setText("Rs." + 0.00);
        DeliveryCharges.setText("Rs. " + 0.00);

        TotalAmount.setText("Rs. " + 0.00);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cartFragmentBinding = null;
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        createOrder();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteCartItem() {
        total = recyclerAdapter.calcTotalAmount();
        delivery_charges = 40;
        final_total = total + delivery_charges;
        CartItemsTotal.setText("Rs." + total);
        DeliveryCharges.setText("Rs. " + delivery_charges);

        TotalAmount.setText("Rs. " + final_total);
        if (db.cartDAO().getAllCartData().size() == 0) {
            Toast.makeText(getContext(), "Empty Cart!", Toast.LENGTH_SHORT).show();
            OnChangeHomeFragment onChangeHomeFragment = (OnChangeHomeFragment) getContext();
            onChangeHomeFragment.changeToHome();
        }
    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(getContext(), "Tr cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionCompleted(@NonNull TransactionDetails transactionDetails) {
        Toast.makeText(getContext(), "tr success", Toast.LENGTH_SHORT).show();
    }
}