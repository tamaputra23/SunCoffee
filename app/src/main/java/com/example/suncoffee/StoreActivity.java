package com.example.suncoffee;

import android.support.v7.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.suncoffee.models.Order;
import com.google.firebase.auth.FirebaseAuth;

public class StoreActivity extends AppCompatActivity {
    LinearLayout lakalakaBtn, kuloBtn, janjijiwaBtn, foreBtn, hanggarBtn, kenanganBtn, pedalBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        final String status = getIntent().getStringExtra("status");
        //getActionBar().setTitle(status);
        lakalakaBtn = findViewById(R.id.btn_lakalaka);
        kuloBtn = findViewById(R.id.btn_kulo);
        janjijiwaBtn = findViewById(R.id.btn_janjijiwa);
        foreBtn = findViewById(R.id.btn_fore);
        hanggarBtn = findViewById(R.id.btn_hanggar);
        kenanganBtn = findViewById(R.id.btn_kenangan);
        pedalBtn = findViewById(R.id.btn_pedal);

        lakalakaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, OrderActivity.class);
                String store = "KL";
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
        kuloBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, OrderActivity.class);
                String store = "KU";
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
        pedalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, OrderActivity.class);
                String store = "PK";
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
        kenanganBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, OrderActivity.class);
                String store = "KK";
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
        janjijiwaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, OrderActivity.class);
                String store = "KJ";
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
        hanggarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, OrderActivity.class);
                String store = "HK";
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
        foreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, OrderActivity.class);
                String store = "FC";
                intent.putExtra("store", store);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case R.id.action_keranjang:
                Intent intent = new Intent(StoreActivity.this, ConfirmActivity.class);
                startActivity(intent);
            case R.id.action_checkout:
                Intent intent1 = new Intent(StoreActivity.this, CheckoutActivity.class);
                startActivity(intent1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
