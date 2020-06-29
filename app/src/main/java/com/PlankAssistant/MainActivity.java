package com.PlankAssistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rxtimertest.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.PlankAssistant.NotifyTimerChooseDialog.NOTIFY_TIME;


public class MainActivity extends AppCompatActivity {
    private static final String TRAININGDAYSTRING = "%s день тренировки";
    private static final String SUCCESS = "ПОЗДРАВЛЯЕМ, ЦЕЛЬ ДОСТИГНУТА!";
    private static final String FAILURE = "НЕ ПОЛУЧИЛОСЬ? ПОПРОБУЙТЕ ЕЩЕ РАЗ ИЛИ УМЕНЬШИТЕ ВРЕМЯ";
    private static final String DATEPATTERN = "dd-MM-yyyy";
    private static final String TRAININGDAY = "TRAININGDAY";
    private static final String TRAININGDATE = "TRAININGDATE";
    protected static final String PREFERENCES = "PREFERENCES";
    private static final String BEGINNINGTIME = "BEGINNINGTIME";
    private static final String ADDITIONALTIME = "ADDITIONALTIME";
    private static final String TIMEPATTERN = "%02d:%02d";
    private static final int MILLSINMINUTE= 60000;
    private static final String FIRSTTIME = "firstTime";


    TextView timerTextView, millisecondsTextView,currentTrainingDay,beforeStartingCount,tommorowTime,todayTime;
    ImageButton startPlankButton;
    ProgressBar minuteProgress;
    SharedPreferences sPrefs;
    ImageButton settingsButton,resetButton,archiveButton,notifyButton;
    ImageView backgroundStopWatch;
    TinyDb tinyDb;
    LinearLayout mainLayout;
    Info_dialog info_dialog;
    Dialog_Timer_Choose dialog_timer_choose;
    EraserDialog eraserdialog;
    CountDownTimer countDownTimer;
    NotifyTimerChooseDialog notifyTimerChooseDialog;

    private @NonNull Disposable observable;

    private boolean isTimerRunning;
    int trainingDayCount;
    long additionalTime;
    int countTimer;
    long timeout;
    boolean firstTime;







    String currentDate() {
        DateFormat formatter = new SimpleDateFormat(DATEPATTERN, Locale.getDefault());
        return formatter.format(new Date());

    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sPrefs = this.getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        timerTextView = findViewById(R.id.timer_text_view);
        millisecondsTextView = findViewById(R.id.millisecondsTextView);
        startPlankButton = findViewById(R.id.button_start_plank);
        currentTrainingDay = findViewById(R.id.currentTrainingDay);
        settingsButton = findViewById(R.id.settingsButton);
        minuteProgress = findViewById(R.id.mProgress);
        beforeStartingCount = findViewById(R.id.beforeStartingCount);
        backgroundStopWatch = findViewById(R.id.backGroundStopWathch);
        resetButton = findViewById(R.id.resetButton);
        archiveButton = findViewById(R.id.archiveButton);
        todayTime = findViewById(R.id.todayTime);
        tommorowTime = findViewById(R.id.tomorrowTime);
        minuteProgress.setMax(60000);
        firstTime = sPrefs.getBoolean("firstTime",true);
        notifyButton = findViewById(R.id.notifyButton);


        dialog_timer_choose=new Dialog_Timer_Choose(this);
        info_dialog = new Info_dialog(this);
        eraserdialog= new EraserDialog(this);
        notifyTimerChooseDialog = new NotifyTimerChooseDialog(this);

        tinyDb = new TinyDb(this.getApplicationContext());
        mainLayout = findViewById(R.id.mainLayout);








        settingsButton.setOnClickListener(v-> dialog_timer_choose.showMenuDialog());


        resetButton.setOnClickListener(v-> eraserdialog.showInfoDialog("СБРОСИТЬ ВСЕ?"));

        archiveButton.setOnClickListener(v-> startActivity(new Intent(this,LibraryActivity.class)));

        notifyButton.setOnClickListener(v->notifyTimerChooseDialog.showInfoDialog(""));


        if (sPrefs.getInt(TRAININGDAY,0)==0&& firstTime){
            dialog_timer_choose.showMenuDialog();
            SharedPreferences.Editor e = sPrefs.edit();
            e.putBoolean(FIRSTTIME, false);
            e.putString(TRAININGDATE,currentDate());
            e.putLong(NOTIFY_TIME,0);
            e.apply();
        }

        sPrefs.edit().putString(TRAININGDATE,currentDate()).apply();


        startPlankButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                countDownTimer.start();
                visibleInterface(View.INVISIBLE);
            } else {
                stopPlank();
            }
        });

        currentTrainingDay.setText(String.format(TRAININGDAYSTRING,
                String.valueOf(trainingDayCount()+1)));

        renewAdditionalTimeTexView();

        initialPreTimer();


    }

    private void progressingPlankTime() {
        if(!currentDate().equals(sPrefs.getString(TRAININGDATE, null))) {
            additionalTime = sPrefs.getLong(ADDITIONALTIME, 0);
            SharedPreferences.Editor e = sPrefs.edit();
            e.putLong(BEGINNINGTIME, sPrefs.getLong(BEGINNINGTIME, 0)+additionalTime);
            e.apply();
            timeout = sPrefs.getLong(BEGINNINGTIME, 0);

        } else {
            additionalTime = sPrefs.getLong(ADDITIONALTIME, 0);
            timeout = sPrefs.getLong(BEGINNINGTIME, 0);

        }
    }


    void resetAll() {
        SharedPreferences.Editor e = sPrefs.edit();
        e.clear().apply();
        tinyDb.clear();
        startActivity(new Intent(this,MainActivity.class));
    }

    private void initialPreTimer() {
        countDownTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                beforeStartingCount.setVisibility(View.VISIBLE);
                beforeStartingCount.setText(String.format(Locale.getDefault(), "%01d",
                        (int) millisUntilFinished / 1000 % 60));
            }

            @Override
            public void onFinish() {
                beforeStartingCount.setVisibility(View.INVISIBLE);
                visibleInterface(View.VISIBLE);
                startMainTimer();
            }
        };
    }

    private void startMainTimer() {
        progressingPlankTime();

        isTimerRunning = true;
        startPlankButton.setImageResource(R.drawable.ic_stopwatchbuttonstatepause);
        getObservable();
        minuteProgress.setProgress(0);
        countTimer=0;
        SharedPreferences.Editor e = sPrefs.edit();
        e.putString(TRAININGDATE,currentDate());

        e.putInt(TRAININGDAY,trainingDayCount());
        e.apply();
    }



