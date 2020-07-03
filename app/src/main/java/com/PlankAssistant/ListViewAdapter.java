package com.PlankAssistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.rxtimertest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListViewAdapter extends BaseAdapter {
    private final ArrayList<Map.Entry<String, ArrayList<String>>> mData;
    private Context context;



    ListViewAdapter(Map<String, ArrayList<String>> map, Context context) {
        mData = new ArrayList<>();
        mData.addAll(map.entrySet());
        this.context = context;

    }


    @Override
    public int getCount() {

        return mData.size();

    }

    @Override
    public Map.Entry<String, ArrayList<String>> getItem(int position) {
        return mData.get(position);
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

        Map.Entry<String, ArrayList<String>> item = getItem(position);

        try {

            ((TextView) result.findViewById(R.id.colTrainingDay)).setText(String.format(context.getString(R.string.DayString), Integer.parseInt(item.getKey()) + 1));
            ((TextView) result.findViewById(R.id.colDate)).setText(item.getValue().get(0));
            ((TextView) result.findViewById(R.id.colTimeResult)).setText(item.getValue().get(1));

            if ((!item.getValue().get(1).equals(item.getValue().get(2)))||(item.getValue().get(1).equals("00:00"))){
                ((TextView) result.findViewById(R.id.colTimeResult)).
                        setTextColor(ContextCompat.getColor(context,R.color.colorFire));
            }



        } catch (IndexOutOfBoundsException aie) {
            ((TextView) result.findViewById(R.id.colTimeResult)).setText(R.string.Error);
        }
        return result;
    }








}
