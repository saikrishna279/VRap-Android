package com.thanoscorp.athene.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thanoscorp.athene.R;
import com.thanoscorp.athene.interfaces.SMSListener;
import com.thanoscorp.athene.interfaces.TextChangeListener;
import com.thanoscorp.athene.models.ActivityX;
import com.thanoscorp.athene.models.BndrsntchTimer;
import com.thanoscorp.athene.models.EditTextX;
import com.thanoscorp.athene.models.StrokedTextView;
import com.thanoscorp.athene.receivers.SMSBroadcastReceiver;
import com.thanoscorp.athene.utility.User;
import com.thanoscorp.athene.utility.scraper.CMS;
import com.thanoscorp.athene.utility.scraper.CMSConstants;
import com.thanoscorp.athene.utility.scraper.CMSCore;
import com.thanoscorp.athene.utility.sms.SMSProcessor;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 *
 * Authentication Activity
 *
 * Read README for details
 *
 */
public class AuthActivity extends ActivityX implements View.OnClickListener, SMSListener {

    //simple objects
    private boolean isRegistrationReset = true;
    private String id, pass, cmspass;

    //complex objects

    private final static int MAGIC_CONSTANT = 69420; // Pray to god that no one else uses this lol

    private EditTextX idET, passET, phoneET;
    private LinearLayout confirmationLayout;
    private FrameLayout insertInto;
    private StrokedTextView welcomeET;
    private BndrsntchTimer timerBar;
    private StrokedTextView confirmationNo;
    private Button signup, signIn;

    FirebaseDatabase fbdatabase = FirebaseDatabase.getInstance();
    DatabaseReference database = fbdatabase.getReference();

