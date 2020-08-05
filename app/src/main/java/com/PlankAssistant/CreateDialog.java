package com.PlankAssistant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

abstract class CreateDialog {
    Dialog d;
    Context context;

    CreateDialog(Context context){
        this.context=context;

    }


    public abstract void showInfoDialog(String message);

    protected abstract void init();

    void createDialogWindow(int layoutRes, Context context) {
        d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = d.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        d.setContentView(layoutRes);
    }
}
