package com.example.qeohelloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PlugInControlReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

	    if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
	    	Toast
            .makeText(context, "Connected!", Toast.LENGTH_LONG)
            .show();
	        Log.d("PluginControl", "Connected!");
	    }
	    else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
	    	Toast
            .makeText(context, "Disconnected!", Toast.LENGTH_LONG)
            .show();
	    	Log.d("PluginControl", "Disconnected!");
	    }
	}
}
