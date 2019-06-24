package com.tuge.myapp.examples.wifiTranslator.DetailActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

public class LogUtil {


    private static Logger logger = null;

    /**
     * @作用 把传递消息写进Log日志
     */
    public static void showTestInfo(final Object msg) {
        if (msg == null || msg.toString().isEmpty()) {
            return;
        }
        String appMsg = getDateToStringStyle("MM-dd HH:mm:ss,SSS", new Date()) + ":" + msg.toString();
        if (logger == null) {
            logger = Logger.getLogger("TGT_T5");
        }
        logger.info(appMsg);

    }

    //获取当前时间(按一定格式)
    public static String getDateToStringStyle(String formatString, Date dates) {
        DateFormat dateFormatter = null;
        Date date = new Date();
        String dateFormat = "";
        if (formatString == null || formatString.isEmpty()) {
            formatString = "MM-dd HH:mm:ss";
        }
        if (date != null) {
            date = dates;
        }
        try {
            dateFormatter = new SimpleDateFormat(formatString);
            dateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            dateFormat = dateFormatter.format(date);
        } catch (Exception e) {
        }
        return dateFormat;
    }
}
