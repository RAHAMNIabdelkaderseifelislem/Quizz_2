package com.example.quizapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.quizapp.Background_Music.HomeWatcherServicesActivity;
import com.example.quizapp.Background_Music.MusicServiceActivity;
import com.example.quizapp.Category_Levels.All_Knowledge.AllKnowledgeQuizActivity;
import com.example.quizapp.Category_Levels.History.HistoryBeginnerActivity;
import com.example.quizapp.Category_Levels.History.HistoryProfessionalActivity;
import com.example.quizapp.Category_Levels.History.HistoryWorldClassActivity;
import com.example.quizapp.Category_Levels.Marketing.MarketingBeginnerActivity;
import com.example.quizapp.Category_Levels.Marketing.MarketingProfessionalActivity;
import com.example.quizapp.Category_Levels.Marketing.MarketingWorldClassActivity;
import com.example.quizapp.Category_Levels.Multimedia.MultimediaBeginnerActivity;
import com.example.quizapp.Category_Levels.Multimedia.MultimediaProfessionalActivity;
import com.example.quizapp.Category_Levels.Multimedia.MultimediaWorldClassActivity;
import com.example.quizapp.Category_Levels.Programming.ProgrammingBeginnerActivity;
import com.example.quizapp.Category_Levels.Programming.ProgrammingProfessionalActivity;
import com.example.quizapp.Category_Levels.Programming.ProgrammingWorldClassActivity;
import com.example.quizapp.User.LoginActivity;
import com.example.quizapp.User.UserProfileActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MenuHomeScreenActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    static final float END_SCALE = 0.7f;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Button navToggler_btn;
    LinearLayout linearLayout;
    Dialog dialog;
    Button allKnowledge_btn;
    private InterstitialAd mInterstitialAd;
    private boolean mIsBound = false;

    private MusicServiceActivity mServ;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName componentName, IBinder
                binder) {
            mServ = ((MusicServiceActivity.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mServ = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); ///Eneter into fullscreen mode

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Google Ads-----Banner Ads Integration
        MobileAds.initialize(this, getString(R.string.admob_id));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        ////Google ads- interstatial Integration-------------------///////////////
        MobileAds.initialize(this, getString(R.string.admob_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });


        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navToggler_btn = findViewById(R.id.action_menu_presenter);
        linearLayout = findViewById(R.id.main_content);
        allKnowledge_btn = findViewById(R.id.allknowledge_start);

        
        allKnowledge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AK = new Intent(getApplicationContext(), AllKnowledgeQuizActivity.class);
                startActivity(AK);
            }
        });
        navigationDrawer();

        dialog = new Dialog(this, R.style.AnimateDialog);

        doBindService();
        Intent music = new Intent();
        music.setClass(MenuHomeScreenActivity.this, MusicServiceActivity.class);
        startService(music);

        HomeWatcherServicesActivity mHomeWatcherServicesActivity;

        mHomeWatcherServicesActivity = new HomeWatcherServicesActivity(this);
        mHomeWatcherServicesActivity.setOnHomePressedListener(new HomeWatcherServicesActivity.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcherServicesActivity.startWatch();
    }

    void doBindService() {
        bindService(new Intent(this, MusicServiceActivity.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(serviceConnection);
            mIsBound = false;
        }
    }


    public void market_start(View view) {
        Button close_btn,beginner_btn,professional_btn,worldclass_btn;
        final ProgressBar progressBar;

        dialog.setContentView(R.layout.activity_custom_popup);
        close_btn = dialog.findViewById(R.id.close_lev);
        beginner_btn = dialog.findViewById(R.id.beginner);
        professional_btn = dialog.findViewById(R.id.professional);
        worldclass_btn = dialog.findViewById(R.id.world_class);
        progressBar = dialog.findViewById(R.id.pro_gress1);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        beginner_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent BG = new Intent(getApplicationContext(), MarketingBeginnerActivity.class);
                startActivity(BG);

            }
        });
        professional_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent PRO = new Intent(MenuHomeScreenActivity.this, MarketingProfessionalActivity.class);
                startActivity(PRO);
            }


        });
        worldclass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent WC = new Intent(getApplicationContext(), MarketingWorldClassActivity.class);
                startActivity(WC);

            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void android_start(View view) {
        Button close_btn;
        Button beginner_btn;
        Button professional_btn;
        Button worldclass_btn;
        final ProgressBar progressBar;


        dialog.setContentView(R.layout.activity_custom_popup);
        close_btn = dialog.findViewById(R.id.close_lev);
        beginner_btn = dialog.findViewById(R.id.beginner);
        professional_btn = dialog.findViewById(R.id.professional);
        worldclass_btn = dialog.findViewById(R.id.world_class);
        progressBar = dialog.findViewById(R.id.pro_gress1);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        beginner_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
//////////////////////////On click action to MultimediaBeginnerActivity Activity
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent BG = new Intent(getApplicationContext(), ProgrammingBeginnerActivity.class);
                startActivity(BG);

            }
        });
        professional_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent PRO = new Intent(MenuHomeScreenActivity.this, ProgrammingProfessionalActivity.class);
                startActivity(PRO);
            }
        });
        worldclass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent WC = new Intent(getApplicationContext(), ProgrammingWorldClassActivity.class);
                startActivity(WC);

            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void history_start(View view) {
        Button close_btn;
        Button beginner_btn;
        Button professional_btn;
        Button worldclass_btn;
        final ProgressBar progressBar;


        dialog.setContentView(R.layout.activity_custom_popup);
        close_btn = dialog.findViewById(R.id.close_lev);
        beginner_btn = dialog.findViewById(R.id.beginner);
        professional_btn = dialog.findViewById(R.id.professional);
        worldclass_btn = dialog.findViewById(R.id.world_class);
        progressBar = dialog.findViewById(R.id.pro_gress1);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        beginner_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent BG = new Intent(getApplicationContext(), HistoryBeginnerActivity.class);
                startActivity(BG);

            }
        });
        professional_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent PRO = new Intent(MenuHomeScreenActivity.this, HistoryProfessionalActivity.class);
                startActivity(PRO);
            }
        });
        worldclass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent WC = new Intent(getApplicationContext(), HistoryWorldClassActivity.class);
                startActivity(WC);

            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public void multimedia_start(View view) {
        Button close_btn;
        Button beginner_btn;
        Button professional_btn;
        Button worldclass_btn;
        final ProgressBar progressBar;


        dialog.setContentView(R.layout.activity_custom_popup);
        close_btn = dialog.findViewById(R.id.close_lev);
        beginner_btn = dialog.findViewById(R.id.beginner);
        professional_btn = dialog.findViewById(R.id.professional);
        worldclass_btn = dialog.findViewById(R.id.world_class);
        progressBar = dialog.findViewById(R.id.pro_gress1);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        beginner_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent BG = new Intent(getApplicationContext(), MultimediaBeginnerActivity.class);
                startActivity(BG);

            }
        });
        professional_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent PRO = new Intent(MenuHomeScreenActivity.this, MultimediaProfessionalActivity.class);
                startActivity(PRO);
            }
        });
        worldclass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(3000);
                Intent WC = new Intent(getApplicationContext(), MultimediaWorldClassActivity.class);
                startActivity(WC);

            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void navigationDrawer() {


        navigationView.bringToFront();
        //navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);

        navToggler_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.cat_heading));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset*(1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                linearLayout.setScaleX(offsetScale);
                linearLayout.setScaleY(offsetScale);


                final float xOffset = drawerView.getWidth()*slideOffset;
                final float xOffsetDiff = linearLayout.getWidth()*diffScaledOffset/2;
                final float xTranslation = xOffset - xOffsetDiff;
                linearLayout.setTranslationX(xTranslation);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.user_profile) {

            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
            MenuHomeScreenActivity.super.onBackPressed();

        } else if (menuItem.getItemId() == R.id.rate) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(" ")));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("Coming Soon")));
            }
        } else if (menuItem.getItemId() == R.id.about) {
            Intent about = new Intent(getApplicationContext(), AboutUsActivity.class);
            startActivity(about);
        } else if (menuItem.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        return true;
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicServiceActivity.class);
        stopService(music);

    }
}
