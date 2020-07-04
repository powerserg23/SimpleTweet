package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_SIZE = 140;
    EditText etCompose;
    Button btnTweet;
    TwitterClient client;
    TextView tvCharCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCharCount = findViewById(R.id.tvCharCount);
        int charCount = 0;
        tvCharCount.setText("Character Count: "+Integer.toString(0)+"/"+Integer.toString(MAX_TWEET_SIZE));

        client = TwitterApp.getRestClient(this);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetContent = etCompose.getText().toString();
                if (tweetContent.length() == 0)
                {
                    Toast.makeText(ComposeActivity.this,"Tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (tweetContent.length() > MAX_TWEET_SIZE)
                {
                    Toast.makeText(ComposeActivity.this,"Tweet too long", Toast.LENGTH_SHORT).show();
                    return;

                }
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.e(TAG,"onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG,"Published tweet says: "+tweet.body);
                            Intent intent =new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to publish tweet",throwable);
                    }
                });
            }
        });
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCharCount.setText("Character Count: "+Integer.toString(charSequence.length())+"/"+Integer.toString(MAX_TWEET_SIZE));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
