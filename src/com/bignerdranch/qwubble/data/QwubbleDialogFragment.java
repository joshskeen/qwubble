package com.bignerdranch.qwubble.data;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bignerdranch.qwubble.R;
import com.bignerdranch.qwubble.Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class QwubbleDialogFragment extends DialogFragment {

    public static final String QWUBBLE_DATA = "QwubbleData";
    private TextView mTextView;
    private View mAnswersView;
    private QwubbleData mQwubbleData;
    private ImageView mImageView;
    private View mAnswerQuestionButton;

    public static QwubbleDialogFragment newInstance(QwubbleData data) {

        QwubbleDialogFragment qwubbleDialogFragment = new QwubbleDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QWUBBLE_DATA, data);
        qwubbleDialogFragment.setArguments(bundle);
        return qwubbleDialogFragment;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater i = getActivity().getLayoutInflater();

        View view = i.inflate(R.layout.qwubble_dialog_fragment, null);

        loadQwubbleImage();
        mTextView = (TextView) view.findViewById(R.id.qwubbleQuestion);
        mImageView = (ImageView) view.findViewById(R.id.qwubbleImage);
        mAnswersView = (ListView) view.findViewById(R.id.qwubbleAnswers);
        mAnswerQuestionButton = view.findViewById(R.id.answerQuestionButton);

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        return b.create();
    }

    private void loadQwubbleImage() {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return downloadImage(Util.getCloudinaryUrl(mQwubbleData.mUrl, 200));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                mImageView.setImageBitmap(bitmap);

            }
        }.execute();
    }

    private Bitmap downloadImage(String url) {
        //---------------------------------------------------
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
            Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
        }
        return bm;
        //---------------------------------------------------
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQwubbleData = (QwubbleData) getArguments().getSerializable(QWUBBLE_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
