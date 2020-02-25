package com.reminder.utils;

import java.util.Comparator;

import com.reminder.response.model.Month;

public class SortByMonth implements Comparator<Month> {

	@Override
	public int compare(Month m1, Month m2) {
		if (m2.getYear() - m1.getYear() >= 0) {
			return m1.getMonth() - m2.getMonth();
		} else {
			return m2.getMonth() - m1.getMonth();
		}
	}
}
