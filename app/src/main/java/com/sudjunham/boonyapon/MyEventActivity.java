package com.sudjunham.boonyapon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;


public class MyEventActivity extends AppCompatActivity {

    Button bt_add_event;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);
        email = getIntent().getExtras().getString("userEmail");

        // swipe to go back
        Slidr.attach(this);

        bt_add_event = findViewById(R.id.bt_add_event);

        bt_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyEventActivity.this , CreateEvent.class );
                intent.putExtra("userEmail", email);
                startActivity(intent);
            }
        });
    }
}
