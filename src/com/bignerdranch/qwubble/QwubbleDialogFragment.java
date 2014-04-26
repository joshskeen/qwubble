package com.bignerdranch.qwubble;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.bignerdranch.qwubble.data.AnswerData;
import com.bignerdranch.qwubble.data.IQwubble;
import com.bignerdranch.qwubble.event.ZoomOutEvent;
import com.bignerdranch.qwubble.web.QwubbleWebservice;
import de.greenrobot.event.EventBus;
import org.andengine.util.debug.Debug;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class QwubbleDialogFragment extends DialogFragment {

    public static final String QWUBBLE_DATA = "QwubbleData";
    private static final String TAG = "QwubbleDialogFragment";
    private static String mRegID;
    private TextView mQuestionText;
    private ListView mAnswersView;
    private IQwubble mIQwubble;
    private ImageView mImageView;
    private View mAnswerQuestionButton;
    private EditText mAnswerQuestionEditText;
    private TextView noAnswersFound;
    private ArrayAdapter<AnswerData> mAnswerDataArray;

    public static QwubbleDialogFragment newInstance(IQwubble iQwubble, String regId) {
        QwubbleDialogFragment qwubbleDialogFragment = new QwubbleDialogFragment();
        Bundle bundle = new Bundle();
        mRegID = regId;
        bundle.putSerializable(QWUBBLE_DATA, iQwubble);
        qwubbleDialogFragment.setArguments(bundle);
        return qwubbleDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (getDialog() == null){
//            return;
//        }
//        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
//        Drawable d = new ColorDrawable(Color.BLACK);
//        d.setAlpha(130);
//        getDialog().getWindow().setBackgroundDrawable(d);
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

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_activity, container, false);

        loadQwubbleAnswers();
        loadQwubbleImage();

        mQuestionText = (TextView) view.findViewById(R.id.qwubbleQuestion);
        mImageView = (ImageView) view.findViewById(R.id.qwubbleImage);
        mAnswersView = (ListView) view.findViewById(R.id.qwubbleAnswers);
        mAnswerQuestionButton = view.findViewById(R.id.answerQuestionButton);
        mAnswerQuestionEditText = (EditText) view.findViewById(R.id.mAnswerQuestionEditText);
//        noAnswersFound = (TextView) view.findViewById(R.id.noAnswersFound);
//        mAnswersView.setEmptyView(noAnswersFound);

        if (mIQwubble.getQuestion() == null) {
            System.out.println(mIQwubble);
            System.out.println(mIQwubble);
        }
        mQuestionText.setText(mIQwubble.getQuestion());
        Debug.d("QUBBLE ID: " + mIQwubble.getId());

        mAnswerQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QwubbleWebservice.getService().postAnswer(mIQwubble.getId(), mRegID, mAnswerQuestionEditText.getText().toString(), new Callback<Void>() {
                    @Override
                    public void success(Void aVoid, Response response) {
                        mAnswerQuestionEditText.setText("");
                        Toast.makeText(getActivity(), "Answer Posted!", Toast.LENGTH_LONG).show();
                        dismiss();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(TAG, "FAILURE");
                    }
                });
            }
        });

        return view;
    }


//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        LayoutInflater i = getActivity().getLayoutInflater();
//        mIQwubble = (IQwubble) getArguments().getSerializable(QWUBBLE_DATA);
//        View view = i.inflate(R.layout.qwubble_dialog_fragment, null);
//
//
//        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
//                .setView(view)
//                .setNegativeButton("Close",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dismiss();
//                            }
//                        }
//                );
//        AlertDialog alertDialog = b.create();
//        return alertDialog;
//    }

    //get the qwubble answers, and display them in the list
    private void loadQwubbleAnswers() {

        QwubbleWebservice.getService().getAnswers(mIQwubble.getId(), new Callback<List<AnswerData>>() {
            @Override
            public void success(List<AnswerData> answerDatas, Response response) {
                if (answerDatas != null && answerDatas.size() > 0) {
                    ArrayAdapter<AnswerData> arrayAdapter = new ArrayAdapter<AnswerData>(getActivity(), R.layout.answer_item, answerDatas) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                                convertView = layoutInflater.inflate(R.layout.answer_item, parent, false);
                            }
                            TextView answerText = (TextView) convertView.findViewById(R.id.mAnswerDisplayTV);
                            answerText.setText(getItem(position).answer);
                            return convertView;
                        }
                    };
                    mAnswersView.setAdapter(arrayAdapter);
                    mAnswersView.setSelection(arrayAdapter.getCount() - 1);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Debug.e(TAG, "OH NO!!!! - retrgoit error: " + retrofitError.getCause());
            }
        });

    }

    private void loadQwubbleImage() {

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
