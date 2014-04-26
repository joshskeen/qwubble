package com.bignerdranch.qwubble.layer;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.bignerdranch.qwubble.*;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class LayerEntity extends Entity {
    public static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
    protected PhysicsWorld mPhysicsWorld;
    protected CameraSize mCameraSize;
    protected MainActivity mMainActivity;
    protected VertexBufferObjectManager mVertexBufferObjectManager;
    protected TextureManager mTextureManager;
    protected Scene mScene;
    protected ZoomLayerEntity mZoomLayer;
    protected int qwubbleCount;
    protected Highlighter mHighlighter;

    protected List<QwubbleSprite> mChildQwubbles = new ArrayList<QwubbleSprite>();

    public VertexBufferObjectManager getVertexBufferObjectManager() {
        return mVertexBufferObjectManager;
    }

    public TextureManager getTextureManager() {
        return mTextureManager;
    }

    public ZoomLayerEntity getZoomLayer() {
        return mZoomLayer;
    }

    public void setZoomLayer(ZoomLayerEntity zoomLayer) {
        mZoomLayer = zoomLayer;
    }

    public void setHighlighter(Highlighter highlighter) {
        mHighlighter = highlighter;
    }

    public Highlighter getHighlighter() {
        return mHighlighter;
    }

    public LayerEntity(VertexBufferObjectManager vertexBufferObjectManager, TextureManager textureManager, Scene scene, PhysicsWorld physicsWorld, CameraSize cameraSize, MainActivity mainActivity) {
        mVertexBufferObjectManager = vertexBufferObjectManager;
        mTextureManager = textureManager;
        mScene = scene;
        mPhysicsWorld = physicsWorld;
        mCameraSize = cameraSize;
        mMainActivity = mainActivity;
    }

    public void disableTouchChildSprites(){
        for (QwubbleSprite childQwubble : mChildQwubbles) {
            mScene.unregisterTouchArea(childQwubble);
        }
    }
    public void enableTouchChildSprites(){
        for (QwubbleSprite childQwubble : mChildQwubbles) {
            mScene.registerTouchArea(childQwubble);
        }
    }

    @Override
    public void onAttached() {
        super.onAttached();
        setupPhysicsBoundaries();
    }

    protected void setupPhysicsBoundaries() {
        final Rectangle ground = new Rectangle(0, mCameraSize.getHeight() - (2 + MainActivity.getButtonHeight()), mCameraSize.getWidth(), 2, mVertexBufferObjectManager);
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
    }

    protected int getQwubbleWidth() {
        Random rand = new Random();
        int randomMod = (int)((new Random().nextInt(100) + 1) * MainActivity.DENSITY);

        return MainActivity.getQwubbleWidth() + randomMod;
    }


}
