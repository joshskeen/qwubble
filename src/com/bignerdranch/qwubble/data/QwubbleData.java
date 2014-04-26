package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class QwubbleData implements Serializable, IQwubble {

    public static final String QWUBBLE_DEFAULT_IMG = "http://res.cloudinary.com/big-nerd-ranch/image/upload/v1398461928/varying_qubbles_kyc5bb.png";
    @SerializedName("image_url")
    public String imageUrl;

    public String getImageUrl() {
        if (imageUrl == null) {
            return QWUBBLE_DEFAULT_IMG;
        }
        return imageUrl;
    }
}
