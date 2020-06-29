package com.PlankAssistant;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.example.rxtimertest.R;

class Dialog_Timer_Choose extends CreateDialog{
    private Context context;
    private static final String PREFERENCES = "PREFERENCES";
    private static final String BEGINNINGTIME = "BEGINNINGTIME";
    private static final String ADDITIONALTIME = "ADDITIONALTIME";
    private SharedPreferencesOperations sharedPreferensessOperations;
    private SharedPreferences sPrefs;



    Dialog_Timer_Choose(Context context) {
        this.context = context;
        sharedPreferensessOperations = new SharedPreferencesOperations(context);
        sPrefs = context.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
    }

    void showMenuDialog() {
        init();
        d.show();
    }


    @Override
    public void showInfoDialog(String message) {

    }

    protected void init() {
        createDialogWindow(R.layout.input_time_dialog,context);
        showNumberPicker();
    }

    private void showNumberPicker() {

        ImageButton ok_Btn = d.findViewById(R.id.ok_Btn);

        int[] numberpickerids = {
                R.id.numberPicker,
                R.id.numberPicker2,
                R.id.numberPicker3,
                R.id.numberPicker4
        };

        for (int i : numberpickerids) initNumberPicker(i);

        long mBaseTime = sPrefs.getLong(BEGINNINGTIME,0);
        long mAdditionalTime = sPrefs.getLong(ADDITIONALTIME,0);

        initNumberPicker(R.id.numberPicker).setValue(timeFormattedMinutesSeconds(mBaseTime)[0]);
        initNumberPicker(R.id.numberPicker2).setValue(timeFormattedMinutesSeconds(mBaseTime)[1]);
        initNumberPicker(R.id.numberPicker3).setValue(timeFormattedMinutesSeconds(mAdditionalTime)[0]);
        initNumberPicker(R.id.numberPicker4).setValue(timeFormattedMinutesSeconds(mAdditionalTime)[1]);




        ok_Btn.setOnClickListener(v -> {

            sharedPreferensessOperations.putLong(BEGINNINGTIME,timeToMillss(initNumberPicker(R.id.numberPicker).getValue(),
                    initNumberPicker(R.id.numberPicker2).getValue()));
            sharedPreferensessOperations.putLong(ADDITIONALTIME,timeToMillss(initNumberPicker(R.id.numberPicker3).getValue(),
                    initNumberPicker(R.id.numberPicker4).getValue()));
            d.dismiss();

            ((MainActivity) context).renewAdditionalTimeTexView();
        });


    }

    private NumberPicker initNumberPicker(int id) {
        NumberPicker np = d.findViewById(id);
        np.setMinValue(0);
        np.setMaxValue(59);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        return np;
    }

    private long timeToMillss(int timeMinutes, int timeSeconds) {
        long minutes = (long) timeMinutes * 1000 * 60;
        long seconds = (long) timeSeconds * 1000 ;
        return minutes + seconds;


    }

    private int [] timeFormattedMinutesSeconds(long mtimeleftminutes) {

        return new int[]{
                (int) mtimeleftminutes / 1000 / 60,
                (int) mtimeleftminutes / 1000 % 60};

    }

}

