package com.bignerdranch.qwubble;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by bphillips on 4/25/14.
 */
public class HighlightSprite extends Sprite {
    private static final float PAD = 1f;

    private final Sprite mHighlightTarget;

    public HighlightSprite(Sprite highlightTarget, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
        super(-PAD, -PAD, textureRegion, vertexBufferObjectManager);
        mHighlightTarget = highlightTarget;

        setWidth(highlightTarget.getWidth() + PAD * 2);
        setHeight(highlightTarget.getHeight() + PAD * 2);

        setRotationCenter(getHeight() / 2, getWidth() / 2);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        setRotation(-mHighlightTarget.getRotation());
    }
}
