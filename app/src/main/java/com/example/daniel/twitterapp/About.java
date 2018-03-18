package com.example.daniel.twitterapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Daniel on 15/01/2018.
 */

public class About extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);
    }
}
