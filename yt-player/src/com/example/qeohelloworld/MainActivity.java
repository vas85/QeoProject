package com.example.qeohelloworld;

import org.qeo.DefaultEventReaderListener;
import org.qeo.EventReader;
import org.qeo.EventWriter;
import org.qeo.QeoFactory;
import org.qeo.android.QeoAndroid;
import org.qeo.android.QeoConnectionListener;
import org.qeo.exception.QeoException;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity implements
		OnInitializedListener {

	private static final String TAG = "QSimpleChat";
	private EventWriter<ChatMessage> mWriter;
	private EventReader<ChatMessage> mReader;
	private QeoFactory mQeo = null;
	private Button mSendButton;
	private EditText mEditText;
	private TextView mTextView;
	private ScrollView mScrollViewTextChat;
	private boolean mQeoClosed = false;
	private String mVideoId;
	YouTubePlayer mPlayer;

	/**
	 * This class that extends DefaultEventReaderListener to be able to override
	 * the method onData. The method OnData is called when a message has been
	 * sent.
	 */
	public class MyListener extends DefaultEventReaderListener<ChatMessage> {

		@Override
		public void onData(final ChatMessage data) {
			// mTextView.append(data.from + "@says: " + data.message + "\n");

			// This line scroll the view to see the last message sent
			// mScrollViewTextChat.smoothScrollTo(0, mTextView.getBottom());
			MainActivity.this.onCommand(data.message);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button) findViewById(R.id.button1);
		mEditText = (EditText) findViewById(R.id.editText1);
		// mScrollViewTextChat = (ScrollView)findViewById(R.id.scrollChatbox);
		// mTextView = (TextView) findViewById(R.id.textView);
		initButton(button);
		initQeo();
	}
	
	public void sendCommand(String command)
	{
		if (null != mQeo)
		{
			ChatMessage message = new ChatMessage();
			message.from = android.os.Build.MODEL;
			message.message = command;
			mWriter.write(message);
		}
	}
	
	public void onCommand(String command)
	{
		try
		{
			if (command == "Pause")
			{
				if (mPlayer != null)
				{
					if (mPlayer.isPlaying())
					{
						mPlayer.pause();
					}
					else
					{
						mPlayer.play();
					}
				}
			}
			else if (command == "Play")
			{
				mPlayer.play();
			}
			else if (command == "Stop")
			{
				if (mPlayer != null)
				{
					mPlayer.pause();
				}
			}
			else if (command.startsWith("url:"))
			{
				createPlayer(command.substring((4)));
			}
		}
		catch (Exception e)
		{
		
		}
	}

	private void initQeo() {
		// Start the Qeo service
		QeoAndroid.initQeo(getApplicationContext(),
				new QeoConnectionListener() {

					// When the connection with the Qeo service is ready we can
					// create our reader and writer.
					@Override
					public void onQeoReady(QeoFactory qeo) {
						mQeo = qeo;
						try {
							// Create the Qeo writer and reader
							mReader = mQeo.createEventReader(ChatMessage.class,
									new MyListener());
							mWriter = mQeo.createEventWriter(ChatMessage.class);
						} catch (final QeoException e) {
						}
						mQeoClosed = false;
					}

					@Override
					public void onQeoError(QeoException ex) {
						if (ex.getMessage() != null) {
							Toast.makeText(
									MainActivity.this,
									"Qeo Service failed due to "
											+ ex.getMessage(),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									MainActivity.this,
									"Failed to initialize Qeo Service.Exiting !",
									Toast.LENGTH_LONG).show();
						}
						finish();
					}

					@Override
					public void onQeoClosed(QeoFactory qeo) {
						super.onQeoClosed(qeo);
						// mSendButton.setEnabled(false);
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

	
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			mPlayer = player;
			mPlayer.cueVideo(mVideoId);
			mPlayer.setPlaybackEventListener(new PlaybackEventListener() {
				@Override
				public void onBuffering(boolean arg0) {
				}

				@Override
				public void onPaused() {
					Toast.makeText(MainActivity.this, "Video Paused",
							Toast.LENGTH_LONG).show();
					Log.d("VIDEO PAUSED", "PAUSE");
					MainActivity.this.sendCommand("PAUSE");
				}

				@Override
				public void onPlaying() {
					// TODO Auto-generated method stub
					MainActivity.this.sendCommand("PLAY");
				}

				@Override
				public void onSeekTo(int arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onStopped() {
					// TODO Auto-generated method stub
					MainActivity.this.sendCommand("STOP");
				}
			});
		}
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub

	}

	public void createPlayer(String key) {
		mVideoId = key;
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, MainActivity.this);
	}

	private void initButton(Button button) {
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				createPlayer(mEditText.getText().toString());
				sendCommand("url:" + mEditText.getText().toString());
				mEditText.setText("");
			}

		});
	}

}
