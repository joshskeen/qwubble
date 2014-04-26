package com.bignerdranch.qwubble.layer;

import android.os.AsyncTask;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.bignerdranch.qwubble.CameraSize;
import com.bignerdranch.qwubble.MainActivity;
import com.bignerdranch.qwubble.QwubbleSprite;
import com.bignerdranch.qwubble.Util;
import com.bignerdranch.qwubble.data.AnswerData;
import com.bignerdranch.qwubble.data.IQwubble;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by bphillips on 4/24/14.
 */

//THE LAYER FOR THE "ASK" TAB

public class AnswerLayerEntity extends LayerEntity {

    public static final MainActivity.QwubbleMode LAYER_MODE = MainActivity.QwubbleMode.ASK;

    public AnswerLayerEntity(VertexBufferObjectManager vertexBufferObjectManager, TextureManager textureManager, Scene scene, PhysicsWorld physicsWorld, CameraSize cameraSize, MainActivity mainActivity) {
        super(vertexBufferObjectManager, textureManager, scene, physicsWorld, cameraSize, mainActivity);
    }

    public void addAnswer(AnswerData answerData, MainActivity.QwubbleMode qwubbleMode) {
        int randomX = MainActivity.QWUBBLE_WIDTH + (int) (Math.random() * mCameraSize.getWidth() - MainActivity.QWUBBLE_WIDTH);
        addQwubble(randomX, MainActivity.QWUBBLE_WIDTH, answerData, qwubbleMode);
    }

    private void addQwubble(final float x, final float y, final IQwubble qwubble, final MainActivity.QwubbleMode qwubbleMode) {
        this.qwubbleCount++;
        Debug.d("Qwubbles: " + this.qwubbleCount);
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
                            Random rand = new Random();
                            int randomMod = rand.nextInt(100) + 1;
                            URL url = new URL(Util.getCloudinaryUrl(qwubble.getImageUrl(), MainActivity.QWUBBLE_WIDTH - randomMod));
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

                } catch (FileNotFoundException e) {
                    ITexture mTexture = null;
                    try {
                        mTexture = new BitmapTexture(getTextureManager(), new IInputStreamOpener() {
                            @Override
                            public InputStream open() throws IOException {
                                Random rand = new Random();
                                int randomMod = rand.nextInt(100) + 1;
                                URL url = new URL(Util.getCloudinaryUrl(QwubbleLayerEntity.DEFAULT_IMAGE, MainActivity.QWUBBLE_WIDTH - randomMod));
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                BufferedInputStream in = new BufferedInputStream(input);
                                return in;
                            }
                        });
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
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
                QwubbleSprite entity = new QwubbleSprite(x, y, textureRegion, getVertexBufferObjectManager(), qwubble);
                entity.setZoomLayer(mZoomLayer);
                Body circleBody = PhysicsFactory.createCircleBody(mPhysicsWorld, entity, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);

                attachChild(entity);
                mHighlighter.addHighlight(entity);
                if (qwubbleMode == LAYER_MODE) {
                    mScene.registerTouchArea(entity);
                }
                mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(entity, circleBody, true, true));
                mChildQwubbles.add(entity);
            }
        }.execute();
    }


}
