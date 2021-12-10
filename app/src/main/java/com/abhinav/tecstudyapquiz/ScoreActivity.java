package com.abhinav.tecstudyapquiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ScoreActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private TextView scored,total,naam,no;
    private InterstitialAd mInterstitialAd;
    private Button doneBtn;
    AdView mAdView;
    AdRequest adRequest;
//    DatabaseReference ref;
//    FirebaseDatabase mDatabase;
    EditText editNameText;
    public  static  final  String sharepref = "SP";
    public  static  final  String user = "UID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

loadAds();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                interstitialAdLoad();


            }
        });

        scored = findViewById(R.id.scored);
        total = findViewById(R.id.total);
        doneBtn = findViewById(R.id.done_btn);
        naam = findViewById(R.id.nameid);
        no = findViewById(R.id.nmbrid);
        editNameText = findViewById(R.id.edtNewUserName);
        saveData();

        sharedPreferences = getSharedPreferences(sharepref, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(user))
        {
            editNameText.setText(sharedPreferences.getString(user,""));


        }



//        mDatabase = FirebaseDatabase.getInstance();
//        ref= mDatabase.getReference("users");

//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
//                    String id = dataSnapshot1.getKey();
//                    String urName = snapshot.child(id).child("yourNaam").getValue(String.class);
//                    String yourNo = snapshot.child(id).child("yourNo").getValue(String.class);
//                    naam.setText(urName);
//                    no.setText(yourNo);
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        scored.setText(String.valueOf(getIntent().getIntExtra("score",0)));
        total.setText("Out of "+String.valueOf(getIntent().getIntExtra("total",0)));


final MediaPlayer mp = MediaPlayer.create(this,R.raw.your_name);
mp.start();

            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    showInterstitialAd();

                    Intent intent = new Intent(getApplicationContext(),CategoriesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(ScoreActivity.this, "Thank you !!", Toast.LENGTH_SHORT).show();
                    final MediaPlayer mediaPlayer = MediaPlayer.create(ScoreActivity.this,R.raw.thank_you);
                    mediaPlayer.start();

                }
            });

        }

    public void loadAds(){

        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }



    public void saveData() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScoreActivity.this);
        alertDialog.setTitle("Name :");
        alertDialog.setMessage("Please Enter Your Name");
        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout, null);

        editNameText = (EditText) sign_up_layout.findViewById(R.id.edtNewUserName);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.name);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                showInterstitialAd();
                String urrName = editNameText.getText().toString();
                naam.setText("Well Done "+urrName);
                sendEmail();
                String n = editNameText.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(user,n);
                editor.commit();
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

showInterstitialAd();
                String n = editNameText.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(user,n);
                editor.commit();

//                final User user = new User(edtNewUser.getText().toString(),
//                        edtNewPassword.getText().toString(), edtNewEmail.getText().toString());
                if (editNameText.getText().toString().isEmpty()){
                    editNameText.setError("Name Required");

                }

                String urrName = editNameText.getText().toString();
                naam.setText("Well Done "+urrName);
                sendEmail();
                final MediaPlayer mediaPlayer = MediaPlayer.create(ScoreActivity.this,R.raw.name_accepted);
                mediaPlayer.start();
                dialogInterface.dismiss();

            }
        });
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {
        if (editNameText.getText().toString().isEmpty()){
            editNameText.setError("Name Required");
        }
        String urrName = editNameText.getText().toString();
        naam.setText(urrName);
        sendEmail();
        super.onBackPressed();
    }

    private void sendEmail() {

        //Getting content for email
        String email = "143abhip@gmail.com";
        String subject = "Result ";
        String message = "Name: "+naam.getText().toString()+"\n"
                +"Contact No: "+no.getText().toString()
                +"\n"+"Marks:"+ scored.getText().toString();

        //Creating com.example.quiz.SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }


    public void interstitialAdLoad(){

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.interstitialAd_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                     //   Log.d("Admob interstitial", "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                             //interstitialAdLoad();


                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                              //  Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                              //  Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                      //  Log.d("Admob interstitial", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    private void showInterstitialAd(){

        if (mInterstitialAd != null) {
            mInterstitialAd.show(ScoreActivity.this);
        } else {

            //Toast.makeText(MainActivity.this, "yourmessage", Toast.LENGTH_SHORT).show();
        }
    }

   

}