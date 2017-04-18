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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class is the 2nd of the store and shows the hottest paid tours in the database.
 * Due to lack of time, there is no rating system so it just uses a Json Get Request
 * to retrieve a number of tours from the server based on an interval
 * and pass that data as an argument for the recycleview Adapter's constructor.
 */
public class Store_Tab_TopPaid extends Fragment implements Tour_Display_Frag.OnFragmentInteractionListener {
    private static final String ARG_PARAM1 = "Parameter";
    public int startpos;
    public int endpos;
    RecyclerView recyclerView;
    //public List<Tour> hotTourlist = new ArrayList<Tour>();
    public List<TourCopy> hotTourlist = new ArrayList<TourCopy>();
    Double zero = 0.0;
    TourCopy test1 = new TourCopy();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Store_Tab_TopPaid() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Store_Tab_TopPaid.
     */
    // TODO: Rename and change types and number of parameters
    public static Store_Tab_TopPaid newInstance(String param1, String param2) {
        Store_Tab_TopPaid fragment = new Store_Tab_TopPaid();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    Store_RecycleAdapter adapter;
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
       final View view = inflater.inflate(R.layout.fragment_store__tab__top_paid, container, false);
        test1.setCreator("Raymond Hu");
        test1.setDescription("Welcome To a Halos Tour");
        test1.setName("Journey in SU");
        try {
            test1.setPrice(zero);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hotTourlist.add(test1);

        startpos = 0;
        endpos = 15;
        GetTour getTour = new GetTour(startpos,endpos);
        getTour.execute();

        //Sets Up the Adapter to load the data into recycleview
        recyclerView = (RecyclerView)view.findViewById(R.id.RecycleView_TopPaid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Store_RecycleAdapter(getActivity(),hotTourlist);
        recyclerView.setAdapter(adapter);

        //Handles clicks on each cardview if the user wants more details on the tour
        //it will load the details Tour_display Fragment or user clicked on add button
        //to add tour to shopping list in the StoreActivity.
        adapter.SetTourListener(new Store_RecycleAdapter.TourListener() {
            @Override
            public void tourClick(View view, int position) {
                TourCopy DisplayTour = hotTourlist.get(position);
                getFragmentManager().beginTransaction()
                        .replace(R.id.RecycleView_Container,Tour_Display_Frag.newInstance(DisplayTour))
                        .addToBackStack(null).commit();
            }

            @Override
            public void addClick(View view, int position) {
                TourCopy tourCopy = hotTourlist.get(position);
                AddTourCopyListerner2 addTourCopyListerner2;
                try{
                    addTourCopyListerner2 = (AddTourCopyListerner2)getContext();
                    addTourCopyListerner2.AddTourCopy2(tourCopy);
                }catch (ClassCastException e){
                    throw new ClassCastException("Check");
                }
            }

        });
        return view;

    }

    public interface AddTourCopyListerner2{
        public void AddTourCopy2(TourCopy copy);
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

    //Class that executes to retrieve a specific number of tours from the server-side and load the data
    //into the store based on specific parameters. Filters out all free tours.

        private class GetTour extends AsyncTask<Void,Void,String> {
            OkHttpClient client = new OkHttpClient();
            String retVal;
            String start;
            String end;

            protected GetTour(int s , int e){
                start = String.valueOf(s);
                end =  String.valueOf(e);
            }

            @Override
            protected String doInBackground(Void... voids) {

                Request request = new Request.Builder()
                        // if you want to run on local use http://10.0.2.2:12344
                        // if you want to run on lcs server use http://lcs-vc-esahbaz.syr.edu:12344
                        .url("http://lcs-vc-esahbaz.syr.edu:12344/get_tour?start="+start+"&end="+end)
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
                        Log.v("Store_tab_toppaid", "onResponse:" + responseData);
                        retVal = "success";


                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONObject respObject = jsonObject.getJSONObject("response");
                            JSONArray rep = respObject.getJSONArray("result");
                            Log.v("Result","Got result array from Json object");
                            hotTourlist.clear();/////
                            for (int i = 0; i < rep.length() ; i++){
                                TourCopy tourcopy = new TourCopy();
                                JSONObject list = new JSONObject();
                                list = rep.getJSONObject(i);
                                Log.v("CHECk", list.getString("tour_id"));
                                tourcopy.setName(list.getString("tour_id"));
                                tourcopy.setDescription(list.getString("description"));
                                tourcopy.setPrice(Double.valueOf(list.getString("price")));
                                tourcopy.setCreator(list.getString("created-by"));
                                if (Double.valueOf(list.getString("price")) > 0) {
                                    hotTourlist.add(tourcopy);
                                }
                            }


                        } catch (Exception e){
                            Log.e("Store_tab_hot_tours", "Exception Thrown: " + e);
                        }

                    }


                });
                return retVal;
            }

            @Override
            protected void onPostExecute(String result) {
                // TODO: Must check that the location was processed to the database before making announcement
                //Log.d("RESULT", result);
              //  Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onPreExecute() {}

            @Override
            protected void onProgressUpdate(Void... values) {}


        }


    public void onBackPressed() {
    }
}
