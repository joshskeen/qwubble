package com.bignerdranch.qwubble;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bignerdranch.qwubble.data.IQwubble;
import com.bignerdranch.qwubble.event.ZoomOutEvent;
import de.greenrobot.event.EventBus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AnswerDialogFragment extends DialogFragment {

    public static final String QWUBBLE_DATA = "QwubbleData";
    private static final String TAG = "QwubbleDialogFragment";
    private static final String QWUBBLE_BITMAP = "QwubbleBitmap";
    private TextView mQuestionText;
    private IQwubble mIQwubble;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private TextView mAnswerText;
    private Button mCloseButton;

    public static AnswerDialogFragment newInstance(IQwubble iQwubble, String regId, Bitmap bitmap) {
        AnswerDialogFragment qwubbleDialogFragment = new AnswerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QWUBBLE_DATA, iQwubble);
        bundle.putParcelable(QWUBBLE_BITMAP, bitmap);
        qwubbleDialogFragment.setArguments(bundle);
        return qwubbleDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        EventBus.getDefault().post(new ZoomOutEvent());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIQwubble = (IQwubble) getArguments().getSerializable(QWUBBLE_DATA);
        mBitmap = (Bitmap) getArguments().getParcelable(QWUBBLE_BITMAP);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.answer_view, container, false);

        mQuestionText = (TextView) view.findViewById(R.id.qwubbleQuestion);
        mAnswerText = (TextView) view.findViewById(R.id.qwubbleAnswer);
        mImageView = (ImageView) view.findViewById(R.id.qwubbleImage);

        mQuestionText.setText(mIQwubble.getQuestion());
        mAnswerText.setText(mIQwubble.getAnswer());

        mCloseButton = (Button) view.findViewById(R.id.closeButton);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        loadQwubbleImage();
        return view;
    }

    private void loadQwubbleImage() {
        if (mBitmap != null) {
            mImageView.setImageBitmap(mBitmap);
            return;
        }

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return downloadImage(Util.getCloudinaryUrl(mIQwubble.getImageUrl(), 200));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                mImageView.setImageBitmap(bitmap);

            }
        }.execute();
    }

    private Bitmap downloadImage(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("NOOOO", "Error getting the image from server : " + e.getMessage().toString());
        }
        return bm;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
