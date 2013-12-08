package org.qeo.qeomessaging.android;

import org.qeo.DefaultEventReaderListener;
import org.qeo.EventReader;
import org.qeo.EventWriter;
import org.qeo.QeoFactory;
import org.qeo.android.QeoAndroid;
import org.qeo.android.QeoConnectionListener;
import org.qeo.exception.QeoException;
import org.qeo.qeomessaging.ChatMessage;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class QeoMessagingHelper {

	public abstract interface IQeoMessagingListener {
		public abstract void onConnected(boolean success);
		public abstract void onMessageReceived(String from, String message);
	}
	
	
	
	public QeoMessagingHelper(IQeoMessagingListener listener) {
		mListener = listener;
	}
	
	public void connect(Context context) {
		if (!mQeoActive && !mQeoConnecting) {
			initQeo(context);
		}
	}

	public void disconnect() {
        if (mWriter != null) {
            mWriter.close();
        }
        if (mReader != null) {
            mReader.close();
        }
        if (mQeo != null) {
            mQeo.close();
        }
	}
	
	// Default send
	
	public void sendMessage(String msg) {
		if (mQeoActive && (mWriter != null)) {
			ChatMessage message = new ChatMessage();
	        message.from = android.os.Build.MODEL;
	        message.message = msg;
	        mWriter.write(message);
		}
	}
	
	// Custom send for image urls
	public void sendImageUrl(String imageUrl)
	{
		sendMessage("{\"type\": \"imageSend\", \"imageUrl\": \"" + imageUrl + "\"}") ;
		
	}
	
	public void sendUserEnter(String googleAccount)
	{
		sendMessage("{\"type\": \"userEnter\", \"userId\": \"" + googleAccount + "\", \"userImage\": \"http://foo.jpg\"}");
	}
	
	public void sendUserExit(String googleAccount)
	{
		sendMessage("{\"type\": \"userEnter\", \"userId\": \"" + googleAccount + "\", \"userImage\": \"http://foo.jpg\"}");
	}
	
	
	
    private void initQeo(final Context context)
    {
        /* Start the Qeo service */
    	mQeoConnecting = true;
        QeoAndroid.initQeo(context, new QeoConnectionListener() {

            /* When the connection with the Qeo service is ready we can create our reader and writer. */
            @Override
            public void onQeoReady(QeoFactory qeo)
            {
                Log.d(TAG, "onQeoReady");
                mQeoConnecting = false;
                mQeo = qeo;
                try {
                    /* Create the Qeo writer and reader */
                    mReader = mQeo.createEventReader(ChatMessage.class, new MyListener());
                    mWriter = mQeo.createEventWriter(ChatMessage.class);
                    mQeoActive = true;
                    mListener.onConnected(true);
                }
                catch (final QeoException e) {
                    Log.e(TAG, "Error creating Qeo reader/writer", e);
                    mListener.onConnected(false);
                    mQeoActive = false;
                }
            }

            @Override
            public void onQeoError(QeoException ex)
            {
            	mQeoConnecting = false;
                mQeoActive = false;
                mListener.onConnected(false);

            	if (ex.getMessage() != null) {
                    Toast
                        .makeText(context, "Qeo Service failed due to " + ex.getMessage(), Toast.LENGTH_LONG)
                        .show();
                }
                else {
                    Toast.makeText(context, "Failed to initialize Qeo Service.Exiting !", Toast.LENGTH_LONG)
                        .show();
                }
            }

            @Override
            public void onQeoClosed(QeoFactory qeo)
            {
                Log.d(TAG, "onQeoClosed");
                super.onQeoClosed(qeo);
                mQeoConnecting = false;
                mQeoActive = false;
                mReader = null;
                mWriter = null;
                mQeo = null;
            }
        });
    }
	
    private static final String TAG = "QeoMessagingHelper";
	private IQeoMessagingListener mListener;
    private QeoFactory mQeo = null;
    private EventWriter<ChatMessage> mWriter;
    private EventReader<ChatMessage> mReader;
    private boolean mQeoActive = false;
    private boolean mQeoConnecting = false;
    
    private class MyListener extends DefaultEventReaderListener<ChatMessage> {
    	@Override
    	public void onData(final ChatMessage data) {
    		QeoMessagingHelper.this.mListener.onMessageReceived(data.from, data.message);
    	}
    }
    
}
