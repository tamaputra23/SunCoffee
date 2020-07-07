package com.example.suncoffee;

import android.support.v7.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends BaseActivity {
    RelativeLayout order_btn;
    RelativeLayout delivery_btn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        order_btn = findViewById(R.id.btn_Order);
        delivery_btn = findViewById(R.id.btn_delivery);
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order();
            }
        });
        delivery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delivery();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }

    }
    private void onAuthSuccess(FirebaseUser user) {

        // Write new user
        writeNewUser(user.getUid(), status);

    }
    private void writeNewUser(String userId, String status) {
        String Status = status;
        mDatabase.child("users").child(userId).setValue(Status);
    }
    private void Order(){
        status = "Order";
        Intent intent = new Intent(MainActivity.this, StoreActivity.class);
        intent.putExtra("status", status);
        startActivity(intent);
    }
    private void Delivery(){
        status = "Delivery";
        Intent intent = new Intent(MainActivity.this, StoreActivity.class);
        intent.putExtra("status", status);
        startActivity(intent);
    }
}
