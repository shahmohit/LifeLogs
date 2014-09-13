package com.android.lifelogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LifelogActivity extends FragmentActivity {

    String ACTIVITY_NAME = "LifelogActivity";
    static final int REQUEST_AUDIO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_VIDEO_CAPTURE = 3;
    currLocationReceiver currLocRec;
    String latData = null;
    String lonData = null;
    boolean isCurrent;
    String mCurrentFilePath;
    String Tags;
    GoogleMap mMap;
    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        isCurrent = intent.hasExtra("current");

        if (isCurrent) {
            mCurrentFilePath = intent.getExtras().getString("current");
            Tags = intent.getExtras().getString("tags");
            Log.d(ACTIVITY_NAME, mCurrentFilePath);
            Log.d(ACTIVITY_NAME,Tags);
        }
        else {
            Log.d(ACTIVITY_NAME,"No current file");
        }
        setContentView(R.layout.activity_lifelog);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.lifelog_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item,mDrawerTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description for accessibility */
                R.string.app_name  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(getResources().getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getResources().getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            // Defer code dependent on restoration of previous instance state.
        };
        mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setUpMapIfNeeded();
        initMaps();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
    }
    private class currLocationReceiver extends BroadcastReceiver {

        public currLocationReceiver() {

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ACTIVITY_NAME, "Receiver Got Location");
            latData = intent.getExtras().getString(Constants.LAT_DATA);
            lonData = intent.getExtras().getString(Constants.LON_DATA);
            if (latData != null) {
                Log.d(ACTIVITY_NAME, latData);
                Log.d(ACTIVITY_NAME, lonData);
            }
            else {
                Log.d(ACTIVITY_NAME,"Receiver Has Not Got Location Yet");
            }

        }
    };

    @Override
    protected void onStart() {
        Log.d(ACTIVITY_NAME,"On Start");
        super.onStart();
        getCurrentLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getCurrentLocation() {
        IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        currLocationReceiver currLocRec = new currLocationReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(currLocRec,mStatusIntentFilter);
        Intent mServiceIntent = new Intent(this, LocationService.class);
        startService(mServiceIntent);
        listenLocationReceiver();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            Log.d(ACTIVITY_NAME,"Map null");
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }
    }

    private void listenLocationReceiver() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng latlng = readCurrentLocation();
                if (latlng == null) {
                    listenLocationReceiver();
                }
                else {
                    displayCurrentMarker(latlng);
                }
            }
        }, 100);
    }

    private void initMaps() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng initPos = new LatLng(33.4172, -111.9365);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initPos, 15));
    }

    private LatLng readCurrentLocation() {
        String lat = latData;
        String lon = lonData;
        LatLng latlng = null;
        if ((lat != null) && (lon != null)) {
            latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            LocalBroadcastManager.getInstance(this).unregisterReceiver(currLocRec);
        }
        else {
            Log.d(ACTIVITY_NAME,"LatLong not received in Activity");
            latlng = null;
        }
        return latlng;
    }

    private void displayCurrentMarker(LatLng latlng) {
        Marker currMarker = mMap.addMarker(new MarkerOptions()
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lifelog, menu);
        return true;
    }
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_audio) {
            openAudio();
        }
        if (id == R.id.action_camera) {
            openCamera();
        }
        if (id == R.id.action_video) {
            openVideo();
        }
        if (id == R.id.action_search) {
            openSearch();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openAudio() {
        Intent intent = new Intent(LifelogActivity.this,AudioActivity.class);
        startActivityForResult(intent,REQUEST_AUDIO_CAPTURE);
    }

    public void openCamera() {
        dispatchTakePictureIntent();
    }

    public void openVideo() {
        dispatchRecordVideoIntent();
    }

    public void openSearch() {
        Intent intent = new Intent(LifelogActivity.this,SearchActivity.class);
        startActivity(intent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentFilePath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File video = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentFilePath = video.getAbsolutePath();
        return video;
    }

    private void dispatchRecordVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(videoFile));
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("videoTags");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment newFragment = VideoDialogFragment.newInstance(mCurrentFilePath);
                newFragment.show(ft, "videoTags");

            }
        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("imageTags");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment newFragment = ImageDialogFragment.newInstance(mCurrentFilePath);
                newFragment.show(ft, "imageTags");
            }
        }

        else if (requestCode == REQUEST_AUDIO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Tags = extras.getString("tags");
                mCurrentFilePath = extras.getString("current");
                Log.d(ACTIVITY_NAME,mCurrentFilePath);
                Log.d(ACTIVITY_NAME,Tags);
            }
            else {
                Log.d(ACTIVITY_NAME,"No Audio Recording Done");
            }
        }
    }


    public static class VideoDialogFragment extends DialogFragment {
        VideoView vView = null;
        EditText vTags;

        static VideoDialogFragment newInstance(String fPath) {
            VideoDialogFragment f = new VideoDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("fPath", fPath);
            f.setArguments(args);

            return f;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String fPath = getArguments().getString("fPath");
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.video_tags, null);
            v.setBackgroundResource(Color.TRANSPARENT);
            builder.setView(v);
            this.vView = (VideoView) v.findViewById(R.id.vsurface_view);
            this.vView.setVideoPath(fPath);
            this.vView.start();

            //builder.setTitle("Suggest Tags?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditText vTags = (EditText) VideoDialogFragment.this.getDialog().findViewById(R.id.video_tags);
                    //Tags = vTags.getText().toString();
                    VideoDialogFragment.this.getDialog().dismiss();
                    //Toast.makeText(LifelogActivity.this,Tags,Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    VideoDialogFragment.this.getDialog().cancel();
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static class ImageDialogFragment extends DialogFragment {
        ImageView iView = null;
        EditText iTags;
        static ImageDialogFragment newInstance(String fPath) {
            ImageDialogFragment f = new ImageDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("fPath", fPath);
            f.setArguments(args);

            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String fPath = getArguments().getString("fPath");
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.image_tags, null);
            v.setBackgroundResource(Color.TRANSPARENT);
            builder.setView(v);
            this.iView = (ImageView) v.findViewById(R.id.isurface_view);
            this.iView.setImageURI(Uri.parse("file://" + fPath));
            this.iView.setScaleType(ImageView.ScaleType.FIT_XY);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditText iTags = (EditText) ImageDialogFragment.this.getDialog().findViewById(R.id.image_tags);
                    //Tags = iTags.getText().toString();
                    ImageDialogFragment.this.getDialog().dismiss();
                    //Toast.makeText(LifelogActivity.this,Tags,Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    ImageDialogFragment.this.getDialog().cancel();
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
