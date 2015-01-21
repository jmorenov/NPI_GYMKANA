package com.NPI_GYMKANA.appfotobrujula;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;



public class activity3 extends Activity implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity3);

        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                Camera.Parameters parameters = camera.getParameters();
        		parameters.setPreviewSize(width, height);
        		camera.setParameters(parameters);
                camera.startPreview();
            	startPreview();
                previewing = true;
            } catch (Exception e) {
                //e.printStackTrace();
            	Log.e("ERROR", e.toString());
            }
        }
    }
    
    boolean isPreviewRunning = false;
    /*public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {            
        if (isPreviewRunning)
        {
            camera.stopPreview();
        }

        Parameters parameters = camera.getParameters();
        Display display = this.getWindowManager().getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(height, width);                           
            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(width, height);                           
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            parameters.setPreviewSize(height, width);               
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(width, height);
            camera.setDisplayOrientation(180);
        }

        camera.setParameters(parameters);
        previewCamera();                      
    }*/

    public void previewCamera()
    {        
        try 
        {           
            camera.setPreviewDisplay(surfaceHolder);          
            camera.startPreview();
            isPreviewRunning = true;
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview", e);    
        }
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
	        camera = Camera.open();
	        //camera.setDisplayOrientation(90);
	        camera.setPreviewDisplay(surfaceHolder);
	    } catch (Exception e) {
	    }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
    
    public boolean isLandscape()
    {
    	if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
    		return true;
    	return false;
    }
    
    String TAG = "CAMERA";
    public void startPreview() {
        try {
            Log.i(TAG, "starting preview");

            // ....
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(0, camInfo);
            int cameraRotationOffset = camInfo.orientation;
            // ...

            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            Camera.Size previewSize = null;
            float closestRatio = Float.MAX_VALUE;
            
            int targetPreviewWidth = isLandscape() ?  getWindowManager().getDefaultDisplay().getWidth() : getWindowManager().getDefaultDisplay().getHeight();
            int targetPreviewHeight = isLandscape() ? getWindowManager().getDefaultDisplay().getHeight() :  getWindowManager().getDefaultDisplay().getWidth();
            float targetRatio = targetPreviewWidth / (float) targetPreviewHeight;

            Log.v(TAG, "target size: " + targetPreviewWidth + " / " + targetPreviewHeight + " ratio:" + targetRatio);
            for (Camera.Size candidateSize : previewSizes) {
                float whRatio = candidateSize.width / (float) candidateSize.height;
                if (previewSize == null || Math.abs(targetRatio - whRatio) < Math.abs(targetRatio - closestRatio)) {
                    closestRatio = whRatio;
                    previewSize = candidateSize;
                }
            }

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
            }
            int displayRotation;
            /*if (isFrontFacingCam) {
                displayRotation = (cameraRotationOffset + degrees) % 360;
                displayRotation = (360 - displayRotation) % 360; // compensate
                                                                    // the
                                                                    // mirror
            } else { // back-facing*/
                displayRotation = (cameraRotationOffset - degrees + 360) % 360;
            //}

            Log.v(TAG, "rotation cam / phone = displayRotation: " + cameraRotationOffset + " / " + degrees + " = "
                    + displayRotation);

            this.camera.setDisplayOrientation(displayRotation);

            int rotate;
            /*if (isFrontFacingCam) {
                rotate = (360 + cameraRotationOffset + degrees) % 360;
            } else {*/
                rotate = (360 + cameraRotationOffset - degrees) % 360;
            //}

            Log.v(TAG, "screenshot rotation: " + cameraRotationOffset + " / " + degrees + " = " + rotate);

            Log.v(TAG, "preview size: " + previewSize.width + " / " + previewSize.height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setRotation(rotate);
            camera.setParameters(parameters);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

            Log.d(TAG, "preview started");

            //started = true;
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }
}