package com.github.qq44920040.mc;

import java.text.*;
import java.io.*;
import java.util.*;

public class LogUtils
{
    private static final int TAG_MAX_LENGTH = 20;
    private static final int MESSAGE_MAX_LENGTH = 1024;
    private static final SimpleDateFormat DATE_FORMAT;
    private static Level logOutLevel;
    private static boolean isOutToConsole;
    private static boolean isOutToFile;
    private static File logOutFile;
    private static RandomAccessFile logOutFileStream;

    public static void setLogOutLevel(Level currentLevel) {
        if (currentLevel == null) {
            currentLevel = Level.INFO;
        }
        LogUtils.logOutLevel = currentLevel;
    }

    public static synchronized void setLogOutFile(final File logOutFile) throws IOException {
        LogUtils.logOutFile = logOutFile;
        if (LogUtils.logOutFileStream != null) {
            closeStream(LogUtils.logOutFileStream);
            LogUtils.logOutFileStream = null;
        }
        if (LogUtils.logOutFile != null) {
            try {
                (LogUtils.logOutFileStream = new RandomAccessFile(LogUtils.logOutFile, "rw")).seek(LogUtils.logOutFile.length());
            }
            catch (IOException e) {
                closeStream(LogUtils.logOutFileStream);
                LogUtils.logOutFileStream = null;
                throw e;
            }
        }
    }

    public static void setLogOutTarget(final boolean isOutToConsole, final boolean isOutToFile) {
        LogUtils.isOutToConsole = isOutToConsole;
        LogUtils.isOutToFile = isOutToFile;
    }

    public static void debug(final String tag, final String message) {
        printLog(Level.DEBUG, tag, message, false);
    }

    public static void info(final String tag, final String message) {
        printLog(Level.INFO, tag, message, false);
    }

    public static void warn(final String tag, final String message) {
        printLog(Level.WARN, tag, message, false);
    }

    public static void error(final String tag, final String message) {
        printLog(Level.ERROR, tag, message, true);
    }

    public static void error(final String tag, final Exception e) {
        if (e == null) {
            error(tag, (String)null);
            return;
        }
        PrintStream printOut = null;
        try {
            final ByteArrayOutputStream bytesBufOut = new ByteArrayOutputStream();
            printOut = new PrintStream(bytesBufOut);
            e.printStackTrace(printOut);
            printOut.flush();
            error(tag, new String(bytesBufOut.toByteArray(), "UTF-8"));
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        finally {
            closeStream(printOut);
        }
    }

    private static void printLog(final Level level, final String tag, final String message, final boolean isOutToErr) {
        if (level.getLevelValue() >= LogUtils.logOutLevel.getLevelValue()) {
            final String log = LogUtils.DATE_FORMAT.format(new Date()) + " " + level.getTag() + "/" + checkTextLengthLimit(tag, 20) + ": " + checkTextLengthLimit(message, 1024);
            if (LogUtils.isOutToConsole) {
                outLogToConsole(isOutToErr, log);
            }
            if (LogUtils.isOutToFile) {
                outLogToFile(log);
            }
        }
    }

    private static void outLogToConsole(final boolean isOutToErr, final String log) {
        if (isOutToErr) {
            System.err.println(log);
        }
        else {
            System.out.println(log);
        }
    }

    private static synchronized void outLogToFile(final String log) {
        if (LogUtils.logOutFileStream != null) {
            try {
                LogUtils.logOutFileStream.write((log + "\n").getBytes("UTF-8"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String checkTextLengthLimit(String text, final int maxLength) {
        if (text != null && text.length() > maxLength) {
            text = text.substring(0, maxLength - 3) + "...";
        }
        return text;
    }

    private static void closeStream(final Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            }
            catch (Exception ex) {}
        }
    }

    static {
        DATE_FORMAT = new SimpleDateFormat("MM/dd HH:mm:ss.SSS");
        LogUtils.logOutLevel = Level.INFO;
        LogUtils.isOutToConsole = true;
        LogUtils.isOutToFile = false;
    }

    public enum Level
    {
        DEBUG("D", 1),
        INFO("I", 2),
        WARN("W", 3),
        ERROR("E", 4);

        private String tag;
        private int levelValue;

        private Level(final String tag, final int levelValue) {
            this.tag = tag;
            this.levelValue = levelValue;
        }

        public String getTag() {
            return this.tag;
        }

        public int getLevelValue() {
            return this.levelValue;
        }
    }
}
