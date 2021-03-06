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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Date_Entity.CalculatingDate;
import com.example.rxtimertest.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.PlankAssistant.NotifyTimerChooseDialog.NOTIFY_TIME;


public class MainActivity extends AppCompatActivity {
    private static final String TRAININGDAYSTRING = "%s день программы";
    private static final String SUCCESS = "ПОЗДРАВЛЯЕМ, ЦЕЛЬ ДОСТИГНУТА!";
    private static final String FAILURE = "НЕ ПОЛУЧИЛОСЬ? ПОПРОБУЙТЕ ЕЩЕ РАЗ ИЛИ УМЕНЬШИТЕ ВРЕМЯ";
    private static final String TRAININGDAY = "TRAININGDAY";
    private static final String TRAININGDATE = "TRAININGDATE";
    protected static final String PREFERENCES = "PREFERENCES";
    private static final String BEGINNINGTIME = "BEGINNINGTIME";
    private static final String ADDITIONALTIME = "ADDITIONALTIME";
    private static final String TIMEPATTERN = "%02d:%02d";
    private static final int MILLSINMINUTE = 60000;
    private static final String FIRSTTIME = "firstTime";
    private static final String HAVE_TRAINING_ALREADY = "HAVE_TRAINING_ALREADY";
    private boolean haveTrainingAlready;
    TextView timerTextView, millisecondsTextView, currentTrainingDay, beforeStartingCount, tommorowTime, todayTime;
    ImageButton startPlankButton;
    ProgressBar minuteProgress;
    SharedPreferences sPrefs;
    ImageButton settingsButton, resetButton, archiveButton, notifyButton;
    ImageView backgroundStopWatch;
    TinyDb tinyDb;
    LinearLayout mainLayout;
    Info_dialog info_dialog;
    Dialog_Timer_Choose dialog_timer_choose;
    EraserDialog eraserdialog;
    CountDownTimer countDownTimer;
    NotifyTimerChooseDialog notifyTimerChooseDialog;
    CalculatingDate calculatingDate;
    private @NonNull Disposable observable;
    private boolean isTimerRunning;
    int trainingDayCount;
    long additionalTime;
    int countTimer;
    long timeout;
    boolean firstTime;
    int id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sPrefs = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
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
        firstTime = sPrefs.getBoolean("firstTime", true);
        notifyButton = findViewById(R.id.notifyButton);


        dialog_timer_choose = new Dialog_Timer_Choose(this);
        info_dialog = new Info_dialog(this);
        eraserdialog = new EraserDialog(this);
        notifyTimerChooseDialog = new NotifyTimerChooseDialog(this);
        calculatingDate = new CalculatingDate();

        tinyDb = new TinyDb(this.getApplicationContext());
        mainLayout = findViewById(R.id.mainLayout);
        haveTrainingAlready = sPrefs.getBoolean(HAVE_TRAINING_ALREADY, false);

        settingsButton.setOnClickListener(v -> dialog_timer_choose.showInfoDialog(null));


        resetButton.setOnClickListener(v -> eraserdialog.showInfoDialog("СБРОСИТЬ ВСЕ?"));

