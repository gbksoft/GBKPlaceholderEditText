package com.example.placeholderedittext.util;

import com.example.placeholderedittext.listeners.PositionListener;

public final class Util {

    public static int blockFirstSymbols(int firstSymbols, int[] data) {
        while(firstSymbols > 0 && data[firstSymbols] == -1) {
            firstSymbols--;
        }
        return firstSymbols;
    }

    public static int fixInitialCharacters(int selection, PositionListener listener) {
        if(selection > listener.lastValidPosition()) {
            return listener.lastValidPosition();
        } else {
            return listener.nextValidPosition(selection);
        }
    }

    public static String clear(String string, String allowedChars, String deniedChars ) {
        if (allowedChars != null){
            StringBuilder builder = new StringBuilder(string.length());

            for(char c: string.toCharArray() ){
                if (allowedChars.contains(String.valueOf(c) )){
                    builder.append(c);
                }
            }

            string = builder.toString();
        }

        if (deniedChars != null){
            for(char c: deniedChars.toCharArray()){
                string = string.replace(Character.toString(c), "");
            }
        }

        return string;
    }

    public static Distance calculateDistance(int start, int end, String template, int[] data, int length, PositionListener listener) {
        Distance distance = new Distance();
        for(int i = start; i <= end && i < template.length(); i++) {
            if(data[i] != -1) {
                if(distance.getStart() == -1) {
                    distance.setStart(data[i]);
                }
                distance.setEnd(data[i]);
            }
        }
        if(end == template.length()) {
            distance.setEnd(length);
        }
        if(distance.getStart() == distance.getEnd() && start < end) {
            int newStart = listener.previousValidPosition(distance.getStart() - 1);
            if(newStart < distance.getStart()) {
                distance.setStart(newStart);
            }
        }
        return distance;
    }

}
