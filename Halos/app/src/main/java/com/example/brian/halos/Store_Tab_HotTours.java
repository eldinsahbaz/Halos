package com.example.brian.halos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Store_Tab_HotTours extends Fragment implements Tour_Display_Frag.OnFragmentInteractionListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Parameter";
    RecyclerView recyclerView;
    public List<Tour> hotTourlist = new ArrayList<Tour>();;
    Tour test1 = new Tour();
    Tour test2 = new Tour();
    Tour test3 = new Tour();
    Tour test4 = new Tour();
    Tour test5 = new Tour();
    Tour test6 = new Tour();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Store_Tab_HotTours() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Store_Tab_HotTours newInstance(String param) {
        Store_Tab_HotTours fragment = new Store_Tab_HotTours();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        hotTourlist.add(test1);
        hotTourlist.add(test2);
        hotTourlist.add(test3);
        hotTourlist.add(test4);
        hotTourlist.add(test5);
        hotTourlist.add(test6);
        final View view = inflater.inflate(R.layout.fragment_store__tab__hot_tours, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.RecycleView_HotTours);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        final Store_RecycleAdapter adapter = new Store_RecycleAdapter(getActivity(),hotTourlist);
        recyclerView.setAdapter(adapter);
        adapter.SetTourListener(new Store_RecycleAdapter.TourListener() {
            @Override
            public void tourClick(View view, int position) {
                Tour DisplayTour = hotTourlist.get(position);
                getFragmentManager().beginTransaction()
                        .replace(R.id.RecycleView_Container,Tour_Display_Frag.newInstance(DisplayTour))
                        .addToBackStack(null).commit();
            }
        });
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