        archiveButton.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));

        notifyButton.setOnClickListener(v -> notifyTimerChooseDialog.showInfoDialog(""));


        createsPrefsModel();


        sayHello("Hello");


        trainingDayCount = sPrefs.getInt(TRAININGDAY, 0);




        try {
            getProgrammDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        id = trainingDayCount() == 0 ? trainingDayCount() + 1 : trainingDayCount()-1;
        Toast.makeText(this,String.valueOf(id),Toast.LENGTH_LONG).show();



        startPlankButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                countDownTimer.start();
                visibleInterface(View.INVISIBLE);
            } else {
                stopPlank();
            }
        });


        currentTrainingDay.setText(String.format(TRAININGDAYSTRING, id+1));

        renewAdditionalTimeTexView();

        initialPreTimer();


    }

    private void createsPrefsModel() {
        if (sPrefs.getInt(TRAININGDAY, 0) == 0 & firstTime) {
            dialog_timer_choose.showInfoDialog(null);


            //putResult(String.valueOf(id-1),date_time_list(calculatingDate.currentDate()));
            SharedPreferences.Editor e = sPrefs.edit();
            e.putBoolean(FIRSTTIME, false);
            e.putString(TRAININGDATE, calculatingDate.currentDate());
            e.putLong(NOTIFY_TIME, 0);
            e.putBoolean(HAVE_TRAINING_ALREADY, false);
            e.apply();
        }
    }

    private void sayHello (String hello){
        Toast.makeText(this,hello,Toast.LENGTH_LONG).show();

    }

    private void getProgrammDate() throws ParseException {


             long diff = calculatingDate.calculatingDateDifferense(sPrefs.getString(TRAININGDATE, null));
             for (int i = sPrefs.getInt(TRAININGDAY, 0); i <= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS); i++) {
                putResult(String.valueOf(i), date_time_list(calculatingDate.getDaysBetweenDates(Objects.requireNonNull(sPrefs.getString(TRAININGDATE, null))).get(i)));
            }

            trainingDayCount = tinyDb.getAll().size();
            saveToSprefs(TRAININGDAY, trainingDayCount());
        }




    private void progressingPlankTime() {
        if (!calculatingDate.currentDate().equals(sPrefs.getString(TRAININGDATE, null)) && haveTrainingAlready) {
            additionalTime = sPrefs.getLong(ADDITIONALTIME, 0);
            SharedPreferences.Editor e = sPrefs.edit();
            e.putLong(BEGINNINGTIME, sPrefs.getLong(BEGINNINGTIME, 0) + additionalTime);
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

        startActivity(new Intent(this, MainActivity.class));
        tinyDb.clear();
    }

    private void initialPreTimer() {
        countDownTimer = new CountDownTimer(5000, 1000) {
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
        countTimer = 0;
        SharedPreferences.Editor e = sPrefs.edit();
        e.putString(TRAININGDATE, calculatingDate.currentDate());
        saveToSprefs(TRAININGDAY, trainingDayCount());
        e.putBoolean(HAVE_TRAINING_ALREADY, true);
        e.apply();
    }


    private void stopPlank() {
        isTimerRunning = false;
        startPlankButton.setImageResource(R.drawable.button_states);
        observable.dispose();
        putResult(String.valueOf(id), date_time_list(calculatingDate.currentDate()));
        saveToSprefs(TRAININGDAY, trainingDayCount());


        if (!timerTextView.getText().toString().equals(timeLeftFormattedMinutesSeconds(timeout, TIMEPATTERN))) {
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
        return String.format(Locale.getDefault(), pattern,
                minutes, seconds);
    }

    private void getObservable() {
        observable = Observable.interval(1, TimeUnit.MILLISECONDS)

                .takeUntil(aLong -> aLong == timeout)
                .doOnComplete(this::runOnUiTread)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
                    timerTextView.setText(timeLeftFormattedMinutesSeconds(result, TIMEPATTERN));
                    millisecondsTextView.setText(String.format(Locale.getDefault(), ".%03d",
                            result % 1000));
                    makeProgress();
                });


    }



    private void makeProgress() {
        countTimer += 1;
        minuteProgress.setProgress(countTimer);
        if (countTimer == MILLSINMINUTE) {
            minuteProgress.setProgress(0);
            countTimer = 0;
        }
    }

    int trainingDayCount() {
        return trainingDayCount;
    }

    private void runOnUiTread() {
        runOnUiThread(new Thread(this::stopPlank));
    }

    void visibleInterface(int i) {
        mainLayout.setVisibility(i);
    }

    public void renewAdditionalTimeTexView() {
        if (!calculatingDate.currentDate().equals(sPrefs.getString(TRAININGDATE, null)) & haveTrainingAlready) {

            todayTime.setText(String.format(getString(R.string.today), timeLeftFormattedMinutesSeconds(sPrefs.getLong(BEGINNINGTIME, 0) +
                    sPrefs.getLong(ADDITIONALTIME, 0), TIMEPATTERN)));
            tommorowTime.setText(String.format(getString(R.string.tommorow), timeLeftFormattedMinutesSeconds(sPrefs.getLong(BEGINNINGTIME, 0) +
                    sPrefs.getLong(ADDITIONALTIME, 0) * 2, TIMEPATTERN)));
        } else {
            todayTime.setText(String.format(getString(R.string.today), timeLeftFormattedMinutesSeconds(sPrefs.getLong(BEGINNINGTIME, 0), TIMEPATTERN)));
            tommorowTime.setText(String.format(getString(R.string.tommorow), timeLeftFormattedMinutesSeconds(sPrefs.getLong(BEGINNINGTIME, 0) +
                    sPrefs.getLong(ADDITIONALTIME, 0), TIMEPATTERN)));
        }

    }

    void putResult(String id, ArrayList<String> params) {
        tinyDb.putListString(id, params);
    }

    private ArrayList<String> date_time_list(String date) {

        ArrayList<String> date_time_list = new ArrayList<>();
        date_time_list.add(date);
        date_time_list.add(timerTextView.getText().toString());
        date_time_list.add(timeLeftFormattedMinutesSeconds(timeout, TIMEPATTERN));

        return date_time_list;
    }


    void saveToSprefs(String place,int value){
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt(place, value);
        editor.apply();

    }


}



