package com.rice.mandi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.rice.mandi.Retrofit.ModuleClasses.BrandDataClass;
import com.rice.mandi.Retrofit.ModuleClasses.CustomersDataClass;
import com.rice.mandi.Retrofit.RetrofitApiClient;
import com.rice.mandi.Retrofit.RetrofitApiInterface;
import com.rice.mandi.adapter.slider.SliderAdapter;
import com.rice.mandi.databinding.ActivityMainBinding;
import com.rice.mandi.helper.CustomerDetails;
import com.rice.mandi.interfaces.BottomCartVisibilityInterface;
import com.rice.mandi.interfaces.OnChangeCartFragment;
import com.rice.mandi.interfaces.OnChangeFragment;
import com.rice.mandi.interfaces.OnChangeHomeFragment;
import com.rice.mandi.interfaces.OnLogin;
import com.rice.mandi.room.AppDatabase;
import com.rice.mandi.room.CartTableClass;
import com.rice.mandi.ui.about.AboutUsFragment;
import com.rice.mandi.ui.brands.BrandsFragment;
import com.rice.mandi.ui.cart.CartFragment;
import com.rice.mandi.ui.contact.ContactUsFragment;
import com.rice.mandi.ui.home.HomeFragment;
import com.rice.mandi.ui.notifications.NotificationsFragment;
import com.rice.mandi.ui.orders.OrdersFragment;
import com.rice.mandi.ui.products.ProductsFragment;
import com.rice.mandi.ui.profile.ProfileFragment;


