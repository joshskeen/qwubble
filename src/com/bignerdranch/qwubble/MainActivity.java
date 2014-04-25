package com.bignerdranch.qwubble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.bignerdranch.qwubble.data.GCMQuestionResponse;
import com.bignerdranch.qwubble.data.IQwubble;
import com.bignerdranch.qwubble.web.QwubbleWebservice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.greenrobot.event.EventBus;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID = "735653081262";
    public static final int QWUBBLE_WIDTH = 150;

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    private CameraSize mCameraSize;
    private ZoomLayerEntity mZoomLayer;

    public String regid;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
    private static final String TAG = "MainActivity";

    private BitmapTextureAtlas mBitmapTextureAtlas;

    private Scene mScene;

    private BroadcastReceiver mGCMBroadcastReceiver;
    private TextureRegion mFromAsset;
    private Font mFont;

    private QwubbleMode mQwubbleMode = QwubbleMode.ANSWER;
    private Text mAskButtonText2;
    private Text mAnswerButtonText2;
    private Rectangle mAskButton2;
    private Rectangle mAnswerButton2;
    public static final int BUTTON_HEIGHT = 100;
    private QwubbleLayerEntity mQwubbleLayerEntity;
    private AnswerLayerEntity mAnswerLayerEntity;

    private List<Entity> mLayers = new ArrayList<Entity>();
    private List<PhysicsWorld> mPhysicsWorlds = new ArrayList<PhysicsWorld>();

    @Override
    public EngineOptions onCreateEngineOptions() {

        int widthPixels = 720;
        int heightPixels = 1280;

        CameraSize size = new CameraSize(widthPixels, heightPixels);
        mCameraSize = size;

        final Camera camera = new Camera(0, 0, size.getWidth(), size.getHeight());
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(size.getWidth(), size.getHeight()), camera);

    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas startNewGameTexture = new BitmapTextureAtlas(getTextureManager(), 32, 32, TextureOptions.BILINEAR);
        mBitmapTextureAtlas = new BitmapTextureAtlas(getTextureManager(), 32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mFromAsset = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "face_box.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mBitmapTextureAtlas);
//        FontFactory.createFromAsset(getFontManager(), getTextureManager(), bitmapTextureAtlas, this, "times.ttf", 45f, true, Color.WHITE);
        mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48);
        mFont.load();
        startNewGameTexture.load();
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
                    storeRegistrationId(context, regid);
                    sendRegistrationIdToBackend(regid);
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
                Log.d(TAG, "Success");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, "Failure");
            }
        });
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
                //handles new Questions & Answers being sent
                String data = intent.getStringExtra("qwubble");
                System.out.println(data);
                Gson gson = new Gson();
                JsonObject asJsonObject1 = new JsonParser().parse(data).getAsJsonObject();
                System.out.println(asJsonObject1);
                String type = asJsonObject1.get("type").getAsString();
                System.out.println(type);
                if (type.equals("question_creation_notification")) {
                    GCMQuestionResponse response = gson.fromJson(data, GCMQuestionResponse.class);
                    mQwubbleLayerEntity.addQuestion(response.mQuestionData);
                }else if(type.equals("answer_creation_notification")){

                } else {
                    Debug.d(TAG, "NOTHING!");
                }
                System.out.println("----> " + data);
            }
        };

        setupPlayServices();

        EventBus.getDefault().register(this);
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setBackground(new Background(0, 0, 0));
        this.mScene.setOnSceneTouchListener(this);

        Highlighter highlighter = new Highlighter(this, getTextureManager());

        mQwubbleLayerEntity = new QwubbleLayerEntity(getVertexBufferObjectManager(), getTextureManager(), mScene,
                newPhysicsWorld(), mCameraSize);
        mAnswerLayerEntity = new AnswerLayerEntity(getVertexBufferObjectManager(), getTextureManager(), mScene,
                newPhysicsWorld(), mCameraSize);

        ZoomLayerEntity zoomLayerEntity = new ZoomLayerEntity(mCameraSize, highlighter);

        mQwubbleLayerEntity.setHighlighter(highlighter);
        mQwubbleLayerEntity.setZoomLayer(zoomLayerEntity);

        mAnswerLayerEntity.setHighlighter(highlighter);
        mAnswerLayerEntity.setZoomLayer(zoomLayerEntity);

        mLayers.add(mQwubbleLayerEntity);
        mLayers.add(mAnswerLayerEntity);

        selectLayer(mQwubbleLayerEntity);
        zoomLayerEntity.setHighlighter(highlighter);
        mZoomLayer = zoomLayerEntity;


        int buttonWidth = mCameraSize.getWidth() / 2;
        int offset = 0;

        mAnswerButton2 = new Rectangle(0, mCameraSize.getHeight() - (BUTTON_HEIGHT + offset), buttonWidth, BUTTON_HEIGHT, getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                updateQwubbleMode(QwubbleMode.ANSWER);
                return true;
            }
        };

        mAnswerButtonText2 = new Text(0, 18, this.mFont, "Answer", new TextOptions(HorizontalAlign.RIGHT), this.getVertexBufferObjectManager());
        mAnswerButtonText2.setX(mAnswerButton2.getWidth() / 2 - mAnswerButtonText2.getWidth() / 2);
        mAnswerButton2.setColor(Color.GREEN);
        mAnswerButton2.attachChild(mAnswerButtonText2);

        mAskButton2 = new Rectangle(buttonWidth, mCameraSize.getHeight() - (BUTTON_HEIGHT + offset), (mCameraSize.getWidth() / 2) + 1, BUTTON_HEIGHT, getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                updateQwubbleMode(QwubbleMode.ASK);
                return true;
            }
        };

        mAskButtonText2 = new Text(0, 18, this.mFont, "Ask", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
        mAskButtonText2.setAlpha(0.3f);
        mAskButtonText2.setX(mAskButton2.getWidth() / 2 - mAskButtonText2.getWidth() / 2);
        mAskButton2.attachChild(mAskButtonText2);

        mScene.attachChild(mAnswerButton2);
        mScene.attachChild(mAskButton2);

        mScene.registerTouchArea(mAnswerButton2);
        mScene.registerTouchArea(mAskButton2);

        this.mScene.attachChild(mQwubbleLayerEntity);
        this.mScene.attachChild(zoomLayerEntity);
        return this.mScene;
    }

    private void selectLayer(Entity entity) {
        for (Entity layer : mLayers) {
            layer.setVisible(layer == entity);
        }
    }

    private PhysicsWorld newPhysicsWorld() {
        PhysicsWorld world = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        mPhysicsWorlds.add(world);

        return world;
    }


    private void updateQwubbleMode(QwubbleMode mode) {
        mQwubbleMode = mode;
        if (mode == QwubbleMode.ANSWER) {
            mAnswerButtonText2.setAlpha(1.0f);
            mAnswerButton2.setColor(Color.GREEN);
            mAskButtonText2.setAlpha(0.3f);
            mAskButton2.setColor(Color.WHITE);
        } else {
            mAnswerButtonText2.setAlpha(0.3f);
            mAnswerButton2.setColor(Color.WHITE);
            mAskButtonText2.setAlpha(1.0f);
            mAskButton2.setColor(Color.GREEN);
        }
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

        for (PhysicsWorld world : mPhysicsWorlds) {
            world.setGravity(gravity);
        }

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
        EventBus.getDefault().unregister(this);
        this.disableAccelerationSensor();
        unregisterReceiver(mGCMBroadcastReceiver);
    }

    public void onEvent(ShowQwubbleEvent event) {
        Debug.d(TAG, "SHOW A QWUBBLE");
        mZoomLayer.zoomToSprite(event.mSprite, event.mQwubble);
        mZoomLayer.setZoomListener(new ZoomLayerEntity.ZoomListener() {
            @Override
            public void onZoomComplete(ZoomSprite zoomSprite, Object zoomData) {
                IQwubble data = (IQwubble) zoomData;
                QwubbleDialogFragment.newInstance(data, regid).show(getFragmentManager(), "QWUBBLE_DIALOG_FRAGMENT");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    enum QwubbleMode {
        ANSWER,
        ASK
    }

}
