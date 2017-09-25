package com.tudorc.foundyou;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap;
import static com.tudorc.foundyou.MainActivity.mDatabase;
import static com.tudorc.foundyou.MainActivity.mLastTribeUID;
import static com.tudorc.foundyou.MainActivity.mUserMarker;
import static com.tudorc.foundyou.MainActivity.members;

public class AddPlaceActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView placeAddress;
    private EditText placeName;
    private Marker mCurrentLocationMarker;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private Spinner spinner;
    private String[] textArray = {
            "Default", //1
            "Home", //2
            "Work", //3
            "Grocery Store", //4
            "Gym", //5
            "Restaurant", //6
            "Club", //7
            "Pub", //8
            "Cafe", //9
            "School", //10
            "Park", //11
            "Gas Station", //12
            "Transport Station"}; //13
    private Integer[] imageArray = {
            R.drawable.arrow, //1
            R.drawable.arrow, //2
            R.drawable.arrow, //3
            R.drawable.arrow, //4
            R.drawable.arrow, //5
            R.drawable.arrow, //6
            R.drawable.arrow, //7
            R.drawable.arrow, //8
            R.drawable.arrow, //9
            R.drawable.arrow, //10
            R.drawable.arrow, //11
            R.drawable.arrow, //12
            R.drawable.arrow};//13

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placeName = (EditText) findViewById(R.id.placeName);
        placeAddress = (TextView) findViewById(R.id.placeAddress);


        placeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });

        TextView placeType = (TextView) findViewById(R.id.placeType);
        ImageView placeIcon =(ImageView)findViewById(R.id.placeIcon);
        final ImageView centerMarker = (ImageView) findViewById(R.id.center_marker);
        spinner = (Spinner) findViewById(R.id.icon_spinner);

        PlaceIconSpinnerAdapter adapter = new PlaceIconSpinnerAdapter(this, R.layout.place_icon_spinner, textArray, imageArray);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                centerMarker.setImageResource(imageArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_custom_overflow, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                try {
                    Geocoder geo = new Geocoder(AddPlaceActivity.this.getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
                    if (addresses.isEmpty()) {
                        placeAddress.setText("Getting Location...");
                    }
                    else {
                        if (addresses.size() > 0) {
                            placeAddress.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception e) {
                    placeAddress.setText("Unknown Location");
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }
            }
        });

        mCurrentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(mUserMarker.getPosition())
                .icon(vectorToBitmap(R.drawable.fireplace, Color.parseColor("#A4C639"))));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mUserMarker.getPosition(), 17));
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



    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);

                placeAddress.setText(place.getAddress());
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17);
                mMap.animateCamera(location);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {

        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_overflow) {
            String name = placeName.getText().toString();
            Double latitude = mMap.getCameraPosition().target.latitude;
            Double longitude = mMap.getCameraPosition().target.longitude;
            String iconType = (String) spinner.getSelectedItem().toString();
            String placeUID = (String) UUID.randomUUID().toString();
            Float radius =1500f;
            mDatabase.child("places").child(mLastTribeUID).child(placeUID).child("name").setValue(name);
            mDatabase.child("places").child(mLastTribeUID).child(placeUID).child("latitude").setValue(latitude);
            mDatabase.child("places").child(mLastTribeUID).child(placeUID).child("longitude").setValue(longitude);
            mDatabase.child("places").child(mLastTribeUID).child(placeUID).child("radius").setValue(radius);
            mDatabase.child("places").child(mLastTribeUID).child(placeUID).child("icon").setValue(iconType);


            mDatabase.child("tribes").child(mLastTribeUID).child("places").child(placeUID).child("name").setValue(name);
            mDatabase.child("tribes").child(mLastTribeUID).child("places").child(placeUID).child("latitude").setValue(latitude);
            mDatabase.child("tribes").child(mLastTribeUID).child("places").child(placeUID).child("longitude").setValue(longitude);
            mDatabase.child("tribes").child(mLastTribeUID).child("places").child(placeUID).child("radius").setValue(radius);
            mDatabase.child("tribes").child(mLastTribeUID).child("places").child(placeUID).child("icon").setValue(iconType);
            if (members != null){
                for (String member: members)
                {
                    mDatabase.child("geofences").child(member).child(placeUID).child("name").setValue(name);
                    mDatabase.child("geofences").child(member).child(placeUID).child("latitude").setValue(latitude);
                    mDatabase.child("geofences").child(member).child(placeUID).child("longitude").setValue(longitude);
                    mDatabase.child("geofences").child(member).child(placeUID).child("radius").setValue(radius);
                    mDatabase.child("geofences").child(member).child(placeUID).child("tribe").setValue(mLastTribeUID);
                }
            }
            startActivity(new Intent(AddPlaceActivity.this,PlacesActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}

