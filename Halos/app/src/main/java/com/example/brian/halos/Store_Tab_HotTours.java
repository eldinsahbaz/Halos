package com.example.brian.halos;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Store_Tab_HotTours extends Fragment implements Tour_Display_Frag.OnFragmentInteractionListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Parameter";
    public int startpos;
    public int endpos;
    RecyclerView recyclerView;
    public List<Tour> hotTourlist = new ArrayList<Tour>();
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
        final View view = inflater.inflate(R.layout.fragment_store__tab__hot_tours, container, false);
        hotTourlist.add(test1);
        hotTourlist.add(test2);
        hotTourlist.add(test3);
        hotTourlist.add(test4);
        hotTourlist.add(test5);
        hotTourlist.add(test6);

        startpos = 0;
        endpos = 9;
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
        GetTour getTour = new GetTour();
        getTour.execute();
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



    private class GetTour extends AsyncTask<Void,Void,String> {
        OkHttpClient client = new OkHttpClient();
        String retVal;

        protected GetTour(){
        }

        @Override
        protected String doInBackground(Void... voids) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Map json_params = new HashMap<String, String>();
            json_params.put("start", String.valueOf(startpos));
            json_params.put("end", String.valueOf(endpos));


            // TODO: need to have an id associated and maybe other things (travelled, guided, etc + cookies, ip, etc)
            // TODO: need to encrypt data going over the wire

            JSONObject json_parameter = new JSONObject(json_params);
            RequestBody json_body = RequestBody.create(JSON, json_parameter.toString());
            Request request = new Request.Builder()
                    // if you want to run on local use http://10.0.2.2:12344
                    // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                    .url("http://lcs-vc-esahbaz.syr.edu:12344/get_tour")
                    .post(json_body)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Server Failure Response", call.request().body().toString());
                    retVal = "cannot connect to server";
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseData = response.body().string();
                    Log.v("Store_tab_hot", "onResponse:" + responseData);
                    retVal = "check";
                    /*
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject respObject = jsonObject.getJSONObject("response");
                        JSONArray jsonArray = new JSONArray(respObject);

                        retVal = result;



                    } catch (Exception e){
                        Log.e("Store_tab_hot_tours", "Exception Thrown: " + e);
                        retVal = e.toString();
                    } */
                }


            });
            return retVal;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO: Must check that the location was processed to the database before making announcement
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}


    }






}
