package com.rice.mandi.adapter.recycler;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rice.mandi.R;
import com.rice.mandi.Retrofit.ModuleClasses.OrderWithItems;
import com.rice.mandi.Retrofit.ModuleClasses.OrdersDataClass;
import com.rice.mandi.helper.OrderStatus;
import com.rice.mandi.room.CartTableClass;
import org.jetbrains.annotations.NotNull;
import java.util.List;


public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.MyViewHolder> {

    View view;
    Context context;
    List<OrderWithItems> orderWithItemsList;


    OrderItemsRecyclerAdapter recyclerAdapter;
    List<CartTableClass> orderItemsList;


    public OrdersRecyclerAdapter(Context context, List<OrderWithItems> orderWithItemsList) {
        this.context = context;
        this.orderWithItemsList = orderWithItemsList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        OrdersDataClass data = orderWithItemsList.get(position).getOrdersDataClass();
        holder.OrderItemsTotal.setText("Rs." + data.getItems_total());
        holder.OrderDeliveryCharges.setText("Rs." + data.getDelivery_charges());
        holder.OrderTotalAmount.setText("Rs." + data.getTotal_amount());
        holder.OrderDate.setText(data.getOrder_date() + ' ' + data.getOrder_time());
        holder.OrderStatus.setText(getOrderStatus(data.getOrder_status()));
        orderItemsList = orderWithItemsList.get(position).getOrderItems();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setNestedScrollingEnabled(false);
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerAdapter = new OrderItemsRecyclerAdapter(context, orderItemsList);
        holder.recyclerView.setAdapter(recyclerAdapter);

        holder.OrderPaymentStatus.setText(getPaymentStatus(data.getPayment_status()));
        recyclerAdapter.notifyDataSetChanged();
    }

    private String getPaymentStatus(int payment_status) {
        return (payment_status == 0) ? "Unpaid": "Paid";
    }

    private String getOrderStatus(int order_status) {
        String status = "";
        OrderStatus.PROCESSING = 5;
        switch (order_status) {
            case 1:
                status = "Processing";
                break;
            case 2:
                status = "Accepted";
                break;
            case 3:
                status = "Cancelled";
                break;
            case 4:
                status = "Rejected";
                break;
            case 5:
                status = "Dispatched";
                break;
            case 6:
                status = "Delivered";
                break;
            default:
                status = "";
                break;
        }
        return status;
    }

    @Override
    public int getItemCount() {
        return orderWithItemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView OrderItemsTotal, OrderDeliveryCharges, OrderTotalAmount, OrderDate, OrderStatus, OrderPaymentStatus;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.order_items_recyclerview);
            OrderItemsTotal = itemView.findViewById(R.id.order_items_total);
            OrderDeliveryCharges = itemView.findViewById(R.id.order_delivery_charges);
            OrderTotalAmount = itemView.findViewById(R.id.order_total_amount);
            OrderDate = itemView.findViewById(R.id.order_date);
            OrderStatus = itemView.findViewById(R.id.order_status);
            OrderPaymentStatus = itemView.findViewById(R.id.order_payment_status);

        }
    }
}
