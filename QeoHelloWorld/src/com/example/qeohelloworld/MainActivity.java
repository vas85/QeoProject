package com.example.qeohelloworld;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.qeo.DefaultEventReaderListener;
import org.qeo.EventReader;
import org.qeo.EventWriter;
import org.qeo.QeoFactory;
import org.qeo.android.QeoAndroid;
import org.qeo.android.QeoConnectionListener;
import org.qeo.exception.QeoException;

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
	private PlayerEventHandler mEventHandler = null;
	private PlugInControlReceiver mPlugInControlReceiver = null;
	    
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
    
    /**
     * This class handles the events fired when a device is connected or disconnected from power source.
     */
    private class PlugInControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
                Toast
                .makeText(context, "Connected!", Toast.LENGTH_LONG)
                .show();
                Log.d("PluginControl", "Connected!");
                mEventHandler.OnConnect();
            }
            else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                Toast
                .makeText(context, "Disconnected!", Toast.LENGTH_LONG)
                .show();
                Log.d("PluginControl", "Disconnected!");
                mEventHandler.OnDisconnect();
            }
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
    
    @Override
    protected void onResume() {
        super.onResume();
        if (mPlugInControlReceiver == null) {
            mPlugInControlReceiver = new PlugInControlReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mPlugInControlReceiver, intentFilter);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mPlugInControlReceiver != null) {
            unregisterReceiver(mPlugInControlReceiver);
        }
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
                    mEventHandler = new PlayerEventHandler("Disconnected", "Connected", "Play", mQeo);
                    mEventHandler.Init();
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
                mSendButton.setEnabled(false);
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
    	button.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v) {
        		final int id = v.getId();
                switch (id) {
                case R.id.button1:
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) v.getContext());
                    builder
                    .setTitle("Send")
                    .setMessage("Send a Message?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
                    {
                        public void onClick(DialogInterface dialog, int which) 
                        {
                        	ChatMessage message = new ChatMessage();
                            message.from = android.os.Build.MODEL;
                            message.message = mEditText.getText().toString();
                            mWriter.write(message);
                            mEditText.setText("");
                        }
                    });             
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
                    {
                        public void onClick(DialogInterface dialog, int which) 
                        {   
                        	dialog.dismiss();           
                        }
                    });         
                AlertDialog alert = builder.create();
                        alert.show();
                    break;
                }
            }	
        });    	
    }
}
