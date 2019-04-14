package com.ccai.student;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.lang.*;
import java.io.*;

import com.ccai.control.UIGlobalVar;
import com.ccai.dllInterfaceInfo.ChessStrategyInterface;
import com.wxyztech.dllInterfaceInfo.*;
import com.ccai.ui.AlertCorrectOrErrorALertBox;
import com.ccai.ui.MainPage;

import javafx.application.Platform;

import java.util.HashMap;
/**
 * 学生自己操作的类
 * @author yuancheng
 *
 */
public class StudentCode extends MainPage{
	
	/**
	 * 将棋盘的数组信息转化为UCCI使用的局面描述字符串
	 * ucci FEN格式串  http://www.xqbase.com/protocol/cchess_fen.htm
	 * @param values  棋盘的数组信息
	 * @return infoString 局面描述FEN格式串
	 */
	private String chessBoardtoString(byte[] values)
	{
		// byte[] values = new byte[]{1, 2, 4, 5, 6, 5, 4, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7,
        //     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17, 0, 17, 0, 17, 0, 17, 0, 17, 0, 13, 0, 0, 0, 0, 0, 13, 0,
        //     0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12, 14, 15, 16, 15, 14, 12, 11 };

        HashMap<Integer, Character> map = new HashMap<Integer, Character>(){
            {
                put(11, 'r');    put(1, 'R');
                put(12, 'n');    put(2, 'N');
                put(14, 'b');    put(4, 'B');
                put(15, 'a');    put(5, 'A');
                put(16, 'k');    put(6, 'K');
                put(13, 'c');    put(3, 'C');
                put(17, 'p');    put(7, 'P');
                put(0, '0');
            }    
        }; 
  
		String res = new String();
		
		// 红黑沿楚河汉界线对调一下
	    // 水平反转
        for (int i = 0; i < 10; i++)
        {
            int count = 0;
            for (int j = 8; j >= 0; j--)
            {
                if (values[i*9+j] != 0)
                {
                    if(count != 0)
                    {
                        res += String.valueOf(count);
                        count = 0;
                    }
                    res += map.get(0xFF & values[i*9+j]);
                }
                else
                {
                    count++;
                }
            }
            if(count != 0)
            {
                res += String.valueOf(count);
            }
            res += '/';
        }
        // 去掉最后一个字符'/'
		res = res.substring(0,res.length()-1);
		res = new StringBuffer(res).reverse().toString();
        // System.out.print("res: ");
        // System.out.println(res);
		return res;
	}
	
	/**
	 * 调用引擎得到策略字符串
	 * @param chessBoardInfoString 局面描述字符串
	 * @return 引擎计算结果
	 */
	private String getMoveString(String chessBoardInfoString ) //throws InterruptedException
	{
		String filePath = ".\\dll\\cyclone.exe";
		StringBuilder sb = new StringBuilder();
		String res = ""; 
		try
		{
			System.out.print("hello!\n");
			// String cm2 = "position fen rCbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/7C1/9/RNBAKABNR b - - 0 1";
			String cm2 = chessBoardInfoString;
			String cm1 = "go time 500 depth 5";
			
			// String cmd = filePath + " && " + cm2 + " && " + cm1;
			String cmd = filePath ;//+ " ucci";
			
			Process p = Runtime.getRuntime().exec(cmd);
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			bw.write(cm2);
			bw.newLine();
			bw.flush();
			bw.write(cm1);
			bw.newLine();
			bw.flush();
			bw.close();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			String ress = "";
			while ((line=bufferedReader.readLine()) != null) 
			{
				if(line.length() > 12)
					ress = line.substring(9, 13);
				// System.out.println(line);
				// System.out.print("res: ");
				// System.out.println(ress);
			}
			// System.out.print("byebye!\n");
			
			// System.out.print("final res: ");
			// System.out.println(ress);

			res = ress;
			return res;
		}catch (IOException ex) {
			return res;
				// ex.printStackTrace();
		}
	}
	
	
	
