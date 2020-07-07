package com.example.suncoffee;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncoffee.models.Order;

import com.example.suncoffee.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CheckoutActivity extends BaseActivity {
    private static final String TAG = "CheckoutActivity";
    RecyclerView recyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabase;
    SharedPreferences mSharedPref; //for saving sort settings
    LinearLayoutManager mLayoutManager; //for sorting
    FirebaseRecyclerAdapter<Order, CheckoutViewHolder> mAdapter;

    TextView tvName, tvPhone, tvUsername, tvPrice ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvName = findViewById(R.id.tv_cfName1);
        tvUsername = findViewById(R.id.tv_cfUsername1);
        tvPhone = findViewById(R.id.tv_cfPhone1);
        tvPrice = findViewById(R.id.tv_cfTotal1);
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
        recyclerView = findViewById(R.id.recyclerView2);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(false);

    }
    @Override
    protected void onStart() {
        super.onStart();
        final String userId = getUid();
        Query postsQuery = mDatabase.child("user-checkout").child(userId);
        mAdapter = new FirebaseRecyclerAdapter<Order, CheckoutViewHolder>(
                Order.class,
                R.layout.checkoutview,
                CheckoutViewHolder.class,
                postsQuery){
            @Override
            protected void populateViewHolder(final CheckoutViewHolder viewHolder, final Order order, final int position) {
                viewHolder.bindToPost(order);
            }

        };
        recyclerView.setAdapter(mAdapter);
        mDatabase.child("user-checkout").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int sum = 0;
                User Order = dataSnapshot.getValue(User.class);
                if (Order == null){
                    tvPrice.setText("0");
                }
                for (DataSnapshot snapm : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map <String, Object>) snapm.getValue();
                    Object price = map.get("price");
                    int total = Integer.parseInt(String.valueOf(price));
                    sum+= total - (total*0.2);
                    tvPrice.setText("Rp " + String.valueOf(sum));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

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
                            Toast.makeText(CheckoutActivity.this,
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
    }
    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvPlace, tvMenu, tvPrice;
        public CheckoutViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvPlace = mView.findViewById(R.id.tv_place1);
            tvMenu = mView.findViewById(R.id.tv_ordermenu1);
            tvPrice = mView.findViewById(R.id.tv_orderprice1);

        }
        public void bindToPost(Order order){
            tvPlace.setText(order.place);
            tvMenu.setText(order.title);
            tvPrice.setText("Rp " + order.price);
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
                Intent intent = new Intent(CheckoutActivity.this, ConfirmActivity.class);
                startActivity(intent);
                finish();
            case R.id.action_checkout:
                Intent intent1 = new Intent(CheckoutActivity.this, CheckoutActivity.class);
                startActivity(intent1);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