    FirebaseAuth authenticator = FirebaseAuth.getInstance();
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        authenticator = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);

        CMSCore.initialize(getApplicationContext());

        user = authenticator.getCurrentUser();
        if (user != null) {
            if (!CMS.isStatus(CMS.Status.LOGGED_IN)) {
                id = User.createIDfromUID(user.getEmail());
                Log.d("CMS", "user present" + id);
                database.child(CMSConstants.DB_CMSPASS_CHILD_FIELD).child(id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                cmspass = dataSnapshot.getValue(String.class);
                                Log.d("CMS", "user present" + cmspass);
                                if (cmspass == null) uiSetup();
                                else collectUserDetails(cmspass);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        } else uiSetup();
    }

    private void uiSetup() {
        idET = findViewById(R.id.idET);
        idET.setVisibility(VISIBLE);
        InputFilter[] editFilters = idET.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        idET.setFilters(newFilters); //set CAPS only
        idET.setOnTextChangeListener(new TextChangeListener() {
            @Override
            public void onTextChange(CharSequence s) {
                if (s.length() == 10) {
                    id = s.toString();
                    database.child(CMSConstants.DB_DETAILS_CHILD_FIELD).child(id).child(CMSConstants.DB_DETAILS_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String result = dataSnapshot.getValue(String.class);
                            if (result == null) {
                                startRegistration();
                            } else {
                                welcomeET.setText(getString(R.string.auth_activity_welcome_name, result));
                                Toast.makeText(getApplicationContext(), dataSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                                startSignIn();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    resetRegistration();
                }
            }
        });
        passET = findViewById(R.id.passET);
        welcomeET = findViewById(R.id.welcome);
        phoneET = findViewById(R.id.phoneET);
        timerBar = findViewById(R.id.timerBar);
        signup = findViewById(R.id.signup);
        signIn = findViewById(R.id.signin);
        getLifecycle().addObserver(timerBar.getLifecycleObserver());
        confirmationLayout = findViewById(R.id.confirmationLayout);
        confirmationNo = findViewById(R.id.confirmNo);
        setUpOTPListener();

    }

    private void startSignIn() {
        passET.setVisibility(VISIBLE);
        signIn.setVisibility(VISIBLE);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = passET.getText().toString();
                if (pass.length() >= 8) {
                    authenticator.signInWithEmailAndPassword(User.createUIDfromID(id), pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Logged IN", Toast.LENGTH_SHORT).show();
                                        database.child(CMSConstants.DB_CMSPASS_CHILD_FIELD).child(id)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        cmspass = dataSnapshot.getValue(String.class);
                                                        if (cmspass != null) CMSCore.login(id, cmspass);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to login: ", Toast.LENGTH_SHORT).show();
                                        Log.w("ATHENE", task.getException());
                                    }
                                }
                            });
                }
            }
        });

    }

    private void resetRegistration() {
        if (!isRegistrationReset) {
            isRegistrationReset = true;
            welcomeET.setText(R.string.auth_activity_welcome);
            phoneET.setText("");
            phoneET.setVisibility(GONE);
            passET.setVisibility(GONE);
            signIn.setVisibility(GONE);
            signup.setVisibility(GONE);
            phoneET.removeAllOnTextChangeListeners();
            timerBar.reset(true);
            confirmationLayout.setVisibility(GONE);
        }
    }

    private void startRegistration() {
        if (isRegistrationReset) {
            isRegistrationReset = false;
            welcomeET.setText(getText(R.string.auth_new_user_string));
            phoneET.setVisibility(VISIBLE);
            passET.setVisibility(GONE);
            signIn.setVisibility(GONE);
            signup.setVisibility(GONE);
            phoneET.setOnTextChangeListener(new TextChangeListener() {
                @Override
                public void onTextChange(CharSequence sequence) {
                    if (sequence.length() == 10) {
                        confirmationLayout.setVisibility(VISIBLE);
                        confirmationLayout.setAlpha(1.0f);
                        timerBar.start(5000, new BndrsntchTimer.OnTimeUpListener() {
                            @Override
                            public void onTimeUp() {
                                String number = phoneET.getText().toString();
                                if (number.length() == 10) {
                                    timerBar.reset(true);
                                    registerStart(id, phoneET.getText().toString());
                                }
                            }
                        });
                        confirmationNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timerBar.reset(false);
                                //confirmationLayout.setVisibility(GONE);
                            }
                        });

                    }
                }
            });
            timerBar.setOnResetFinishedListener(new BndrsntchTimer.onResetListener() {
                @Override
                public void onResetFinished() {
                    confirmationLayout.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            confirmationLayout.setVisibility(GONE);
                        }
                    }).setDuration(300).start();
                }
            });
        }
    }

    private void hideUIElements(){
        if(welcomeET!=null) welcomeET.setVisibility(GONE);
        if(phoneET!=null) phoneET.setVisibility(GONE);
        if(passET!=null) passET.setVisibility(GONE);
        if(signIn!=null) signIn.setVisibility(GONE);
        if(signup!=null) signup.setVisibility(GONE);
        if(confirmationLayout!=null) confirmationLayout.setVisibility(GONE);
    }

    public void registerStart(String id, String number) {
        CMSCore.register(id, number);
    }

    public void setUpOTPListener() {
        SMSBroadcastReceiver.bindListener(this);
    }

    private void initiateCloudSignUp(final String string) {
        phoneET.setVisibility(GONE);
        passET.setVisibility(VISIBLE);
        signup.setVisibility(VISIBLE);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passET.getText().toString();
                if (password.length() >= 8) {
                    Log.d("ATHENE", "id: " + User.createUIDfromID(id));
                    authenticator.createUserWithEmailAndPassword(User.createUIDfromID(id), password)
                            .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Sign up successful!", Toast.LENGTH_SHORT).show();
                                        database.child(CMSConstants.DB_CMSPASS_CHILD_FIELD).child(id).setValue(string);
                                        collectUserDetails(string);
                                    } else {
                                        Log.w("AUTH", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Sign up failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void collectUserDetails(String string) {
        hideUIElements();
        Log.d("CMS", "AuthActivity:collectUserDetails()");
        CMSCore.getStudentDetails(database, id, string);
        Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
        startActivity(intent);
        CMSCore.login(id, string);

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onSMSAvailable(SmsMessage message) {
        String origin = message.getDisplayOriginatingAddress();
        String content = message.getMessageBody();
        if (SMSProcessor.isFromVRSEC(origin)) {
            String processed;
            if (SMSProcessor.isMessageOTP(content)) {
                processed = SMSProcessor.processOTP(content);
                CMSCore.submitOTP(processed);
            } else {
                processed = SMSProcessor.processPASS(content);
                initiateCloudSignUp(processed);
            }
            welcomeET.setText(SMSProcessor.processOTP(content));

        }
    }
}
