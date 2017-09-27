package com.tudorc.foundyou;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Target;

import de.hdodenhof.circleimageview.CircleImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap;
import static com.tudorc.foundyou.Constants.PLACES_ICONS;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        OnMarkerClickListener,
        OnCompleteListener<Void>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    public static Location mCurrentLocation;

    // UI Widgets.
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mLastUpdateTimeTextView;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    // Labels.
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;


    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;

    private TextView mUsernameView;
    private TextView mEmailView;
    private CircleImageView mProfilePictureView;
    private ImageView mProfileInitialsView;
    public Bitmap bitmap;

    private boolean isMainActivity = false;

    public FirebaseAuth mAuth;
    public static FirebaseUser mUser;
    public String mUsername;
    public String mEmail;
    public String mPhotoUrl;
    public static String mUID;
    public String mInitials;
    public static String mLastTribeUID;
    public static DatabaseReference mDatabase;
    public static String mNameTribe;


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    /**
     * Provides access to the Geofencing API.
     */
    private GeofencingClient mGeofencingClient;

    /**
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    private ArrayList locationTime;
    private Map<String, ArrayList> membersLocations;
    private Map<String, ArrayList> placesLocations;
    public static ArrayList<String> members;
    private Map<String, Marker> memberMarker;
    private Map<String, Marker> placeMarker;
    private String mMemberUID;
    private String placeKey;


    public DrawerLayout mDrawerLayout;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;


    public Toolbar toolbar;


    private GoogleMap mMap;

    public static Marker mUserMarker;

    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;

    private final Random mRandom = new Random();

    private Target mTarget;

    public Bitmap mBitmap;
    public Bitmap mUserBitmap;

    public ArrayList<String> tribesList;
    public static ArrayList<String> tribesListNames;

    public String newTribeUID;
    public String newTribeName;
    public ChildEventListener cListener1;
    public ChildEventListener cListener2;
    public ChildEventListener cListener3;
    public ChildEventListener cListener4;
    public ChildEventListener cListener5;
    public ValueEventListener vListener1;
    public ValueEventListener vListener2;


    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;


    public static HashMap<String, LatLng> geofencePlaces = new HashMap<>();

    private DetectedActivitiesAdapter mAdapter;

    /**
     * The DetectedActivities that we track in this sample. We use this for initializing the
     * {@code DetectedActivitiesAdapter}. We also use this for persisting state in
     * {@code onSaveInstanceState()} and restoring it in {@code onCreate()}. This ensures that each
     * activity is displayed with the correct confidence level upon orientation changes.
     */
    @SuppressWarnings("unchecked")
    private ArrayList<DetectedActivity> mDetectedActivities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate the UI widgets.
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);

        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        Log.w("DATA","AM FOST AICI DE ATATEA ORI");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //GET LAST TRIBE AND OTHER DATABASE INFO
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUID = mUser.getUid();
        setNavDrawer();
        setFab();
        updateValuesFromBundle(savedInstanceState);

        if (mLastTribeUID == null) {
            Log.w("TAG", "Sunt inainte de ValueEvent" + mLastTribeUID);
            mDatabase.child("users").child(mUID).child("lastTribe").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        Log.w("TAG", "Am trecut de on DataChange");
                        mLastTribeUID = snapshot.getValue().toString();
                        Log.w("TAG", "Sunt dupa on data change si aste e mLastTribeUID: " + mLastTribeUID);
                        Log.w("FetchLocations", " I fetched Locations");
                        fetchLocations();

                        getTribesNames();
                } else {
                        askInitialAlertDialog();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


        } else {
            fetchLocations();
        }

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);


        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();


        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;


        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);


            /*
            ListView detectedActivitiesListView = (ListView) findViewById(
                    R.id.detected_activities_listview);
*/
        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        // Enable either the Request Updates button or the Remove Updates button depending on
  /*          // whether activity updates have been requested.
            setButtonsEnabledState();
*/
        // Reuse the value of mDetectedActivities from the bundle if possible. This maintains state
        // across device orientation changes. If mDetectedActivities is not stored in the bundle,
        // populate it with DetectedActivity objects whose confidence is set to 0. Doing this
        // ensures that the bar graphs for only only the most recently detected activities are
        // filled in.
        if (savedInstanceState != null && savedInstanceState.containsKey(
                Constants.DETECTED_ACTIVITIES)) {

            mDetectedActivities = (ArrayList) savedInstanceState.getSerializable(
                    Constants.DETECTED_ACTIVITIES);
        } else {
            mDetectedActivities = new ArrayList<>();

            // Set the confidence level of each monitored activity to zero.
            for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
                mDetectedActivities.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], 0));
            }
        }

        // Bind the adapter to the ListView responsible for display data for detected activities.
        mAdapter = new DetectedActivitiesAdapter(this, mDetectedActivities);
