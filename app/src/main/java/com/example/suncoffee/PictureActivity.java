package com.example.suncoffee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suncoffee.models.model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class PictureActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    DatabaseReference mDatabase;
    SharedPreferences mSharedPref; //for saving sort settings
    LinearLayoutManager mLayoutManager; //for sorting
    ImageView imgPlace;
    String store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PLACE");
        store  = getIntent().getStringExtra("store");
        myRef = FirebaseDatabase.getInstance().getReference().child("Picture").child(store);
        recyclerView = findViewById(R.id.recyclerView4);
        RecyclerView.LayoutManager mLayoutManager = new
               GridLayoutManager(getApplicationContext(), 2);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(false);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<model, PictureViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<model, PictureViewHolder>(
                        model.class,
                        R.layout.pictureview,
                        PictureViewHolder.class,
                        myRef
                ){
                    @Override
                    protected void populateViewHolder(PictureViewHolder viewHolder, model model, int i) {
                        viewHolder.setDetails(getApplicationContext(), model.getImage());
                    }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

        public static class PictureViewHolder extends RecyclerView.ViewHolder  {
        View mView;
        Button btnAdd;
        public PictureViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnAdd = mView.findViewById(R.id.btn_add);

        }
        public void setDetails(Context ctx, String image){
            ImageView imgMenu = mView.findViewById(R.id.img_place);
            Picasso.get().load(image).into(imgMenu);
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
                Intent intent = new Intent(PictureActivity.this, ConfirmActivity.class);
                startActivity(intent);
                finish();
            case R.id.action_checkout:
                Intent intent1 = new Intent(PictureActivity.this, CheckoutActivity.class);
                startActivity(intent1);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
