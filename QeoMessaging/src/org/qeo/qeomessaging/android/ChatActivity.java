/************** COPYRIGHT AND CONFIDENTIALITY INFORMATION *********************
 **                                                                          **
 ** Copyright (c) 2013 Technicolor                                           **
 ** All Rights Reserved                                                      **
 **                                                                          **
 ** This program contains proprietary information which is a trade           **
 ** secret of TECHNICOLOR and/or its affiliates and also is protected as     **
 ** an unpublished work under applicable Copyright laws. Recipient is        **
 ** to retain this program in confidence and is not permitted to use or      **
 ** make copies thereof other than as permitted in a written agreement       **
 ** with TECHNICOLOR, UNLESS OTHERWISE EXPRESSLY ALLOWED BY APPLICABLE LAWS. **
 **                                                                          **
 ******************************************************************************/

package org.qeo.qeomessaging.android;

import java.util.LinkedList;
import java.util.List;

import org.qeo.qeomessaging.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * This class that extends the android Activity class and uses the ChatMessage class to construct the chat messages that
 * will send around.
 * 
 */
public class ChatActivity
    extends Activity
    implements OnClickListener, QeoMessagingHelper.IQeoMessagingListener
{

	private LinearLayout mTopLayout;
    private Button mSendImageButton;
    private Button mUserEnterButton;
    private Button mUserExitButton;
    private Button mTemperatureButton;
    private Button mSendButton;
    private EditText mEditText;
    private TextView mTextView;
    private ScrollView mScrollViewTextChat;
    private CameraPreview mPreview;

    private String mLastSendImageText;
    private String mLastUserId;
    private String mLastTemp;
    
    private QeoMessagingHelper mQeoHelper;
    
    public QeoMessagingHelper getQeoHelper()
    {
    	return this.mQeoHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        

        setContentView(R.layout.activity_main);

        /* Associate the local variables with the right view */
        mTopLayout = (LinearLayout)findViewById(R.id.LinearLayout1);
        mSendImageButton = (Button)findViewById(R.id.sendImage);
        mUserEnterButton = (Button)findViewById(R.id.sendUserEnter);
        mUserExitButton = (Button)findViewById(R.id.sendUserExit);
        mTemperatureButton = (Button)findViewById(R.id.sendTemperature);
        
        mSendButton = (Button) findViewById(R.id.buttonSend);
        mEditText = (EditText) findViewById(R.id.editText);
        mTextView = (TextView) findViewById(R.id.textView);
        mScrollViewTextChat = (ScrollView) findViewById(R.id.scrollChatbox);

        mSendImageButton.setOnClickListener(this);
        mSendImageButton.setEnabled(false);
        mUserEnterButton.setOnClickListener(this);
        mUserEnterButton.setEnabled(false);
        mUserExitButton.setOnClickListener(this);
        mUserExitButton.setEnabled(false);
        mTemperatureButton.setOnClickListener(this);
        mTemperatureButton.setEnabled(false);
        mSendButton.setOnClickListener(this);
        mSendButton.setEnabled(false);
        
        mPreview = (CameraPreview)findViewById(R.id.cameraView);//new CameraPreview(this);

        mQeoHelper = new QeoMessagingHelper(this /* listener */);
        mQeoHelper.connect(getApplicationContext());

        
        
    }
    
  

    @Override
    public void onStart()
    {
        super.onStart();
        mQeoHelper.connect(getApplicationContext());
        
    }

    // IQeoMessagingListener
	@Override
	public void onConnected(boolean success) {
		if (!success) {
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

			dlgAlert.setMessage("Failed to connect to Qeo");
			dlgAlert.setTitle("Failed to connect!");
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(true);
			dlgAlert.create().show();
			dlgAlert.setPositiveButton("Ok",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				        	dialog.dismiss();
				        }
				    });
		} else {
			System.out.println(sendUserId());
	        System.out.println(getSSID());
	        
	        mSendImageButton.setEnabled(true);
	        mUserEnterButton.setEnabled(true);
	        mUserExitButton.setEnabled(true);
	        mTemperatureButton.setEnabled(true);
	        mSendButton.setEnabled(true);
		}
	}

	@Override
	public void onMessageReceived(String from, String message) {
        mTextView.append(from + "@says: " + message + "\n");

        /* This line scroll the view to see the last message sent */
        mScrollViewTextChat.smoothScrollTo(0, mTextView.getBottom());		
	}
	
	
	
	
    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {

            case R.id.buttonSend:
            	mQeoHelper.sendMessage(mEditText.getText().toString());
                mEditText.setText("");
                break;

            case R.id.sendImage:
            {
            	ImageUploader uploader = new ImageUploader(this.mQeoHelper);
                uploader.startCapturing(mPreview); 
                /*
            	ShowDialog("Enter Image URL", mLastSendImageText, new DialogInterface.OnClickListener() { 
            	    @Override
            	    public void onClick(DialogInterface dialog, int which) {
            	    	EditText input = (EditText) ((AlertDialog) dialog).findViewById(0x1000);
            	    	if (input != null) {
            	    		ChatActivity.this.mLastSendImageText = input.getText().toString();
            	    		ChatActivity.this.mQeoHelper.sendMessage("{\"type\": \"imageSend\", \"imageUrl\": \"" + mLastSendImageText + "\"}");
            	    	}
            	    }});
            	    */            
            }
            break;
            	
            case R.id.sendUserEnter:
            	ShowDialog("Enter User Id", mLastSendImageText, new DialogInterface.OnClickListener() { 
            	    @Override
            	    public void onClick(DialogInterface dialog, int which) {
            	    	EditText input = (EditText) ((AlertDialog) dialog).findViewById(0x1000);
            	    	if (input != null) {
            	    		ChatActivity.this.mLastUserId = input.getText().toString();
            	    		ChatActivity.this.mQeoHelper.sendMessage("{\"type\": \"userEnter\", \"userId\": \"" + mLastUserId + "\", \"userImage\": \"http://foo.jpg\"}");
            	    	}
            	    }});   
            	break;
            
            case R.id.sendUserExit:
            	ShowDialog("Enter User Id", mLastUserId, new DialogInterface.OnClickListener() { 
            	    @Override
            	    public void onClick(DialogInterface dialog, int which) {
            	    	EditText input = (EditText) ((AlertDialog) dialog).findViewById(0x1000);
            	    	if (input != null) {
            	    		ChatActivity.this.mLastUserId = input.getText().toString();
            	    		ChatActivity.this.mQeoHelper.sendMessage("{\"type\": \"userExit\", \"userId\": \"" + mLastUserId + "\"}");
            	    	}
            	    }});   
            	break;
            	
            case R.id.sendTemperature:
            	ShowDialog("Enter Temperature", mLastTemp, new DialogInterface.OnClickListener() { 
            	    @Override
            	    public void onClick(DialogInterface dialog, int which) {
            	    	EditText input = (EditText) ((AlertDialog) dialog).findViewById(0x1000);
            	    	if (input != null) {
            	    		ChatActivity.this.mLastTemp = input.getText().toString();
            	    		ChatActivity.this.mQeoHelper.sendMessage("{\"type\": \"tempChanged\", \"temp\": \"" + mLastTemp + "\"}");
            	    	}
            	    }});   
            	break;
            	
            default:
                throw new IllegalArgumentException("Unknown onclick");

        }
    }

    @Override
    protected void onDestroy()
    {
        mQeoHelper.disconnect();

        super.onDestroy();
    }
    
    private void ShowDialog(String title, String lastText, DialogInterface.OnClickListener clickListener) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(title);

    	// Set up the input
    	final EditText input = new EditText(this);
    	input.setText(lastText);
    	input.setInputType(InputType.TYPE_CLASS_TEXT);
    	input.setId(0x1000);
    	builder.setView(input);

    	// Set up the buttons
    	builder.setPositiveButton("OK", clickListener);
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        dialog.cancel();
    	    }
    	});

    	builder.show();	
 }
    
    
    
    public String sendUserId() {

  		AccountManager manager = AccountManager.get(this);
  		Account[] accounts = manager.getAccountsByType("com.google");
  		List<String> possibleEmails = new LinkedList<String>();

  		for (Account account : accounts) {
  			// TODO: Check possibleEmail against an email regex or treat
  			// account.name as an email address only for certain account.type
  			// values.
  			possibleEmails.add(account.name);
  		}

  		if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
  			String email = possibleEmails.get(0);
  			String[] parts = email.split("@");
  			if (parts.length > 0 && parts[0] != null)
  			{
  				mQeoHelper.sendUserEnter(parts[0]);
  				return parts[0];
  			}
  			else
  				return null;
  		} else
  			return null;

  	}
    
    public String getSSID()
    {
    	 WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    	   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	   Log.d("wifiInfo", wifiInfo.toString());
    	   Log.d("SSID",wifiInfo.getSSID());
    	   return wifiInfo.getBSSID();
    }
}
