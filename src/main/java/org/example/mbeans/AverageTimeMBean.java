package org.example.mbeans;

public interface AverageTimeMBean {
    double getAverageTime();
    int getClickCount();
    long getTotalTime();

    void reset();
}
