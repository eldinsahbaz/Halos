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
 * Created by raych on 4/7/2017.
 */

public class Profile_Adapter extends   RecyclerView.Adapter<Profile_Adapter.ViewHolder>{
    private Context tourlist;
    private List<String> list;
    TourclickListerner listerner;

    public Profile_Adapter(Context content, List<String> data){
        list=data;
        tourlist=content;
    }

    @Override
    public Profile_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardlayout;
        cardlayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);
        ViewHolder cardholder =new ViewHolder(cardlayout);
        return cardholder;
    }

    @Override
    public void onBindViewHolder(Profile_Adapter.ViewHolder holder, int position) {
        String name_of_tour = list.get(position);
        holder.SetData(name_of_tour);
    }

    @Override
    public int getItemCount() {
        if (list == null){
            return 0;
        }else {
            return list.size();
        }
    }
    public void SetTourListener(final TourclickListerner listener) {
        this.listerner =listener;
    }
    public interface TourclickListerner {
        public void userTourClick(View view,int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tourname;
        public ViewHolder(View itemView) {
            super(itemView);
            tourname = (TextView) itemView.findViewById(R.id.userProfile_card_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listerner.userTourClick(v,getAdapterPosition());
                }
            });
        }
        public void SetData(String name) {
            tourname.setText(name);
        }
    }
}
