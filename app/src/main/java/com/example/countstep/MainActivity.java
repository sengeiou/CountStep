package com.example.countstep;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import Common.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import config.Constant;
import service.StepService;
import utils.SharedPreferencesUtils;
import utils.UpdateUiCallBack;
import view.StepArcView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tool_bar1)
    Toolbar toolbar1;
    @BindView(R.id.tv_data)
    TextView tvData;
    @BindView(R.id.cc)
    StepArcView cc;
    @BindView(R.id.tv_set)
    TextView tvSet;
    @BindView(R.id.tv_isSupport)
    TextView tvIsSupport;
    @BindView(R.id.week)
    TextView week;
    //    @BindView(R.id.date)
//    TextView date;
    @BindView(R.id.rl_instance)
    TextView rlInstance;
    @BindView(R.id.rl_target)
    TextView rlTarget;
    @BindView(R.id.rl_Calorie)
    TextView rlCalorie;
    @BindView(R.id.rl_button_clear)
    Button rlButtonClear;
    @BindView(R.id.rl_button_step)
    Button rlButtonStep;
    @BindView(R.id.rl_time)
    Chronometer rlTime;
    private SharedPreferencesUtils sp;
    private boolean isBind = false;
    private String planWalk_QTY;
    private String startInstance = "距离：" + "<font color='#EE2C2C'>" + 0 + "</font>" + " km";
    private String startCalorie = "消耗：" + "<font color='#EE2C2C'>" + 0 + "</font>" + " 卡路里";
    private StepService stepService;
    private long mTime = 0;
    private String newCalorie;
    private int hour;
    //运动系数
    private double k = 0.9;
    //体重
    private double weight = 120;
    private double instance;
    private double calorie;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams barLayoutParams = getWindow().getAttributes();
        barLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | barLayoutParams.flags);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar1);
        toolbar1.setTitle("");
        initData();
        addListener();
    }


    private void addListener() {
        tvSet.setOnClickListener(this);
        tvData.setOnClickListener(this);
        rlButtonClear.setOnClickListener(this);
        rlButtonStep.setOnClickListener(this);
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        planWalk_QTY = (String) sp.getParam(Constants.Common.PLAN_STEP, getResources().getString(R.string.number));
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        rlInstance.setText(Html.fromHtml(startInstance));
        tvIsSupport.setText(getResources().getString(R.string.count_step));
        rlCalorie.setText(Html.fromHtml(startCalorie));
//        date.setText(DataString.StringData());
        week.setText(DataString.StringData());
        String target = "目标：" + "<font color='#3c8de7'>" + planWalk_QTY + "</font>" + "步";
        rlTarget.setText(Html.fromHtml(target));
//        long time = (long) sp.getParam(Constants.Common.COUNT_TIME, 0);
        rlTime.setBase((Long) sp.getParam(Constants.Common.COUNT_TIME, 0L));

//        rlTime.setFormat("用时："+ "0" + (sp.getParam(Constants.Common.COUNT_TIME, 0)) + ":%s");
        setupService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "博客地址：http://blog.csdn.net/i_do_can");
                shareIntent.setType("text/plain");
                //设置分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;
            case R.id.about:
                Toast.makeText(this, "这是一款计步器", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void upData(double newInstance) {
        String i = String.format(Locale.getDefault(), "%.2f", newInstance);
        String s = "距离：" + "<font color='#EE2C2C'>" + i + "</font>" + " km";
        rlInstance.setText(Html.fromHtml(s));
        //消耗的卡路里
        calorie = (weight * newInstance * k);
        String c = String.format(Locale.getDefault(), "%.2f", calorie);
        if (calorie >= 1000){
            calorie = calorie / 1000;
            newCalorie = "消耗：" + "<font color='#EE2C2C'>" + c + "</font>" + " 千卡路里";
        }else {
            newCalorie = "消耗：" + "<font color='#EE2C2C'>" + c + "</font>" + " 卡路里";
        }
        rlCalorie.setText(Html.fromHtml(newCalorie));
    }

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }


    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam(Constants.Common.PLAN_STEP, getResources().getString(R.string.number));
            cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepService.getStepCount());
            instance = (stepService.getStepCount() * 1.2) / 1000;
            upData(instance);
            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    String planWalk_QTY = (String) sp.getParam(Constants.Common.PLAN_STEP, getResources().getString(R.string.number));
                    cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
                    instance = (stepCount * 1.2) / 1000;
                    upData(instance);
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, SetPlanActivity.class));
                break;
            case R.id.tv_data:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.rl_button_clear:
                rlInstance.setText(Html.fromHtml(startInstance));
                rlCalorie.setText(Html.fromHtml(startCalorie));
                rlTime.setBase(SystemClock.elapsedRealtime());
                rlTime.setFormat(getResources().getString(R.string.zero_time));
                rlTime.stop();
                rlButtonStep.setText(getResources().getString(R.string.start_time));
                stepService.clearData();
                cc.setCurrentCount(0, 0);

                break;
            case R.id.rl_button_step:
                mHandler.post(runnableTime);

                break;
            default:
                break;
        }
    }

    private Runnable runnableTime = new Runnable() {
        @Override
        public void run() {

            if (rlButtonStep.getText().equals(getResources().getString(R.string.start_time))) {
                if (mTime != 0){
                    rlTime.setBase(rlTime.getBase() + (SystemClock.elapsedRealtime() - mTime));
                }else {
                    rlTime.setBase(SystemClock.elapsedRealtime());
                }
                hour = (int) ((SystemClock.elapsedRealtime() - rlTime.getBase()) / 1000 / 60);
                rlTime.setFormat("用时："+ "0" + hour + ":%s");
                rlTime.start();
//                sp.setParam(Constants.Common.COUNT_TIME, mTime);
                rlButtonStep.setText(getResources().getString(R.string.stop_time));
            } else {
                rlButtonStep.setText(getResources().getString(R.string.start_time));
                mTime = SystemClock.elapsedRealtime();
                rlTime.stop();
            }

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        planWalk_QTY = (String) sp.getParam(Constants.Common.PLAN_STEP, getResources().getString(R.string.number));
        String target = "目标：" + "<font color='#3c8de7'>" + planWalk_QTY + "</font>" + " 步";
        rlTarget.setText(Html.fromHtml(target));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
        if (mHandler != null){
            mHandler.post(runnableTime);
            mHandler = null;
        }
        sp.setParam(Constants.Common.COUNT_TIME, rlTime.getBase() + (SystemClock.elapsedRealtime() - mTime));
    }
}
