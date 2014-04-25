package com.bignerdranch.qwubble;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by bphillips on 4/25/14.
 */
public class HighlightSprite extends Sprite {

    private final Sprite mHighlightTarget;

    public HighlightSprite(Sprite highlightTarget, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
        super(0, 0, textureRegion, vertexBufferObjectManager);
        mHighlightTarget = highlightTarget;

        setWidth(highlightTarget.getWidth());
        setHeight(highlightTarget.getHeight());

        setRotationCenter(getHeight() / 2, getWidth() / 2);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        setRotation(-mHighlightTarget.getRotation());
    }
}
