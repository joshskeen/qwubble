package com.bignerdranch.qwubble;

import android.os.AsyncTask;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bphillips on 4/24/14.
 */
public class QwubbleLayerEntity extends Entity {
    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);


    private PhysicsWorld mPhysicsWorld;
    private CameraSize mCameraSize;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private TextureManager mTextureManager;
    private Scene mScene;
    private QwubbleZoomLayerEntity mZoomLayer;
    private int mFaceCount;


    public QwubbleLayerEntity(VertexBufferObjectManager vertexBufferObjectManager, TextureManager textureManager, Scene scene, PhysicsWorld physicsWorld, CameraSize cameraSize) {

        mVertexBufferObjectManager = vertexBufferObjectManager;
        mTextureManager = textureManager;
        mScene = scene;
        mPhysicsWorld = physicsWorld;
        mCameraSize = cameraSize;
    }

    @Override
    public void onAttached() {
        super.onAttached();

        final Rectangle ground = new Rectangle(0, mCameraSize.getHeight() - (2 + MainActivity.BUTTON_HEIGHT), mCameraSize.getWidth(), 2, mVertexBufferObjectManager);
        final Rectangle roof = new Rectangle(0, 0, mCameraSize.getWidth(), 2, mVertexBufferObjectManager);
        final Rectangle left = new Rectangle(0, 0, 2, mCameraSize.getHeight(), mVertexBufferObjectManager);
        final Rectangle right = new Rectangle(mCameraSize.getWidth() - 2, 0, 2, mCameraSize.getHeight(), mVertexBufferObjectManager);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);


        attachChild(ground);
        attachChild(roof);
        attachChild(left);
        attachChild(right);

        registerUpdateHandler(this.mPhysicsWorld);

        for (int i = 0; i < 8; i++) {
            int randomX = 0 + (int) (Math.random() * mCameraSize.getWidth());
            addFace(randomX, 0);
        }

    }

    private void addFace(final float x, final float y) {
        this.mFaceCount++;
        Debug.d("Faces: " + this.mFaceCount);
        Debug.d("px: " + x + ", py =" + y);

        final AnimatedSprite face;
        final Body body;

        new AsyncTask<Void, Void, TextureRegion>() {
            TextureRegion imageFromWebservice;

            @Override
            protected TextureRegion doInBackground(Void... params) {
                try {
                    ITexture mTexture = new BitmapTexture(getTextureManager(), new IInputStreamOpener() {
                        @Override
                        public InputStream open() throws IOException {
                            URL url = new URL(getCloudinaryUrl("http://fc07.deviantart.net/fs44/i/2009/086/e/3/THE_EASTER_BUNNY_SUIT_by_chuckjarman.jpg"));
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

            private String getCloudinaryUrl(String url) {
                return "http://res.cloudinary.com/dcu4qkwdf/image/fetch/w_100,h_100,r_max,c_thumb,g_face,c_fill,t_png,/" + url;
            }

            @Override
            protected void onPostExecute(TextureRegion textureRegion) {
                VertexBufferObjectManager vertexBufferObjectManager = getVertexBufferObjectManager();
                QwubbleSprite entity = new QwubbleSprite(x, y, textureRegion, getVertexBufferObjectManager());
                entity.setZoomLayer(mZoomLayer);

                Body circleBody = PhysicsFactory.createCircleBody(mPhysicsWorld, entity, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);

                attachChild(entity);
                mScene.registerTouchArea(entity);
                mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(entity, circleBody, true, true));

            }
        }.execute();

    }



    public VertexBufferObjectManager getVertexBufferObjectManager() {
        return mVertexBufferObjectManager;
    }

    public TextureManager getTextureManager() {
        return mTextureManager;
    }

    public QwubbleZoomLayerEntity getZoomLayer() {
        return mZoomLayer;
    }

    public void setZoomLayer(QwubbleZoomLayerEntity zoomLayer) {
        mZoomLayer = zoomLayer;
    }
}
