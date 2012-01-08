package com.royvandewater.roybots.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import robocode.RobocodeFileOutputStream;

public class RoboLog implements AutoCloseable {

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    
    private static final String[] LOG_TYPES = {"DEBUG", "INFO", "WARNING", "ERROR"};

    private PrintStream printStream;
    private RobocodeFileOutputStream outputStream;
    private int logLevel;

    public RoboLog(File dataFile, int logLevel) {
        try {
        	this.outputStream = new RobocodeFileOutputStream(dataFile.getAbsolutePath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.printStream = new PrintStream(outputStream, true);
        this.logLevel = logLevel;
        this.i("Logger Initialized");
    }

    @Override
    protected void finalize() throws Throwable {
        outputStream.close();
        super.finalize();
    }
    
    public void close() {
        close();
    }
    
    public void close(Object object) {
        i("Closing Log: " + object);
        
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (logType <= logLevel)
            printStream.println(String.format("%1$-8s", LOG_TYPES[logType] + ": ") + object.toString());
    }
}