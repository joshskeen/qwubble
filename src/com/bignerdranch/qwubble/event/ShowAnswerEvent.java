package com.bignerdranch.qwubble.event;

import com.bignerdranch.qwubble.QwubbleSprite;
import com.bignerdranch.qwubble.data.IQwubble;

public class ShowAnswerEvent {
    public IQwubble mQwubble;
    public QwubbleSprite mQwubbleSprite;

    public ShowAnswerEvent(IQwubble qwubble, QwubbleSprite qwubbleSprite) {
        mQwubble = qwubble;
        mQwubbleSprite = qwubbleSprite;
    }
}