	/**
	 * 策略计算
	 * @param values  棋盘的数组信息
	 * @param redOrBlack  红色或者黑色策略, (byte)0x00  红色策略,   (byte)0x01 黑色策略
	 * @return  返回值如果为int类型,则计算策略失败,如果返回值为int数组,则计算成功,从索引0到索引3 一次为x1,y1,x2,y2的棋盘行列位置信息
	 */
	public  Object strategy(byte[] values ,byte redOrBlack){

		// 打印当前棋盘
		System.out.print("board:");
		System.out.println(values);
		for (int i = 0; i<90; i++)
		{
			System.out.print(values[i]);
			if ((i+1)%9 == 0)
			System.out.println();
		}

        //     values[90]                           positionIfo   
		// 1 2 4 5 6 5 4 2 1                       
		// 0 0 0  red                       RED   - RNBAKCP
		// 0 3 ...                          black - rnbakcp     
		// .                                rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/7C1/9/RNBAKABNR
		// .                      
		// .      black           
		// 0                      
		// 11 12 ...      11   

		//得到局面描述字符串
        String positionIfo =  chessBoardtoString( values);
		System.out.print("posIfo: ");
		System.out.println(positionIfo);
		
		//得到完整字符串
		String chessBoardInfoString = "position fen " + positionIfo + " b - - 0 1";

		//调用引擎得到策略字符串
        String sMove =  getMoveString( chessBoardInfoString);
		
		//打印策略
		System.out.print("sMove: ");
		System.out.println(sMove);
		//将策略转化为移动坐标
		// String sMove = "b7b5";
		
		//     sMove("b7b5")               real board for arm
		// 0                       		1
		// 1      red              		2        red
		// 2                     		3
		// .                     		.   
		// .                      		.
		// .      black            		.       black
		// 9                       	   10
		//   a b c ... g h i         		1 2 3 ... 8 9

		int[] strategy = new int[4];//创建一个长度为4 的数组
        strategy[0] = sMove.charAt(1)-'0'+1;
        strategy[1] = sMove.charAt(0)-'a'+1;
        strategy[2] = sMove.charAt(3)-'0'+1;
		strategy[3] = sMove.charAt(2)-'a'+1;
		
		return strategy;

	}




	/**
	 *移动机械臂,完成走棋子的步骤
	 * @param endIndex  终点的索引,用以判断终点是否有棋子,如果有棋子,则先必须移开
	 * @param values    获得的象棋棋盘的数组信息,0代表该位置没有棋子,其余数字请参照说明
	 * @param start     起点位置的坐标
	 * @param end       终点位置的坐标
	 * @param layout16     棋盒的x y 坐标
	 */
	public  void moveChess(int endIndex,  byte[] values,float[] start, float[] end, float[][] layout16){

		UIGlobalVar.brobotArm.setControlSignel(UIGlobalVar.ppArm.getValue(), (byte) 0x01);
		UIGlobalVar.brobotArm.setMovementSpeedRate(UIGlobalVar.ppArm.getValue(), 1.0f);
		UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 0, new float[] { 0, -286, 150 });// 回零
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		startMove = true;
		if (values[endIndex] != 0) {// 如果走的终点有棋子,则先移开棋子
			startMove = false;
			// 高度需要为start[2]
			int code = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20, new float[] { end[0], end[1], start[2] });
			if (code == 4) {
				try {
					 moveFlag = true;

					new Thread(new TenMinutesThread()).start();//开启倒计时10秒的循环

					while (moveFlag) {
						System.out.println("aaaaaaaa");
						if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - end[0]) <= 10)
								&& (Math.abs(UIGlobalVar.COMM_Y_Layout1 - end[1]) <= 10)
								&& (Math.abs(UIGlobalVar.COMM_Z_Layout1 - start[2]) <= 10)) {
							tenMinutesFlag = false;//结束10秒的倒计时
							moveFlag = false; //结束本次循环

							UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x01);// 吸气泵

							Thread.sleep(1000);

