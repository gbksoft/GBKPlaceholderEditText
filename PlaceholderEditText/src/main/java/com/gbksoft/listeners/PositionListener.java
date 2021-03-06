package com.gbksoft.listeners;

public interface PositionListener {

    int previousValidPosition(int currentPosition);

    int nextValidPosition(int currentPosition);

    int lastValidPosition();

    int findLastValidPosition();

}
