package com.tudorc.foundyou;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

import static com.tudorc.foundyou.MainActivity.mLastTribeUID;

public class CreateTribeActivity extends AppCompatActivity {

    private String mUID;
    private AutoCompleteTextView tribeNameView;
    private Button doneButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tribe);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SUGGESTEDNAMES);
        tribeNameView = (AutoCompleteTextView)
                findViewById(R.id.create_tribe_et);
        tribeNameView.setMaxLines(1);
        tribeNameView.setInputType(InputType.TYPE_CLASS_TEXT);
        tribeNameView.setAdapter(adapter);

        imm.showSoftInput(tribeNameView, InputMethodManager.SHOW_IMPLICIT);

        doneButton = (Button) findViewById(R.id.create_tribe_done);
        doneButton.setOnClickListener ( new Button.OnClickListener()
        {
            @Override
            public void onClick ( View create_tribe_done )
            {
                String tribeName = (String) tribeNameView.getText().toString();
                if (tribeName.matches("")) {
                    Toast.makeText(CreateTribeActivity.this, "You did not enter the name of your tribe", Toast.LENGTH_SHORT).show();
                } else {
                    // create token for tribe

                    mUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mLastTribeUID = (String) UUID.randomUUID().toString();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("tribes").child(mLastTribeUID).child("UID").setValue(mLastTribeUID);
                    mDatabase.child("tribes").child(mLastTribeUID).child("name").setValue(tribeName);
                    mDatabase.child("tribes").child(mLastTribeUID).child("timestamp").setValue(new Date().getTime());
                    mDatabase.child("tribes").child(mLastTribeUID).child("members").child(mUID).setValue(true);
                    mDatabase.child("tribes").child(mLastTribeUID).child("inviteCode").setValue("a");
                    mDatabase.child("tribes").child(mLastTribeUID).child("validUntil").setValue(1);
                    mDatabase.child("users").child(mUID).child("tribes").child(mLastTribeUID).child("name").setValue(tribeName);
                    mDatabase.child("users").child(mUID).child("lastTribe").setValue(mLastTribeUID);

                    Intent intent = new Intent(CreateTribeActivity.this,InviteActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tribeNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doneButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private static final String[] SUGGESTEDNAMES = new String[] {
            "Family", "Friends", "Siblings", "Extended Familiy", "Special Someone", "Significant Other", "Field Trip Group", "Carpool",
            "Vacation Group", "Babysitter", "Coworkers", "Lunch Friends"
    };
}