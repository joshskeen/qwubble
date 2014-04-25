package com.bignerdranch.qwubble;

import com.bignerdranch.qwubble.data.IQwubble;

public class ShowQwubbleEvent {

    public IQwubble mQwubble;
    public QwubbleSprite mSprite;

    public ShowQwubbleEvent(IQwubble qwubble, QwubbleSprite sprite) {
        mQwubble = qwubble;
        mSprite = sprite;
    }

}
