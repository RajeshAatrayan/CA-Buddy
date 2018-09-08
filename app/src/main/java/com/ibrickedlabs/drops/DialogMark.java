package com.ibrickedlabs.drops;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;

import android.widget.Button;
import android.widget.ImageButton;

import com.ibrickedlabs.drops.adapters.CompleteListner;

/**
 * Created by RajeshAatrayan on 30-08-2018.
 */

public class DialogMark extends DialogFragment {
    private ImageButton clooseButton;
    private Button markButton;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.markCompleted:
                    markAsComplete();
                    break;

                case R.id.close_Button:
                    dismiss();
                    break;

            }
            dismiss();
        }
    };
    private CompleteListner mListener;

    private void markAsComplete() {

        Bundle bundle = getArguments();

        if (mListener != null && bundle != null) {
            int pos = bundle.getInt("POSITION");
            mListener.onComplete(pos);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clooseButton = (ImageButton) view.findViewById(R.id.markCloseButton);
        markButton = (Button) view.findViewById(R.id.markCompleted);
        markButton.setOnClickListener(clickListener);
    clooseButton.setOnClickListener(clickListener);


    }

    public void setCompleteListener(CompleteListner completeListner) {
        mListener = completeListner;
    }
}
