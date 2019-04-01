package com.example.chef_.firebasecalendar.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.chef_.firebasecalendar.R;
import com.example.chef_.firebasecalendar.model.Schedule;

public class ScheduleViewHolder extends RecyclerView.ViewHolder {

    private TextView dateText;
    private TextView contentText;

    public ScheduleViewHolder(View itemView){
        super(itemView);

        dateText = itemView.findViewById(R.id.datetext);
        contentText = itemView.findViewById(R.id.contentview);
    }

    public void bindView(Schedule schedule, long date){
        if(date == schedule.getDate()) {
            Log.e("if", "is true");
            dateText.setText(String.valueOf(schedule.getDate()));
            contentText.setText(schedule.getContent());
        }
    }
}
