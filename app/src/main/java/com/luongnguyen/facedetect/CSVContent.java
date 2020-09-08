package com.luongnguyen.facedetect;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

import static com.luongnguyen.facedetect.MainActivity.PassClassflag;

public class CSVContent extends AppCompatActivity {

    ListView mListView;
    TextView ClassHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.csvcontent);
        mListView = findViewById(R.id.AttendList);
        CustomAdaptor customAdaptor = new CustomAdaptor();
        mListView.setAdapter(customAdaptor);
        ClassHeader = findViewById(R.id.ClassHeader);
        if(PassClassflag){
            ClassHeader.setText("CLASS LIST");
        }else{
            ClassHeader.setText("ATTENDANCE LIST");
        }

    }

    class CustomAdaptor extends BaseAdapter {

        List<StudentInfo> student = MainActivity.TempList;

        @Override
        public int getCount() {
            return student.size();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.customlayout2, null);
            TextView mID = v.findViewById(R.id.CSVID);
            TextView mName = v.findViewById(R.id.CSVName);
            TextView mStatus = v.findViewById(R.id.CSVStatus);
            TextView mDate = v.findViewById(R.id.CSVDate);
            TextView mClassID = v.findViewById(R.id.CSVClassID);


            mID.setText(student.get(position).getID());
            mName.setText(student.get(position).getName());
            mStatus.setText(student.get(position).getStatus());
            mDate.setText(student.get(position).getDate());
            mClassID.setText(student.get(position).getClassID());
            return v;
        }

    }
}