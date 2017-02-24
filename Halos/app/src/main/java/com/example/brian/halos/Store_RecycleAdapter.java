package com.example.brian.halos;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by raych on 2/24/2017.
 */

public class Store_RecycleAdapter extends RecyclerView.Adapter<Store_RecycleAdapter.ViewHolder> implements
Store_Tab_Checkout.OnFragmentInteractionListener,Store_Tab_TopPaid.OnFragmentInteractionListener,Store_Tab_Explore.OnFragmentInteractionListener,
        Store_Tab_HotTours.OnFragmentInteractionListener,Store_Tab_TopFree.OnFragmentInteractionListener
{

    private List<Tour> tourlist;
    private Context tourcontext;
    TourListener tourListener;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface TourListener {
        public void tourClick(View view,int position);
    }
    public void SetTourListener(final TourListener listener) {
        this.tourListener =listener;
    }
    public Store_RecycleAdapter(Context content, List<Tour> data) {
        tourcontext=content;
        tourlist=data;
    }

    @Override
    public Store_RecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardlayout;
        cardlayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.tourcard,parent,false);
        ViewHolder cardholder =new ViewHolder(cardlayout);
        return cardholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tour tour = tourlist.get(position);
        holder.SetData(tour);
    }



    @Override
    public int getItemCount() {
        return tourlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView Picture;
        public TextView Title,Description,user_creator,Price;


        public ViewHolder(View itemView) {
            super(itemView);
            Picture = (ImageView)itemView.findViewById(R.id.card_image);
            Title = (TextView)itemView.findViewById(R.id.card_title);
            Description = (TextView)itemView.findViewById(R.id.card_description);
            user_creator = (TextView)itemView.findViewById(R.id.card_creator);
            Price = (TextView)itemView.findViewById(R.id.card_price);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tourListener != null){
                        tourListener.tourClick(view,getAdapterPosition());
                    }
                }
            });
        }

        public void SetData(Tour tour) {
            Picture.setImageResource(R.drawable.logo);  //Change when image is added into tour class
            Title.setText("CardTitleSet");
            Description.setText("A Description of the tour");
            user_creator.setText("By Creator");
            Price.setText(toString().valueOf(tour.getPrice()));
        }
    }
}
