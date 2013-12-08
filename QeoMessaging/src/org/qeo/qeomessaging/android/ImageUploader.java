package org.qeo.qeomessaging.android;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.sql.Date;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
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
		//upload("foo", data);
		UploadAsyncTask task = new UploadAsyncTask("foo" + System.currentTimeMillis() + ".jpeg", data);
		task.execute();
		if (camera != null) {
			camera.startPreview();
		}
	}
	
	class UploadAsyncTask extends AsyncTask<Void, Void, Void>    {
		private byte[] imageBytes;
		private String filename;
		UploadAsyncTask(String filename, byte[] bytes)    {
        	this.imageBytes = bytes;
        	this.filename = filename;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) { 
            upload(this.filename, this.imageBytes);
            return null;
        }   
        
        private void upload(String filename, byte[] imageBytes) {
    		HttpURLConnection connection = null;
    		DataOutputStream outputStream = null;

    		String urlServer = "http://www.vswam.com/stage/svc/testUpload.php";
    		String lineEnd = "\r\n";
    		String twoHyphens = "--";
    		String boundary =  "*****";

    		int bytesRead, bytesAvailable, bufferSize;
    		byte[] buffer;
    		int maxBufferSize = 1*1024*1024;

    		try {
    			InputStream fileInputStream = new ByteArrayInputStream(imageBytes);
    	
    			URL url = new URL(urlServer);
    			connection = (HttpURLConnection) url.openConnection();
    	
    			// Allow Inputs & Outputs
    			connection.setDoInput(true);
    			connection.setDoOutput(true);
    			connection.setUseCaches(false);
    	
    			// Enable POST method
    			connection.setRequestMethod("POST");
    	
    			connection.setRequestProperty("Connection", "Keep-Alive");
    			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
    	
    			outputStream = new DataOutputStream( connection.getOutputStream() );
    			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
    			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + filename +"\"" + lineEnd);
    			outputStream.writeBytes(lineEnd);
    	
    			bytesAvailable = fileInputStream.available();
    			bufferSize = Math.min(bytesAvailable, maxBufferSize);
    			buffer = new byte[bufferSize];
    	
    			// Read file
    			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	
    			while (bytesRead > 0)
    			{
    				outputStream.write(buffer, 0, bufferSize);
    				bytesAvailable = fileInputStream.available();
    				bufferSize = Math.min(bytesAvailable, maxBufferSize);
    				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    			}
    	
    			outputStream.writeBytes(lineEnd);
    			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    	
    			fileInputStream.close();
    			outputStream.flush();
    			outputStream.close();
    		}
    		catch (Exception ex)
    		{
    			Log.e("ERROR!", ex.toString());
    		}
    	}
    }  
	
	
}
