package adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.countstep.R;

import java.util.ArrayList;
import java.util.List;

import bean.StepData;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Activity mActivity;
    private List<StepData> mDataList = new ArrayList<>();

    public HistoryAdapter(Activity activity, List<StepData> list){
        mActivity = activity;
        mDataList.addAll(list);
    }
    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        StepData stepData = mDataList.get(position);
        String s = stepData.getStep();
        Log.d("111", s);
        holder.tvDate.setText(stepData.getToday());
        holder.tvStep.setText(stepData.getStep() + "步");

    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvStep;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStep = itemView.findViewById(R.id.tv_step);
        }
    }
}
