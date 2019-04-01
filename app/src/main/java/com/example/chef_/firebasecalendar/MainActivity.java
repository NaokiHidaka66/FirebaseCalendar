package com.example.chef_.firebasecalendar;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.CoreComponentFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.chef_.firebasecalendar.model.Schedule;
import com.example.chef_.firebasecalendar.viewholder.ScheduleViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private long mDate;
    private String mDateString;
    private CompactCalendarView compactCalendarView;

    private DatabaseReference mDatabase;

    private List<Event> mList;
    private long listDate;

    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mList = new ArrayList<>();
        bindList();

        addButton = findViewById(R.id.addbutton);
        addButton.setVisibility(View.INVISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventAdding.class);
                intent.putExtra("date", mDate);
                startActivityForResult(intent, 0);
            }
        });

        mRecycler = findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(manager);

        //CalendarView calendarView = findViewById(R.id.calendar);
        //calendarView.set(2018, 3-1);

        Date cal = Calendar.getInstance().getTime();

        compactCalendarView = findViewById(R.id.compactcalendar_view);
        compactCalendarView.setCurrentDate(cal);
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.JAPAN);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                addButton.setVisibility(View.VISIBLE);
                mDate = dateClicked.getTime();
                Log.e("onDayClick date", String.valueOf(mDate));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });

        createView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 0){
            Event event = new Event(Color.BLUE, mDate);
            compactCalendarView.addEvent(event);
            mList.add(event);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void bindList(){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("schedule");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*
                while(dataSnapshot.child("date").getValue() != null){
                    Event event = new Event(Color.BLUE, (long)dataSnapshot.getValue());
                    mList.add(event);
                }
                */

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    long date = (long)snapshot.child("date").getValue();
                    Event event = new Event(Color.BLUE, date);
                    mList.add(event);
                }

                if(mList.size() > 0){
                    compactCalendarView.addEvents(mList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createView(){
        final Query query = FirebaseDatabase.getInstance().getReference()
                .child("schedule");


        FirebaseRecyclerOptions<Schedule> options = new FirebaseRecyclerOptions.Builder<Schedule>()
                .setQuery(query, Schedule.class)
                .build();


        mAdapter = new FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position, @NonNull Schedule model) {
                Log.e("onBindViewHolder", "is called");
                Log.e("onBindViewHolder date", String.valueOf(mDate));
                holder.bindView(model, mDate);
            }

            @NonNull
            @Override
            public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.e("onCreateViewHolder", "is called");
                return new ScheduleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_list, viewGroup, false));
            }

        };

        mRecycler.setAdapter(mAdapter);
    }
}
