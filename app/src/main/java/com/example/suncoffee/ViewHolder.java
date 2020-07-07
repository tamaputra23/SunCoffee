package com.example.suncoffee;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder  {
    View mView;
    Button btnAdd;
    public ViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        btnAdd = mView.findViewById(R.id.btn_add);

    }
    public void setDetails(Context ctx, String image, String price, String title){
        ImageView imgMenu = mView.findViewById(R.id.img_menu);
        TextView tvMenu = mView.findViewById(R.id.tv_menu);
        TextView tvPrice = mView.findViewById(R.id.tv_price);


        Picasso.get().load(image).into(imgMenu);
        tvMenu.setText(title);
        tvPrice.setText("Rp " + price);
    }
    private ViewHolder.ClickListener mClickListener;
    //interface to send callbacks
    public interface ClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }

}