package com.android.lifelogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LifelogActivity extends FragmentActivity {

    String ACTIVITY_NAME = "LifelogActivity";
    static final int REQUEST_AUDIO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_VIDEO_CAPTURE = 3;
    boolean isCurrent;
    String mCurrentFilePath;
    String Tags;
    //GoogleMap mMap;
    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    DrawerAdapter dAdapter;
    List<DrawerItem> dataList;
    //MapFragment mapFrag;
    Marker currMarker;
    SharedPreferences sharedPref;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifelog);

        sharedPref = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String firstName = sharedPref.getString("firstname", "null");
        String lastName = sharedPref.getString("lastname", "null");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.lifelog_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        dataList = new ArrayList<DrawerItem>();
        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        dataList.add(new DrawerItem(firstName + " " + lastName, R.drawable.ic_action_person));
        dataList.add(new DrawerItem("Search", R.drawable.ic_action_search));
        dataList.add(new DrawerItem("Preferences", R.drawable.ic_action_settings));
        dAdapter = new DrawerAdapter(this, R.layout.drawer_item, dataList);
        mDrawerList.setAdapter(dAdapter);

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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //setUpMap();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:

            case 1:

            case 2:
                mDrawerList.setItemChecked(position, true);
                setTitle("Preferences");
                mDrawerLayout.closeDrawer(mDrawerList);
                Intent intent = new Intent(LifelogActivity.this, PrefsActivity.class);
                startActivity(intent);

        }
    }

    @Override
    protected void onStart() {
        Log.d(ACTIVITY_NAME,"On Start");
        homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.lifelog_frame, homeFragment, "home_fragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(homeFragment).commit();
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
