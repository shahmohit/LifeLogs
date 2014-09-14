package com.android.lifelogs;


import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    String FRAGMENT_NAME = "HomeFragment";
    String MAP_TAG = "home_map";
    View rootView;
    GoogleMap mMap;
    MapFragment mapFragment;
    currLocationReceiver currLocRec;
    String latData = null;
    String lonData = null;
    Marker currMarker;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mapFragment = new MapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.home_fragment, mapFragment, MAP_TAG).commit();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap = ((MapFragment) getChildFragmentManager().findFragmentByTag(MAP_TAG)).getMap();
        initMaps();
        getCurrentLocation();
    }

    @Override
    public void onDestroyView() {
        //FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //transaction.remove(mapFragment);
        //transaction.commit();
        super.onDestroyView();
    }

    private void initMaps() {
        if (mMap != null) {
            Log.d(FRAGMENT_NAME, "Map not null");
        } else {
            Log.d(FRAGMENT_NAME, "Map null");
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng initPos = new LatLng(33.4172, -111.9365);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initPos, 15));
    }

    private void getCurrentLocation() {
        IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        currLocationReceiver currLocRec = new currLocationReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(currLocRec, mStatusIntentFilter);
        Intent mServiceIntent = new Intent(getActivity(), LocationService.class);
        mServiceIntent.setAction(Constants.BROADCAST_ACTION);
        getActivity().startService(mServiceIntent);
        listenLocationReceiver();
    }

    private void listenLocationReceiver() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng latlng = readCurrentLocation();
                if (latlng == null) {
                    listenLocationReceiver();
                } else {
                    displayCurrentMarker(latlng);
                }
            }
        }, 100);
    }

    private LatLng readCurrentLocation() {
        String lat = latData;
        String lon = lonData;
        LatLng latlng = null;
        if ((lat != null) && (lon != null)) {
            latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(currLocRec);
        } else {
            Log.d(FRAGMENT_NAME, "LatLong not received in Activity");
            latlng = null;
        }
        return latlng;
    }

    private void displayCurrentMarker(LatLng latlng) {
        if (currMarker != null) {
            //mMap.
            currMarker.remove();
        }
        currMarker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .draggable(false)
                .title("You Are Here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        currMarker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng)      // Sets the center of the map to Current Position
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private class currLocationReceiver extends BroadcastReceiver {

        public currLocationReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BROADCAST_ACTION)) {
                Log.d(FRAGMENT_NAME, "Receiver Got Location");
                latData = intent.getExtras().getString(Constants.PLAT_DATA);
                lonData = intent.getExtras().getString(Constants.PLON_DATA);
                if (latData == null) {
                    Log.d(FRAGMENT_NAME, "Receiver Has Not Got Location Yet");
                }
            }

        }
    }

    ;
}
