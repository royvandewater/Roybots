package com.royvandewater.roybots.utils;

import java.io.IOException;
import java.io.PrintStream;
import robocode.RobocodeFileOutputStream;

public class RoboLog {

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    
    private static final String[] LOG_TYPES = {"DEBUG", "INFO", "WARNING", "ERROR"};

    private PrintStream printStream;
    private RobocodeFileOutputStream outputStream;
    private int logLevel;

    public RoboLog(String loggerFilename, int logLevel) {
        try {
            this.outputStream = new RobocodeFileOutputStream(loggerFilename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.printStream = new PrintStream(outputStream);
        this.logLevel = logLevel;
        this.i("================================================");
        this.i("Logger Initialized");
        this.i("================================================");
    }

    @Override
    protected void finalize() throws Throwable {
        outputStream.close();
        super.finalize();
    }

    public void d(Object object) {
        writeToLog(object, DEBUG);
    }

    public void i(Object object) {
        writeToLog(object, INFO);
    }

    public void w(Object object) {
        writeToLog(object, WARNING);
    }

    public void e(Object object) {
        writeToLog(object, ERROR);
    }

    private void writeToLog(Object object, int logType) {
        if (logType >= logLevel)
            printStream.println(String.format("%1$-8s", LOG_TYPES[logType] + ": ") + object.toString());
    }
}
