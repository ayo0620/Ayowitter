package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String ImgUrl;
    public String date;
    public int numRetweet;
    public int numLikes;
    public String id;
    public boolean isFavorited;
    public boolean isRetweet;


    public Tweet(){}
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("retweeted_status"))
        {
            return null;
        }

        Tweet tweet = new Tweet();

        tweet.id = jsonObject.getString("id_str");

        if(jsonObject.has("full_text"))
        {
            tweet.body = jsonObject.getString("full_text");
        }
        else{
            tweet.body = jsonObject.getString("text");
        }

        if(jsonObject.getJSONObject("entities").has("media"))
        {
            tweet.ImgUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.numRetweet = jsonObject.getInt("retweet_count");
        tweet.numLikes = jsonObject.getInt( "favorite_count");
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweet = jsonObject.getBoolean("retweeted");
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0; i<jsonArray.length();i++)
        {
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            if(newTweet != null)
            {
                tweets.add(newTweet);
            }
        }
        return tweets;
    }
}
