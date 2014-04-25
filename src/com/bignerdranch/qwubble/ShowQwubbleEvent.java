package com.bignerdranch.qwubble;

import com.bignerdranch.qwubble.data.QwubbleData;

public class ShowQwubbleEvent {

    public QwubbleData mQwubble;

    public ShowQwubbleEvent(QwubbleData qwubble) {
        mQwubble = qwubble;
    }

}
