package com.example.daniel.twitterapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * About class opens the about XML file
 *
 * @author Daniel Leca
 * @since 30/2/2018
 */

public class About extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);
    }
}
