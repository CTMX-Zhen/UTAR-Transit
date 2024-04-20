package my.edu.utar.utartransit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    public static final int SPLASH_TIMEOUT = 3000;
    ImageView Logo, Roadmap, Transportimage;
    TextView text1, text2;

    Animation Top_anim, Bottom_anim, Left_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Logo=findViewById(R.id.logo);
        Roadmap=findViewById(R.id.roadmap);
        Transportimage=findViewById(R.id.transportation);
        text1=findViewById(R.id.ss_text1);
        text2=findViewById(R.id.ss_description);

        Top_anim= AnimationUtils.loadAnimation(this, R.anim.topanim);
        Bottom_anim=AnimationUtils.loadAnimation(this,R.anim.bottomanim);
        Left_anim=AnimationUtils.loadAnimation(this,R.anim.leftanim);

        //Logo.setAnimation(Top_anim);
        Roadmap.setAnimation(Top_anim);
        Transportimage.setAnimation(Left_anim);
        text1.setAnimation(Bottom_anim);
        text2.setAnimation(Bottom_anim);

        // Using a Handler to delay the transition to MainActivity

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                // Close SplashScreen activity
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
