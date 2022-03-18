package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCount: TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCount = findViewById(R.id.tvCount)

        client = TwitterApplication.getRestClient(this)



        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener {
            // Grab the content of edittext (etCompose)
            val tweetContent = etCompose.text.toString() as EditText

            tweetContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    TODO("Not yet implemented")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // Count character
                    val character = tweetContent.length()
                    // Convert into String
                    val characterCount = character.toString()
                    // Set to Textview
                    tvCount.setText("Count: $characterCount")
                }

                override fun afterTextChanged(p0: Editable?) {
                    // 1. Make sure the tweet isn't empty
                    if (tweetContent.length() == 0) {
                        Toast.makeText(ComposeActivity(), "Empty tweets not allowed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    // 2. Make sure the tweet is under character count
                    if (tweetContent.length() > 280) {
                        btnTweet.isClickable = false
                        Toast.makeText(
                            ComposeActivity(),
                            "Tweet is too long! Limit is 140 characters",
                            Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        btnTweet.isClickable = true
                        // Make an API call to Twitter to publish tweet
                        client.publishTweet(tweetContent, object: JsonHttpResponseHandler() {
                            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                                Log.i(TAG, "Successfully published tweet!")
                                // Send the tweet back to TimelineActivity to show
                                val tweet = Tweet.fromJson(json.jsonObject)

                                val intent = Intent()
                                intent.putExtra("tweet", tweet)
                                setResult(RESULT_OK, intent)
                                finish()
                            }

                            override fun onFailure(
                                statusCode: Int,
                                headers: Headers?,
                                response: String?,
                                throwable: Throwable?
                            ) {
                                Log.e(TAG, "Failed to publish tweet", throwable)
                            }
                        })
                    }
                }

            })
        }
    }

    companion object {
        val TAG = "ComposeActivity"
    }
}