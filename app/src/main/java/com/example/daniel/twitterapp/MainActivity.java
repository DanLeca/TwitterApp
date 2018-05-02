package com.example.daniel.twitterapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends Activity {
    TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                login(session);
            }
            @Override
            public void failure(TwitterException exception) {
                StyleableToast.makeText(MainActivity.this, "Authentication Failed", R.style.authFail).show();
            }
        });
    }

    private void login(TwitterSession session) {
        String username = session.getUserName();
        Intent intent = new Intent(MainActivity.this, Profile.class);

        intent.putExtra("username", username);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
