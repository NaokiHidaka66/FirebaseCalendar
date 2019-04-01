package com.example.chef_.firebasecalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarView extends LinearLayout {

    private static final int WEEKDAYS = 7;
    private static final int MAX_WEEK = 6;

    // 週の始まりの曜日を保持する
    private static final int BIGINNING_DAY_OF_WEEK = Calendar.SUNDAY;
    // 今日のフォント色
    private static final int TODAY_COLOR = Color.RED;
    // 通常のフォント色
    private static final int DEFAULT_COLOR = Color.DKGRAY;
    // 今週の背景色
    private static final int TODAY_BACKGROUND_COLOR = Color.LTGRAY;
    // 通常の背景色
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;


    // 年月表示部分
    private TextView mTitleView;

    // 週のレイアウト
    private LinearLayout mWeekLayout;
    private ArrayList<LinearLayout> mWeeks = new ArrayList<LinearLayout>();

    /**
     * コンストラクタ
     *
     * @param context context
     */
    public CalendarView(Context context){
        super(context, null);
    }

    /**
     * コンストラクタ
     *
     * @param context context
     * @param attrs attributeset
     */
    @SuppressLint("SimpleDateFormat")
    public CalendarView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setOrientation(VERTICAL);

        createTitleView(context);
        createWeekViews(context);
        createDayViews(context);
    }

    /**
     * 年月日表示のタイトルを生成する
     *
     * @param context context
     */
    private void createTitleView(Context context){
        float scaleDensity = context.getResources().getDisplayMetrics().density;

        mTitleView = new TextView(context);
        mTitleView.setGravity(Gravity.CENTER_HORIZONTAL);
        mTitleView.setTextSize((int)(scaleDensity * 14));
        mTitleView.setTypeface(null, Typeface.BOLD);
        mTitleView.setPadding(0,0,0, (int)(scaleDensity * 16));

        addView(mTitleView, new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * 曜日表示のビューを生成する
     *
     * @param context context
     */
    private void createWeekViews(Context context){
        float scaleDensity = context.getResources().getDisplayMetrics().density;

        // 週表示レイアウト
        mWeekLayout = new LinearLayout(context);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, BIGINNING_DAY_OF_WEEK); // 週の頭をセット

        for(int i = 0; i < WEEKDAYS; i++){
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER); //中央に表示
            textView.setPadding(0, 0, (int)(scaleDensity * 4), 0);

            LinearLayout.LayoutParams llp =
                    new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
            llp.weight = 1;

            mWeekLayout.addView(textView, llp);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        addView(mWeekLayout, new LinearLayoutCompat.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * 日付表示用のビューを生成する
     *
     * @param context context
     */
    private void createDayViews(Context context){
        float scaleDensity = context.getResources().getDisplayMetrics().density;

        // カレンダー部 最大6行必要
        for (int i = 0; i < MAX_WEEK; i++){
            LinearLayout weekLine = new LinearLayout(context);
            mWeeks.add(weekLine);

            // 1週間分の日付ビュー作成
            for (int j = 0; j < WEEKDAYS; j++){
                TextView dayView = new TextView(context);
                dayView.setGravity(Gravity.TOP | Gravity.CENTER);
                dayView.setPadding(0, (int)(scaleDensity * 4), (int)(scaleDensity * 4), 0);
                LinearLayout.LayoutParams llp =
                        new LinearLayout.LayoutParams(
                                0, (int)(scaleDensity * 48)
                        );
                llp.weight = 1;
                weekLine.addView(dayView, llp);
            }
            this.addView(weekLine, new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
        }
    }

    /**
     * 年と月を指定して，カレンダーの表示を初期化する
     *
     * @param year 年の指定
     * @param month 月の指定
     */
    public void set(int year, int month){
        setTitle(year, month);
        setWeeks();
        setDays(year, month);
    }

    /**
     * 指定した年月日をタイトルに設定する
     *
     * @param year 年の指定
     * @param month 月の指定
     */
    @SuppressLint("SimpleDateFormat")
    private void setTitle(int year, int month){
        Calendar targetCalendar = getTargetCalendar(year, month);

        // 年月フォーマット文字列
        String formatString = mTitleView.getContext().getString(R.string.format_month_year);
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);

        mTitleView.setText(formatter.format(targetCalendar.getTime()));
    }

    /**
     * 曜日を設定する
     */
    @SuppressLint("SimpleDateFormat")
    private void setWeeks() {
        Calendar week = Calendar.getInstance();
        week.set(Calendar.DAY_OF_WEEK, BIGINNING_DAY_OF_WEEK); // 週の頭をセット
        SimpleDateFormat weekFormatter = new SimpleDateFormat("E"); // 曜日を取得するフォーマッタ
        for (int i = 0; i < WEEKDAYS; i++) {
            TextView textView = (TextView) mWeekLayout.getChildAt(i);
            textView.setText(weekFormatter.format(week.getTime())); // テキストに曜日を表示
            week.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    /**
     * 日付を設定していくメソッド
     *
     * @param year 年の指定
     * @param month 月の指定
     */
    private void setDays(int year, int month) {
        Calendar targetCalendar = getTargetCalendar(year, month);

        int skipCount = getSkipCount(targetCalendar);
        int lastDay = targetCalendar.getActualMaximum(Calendar.DATE);
        int dayCounter = 1;

        Calendar todayCalendar = Calendar.getInstance();
        int todayYear  = todayCalendar.get(Calendar.YEAR);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayDay   = todayCalendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < MAX_WEEK; i++) {
            LinearLayout weekLayout = mWeeks.get(i);
            weekLayout.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            for (int j = 0; j < WEEKDAYS; j++) {
                TextView dayView = (TextView) weekLayout.getChildAt(j);

                // 第一週かつskipCountが残っていれば
                if (i == 0 && skipCount > 0) {
                    dayView.setText(" ");
                    skipCount--;
                    continue;
                }

                // 最終日より大きければ
                if (lastDay < dayCounter) {
                    dayView.setText(" ");
                    continue;
                }

                // 日付を設定
                dayView.setText(String.valueOf(dayCounter));

                boolean isToday = todayYear  == year  &&
                        todayMonth == month &&
                        todayDay   == dayCounter;

                if (isToday) {
                    dayView.setTextColor(TODAY_COLOR); // 赤文字
                    dayView.setTypeface(null, Typeface.BOLD); // 太字
                    weekLayout.setBackgroundColor(TODAY_BACKGROUND_COLOR); // 週の背景グレー
                } else {
                    dayView.setTextColor(DEFAULT_COLOR);
                    dayView.setTypeface(null, Typeface.NORMAL);
                }
                dayCounter++;
            }
        }
    }

    /**
     * カレンダーの最初の空白の個数を求める
     *
     * @param targetCalendar 指定した月のCalendarのInstance
     * @return skipCount
     */
    private int getSkipCount(Calendar targetCalendar) {
        int skipCount; // 空白の個数
        int firstDayOfWeekOfMonth = targetCalendar.get(Calendar.DAY_OF_WEEK); // 1日の曜日
        if (BIGINNING_DAY_OF_WEEK > firstDayOfWeekOfMonth) {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK + WEEKDAYS;
        } else {
            skipCount = firstDayOfWeekOfMonth - BIGINNING_DAY_OF_WEEK;
        }
        return skipCount;
    }

    private Calendar getTargetCalendar(int year, int month) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.clear(); // カレンダー情報の初期化
        targetCalendar.set(Calendar.YEAR, year);
        targetCalendar.set(Calendar.MONTH, month);
        targetCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return targetCalendar;
    }
}
