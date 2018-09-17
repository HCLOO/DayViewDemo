package com.example.huangcl.myweekviewtest;

import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WeekView mWeekView;
    List<WeekViewEvent> events = new ArrayList<>();
    List<WeekViewEvent> drawEvents = new ArrayList<>();
    int mStartHourTime=0;
    int mStartMinTime=0;
    private static final int START_TIME=0;
    private static final int END_TIME=1;
    private static final int ERROR_TIME=-1;
    private Toast toast;
    boolean flag=false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeekView = (WeekView) findViewById(R.id.weekView);

        mWeekView.setEmptyViewClickListener(new WeekView.EmptyViewClickListener() {
            @Override
            public void onEmptyViewClicked(Calendar time) {
                showToast("Please set the start time by long press gesture");
            }
        });

        mWeekView.setEmptyViewLongPressListener(new WeekView.EmptyViewLongPressListener() {
            @Override
            public void onEmptyViewLongPress(Calendar time, boolean startOrEnd) {
                if(startOrEnd) {
                    Calendar startTime = Calendar.getInstance();
                    Calendar endTime = Calendar.getInstance();
                    if(mStartHourTime==time.getTime().getHours()) {
                        if(mStartMinTime<=time.getTime().getMinutes()/10*10) {
                            startTime.set(Calendar.HOUR_OF_DAY, mStartHourTime);
                            startTime.set(Calendar.MINUTE,mStartMinTime);
                            endTime.set(Calendar.HOUR_OF_DAY, time.getTime().getHours());
                            endTime.set(Calendar.MINUTE,time.getTime().getMinutes()/10*10);
                        } else {
                            startTime.set(Calendar.HOUR_OF_DAY, mStartHourTime);
                            startTime.set(Calendar.MINUTE,time.getTime().getMinutes()/10*10);
                            endTime.set(Calendar.HOUR_OF_DAY, time.getTime().getHours());
                            endTime.set(Calendar.MINUTE,mStartMinTime);
                        }
                    } else if(mStartHourTime<time.getTime().getHours()) {
                        startTime.set(Calendar.HOUR_OF_DAY, mStartHourTime);
                        startTime.set(Calendar.MINUTE,mStartMinTime);
                        endTime.set(Calendar.HOUR_OF_DAY, time.getTime().getHours());
                        endTime.set(Calendar.MINUTE,time.getTime().getMinutes()/10*10);
                    } else {
                        startTime.set(Calendar.HOUR_OF_DAY, time.getTime().getHours());
                        startTime.set(Calendar.MINUTE,time.getTime().getMinutes()/10*10);
                        endTime.set(Calendar.HOUR_OF_DAY, mStartHourTime);
                        endTime.set(Calendar.MINUTE,mStartMinTime);
                    }
                    WeekViewEvent event = new WeekViewEvent(events.size() + 1, null, startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_02));
                    if(drawEvents!=null)
                        drawEvents.clear();
                    drawEvents.add(event);
                    flag=true;
                    mWeekView.notifyDatasetChanged();
                } else {
                    mStartHourTime=time.getTime().getHours();
                    mStartMinTime=time.getTime().getMinutes()/10*10;
                }
                time.set(Calendar.MINUTE,time.getTime().getMinutes()/10*10);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                showToast(format.format(time.getTime().getTime()));
            }
        });

        mWeekView.setEventFinishDrawListener(new WeekView.EventFinishDrawListener() {
            @Override
            public void onEvenFinishDraw() {
                flag=false;
                mWeekView.notifyDatasetChanged();
            }
        });

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                if(flag)
                    return drawEvents;
                events.addAll(drawEvents);
                for(WeekViewEvent event : events) {
                    event.setColor(getResources().getColor(R.color.event_color_01));
                }
                return events;
            }
        });

        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect, Calendar currentChangeTime, int startOrEndTime) {
                switch (startOrEndTime) {
                    case START_TIME: {
                        currentChangeTime.set(Calendar.MINUTE,currentChangeTime.getTime().getMinutes()/10*10);
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        showToast(format.format(currentChangeTime.getTime().getTime()));
                        event.setStartTime(currentChangeTime);
                        mWeekView.notifyDatasetChanged();
                        break;
                    }
                    case END_TIME: {
                        currentChangeTime.set(Calendar.MINUTE,currentChangeTime.getTime().getMinutes()/10*10);
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        showToast(format.format(currentChangeTime.getTime().getTime()));
                        event.setEndTime(currentChangeTime);
                        mWeekView.notifyDatasetChanged();
                        break;
                    }
                    case ERROR_TIME: {
                        showToast("set event error");
                        break;
                    }
                }
            }
        });
    }

    private void showToast(String message) {
        if(toast!=null)
            toast.cancel();
        toast=Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER,0,0);
        toast.show();
    }
}
