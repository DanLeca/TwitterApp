package com.example.daniel.twitterapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Daniel on 13/01/2018.
 */

public class Profile extends ListActivity {
    android.webkit.CookieManager cookieManager = CookieManager.getInstance();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    final private TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
    final private StatusesService statusesService = twitterApiClient.getStatusesService();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.homeRefresh);

        showTimeline();
    }

    private void createProfile(View view) {
        String username = getIntent().getStringExtra("username");

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(username)
                .build();

        addTimeline(userTimeline);
    }

    private void onSwipe() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void addTimeline(UserTimeline userTimeline) {
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        setListAdapter(adapter);
    }

    private void addTimeline(FixedTweetTimeline timeline) {
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(Profile.this)
                .setTimeline(timeline)
                .build();

        setListAdapter(adapter);
    }

    private void showMentions()  {
        Call<List<Tweet>> call = statusesService.mentionsTimeline(200, null, null,
                null, null, null);

        call.enqueue(new Callback<List<Tweet>>()    {
            @Override
            public void success(Result<List<Tweet>> result) {
                final FixedTweetTimeline timeline = new FixedTweetTimeline.Builder()
                        .setTweets(result.data)
                        .build();

                addTimeline(timeline);
            }
            public void failure(TwitterException exception) {
                //messageBox("showMentions", exception.getMessage());
                Toast.makeText(Profile.this, "Timeline failed to load", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openAbout(View view)   {
        //createProfile(null);
        //showMentions();
        Intent openSearch = new Intent(this, Search.class);
        startActivity(openSearch);
    }

    private void showTimeline()    {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         TextView textField = (TextView) findViewById(R.id.loading);
                                                         textField.setText("Loading...");
                                                         showTimeline();
                                                     }
                                                 }
        );

        onSwipe();
        Call<List<Tweet>> call = statusesService.homeTimeline(200, null, null,
                null, null, null, true);
            call.enqueue(new Callback<List<Tweet>>()    {
            @Override
            public void success(Result<List<Tweet>> result) {

                final FixedTweetTimeline timeline = new FixedTweetTimeline.Builder()
                        .setTweets(result.data)
                        .build();
                addTimeline(timeline);
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Profile.this, "Timeline failed to load", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void signOut(View view)  {
        android.webkit.CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        Intent signedIntent = new Intent (Profile.this, MainActivity.class);
        startActivity(signedIntent);
    }

    public void createTweet(View view)  {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(this)
                .session(session)
                .createIntent();
        startActivity(intent);

        Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show();
    }

}