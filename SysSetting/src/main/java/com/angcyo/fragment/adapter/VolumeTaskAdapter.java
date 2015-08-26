package com.angcyo.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.angcyo.db.node.VolumeTaskRecord;
import com.angcyo.fragment.dialog.AddTaskVolumeDialog;
import com.angcyo.syssetting.R;

import java.util.List;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public class VolumeTaskAdapter extends BaseAdapter {

    Context context;
    List<VolumeTaskRecord> listTaskData;

    public VolumeTaskAdapter(Context context, List<VolumeTaskRecord> listTaskData) {
        this.context = context;
        this.listTaskData = listTaskData;
    }

    @Override
    public int getCount() {
        return listTaskData.size();
    }

    @Override
    public Object getItem(int position) {
        return listTaskData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_volume_task_item, null);
            holder = new ViewHolder();
            holder.txTaskPeriod = (TextView) convertView.findViewById(R.id.id_tx_period);
            holder.txTaskSate = (TextView) convertView.findViewById(R.id.id_tx_task_state);
            holder.txTaskTime = (TextView) convertView.findViewById(R.id.id_tx_task_time);
            holder.txTaskVolumeVaule = (TextView) convertView.findViewById(R.id.id_tx_volume_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        VolumeTaskRecord task = listTaskData.get(position);
        holder.txTaskPeriod.setText(getPeriodStr(task.period));
        holder.txTaskSate.setText(task.state == AddTaskVolumeDialog.STATE_ENABLED ? "已开启" : "已禁用");
        holder.txTaskTime.setText(String.format("%02d", task.hour) + ":" + String.format("%02d", task.minute));
        holder.txTaskVolumeVaule.setText(String.format("%02d", task.volume));

        return convertView;
    }

    public void setDataChanged(List list) {
        this.listTaskData = list;
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView txTaskTime, //任务时间
                txTaskVolumeVaule, //音量
                txTaskSate, //状态
                txTaskPeriod;//周期
    }

    //获取周期
    public static String getPeriodStr(int period) {
        StringBuilder strPeriod = new StringBuilder();
        if (period == 0) {
            return "仅一次";
        }
        if ((period & AddTaskVolumeDialog.WEEK_ALL) == AddTaskVolumeDialog.WEEK_ALL) {
            return "每一天";
        }
        if ((period & AddTaskVolumeDialog.WEEK1) == AddTaskVolumeDialog.WEEK1) {
            strPeriod.append(" 一");
        }
        if ((period & AddTaskVolumeDialog.WEEK2) == AddTaskVolumeDialog.WEEK2) {
            strPeriod.append(" 二");
        }
        if ((period & AddTaskVolumeDialog.WEEK3) == AddTaskVolumeDialog.WEEK3) {
            strPeriod.append(" 三");
        }
        if ((period & AddTaskVolumeDialog.WEEK4) == AddTaskVolumeDialog.WEEK4) {
            strPeriod.append(" 四");
        }
        if ((period & AddTaskVolumeDialog.WEEK5) == AddTaskVolumeDialog.WEEK5) {
            strPeriod.append(" 五");
        }
        if ((period & AddTaskVolumeDialog.WEEK6) == AddTaskVolumeDialog.WEEK6) {
            strPeriod.append(" 六");
        }
        if ((period & AddTaskVolumeDialog.WEEK7) == AddTaskVolumeDialog.WEEK7) {
            strPeriod.append(" 日");
        }

        return "周" + strPeriod.toString();
    }
}
