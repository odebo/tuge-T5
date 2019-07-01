package com.tuge.myapp.examples.wifiTranslator.ui;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;



/**
 * Camera 根据屏幕大小，获取最佳拍摄图片大小
 * 
 * @author xuchang02
 * 
 */
public class CameraConfigurationManager {
    private static final String TAG = CameraConfigurationManager.class.getSimpleName();

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final int TEN_DESIRED_ZOOM = 27;

    private final Context context;
    private Point screenResolution;
    private Point cameraResolution;
    private int previewFormat;
    private String previewFormatString;

    public CameraConfigurationManager(Context context) {
        this.context = context;
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    public void initFromCameraParameters(Camera camera, int width, int height) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            previewFormat = parameters.getPreviewFormat();
            previewFormatString = parameters.get("preview-format");
            Log.i(TAG, "Default preview format: " + previewFormat + '/' + previewFormatString);
            screenResolution = new Point(width, height);
            Log.i(TAG, "Screen resolution: " + screenResolution);
            cameraResolution = getCameraResolution(parameters, screenResolution);
            Log.i(TAG, "Camera resolution: " + cameraResolution);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the camera up to take preview images which are used for both preview and decoding. We detect the preview
     * format here so that buildLuminanceSource() can build an appropriate LuminanceSource subclass. In the future we
     * may want to force YUV420SP as it's the smallest, and the planar Y can be used for barcode scanning without a copy
     * in some cases.
     */
    public void setDesiredCameraParameters(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            Log.i(TAG, "Setting preview size: " + cameraResolution);
            parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
            camera.setParameters(parameters);
            setZoom(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPreviewFormat() {
        return previewFormat;
    }

    public String getPreviewFormatString() {
        return previewFormatString;
    }

    public Point getScreenResolution() {
        return screenResolution;
    }

    public Point getCameraResolution() {
        return cameraResolution;
    }

    private Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

        String previewSizeValueString = parameters.get("preview-size-values");
        // saw this on Xperia
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;

        if (previewSizeValueString != null) {
            Log.i(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }

        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the
            // screen may not be.
            cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
        }

        return cameraResolution;
    }

    // 获取最合适的预览大小
    private Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            // 此处有两种情况，横屏时拿camera.x,camera.y与screen.x,screen.y做diff
            // 竖屏时拿camera.y,camera.x与screen.x,screen.y做diff
            int newDiff;
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            } else {
                newDiff = Math.abs(newX - screenResolution.y) + Math.abs(newY - screenResolution.x);
            }

            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }
        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    private void setZoom(Camera.Parameters parameters) {

        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Bad max-zoom: " + maxZoomString);
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
//                L.e(nfe);
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }
}
