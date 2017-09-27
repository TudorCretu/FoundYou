package com.tudorc.foundyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tudorc.foundyou.MainActivity.mDatabase;
import static com.tudorc.foundyou.MainActivity.mLastTribeUID;

public class PlacesActivity extends AppCompatActivity {

    ArrayList<Place> placeList;
    ListView listView;
    private static PlacesListAdapter adapter;
    private Toolbar toolbar;
    private ChildEventListener cListener;
    public static Map<String,Integer> drawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        drawableIds =  new HashMap<String, Integer>();
        drawableIds.put("Home",R.drawable.home);
        drawableIds.put("Work",R.drawable.briefcase);
        drawableIds.put("Gym",R.drawable.arrow);
        drawableIds.put("Restuarant",R.drawable.arrow);
        drawableIds.put("Club",R.drawable.arrow);
        drawableIds.put("Pub",R.drawable.arrow);
        drawableIds.put("School",R.drawable.arrow);
        drawableIds.put("Cafe",R.drawable.arrow);
        drawableIds.put("Gas Station",R.drawable.arrow);
        drawableIds.put("Transport Station",R.drawable.arrow);
        drawableIds.put("Park",R.drawable.arrow);
        drawableIds.put("Grocery Store",R.drawable.arrow);
        drawableIds.put("Default",R.drawable.arrow);

        listView=(ListView)findViewById(R.id.list);
        placeList = new ArrayList<>();

        cListener = mDatabase.child("places").child(mLastTribeUID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        if (dataSnapshot.exists()) {
                            String icon = (String) dataSnapshot.child("icon").getValue();
                            String name = (String) dataSnapshot.child("name").getValue();
                            placeList.add(new Place(icon,name));

                            adapter= new PlacesListAdapter(placeList,getApplicationContext(),drawableIds);
                            listView.setAdapter(adapter);
                        }
                    }
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    public void onCancelled(DatabaseError firebaseError) {}
                });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Place place= placeList.get(position);

                //TODO make place's activity
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_overflow) {
            startActivity(new Intent(PlacesActivity.this,AddPlaceActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}