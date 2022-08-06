package com.rice.mandi.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartDAO {

    @Query("select * from carttableclass")
    List<CartTableClass> getAllCartData();

    @Query("select * from carttableclass")
    LiveData<List<CartTableClass>> getAllLiveCartData();

    @Insert
    void addItemToCart(CartTableClass cartTableClasses);

    @Query("update carttableclass set product_quantity = :product_quantity where product_id = :product_id")
    void updateQuantity(int product_id, int product_quantity);

    @Query("select COUNT(*) from carttableclass where product_id = :product_id")
    int getCount(int product_id);

    @Query("select COUNT(*) from carttableclass")
    int getCount();

    @Query("select product_quantity from carttableclass where product_id = :product_id")
    int getQuantity(int product_id);

    @Query("delete from carttableclass where product_id = :product_id")
    void deleteItemFromCart(int product_id);

    @Query("delete from carttableclass")
    void deleteCartItems();
}
