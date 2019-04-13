package com.ccai.dllInterfaceInfo;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.wxyztech.chessInfoListener.ChessInfoCallback;
import com.wxyztech.dllInterfaceInfo.Result;
import com.wxyztech.dllInterfaceInfo.ChessMoves;;


/**
 * Java控制象棋接口类
 *
 * @author yuancheng
 * @version 1.0
 * @since 1.0
 */
public class ChessStrategyInterface {
	/**
	 * 下棋策略
	 * @param chessboard   棋面信息数组
	 * @param chesscolor  红色(byte)0x00     黑色(byte)0x01
	 * @return 如果返回int,则执行策略失败
	 */
	public static Object strategy(byte[] chessboard,byte chesscolor){
		Result c=new Result();
		Result[] result=(Result[])c.toArray(1);
		int status=ChessMoves.INSTANCE.strategy(chessboard, chesscolor, result);
		if(status==0){
			return result;
		}else{
			return status;
		}
	}
	
	public static void main(String args[])
	{
        System.out.println("hello!");
    }

}
