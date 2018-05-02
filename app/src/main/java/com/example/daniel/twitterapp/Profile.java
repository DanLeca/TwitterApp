package com.example.daniel.twitterapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.Timeline;
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
    private Button retweet;
    private Button reply;
    private Button delete;
    private Button like;
    private View dialogWindow;
    private TextView dialogText;
    private Boolean retweetedStatus;
    private ImageButton exitMenu;
    private Boolean favouriteStatus;
    private Button dmButton;
    private View dmWindow;
    private EditText dmField;
    private Button sendDM;
    private ImageButton exitDMenu;
    private EditText dmUser;

    /*
    TODO: OnRefreshListener for the current timeline view
     */
    /*
    TODO: Use external library for a custom UI of the user profile
     */
    /*
    TODO: Fix the text for retweet + favourite buttons
     */
    /*
    TODO: Externalise strings in the strings.xml file
     */
    /*
    TODO: Tidy up the toast notifications
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        String username = getIntent().getStringExtra("username");
        toolbar.setTitle(username);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProfile();
            }
        });

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
    public void onBackPressed() {
        showTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)   {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    private void searchTimeline()   {
        SearchTimeline searchTimeline = new SearchTimeline.Builder().query(searchQuery).build();
        final CustomTweetTimelineListAdapter adapter = new CustomTweetTimelineListAdapter(this, searchTimeline);
        mListView = (ListView) findViewById(R.id.timelineView);
        mListView.setEmptyView(findViewById(R.id.loading));
        mListView.setAdapter(adapter);
    }

    class CustomTweetTimelineListAdapter extends TweetTimelineListAdapter {

        public CustomTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline) {
            super(context, timeline);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);

            view.setEnabled(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long tweetId = getItemId(position);
                    String userID = getItem(position).user.screenName;

                    if(getItem(position).retweeted) {
                        retweetedStatus = true;
                    }   else    {
                        retweetedStatus = false;
                    }

                    if(getItem(position).favorited) {
                        favouriteStatus = true;
                    }   else    {
                        favouriteStatus = false;
                    }

                    displayPopupWindow(view, tweetId, retweetedStatus, userID, favouriteStatus);
                }
            });
            return view;
        }
    }

    public void displayPopupWindow(View v, final Long tweetId, final Boolean retweetedStatus,
                                   final String userID, final Boolean favouriteStatus)    {
        actionsMenu();

        retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(retweetedStatus == true) {
                    unretweetAPI(tweetId);
                }   else    {
                    retweetAPI(tweetId);
                }
                actionsMenu();
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyTweet(userID);
                actionsMenu();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTweet(tweetId);
                actionsMenu();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favouriteStatus == true) {
                    unfavourite(tweetId);
                }   else    {
                    favourite(tweetId);
                }
                actionsMenu();
            }
        });

        exitMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionsMenu();
            }
        });

        dmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dmMenu();
                dmUser = (EditText) findViewById(R.id.dmUser);
                dmUser.setText(userID.toString());

                sendDM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dmField = (EditText) findViewById(R.id.dmField);
                        String dmContents = dmField.getText().toString();
                        String sendTo = dmUser.getText().toString();

                        sendDirectMessage(dmContents, sendTo);
                        dmMenu();
                        dmField.setText("");
                        dmUser.setText("");
                    }
                });
                exitDMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dmMenu();
                    }
                });
            }
        });
    }

    public void dmMenu()    {
        dmWindow = (View) findViewById(R.id.colored_bar2);
        dmField = (EditText) findViewById(R.id.dmField);
        retweet = (Button) findViewById(R.id.retweetButton);
        reply = (Button) findViewById(R.id.replyButton);
        delete = (Button) findViewById(R.id.deleteButton);
        like = (Button) findViewById(R.id.likeButton);
        dialogWindow = (View) findViewById(R.id.colored_bar);
        dialogText = (TextView) findViewById(R.id.popupDialog);
        exitMenu = (ImageButton) findViewById(R.id.closeMenu);
        dmButton = (Button) findViewById(R.id.dmButton);
        sendDM = (Button) findViewById(R.id.sendDM);
        exitDMenu = (ImageButton) findViewById(R.id.closeDMenu);
        dmUser = (EditText) findViewById(R.id.dmUser);

        if(dmWindow.getVisibility() == View.INVISIBLE)    {
            dialogText.setVisibility(View.INVISIBLE);
            dialogWindow.setVisibility(View.INVISIBLE);
            retweet.setVisibility(View.INVISIBLE);
            reply.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            like.setVisibility(View.INVISIBLE);
            dmButton.setVisibility(View.INVISIBLE);
            exitDMenu.setVisibility(View.VISIBLE);
            exitMenu.setVisibility(View.VISIBLE);
            dmWindow.setVisibility(View.VISIBLE);
            sendDM.setVisibility(View.VISIBLE);
            dmField.setVisibility(View.VISIBLE);
            dmUser.setVisibility(View.VISIBLE);
        }   else    {
            dialogText.setVisibility(View.INVISIBLE);
            dialogWindow.setVisibility(View.INVISIBLE);
            retweet.setVisibility(View.INVISIBLE);
            reply.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            like.setVisibility(View.INVISIBLE);
            exitDMenu.setVisibility(View.INVISIBLE);
            exitMenu.setVisibility(View.INVISIBLE);
            dmButton.setVisibility(View.INVISIBLE);
            dmWindow.setVisibility(View.INVISIBLE);
            dmField.setVisibility(View.INVISIBLE);
            sendDM.setVisibility(View.INVISIBLE);
            dmUser.setVisibility(View.INVISIBLE);
        }
    }

    public void actionsMenu()   {
        retweet = (Button) findViewById(R.id.retweetButton);
        reply = (Button) findViewById(R.id.replyButton);
        delete = (Button) findViewById(R.id.deleteButton);
        like = (Button) findViewById(R.id.likeButton);
        dialogWindow = (View) findViewById(R.id.colored_bar);
        dialogText = (TextView) findViewById(R.id.popupDialog);
        exitMenu = (ImageButton) findViewById(R.id.closeMenu);
        dmButton = (Button) findViewById(R.id.dmButton);

        if(dialogText.getVisibility() == View.VISIBLE)  {
            dialogText.setVisibility(View.INVISIBLE);
            dialogWindow.setVisibility(View.INVISIBLE);
            retweet.setVisibility(View.INVISIBLE);
            reply.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            like.setVisibility(View.INVISIBLE);
            exitMenu.setVisibility(View.INVISIBLE);
            dmButton.setVisibility(View.INVISIBLE);
        }   else    {
            dialogText.setVisibility(View.VISIBLE);
            dialogWindow.setVisibility(View.VISIBLE);
            retweet.setVisibility(View.VISIBLE);
            reply.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            like.setVisibility(View.VISIBLE);
            exitMenu.setVisibility(View.VISIBLE);
            dmButton.setVisibility(View.VISIBLE);
        }
    }

    private void retweetAPI(Long tweetID)    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> call = statusesService.retweet(tweetID, null);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Profile.this, "Retweeted", Toast.LENGTH_SHORT).show();
                retweet = (Button)findViewById(R.id.retweetButton);
                retweet.setText("Unretweet");

                Parcelable state = mListView.onSaveInstanceState();
                mListView.onRestoreInstanceState(state);
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void favourite(Long tweetID)    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        FavoriteService statusesService = twitterApiClient.getFavoriteService();
        Call<Tweet> call = statusesService.create(tweetID, true);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Profile.this, "Favourited", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unretweetAPI(Long tweetID)    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> call = statusesService.unretweet(tweetID, null);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Profile.this, "Unretweeted", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unfavourite(Long tweetID)    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        FavoriteService statusesService = twitterApiClient.getFavoriteService();
        Call<Tweet> call = statusesService.destroy(tweetID, true);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Profile.this, "Unfavourited", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTweet(Long tweetID)    {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> call = statusesService.destroy(tweetID, null);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(Profile.this, "Tweet Deleted", Toast.LENGTH_SHORT).show();
            }
            public void failure(TwitterException exception) {
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTimeline(FixedTweetTimeline timeline) {
        final CustomTweetTimelineListAdapter adapter = new CustomTweetTimelineListAdapter(this, timeline);
        mListView = (ListView) findViewById(R.id.timelineView);
        mListView.setEmptyView(findViewById(R.id.loading));
        mListView.setAdapter(adapter);
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
            case R.id.profileButton:
                createProfile();
                return true;
            case R.id.mentionsButton:
                showMentions();
                return true;
            case R.id.dmButton:
                sendDM();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendDM() {
        dmMenu();
        dmUser = (EditText) findViewById(R.id.dmUser);

        sendDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dmField = (EditText) findViewById(R.id.dmField);
                String dmContents = dmField.getText().toString();
                String sendTo = dmUser.getText().toString();

                sendDirectMessage(dmContents, sendTo);
                dmMenu();
                dmField.setText("");
                dmUser.setText("");
            }
        });
        exitDMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dmMenu();
            }
        });
    }

    private void createProfile() {
        String username = getIntent().getStringExtra("username");
        UserTimeline userTimeline = new UserTimeline.Builder().screenName(username).build();
        final CustomTweetTimelineListAdapter adapter = new CustomTweetTimelineListAdapter(this, userTimeline);
        mListView = (ListView) findViewById(R.id.timelineView);
        mListView.setEmptyView(findViewById(R.id.loading));
        mListView.setAdapter(adapter);
    }

    private void onSwipe() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
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

    public void sendDirectMessage(String message, String userID) {
        DirectMessage dm = new DirectMessage(activeSession);
        Call<Tweet> call = dm.sendDirectMessage().sendPrivateMessage(null, userID, message);

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

    public void getUserDetails(View view)    {
    }

    public void signOut(View view)  {
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

    public void replyTweet(String userID)    {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(this)
                .text("@" + userID + ", ")
                .session(activeSession)
                .createIntent();
        startActivity(intent);
    }

}