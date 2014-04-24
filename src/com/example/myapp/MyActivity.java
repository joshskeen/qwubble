package com.example.myapp;

import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.widget.Toast;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {
    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 720;

    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

    private BitmapTextureAtlas mBitmapTextureAtlas;

    private TiledTextureRegion mCircleFaceTextureRegion;

    private Scene mScene;

    private PhysicsWorld mPhysicsWorld;
    private int mFaceCount = 0;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Toast.makeText(this, "Touch the screen to add objects.", Toast.LENGTH_LONG).show();

        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);

    }

    @Override
    public void onCreateResources() {

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

//        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
//        this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "headtest.png", 0, 64, 2, 1); // 64x32
//
//        this.mBitmapTextureAtlas.load();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setBackground(new Background(0, 0, 0));
        this.mScene.setOnSceneTouchListener(this);

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
        final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
        final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
        final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
        final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        this.mScene.attachChild(ground);
        this.mScene.attachChild(roof);
        this.mScene.attachChild(left);
        this.mScene.attachChild(right);

        this.mScene.registerUpdateHandler(this.mPhysicsWorld);

        for(int i = 0; i < 8; i++){
            int randomX = 0 + (int)(Math.random()*CAMERA_WIDTH);
            addFace(randomX, 0);
        }

        return this.mScene;
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
//        if (this.mPhysicsWorld != null) {
//            if (pSceneTouchEvent.isActionDown()) {
//                this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
//                return true;
//            }
//        }
//        return false;
        return true;
    }

    @Override
    public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

    }

    @Override
    public void onAccelerationChanged(final AccelerationData pAccelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    @Override
    public void onResumeGame() {
        super.onResumeGame();

        this.enableAccelerationSensor(this);
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();

        this.disableAccelerationSensor();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void addFace(final float pX, final float pY) {
        this.mFaceCount++;
        Debug.d("Faces: " + this.mFaceCount);
        Debug.d("px: " + pX + ", py =" + pY);

        final AnimatedSprite face;
        final Body body;

        new AsyncTask<Void, Void, TextureRegion>(){
            TextureRegion imageFromWebservice;

            @Override
            protected TextureRegion doInBackground(Void... params) {
                try {
                    ITexture mTexture = new BitmapTexture(getTextureManager(), new IInputStreamOpener() {
                        @Override
                        public InputStream open() throws IOException {
                            URL url = new URL("http://www.floridanest.com/_/rsrc/1394128557181/Sell-your-home-with-Marie-Louise-Verbeke/ROUND%20AVATAR%20-%20ML.png?height=100&width=100");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            BufferedInputStream in = new BufferedInputStream(input);
                            return in;
                        }
                    });
                    mTexture.load();
                    imageFromWebservice = TextureRegionFactory.extractFromTexture(mTexture);

                } catch (IOException e) {
                    Debug.e(e);
                }
                return imageFromWebservice;
            }

            @Override
            protected void onPostExecute(TextureRegion textureRegion) {

                VertexBufferObjectManager vertexBufferObjectManager = getVertexBufferObjectManager();
//                face = new AnimatedSprite(pX, pY, textureRegion, this.MyActivity.getVertexBufferObjectManager());

                //create face from TextureRegion

                Sprite entity = new Sprite(pX, pY, textureRegion, getVertexBufferObjectManager());

                Body circleBody = PhysicsFactory.createCircleBody(mPhysicsWorld, entity, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);

//                face.animate(200);

                mScene.attachChild(entity);
                mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(entity, circleBody, true, true));

            }
        }.execute();


    }


}