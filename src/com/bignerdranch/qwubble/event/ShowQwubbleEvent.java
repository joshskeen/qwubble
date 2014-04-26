package com.bignerdranch.qwubble.event;

import com.bignerdranch.qwubble.QwubbleSprite;
import com.bignerdranch.qwubble.data.IQwubble;

public class ShowQwubbleEvent {

    public IQwubble mQwubble;
    public QwubbleSprite mSprite;

    public ShowQwubbleEvent(IQwubble qwubble, QwubbleSprite sprite) {
        mQwubble = qwubble;
        mSprite = sprite;
    }

}
