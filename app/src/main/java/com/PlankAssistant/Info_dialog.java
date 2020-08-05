package com.PlankAssistant;


import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rxtimertest.R;

class Info_dialog extends CreateDialog{
    private TextView infoTextView;


    Info_dialog(Context context){
        super(context);

    }



    public void showInfoDialog(String message) {
        init();
        infoTextView.setText(message);
        d.show();
    }


    protected void init() {
        createDialogWindow(R.layout.info_dialog, context);
        infoTextView = d.findViewById(R.id.message_TextView);
        ImageButton ok_button = d.findViewById(R.id.ok_Btn);
        ok_button.setOnClickListener(v -> d.hide());
    }



}
