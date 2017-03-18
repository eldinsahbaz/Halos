package com.example.brian.halos;

/**
 * Created by raych on 2/25/2017.
 */

public class DeveloperNotes {
        //DONT DELETE- until final product- need this for notes.
    /*
        Saving state of destroyed Frag
        oncreate-
        if(savedinstance != null)
        saved.instancestate.get whatever

        onsaveStateInstance
        outstate.put Int/serizable/blah ("savedinfo",object)


        when implemetning removeallviews in each store tab because of onclick.
        consider oncreate - if savedinstance is null (means new


        //might work - create a new fragment containing that viewpager. and just call this frag
        //oncreate in shoppingcart.
        //NEED FIXING- instead of a color Fragment Background -- make it so it can replace
        viewpager instead - view hierachy.




        for(int position = 0; position < mTour.landmarks.size()-1; position++)
        {
            LatLng start = new LatLng(mTour.landmarks.get(position).getLatitude(),mTour.landmarks.get(position).getLongitude());
            LatLng destination = new LatLng(mTour.landmarks.get(position).getLatitude(),mTour.landmarks.get(position).getLongitude());

            String url = getDirectionsUrl(start, destination);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }

        LatLng final_stop = new LatLng(mTour.landmarks.getLast().getLatitude(),mTour.landmarks.getLast().getLongitude());
        LatLng beginning = new LatLng(mTour.landmarks.getFirst().getLatitude(),mTour.landmarks.getFirst().getLongitude());

        String url = getDirectionsUrl(final_stop, beginning);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);






     */
}
