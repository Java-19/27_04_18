package com.sheygam.java19_24__04_18;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.reg_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String token = HttpProvider.getInstance().registration("gregjava19111@mail.com","12345");
                                    Log.d("MY_TAG", "run: token: " + token);
                                } catch (IOException e){
                                    Log.d("MY_TAG", "run: connection error!");
                                }catch (Exception e) {
                                    Log.d("MY_TAG", "run: " + e.getMessage());
                                }
                            }
                        }).start();
                    }
                });
    }
}