import java.io.IOException;
import java.net.NoRouteToHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnChangeFragment, OnChangeHomeFragment, OnChangeCartFragment, MenuItem.OnMenuItemClickListener, OnLogin, NavigationView.OnNavigationItemSelectedListener, BottomCartVisibilityInterface {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private CustomerDetails customerDetails;

    Call<CustomersDataClass> call_customer;
    Call<Integer> call_token_register;
    private CustomersDataClass customersDataClass;
    MenuItem signInMenu;
    RetrofitApiInterface retrofitApiInterface;
    DrawerLayout drawer;
    Button MainBottomCart;
    CardView BottomCartContainer;
    TextView BottomCartCount, TotalCartValue;
    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = AppDatabase.getDbInstance(this);
        retrofitApiInterface = RetrofitApiClient.getRetrofitApiClient().create(RetrofitApiInterface.class);
        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_home));

        drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        signInMenu = menu.findItem(R.id.nav_signin);
        signInMenu.setOnMenuItemClickListener(this);

        MainBottomCart = findViewById(R.id.main_bottom_cart);
        BottomCartContainer = findViewById(R.id.bottom_cart_container);
        BottomCartCount = findViewById(R.id.bottom_cart_count);
        TotalCartValue = findViewById(R.id.total_cart_value);
        MainBottomCart.setOnClickListener(v -> onChangeCart());

        customerDetails = new CustomerDetails(this);
        if (customerDetails.getCustomerID().equals("")) {
            signInMenu.setTitle("உள்நுழைய");
        } else {
            signInMenu.setTitle("வெளியேறு");

        }


        db.cartDAO().getAllLiveCartData().observe(this, cartTableClasses -> {
          onUpdateCart();
        });



        changeToHome();

        if(customerDetails.getCustomerFCM().equals("")) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::sendFCMtoken);
        }

        if (customerDetails.getCustomerID().equals("")) triggerLoginPopup();

        

    }

    private void sendFCMtoken(String token) {
        call_token_register = retrofitApiInterface.registerFCM(token);
        customerDetails.setCustomerFCM(token);
        call_token_register.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                //Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText( MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void triggerLoginPopup() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showPopup();

            }
        };
        handler.postDelayed(runnable, 3000);
    }


    private void showPopup() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_item_phone, null);
        AlertDialog.Builder  builder = new AlertDialog.Builder(MainActivity.this);
        EditText PhoneNumber = view.findViewById(R.id.dialog_customer_phone);
        Button ActionSubmit = view.findViewById(R.id.dialog_submit);
        Button ActionCancel = view.findViewById(R.id.dialog_cancel);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        ActionSubmit.setOnClickListener(v -> {
            String phone_number = PhoneNumber.getText().toString();
            call_customer = retrofitApiInterface.checkCustomer(phone_number, "");
            call_customer.enqueue(new Callback<CustomersDataClass>() {
                @Override
                public void onResponse(Call<CustomersDataClass> call, Response<CustomersDataClass> response) {
                    customersDataClass = response.body();
                    //Toast.makeText(MainActivity.this, new Gson().toJson(customersDataClass), Toast.LENGTH_LONG).show();
                    customerDetails.setCustomerPhone(customersDataClass.getCustomer_phone());
                    customerDetails.setCustomerId(customersDataClass.getId());
                    alertDialog.dismiss();
                    signInMenu.setTitle("வெளியேறு");
                }

                @Override
                public void onFailure(Call<CustomersDataClass> call, Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText( MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Problem in network! Try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        });

        ActionCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menu_id = item.getItemId();
       // if (menu_id == R.id.action_notification) customerDetails.removeCustomerDetails();
        if (menu_id == R.id.action_notification) changeFragment(new NotificationsFragment(), "நோட்டிபிகேஷன்");
        if (menu_id == android.R.id.home) drawer.openDrawer(Gravity.LEFT);
        if(menu_id == R.id.action_cart) changeFragment(new CartFragment(), "கார்ட்");



        return true;
    }



    @Override
    public void onClickBrand(int brand_id) {

        Bundle bundle = new Bundle();
        bundle.putString("brand_id", brand_id+"");
        Fragment fragment = new ProductsFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_main,fragment);
        fragmentTransaction.commit();
        if(db.cartDAO().getCount() > 0) changeVisible(View.VISIBLE);
        else changeVisible(View.INVISIBLE);

    }

    @Override
    public void changeToHome() {
        Fragment fragment = new HomeFragment();
        binding.appBarMain.toolbar.setTitle("முகப்பு");
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_main,fragment);
        fragmentTransaction.commit();
        if(db.cartDAO().getCount() > 0) changeVisible(View.VISIBLE);
        else changeVisible(View.INVISIBLE);
    }

    @Override
    public void changeToOrder() {
        changeFragment(new OrdersFragment(), getResources().getString(R.string.menu_orders));
    }

    public void changeFragment(Fragment fragment, String title) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        binding.appBarMain.toolbar.setTitle(title);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_main,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        final Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (fragmentInFrame instanceof HomeFragment){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            MainActivity.super.onBackPressed();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;

                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("Exit", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener);
            Dialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setTitle("Are you sure want to exit?");
            dialog.show();

    }

        else
            changeToHome();


    }

    @Override
    public void onChangeCart() {
        Fragment fragment = new CartFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_main,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(!customerDetails.getCustomerID().equals("")) {
            DialogInterface.OnClickListener dialog = (DialogInterface.OnClickListener) (dialogInterface, i) -> {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        signInMenu.setTitle("உள்நுழைய");
                        customerDetails.setCustomerId("");
                        customerDetails.setCustomerName("");
                        customerDetails.setCustomerPhone("");
                        customerDetails.setCustomerAddress("");
                        customerDetails.setCustomerCity("");
                    }

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("வெளியேறு", dialog)
                    .setNegativeButton("இல்லை", dialog);

            Dialog dialog1 = builder.create();
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog1.setTitle("நீங்கள் நிச்சயமாக வெளியேற விரும்புகிறீர்களா?");
            dialog1.show();


        } else {
            showPopup();

        }

        return true;
    }

    @Override
    public void onChangeLogin() {
            signInMenu.setTitle("வெளியேறு");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int menu_id = item.getItemId();
        Fragment fragment = null;
        if(menu_id == R.id.nav_home) changeFragment(new HomeFragment(), getResources().getString(R.string.menu_home));
        if(menu_id == R.id.nav_brands) changeFragment(new BrandsFragment(), getResources().getString(R.string.menu_brands));
        if(menu_id == R.id.nav_products) changeFragment(new ProductsFragment(), getResources().getString(R.string.menu_products));
        if(menu_id == R.id.nav_cart) changeFragment(new CartFragment(), getResources().getString(R.string.menu_cart));
        if(menu_id == R.id.nav_orders) changeFragment(new OrdersFragment(), getResources().getString(R.string.menu_orders));
        //if(menu_id == R.id.nav_notification) changeFragment(new NotificationsFragment(), getResources().getString(R.string.menu_notification));
        if(menu_id == R.id.nav_profile) changeFragment(new ProfileFragment(), getResources().getString(R.string.menu_profile));
        if(menu_id == R.id.nav_about_us) changeFragment(new AboutUsFragment(), getResources().getString(R.string.menu_about_us));
        if(menu_id == R.id.nav_contact_us) changeFragment(new ContactUsFragment(), getResources().getString(R.string.menu_contact_us));
        if(db.cartDAO().getCount() > 0) changeVisible(View.VISIBLE);
        else changeVisible(View.INVISIBLE);
        drawer.closeDrawers();
        return true;
    }

    @Override
    public void changeVisible(int visibility) {
        this.BottomCartContainer.setVisibility(visibility);
    }

    @Override
    public void onUpdateCart() {
        int qnty = 0;
        float total_value = 0.00f;
        for (CartTableClass c: db.cartDAO().getAllCartData()) {
            qnty = qnty + c.product_quantity;
            total_value = total_value + (c.product_quantity * Float.parseFloat(c.product_rate));
        }
        BottomCartCount.setText(""+ qnty);
        TotalCartValue.setText("Rs." + String.format("%.2f", total_value) );
        if(db.cartDAO().getCount() > 0) changeVisible(View.VISIBLE);
        else changeVisible(View.INVISIBLE);
    }
}