package com.angcyo.util;

import com.angcyo.util.bean.DownTask;

/**
 * Created by angcyo on 2015-04-17 017.
 */
public interface IDownload {
    /**
     * On down start.
     *
     * @param task 下载的任务
     * @param downUrl 下载的url,全地址
     */
    void onDownStart(DownTask task, String downUrl);

    /**
     * On down progress.
     *
     * @param speed 下载速度,没有处理的大小数据
     * @param progress 下载进度,没有%号
     */
    void onDownProgress(long speed, int progress);

    /**
     * On down end.
     *
     * @param task the task
     * @param downUrl the down url
     * @param filePath 文件保存的路径
     * @param downTime 下载公共使用的时间,单位秒
     */
    void onDownEnd(DownTask task, String downUrl, String filePath, long downTime);

    /**
     * 下载错误信息
     *
     * @param error 错误信息
     */
    void onDownError(String error);
}
