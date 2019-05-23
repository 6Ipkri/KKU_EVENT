package com.sudjunham.boonyapon;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;

public class UserActivity extends AppCompatActivity implements RecyclerViewItemClickListener, RadioGroup.OnCheckedChangeListener {

    private int langv = 0;

    ImageView img_calendar,signOut;
    private GoogleSignInClient googleSignInClient;
    private TextView profileName, profileEmail;
    private ImageView profileImage;
    private TextView myEvent;
    TextView tv_num_join,tv_num_fev;
    RecyclerView recyclerView;
    RecyclerViewAdapterUser adapter;
    LinearLayoutManager manager;
    List<Event_list> event_kku = new ArrayList<>();
    List<Event_list> event_user = new ArrayList<>();
    List<Event_list> event_all = new ArrayList<>();
    List<Event_list> upComing = new ArrayList<>();
    List<Event_list> fevEvent = new ArrayList<>();
    RadioGroup rd_user;
    ScrollView scrollView;
    ProgressBar progressBar;
    User user;
    List<String> likedList;

    com.google.api.services.calendar.Calendar mService;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    GoogleAccountCredential credentialCaledndar;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // swipe to go back
        Slidr.attach(this);

        final GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        profileName = findViewById(R.id.tv_userName);
        profileEmail = findViewById(R.id.tv_Email);
        profileImage = findViewById(R.id.profile_image);
        signOut = findViewById(R.id.btn_signout);
        myEvent = findViewById(R.id.my_event);
        tv_num_join = findViewById(R.id.tv_num_join);
        tv_num_fev = findViewById(R.id.tv_num_fev);
        rd_user = findViewById(R.id.rg_user);
        scrollView = findViewById(R.id.scrollView_user);
        progressBar = findViewById(R.id.progress_bar_user);

        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

        scrollView.smoothScrollTo(0,0);

        event_kku = Event_all.getInstance().getEventLists();
        event_user = Event_all.getInstance().getEventUser();

        rd_user.setOnCheckedChangeListener(this);

        credentialCaledndar = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(googleSignInAccount.getEmail());

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credentialCaledndar)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        new ApiAsyncTaskForUser(UserActivity.this).execute();

        recyclerView = findViewById(R.id.list_view_user);
        recyclerView.setNestedScrollingEnabled(false);
        manager = new LinearLayoutManager(this) ;
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewAdapterUser(UserActivity.this,fevEvent);

        readliked();
        setAdapterFunc(fevEvent);

        myEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this,MyEventActivity.class);
                intent.putExtra("userEmail", googleSignInAccount.getEmail());
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        FirebaseAuth.getInstance().signOut();
                    }
                });
            }
        });

        Picasso.with(this)
                .load(googleSignInAccount.getPhotoUrl().toString())
                .into(profileImage);
        profileName.setText(googleSignInAccount.getDisplayName());
        profileEmail.setText(googleSignInAccount.getEmail());

        img_calendar = findViewById(R.id.img_calendar);
        img_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, 0);
                long time = cal.getTime().getTime();
                Uri.Builder builder =
                        CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                builder.appendPath(Long.toString(time));
                Intent intent =
                        new Intent(Intent.ACTION_VIEW, builder.build());
                startActivity(intent);
            }
        });
    }

    public void setAdapterFunc(List<Event_list> list){
        adapter = new RecyclerViewAdapterUser(UserActivity.this,list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(UserActivity.this , InfoEventActivity.class);
        Parcelable parcelable = Parcels.wrap(adapter.getItem(position));
        intent.putExtra("objEvent",parcelable);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_user_fev:
                setAdapterFunc(fevEvent);
                break;
            case R.id.rb_user_upcome:
                setAdapterFunc(upComing);
                recyclerView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void readliked(){
        user = User.getInstance();
        String getTitleFirebase = user.title;
        if(user != null && getTitleFirebase != null) {
            likedList = Arrays.asList(getTitleFirebase.split(","));
            int countFev = (likedList.toString().equals("[]"))? 0 : likedList.size();
                tv_num_fev.setText(Integer.toString(countFev));

            Log.d("TAG123" , Event_all.getInstance().getEventUser().toString());

                for (int i = 0 ; i < likedList.size() ; i++){
                    for (int j = 0; j < event_kku.size(); j++) {
                        if (likedList.get(i).equals(event_kku.get(j).name)) {
                            fevEvent.add(event_kku.get(j));
                            adapter.notifyDataSetChanged();
                        }

                    }

                }
            for (int i = 0 ; i < likedList.size() ; i++){
                for (int j = 0; j < event_user.size(); j++) {
                    if (likedList.get(i).equals(event_user.get(j).name)) {
                        fevEvent.add(event_user.get(j));
                        adapter.notifyDataSetChanged();
                    }

                }

            }

        }
    }
}