package com.bignerdranch.qwubble;

import com.bignerdranch.qwubble.data.QwubbleData;

public class ShowQwubbleEvent {

    public QwubbleData mQwubble;
    public QwubbleSprite mSprite;

    public ShowQwubbleEvent(QwubbleData qwubble, QwubbleSprite sprite) {
        mQwubble = qwubble;
        mSprite = sprite;
    }

}