/*
            detectedActivitiesListView.setAdapter(mAdapter);
*/

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        if (vListener1 == null)
            vListener1 = mDatabase.child("alerts").child(mUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        sendNotification(snapshot.getValue().toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

    }

    private void getTribesNames(){
        tribesList = new ArrayList<String>();
        tribesListNames = new ArrayList<String>();
        cListener1 = mDatabase.child("users").child(mUID).child("tribes")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        if (dataSnapshot.exists()) {
                            String cTribeUID = dataSnapshot.getKey();
                            Log.w("tribesList ", tribesList.toString());
                            Log.w("tribesListNames ", tribesListNames.toString());
                            Log.w("ctribeUID ", cTribeUID);
                            Log.w("mLastTribeUID ", mLastTribeUID);
                            if (!tribesList.contains(cTribeUID)) {
                                tribesList.add(cTribeUID);
                                tribesListNames.add(dataSnapshot.child("name").getValue().toString());
                                Log.w("tribesList ", tribesList.toString());
                                Log.w("tribesListNames ", tribesListNames.toString());
                            }
                            if (mLastTribeUID.equals(cTribeUID))
                                {
                                    Log.w("ctribeUID ", cTribeUID);
                                    mNameTribe = dataSnapshot.child("name").getValue().toString();
                                    Log.w("mNameTribe ", mNameTribe);
                                }
                            updateTribesList();
                        }
                    }
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    public void onCancelled(DatabaseError firebaseError) {}
                });
    }

    private void setNavDrawer() {

        // NAVIGATION DRAWER
        final ActionBar ab = getSupportActionBar();
                        /* to set the menu icon image*/
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);

        mUsername = mUser.getDisplayName();

        mUsernameView = (TextView) hView.findViewById(R.id.mUsernameView);
        mUsernameView.setText(getString(R.string.mUsername, mUsername));
        mEmail = mUser.getEmail();
        mEmailView = (TextView) hView.findViewById(R.id.mEmailView);
        mEmailView.setText(getString(R.string.mEmail, mEmail));

        mProfilePictureView = (CircleImageView) hView.findViewById(R.id.mProfilePicture);
        mProfileInitialsView = (ImageView) hView.findViewById(R.id.mProfileInitials);
        String[] parts = mUsername.split(" ");
        mInitials = parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(mUID);

        Log.w("TAG", mUsername + " " + mEmail + " " + mInitials + " " + mUser.getPhotoUrl().toString());

        if (mUser.getPhotoUrl() == null || mUser.getPhotoUrl().toString().indexOf("default") > 0) {
            Log.w(TAG, "Initials: " + mInitials);
            TextDrawable mProfileInitials = TextDrawable.builder()
                    .buildRound(mInitials, color);
            mProfileInitialsView.setImageDrawable(mProfileInitials);
            mProfilePictureView.setVisibility(View.GONE);
            mProfileInitialsView.setVisibility(View.VISIBLE);

            int width = mProfileInitials.getIntrinsicWidth();
            width = width > 0 ? width : 96; // Replaced the 1 by a 96
            int height = mProfileInitials.getIntrinsicHeight();
            height = height > 0 ? height : 96; // Replaced the 1 by a 96

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            mPhotoUrl = mUser.getPhotoUrl().toString();
            Log.w(TAG, "Picasso was used, photoUrl: " + mPhotoUrl);
            Picasso.with(getApplicationContext()).load(mPhotoUrl).noFade().into(mProfilePictureView);
            mProfileInitialsView.setVisibility(View.GONE);
            mProfilePictureView.setVisibility(View.VISIBLE);

            mTarget = new Target() {
                @Override
                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(output);

                    final int color = 0xff424242;
                    final Paint paint = new Paint();
                    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                    paint.setAntiAlias(true);
                    canvas.drawARGB(0, 0, 0, 0);
                    paint.setColor(color);
                    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
                    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                            bitmap.getWidth() / 2, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, rect, rect, paint);
                    mBitmap = output;
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            };
            Picasso.with(this.getApplicationContext())
                    .load(mPhotoUrl)
                    .into(mTarget);
            Log.w("bitmap"," Am facut bitmapul");
        }

        View.OnClickListener handleClick = new View.OnClickListener() {
            public void onClick(View v) {
                // Handle navigation view item clicks here.
                int id = v.getId();

                if (id == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                } else if (id == R.id.create_tribe) {
                    startActivity(new Intent(MainActivity.this, CreateTribeActivity.class));
                } else if (id == R.id.join_tribe) {
                    startActivity(new Intent(MainActivity.this, JoinTribeActivity.class));
                } else if (id == R.id.nav_places) {
                    startActivity(new Intent(MainActivity.this, PlacesActivity.class));
                } else if (id == R.id.nav_invite) {
                    Intent intent = new Intent(MainActivity.this, InviteActivity.class);
                    intent.putExtra("mLastTribeUID",mLastTribeUID);
                    startActivity(intent);
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                } else if (id == R.id.nav_faqs) {
                    startActivity(new Intent(MainActivity.this, FAQActivity.class));
                }
            }
        };
        findViewById(R.id.create_tribe).setOnClickListener(handleClick);
        findViewById(R.id.join_tribe).setOnClickListener(handleClick);
        findViewById(R.id.nav_settings).setOnClickListener(handleClick);
        findViewById(R.id.nav_invite).setOnClickListener(handleClick);
        findViewById(R.id.nav_places).setOnClickListener(handleClick);
        findViewById(R.id.nav_chat).setOnClickListener(handleClick);
        findViewById(R.id.nav_faqs).setOnClickListener(handleClick);
        updateTribesList();
    }


    private void updateTribesList(){
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName(mNameTribe);
        item1.setIconImg(R.drawable.checkmark);
        // Adding data header
        listDataHeader.add(item1);

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        if (tribesListNames != null){
            for (String tribe: tribesListNames)
            {
                if (!tribe.equals(mNameTribe))
                    heading1.add(tribe);
            }
        }
        listDataChild.put(listDataHeader.get(0), heading1);
        Log.w("TAG",mNameTribe +" "+tribesListNames);
        expandableList();
    }

    private void expandableList(){

        expandableList = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableList.setBackgroundResource(R.color.white);
        expandableList.setZ(1);
        mMenuAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                mDrawerLayout.closeDrawers();
                newTribeName = new String();
                newTribeUID = new String();
                newTribeName = (String) listDataChild.get(listDataHeader.get(i)).get(i1);

                DatabaseReference mDatabaseTribe=mDatabase.child("tribes");

                cListener3 = mDatabase.child("tribes").orderByChild("name").equalTo(newTribeName)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                if (dataSnapshot.exists() && dataSnapshot.getKey() != null) {
                                    Log.w("tribeUID", "tribeUID is this one: " + dataSnapshot);
                                    newTribeUID = dataSnapshot.getKey();
                                    mLastTribeUID = newTribeUID;
                                    mDatabase.child("users").child(mUID).child("lastTribe").setValue(newTribeUID);
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                    finish();
                                }}
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                            public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            public void onCancelled(DatabaseError firebaseError) {}
                        });
                return true ;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (expandableList.isGroupExpanded(0))
                    expandableList.collapseGroup(0);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });

    }


    private  void askInitialAlertDialog(){
        Log.w("S-a deschis Alerta", "Aici am de ales!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You don't belong to any tribe")
                .setCancelable(true)
                .setNeutralButton("Create Tribe", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(MainActivity.this, CreateTribeActivity.class));
                    }
                })
                .setPositiveButton("Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Join Tribe", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(MainActivity.this, JoinTribeActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem actionOverflow = menu.findItem(R.id.action_overflow);
        actionOverflow.setTitle("CHECK IN");
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Demonstrates customizing the info window and/or its contents. */


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);

    }
    @Override
    public void onMapLoaded() {
        if (mCurrentLocation != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 50));
        else mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.207270, 0.117760), 50));

    }


    private void addMarkersToMap() {
        mBitmap = setUserMarkerBitmap ();
        if (mBitmap == null) {
            String[] parts = mUsername.split(" ");
            mInitials = parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0);

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(mUID);
            Log.w(TAG, "Initials: " + mInitials);
            TextDrawable mProfileInitials = TextDrawable.builder()
                    .buildRound(mInitials, color);
            mProfileInitialsView.setImageDrawable(mProfileInitials);
            mProfilePictureView.setVisibility(View.GONE);
            mProfileInitialsView.setVisibility(View.VISIBLE);

            int width = mProfileInitials.getIntrinsicWidth();
            width = width > 0 ? width : 96; // Replaced the 1 by a 96
            int height = mProfileInitials.getIntrinsicHeight();
            height = height > 0 ? height : 96; // Replaced the 1 by a 96

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);
            mProfileInitials.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            mProfileInitials.draw(canvas);
            if (mBitmap == null) {
                mBitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.location);
            }
        }





        if (mCurrentLocation != null) {
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .title("Tudor")
                    .icon(fromBitmap(mBitmap))
                    .snippet("Tudor o iubeste pe Theuta"));
        } else {
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(52.207270, 0.117760))
                    .title("Tudor")
                    .icon(fromBitmap(mBitmap))
                    .snippet("Tudor o iubeste pe Theuta"));
        }
        updateMarkers();
        }


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);

        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateLocationUI();
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateLocationUI();
                        break;
                }
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
        updateLocationUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
        // Remove location updates to save battery.
        stopLocationUpdates();
    }
    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateLocationUI();
                    }
                });
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            mDatabase.child("users").child(mUID).child("lastLocation").child("latitude").setValue(mCurrentLocation.getLatitude());
            mDatabase.child("users").child(mUID).child("lastLocation").child("longitude").setValue(mCurrentLocation.getLongitude());
            mDatabase.child("users").child(mUID).child("lastLocation").child("lastTime").setValue(mLastUpdateTime);
            if (mLastTribeUID != null){
                mDatabase.child("tribes").child(mLastTribeUID).child("members").child(mUID).child("latitude").setValue(mCurrentLocation.getLatitude());
                mDatabase.child("tribes").child(mLastTribeUID).child("members").child(mUID).child("longitude").setValue(mCurrentLocation.getLongitude());
                mDatabase.child("tribes").child(mLastTribeUID).child("members").child(mUID).child("lastTime").setValue(mLastUpdateTime);
            }
