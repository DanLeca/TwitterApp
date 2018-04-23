package com.example.daniel.twitterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.muddzdev.styleabletoastlibrary.StyleableToast;
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
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.List;

import retrofit2.Call;
/**
 * Created by Daniel on 13/01/2018.
 */

public class Profile extends AppCompatActivity {
    android.webkit.CookieManager cookieManager = CookieManager.getInstance();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    final private TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
    final private StatusesService statusesService = twitterApiClient.getStatusesService();
    final TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
    private ListView mListView;
    private EditText searchField;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        String username = getIntent().getStringExtra("username");
        toolbar.setTitle(username);
        setSupportActionBar(toolbar);

        searchField = (EditText)findViewById(R.id.searchField);
        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) view;
                String enteredText = editText.getText().toString();

                if(enteredText.equals("") && !enteredText.toString().startsWith("@"))   {
                    showTimeline();
                }   if(enteredText.toString().startsWith("@"))  {
                    searchQuery = "from:" + enteredText;
                    searchTimeline();
                }   if(!enteredText.equals("") && !enteredText.toString().startsWith("@"))  {
                    searchQuery = enteredText;
                    searchTimeline();
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.homeRefresh);
        showTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)   {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    private void searchTimeline()   {
        SearchTimeline searchTimeline = new SearchTimeline.Builder().query(searchQuery).build();
        final TweetTimelineListAdapter timelineAdapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(searchTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();

        ListView timelineView = (ListView) findViewById(R.id.timelineView);
        timelineView.setEmptyView(findViewById(R.id.loading));
        timelineView.setAdapter(timelineAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutButton:
                signOut(null);
                return true;
            case R.id.searchField:
                return true;
            case R.id.aboutButton:
                Intent aboutIntent = new Intent(Profile.this, About.class);
                startActivity(aboutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        final Callback<Tweet> actionCallback = new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
            }
            @Override
            public void failure(TwitterException exception) {
                StyleableToast.makeText(Profile.this, exception.toString(), R.style.authFail).show();
            }
        };
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .setOnActionCallback(actionCallback)
                .build();
        mListView = (ListView) findViewById(R.id.timelineView);
        mListView.setAdapter(adapter);
    }

    private void addTimeline(FixedTweetTimeline timeline) {
        final Callback<Tweet> actionCallback = new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
            }
            @Override
            public void failure(TwitterException exception) {
                StyleableToast.makeText(Profile.this, exception.toString(), R.style.authFail).show();
            }
        };
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(Profile.this)
                .setTimeline(timeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .setOnActionCallback(actionCallback)
                .build();
        mListView = (ListView) findViewById(R.id.timelineView);
        mListView.setAdapter(adapter);
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
                Toast.makeText(Profile.this, "Timeline failed to load", Toast.LENGTH_LONG).show();
            }
        });
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

    private void refresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         TextView textField = (TextView) findViewById(R.id.loading);
                                                         textField.setText("Loading...");
                                                         showTimeline();
                                                     }
                                                 }
        );
    }

    public void sendDirectMessage(View view) {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        DirectMessage dm = new DirectMessage(session);
        Call<Tweet> call = dm.sendDirectMessage().sendPrivateMessage(null, "dandaleca",
                "Just a test");

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                StyleableToast.makeText(Profile.this, "DM Sent", R.style.authFail).show();
            }

            @Override
            public void failure(TwitterException exception) {
                StyleableToast.makeText(Profile.this, exception.toString(), R.style.authFail).show();
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
                .session(activeSession)
                .createIntent();
        startActivity(intent);
    }

}