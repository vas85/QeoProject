package com.example.qeohelloworld;

import org.qeo.DefaultEventReaderListener;
import org.qeo.EventReader;
import org.qeo.EventWriter;
import org.qeo.QeoFactory;
import org.qeo.android.QeoAndroid;
import org.qeo.android.QeoConnectionListener;
import org.qeo.exception.QeoException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.youtubeapidemo.DeveloperKey;
import com.examples.youtubeapidemo.R;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class MainActivity extends Activity {

	private static final String TAG = "QSimpleChat";
	private EventWriter<ChatMessage> mWriter;
	private EventReader<ChatMessage> mReader;
	private QeoFactory mQeo = null;
	private Button mSendButton;
	private EditText mEditText;
	private TextView mTextView;
	private ScrollView mScrollViewTextChat;
	private boolean mQeoClosed = false;
	    
	/**
     * This class that extends DefaultEventReaderListener to be able to override the method onData. The method OnData is
     * called when a message has been sent.
     */
    public class MyListener
        extends DefaultEventReaderListener<ChatMessage>
    {

        @Override
        public void onData(final ChatMessage data)
        {
            mTextView.append(data.from + "@says: " + data.message + "\n");

            // This line scroll the view to see the last message sent 
            mScrollViewTextChat.smoothScrollTo(0, mTextView.getBottom());
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button1);
        mEditText = (EditText)findViewById(R.id.editText1);
        mScrollViewTextChat = (ScrollView)findViewById(R.id.scrollChatbox);
        mTextView = (TextView) findViewById(R.id.textView);
        initButton(button);
        initQeo();
    }

    private void initQeo()
    {
        // Start the Qeo service 
        QeoAndroid.initQeo(getApplicationContext(), new QeoConnectionListener() {

            // When the connection with the Qeo service is ready we can create our reader and writer. 
            @Override
            public void onQeoReady(QeoFactory qeo)
            {
                mQeo = qeo;
                try {
                    // Create the Qeo writer and reader 
                    mReader = mQeo.createEventReader(ChatMessage.class, new MyListener());
                    mWriter = mQeo.createEventWriter(ChatMessage.class);
                }
                catch (final QeoException e) {
                }
                mQeoClosed = false;
            }

            @Override
            public void onQeoError(QeoException ex)
            {
                if (ex.getMessage() != null) {
                    Toast
                        .makeText(MainActivity.this, "Qeo Service failed due to " + ex.getMessage(), Toast.LENGTH_LONG)
                        .show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed to initialize Qeo Service.Exiting !", Toast.LENGTH_LONG)
                        .show();
                }
                finish();
            }

            @Override
            public void onQeoClosed(QeoFactory qeo)
            {
                super.onQeoClosed(qeo);
  //              mSendButton.setEnabled(false);
                mQeoClosed = true;
                mReader = null;
                mWriter = null;
                mQeo = null;
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	private void initButton(Button button) {
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				ChatMessage message = new ChatMessage();
				message.from = android.os.Build.MODEL;
				message.message = mEditText.getText().toString();
				mWriter.write(message);
				Intent intent;
				 intent = YouTubeStandalonePlayer.createVideoIntent(
				          MainActivity.this, DeveloperKey.DEVELOPER_KEY, message.message, 0 , true, false);
				
				
				
				Bundle b = new Bundle();
				b.putInt("key", 1); //Your id
				intent.putExtras(b); //Put your id to your next Intent
				startActivity(intent);
				finish();
				
				mEditText.setText("");
			}

		});
	}
}