/*
            mLatitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLatitudeLabel,
                    mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLongitudeLabel,
                    mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText(String.format(Locale.ENGLISH, "%s: %s",
                    mLastUpdateTimeLabel, mLastUpdateTime));
*/
            if (mCurrentLocation != null && mUserMarker != null)
                mUserMarker.setPosition(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));
        }
    }


    private void updateMarkers(){
        if (membersLocations != null) {
            Log.w("membersLocations check ",membersLocations.toString());
            for (Map.Entry<String, ArrayList> entry : membersLocations.entrySet()) {
                String member = entry.getKey();
                Log.w("le map",entry.toString());
                Log.w("member name",member);
                ArrayList locationTime = entry.getValue();
                LatLng location = (LatLng) locationTime.get(0);
                String lastTime = (String) locationTime.get(1);
                String url = (String) locationTime.get(2);
                memberMarker.put(member, mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(fromBitmap(setMarkerBitmap(url)))
                        .snippet("Last update: "+lastTime)
                        .title(member)));
            }
        }
        if (placesLocations != null) {
            for (Map.Entry<String, ArrayList> entry : placesLocations.entrySet()) {
                String place = entry.getKey();
                Log.w("le map",entry.toString());
                ArrayList nameLocationIcon = entry.getValue();
                String name = (String) nameLocationIcon.get(0);
                LatLng location = (LatLng) locationTime.get(1);
                int icon = (int) locationTime.get(2);
                placeMarker.put(place, mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromResource(icon))
                        .title(name)));
            }
        }
    }
    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void fetchLocations(){
        membersLocations = new HashMap<String, ArrayList>();
        memberMarker = new HashMap<String, Marker>();
        members = new ArrayList<String>();
        Log.w("am ajuns ","inainte de clistener2 "+ membersLocations.toString());
        cListener2 = mDatabase.child("tribes").child(mLastTribeUID).child("members")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        Log.w("AM trecut de ", "on child Added din fetchlocations");
                        if (dataSnapshot.exists()) {
                            members.add(dataSnapshot.getKey());
                        }
                        handleMemberSnapshot(dataSnapshot);
                        }
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        handleMemberSnapshot(dataSnapshot);
                    }
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    public void onCancelled(DatabaseError firebaseError) {}
                });

        placesLocations = new HashMap<String, ArrayList>();
        placeMarker = new HashMap<String, Marker>();
        cListener5 = mDatabase.child("tribes").child(mLastTribeUID).child("places")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        handlePlaceSnapshot(dataSnapshot);
                    }
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        handlePlaceSnapshot (dataSnapshot);
                    }
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    public void onCancelled(DatabaseError firebaseError) {}
                });

    }

    public void handleMemberSnapshot (DataSnapshot dataSnapshot){
        if (dataSnapshot.exists() &&
                !dataSnapshot.getKey().equals(mUID) &&
                dataSnapshot.child("latitude").getValue() != null &&
                dataSnapshot.child("longitude").getValue() != null &&
                dataSnapshot.child("lastTime").getValue() != null) {
            mMemberUID = dataSnapshot.getKey();
            Log.w(" the member", mMemberUID);
            double mLastLat = (double) dataSnapshot.child("latitude").getValue();
            double mLastLong = (double) dataSnapshot.child("longitude").getValue();
            String mLastUpdatedTime = (String) dataSnapshot.child("lastTime").getValue();
            String mURL = (String) dataSnapshot.child("url").getValue();
            LatLng mLastLocation = new LatLng(mLastLat, mLastLong);
            ArrayList mLocationTime = new ArrayList<>();
            mLocationTime.add(0, mLastLocation);
            mLocationTime.add(1, mLastUpdatedTime);
            mLocationTime.add(2, mURL);
            membersLocations.put(mMemberUID, mLocationTime);

            if (!memberMarker.containsKey(mMemberUID) && mMap!= null && mLastLocation!= null) {
                memberMarker.put(mMemberUID, mMap.addMarker(new MarkerOptions()
                        .position(mLastLocation)
                        .snippet("Last update: "+mLastUpdatedTime)
                        .icon(fromBitmap(setMarkerBitmap(mURL)))));
            } else if (memberMarker.get(mMemberUID)!= null && mMap!= null && mLastLocation!= null){
                memberMarker.get(mMemberUID).setPosition(mLastLocation);
            }
        }
    }


    public void handlePlaceSnapshot (DataSnapshot dataSnapshot)
    {   if (dataSnapshot.exists() &&
                dataSnapshot.child("latitude").getValue() != null &&
                dataSnapshot.child("longitude").getValue() != null &&
                dataSnapshot.child("icon").getValue() != null &&
                dataSnapshot.child("name").getValue() != null) {
            placeKey = dataSnapshot.getKey();
            String name = (String) dataSnapshot.child("name").getValue();
            double mLastLat = (double) dataSnapshot.child("latitude").getValue();
            double mLastLong = (double) dataSnapshot.child("longitude").getValue();
            String iconType = (String) dataSnapshot.child("icon").getValue();
            Integer icon = PLACES_ICONS.get(iconType);
            LatLng mLastLocation = new LatLng(mLastLat, mLastLong);
            ArrayList nameLocationIcon = new ArrayList<>();
            nameLocationIcon.add(0, name);
            nameLocationIcon.add(1, mLastLocation);
            nameLocationIcon.add(2, icon);
            placesLocations.put(placeKey, nameLocationIcon);
            if (!placeMarker.containsKey(placeKey) && mMap!= null) {
                placeMarker.put(placeKey, mMap.addMarker(new MarkerOptions()
                        .position(mLastLocation)
                        .title(name)
                        .icon(BitmapDescriptorFactory.fromResource(icon))));
            }
        }
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        savedInstanceState.putSerializable(Constants.DETECTED_ACTIVITIES, mDetectedActivities);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private void populateGeofenceList() {
        cListener4 = mDatabase.child("geofences").child(mUID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        if (dataSnapshot.exists() &&
                                dataSnapshot.child("name").getValue() != null &&
                                dataSnapshot.child("latitude").getValue() != null &&
                                dataSnapshot.child("longitude").getValue() != null &&
                                dataSnapshot.child("radius").getValue() != null &&
                                dataSnapshot.child("tribe").getValue() != null) {
                            String placeUID = dataSnapshot.getKey();
                            Log.w("place ",dataSnapshot.toString());
                            String placeName = (String) dataSnapshot.child("name").getValue();
                            double placeLat = (double) dataSnapshot.child("latitude").getValue();
                            double placeLong = (double) dataSnapshot.child("longitude").getValue();
                            Double placeRadiusInterm = (Double) dataSnapshot.child("radius").getValue();
                            float placeRadius = (float) placeRadiusInterm.floatValue();

                            mGeofenceList.add(new Geofence.Builder()
                                    // Set the request ID of the geofence. This is a string to identify this
                                    // geofence.
                                    .setRequestId(placeUID)

                                    // Set the circular region of this geofence.
                                    .setCircularRegion(
                                            placeLat,
                                            placeLong,
                                            placeRadius
                                    )

                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                            Geofence.GEOFENCE_TRANSITION_EXIT)

                                    .build());
                        }


                    }
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    public void onCancelled(DatabaseError firebaseError) {}
                });

        for (Map.Entry<String, LatLng> entry : geofencePlaces.entrySet()) {
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }


    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }

    private void setFab(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastTribeUID !=null)
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                else {
                    Toast.makeText(MainActivity.this, "You can't chat alone! Create or Join a tribe to start chatting!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cListener1!=null)
            mDatabase.child("users").child(mUID).child("tribes").removeEventListener(cListener1);
        if (cListener2!=null)
            mDatabase.child("users").child(mUID).child("tribes").removeEventListener(cListener2);
        if (cListener3!=null)
            mDatabase.child("tribes").removeEventListener(cListener3);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} starts receiving callbacks when
     * activities are detected.
     */
    public void requestActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     */
    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

            // Update the UI. Requesting activity updates enables the Remove Activity Updates
            // button, and removing activity updates enables the Add Activity Updates button.
           /* setButtonsEnabledState();*/

            Toast.makeText(
                    this,
                    getString(requestingUpdates ? R.string.activity_updates_added :
                            R.string.activity_updates_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Ensures that only one button is enabled at any time. The Request Activity Updates button is
     * enabled if the user hasn't yet requested activity updates. The Remove Activity Updates button
     * is enabled if the user has requested activity updates.
     */
/*
    private void setButtonsEnabledState() {
        if (getUpdatesRequestedState()) {
            mRequestActivityUpdatesButton.setEnabled(false);
            mRemoveActivityUpdatesButton.setEnabled(true);
        } else {
            mRequestActivityUpdatesButton.setEnabled(true);
            mRemoveActivityUpdatesButton.setEnabled(false);
        }
    }

*/
    /**
     * Retrieves a SharedPreference object used to store or read values in this app. If a
     * preferences file passed as the first argument to {@link #getSharedPreferences}
     * does not exist, it is created when {@link SharedPreferences.Editor} is used to commit
     * data.
     */
    private SharedPreferences getSharedPreferencesInstance() {
        return getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .apply();
    }

    /**
     * Processes the list of freshly detected activities. Asks the adapter to update its list of
     * DetectedActivities with new {@code DetectedActivity} objects reflecting the latest detected
     * activities.
     */
    protected void updateDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities) {
        mAdapter.updateActivities(detectedActivities);
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            updateDetectedActivitiesList(updatedActivities);
        }
    }

    private Bitmap setUserMarkerBitmap (){
        if (mUser.getPhotoUrl() == null || mUser.getPhotoUrl().toString().indexOf("default") > 0) {
            String[] parts = mUsername.split(" ");
            mInitials = parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0);

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(mUID);
            Log.w(TAG, "Initials: " + mInitials);
            TextDrawable mProfileInitials = TextDrawable.builder()
                    .buildRound(mInitials, color);
            mProfileInitialsView.setImageDrawable(mProfileInitials);
            mProfilePictureView.setVisibility(View.GONE);
            mProfileInitialsView.setVisibility(View.VISIBLE);

            int width = mProfileInitials.getIntrinsicWidth();
            width = width > 0 ? width : 96; // Replaced the 1 by a 96
            int height = mProfileInitials.getIntrinsicHeight();
            height = height > 0 ? height : 96; // Replaced the 1 by a 96

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);
            mProfileInitials.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            mProfileInitials.draw(canvas);
        } else {
            mPhotoUrl = mUser.getPhotoUrl().toString();
            Log.w(TAG, "Picasso was used, photoUrl: " + mPhotoUrl);
            Picasso.with(getApplicationContext()).load(mPhotoUrl).noFade().into(mProfilePictureView);
            mProfileInitialsView.setVisibility(View.GONE);
            mProfilePictureView.setVisibility(View.VISIBLE);

            mTarget = new Target() {
                @Override
                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(output);

                    final int color = 0xff424242;
                    final Paint paint = new Paint();
                    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                    paint.setAntiAlias(true);
                    canvas.drawARGB(0, 0, 0, 0);
                    paint.setColor(color);
                    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
                    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                            bitmap.getWidth() / 2, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, rect, rect, paint);
                    mBitmap = output;
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            };
            Picasso.with(this.getApplicationContext())
                    .load(mPhotoUrl)
                    .into(mTarget);
            Log.w("bitmap"," Am facut bitmapul");
        }
