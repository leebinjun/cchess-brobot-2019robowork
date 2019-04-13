package com.ccai.dllInterfaceInfo;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.wxyztech.dllInterfaceInfo.*;

/**
 * java和动态库接口
 *
 * @author yuancheng
 * @since 1.0
 */
public interface ChessStrategy extends Library {

	public static String dllName = "ChessStrategy";// 动态库名字
	// public static String dllName = "dll/ChessStrategy";// 动态库名字

	ChessStrategy INSTANCE = (ChessStrategy) Native.loadLibrary(dllName, ChessStrategy.class);// 动态库实例

	/**
	 * 下棋策略
	 * @param chessboar   棋面信息数组
	 * @param chesscolor  红色(byte)0x00     黑色(byte)0x01
	 * @param result  数组长度为1
	 * @return 如果返回int,则执行策略失败
	 */
	public int strategy(byte[] chessboar,byte chesscolor,Result[] result);//

	public static void main(String args[])
	{
        System.out.println("hello!");
    }

}