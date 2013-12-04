package com.example.qeohelloworld;

import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;

public class ytCallback implements PlaybackEventListener {

	@Override
	public void onBuffering(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaused() {
		Toast
        .makeText(null, "Video Paused", Toast.LENGTH_LONG)
        .show();
		Log.d("VIDEO PAUSED","PAUSE");
		
	}

	@Override
	public void onPlaying() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSeekTo(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopped() {
		// TODO Auto-generated method stub
		
	}
	

}
