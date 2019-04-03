package com.example.daniel.twitterapp;

/**
 * DirectMessage class implements interface to send a directMessage
 *
 * @author Daniel Leca
 * @since 9/3/2018
 */

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class DirectMessage extends TwitterApiClient {
    public DirectMessage(TwitterSession session) {
        super(session);
    }

    public SendDirectMessage sendDirectMessage() {
        return getService(SendDirectMessage.class);
    }

}

/**
 * @param userId implements the id of the Twitter user
 * @param screenName implements the screenName of the Twitter user
 * @param text takes a message in the direct message
 */
interface SendDirectMessage {
    @FormUrlEncoded
    @POST("1.1/direct_messages/new.json")
    Call<Tweet> sendPrivateMessage(@Field("user_id") Long userId,
                                   @Field("screen_name") String screenName,
                                   @Field("text") String text);
}