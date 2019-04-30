package com.sudjunham.boonyapon;

import android.annotation.SuppressLint;
import android.app.Activity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Bundle;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.parceler.Parcels;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MainActivity extends AppCompatActivity implements RecyclerViewItemClickListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private long mLastClickTime = 0;
    List<Event_list> event_List_Arr = new ArrayList<Event_list>();
    ScrollView scrollView;
     RecyclerView recyclerView;
     RecyclerViewAdapter adapter;
     LinearLayoutManager manager;
    ImageView img_filter,img_user;
    Boolean checkedTAG = false,checkedLocation = false;
    LinearLayout seach_bar;
    TextView tv_result_filter;
    SwipeRefreshLayout pullToRefresh;
    String urlVal , getName = null;
    ProgressBar progressBar;
    RadioButton rb_kku,rb_else;
    RadioGroup rg_main;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RetrieveFeedTask().execute();
                pullToRefresh.setRefreshing(false);
            }
        });

        scrollView = findViewById(R.id.scrollView_main);
        img_filter = findViewById(R.id.img_filter);
        seach_bar = findViewById(R.id.seach_bar);
        img_user = findViewById(R.id.img_user);
        tv_result_filter = findViewById(R.id.tv_result_filter);
        progressBar = findViewById(R.id.progress_bar_main);
        rb_kku = findViewById(R.id.rb_event_kku_main);
        rb_else = findViewById(R.id.rb_event_else_main);
        rg_main = findViewById(R.id.rg_main);

        rg_main.setOnCheckedChangeListener(this);

        tv_result_filter.setVisibility(View.INVISIBLE);
        seach_bar.setOnClickListener(this);
        img_user.setOnClickListener(this);
        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignature = new Intent(getApplicationContext(),FilterActivity.class);
                startActivityForResult(intentSignature,1000);
            }
        });

        scrollView.smoothScrollTo(0,0);
        recyclerView = findViewById(R.id.list_view1);
        recyclerView.setNestedScrollingEnabled(false);
        new RetrieveFeedTask().execute();

        manager = new LinearLayoutManager(this) ;
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerViewAdapter(MainActivity.this,event_List_Arr);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        setImgUser();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
      switch (checkedId){
          case R.id.rb_event_kku_main:
              tv_result_filter.setVisibility(View.INVISIBLE);
              recyclerView.setVisibility(View.VISIBLE);
              new RetrieveFeedTask().execute();
              break;
          case R.id.rb_event_else_main:
              tv_result_filter.setVisibility(View.VISIBLE);
              recyclerView.setVisibility(View.GONE);

      }
    }

    // Double Back to exit
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.DoubleBack), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        // display progressbar while loading
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @SuppressLint("WrongThread")
        protected String doInBackground(Void... urls) {

            try {
                urlVal = "https://www.kku.ac.th/ikku/api/activities/services/topActivity.php";
                URL urlAddr;
                urlAddr = new URL(urlVal);
                HttpURLConnection urlConnection = (HttpURLConnection) urlAddr.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected void onPostExecute(String response) {
            super.onPreExecute();

            try {
                if(response == null) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.customdialog);
                    dialog.setCancelable(false);

                    Button button1 = dialog.findViewById(R.id.button_dialog);
                    button1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.cancel();
                            finish();
                            System.exit(0);
                        }
                    });
                    dialog.show();
                }else {

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray jsonArray = object.getJSONArray("activities");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject activity_event = jsonArray.getJSONObject(i);
                        Event_list event_list = new Event_list();

                        String pDateST = activity_event.getString("dateSt");
                        String pDateED = activity_event.getString("dateEd");
                        String pTimeST = activity_event.getString("timeSt");
                        String pTimeED = activity_event.getString("timeEd");

                        String phoneDEL = activity_event.getJSONObject("contact").getString("phone");
                        phoneDEL = phoneDEL.replaceAll(" ", "");
                        phoneDEL = phoneDEL.replaceAll("-", "");
                        if (phoneDEL.length() > 10) {
                            phoneDEL = phoneDEL.substring(0, 9);
                        }

                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(Calendar.getInstance().getTime());
                        LocalDate currentDate = LocalDate.parse(timeStamp, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        LocalDate getDateEvent = LocalDate.parse(pDateST, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                        event_list.setName(activity_event.getString("title").replaceAll("&quot;", "\""));
                        event_list.setDate((pDateST.equals(pDateED))
                                ? (dateThai(pDateST, null, pTimeST, pTimeED))
                                : (dateThai(pDateST, pDateED, pTimeST, pTimeED)));
                        event_list.setLocation(activity_event.getString("place"));
                        event_list.setContent(activity_event.getString("content").replaceAll("&quot;", "\""));
                        event_list.setImglink(activity_event.getString("image"));
                        event_list.setSponsor(activity_event.getString("sponsor"));
                        event_list.setPhonecontact(phoneDEL);
                        event_list.setWebsite(activity_event.getJSONObject("contact").getString("website"));
                        event_list.setmonthForFilter(getDateEvent.getMonthValue());
                        event_list.setDateTimeST(parseDateTime(pDateST, pTimeST));
                        event_list.setDateTimeED(parseDateTime(pDateED, pTimeED));

                        event_List_Arr.add(event_list);
                        adapter.notifyDataSetChanged();

                        getName += event_List_Arr.get(i).name + "\n";

                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setImgUser();

        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK) {
                List<Event_list> event_List_Arr_stack = new ArrayList<>();
                for(int i = 0 ; i < event_List_Arr.size() ; i++){
                    checkedTAG = false;
                    checkedLocation = false;
                    if(FilteHelper.getInstance().getMinMonth() > event_List_Arr.get(i).getmonthForFilter() || FilteHelper.getInstance().getMaxMonth() < event_List_Arr.get(i).getmonthForFilter()){ }
                    else{
                        if(event_List_Arr.get(i).getLocation().contains("จังหวัด") && FilteHelper.getInstance().isCb_outsude()){
                            checkedLocation = true;
                        }
                        else if(!event_List_Arr.get(i).getLocation().contains("จังหวัด") && FilteHelper.getInstance().isCb_ui()){
                            checkedLocation = true;
                        }
                        else if(!FilteHelper.getInstance().isCb_ui() && !FilteHelper.getInstance().isCb_outsude()) {
                            checkedLocation = true;
                        }
                        if(checkedLocation){
                            event_List_Arr_stack.add(event_List_Arr.get(i));
                        }

                    }
                }
                if(event_List_Arr_stack.size()== 0){
                    tv_result_filter.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else{
                    tv_result_filter.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter = new RecyclerViewAdapter(MainActivity.this,event_List_Arr_stack);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(this);
            }
        }
    }

    public static String dateThai(String strDate,String endDate,String strtime , String timeED)throws ParseException
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat DateFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
        SimpleDateFormat DateFormatWYear = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

        Date dateST = df.parse(strDate);

        DateFormat parser = new SimpleDateFormat("a. HH.mm",Locale.ENGLISH);
        SimpleDateFormat formatter = new SimpleDateFormat("HH.mm",Locale.ENGLISH);
        Date dtimeST = parser.parse(strtime);
        Date dtimeEd = parser.parse(timeED);
        strtime = formatter.format(dtimeST);
        timeED = formatter.format(dtimeEd);

        //same date
        if(endDate == null) {
            strDate = DateFormatWYear.format(dateST);
            return String.format("%s เวลา %s - %s น.", strDate,strtime,timeED);
        }
        //muti date
        else{
            Date dateED = df.parse(endDate);
            strDate = DateFormat.format(dateST);
            endDate = DateFormat.format(dateED);

            return String.format("%s %s น. - %s %s น.", strDate, strtime,endDate,timeED);
        }
    }

    public String parseDateTime(String date , String time)throws ParseException{
        DateFormat parserTime = new SimpleDateFormat("a. HH.mm", Locale.ENGLISH);
        Date timeP = parserTime.parse(time);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = formatter.format(timeP);
       return date + "T" + formattedTime;

    }

    private void Toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();}


    @Override
    public void onItemClick(View view, int position) {
        Log.d("Check","in position " + adapter.getItem(position));
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(MainActivity.this , InfoEventActivity.class);
        Parcelable parcelable = Parcels.wrap(adapter.getItem(position));
        intent.putExtra("objEvent",parcelable);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seach_bar:
                Intent intent = new Intent(MainActivity.this, SeachActivity.class);
                Parcelable parcelable = Parcels.wrap(adapter.getEvent_lists());
                intent.putExtra("listEvent", parcelable);
                startActivity(intent);
                break;
            case R.id.img_user:
                GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                if (googleSignInAccount != null) {
                    Intent userPage = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(userPage);
                } else {
                    Intent userPage = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(userPage);
                }
        }
    }

    private void setImgUser() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            Picasso.with(this)
                    .load(googleSignInAccount.getPhotoUrl().toString())
                    .into(img_user);
        } else {
            Picasso.with(this)
                    .load(R.drawable.userr)
                    .into(img_user);
        }
    }
}
