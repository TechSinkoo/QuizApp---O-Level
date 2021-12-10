package com.abhinav.tecstudyapquiz;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    public static final String FILE_NAME = "TecStudyAP";
    public static final String KEY_NAME = "QUESTIONS";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private TextView question,noIndicator;
    private FloatingActionButton bookmarkBtn;
    private LinearLayout optionsContainer;
    private Button shareBtn,nextBtn;
    private int count = 0;
    private List<QuestionModel> list;
    private int position = 0;
    private int score = 0;

    private String setId;
    private Dialog loadingDialog;

    private List<QuestionModel> bookmarksList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedQuestionPosition;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private AdRequest adRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadAds();
        //adMyListener();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                interstitialAdLoad();

            }
        });

        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarkBtn = findViewById(R.id.bookmarks_btn);
        optionsContainer = findViewById(R.id.options_container);
        shareBtn = findViewById(R.id.share_btn);
        nextBtn = findViewById(R.id.next_btn);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelMatch())
                {
                    bookmarksList.remove(matchedQuestionPosition);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }else{
                    bookmarksList.add(list.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });


        setId = getIntent().getStringExtra("setId");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        list = new ArrayList<>();
        loadingDialog.show();
        myRef.child("SETS").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    String id = dataSnapshot1.getKey();
                    String question = dataSnapshot1.child("question").getValue().toString();
                    String a = dataSnapshot1.child("optionA").getValue().toString();
                    String b = dataSnapshot1.child("optionB").getValue().toString();
                    String c = dataSnapshot1.child("optionC").getValue().toString();
                    String d = dataSnapshot1.child("optionD").getValue().toString();
                    String correctANS = dataSnapshot1.child("correctANS").getValue().toString();

                    list.add(new QuestionModel(id,question,a,b,c,d,correctANS,setId));
                }
                if (list.size()>0)
                {

                    for(int i = 0;i <4; i++)
                    {
                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkAnswer((Button)view);
                            }
                        });
                    }
                    playAnim(question,0,list.get(position).getQuestion());
                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.7f);
                            enableOption(true);
                            position++;
                            if(position == list.size())
                            {
                                //show add
                                showInterstitialAd();
                                Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("total",list.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
                            count = 0;
                            playAnim(question,0,list.get(position).getQuestion());



                        }
                    });

                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String body = list.get(position).getQuestion() +"\n\n"+
                                    list.get(position).getA()+"\n"+
                                    list.get(position).getB()+"\n"+
                                    list.get(position).getC()+"\n"+
                                    list.get(position).getD()+"\n\n"+
                                    "Download TecStudyAP QuizAPP\nhttps://play.google.com/store/apps/details?id=com.abhinav.tecstudyapquiz";
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Quiz Challenge");
                            shareIntent.putExtra(Intent.EXTRA_TEXT,body);
                            startActivity(Intent.createChooser(shareIntent,"Share via"));
                        }
                    });

                }else{
                    finish();
                    Toast.makeText(QuestionsActivity.this, "No Questions", Toast.LENGTH_SHORT).show();

                }
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(QuestionsActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data)
    {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(300).setStartDelay(50)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if(value == 0 && count<4)
                {
                    String option="";

                    if(count==0)
                    {
                        option=list.get(position).getA();
                    }else if(count==1){
                        option=list.get(position).getB();
                    }else if(count==2){
                        option=list.get(position).getC();
                    }else if(count==3){
                        option=list.get(position).getD();

                    }
                    playAnim(optionsContainer.getChildAt(count),0,option);
                    count++;
                }

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if(value == 0)
                {
                    try{
                        ((TextView)view).setText(data);
                        noIndicator.setText(position+1+"/"+list.size());
                        if (modelMatch())
                        {
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                        }else{

                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        }

                    }catch(ClassCastException ex){
                        ((Button)view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view,1,data);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    private void checkAnswer(Button selectedOption){
        enableOption(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);



        if(selectedOption.getText().toString().equals(list.get(position).getAnswer())){
            //correct
            score++;
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#86E18A")));
            nextBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3FB644")));

            final MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.right);
            mediaPlayer.start();


        }else{
            //incorrect
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF7A7A")));
            Button correctOption = (Button) optionsContainer.findViewWithTag(list.get(position).getAnswer());
            correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#86E18A")));
            nextBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FD5252")));


            MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.wrong_answer);
            mediaPlayer.start();


        }


    }
    private void enableOption(boolean enable){

        for(int i = 0;i <4; i++)
        {
            optionsContainer.getChildAt(i).setEnabled(enable);
            if(enable)
            {
                optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E1E1E1")));


                nextBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A3A3A3")));
            }
        }
    }
    private void getBookmarks()
    {
        String json = preferences.getString(KEY_NAME,"");
        Type type = new TypeToken<List<QuestionModel>>(){}.getType();

        bookmarksList = gson.fromJson(json,type);
        if(bookmarksList ==  null){
            bookmarksList = new ArrayList<>();
        }
    }

    private boolean modelMatch(){
        boolean matched = false;
        int i=0;

        for (QuestionModel model : bookmarksList) {
            if (model.getQuestion().equals(list.get(position).getQuestion())
                    && model.getAnswer().equals(list.get(position).getAnswer())
                    && model.getSet().equals(list.get(position).getSet())) {
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;
        }
        return  matched;
    }

    private  void storeBookmarks()
    {
        String json = gson.toJson(bookmarksList);
        editor.putString(KEY_NAME,json);
        editor.commit();
    }

    public void loadAds(){

         mAdView = findViewById(R.id.adView);
         adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
//    public void adMyListener(){
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                super.onAdLoaded();
//                Toast.makeText(QuestionsActivity.this, "Ad loaded", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError adError) {
//                // Code to be executed when an ad request fails.
//                super.onAdFailedToLoad(adError);
//                mAdView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//                super.onAdClicked();
//
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//            }
//        });
//    }



    public void interstitialAdLoad(){

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.interstitialAd_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        // Log.d("Admob interstitial", "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                //interstitialAdLoad();


                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                // Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
//                                showInterstitialAd();
//                                Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
//                                scoreIntent.putExtra("score",score);
//                                scoreIntent.putExtra("total",list.size());
//                                startActivity(scoreIntent);
//                                finish();
//                                return;
                                // Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        // Log.d("Admob interstitial", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    private void showInterstitialAd(){

        if (mInterstitialAd != null) {
            mInterstitialAd.show(QuestionsActivity.this);
        } else {

            //Toast.makeText(MainActivity.this, "yourmessage", Toast.LENGTH_SHORT).show();
        }
    }
}