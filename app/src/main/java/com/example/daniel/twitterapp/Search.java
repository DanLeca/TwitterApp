package com.example.daniel.twitterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

/**
 * Created by Daniel on 14/01/2018.
 */

public class Search extends AppCompatActivity {
    private CheckBox excludeReplies;
    private String searchQuery = "";
    private EditText searchField;
    private Button button;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchField = (EditText) findViewById(R.id.editText);
        excludeReplies = (CheckBox) findViewById(R.id.excludeReplies);

        /**
         * TODO: Implement a menu to exclude replies and mentions from the search results
         */
 /*        button = (Button) findViewById(R.id.Filter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Search.this, button);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setChecked(!item.isChecked());
                        Toast.makeText(Search.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popupMenu.show();
            }
        }); */

        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) view;
                String enteredText = editText.getText().toString();

                updateTimeline(enteredText);
            }
        });
    }

    private void updateTimeline(String enteredText) {
        if(enteredText.toString().startsWith("@")) {
            if(excludeReplies.isChecked())   {
                searchQuery = "from:" + enteredText + " exclude:replies";
                setUpTimeline();
            }   else    {
                searchQuery = "from:" + enteredText;
                setUpTimeline();
            }
        }   else    {
            if(excludeReplies.isChecked())   {
                searchQuery = enteredText + " exclude:replies";
                setUpTimeline();
            }   else    {
                searchQuery = enteredText;
                setUpTimeline();
            }
        }
    }

    private void setUpTimeline() {
        SearchTimeline searchTimeline = new SearchTimeline.Builder().query(searchQuery).build();
        final TweetTimelineListAdapter timelineAdapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(searchTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();

        ListView timelineView = (ListView) findViewById(R.id.event_timeline);
        timelineView.setEmptyView(findViewById(R.id.empty_timeline));
        timelineView.setAdapter(timelineAdapter);
    }
}
