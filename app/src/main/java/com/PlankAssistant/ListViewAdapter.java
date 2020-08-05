package com.PlankAssistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.rxtimertest.R;
import java.util.List;


public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<List<String>> fromLibraryActivity;


    ListViewAdapter(List<List<String>> tempArray, Context context) {
        fromLibraryActivity = tempArray;
        this.context = context;
    }


    @Override
    public int getCount() {
        return fromLibraryActivity.size();
    }

    @Override
    public List<String> getItem(int position) {
        return fromLibraryActivity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.lv, parent, false);
        } else {
            result = convertView;
        }

        List<String> item = getItem(position);

        try {

            ((TextView) result.findViewById(R.id.colTrainingDay)).setText(String.valueOf(position+1));
            ((TextView) result.findViewById(R.id.colDate)).setText(item.get(0));
            ((TextView) result.findViewById(R.id.colTimeResult)).setText(item.get(1));

            if ((!item.get(1).equals(item.get(2)))||(item.get(1).equals("00:00"))){
                ((TextView) result.findViewById(R.id.colTimeResult)).
                        setTextColor(ContextCompat.getColor(context,R.color.colorFire));
            }



        } catch (Exception aie) {
            ((TextView) result.findViewById(R.id.colTimeResult)).setText(R.string.Error);
        }
        return result;
    }








}
