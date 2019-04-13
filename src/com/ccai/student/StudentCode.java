package com.ccai.student;

import sun.applet.Main;
import com.ccai.dllInterfaceInfo.ChessStrategyInterface;
import com.ccai.control.UIGlobalVar;
import com.wxyztech.dllInterfaceInfo.*;
import com.ccai.dllInterfaceInfo.ChessStrategyInterface;
import com.ccai.ui.AlertCorrectOrErrorALertBox;
import com.ccai.ui.MainPage;

/**
 * 学生自己操作的类
 * @author yuancheng
 *
 */
public class StudentCode extends Main{



	/**
	 * 策略计算
	 * @param values  棋盘的数组信息
	 * @param redOrBlack  红色或者黑色策略, (byte)0x00  红色策略,   (byte)0x01 黑色策略
	 * @return  返回值如果为int类型,则计算策略失败,如果返回值为int数组,则计算成功,从索引0到索引3 一次为x1,y1,x2,y2的棋盘行列位置信息
	 */
	public  Object strategy(byte[] values ,byte redOrBlack){

		ChessStrategyInterface chessStrategyInterface = new ChessStrategyInterface();
		Object object = null;
		
		object = chessStrategyInterface.strategy(values, redOrBlack);// 黑色策略


		if (object instanceof Integer) {  //策略计算失败

			return 0;
		} else { //策略计算成功
			int[] strategy = new int[4];//创建一个长度为4 的数组

			Result[] results = (Result[]) object;
			strategy[0] = results[0].x1;
			strategy[1] = results[0].y1;
			strategy[2] = results[0].x2;
			strategy[3] = results[0].y2;

			return strategy;
		}


		return object;
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
