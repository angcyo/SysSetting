package com.angcyo.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.angcyo.db.helper.ShutdownHelper;
import com.angcyo.db.node.ShutdownTaskRecord;
import com.angcyo.fragment.port.OnTaskAdd;
import com.angcyo.fragment.task.AsyncRun;
import com.angcyo.syssetting.R;
import com.angcyo.util.Util;
import com.angcyo.view.ui.TimePicker;

import org.joda.time.LocalDateTime;

/**
 * Created by angcyo on 2015-03-24 024.
 */
public class AddTaskShutdownDialog extends BaseTaskDialog implements View.OnClickListener {

    ShutdownTaskRecord taskRecord;//如果不为空,说明是修改弹出来的对话框

    public AddTaskShutdownDialog(Context context) {
        super(context);
    }

    public AddTaskShutdownDialog(Context context, int theme) {
        super(context, theme);
    }

    public AddTaskShutdownDialog(Context context, int theme, OnTaskAdd listener) {
        super(context, theme);
        this.taskAddListener = listener;
    }

    public AddTaskShutdownDialog(Context context, int theme, ShutdownTaskRecord task, OnTaskAdd listener) {
        super(context, theme);
        this.taskAddListener = listener;
        this.taskRecord = task;
    }

    protected AddTaskShutdownDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        initDialogLayout();
    }

    RadioButton rbTaskClose, rbTaskStart;
    int taskType = TASK_TYPE_CLOSE;

    private void initDialogLayout() {
        btHourAdd = (Button) findViewById(R.id.id_bt_volume_hour_add);
        btHourMinus = (Button) findViewById(R.id.id_bt_volume_hour_minus);

        btMinuteAdd = (Button) findViewById(R.id.id_bt_volume_minute_add);
        btMinuteMinus = (Button) findViewById(R.id.id_bt_volume_minute_minus);

        btOkAndCon = (Button) findViewById(R.id.id_bt_volume_ok_and_con);
        btOk = (Button) findViewById(R.id.id_bt_volume_ok);
        btCancel = (Button) findViewById(R.id.id_bt_volume_cancel);

        timePicker = (TimePicker) findViewById(R.id.id_task_volume_timepicker);

        rbTaskClose = (RadioButton) findViewById(R.id.id_rb_task_type_close);
        rbTaskStart = (RadioButton) findViewById(R.id.id_rb_task_type_start);

        cb1 = (CheckBox) findViewById(R.id.id_cb_volume_period1);
        cb2 = (CheckBox) findViewById(R.id.id_cb_volume_period2);
        cb3 = (CheckBox) findViewById(R.id.id_cb_volume_period3);
        cb4 = (CheckBox) findViewById(R.id.id_cb_volume_period4);
        cb5 = (CheckBox) findViewById(R.id.id_cb_volume_period5);
        cb6 = (CheckBox) findViewById(R.id.id_cb_volume_period6);
        cb7 = (CheckBox) findViewById(R.id.id_cb_volume_period7);

        btHourAdd.setOnClickListener(this);
        btHourMinus.setOnClickListener(this);

        btMinuteAdd.setOnClickListener(this);
        btMinuteMinus.setOnClickListener(this);

        btOkAndCon.setOnClickListener(this);
        btOk.setOnClickListener(this);
        btCancel.setOnClickListener(this);

        initDialogLayoutData();
    }

    private void initDialogLayoutData() {
        if (taskRecord != null) {//如果是修改...弹出来的对话框.
            btOkAndCon.setVisibility(View.INVISIBLE);//不可见
            btOk.setText("保存修改");
            btCancel.setText("放弃修改");

            timePicker.setH(taskRecord.hour);
            timePicker.setM(taskRecord.minute);

            if (taskRecord.taskType == TASK_TYPE_CLOSE) {
                rbTaskClose.setChecked(true);
            } else {
                rbTaskStart.setChecked(true);
            }

            int period = taskRecord.period;
            if ((period & AddTaskShutdownDialog.WEEK1) == AddTaskShutdownDialog.WEEK1) {
                cb1.setChecked(true);
            }
            if ((period & AddTaskShutdownDialog.WEEK2) == AddTaskShutdownDialog.WEEK2) {
                cb2.setChecked(true);
            }
            if ((period & AddTaskShutdownDialog.WEEK3) == AddTaskShutdownDialog.WEEK3) {
                cb3.setChecked(true);
            }
            if ((period & AddTaskShutdownDialog.WEEK4) == AddTaskShutdownDialog.WEEK4) {
                cb4.setChecked(true);
            }
            if ((period & AddTaskShutdownDialog.WEEK5) == AddTaskShutdownDialog.WEEK5) {
                cb5.setChecked(true);
            }
            if ((period & AddTaskShutdownDialog.WEEK6) == AddTaskShutdownDialog.WEEK6) {
                cb6.setChecked(true);
            }
            if ((period & AddTaskShutdownDialog.WEEK7) == AddTaskShutdownDialog.WEEK7) {
                cb7.setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_bt_volume_hour_add:
                timePicker.setH(timePicker.getH() + 1);
                break;
            case R.id.id_bt_volume_hour_minus:
                timePicker.setH(timePicker.getH() - 1);
                break;

            case R.id.id_bt_volume_minute_add:
                timePicker.setM(timePicker.getM() + 1);
                break;
            case R.id.id_bt_volume_minute_minus:
                timePicker.setM(timePicker.getM() - 1);
                break;

            case R.id.id_bt_volume_ok_and_con:
                asyncSaveTask(false);
                break;
            case R.id.id_bt_volume_ok:
                if (taskRecord == null) {
                    asyncSaveTask(true);
                } else {//如果是修改
                    taskRecord.hour = getHour();
                    taskRecord.minute = getMinute();
                    taskRecord.period = getPeriod();
                    taskRecord.taskType = getTaskType();
                    taskRecord.state = STATE_ENABLED;
                    taskRecord.remark = new LocalDateTime().toString("yyyy-MM-dd HH:mm:ss");
                    taskRecord.save();

                    if (taskAddListener != null) {
                        taskAddListener.onTaskAdd();
                    }
                    this.cancel();
                    Util.showPostMsg("执行完成");
                }

         /*       Logger.e("H:" + getHour() +
                        ":M:" + getMinute() +
                        ":V:" + getVolumeValue() +
                        ":P:" + getPeriod() +
                        "-" + new LocalDateTime().toString("yyyy-MM-dd HH:mm:ss"));*/
                break;
            case R.id.id_bt_volume_cancel:
                this.cancel();
                break;
            default:
                break;
        }
    }

    void asyncSaveTask(final boolean cancel) {
        new AsyncRun() {
            @Override
            public void doBack() {
                ShutdownHelper.save(getHour(), getMinute(), getTaskType(), getPeriod(), STATE_ENABLED, new LocalDateTime().toString("yyyy-MM-dd HH:mm:ss"));
            }

            @Override
            public void doPost() {
                if (taskAddListener != null) {
                    taskAddListener.onTaskAdd();
                }
                if (cancel) {
                    AddTaskShutdownDialog.this.cancel();
                }
                Util.showPostMsg("执行完成");
            }
        }.execute();
    }

    int getTaskType() {
        int type = TASK_TYPE_CLOSE;
        if (rbTaskClose.isChecked()) {
            type = TASK_TYPE_CLOSE;
        } else if (rbTaskStart.isChecked()) {
            type = TASK_TYPE_START;
        }

        return type;
    }
}
