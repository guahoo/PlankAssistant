package com.PlankAssistant;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rxtimertest.R;

public class EraserDialog extends CreateDialog {

    private TextView messageTextView;


    public void showInfoDialog(String message) {
        init();
        messageTextView.setText(message);
        d.show();
    }

    EraserDialog(Context context){
        this.context = context;
    }


    @Override
    protected void init() {
        createDialogWindow(R.layout.info_dialog_eraser,context);
        ImageButton ok_btn = d.findViewById(R.id.ok_Btn);
        messageTextView = d.findViewById(R.id.message_TextView);
        ok_btn.setOnClickListener(v-> ((MainActivity)context).resetAll());
    }


}
