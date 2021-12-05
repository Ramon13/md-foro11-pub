package br.com.javamoon.application.test;

import java.util.Iterator;
import java.util.TreeSet;

public class Main {

	public static void main(String[] args) {
		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(1);
		set.add(2);
		set.add(3);
		set.add(4);
		set.add(2);
		Iterator<Integer> iterator = set.iterator();
		while(iterator.hasNext())
			System.out.println(iterator.next());
	}
	
}
