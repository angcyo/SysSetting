package com.angcyo.fragment.task;

import android.os.AsyncTask;

import com.angcyo.fragment.adapter.node.FileNote;
import com.angcyo.util.file.FileUtil;

/**
 * Created by angcyo on 2015-03-19 019.
 */
public abstract class AsyncGetFilePath extends AsyncTask<String, Integer, FileNote> {

    @Override
    protected FileNote doInBackground(String... params) {
        FileNote fileNote = FileUtil.getFileNote(params[0]);
        return fileNote;
    }

    @Override
    protected void onPostExecute(FileNote fileNote) {
        onGetFilePath(fileNote);
    }

    public abstract void onGetFilePath(FileNote fileNote);
}
