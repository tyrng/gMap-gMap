package com.example.ongty.gmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.transition.Slide;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.utils.LatLngUtils;
import com.example.ongty.gmap.models.item;
import com.example.ongty.gmap.models.place;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, DiscoverFragment.OnFragmentInteractionListener, ItemFragment.OnFragmentInteractionListener
        , NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, ShoppingList.OnFragmentInteractionListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    /** RADIUS FOR CIRCLE AND MARKER GENERATION (metres) */
    private static final int mRadius = 5000;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //TODO GET PLACE NEXT STEP
    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;


    private List<place> mDataPlaces;
    private List<Marker> mDataMarkers;

    private Marker addLocation;
    private List<Address> addresses;

    /** Upload Image */
    private static final int REQ_CODE = 1;

    private static final int RC_SIGN_IN = 123;
    android.support.v7.widget.Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu menu;
    private ItemFragment itemFragment;
    private DiscoverFragment discoverFragment;

    private FloatingActionButton fab;
    private FrameLayout frame;

    /** IMAGE UPLOAD FOR ITEMS */
    private String uploadedItemImage;

    private AutoCompleteTextView ACTV;

    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        checkCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout);
        setNavigationViewListener();
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /** load all navigationView here */
        loadNavigationView();

        /** load Bottom Navigation View */
        loadBottomNavigationView();

        /** SEARCH BAR*/
        ACTV = findViewById(R.id.mapSearchBar);
        searchBar();

    }
    /** TODO: CODE HERE ----------------------------------------------------------------------------- */

    /** On back pressed */
    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {

            if(findViewById(R.id.fragment_container).isShown()) {
                fab.show();
                frame = findViewById(R.id.fragment_container);
                frame.bringToFront();
                navigationView = findViewById(R.id.nav_view);
                navigationView.bringToFront();
            }

            super.onBackPressed();
        }
    }

    /** Top Navigation View ------------------------------------------------------------------------ */
    private void loadNavigationView(){
        final android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** floating action bar shown below screen */
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab = findViewById(R.id.fab);
                if(addLocation == null) {
                    try {
                        fab.setImageResource(R.drawable.ic_check_white_24dp);
                        addMarker(mMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "New pin point selected!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 456);
                    toast.show();

                    addItemFragment();
                    //TODO DELETE addLocation Marker after completion
                    addLocation.remove();
                    addLocation = null;
                    fab.setImageResource(R.drawable.ic_add_white_24dp);
                }
            }
        });
        /**=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=*/

        /** add drawer layout */
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /** Bottom Navigation View --------------------------------------------------------------------------------------------------- */
    private void loadBottomNavigationView(){
        BottomNavigationView navigation = findViewById(R.id.nav_bar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            /** hide or show button when navigate */
            fab = findViewById(R.id.fab);
            /** get frame to set active */
            frame = findViewById(R.id.fragment_container);
            /** get toolbar name*/
            toolbar = findViewById(R.id.toolbar);
            navigationView = findViewById(R.id.nav_view);

            switch (item.getItemId()) {
                case R.id.nav_bar_discover:
                    discoverFragment = new DiscoverFragment();
                    fragmentTransaction.replace(R.id.fragment_container, discoverFragment).addToBackStack(null).commit();
                    fab.hide();
                    ACTV.setVisibility(View.INVISIBLE);
//                    frame.setClickable(true);
//                    frame.setFocusable(true);
                    frame.bringChildToFront(findViewById(R.id.fragment_container));
                    navigationView.bringToFront();
                    toolbar.setTitle(R.string.nav_bar_discover);
                    return true;
                case R.id.nav_bar_map:
                    /** RESET FAB BUTTON */
                    fab.setImageResource(R.drawable.ic_add_white_24dp);
                    ACTV.setVisibility(View.VISIBLE);
                    if(addLocation!=null){
                        addLocation.remove();
                        addLocation = null;
                    }
                    /** set fragment into map */
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

                    /** only remove when fragment is not the gMap */
                    if(fragment != null) {
                        fragmentTransaction.remove(fragment).addToBackStack(null).commit();

                        NavigationView nav = findViewById(R.id.nav_view);
                        nav.bringToFront();

                        fab.show();
                        frame.bringToFront();
                        toolbar.setTitle(R.string.app_name);
                        navigationView.bringToFront();
                    }
                    return true;
                case R.id.nav_bar_addItem:
                    ACTV.setVisibility(View.INVISIBLE);
                    navigationView.bringToFront();
                    addItemFragment();
                    return true;
            }
            return false;
        }
    };


    //ADD ITEM FRAGMENT ----------------------------------------------------------------------------
    private void addItemFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /** get frame to set active */
        frame = findViewById(R.id.fragment_container);
        /** Handle Add Item action */
        itemFragment = new ItemFragment();

        /** get toolbar name*/
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.nav_bar_addItem);
        /** Bundle to pass argument from activity to fragment */
        if (addLocation != null){
            Bundle  bundleItemFragment = new Bundle();
            bundleItemFragment.putDouble("latitude", addLocation.getPosition().latitude);
            bundleItemFragment.putDouble("longitude", addLocation.getPosition().longitude);
            bundleItemFragment.putString("address", addresses.get(0).getAddressLine(0));
            itemFragment.setArguments(bundleItemFragment);
            toolbar.setTitle("Add Place with Item");
        }
        FloatingActionButton fab = findViewById(R.id.fab);

        fragmentTransaction.replace(R.id.fragment_container, itemFragment).addToBackStack(null).commit();
        fab.hide();
