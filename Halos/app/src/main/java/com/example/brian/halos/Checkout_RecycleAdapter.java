package com.example.brian.halos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by raych on 4/3/2017.
 */

public class Checkout_RecycleAdapter extends RecyclerView.Adapter<Checkout_RecycleAdapter.ViewHolder> {

    private List<TourCopy> tourlist;
    private Context tourcontext;

    public Checkout_RecycleAdapter(Context content, List<TourCopy> data) {
        tourcontext=content;
        tourlist=data;
    }

    @Override
    public Checkout_RecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardlayout;
        cardlayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card,parent,false);
        ViewHolder cardholder =new ViewHolder(cardlayout);
        return cardholder;
    }



    @Override
    public void onBindViewHolder(Checkout_RecycleAdapter.ViewHolder holder, int position) {
        TourCopy tour = tourlist.get(position);
        holder.SetData(tour);
    }

    @Override
    public int getItemCount() {
        if (tourlist == null){
            return 0;
        }else {
            return tourlist.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView Picture;
        public TextView Title,Description,user_creator,Price;

        public ViewHolder(View itemView) {
            super(itemView);
            Picture = (ImageView)itemView.findViewById(R.id.cart_image);
            Title = (TextView)itemView.findViewById(R.id.cart_title);
            Description = (TextView)itemView.findViewById(R.id.cart_description);
            user_creator = (TextView)itemView.findViewById(R.id.cart_creator);
            Price = (TextView)itemView.findViewById(R.id.cart_price);

        }

        public void SetData(TourCopy tour) {
            Picture.setImageResource(R.drawable.logo);  //Change when image is added into tour class
            Title.setText(tour.getName());
            Description.setText(tour.getDescription());
            user_creator.setText(tour.getCreator());
            Price.setText(toString().valueOf(tour.getPrice()));
        }

    }
}
