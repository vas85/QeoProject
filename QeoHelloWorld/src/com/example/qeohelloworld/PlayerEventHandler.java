/**
 * 
 */
package com.example.qeohelloworld;

import org.qeo.DefaultEventReaderListener;
import org.qeo.EventWriter;
import org.qeo.QeoFactory;
import org.qeo.exception.QeoException;

import android.widget.Toast;

import com.example.qeohelloworld.MainActivity.MyListener;

/**
 * @author brahm
 * This class is basically a library for handling the connect/disconnect events
 * on an Android Phone.
 * Fires different events based on the input events/receiving broadcasts.
 */
public class PlayerEventHandler
    extends DefaultEventReaderListener<ChatMessage>
{
    private String mBroadcastMessageOnDisconnect;
    private String mBroadcastMessageOnConnect;
    private String mActOnBroadcastMessage;
    private EventWriter<ChatMessage> mWriter;
    private QeoFactory mQeo;
    

    // Constructor
    public PlayerEventHandler(String broadcastMessageOnDisconnect,
                              String broadcastMessageOnConnect,
                              String playOnBroadcastMessage,
                              QeoFactory qeo) {
        this.mBroadcastMessageOnConnect = broadcastMessageOnConnect;
        this.mBroadcastMessageOnDisconnect = broadcastMessageOnDisconnect;
        this.mActOnBroadcastMessage = playOnBroadcastMessage;
        this.mQeo = qeo;
    }
    
    // Init
    public void Init() {
        try {
            // Create the Qeo writer 
            mWriter = mQeo.createEventWriter(ChatMessage.class);
            mQeo.createEventReader(ChatMessage.class, this);
        }
        catch (final QeoException e) {
        }
    }
    
    // On disconnect event
    public void OnDisconnect() {
        // Send event to topic
        ChatMessage message = new ChatMessage();
        message.from = android.os.Build.MODEL;
        message.message = this.mBroadcastMessageOnDisconnect;
        mWriter.write(message);
        System.out.println("Disconnected");
    }
    
    // On connect event
    public void OnConnect() {
        // Send event to topic
        ChatMessage message = new ChatMessage();
        message.from = android.os.Build.MODEL;
        message.message = this.mBroadcastMessageOnConnect;
        mWriter.write(message);
        System.out.println("Connected");
    }
    
    // Receive a broadcast
    @Override
    public void onData(final ChatMessage data)
    {
        ChatMessage message = new ChatMessage();
        message.from = android.os.Build.MODEL;
        if (data.message.equals(this.mActOnBroadcastMessage)) {
            // Play something.
            message.message = "Received the required event" + data.message;
            mWriter.write(message);
        } else {
            // Not a relevant message - ignore.
            
        }
        System.out.println("Received message");
    }
    
}