//        frame.setClickable(true);
//        frame.setFocusable(true);
        frame.bringChildToFront(findViewById(R.id.fragment_container));
    }


    /** Listener to link DiscoverFragment and ItemFragment interface */
    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    /** Select From drawer Menu*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /** Handle drawer item clicks here*/
        switch (item.getItemId()){
            case R.id.nav_shop_list:
                ShoppingList shoppingList = new ShoppingList();

                /** hide or show button when navigate */
                fab = findViewById(R.id.fab);
                /** get frame to set active */
                frame = findViewById(R.id.fragment_container);
                /** get toolbar name*/
                toolbar = findViewById(R.id.toolbar);
                /** */
                navigationView = findViewById(R.id.nav_view);
                AutoCompleteTextView searchText = findViewById(R.id.mapSearchBar);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container, shoppingList).addToBackStack(null).commit();
                fab.hide();
//                frame.setClickable(true);
//                frame.setFocusable(true);

                frame.bringToFront();
                toolbar.setTitle(R.string.nav_shop_list);
                navigationView.bringToFront();

                break;
            case R.id.action_log: {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // TODO make activity and fragments for navigation
    public void addMarker(GoogleMap googleMap) throws IOException {
        //TODO add Marker
        LatLng currentLocation = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);

        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_RED);
        addLocation = googleMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true).icon
                (BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Hold and drag me around!"))));
        //.title("Hold and drag me around!")
        //addLocation.showInfoWindow();
        Geocoder geocoder;
        geocoder =  new Geocoder(this, Locale.getDefault());
        //ADDRESS HEREEE
        addresses = geocoder.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1);
    }

    /** $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ */

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.option_get_place) {
//            showCurrentPlace();
//        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mDataMarkers = new ArrayList<>();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                iconFactory.setStyle(IconGenerator.STYLE_RED);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("New Location")));
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = (infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = (infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            // Prompt the user for permission.
            getLocationPermission();

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();

            //fetch existing location
            fetchExistingLocation(mMap);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            //TODO
                            //mMap.setMyLocationEnabled(false);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    circle = drawCircle(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(circle.getCenter(), getZoomLevel(circle)));
                                }
                            }, 3000);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    /**
     * DRAW BIG CIRCLE
     */
    private Circle drawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.fillColor(0x20FF0000); //4Ccdffb5
        circleOptions.strokeColor(0xE3FFB5);
        circleOptions.strokeWidth(4);
        circleOptions.radius(mRadius);
        return mMap.addCircle(circleOptions);
    }
    /**
     * GET CIRCLE ZOOM LEVEL
     */
    public float getZoomLevel(Circle circle) {
        float zoomLevel = 2;
        float zoomConstant = 720; // this is where we set relative zoom level
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / zoomConstant;
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                }else {
                    mLocationPermissionGranted = true;
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mLastKnownLocation = null;
                }
            }
            break;
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                //TODO STUDY/CONFIGURE PLACE LIKELIHOOD TO STORE MARKED LOCATIONS LATLNG
                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
        //
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            updateUIAfterLogin();

        } else {
            createSignInIntent();
        }
        // [END check_current_user]
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.my_great_logo)      // Set logo drawable
                        .setTheme(R.style.LoginTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                onSelectFromImageResult(data);
        } else if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK){
                onCaptureImageResult(data);
            }
        } else if (requestCode == RC_SIGN_IN) { // 123
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                updateUIAfterLogin();
                // Prompt the user for permission.
                getLocationPermission();

                // Turn on the My Location layer and the related control on the map.
                updateLocationUI();

                // Get the current location of the device and set the position of the map.
                getDeviceLocation();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                finish();
            }
        }
    }

    private void setNavigationViewListener() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void updateUIAfterLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.UsernameID);
        TextView navEmail = (TextView) headerView.findViewById(R.id.EmailID);
        final ImageView profile_pic = (ImageView) headerView.findViewById(R.id.profileImage);
        if (user.isAnonymous()) {
            navigationView.getMenu().findItem(R.id.action_log).setTitle(R.string.action_login);
            navUsername.setText("Hi There,");
            navEmail.setText("Welcome to MurahApp");
        } else {
            navigationView.getMenu().findItem(R.id.action_log).setTitle(R.string.action_logout);
            if (user.getDisplayName() != null) {
                navUsername.setText(user.getDisplayName());
            } else {
                navUsername.setText("Hi There,");
            }
            if (user.getEmail() != null) {
                navEmail.setText(user.getEmail());
            } else {
                navEmail.setText("Welcome to MurahApp");
            }
            if(user.getPhotoUrl() != null){
                loadImage(user.getPhotoUrl().toString(), profile_pic);
            }
        }
    }

    public void loadImage(final String url, final ImageView iv) {
        //start a background thread for networking
        new Thread(new Runnable() {
            public void run() {
                try {
                    //download the drawable
                    final Drawable drawable = Drawable.createFromStream((InputStream) new URL(url).getContent(), "src");
                    //edit the view in the UI thread
                    iv.post(new Runnable() {
                        public void run() {
                            iv.setImageDrawable(drawable);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Marker getAddLocation() {
        return addLocation;
    }

    public void setAddLocation(Marker addLocation) {
        this.addLocation = addLocation;
    }

    public void nullAddLocation() {
        this.addLocation = null;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromImageResult(Intent data) {
        ImageView ivImage = findViewById(R.id.ivPreview);
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                uploadedItemImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                Bundle  bundleItemFragment = new Bundle();
                if (addLocation != null){
                    bundleItemFragment.putDouble("latitude", addLocation.getPosition().latitude);
                    bundleItemFragment.putDouble("longitude", addLocation.getPosition().longitude);
                    bundleItemFragment.putString("address", addresses.get(0).getAddressLine(0));
                }
                bundleItemFragment.putString("image", uploadedItemImage);
                itemFragment.setArguments(bundleItemFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        ImageView ivImage = findViewById(R.id.ivPreview);
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        uploadedItemImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        Bundle  bundleItemFragment = new Bundle();
        if (addLocation != null){
            bundleItemFragment.putDouble("latitude", addLocation.getPosition().latitude);
            bundleItemFragment.putDouble("longitude", addLocation.getPosition().longitude);
            bundleItemFragment.putString("address", addresses.get(0).getAddressLine(0));
        }
        bundleItemFragment.putString("image", uploadedItemImage);
        itemFragment.setArguments(bundleItemFragment);
        ivImage.setImageBitmap(bm);
    }

    private void fetchExistingLocation(final GoogleMap mMap){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(mLocationPermissionGranted) {
                    /** Instantialize firebase */
                    FirebaseApp.initializeApp(getApplicationContext());
                    FirebaseDatabase data = FirebaseDatabase.getInstance();

                    DatabaseReference database = data.getReference();

                    //Child the root before all the push() keys are found and add a ValueEventListener()
                    database.child("places").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                            for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                                place onePlace = suggestionSnapshot.getValue(place.class);
                                if (mMap.getCameraPosition().target != null) {
//                                    if (circleContains(circle, new LatLng(onePlace.getLatitude(), onePlace.getLongitude()))) {
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(new LatLng(onePlace.getLatitude(), onePlace.getLongitude()));
                                        //markerOptions.title(onePlace.getName());
                                        IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                                        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(onePlace.getName())));
                                        mDataMarkers.add(mMap.addMarker(markerOptions));
//                                    }
                                }
                            }

                            /** DEFAULT ZOOM LEVEL IS ALL MARKER WITHIN RADIUS */
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                for (Marker marker : mDataMarkers) {
//                    builder.include(marker.getPosition());
//                }
//                LatLngBounds bounds = builder.build();
//
//                int padding = 0; // offset from edges of the map in pixels
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                mMap.moveCamera(cu);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }, 1000);

    }

    private void searchBar() {
        /** Instantialize firebase */
        FirebaseApp.initializeApp(this);
        FirebaseDatabase data = FirebaseDatabase.getInstance();

        final DatabaseReference database = data.getReference();
        mDataPlaces = new ArrayList<>();
        //Create a new ArrayAdapter with your context and the simple layout for the dropdown menu provided by Android
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        //Child the root before all the push() keys are found and add a ValueEventListener()
        database.child("places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                    //Get the suggestion by childing the key of the string you want to get.
                    place suggestion = suggestionSnapshot.getValue(place.class);
                    mDataPlaces.add(suggestion);
                    //Add the retrieved string to the list
                    autoComplete.add(suggestion.getName());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final AutoCompleteTextView ACTV = (AutoCompleteTextView) findViewById(R.id.mapSearchBar);
        ACTV.setAdapter(autoComplete);
        ACTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // on focus off
                    String str = ACTV.getText().toString();

                    ListAdapter listAdapter = ACTV.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    ACTV.setText("");

                }
            }
        });
        ACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                String selection = (String) parent.getItemAtPosition(pos);
                for(place p : mDataPlaces){
                    if(p.getName().equals(selection)){
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(p.getLatitude(),p.getLongitude()), DEFAULT_ZOOM));
                        break;
                    }
                }
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (!marker.equals(addLocation) && marker!=null)
        {
            /** Instantialize firebase */
            FirebaseApp.initializeApp(this);
            FirebaseDatabase data = FirebaseDatabase.getInstance();

            final DatabaseReference database = data.getReference();
            database.child("places").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                        //Get the suggestion by childing the key of the string you want to get.
                        place suggestion = suggestionSnapshot.getValue(place.class);
                        if(suggestion.getLatitude() == marker.getPosition().latitude && suggestion.getLongitude() == marker.getPosition().longitude){
                            discoverFragment = new DiscoverFragment();
                            Bundle  bundleItemFragment = new Bundle();
                            bundleItemFragment.putDouble("mLatitude", marker.getPosition().latitude);
                            bundleItemFragment.putDouble("mLongitude", marker.getPosition().longitude);
                            discoverFragment.setArguments(bundleItemFragment);

                            // GO TO DISCOVER FRAGMENT
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            /** get frame to set active */
                            frame = findViewById(R.id.fragment_container);
                            /** get toolbar name*/
                            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
                            FloatingActionButton fab = findViewById(R.id.fab);
                            fragmentTransaction.replace(R.id.fragment_container, discoverFragment).addToBackStack(null).commit();
                            fab.hide();
                            frame.bringChildToFront(findViewById(R.id.fragment_container));
                            toolbar.setTitle(suggestion.getName());
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            marker.showInfoWindow();
        }
        return true;
    }

    public void discoverArea(String name, double latitude, double longitude){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), DEFAULT_ZOOM));
        for(place p : mDataPlaces){
            if(p.getLatitude() == latitude && p.getLongitude() == longitude && !circleContains(circle, new LatLng(latitude,longitude))){
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                //markerOptions.title(onePlace.getName());
                IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(name)));
                mMap.addMarker(markerOptions);
                break;
            }
        }
    }

    public boolean circleContains(Circle circle, LatLng position) {
        LatLng center = circle.getCenter();
        double radius = circle.getRadius();
        float distance = LatLngUtils.distanceBetween(position, center);
        return distance < radius;
    }
}