							int code1 = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20,
									new float[] { layout16[UIGlobalVar.countChessBox][0], layout16[UIGlobalVar.countChessBox][1], end[2] }); // z轴要高一点
							if (code1 == 4) {
								moveFlag = true;
								new Thread(new TenMinutesThread()).start();//开启倒计时10秒的循环
								while (moveFlag) {
									System.out.println("bbbbbbbbbb");
									if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - layout16[UIGlobalVar.countChessBox][0]) <= 10)
											&& (Math.abs(UIGlobalVar.COMM_Y_Layout1 - layout16[UIGlobalVar.countChessBox][1]) <= 10)
											&& (Math.abs(UIGlobalVar.COMM_Z_Layout1 - end[2]) <= 10)) {
										tenMinutesFlag = false;//结束10秒的倒计时
										startMove = true;
										moveFlag = false;
										UIGlobalVar.countChessBox++;
										if (UIGlobalVar.countChessBox == 16) {
											UIGlobalVar.countChessBox = 0;
										}
										UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x00); // 停

										Thread.sleep(500);
									}
									Thread.sleep(100);
								}
							} else {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										startMove = false;
										new AlertCorrectOrErrorALertBox().display(code1, "移动到棋盒点错误", null);
									}
								});
							}

							UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x00); //

						}
						Thread.sleep(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						startMove = false;//不执行移开棋子后再走棋子的步骤
						new AlertCorrectOrErrorALertBox().display(code, "移动到吃棋子点错误", null);
					}
				});

			}

		}

		/*************************** 移开棋子后再走棋子的步骤 ******************************/
		if (startMove) { //如果正确执行了之前的代码
			int code = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20, start);
			if (code == 4) {
				moveFlag = true;
				new Thread(new TenMinutesThread()).start();//开启倒计时10秒的循环
				while (moveFlag) {
					try {
						if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - start[0]) < 10)
								&& Math.abs(UIGlobalVar.COMM_Y_Layout1 - start[1]) < 10
								&& Math.abs(UIGlobalVar.COMM_Z_Layout1 - start[2]) < 10) {
							tenMinutesFlag = false;//结束10秒倒计时
							moveFlag = false;
							UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x01);
							Thread.sleep(1000);
							// 运动到目标点
							int code1 = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20, end);
							if (code1 == 4) {
								moveFlag = true;
								new Thread(new TenMinutesThread()).start();//开启倒计时10秒的循环
								while (moveFlag) {
									System.out.println("dddddddddd");
									if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - end[0]) < 10)
											&& Math.abs(UIGlobalVar.COMM_Y_Layout1 - end[1]) < 10
											&& Math.abs(UIGlobalVar.COMM_Z_Layout1 - end[2]) < 10) {
										tenMinutesFlag = false;//结束10秒倒计时
										moveFlag = false;
										startMove = true;
										Platform.runLater(new Runnable() {
											public void run() {
												UIGlobalVar.textValue.setText("处理成功");
												// new
												// AlertCorrectOrErrorALertBox().display(101,
												// "运动完毕,请点击\"发送\"按钮继续",
												// null);

											}
										});
									}
									Thread.sleep(100);
								}
							} else {
								Platform.runLater(new Runnable() {
									public void run() {
										startMove = false;
										new AlertCorrectOrErrorALertBox().display(code1, "运动到终点失败", null);
									}
								});
							}
						}
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} else {
				Platform.runLater(new Runnable() {
					public void run() {
						startMove = false;
						new AlertCorrectOrErrorALertBox().display(4, "运动到起点失败", null);
					}
				});

			}
			if (startMove) { //如果以上出现问题,则不执行以下代码
				try {
					Thread.sleep(500);
					UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x00); // 停气泵
					Thread.sleep(100);
					UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 0, new float[] { 0, -286, 150 });// 先回零
					Thread.sleep(1500);
					UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 0, new float[] { -277, -68, 150 });// 再移开
					Thread.sleep(2000);
					UIGlobalVar.brobotArm.setControlSignel(UIGlobalVar.ppArm.getValue(), (byte) 0x05);

					new Thread(new Runnable() {
						public void run() {
							int code = UIGlobalVar.ChessPC.finishChessing(UIGlobalVar.ppPC.getValue());
							Platform.runLater(new Runnable() {

								@Override
								public void run() {

									if (code == 4) {
										new AlertCorrectOrErrorALertBox().display(code, "下棋指令发送给裁判端完毕", null);
										UIGlobalVar.textValue.setText("");
									} else {
										new AlertCorrectOrErrorALertBox().display(code, "下棋指令发送给裁判端失败,请重试", null);
									}

								}
							});
						}
					}).start();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}



	}


}
