package learnandroid.academy.bloodbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView appLogo;
    private TextView appTitle,appSlogan;
    Animation topAnimation, bottomAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_scren);

        appLogo = findViewById(R.id.appLogo);
        appTitle = findViewById(R.id.appTitle);
        appSlogan = findViewById(R.id.appSlogan);

        //Splash screen animation setup
        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //setting animations
        appLogo.setAnimation(topAnimation);
        appTitle.setAnimation(bottomAnimation);
        appSlogan.setAnimation(bottomAnimation);

        //Next activity after animation is completed
        int SPLASH_SCREEN =4300;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        },SPLASH_SCREEN);
    }
}