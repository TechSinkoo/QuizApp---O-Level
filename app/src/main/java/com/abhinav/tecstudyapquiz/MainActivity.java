package com.abhinav.tecstudyapquiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
//#5DC3FF
    private Button startBtn,bookmarkBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextView visitWebsite;

    private static final String ONESIGNAL_APP_ID = "7f877482-8a46-4d2e-92bb-7569f12691e2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        bookmarkBtn = findViewById(R.id.bookmarks_btn);

        visitWebsite = findViewById(R.id.visitwebsite);



        startBtn = findViewById(R.id.start_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent = new Intent(MainActivity.this,CategoriesActivity.class);
                startActivity(categoryIntent);

            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookmarksIntent = new Intent(MainActivity.this,BookmarkActivity.class);
                startActivity(bookmarksIntent);

            }
        });



        visitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tecstudyap.blogspot.com/"));
                startActivity(intent);
            }
        });


        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }




//    private void sendToLoginPage()
//    {
//        Intent loginPage = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(loginPage);
//        finish();
//    }
}