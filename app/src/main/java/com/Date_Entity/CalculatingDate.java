package com.Date_Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalculatingDate {

    private SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());


    public CalculatingDate() {

    }




    public String currentDate() {

        return myFormat.format(new Date());
    }


    public long calculatingDateDifferense(String savedDate) {
        long diff = 0;
        try {
            Date date1 = myFormat.parse(savedDate);
            Date date2 = myFormat.parse(currentDate());
            if (date1 != null) {
                if (date2 != null) {
                     diff = date2.getTime() - date1.getTime();
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    public List<String> getDaysBetweenDates(String startDate) throws ParseException {
        List<String> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(Objects.requireNonNull(myFormat.parse(startDate)));

        while (calendar.getTime().before(myFormat.parse(currentDate()))) {
            String result = myFormat.format(calendar.getTime());
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        dates.add(currentDate());
        return dates;
    }
}
