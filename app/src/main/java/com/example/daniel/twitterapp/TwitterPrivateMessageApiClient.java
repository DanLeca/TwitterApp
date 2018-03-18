package com.example.daniel.twitterapp;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Daniel on 14/03/2018.
 */

public class TwitterPrivateMessageApiClient extends TwitterApiClient {
    public TwitterPrivateMessageApiClient (TwitterSession session){
        super(session);
    }

    /** This class provides the customized PrivateMessageService */
    public PrivateMessageService getPrivateMessageService(){
        return getService(PrivateMessageService.class);
    }

    public PrivateMessageService2 getPrivateMessageService2(){
        return getService(PrivateMessageService2.class);
    }
}

interface PrivateMessageService {
    @FormUrlEncoded
    @POST("/1.1/direct_messages/new.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<Tweet> sendPrivateMessage(@Field("user_id") Long userId,
                                   @Field("screen_name") String screenName,
                                   @Field("text") String text);
}

interface PrivateMessageService2 {
    @FormUrlEncoded
    @POST("1.1/direct_messages/new.json")
    Call<Tweet> sendPrivateMessage2(@Field("user_id") Long userId,
                                   @Field("screen_name") String screenName,
                                   @Field("text") String text);
}

