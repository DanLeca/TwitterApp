package com.example.daniel.twitterapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;

import retrofit2.Call;

/**
 * Created by Daniel on 22/01/2018.
 */

/*
 * UNUSED CLASS
 */

public class Interactions extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.about);
    }

    private void retweetAPI()    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> call = statusesService.retweet(952937851800473600L, null);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Interactions.this, "Retweeted", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Interactions.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unretweetAPI()    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> call = statusesService.unretweet(952937851800473600L, null);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Interactions.this, "Unretweeted", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Interactions.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTweet()    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> call = statusesService.destroy(848561333004107776L, null);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Interactions.this, "Tweet Deleted", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Interactions.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void favouriteTweet()    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        FavoriteService statusesService = twitterApiClient.getFavoriteService();
        Call<Tweet> call = statusesService.create(952937851800473600L, true);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Interactions.this, "Tweet has been favourited", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Interactions.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unfavouriteTweet()  {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        FavoriteService statusesService = twitterApiClient.getFavoriteService();
        Call<Tweet> call = statusesService.destroy(952937851800473600L, true);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Interactions.this, "Tweet has been unfavourited", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Interactions.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
