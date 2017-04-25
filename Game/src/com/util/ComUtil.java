package com.util;

import java.net.*;
public class ComUtil{
	/**
	* 解析字符串地址
	* @param address
	* @return
	*/
	public static InetSocketAddress strToAddress(String address){
		System.out.println(address);
		String str = address.substring(1, address.length());
		String[] arr = str.split(":");
		InetSocketAddress dest = new InetSocketAddress(arr[0], Integer.parseInt(arr[1]));
		return dest;
	}
}