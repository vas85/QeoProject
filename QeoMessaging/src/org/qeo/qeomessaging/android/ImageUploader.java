package org.qeo.qeomessaging.android;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

public class ImageUploader implements Camera.PictureCallback {
	private static final int PICTURE_TIME = 5000;
	private Camera mCamera;
	private CameraPreview mPreview;
	Handler mTimerHandler;
	Runnable mTimerRunnable;
	
	public ImageUploader() {
		mTimerRunnable = new Runnable() {
	        @Override
	        public void run() {
	            if (mCamera != null) {
	            	mCamera.takePicture(null, null, ImageUploader.this);
	            }
	            
	        	if (mTimerHandler != null) {
	            	mTimerHandler.postDelayed(this, PICTURE_TIME);
	            }
	        }
	    };
	}
	
	public void startCapturing(CameraPreview preview) {
		mTimerHandler = new Handler();
		mPreview = preview;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    int cameraCount = Camera.getNumberOfCameras();
	    int camIdx = 0;
	    for (camIdx = 0; camIdx < cameraCount; camIdx++) {
	        Camera.getCameraInfo( camIdx, cameraInfo );
	        if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
	        	
	            break;
	        }
	    }
	    safeCameraOpen(camIdx);
	    if (mTimerHandler != null) {
        	mTimerHandler.postDelayed(mTimerRunnable, PICTURE_TIME);
        }
	}
	
	public void stopCapturing() {
		if (mTimerHandler != null) {
			mTimerHandler.removeCallbacks(mTimerRunnable);
		}
		releaseCameraAndPreview();
	}
	
	private boolean safeCameraOpen(int id) {
	    boolean qOpened = false;
	  
	    try {
	        releaseCameraAndPreview();
	        mCamera = Camera.open(id);
	        mPreview.setCamera(mCamera, id);
	        qOpened = (mCamera != null);
	    } catch (Exception e) {
	        Log.e("ImageUploader", "failed to open Camera");
	        e.printStackTrace();
	    }

	    return qOpened;    
	}

	private void releaseCameraAndPreview() {
	    mPreview.setCamera(null, 0);
	    if (mCamera != null) {
	        mCamera.release();
	        mCamera = null;
	    }
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.e("ImageUploader", "Picture taken");
		if (camera != null) {
			camera.startPreview();
		}
	}
}
