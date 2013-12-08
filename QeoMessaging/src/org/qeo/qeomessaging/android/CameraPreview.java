package org.qeo.qeomessaging.android;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CameraPreview extends LinearLayout implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera mCamera;
    List<Camera.Size> mSupportedPreviewSizes;
    private Context mContext;
    
    public CameraPreview(Context context, AttributeSet set) {
        super(context, set);
        mContext = context;

        mSurfaceView = new SurfaceView(mContext);
        
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        addView(mSurfaceView);
    }
    
    public void setCamera(Camera camera, int cameraId) {
        if (mCamera == camera) { return; }
        
        stopPreviewAndFreeCamera();
        
        mCamera = camera;
        
        if (mCamera != null) {
        	setCameraDisplayOrientation((Activity)this.getContext(), cameraId, mCamera);
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = localSizes;
            requestLayout();
            
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            mCamera.startPreview();
        }
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int format, int width, int height) {
		if (mCamera != null) {
		    Camera.Parameters parameters = mCamera.getParameters();
		    parameters.setPreviewSize(mSupportedPreviewSizes.get(0).width, mSupportedPreviewSizes.get(0).height);
		    requestLayout();
		    mCamera.setParameters(parameters);
		    mCamera.startPreview();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		Log.e("CameraPreview", "surfacecreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	    stopPreviewAndFreeCamera();
	}
	
	private void stopPreviewAndFreeCamera() {
	    if (mCamera != null) {
	        mCamera.stopPreview();
	        mCamera.release();
	    
	        mCamera = null;
	    }
	}
	
	private static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
}