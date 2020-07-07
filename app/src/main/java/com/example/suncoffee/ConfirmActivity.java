package com.example.suncoffee;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncoffee.models.Checkout;
import com.example.suncoffee.models.Order;
import com.example.suncoffee.models.OrderViewHolder;
import com.example.suncoffee.models.User;
import com.example.suncoffee.models.model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public  class ConfirmActivity extends BaseActivity {
    private static final String TAG = "ConfirmActivity";
    int intTotal;
    String totalprice;
    RecyclerView recyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    DatabaseReference mDatabase;
    SharedPreferences mSharedPref; //for saving sort settings
    LinearLayoutManager mLayoutManager; //for sorting
     FirebaseRecyclerAdapter<Order, OrderViewHolder> mAdapter;

    TextView tvName, tvPhone, tvUsername, tvPrice, tvDisc, tvTotal;
    Button btnRemove, btnDelivery, btnPickup;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CART");
        tvName = findViewById(R.id.tv_cfName);
        tvUsername = findViewById(R.id.tv_cfUsername);
        tvPhone = findViewById(R.id.tv_cfPhone);
        tvPrice = findViewById(R.id.tv_price);
        tvDisc = findViewById(R.id.tv_disc);
        tvTotal = findViewById(R.id.tv_total);
        btnPickup = findViewById(R.id.btn_pickup2);
        btnRemove = findViewById(R.id.btn_orderremove);
        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest"); //where if no settingsis selected newest will be default
        if (mSorting.equals("newest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means newest first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means oldest first
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();
        recyclerView = findViewById(R.id.recyclerView1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(false);

        btnPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userId = getUid();
                ViewDialog alert = new ViewDialog();
                alert.showDialog();
                mDatabase.child("user-orders").child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int sum = 0;
                        User Order = dataSnapshot.getValue(User.class);
                        if (Order == null){
                            tvPrice.setText("0");
                        }
                        for (DataSnapshot snapm : dataSnapshot.getChildren()) {
                            Map<String, Object> map = (Map <String, Object>) snapm.getValue();
                            Object mPrice = map.get("price");
                            Object name = map.get("author");
                            Object place = map.get("place");
                            Object mTitle = map.get("title");
                            writeNewPost(userId, String.valueOf(name), String.valueOf(place), String.valueOf(mTitle), String.valueOf(mPrice));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(ConfirmActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            String name = user.firstname + " " + user.lastname;
                            String uname= user.username;
                            String phone = user.phonenumber;
                            tvName.setText(name);
                            tvUsername.setText(uname);
                            tvPhone.setText(phone);
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]
        Query postsQuery = mDatabase.child("user-orders").child(userId);
        mAdapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(
                Order.class,
                R.layout.orderview,
                OrderViewHolder.class,
                postsQuery){
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final Order order, final int position) {
            viewHolder.bindToPost(order, new View.OnClickListener() {
                final DatabaseReference postRef = getRef(position);
                @Override
                public void onClick(View view) {
                    DatabaseReference UserOrderRef = mDatabase.child("user-orders").child(userId).child(postRef.getKey());
                    UserOrderRef.setValue(null);
                }
            });
            }

        };
        recyclerView.setAdapter(mAdapter);

        mDatabase.child("user-orders").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int sum = 0;
                int disc =0;
                int all = 0;
                User Order = dataSnapshot.getValue(User.class);
                if (Order == null){
                    tvPrice.setText("0");
                    tvTotal.setText("0");
                    tvDisc.setText("0");
                }
                for (DataSnapshot snapm : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map <String, Object>) snapm.getValue();
                    Object price = map.get("price");
                    int total = Integer.parseInt(String.valueOf(price));
                    sum+= total;
                    tvPrice.setText(String.valueOf(sum));
                    disc+= sum*0.20;
                    tvDisc.setText(String.valueOf(disc));
                    all += total - (total*0.2);
                    tvTotal.setText(String.valueOf(all));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void writeNewPost(String userId, String name, String place, String title, String price) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("order").push().getKey();
        Checkout checkout = new Checkout(userId, name ,place , title, price);
        Map<String, Object> postValues = checkout.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/order/" + key, postValues);
        childUpdates.put("/user-checkout/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }
    public class ViewDialog extends BaseActivity {

        public void showDialog(){
            final Dialog dialog = new Dialog(ConfirmActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.order_popup);

            ImageView imgClose = dialog.findViewById(R.id.img_close);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String userId = getUid();
                    DatabaseReference UserOrderRef = mDatabase.child("user-orders").child(userId);
                    UserOrderRef.setValue(null);
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case R.id.action_keranjang:
                Intent intent = new Intent(ConfirmActivity.this, ConfirmActivity.class);
                startActivity(intent);
                finish();
            case R.id.action_checkout:
                Intent intent1 = new Intent(ConfirmActivity.this, CheckoutActivity.class);
                startActivity(intent1);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

