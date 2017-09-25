package com.tudorc.foundyou;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.tudorc.foundyou.MainActivity.mDatabase;
import static com.tudorc.foundyou.MainActivity.mLastTribeUID;

import java.util.UUID;

public class JoinTribeActivity extends AppCompatActivity {

    private EditText code1view;
    private EditText code2view;
    private EditText code3view;
    private EditText code4view;
    private EditText code5view;
    private EditText code6view;
    private String mInviteCode;
    private Button submitButton;
    private String mUID;
    private DatabaseReference mDatabase;
    public ChildEventListener cListener;
    public FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_tribe);

        code1view = (EditText) findViewById(R.id.code1);
        code1view.requestFocus();
        code1view.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                code2view.requestFocus();
            }
        });
        code2view = (EditText) findViewById(R.id.code2);
        code2view.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                code3view.requestFocus();
            }
        });
        code3view = (EditText) findViewById(R.id.code3);
        code3view.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                code4view.requestFocus();
            }
        });
        code4view = (EditText) findViewById(R.id.code4);
        code4view.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                code5view.requestFocus();
            }
        });
        code5view = (EditText) findViewById(R.id.code5);
        code5view.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                code6view.requestFocus();
            }
        });
        code6view = (EditText) findViewById(R.id.code6);
        code6view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    submitButton.performClick();
                }
                return false;
            }
        });

        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View submit_button) {
                mInviteCode = code1view.getText().toString() +
                        code2view.getText().toString() +
                        code3view.getText().toString() +
                        code4view.getText().toString() +
                        code5view.getText().toString() +
                        code6view.getText().toString();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Log.w("Invite", "This is the invite code you inputed: " + mInviteCode);
                cListener = mDatabase.child("tribes").orderByChild("inviteCode").equalTo(mInviteCode)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                if (dataSnapshot.exists() && dataSnapshot.getKey() != null){
                                    Log.w("tribeUID", "tribeUID is this one: " + dataSnapshot);
                                    mLastTribeUID = dataSnapshot.getKey();
                                    mUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    if (dataSnapshot.child(mLastTribeUID).child("members").hasChild(mUID))
                                        Toast.makeText(JoinTribeActivity.this, "You are already a member of this tribe.", Toast.LENGTH_SHORT).show();
                                    Log.w("tribeUID", "tribeUID is this one: " + mLastTribeUID);
                                    mDatabase.child("tribes").child(mLastTribeUID).child("members").child(mUID).child("displayName").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                    mDatabase.child("users").child(mUID).child("tribes").child(mLastTribeUID).child("name").setValue(dataSnapshot.child("name").getValue());
                                    mDatabase.child("users").child(mUID).child("lastTribe").setValue(mLastTribeUID);
                                    for (DataSnapshot postSnapshot: dataSnapshot.child("tribes").child(mLastTribeUID).child("places").getChildren())
                                    {
                                        String placeUID = (String) postSnapshot.getKey();
                                        String placeName = (String) postSnapshot.child("name").getValue();
                                        double placeLat = (double) postSnapshot.child("latitude").getValue();
                                        double placeLong = (double) postSnapshot.child("longitude").getValue();
                                        float placeRadius = (float) postSnapshot.child("radius").getValue();

                                        mDatabase.child("geofences").child(mUID).child(placeUID).child("name").setValue(placeName);
                                        mDatabase.child("geofences").child(mUID).child(placeUID).child("latitude").setValue(placeLat);
                                        mDatabase.child("geofences").child(mUID).child(placeUID).child("longitude").setValue(placeLong);
                                        mDatabase.child("geofences").child(mUID).child(placeUID).child("radius").setValue(placeRadius);
                                    }
                                    Intent intent = new Intent(JoinTribeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.w("TOAST", "This toast just won't burn at all");
                                    Toast.makeText(JoinTribeActivity.this, "Your code didn't match any tribe. Please try again!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                            public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            public void onCancelled(DatabaseError firebaseError) {}
                        });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cListener != null)
            mDatabase.child("tribes").removeEventListener(cListener);
    }
}