package com.PlankAssistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.example.rxtimertest.R;

import java.util.Calendar;


public class NotifyTimerChooseDialog extends CreateDialog{
    static final String NOTIFY_TIME = "notifyTime";

    Context context;
    private NumberPicker hoursPicker;
    private NumberPicker minutesPicker;
    private static final int NOTIFICATION_REMINDER_NIGHT = 1;
    private SharedPreferencesOperations sharedPreferencesOperations;
    private SharedPreferences sPrefs;

    NotifyTimerChooseDialog(Context context) {
        super(context);
        sharedPreferencesOperations = new SharedPreferencesOperations(context);
        sPrefs = context.getSharedPreferences(MainActivity.PREFERENCES,Context.MODE_PRIVATE);
    }


    @Override
    public void showInfoDialog(String message) {
        init();
        d.show();
    }

    @Override
    protected void init() {
        createDialogWindow(R.layout.notify_time_choose_dialog,context);
        ImageButton ok_Button = d.findViewById(R.id.ok_Btn);
        initNumberPicker();
        setNumberPickerSavedValue();

        ok_Button.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year  = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int date  = cal.get(Calendar.DATE);
            cal.clear();
            cal.set(year, month, date);

            long startDay = cal.getTimeInMillis();
            setTrainingAlarm(startDay+timeToMillss(hoursPicker.getValue(),minutesPicker.getValue()));

            sharedPreferencesOperations.putLong(NOTIFY_TIME,timeToMillss(hoursPicker.getValue(),
                    minutesPicker.getValue()));
            d.hide();
        });
    }




    private void initNumberPicker(){
       hoursPicker= d.findViewById(R.id.numberPicker);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        minutesPicker= d.findViewById(R.id.numberPicker2);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

    }

    private long timeToMillss(int timeMinutes, int timeSeconds) {
        long minutes = (long) timeMinutes * 1000 * 60*60;
        long seconds = (long) timeSeconds * 1000*60 ;
        return minutes + seconds;


    }


    private void setTrainingAlarm(long triggerTime) {


        triggerTime=triggerTime<System.currentTimeMillis()?triggerTime+1000*60*60*24:triggerTime;

        Intent notifyIntent = new Intent(context,TrainingNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context, NOTIFICATION_REMINDER_NIGHT, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  triggerTime,
                1000*60*60*24, pendingIntent);

    }

    private void setNumberPickerSavedValue(){
        long mBaseTime = sPrefs.getLong(NOTIFY_TIME,0);
        hoursPicker.setValue(timeFormattedMinutesSeconds(mBaseTime)[0]);
        minutesPicker.setValue(timeFormattedMinutesSeconds(mBaseTime)[1]);
    }

    private int [] timeFormattedMinutesSeconds(long mtimeleftminutes) {

        return new int[]{
                (int) mtimeleftminutes / 1000 / 60 / 60,
                (int) mtimeleftminutes / 1000 /60 % 60};

    }


}
