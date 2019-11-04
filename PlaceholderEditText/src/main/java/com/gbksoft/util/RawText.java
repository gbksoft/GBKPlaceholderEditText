package com.gbksoft.util;


public class RawText {
	private String text = "";

    /**
     * @param distance given distance
     */
	public void subtractFromString(Distance distance) {
		String firstPart = "";
		String lastPart = "";
		
		if(distance.getStart() > 0 && distance.getStart() <= text.length()) {
			firstPart = text.substring(0, distance.getStart());
		}
		if(distance.getEnd() >= 0 && distance.getEnd() < text.length()) {
			lastPart = text.substring(distance.getEnd());
		}
		text = firstPart.concat(lastPart);
	}

	/**
	 * 
	 * @param newString New String to be added
	 * @param start Position to insert newString
	 * @param maxLength Maximum raw text length
	 * @return Number of added characters
	 */
	public int addToString(String newString, int start, int maxLength) {
		String firstPart = "";
		String lastPart = "";
		
		if(newString == null || newString.equals("")) {
			return 0;
		}
		else if(start < 0) {
			throw new IllegalArgumentException("Start position must be non-negative");
		}
		else if(start > text.length()) {
			throw new IllegalArgumentException("Start position must be less than the actual text length");
		}
		
		int count = newString.length();
		
		if(start > 0) {
			firstPart = text.substring(0, start);
		}
		if(start < text.length()) {
			lastPart = text.substring(start);
		}
		if(text.length() + newString.length() > maxLength) {
			count = maxLength - text.length();
			newString = newString.substring(0, count);
		}
		text = firstPart.concat(newString).concat(lastPart);		
		return count;
	}

	public String getText() {
		return text;
	}

	public int length() {
		return text.length();
	}

	public char charAt(int position) {
		return text.charAt(position);
	}
}