//    private void sPrefsCreateModel() {
//        SharedPreferences.Editor editor = sPrefs.edit();
//        editor.putLong(BEGINNINGTIME, 0);
//        editor.putLong(ADDITIONALTIME, 0);
//        editor.putInt(TRAININGDAY,trainingDayCount);
//        editor.putString(TRAININGDATE,currentDate());
//        editor.putBoolean("firstTime", true);
//        editor.apply();
////        timeout = sPrefs.getLong(BEGINNINGTIME, 0) + (sPrefs.getLong(ADDITIONALTIME, 0) *
////                (sPrefs.getInt(TRAININGDAY, 0)));
//
//
//        timeout = sPrefs.getLong(BEGINNINGTIME, 0);
//    }

    private void stopPlank() {
        isTimerRunning = false;
        startPlankButton.setImageResource(R.drawable.button_states);
        observable.dispose();
        putResult(date_time_list());




        if (!timerTextView.getText().toString().equals(timeLeftFormattedMinutesSeconds(timeout,TIMEPATTERN))){
            showInfo(FAILURE);
        } else {
            showInfo(SUCCESS);
        }
    }

    private void showInfo(String result) {

        info_dialog.showInfoDialog(result);
    }

    public String timeLeftFormattedMinutesSeconds(long mtimeleftminutes, String pattern) {
        int minutes = (int) mtimeleftminutes / 1000 / 60;
        int seconds = (int) mtimeleftminutes / 1000 % 60;
       // int milliseconds = (int) mtimeleftminutes % 1000;
        return String.format(Locale.getDefault(), pattern,
                minutes, seconds);
    }

    private void getObservable() {
        observable = Observable.interval(1, TimeUnit.MILLISECONDS)

                .takeUntil(aLong -> aLong==timeout)
                .doOnComplete(this::runOnUiTread)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
                    timerTextView.setText(timeLeftFormattedMinutesSeconds(result, TIMEPATTERN));
                    millisecondsTextView.setText(String.format(Locale.getDefault(), ".%03d",
                            result % 1000));
                    makeProgress();
                });


    }

    private void makeProgress() {
        countTimer+=1;
        minuteProgress.setProgress(countTimer);
        if (countTimer == MILLSINMINUTE) {
            minuteProgress.setProgress(0);
            countTimer=0;
        }
    }

    int trainingDayCount(){
        trainingDayCount= sPrefs.getInt(TRAININGDAY,0);
        if(!currentDate().equals(sPrefs.getString(TRAININGDATE, null))) {
            trainingDayCount++;
        }
        return trainingDayCount;

    }

    private void runOnUiTread(){
        runOnUiThread(new Thread(this::stopPlank));
    }

    void visibleInterface(int i) {
        mainLayout.setVisibility(i);
    }

    public void renewAdditionalTimeTexView(){
        boolean sameDate = !currentDate().equals(sPrefs.getString(TRAININGDATE, null));

        String todayTiming= timeLeftFormattedMinutesSeconds(sameDate ? sPrefs.getLong(BEGINNINGTIME, 0) +
                sPrefs.getLong(ADDITIONALTIME, 0) : sPrefs.getLong(BEGINNINGTIME, 0), TIMEPATTERN);

        String tomorrowTiming = timeLeftFormattedMinutesSeconds(sPrefs.getLong(BEGINNINGTIME, 0) +
                (sameDate ? sPrefs.getLong(ADDITIONALTIME, 0) * 2 : sPrefs.getLong(ADDITIONALTIME, 0)), TIMEPATTERN);

        todayTime.setText(String.format(getString(R.string.today),todayTiming));
        tommorowTime.setText(String.format(getString(R.string.tommorow),tomorrowTiming));



    }

    void putResult(ArrayList<String>params){
        String id=String.valueOf(trainingDayCount());
        tinyDb.putListString(id,params);
    }

    private ArrayList<String> date_time_list(){

        ArrayList<String> date_time_list=new ArrayList<>();
        date_time_list.add(currentDate());
        date_time_list.add(timerTextView.getText().toString());
        date_time_list.add(timeLeftFormattedMinutesSeconds(timeout,TIMEPATTERN));

        return date_time_list;
    }




}



