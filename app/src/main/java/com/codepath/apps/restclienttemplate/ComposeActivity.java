package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;

    @BindView(R.id.tvNewTweet) TextView tvNewTweet;
    @BindView(R.id.charCount) TextView charCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                charCount.setText(String.valueOf(s.length()) + "/140");
            }

            public void afterTextChanged(Editable s) {
            }
        };

        tvNewTweet.addTextChangedListener(mTextEditorWatcher);
    }

    public final int REQUEST_CODE = 20;

    public void networkRequest(View v) {
        client = TwitterApp.getRestClient(getApplicationContext());
        client.sendTweet(tvNewTweet.getText().toString(), 0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                try {
                    Tweet tweet = Tweet.fromJSON(response);

                    Intent data = new Intent();
                    data.putExtra("name", Parcels.wrap(tweet));
                    data.putExtra("code", 200); // ints work too
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish();
                } catch (JSONException e) {
                    Log.e("sendtweetjson", "sendtweet json failed");
                    //e.printStackTrace();
                }
                //Intent i = new Intent(ComposeActivity.this, TimelineActivity.class);
                //startActivityForResult(i, REQUEST_CODE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("sendTweet", "sendtweet failed");
            }
        });


    }


}
