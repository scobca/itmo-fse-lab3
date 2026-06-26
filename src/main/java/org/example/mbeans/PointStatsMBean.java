package org.example.mbeans;

public interface PointStatsMBean {
    int getTotalPoints();
    int getMissPoints();
    int getMissSequence();

    void reset();
}
