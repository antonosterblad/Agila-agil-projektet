// Copyright 2007-2014 metaio GmbH. All rights reserved.
package agilec.ikeaswipe;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.io.AssetsManager;

public class ArFindStepsActivity extends ARViewActivity {


  /**
   * Reference to loaded metaioman geometry
   */
  private IGeometry mMetaioStep1;
  private IGeometry mMetaioStep2;

  /**
   * Metaio SDK callback handler
   */
  private MetaioSDKCallbackHandler mCallbackHandler;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    mCallbackHandler = new MetaioSDKCallbackHandler();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mCallbackHandler.delete();
    mCallbackHandler = null;
  }


  @Override
  protected int getGUILayout() {
    return R.layout.activity_ar_view_find_steps;
  }

  public void onButtonClick(View v) {
    finish();
  }

  /**
   * This function loads the tracking file, which contains the images which are used as trackers.
   * This function then loads the geometries which shall be used for each tracker.
   *
   * @author @emmaforsling @marcusnygren
   */
  @Override
  protected void loadContents() {
    // Load all the geometries with its corresponding texture
    mMetaioStep1 = loadModel("scanningsteps/objects/step_01.obj", "scanningsteps/textures/step00.png");
    mMetaioStep2 = loadModel("scanningsteps/objects/step_02.obj", "scanningsteps/textures/step00.png");

    //Connect a geometry to a tracking marker.
    // The coordinate ID corresponds to the patches in the XML file.

    // Set id for each models individual coordinate system
    if (mMetaioStep1 != null) {
      mMetaioStep1.setCoordinateSystemID(1); //bind the loaded geometry to this target
    }

    if (mMetaioStep2 != null) {
      mMetaioStep2.setCoordinateSystemID(2); //bind the loaded geometry to this target
    }

    // Tracking.xml defines how to track the model
    setTrackingConfiguration("scanningsteps/TrackingData_MarkerlessFast.xml");
  }

  /**
   * Load 3D model
   *
   * @param path Path to object to load
   * @return geometry
   * @author @antonosterblad @linneamalcherek @jacobselg
   */
  private IGeometry loadModel(final String path, final String texturepath) {
    IGeometry geometry = null;
    try {
      // Load model
      AssetsManager.extractAllAssets(this, true);
      final File modelPath = AssetsManager.getAssetPathAsFile(getApplicationContext(), path);
      // Log.i("info", "modelPath: " + modelPath);
      geometry = metaioSDK.createGeometry(modelPath);
      geometry.setTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(), texturepath));
      if (geometry != null) {
        // Set geometry properties
        geometry.setScale(30f);
        MetaioDebug.log("Loaded geometry " + modelPath);
      } else {
        MetaioDebug.log(Log.ERROR, "Error loading geometry: " + geometry);
      }
    } catch (Exception e) {
      MetaioDebug.log(Log.ERROR, "Error loading geometry: " + e.getMessage());
      return geometry;
    }
    return geometry;
  }

  @Override
  protected void onGeometryTouched(IGeometry geometry) {
    // TODO Auto-generated method stub
  }

  /**
   * @return
   */
  @Override
  protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
    return mCallbackHandler;
  }

  final class MetaioSDKCallbackHandler extends IMetaioSDKCallback {

    @Override
    public void onSDKReady() {
      // show GUI
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          mGUIView.setVisibility(View.VISIBLE);
        }
      });
    }

    /**
     * onTrackingEvent can be used to determine if an object has been identified
     *
     * @param trackingValues
     * @author @emmaforsling @marcusnygren
     */
    @Override
    public void onTrackingEvent(TrackingValuesVector trackingValues) {

    }

  }

  /**
   * Define how to track the 3D model
   *
   * @param path Path to which object will be used to track.
   * @return result
   * @author @antonosterblad @linneamalcherek @jacobselg
   */
  private boolean setTrackingConfiguration(final String path) {
    boolean result = false;
    try {
      // Set tracking configuration
      final File xmlPath = AssetsManager.getAssetPathAsFile(getApplicationContext(), path);
      result = metaioSDK.setTrackingConfiguration(xmlPath);
      MetaioDebug.log("Loaded tracking configuration " + xmlPath);
    } catch (Exception e) {
      MetaioDebug.log(Log.ERROR, "Error loading tracking configuration: " + path + " " + e.getMessage());
      return result;
    }
    return result;
  }

  /**
   * This function can be used to manipulate the camera in Metaio
   *
   * @user @marcusnygren
   */
  @Override
  protected void startCamera() {
    super.startCamera();

  }
}