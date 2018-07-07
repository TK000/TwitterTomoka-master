package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    public List<Tweet> mTweets;
    Context context;
    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }
    private TwitterClient client;
    public boolean fav = false;

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvTimestamp.setText(tweet.createdAt);

        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);
        Glide.with(context).load(tweet.mediaUrl).into(holder.tvImage);

        client = TwitterApp.getRestClient(context);
        client.showTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                try {
                    Tweet tweet1 = Tweet.fromJSON(response);
                    Log.i("fav", String.format("%s",tweet1.favorited));
                    if (tweet1.favorited) {
                        fav = true;
                    }
                } catch (JSONException e) {
                    Log.e("retweet", "retweet failed");
                }
            }
        });

        if (fav) {
            holder.imFavorite.setImageResource(R.drawable.ic_vector_heart_filled);
        }
        else {
            holder.imFavorite.setImageResource(R.drawable.ic_vector_heart);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivProfileImage) public ImageView ivProfileImage;
        @BindView(R.id.tvUserName) public TextView tvUsername;
        @BindView(R.id.tvBody) public TextView tvBody;
        @BindView(R.id.tvTimestamp) public TextView tvTimestamp;
        @BindView(R.id.tvImage) public ImageView tvImage;
        @BindView(R.id.rLayout) public RelativeLayout rLayout;
        @BindView(R.id.ivReply) public ImageView ivReply;
        @BindView(R.id.imFavorite) public ImageView imFavorite;


        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ButterKnife.bind(this, itemView);

            rLayout.setOnClickListener(this);
            ivReply.setOnClickListener(this);
        }


        // when the user clicks on a row, show MovieDetailsActivity for the selected movie
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);
                // create intent for the new activity
                if (v == ivReply) {
                    Intent intent = new Intent(context, ReplyActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    //intent.putExtra(placeholder, config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath()));
                    // show the activity
                    context.startActivity(intent);
                    //Log.i("onclick", "tvImage clicked");
                }
                else {
                    Intent intent = new Intent(context, TweetDetailActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    //intent.putExtra(placeholder, config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath()));
                    // show the activity
                    context.startActivity(intent);
                }
            }

        }
    }


    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
