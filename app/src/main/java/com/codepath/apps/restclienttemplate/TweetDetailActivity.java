package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;

    @BindView(R.id.tvdUser) TextView tvdUser;
    @BindView(R.id.tvdBody) TextView tvdBody;
    @BindView(R.id.tvdCreatedAt) TextView tvdCreatedAt;
    @BindView(R.id.ivdProfile) ImageView ivdProfile;
    @BindView(R.id.ivMedia) ImageView ivMedia;
    @BindView(R.id.btnFav) ImageView imfav;

    public int fav = 0;
    public int retweeted = 0;

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        ButterKnife.bind(this);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Log.d("TweetDetailsActivity", String.format("Showing details for '%s'", tweet.user));

        imfav.setImageResource(R.drawable.ic_vector_heart);

        // set the title and overview
        tvdUser.setText(tweet.user.name);
        tvdBody.setText(tweet.body);
        tvdCreatedAt.setText(tweet.createdAt);
        Glide.with(getApplicationContext()).load(tweet.user.profileImageUrl).into(ivdProfile);
        Glide.with(getApplicationContext()).load(tweet.mediaUrl).into(ivMedia);

        client = TwitterApp.getRestClient(getApplicationContext());
        client.showTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                try {
                    Tweet tweet1 = Tweet.fromJSON(response);
                    Log.i("fav", String.format("%s",tweet1.favorited));
                    if (tweet1.favorited) {
                        imfav.setImageResource(R.drawable.ic_vector_heart_filled);
                        fav = 1;
                    }
                    if (tweet1.retweeted) {
                        retweeted = 1;
                    }
                } catch (JSONException e) {
                    Log.e("retweet", "retweet failed");
                }
            }
        });
    }

    public void onRetweet(View v) {
        if (retweeted == 0) {
            client = TwitterApp.getRestClient(getApplicationContext());
            client.reTweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet tweet1 = Tweet.fromJSON(response);
                        TimelineActivity.tweets.add(0, tweet1);
                        TimelineActivity.tweetAdapter.notifyItemInserted(0);
                        TimelineActivity.rvTweets.scrollToPosition(0);
                        //Intent i = new Intent(TweetDetailActivity.this, TimelineActivity.class);
                        //startActivity(i);
                        finish();
                    } catch (JSONException e) {
                        Log.e("retweet", "retweet failed");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("retweet", "retweet failed");
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Already retweeted!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFav(View v) {
        imfav = (ImageView) findViewById(R.id.btnFav);
        client = TwitterApp.getRestClient(getApplicationContext());
        if (fav == 0) {
            client.favTweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //super.onSuccess(statusCode, headers, response);
                    try {
                        fav = 1;
                        Tweet tweet1 = Tweet.fromJSON(response);
                        TimelineActivity.tweets.add(0, tweet1);
                        TimelineActivity.tweetAdapter.notifyItemInserted(0);
                        imfav.setImageResource(R.drawable.ic_vector_heart_filled);
                        //Intent i = new Intent(TweetDetailActivity.this, TimelineActivity.class);
                        //startActivity(i);
                        //finish();
                    } catch (JSONException e) {
                        Log.e("favtweet", "favtweet failed");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("favtweet", "favtweet failed");
                }
            });
        }
        else {
            client.unfavTweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //super.onSuccess(statusCode, headers, response);
                    try {
                        fav = 0;
                        Tweet tweet1 = Tweet.fromJSON(response);
                        TimelineActivity.tweets.add(0, tweet1);
                        TimelineActivity.tweetAdapter.notifyItemInserted(0);
                        imfav.setImageResource(R.drawable.ic_vector_heart);
                        //Intent i = new Intent(TweetDetailActivity.this, TimelineActivity.class);
                        //startActivity(i);
                        //finish();
                    } catch (JSONException e) {
                        Log.e("favtweet", "favtweet failed");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("favtweet", "favtweet failed");
                }
            });
        }
    }
}
