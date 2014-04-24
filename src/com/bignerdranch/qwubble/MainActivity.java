package com.bignerdranch.qwubble;

import android.content.*;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import android.preference.PreferenceManager;
import android.widget.TextView;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {

    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 720;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID = "735653081262";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
    private static final String TAG = "MyACtivity";

    private BitmapTextureAtlas mBitmapTextureAtlas;

    private TiledTextureRegion mCircleFaceTextureRegion;

    private Scene mScene;

    private PhysicsWorld mPhysicsWorld;
    private int mFaceCount = 0;
    private BroadcastReceiver mGCMBroadcastReceiver;

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);

    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            public String msg = "";

            @Override
            protected Void doInBackground(Void... params) {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    Debug.d(TAG, "GCM: " + gcm);
                }
                try {
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    sendRegistrationIdToBackend(regid);
                    storeRegistrationId(context, regid);
                    Debug.d(TAG, "REGISTRATION ID: " + regid);
                } catch (Exception e) {
                    Debug.e(TAG, "SOMETHING BAD HAPPENED!!!", e);
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void sendRegistrationIdToBackend(String regid) {


        QwubbleWebservice.getService().postRegistration(regid, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                Log.d(TAG, "Posted my REGID, got response: " + response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, "FAIL!!!");
            }
        });

//        QwubbleWebservice.getService().getPing(new Callback<Void>() {
//            @Override
//            public void success(Void aVoid, Response response) {
//                Debug.d(TAG, "PING PONG YALL");
//            }
//            @Override
//            public void failure(RetrofitError retrofitError) {
//                Debug.d(TAG, "SOMETHING HORRIBLE HAPPENED");
//            }
//        });
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Debug.i(TAG, "Saving REGID!!! on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        Debug.d(TAG, "STORE THE REG ID: " + "SAVING: " + regId + ",, " + appVersion);
        editor.commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Log.e(TAG, "GCM connection Error! Result COde: " + resultCode);
            } else {
                Log.i(TAG, "---> This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public Scene onCreateScene() {

        mGCMBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                Debug.d("alert => ", intent.getExtras().getString("alert"));
                intent.getExtras().getString("time");
            }
        };

        setupPlayServices();

        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setBackground(new Background(0, 0, 0));

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        this.getVertexBufferObjectManager();

        QwubbleLayerEntity layerEntity = new QwubbleLayerEntity(getVertexBufferObjectManager(), getTextureManager(), mScene, mPhysicsWorld);

        this.mScene.attachChild(layerEntity);

        return this.mScene;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but how you store the regID in your app is up to you.
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    private static int getAppVersion(Context context) {
        return 1;
    }

    private void setupPlayServices() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            Log.d(TAG, "REGID was " + regid);
            sendRegistrationIdToBackend(regid);
            if (regid.isEmpty()) {
                Log.d(TAG, "REGID was empty");
                registerInBackground();
            }
        } else {
            Log.d(TAG, "SETUP PLAY SERVICES FAILED!!!!!!");
        }
    }


    @Override
    public boolean onSceneTouchEvent(final Scene scene, final TouchEvent sceneTouchEvent) {

        return true;
    }

    @Override
    public void onAccelerationAccuracyChanged(final AccelerationData accelerationData) {

    }

    @Override
    public void onAccelerationChanged(final AccelerationData accelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(accelerationData.getX(), accelerationData.getY());
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    @Override
    public void onResumeGame() {
        super.onResumeGame();
        this.enableAccelerationSensor(this);
        IntentFilter filter = new IntentFilter();

        filter.addAction("com.google.android.c2dm.intent.RECEIVE");
        filter.addCategory("com.bignerdranch.qwubble");

        registerReceiver(mGCMBroadcastReceiver, filter);
        Log.d(TAG, "receiver registerd!!!!");
        checkPlayServices();
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();
        this.disableAccelerationSensor();
        unregisterReceiver(mGCMBroadcastReceiver);
    }



}