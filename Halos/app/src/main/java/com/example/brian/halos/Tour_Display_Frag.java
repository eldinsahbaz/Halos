package com.example.brian.halos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;


/**
 * Class that handle displaying and setting all the details/Information for the UI
 * when given a Tour Object in a new instance.
 */
public class Tour_Display_Frag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TOUR_OBJECT = "TourObject";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Tour_Display_Frag() {
        // Required empty public constructor
    }


    //
    public static Tour_Display_Frag newInstance(TourCopy tour) {
        Tour_Display_Frag fragment = new Tour_Display_Frag();
        Bundle args = new Bundle();
        args.putSerializable(TOUR_OBJECT , tour);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          //  mParam1 = getArguments().getString(TOUR_OBJECT );
        }
    }

    //Sets the data into each view id.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tour__display_, container, false);
        Bundle bundle = getArguments();
        //Tour Tourdata = (Tour) bundle.getParcelable(TOUR_OBJECT);
        TourCopy Tourdata = (TourCopy) bundle.getSerializable(TOUR_OBJECT);
        ImageView imageView= (ImageView)view.findViewById(R.id.Frag_Tour_Pic);
        TextView title = (TextView)view.findViewById(R.id.frag_tour_title);
        TextView Description = (TextView)view.findViewById(R.id.frag_tour_description);
        TextView creator = (TextView)view.findViewById(R.id.frag_tour_Creator);

        imageView.setImageResource(R.drawable.nyc);
        title.setText("Tour Name: " +Tourdata.getName());
        Description.setText("Description: "+Tourdata.getDescription());
        creator.setText("Creator: "+Tourdata.getCreator());
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
