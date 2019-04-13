package com.ccai.control;

import com.sun.jna.ptr.PointerByReference;
import com.wxyztech.armInterface.BrobotUserInterface;
import com.wxyztech.dllInterfaceInfo.ChessMovesInterface;

import javafx.scene.control.Label;

/**
 * UI全局控制变量�?
 *
 * @author yuancheng
 *
 */
public class UIGlobalVar {

	public static boolean connArm; // 黑色连接状�??

	public static boolean connPC; // 红色连接状�??

	public static int startFlag; // 控制�?始的标志�? 1代表可以点击 0代表不能点击 ,2代表停止

	public static boolean staus; // 运行状�??,true,正在运行�?

	public static float COMM_X_Layout1; // 机械�?1的实时位�?

	public static float COMM_Y_Layout1;

	public static float COMM_Z_Layout1;

	public static Label textValue;

	public static int countChessBox = 0; // 下棋策略�?
											// 如果�?测到走的棋子结束坐标有棋�?,则会先吃掉对方棋�?,即将吃掉的棋子移动到棋盒,没移动一个减�?�?

	// 多机控制 PC
	public static PointerByReference ppPC ;

	public static ChessMovesInterface ChessPC;

	// 多机控制 ARM
	public static PointerByReference ppArm ;
	public static BrobotUserInterface brobotArm;




}
