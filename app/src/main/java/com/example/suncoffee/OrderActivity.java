package com.example.suncoffee;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.net.Uri;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncoffee.models.Order;
import com.example.suncoffee.models.User;
import com.example.suncoffee.models.model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends BaseActivity {
    ImageView imgTitle, openhour;
    TextView tvTitle, tvSenin, tvSelasa, tvRabu, tvKamis, tvJumat, tvSabtu, tvMinggu;
    String store;
    Intent mapIntent;
    String goolgeMap = "com.google.android.apps.maps";
    Uri gmmIntentUri;

    RecyclerView recyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    DatabaseReference mDatabase;
    SharedPreferences mSharedPref; //for saving sort settings
    LinearLayoutManager mLayoutManager; //for sorting
    String place, maps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgTitle = findViewById(R.id.img_title);
        tvTitle = findViewById(R.id.tv_title);
        openhour = findViewById(R.id.btn_openhour);
        openhour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog();
            }
        });
        store  = getIntent().getStringExtra("store");
        if (store.equals("KL")){
            //getActionBar().setTitle("KOPI LAKA - LAKA");
            tvTitle.setText("KOPI LAKA - LAKA, MARGONDA");
            place = "KOPI LAKA - LAKA";
            maps = "Kopi Laka Laka Margonda";
            getSupportActionBar().setTitle(place);
            imgTitle.setImageResource(R.drawable.img_lakalaka);
        }
        else if(store.equals("PK")){
            tvTitle.setText("PEDAL KOPI,RTM");
            place = "PEDAL KOPI";
            maps = "Pedal Kopi";
            getSupportActionBar().setTitle(maps);
            imgTitle.setImageResource(R.drawable.img_pedal);
        }
        else if(store.equals("KK")){
            tvTitle.setText("KOPI KENANGAN MARGONDA");
            place = "KOPI KENANGAN";
            maps = "Kopi Kenangan - D'Mall Depok";
            getSupportActionBar().setTitle(place);
            imgTitle.setImageResource(R.drawable.img_kenangan);
        }
        else if(store.equals("KJ")){
            tvTitle.setText("KOPI JANJI JIWA, KELAPA DUA");
            place = "JANJI JIWA JILID 263";
            maps = "Janji Jiwa Jilid 263";
            getSupportActionBar().setTitle(maps);
            imgTitle.setImageResource(R.drawable.img_janjijiwa);
        }
        else if(store.equals("HK")){
            tvTitle.setText("HANGGAR KOPI, DEPOK");
            place = "HANGGAL KOPI";
            maps = "Hanggar Kopi Margonda";
            getSupportActionBar().setTitle(place);
            imgTitle.setImageResource(R.drawable.img_hanggar);
        }
        else if(store.equals("FC")){
            tvTitle.setText("FORE COFFEE, MARGONDA");
            place = "FORE COFFEE";
            maps = "Fore Coffee Margonda";
            getSupportActionBar().setTitle(maps);
            imgTitle.setImageResource(R.drawable.img_fore);
        }
        else if(store.equals("KU")){
            tvTitle.setText("KEDAI KOPI KULO, KELAPA DUA");
            place = "KOPI KULO";
            maps = "Kopi Kulo, jl Akses UI";
            getSupportActionBar().setTitle("Kedai Kopi Kulo");
            imgTitle.setImageResource(R.drawable.img_kulo);
        }
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
        myRef = mFirebaseDatabase.getReference().child("Data").child(store);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<model, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<model, ViewHolder>(
                        model.class,
                        R.layout.cardview,
                        ViewHolder.class,
                        myRef
                ){
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, model model, int i) {
                        viewHolder.setDetails(getApplicationContext(), model.getImage(), model.getPrice(), model.getTitle());
                        final String mImage = model.getImage();
                        final String mTitle = model.getTitle();
                        final String mPrice = model.getPrice();
                        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String TAG = "OrderAcctivity";
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
                                                    Toast.makeText(OrderActivity.this,
                                                            "Error: could not fetch user.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Write new post
                                                    String name = user.firstname + " " + user.lastname;
                                                    writeNewPost(userId, name, place, mTitle, mPrice);
                                                }

                                                // Finish this Activity, back to the stream
                                                finish();
                                                // [END_EXCLUDE]
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                            }
                                        });
                                // [END single_value_read]
                        Intent intent = new Intent(OrderActivity.this, ConfirmActivity.class);
                        startActivity(intent);
                        finish();
                            }
                        });
                    }
                    };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void writeNewPost(String userId, String name, String place, String title, String price) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("user-orders").push().getKey();
        Order order = new Order(userId, name ,place , title, price);
        Map<String, Object> postValues = order.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-orders/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
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
                Intent intent = new Intent(OrderActivity.this, ConfirmActivity.class);
                startActivity(intent);
                finish();
            case R.id.action_checkout:
                Intent intent1 = new Intent(OrderActivity.this, CheckoutActivity.class);
                startActivity(intent1);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void Navigasi(View view) {
        gmmIntentUri = Uri.parse("google.navigation:q=" + maps);
        // Buat Uri dari intent gmmIntentUri. Set action => ACTION_VIEW
        mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Set package Google Maps untuk tujuan aplikasi yang di Intent yaitu google maps
        mapIntent.setPackage(goolgeMap);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(OrderActivity.this, "Google Maps Belum Terinstal. Install Terlebih dahulu.",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void Place(View view){
        Intent inten = new Intent(OrderActivity.this, PictureActivity.class);
        inten.putExtra("store", store);
        startActivity(inten);
    }
    public void Openhour (View view){
        ViewDialog alert = new ViewDialog();
        alert.showDialog();

    }
    public class ViewDialog extends BaseActivity {

        public void showDialog() {
            final Dialog dialog = new Dialog(OrderActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.openhours_dialog);
            tvSenin = dialog.findViewById(R.id.tv_senin);
            tvSelasa = dialog.findViewById(R.id.tv_selasa);
            tvRabu = dialog.findViewById(R.id.tv_rabu);
            tvKamis = dialog.findViewById(R.id.tv_kamis);
            tvJumat = dialog.findViewById(R.id.tv_jumat);
            tvSabtu = dialog.findViewById(R.id.tv_sabtu);
            tvMinggu = dialog.findViewById(R.id.tv_minggu);
            ImageView imgClose = dialog.findViewById(R.id.img_close1);


            if(store.equals("KK")){
                tvSabtu.setText("10.00 - 23.00");
                tvMinggu.setText("10.00 - 23.00");
            }
            else if(store.equals("KJ")){
                tvSabtu.setText("10.00 - 23.00");
                tvMinggu.setText("10.00 - 23.00");
            }
            else if(store.equals("HK")){
                tvSabtu.setText("10.00 - 22.00");
                tvMinggu.setText("10.00 - 22.00");
            }
            else if(store.equals("FC")){
                tvSabtu.setText("09.00 - 23.00");
                tvMinggu.setText("09.00 - 23.00");
            }
            else if(store.equals("KU")){
                tvSabtu.setText("10.00 - 23.00");
                tvMinggu.setText("10.00 - 23.00");
            }
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        }

}
