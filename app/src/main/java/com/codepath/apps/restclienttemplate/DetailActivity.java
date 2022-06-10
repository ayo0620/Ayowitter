package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;
import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {
    Tweet tweet;

    ImageView ivDetailProfileImage;
    TextView tvUserName;
    TextView tvFullBody;
    ImageView ivDetailImageTweet;
    TextView tvTimeStamp;
    TextView ret_In_Detail;
    TextView likes_In_Detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ivDetailProfileImage = findViewById(R.id.ivDetailProfileImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvFullBody = findViewById(R.id.tvFullBody);
        ivDetailImageTweet = findViewById(R.id.ivDetailImageTweet);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        ret_In_Detail = findViewById(R.id.ret_In_Detail);
        likes_In_Detail = findViewById(R.id.likes_In_Detail);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivDetailProfileImage);
        tvUserName.setText(tweet.user.name);
        tvFullBody.setText(tweet.body);
        Glide.with(this).load(tweet.ImgUrl).centerCrop().transform(new RoundedCorners(40)).into(ivDetailImageTweet);
        tvTimeStamp.setText(tweet.date);
        ret_In_Detail.setText(tweet.numRetweet);
        likes_In_Detail.setText(tweet.numLikes);

    }
}