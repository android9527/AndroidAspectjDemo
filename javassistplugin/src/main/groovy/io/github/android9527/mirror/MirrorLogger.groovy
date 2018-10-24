package io.github.android9527.mirror;

/**
 * Created by michaelzhong on 2018/1/25.
 */

public class MirrorLogger {

    private static def logger

    public static void setMirrorLogger(def logger) {
        MirrorLogger.logger = logger
    }

    public static void log(String msg) {
        MirrorLogger.logger.error msg
    }

}
