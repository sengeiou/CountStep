package com.example.countstep;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import adapter.CommonAdapter;
import adapter.CommonViewHolder;
import adapter.HistoryAdapter;
import bean.StepData;
import butterknife.BindView;
import butterknife.ButterKnife;

import static utils.DbUtils.createDb;
import static utils.DbUtils.getLiteOrm;
import static utils.DbUtils.getQueryAll;

public class HistoryActivity extends AppCompatActivity {


    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.ll_recycler_view)
    RecyclerView llRecyclerView;
    private HistoryAdapter mHistoryAdapter;
    private List<StepData> mStepDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_history);
        WindowManager.LayoutParams barLayoutParams=getWindow().getAttributes();
        barLayoutParams.flags=(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|barLayoutParams.flags);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initData();
    }

    private void initData() {
        toolBar.setTitle("");
        if (getLiteOrm() == null) {
            createDb(this, "jingzhi");
        }
        mStepDataList = getQueryAll(StepData.class);
        mHistoryAdapter = new HistoryAdapter(this, mStepDataList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        llRecyclerView.addItemDecoration(dividerItemDecoration);
        llRecyclerView.setLayoutManager(manager);
        llRecyclerView.setAdapter(mHistoryAdapter);
//        setEmptyView(lv);
//        List<StepData> stepDatas = getQueryAll(StepData.class);
//        Logger.d("stepDatas="+stepDatas);
//        lv.setAdapter(new CommonAdapter<StepData>(this, stepDatas, R.layout.item) {
//            @Override
//            protected void convertView(View item, StepData stepData) {
//                TextView tv_date = CommonViewHolder.get(item, R.id.tv_date);
//                TextView tv_step = CommonViewHolder.get(item, R.id.tv_step);
//                tv_date.setText(stepData.getToday());
//                tv_step.setText(stepData.getStep() + "步");
//            }
//        });
    }

    protected <T extends View> T setEmptyView(ListView listView) {
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无数据！");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        return (T) emptyView;
    }
}
