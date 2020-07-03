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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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

    private @NonNull Disposable observable;

    private boolean isTimerRunning;
    int trainingDayCount;
    long additionalTime;
    int countTimer;
    long timeout;
    boolean firstTime;
    long diff;
    int id;
    SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());


    String currentDate() {
        return myFormat.format(new Date());
    }


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

        tinyDb = new TinyDb(this.getApplicationContext());
        mainLayout = findViewById(R.id.mainLayout);
        haveTrainingAlready = sPrefs.getBoolean(HAVE_TRAINING_ALREADY, false);

        settingsButton.setOnClickListener(v -> dialog_timer_choose.showMenuDialog());


        resetButton.setOnClickListener(v -> eraserdialog.showInfoDialog("СБРОСИТЬ ВСЕ?"));

        archiveButton.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));

        notifyButton.setOnClickListener(v -> notifyTimerChooseDialog.showInfoDialog(""));


        createsPrefsModel();


        trainingDayCount = sPrefs.getInt(TRAININGDAY, 0);

        calculateProgrammDays();

        id = trainingDayCount() == 0 ? trainingDayCount() + 1 : trainingDayCount();



        startPlankButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                countDownTimer.start();
                visibleInterface(View.INVISIBLE);
            } else {
                stopPlank();
            }
        });


        currentTrainingDay.setText(String.format(TRAININGDAYSTRING, id));

        renewAdditionalTimeTexView();

        initialPreTimer();


    }

    private void createsPrefsModel() {
        if (sPrefs.getInt(TRAININGDAY, 0) == 0 & firstTime) {
            dialog_timer_choose.showMenuDialog();

            putResult(String.valueOf(id),date_time_list(currentDate()));
            SharedPreferences.Editor e = sPrefs.edit();
            e.putBoolean(FIRSTTIME, false);
            e.putString(TRAININGDATE, currentDate());
            e.putLong(NOTIFY_TIME, 0);
            e.putBoolean(HAVE_TRAINING_ALREADY, false);
            e.apply();
        }
    }

    private void calculateProgrammDays() {

        try {
            Date date1 = myFormat.parse(Objects.requireNonNull(sPrefs.getString(TRAININGDATE, null)));
            Date date2 = myFormat.parse(currentDate());
            if (date1 != null) {
                if (date2 != null) {
                    diff = date2.getTime() - date1.getTime();
                }
            }


            for (int i = sPrefs.getInt(TRAININGDAY, 0)+1; i <= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS); i++) {
                putResult(String.valueOf(i), date_time_list(getDaysBetweenDates(date1, date2).get(i)));
            }
            trainingDayCount = tinyDb.getAll().size();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<String> getDaysBetweenDates(Date startdate, Date enddate) {
        List<String> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate)) {
            String result = myFormat.format(calendar.getTime());
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        dates.add(currentDate());
        return dates;
    }

    private void progressingPlankTime() {
        if (!currentDate().equals(sPrefs.getString(TRAININGDATE, null)) && haveTrainingAlready) {
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
        tinyDb.clear();
        startActivity(new Intent(this, MainActivity.class));
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
        e.putString(TRAININGDATE, currentDate());
        e.putInt(TRAININGDAY, trainingDayCount());
        e.putBoolean(HAVE_TRAINING_ALREADY, true);
        e.apply();
    }


    private void stopPlank() {
        isTimerRunning = false;
        startPlankButton.setImageResource(R.drawable.button_states);
        observable.dispose();
        putResult(String.valueOf(id - 1), date_time_list(currentDate()));


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
        // int milliseconds = (int) mtimeleftminutes % 1000;
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
        if (!currentDate().equals(sPrefs.getString(TRAININGDATE, null)) & haveTrainingAlready) {

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


}



