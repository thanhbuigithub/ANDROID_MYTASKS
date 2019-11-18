package com.example.mytasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends MainActivity {
    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        imageView=findViewById(R.id.splash_logo);
        textView=findViewById(R.id.text_splash);
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.transition_splash_screen);
        imageView.startAnimation(animation);
        textView.startAnimation(animation);

        final Intent intent=new Intent(this,SignIn_Activity.class);
        Thread wait = new Thread(){
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                }
            }
        };
        wait.start();
    }
}
