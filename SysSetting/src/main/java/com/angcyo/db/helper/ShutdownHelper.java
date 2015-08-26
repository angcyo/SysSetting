package com.angcyo.db.helper;

import com.angcyo.db.node.ShutdownTaskRecord;

import java.util.List;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public class ShutdownHelper {

    public static void save(int hour, int minute, int taskType, int period, int state, String remark) {
        new ShutdownTaskRecord(hour, minute, taskType, period, state, remark).save();
    }

    public static void delete(long id) {
        ShutdownTaskRecord.findById(ShutdownTaskRecord.class, id).delete();
    }

    public static List<ShutdownTaskRecord> getAll() {
        return ShutdownTaskRecord.listAll(ShutdownTaskRecord.class);
    }

    public static ShutdownTaskRecord get(long id) {
        return ShutdownTaskRecord.findById(ShutdownTaskRecord.class, id);
    }
}
