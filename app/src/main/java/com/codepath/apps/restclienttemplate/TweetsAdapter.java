package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final String TAG = "TweetAdapter";


    //    Pass in the context and the list of tweets
    public TweetsAdapter(Context context,List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
//    For each row, inflate the layout
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
        return new ViewHolder(view);
    }

    //    Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Get the data at position
        Tweet tweet = tweets.get(position);
//      Bind the tweet with view holder
        holder.bind(tweet);
    }


    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //    Define a viewHolder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivImageTweet;
        TextView tvTime;
        TextView tvHandle;
        TextView tvNumRetweet;
        ImageButton ibFavourite;
        TextView tvFavouriteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivImageTweet = itemView.findViewById(R.id.ivImageTweet);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvNumRetweet = itemView.findViewById(R.id.tvNumRetweet);
            ibFavourite = itemView.findViewById(R.id.ibFavourite);
            tvFavouriteCount = itemView.findViewById(R.id.tvFavouriteCount);
        }

        public void bind(Tweet tweet)
        {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.name);
            tvTime.setText(getRelativeTimeAgo(tweet.createdAt));
            tvHandle.setText("@"+tweet.user.screenName);
            String retText = String.valueOf(tweet.numRetweet);
            tvNumRetweet.setText(retText);
            String likesText = String.valueOf(tweet.numLikes);
            tvFavouriteCount.setText(likesText);

            if(tweet.isFavorited)
            {
                Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart);
                ibFavourite.setImageDrawable(newImage);
            }
            else{
                Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                ibFavourite.setImageDrawable(newImage);
            }
            Glide.with(context).load(tweet.user.profileImageUrl).centerCrop().transform(new RoundedCorners(70)).into(ivProfileImage);
            if(tweet.ImgUrl != null)
            {
                Glide.with(context).load(tweet.ImgUrl).centerCrop().transform(new RoundedCorners(40)).into(ivImageTweet);
                ivImageTweet.setVisibility(View.VISIBLE);
            }
            else{
                ivImageTweet.setVisibility(View.GONE);
            }

            ibFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if not already favourited
                    if(!tweet.isFavorited)
                    {
//                        hard: tell twitter we want to favourite this
                        TwitterApp.getRestClient(context).favorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter","This should have been favorited, go check!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });


                        //   tell twitter I want to favourite this
                        //  change the drawable to btn_star_on
                        tweet.isFavorited = true;
                        Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart);
                        ibFavourite.setImageDrawable(newImage);

//                        increase number of likes
                        tweet.numLikes +=1;
                        tvFavouriteCount.setText(String.valueOf(tweet.numLikes));
                    }

//                  else if already favourited
                    else
                    {
                        TwitterApp.getRestClient(context).unfavorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter","This should have been unfavorited, go check!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });

                        tweet.isFavorited = false;
                        Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                        ibFavourite.setImageDrawable(newImage);

                        tweet.numLikes -=1;
                        tvFavouriteCount.setText(String.valueOf(tweet.numLikes));
                    }
//                        tell twitter I want to unfavourite this
//                        change the drawable back to btn_star_big_off
                }
            });
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


}