/*
        mDatabase.child("tribes").child(mLastTribeUID).child("members").child(mUID).child("icon").setValue("mBitmap");
*/
        //TODO Set Bitmap
        return mBitmap;
    }
    public void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }


    private Bitmap setMarkerBitmap (String mURL){
        if (mURL == null || mURL.indexOf("default") > 0) {
            String[] parts = mUsername.split(" ");
            mInitials = parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0);

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(mUID);
            Log.w(TAG, "Initials: " + mInitials);
            TextDrawable mProfileInitials = TextDrawable.builder()
                    .buildRound(mInitials, color);

            int width = mProfileInitials.getIntrinsicWidth();
            width = width > 0 ? width : 96; // Replaced the 1 by a 96
            int height = mProfileInitials.getIntrinsicHeight();
            height = height > 0 ? height : 96; // Replaced the 1 by a 96

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);
            mProfileInitials.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            mProfileInitials.draw(canvas);
        } else {
            mTarget = new Target() {
                @Override
                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(output);

                    final int color = 0xff424242;
                    final Paint paint = new Paint();
                    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                    paint.setAntiAlias(true);
                    canvas.drawARGB(0, 0, 0, 0);
                    paint.setColor(color);
                    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
                    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                            bitmap.getWidth() / 2, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, rect, rect, paint);
                    mBitmap = output;
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            };
            Picasso.with(this.getApplicationContext())
                    .load(mURL)
                    .into(mTarget);
        }
        mBitmap = Bitmap.createScaledBitmap(mBitmap, 96, 96, false);
        return mBitmap;
    }
}