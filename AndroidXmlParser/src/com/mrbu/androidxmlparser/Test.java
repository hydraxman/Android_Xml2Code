package com.mrbu.androidxmlparser;

public class Test {
	/*public static void main(String[] args) {
		int i=1;
		int j=2;
		swap(i,j);
		System.out.println("i"+i+"j"+j);
		int[] arr=new int[]{i,j};
		swap(arr);
		System.out.println("arr[0]"+arr[0]);
		System.out.println("arr[1]"+arr[1]);
	}*/

	private static void swap(int[] arr) {
		int temp=0;
		temp=arr[1];
		arr[1]=arr[0];
		arr[0]=temp;
	}

	private static void swap(int i, int j) {
		int temp=0;
		temp=i;
		i=j;
		j=temp;
	}
}
