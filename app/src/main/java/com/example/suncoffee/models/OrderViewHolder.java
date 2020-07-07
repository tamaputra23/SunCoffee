package com.example.suncoffee.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.suncoffee.R;


public class OrderViewHolder extends RecyclerView.ViewHolder {
    View mView;
    Button btnRemove;
    TextView tvPlace, tvMenu, tvPrice;
    public OrderViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        btnRemove = mView.findViewById(R.id.btn_orderremove);
         tvPlace = mView.findViewById(R.id.tv_place);
         tvMenu = mView.findViewById(R.id.tv_ordermenu);
         tvPrice = mView.findViewById(R.id.tv_orderprice);

    }
    public void bindToPost(Order order, View.OnClickListener removeClickListener){
        tvPlace.setText(order.place);
        tvMenu.setText(order.title);
        tvPrice.setText("Rp " + order.price);
        btnRemove.setOnClickListener(removeClickListener);
    }
}
