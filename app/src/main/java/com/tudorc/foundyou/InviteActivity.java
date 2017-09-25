package com.tudorc.foundyou;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.tudorc.foundyou.MainActivity.mDatabase;
import static com.tudorc.foundyou.MainActivity.mLastTribeUID;

public class InviteActivity extends AppCompatActivity {
    private Long mValidUntil;
    public String mInviteCode;
    private TextView inviteCodeView;
    private TextView validUntilView;
    private Button reGenButton;
    private Button doneButton;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        inviteCodeView = (TextView) findViewById(R.id.invite_code);;
        validUntilView = (TextView) findViewById(R.id.valid_until);

        mDatabase.child("tribes").child(mLastTribeUID).child("validUntil").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mValidUntil = (Long) (snapshot.getValue());
                if (mValidUntil > new Date().getTime()) {
                    mDatabase.child("tribes").child(mLastTribeUID).child("inviteCode").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mInviteCode = (String) (snapshot.getValue());
                            if (mInviteCode.length()==6) {
                                inviteCodeView.setText(mInviteCode.substring(0, 2)+"-"+mInviteCode.substring(2, 4)+"-"+mInviteCode.substring(4, 6));
                                validUntilView.setText(getString(R.string.valid_until,(Double.toString((mValidUntil-new Date().getTime())/86400000 + 1) + " more")));
                            }
                            else regenerateInviteCode();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("Database","There was an error of retrieving data of the inviteCode");
                        }
                    });
                }
                else {
                    regenerateInviteCode();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            Log.w("Database","There was an error of retrieving data of the vaildUntil");
            }
        });


        reGenButton = (Button) findViewById(R.id.regen_button);
        reGenButton.setOnClickListener ( new Button.OnClickListener() {
            @Override
            public void onClick(View regen_button) {
                regenerateInviteCode();
            }
        });
        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener ( new Button.OnClickListener() {
            @Override
            public void onClick(View regen_button) {
                Intent intent = new Intent(InviteActivity.this,MainActivity.class);
                intent.putExtra("mLastTribeUID",mLastTribeUID);
                startActivity(intent);
                finish();
            }
        });
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener ( new Button.OnClickListener() {
            @Override
            public void onClick(View regen_button) {
                mDatabase.child("tribes").child(mLastTribeUID).child("inviteCode").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        mInviteCode = (String) (snapshot.getValue());
                        if (mInviteCode.length()==6) {
                            String inviteCode = (String) mInviteCode.substring(0, 2)+"-"+mInviteCode.substring(2, 4)+"-"+mInviteCode.substring(4, 6);
                            String inviteText = (String) "I FoundYou! Join my FoundYou Tribe! Use my invite code: " + inviteCode + ". Download the app here: https://www.facebook.com/FoundYouApp";
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, inviteText);
                            sendIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent, "Share Code using:"));
                        }
                        else {
                            Log.w("TAG","It needed to regenerate the invitecode to send it");
                            regenerateInviteCode();
                            sendButton.performClick();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Database","There was an error of retrieving data of the inviteCode");
                    }
                });

            }
        });
    }

    private void regenerateInviteCode(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        String newInviteCode = sb.toString();
        Long validUntil = new Date().getTime() + 604800000;
        mDatabase.child("tribes").child(mLastTribeUID).child("inviteCode").setValue(newInviteCode);
        mDatabase.child("tribes").child(mLastTribeUID).child("validUntil").setValue(validUntil);
        inviteCodeView.setText(newInviteCode.substring(0, 2)+"-"+newInviteCode.substring(2, 4)+"-"+newInviteCode.substring(4, 6));
        validUntilView.setText(getString(R.string.valid_until,"7"));
    }
}