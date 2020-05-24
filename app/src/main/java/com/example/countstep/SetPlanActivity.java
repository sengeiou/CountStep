package com.example.countstep;


import android.app.Service;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Common.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import config.Constant;
import utils.SharedPreferencesUtils;

public class SetPlanActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_step_number)
    EditText tvStepNumber;
    @BindView(R.id.cb_remind)
    CheckBox cbRemind;
    @BindView(R.id.tv_remind_time)
    TextView tvRemindTime;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tool_bar_plan)
    Toolbar toolBarPlan;
    private SharedPreferencesUtils sp;

    private String walk_qty;
    private String remind;
    private String achieveTime;
    private Calendar mCalendar;
    private int mHour;
    private int mMinute;
    private  DateFormat mDateFormat;
    private Date mDate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.ac_exercise_plan);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams barLayoutParams=getWindow().getAttributes();
        barLayoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|barLayoutParams.flags);
        setSupportActionBar(toolBarPlan);
        toolBarPlan.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
        addListener();
    }

    public void initData() {//获取锻炼计划
        toolBarPlan.setTitle("");
        sp = new SharedPreferencesUtils(this);
        String planWalk_QTY = (String) sp.getParam(Constants.Common.PLAN_STEP, "5000");
        String remind = (String) sp.getParam(Constants.Common.REMIND, "1");
        String achieveTime = (String) sp.getParam(Constants.Common.ACHIEVE_TIME, "20:00");
        if (!planWalk_QTY.isEmpty()) {
            if ("0".equals(planWalk_QTY)) {
                tvStepNumber.setText(getString(R.string.number));
            } else {
                tvStepNumber.setText(planWalk_QTY);
            }
        }
        if (!remind.isEmpty()) {
            if ("0".equals(remind)) {
                cbRemind.setChecked(false);
            } else if ("1".equals(remind)) {
                cbRemind.setChecked(true);
            }
        }

        if (!achieveTime.isEmpty()) {
            tvRemindTime.setText(achieveTime);
        }

    }


    public void addListener() {
        btnSave.setOnClickListener(this);
        tvRemindTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                save();
                break;

            case R.id.tv_remind_time:
                showTimeDialog1();
                break;
        }
    }

    private void save() {
        walk_qty = tvStepNumber.getText().toString().trim();
//        remind = "";
        if (cbRemind.isChecked()) {
            remind = "1";
            showTime();
            String currentTime = mCalendar.get(Calendar.HOUR_OF_DAY) + ":" + mCalendar.get(Calendar.MINUTE);
            Log.d("wangtianxiong", currentTime);
//            try {
//                mDateFormat.parse(currentTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            Vibrator vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(70);
//                if (sp.getParam(Constants.Common.ACHIEVE_TIME, "21:00")  )
        } else {
            remind = "0";
        }
        achieveTime = tvRemindTime.getText().toString().trim();
        if (walk_qty.isEmpty() || "0".equals(walk_qty)) {
            sp.setParam(Constants.Common.PLAN_STEP, getResources().getString(R.string.number));
        } else {
            sp.setParam(Constants.Common.PLAN_STEP, walk_qty);
        }
        sp.setParam(Constants.Common.REMIND, remind);

        if (achieveTime.isEmpty()) {
            sp.setParam(Constants.Common.ACHIEVE_TIME, "21:00");
            this.achieveTime = "21:00";
        } else {
            sp.setParam(Constants.Common.ACHIEVE_TIME, achieveTime);
        }
        finish();
    }

    private void showTime(){
        mCalendar = Calendar.getInstance(Locale.CHINA);
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mDateFormat = new SimpleDateFormat("HH:mm");
    }

    private void showTimeDialog1() {
        showTime();
//        String time = tv_remind_time.getText().toString().trim();

        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                String remaintime = mCalendar.get(Calendar.HOUR_OF_DAY) + ":" + mCalendar.get(Calendar.MINUTE);

                try {
                    mDate = mDateFormat.parse(remaintime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (null != mDate) {
                    mCalendar.setTime(mDate);
                }
                tvRemindTime.setText(mDateFormat.format(mDate));
            }
        }, mHour, mMinute, true).show();
    }
}
