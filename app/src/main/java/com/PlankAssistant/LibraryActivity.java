package com.PlankAssistant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rxtimertest.R;
import java.util.ArrayList;
import java.util.List;


public class LibraryActivity extends AppCompatActivity {
    ListView lv;
    TinyDb tinyDb;
    ListViewAdapter adapter;
    TextView emptyList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDb = new TinyDb(getApplicationContext());
        setContentView(R.layout.activity_library);
        lv = findViewById(R.id.lv);
        emptyList = findViewById(R.id.emptyView);
        adapter = new ListViewAdapter(tinyDbArray(), this.getApplicationContext());
        lv.setAdapter(adapter);
        lv.setEmptyView(emptyList);
    }

    public List<List<String>> tinyDbArray() {
        List<List<String>> tinyDbArray = new ArrayList<>();


        for (int i = 0; i < tinyDb.getAll().size(); i++) {
            tinyDbArray.add(tinyDb.getListString(String.valueOf(i)));
        }
        return tinyDbArray;
    }




//
//        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(lv,
//                new OnSwipeTouchListener.OnSwipeCallback() {
//                    @Override
//                    public void onSwipeLeft(ListView listView, int[] reverseSortedPositions, int position) {
//                        onSwipeEvent(position);
//
//                    }
//
//                    @Override
//                    public void onSwipeRight(ListView lv, int[] reverseSortedPositions, int position) {
//                        onSwipeEvent(position);
//                    }
//                }, false, // example : left action = dismiss
//                true); // example : right action without dismiss animation









//    static HashMap<String, ArrayList<String>> notesMap() {
//        HashMap<String, ArrayList<String>> mappy = new HashMap<>();
//
//            for (int i = 0; i < tinyDb.getAll().size(); i++) {
//                String id = String.valueOf(i);
//                mappy.put(id, tinyDb.getListString(id));
//            }
//        return mappy;
//    }

//    public static void remove(int position) {
//        tinyDb.remove(String.valueOf(position));
//        renewCatalog();
//        initialListViewMap();
//       // showWork(mappy);
//        lv.setAdapter(adapter);
//
//
//    }


//    static void renewCatalog(){
//        ArrayList<ArrayList<String>> arrayLists=new ArrayList<>();
//        Map<String,?> keys = tinyDb.getAll();
//        for(Map.Entry<String,?> entry : keys.entrySet()){
//            arrayLists.add(new ArrayList<>(Arrays.asList(TextUtils.split(entry.getValue().toString(),
//                    "‚‗‚"))));
//
//        }
//
//        tinyDb.clear();
//        mappy = new HashMap<>();
//
//        for (int i=0;i<arrayLists.size();i++){
//            tinyDb.putListString(String.valueOf(i),arrayLists.get(i));
//        }
//    }

}
