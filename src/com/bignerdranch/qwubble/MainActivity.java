package com.bignerdranch.qwubble;

import android.content.*;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.bignerdranch.qwubble.data.GCMAnswerResponse;
import com.bignerdranch.qwubble.data.GCMQuestionResponse;
import com.bignerdranch.qwubble.data.QuestionData;
import com.bignerdranch.qwubble.event.ShowAnswerEvent;
import com.bignerdranch.qwubble.event.ShowQwubbleEvent;
import com.bignerdranch.qwubble.event.ZoomOutEvent;
import com.bignerdranch.qwubble.layer.AnswerLayerEntity;
import com.bignerdranch.qwubble.layer.LayerEntity;
import com.bignerdranch.qwubble.layer.QwubbleLayerEntity;
import com.bignerdranch.qwubble.web.QwubbleWebservice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.greenrobot.event.EventBus;
import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
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
    private static final int REQUEST_SHOW_ANSWER = 5;
    private static final int REQUEST_SHOW_QUESTION = 10;
    String SENDER_ID = "735653081262";
    public static final int QWUBBLE_WIDTH = 70;
    public LayerEntity mActiveLayer;

    public static final int getQwubbleWidth() {
        return mCameraSize.getWidth() / 8;
    }

    private static final Color ACCENT_COLOR = new Color(0.5f, 0.42f, 0.35f);

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    private static CameraSize mCameraSize;
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

    private List<LayerEntity> mLayers = new ArrayList<LayerEntity>();
    private List<PhysicsWorld> mPhysicsWorlds = new ArrayList<PhysicsWorld>();
    private Rectangle mAddQuestionButton;
    private Text mAddQuestionText;
    public static float DENSITY;
    public static float ZOOMED_SIZE;
    public static float ZOOMED_CENTER_X;
    public static float ZOOMED_CENTER_Y;

    @Override
    public EngineOptions onCreateEngineOptions() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DENSITY = metrics.density;

        // calculate where the dialog images will go
        int imageWidthDp = 119;
        float imageWidthPx = DENSITY * imageWidthDp;
        int imageTopGapDp = 23;
        float imageTopGapPx = DENSITY * imageTopGapDp;

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        ZOOMED_SIZE = imageWidthPx;
        ZOOMED_CENTER_X = widthPixels / 2;
        ZOOMED_CENTER_Y = imageTopGapPx + imageWidthPx / 2;

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
                Gson gson = new Gson();
                JsonObject asJsonObject1 = new JsonParser().parse(data).getAsJsonObject();
                String type = asJsonObject1.get("type").getAsString();
                if (type.equals("question_creation_notification")) {
                    GCMQuestionResponse response = gson.fromJson(data, GCMQuestionResponse.class);
                    mQwubbleLayerEntity.addQuestion(response.mQuestionData, mActiveLayer);
                    if (!regid.equals(response.mQuestionData.registrationId)) {
                        Crouton.makeText(MainActivity.this, "New Question: " + response.mQuestionData.getQuestion(), Style.INFO).show();
                    }
                } else if (type.equals("answer_creation_notification")) {
                    GCMAnswerResponse response = gson.fromJson(data, GCMAnswerResponse.class);
                    Crouton.makeText(MainActivity.this, response.mAnswerData.getQuestion() + " : " + response.mAnswerData.getAnswer(), Style.CONFIRM).show();
                    mAnswerLayerEntity.addAnswer(response.mAnswerData, mActiveLayer);
                    Debug.d(TAG, "!!!!!!!");
                } else {
                    Debug.d(TAG, "NOTHING!");
                }
                selectLayer(mActiveLayer);

                System.out.println("----> " + data);
            }
        };

        setupPlayServices();

        // find out where to put the zoomed qwubbles

        float zoomedWidthPixels = DENSITY * 125;

        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setBackground(new Background(0.19f, 0.18f, 0.16f));
        this.mScene.setOnSceneTouchListener(this);

        Highlighter highlighter = new Highlighter(this, getTextureManager());

        mQwubbleLayerEntity = new QwubbleLayerEntity(getVertexBufferObjectManager(), getTextureManager(), mScene,
                newPhysicsWorld(), mCameraSize, this);
        mAnswerLayerEntity = new AnswerLayerEntity(getVertexBufferObjectManager(), getTextureManager(), mScene,
                newPhysicsWorld(), mCameraSize, this);

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
        mZoomLayer.setZoomSpriteWidth(ZOOMED_SIZE);
        mZoomLayer.setZoomSpriteX(ZOOMED_CENTER_X);
        mZoomLayer.setZoomSpriteY(ZOOMED_CENTER_Y);

        int buttonWidth = mCameraSize.getWidth() / 2;
        int offset = 0;


        mAnswerButton2 = new Rectangle(0, mCameraSize.getHeight() - (BUTTON_HEIGHT + offset), buttonWidth, BUTTON_HEIGHT, getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                updateQwubbleMode(QwubbleMode.ANSWER);
                return true;
            }
        };

        Log.i(TAG, "ZOOM BUTTON 2 WIDTH: " + mAnswerButton2.getWidth());

        mAnswerButtonText2 = new Text(0, 18, this.mFont, "Answer", new TextOptions(HorizontalAlign.RIGHT), this.getVertexBufferObjectManager());
        mAnswerButtonText2.setX(mAnswerButton2.getWidth() / 2 - mAnswerButtonText2.getWidth() / 2);
        mAnswerButton2.setColor(ACCENT_COLOR);

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
        this.mScene.attachChild(mAnswerLayerEntity);

        int buttonPadding = 40;

        mAddQuestionButton = new Rectangle(buttonPadding, 80, mCameraSize.getWidth() - (buttonPadding * 2), BUTTON_HEIGHT, getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    AskQuestionDialogFragment.newInstance(regid).show(getFragmentManager(), "ASK_QUESTION_DIALOG");
                    return true;
                }
                return true;
            }
        };

        mAddQuestionText = new Text(0, 20, this.mFont, "Ask A Question..", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
        mAddQuestionButton.attachChild(mAddQuestionText);
        mAddQuestionButton.setColor(ACCENT_COLOR);

        mAnswerLayerEntity.attachChild(mAddQuestionButton);
        mAddQuestionText.setX(mAddQuestionButton.getWidth() / 2 - mAddQuestionText.getWidth() / 2);
        this.mScene.attachChild(zoomLayerEntity);

        updateQwubbleMode(mQwubbleMode);
        return this.mScene;
    }

    private void addDebugQuestions() {
        QuestionData data = new QuestionData();
        data.imageUrl = "http://res.cloudinary.com/demo/image/fetch/w_300,h_300,r_300,c_thumb,g_face,c_fill,/http://fc07.deviantart.net/fs44/i/2009/086/e/3/THE_EASTER_BUNNY_SUIT_by_chuckjarman.jpg";
        data.question = "hey";

        mQwubbleLayerEntity.addQuestion(data, mActiveLayer);
    }


    public void selectLayer(LayerEntity entity) {
        mActiveLayer = entity;

        for (LayerEntity layer : mLayers) {
            layer.setVisible(false);
            layer.disableTouchChildSprites();
        }

        mActiveLayer.setVisible(true);
        mActiveLayer.enableTouchChildSprites();
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
            mAnswerButton2.setColor(ACCENT_COLOR);
            mAskButtonText2.setAlpha(0.3f);
            mAskButton2.setColor(Color.WHITE);
            selectLayer(mQwubbleLayerEntity);
            mScene.unregisterTouchArea(mAddQuestionButton);
        } else {
            mAnswerButtonText2.setAlpha(0.3f);
            mAnswerButton2.setColor(Color.WHITE);
            mAskButtonText2.setAlpha(1.0f);
            mAskButton2.setColor(ACCENT_COLOR);
            selectLayer(mAnswerLayerEntity);
            mScene.registerTouchArea(mAddQuestionButton);
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

    private boolean isResumed = false;

    @Override
    public void onResumeGame() {
        super.onResumeGame();

        if (isResumed) {
            return;
        }
        isResumed = true;

        this.enableAccelerationSensor(this);
        EventBus.getDefault().register(this);

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
        Log.d(TAG, "UNREGISTER RECEIVER");
        unregisterReceiver(mGCMBroadcastReceiver);
        isResumed = false;
    }

    public void onEvent(final ZoomOutEvent event) {
        mZoomLayer.zoomOut();
    }

    public void onEvent(final ShowQwubbleEvent event) {
        Debug.d(TAG, "SHOW A QWUBBLE");
        mZoomLayer.zoomToSprite(event.mSprite, event.mQwubble);
        mZoomLayer.setZoomListener(new ZoomLayerEntity.ZoomListener() {
            @Override
            public void onZoomComplete(ZoomSprite zoomSprite, Object zoomData) {
//                Intent i = QwubbleDialogActivity.getIntent(MainActivity.this, regid, event.mQwubble);
//                int code;
//
//                if (event.mQwubble instanceof AnswerData) {
//                    code = REQUEST_SHOW_ANSWER;
//                } else {
//                    code = REQUEST_SHOW_QUESTION;
//                }
//                startActivityForResult(i, code);
                QwubbleDialogFragment.newInstance(event.mQwubble, regid, event.mSprite.getBitmap()).show(getFragmentManager(), "QWUBBLE_DIALOG_FRAGMENT");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SHOW_ANSWER:
                selectLayer(mAnswerLayerEntity);
                break;
            case REQUEST_SHOW_QUESTION:
                selectLayer(mQwubbleLayerEntity);
                break;
        }
        mZoomLayer.zoomOut();

    }

    public enum QwubbleMode {
        ANSWER,
        ASK
    }

    public void onEvent(final ShowAnswerEvent event) {
        Debug.d(TAG, "SHOW A QWUBBLE");
        mZoomLayer.zoomToSprite(event.mQwubbleSprite, event.mQwubble);
        mZoomLayer.setZoomListener(new ZoomLayerEntity.ZoomListener() {
            @Override
            public void onZoomComplete(ZoomSprite zoomSprite, Object zoomData) {
                AnswerDialogFragment.newInstance(event.mQwubble, regid, event.mQwubbleSprite.getBitmap()).show(getFragmentManager(), "QWUBBLE_DIALOG_FRAGMENT");
            }
        });
    }

}
