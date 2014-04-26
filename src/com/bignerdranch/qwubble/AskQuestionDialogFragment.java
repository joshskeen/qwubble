package com.bignerdranch.qwubble;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bignerdranch.qwubble.data.QuestionData;
import com.bignerdranch.qwubble.event.ZoomOutEvent;
import com.bignerdranch.qwubble.web.QwubbleWebservice;
import de.greenrobot.event.EventBus;
import org.andengine.util.debug.Debug;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AskQuestionDialogFragment extends DialogFragment {

    public static final String REG_ID = "REG_ID";
    private static final String TAG = "AskQuestionDialogFragment";
    private static String mRegID;
    private EditText mAnswerQuestionEditText;
    private Button askQuestionButton;

    public static AskQuestionDialogFragment newInstance(String regId) {
        AskQuestionDialogFragment qwubbleDialogFragment = new AskQuestionDialogFragment();
        Bundle bundle = new Bundle();
        mRegID = regId;
        bundle.putSerializable(REG_ID, mRegID);
        qwubbleDialogFragment.setArguments(bundle);
        return qwubbleDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        getDialog().getWindow().setBackgroundDrawable(d);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        EventBus.getDefault().post(new ZoomOutEvent());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegID = getArguments().getString(REG_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRegID = getArguments().getString(REG_ID);
        LayoutInflater i = getActivity().getLayoutInflater();
        View view = i.inflate(R.layout.ask_question_dialog, null);

        mAnswerQuestionEditText = (EditText) view.findViewById(R.id.mAnswerQuestionEditText);
        askQuestionButton = (Button) view.findViewById(R.id.askQuestionButton);
        askQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitQuestion();
            }
        });

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        }
                );
        AlertDialog alertDialog = b.create();
        return alertDialog;
    }

    //get the qwubble answers, and display them in the list
    private void submitQuestion() {
        QwubbleWebservice.getService().postQuestion(mRegID, mAnswerQuestionEditText.getText().toString(), new Callback<QuestionData>() {
            @Override
            public void success(QuestionData questionData, Response response) {
                Toast.makeText(getActivity(), "Question Asked!", Toast.LENGTH_LONG).show();
                dismiss();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Debug.e(TAG, "Posting question failed!", retrofitError);
            }
        });
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
