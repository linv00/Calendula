package com.bvl.calendula.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bvl.calendula.DatabaseHelper;
import com.bvl.calendula.ElementAdapterMonth;
import com.bvl.calendula.R;
import com.bvl.calendula.ScrollAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MonthFragment extends Fragment implements ScrollAdapter.OnDateClickListener {

    RecyclerView scroll;
    TableLayout table;

    DatabaseHelper db_helper;
    ArrayList<String> id, name, date, day_repeat, week_repeat, time_start, time_finish, tags, text_note, pic_note, audio_note, done;
    ElementAdapterMonth adapter;

    public static MonthFragment newInstance(Calendar date) {

        Bundle args = new Bundle();
        args.putLong("date", date.getTime().getTime()); //getTime to calendar->date and getTime to date->milliseconds
        MonthFragment fragment = new MonthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_month, container, false);

        scroll = v.findViewById(R.id.scroll);
        scroll.setLayoutManager(new LinearLayoutManager(MonthFragment.this.getActivity(), RecyclerView.HORIZONTAL, false));
        scroll.setAdapter(new ScrollAdapter(this, "MONTH"));
        scroll.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);

        table = v.findViewById(R.id.monthTable);

        db_helper = new DatabaseHelper(MonthFragment.this.getActivity());
        id = new ArrayList<>();
        name = new ArrayList<>();
        date = new ArrayList<>();
        day_repeat = new ArrayList<>();
        week_repeat = new ArrayList<>();
        time_start = new ArrayList<>();
        time_finish = new ArrayList<>();
        tags = new ArrayList<>();
        text_note = new ArrayList<>();
        pic_note = new ArrayList<>();
        audio_note = new ArrayList<>();
        done = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getArguments().getLong("date"));

        setTable(table, calendar);


        View elements [] = new View[42];
        int count = 0;

        for(int i = 0; i < table.getChildCount(); i++)
        {
            TableRow row = (TableRow) table.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++)
            {
                elements[count] = (View) row.getChildAt(j);
            }
        }

        toArrays(calendar);

        return v;
    }

    public void setTable(TableLayout tableLayout, Calendar cal)
    {
        tableLayout.removeAllViews();
        int day = 0;
        int [][] days = new int[42][2]; //0 - day, 1 - color

        TypedValue value = new TypedValue(); //get colors from attr
        getContext().getTheme().resolveAttribute(R.attr.colorSecondary, value, true);
        int colorSecondary = value.data;
        getContext().getTheme().resolveAttribute(R.attr.colorOnSecondary, value, true);
        int colorOnSecondary = value.data;

        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(cal.getTime());
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstIndex = tempCal.get(Calendar.DAY_OF_WEEK) == 1 ? 6 : tempCal.get(Calendar.DAY_OF_WEEK)-2;
        tempCal.add(Calendar.DATE, -firstIndex);

        for(int i = 0; i < days.length; i++)
        {
            days[i][0] = tempCal.get(Calendar.DAY_OF_MONTH);
            days[i][1] = tempCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) ? colorSecondary : colorOnSecondary;
            tempCal.add(Calendar.DATE, 1);
        }

        for (int i = 0; i < 7; i++) {

            TableRow tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            if(i != 0) {
                tableRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));}

            for (int j = 0; j < 8; j++) {
                View element =  null;
                if (i == 0)
                {
                    element = getLayoutInflater().inflate(R.layout.element_text, null);
                    TextView date = element.findViewById(R.id.data);
                    if (j != 0) {date.setText("пн");}
                }
                else
                {
                    if(j == 0)
                    {
                        element = getLayoutInflater().inflate(R.layout.element_text, null);
                        TextView date = element.findViewById(R.id.data);
                        date.setText("22");
                    }
                    if(j > 0)
                    {
                        element = getLayoutInflater().inflate(R.layout.element_month, null);
                        TextView date = element.findViewById(R.id.day);
                        date.setText(String.valueOf(days[day][0]));
                        date.setTextColor(days[day][1]);
                        element.setTag(days[day][1] == colorSecondary ? String.valueOf(date.getText()) : "NULL");

                        element.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getContext(), (String) view.getTag() , Toast.LENGTH_SHORT).show();
                            }
                        });

                        day++;
                    }
                }

                tableRow.addView(element);
            }

            tableLayout.addView(tableRow, i);
        }
    }

    void toArrays(Calendar desiredDate){
        Cursor cursor = db_helper.read(desiredDate);
        if(cursor.getCount() != 0)
        {
            while(cursor.moveToNext())
            {
                this.id.add(cursor.getString(0));
                this.name.add(cursor.getString(1));
                this.date.add(cursor.getString(2));
                this.day_repeat.add(cursor.getString(3));
                this.week_repeat.add(cursor.getString(4));
                this.time_start.add(cursor.getString(5));
                this.time_finish.add(cursor.getString(6));
                this.tags.add(cursor.getString(7));
                this.text_note.add(cursor.getString(8));
                this.pic_note.add(cursor.getString(9));
                this.audio_note.add(cursor.getString(10));
                this.done.add(cursor.getString(11));
            }
        }
    }

    @Override
    public void onDateClick(int position) {
        this.id.clear();
        this.name.clear();
        this.date.clear();
        this.day_repeat.clear();
        this.week_repeat.clear();
        this.time_start.clear();
        this.time_finish.clear();
        this.tags.clear();
        this.text_note.clear();
        this.pic_note.clear();
        this.audio_note.clear();
        this.done.clear();
        int dif = Integer.MAX_VALUE / 2 - position;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -dif);
        toArrays(calendar);

        setTable(table, calendar);
    }
}
