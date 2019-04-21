package com.ccai.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.wxyztech.armInterface.BrobotUserInterface;
import com.wxyztech.armMsgListener.ArmDataFrame;
import com.wxyztech.armMsgListener.ArmDataListener;
import com.wxyztech.chessInfoListener.ChessInfoListener;
import com.wxyztech.chessInfoListener.chessInfoModel;
import com.ccai.control.UIGlobalVar;
import com.wxyztech.dllInterfaceInfo.ChessMovesInterface;
import com.ccai.student.StudentCode;
// import com.ccai.student.Demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MainPage extends Application {

	private Pane mainPane;

	private Button resetButton; // 复位

	private Button photoButtonToMain; // 发送给裁判端

	private Button photoButtonStargy;// 执行策略

	private Button buttonConnMain; // 连接裁判端电脑

	private Button buttonConnArm; // 连接机械臂

	private Button setting; // 设置坐标的弹窗

	private byte[] values; // 裁判端传递过来的数组

	private AlertCorrectOrErrorALertBox alertConncet;// 正在连接的弹窗

	// 棋盘90个位置的坐标
	private float[][] layout90 = new float[90][2];

	// 棋盒16个位置的坐标
	private float[][] layout16 = new float[16][2];

	public float[] layoutZ = new float[2]; // 两个Z轴的坐标

	// 实例化和PC的回调
	MyListenerPC listenerPC = new MyListenerPC();

	// 实例化和ARM端的回调
	MyListenerArm listenerArm = new MyListenerArm();

	private ComboBox<String> comboBoxMain; // 连接的串口显示

	private ComboBox<String> comboBoxArm;// 连接的串口显示

	private Label labelLayout;

	protected boolean tenMinutesFlag;// 10秒倒计时的标志位

	protected boolean startMove = true;// 开始为true,果走的终点有棋子,则先置为false,待走完后再置为true

	protected boolean moveFlag;// 循环检测机械臂坐标的标志位

	private int count10Second;// 坐标超限倒计时的计数

	StudentCode demo;

	@Override
	public void start(Stage primaryStage) {
		initValue(); // 初始化变量
		initPane(primaryStage); // 初始化布局
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 初始化变量
	 */
	public void initValue() {
		// 初始化变量
		UIGlobalVar.connArm = false; // 未连接
		UIGlobalVar.connPC = false; // 未连接
		UIGlobalVar.staus = false;// 不在运行中
		UIGlobalVar.startFlag = 1; // 点击即是开始
		// 初始化X和Y的坐标
	}

	/**
	 * 初始化布局
	 */
	public void initPane(Stage primaryStage) {
		try {
			UIGlobalVar.ppPC = new PointerByReference(Pointer.NULL);

			UIGlobalVar.ChessPC = new ChessMovesInterface();

			// 多机控制 ARM
			UIGlobalVar.ppArm = new PointerByReference(Pointer.NULL);
			UIGlobalVar.brobotArm = new BrobotUserInterface();
			mainPane = new Pane();
			middleInit();
			BorderPane root = new BorderPane();
			root.setCenter(mainPane);
			root.setId("backgroundColor");
			Scene scene = new Scene(root, 500, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.getIcons().add(new Image("/icon/LOGO.png")); // 给软件加上图标
			primaryStage.setScene(scene);
			primaryStage.show();
			allClick();
			getCOMS();// 获取串口
			demo = new StudentCode();
			// 关闭窗体的事件
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					// 断开的线程操作
					new Thread(new Runnable() {

						@Override
						public void run() {
							// 如果正在通信中

							// 如果连接了
							if (UIGlobalVar.connPC) {
								// 如果有连接就断开连接
								UIGlobalVar.ChessPC.connectBrobotPing(UIGlobalVar.ppPC.getValue(), (byte) 0x02);
								UIGlobalVar.ChessPC.disConnectBrobot(UIGlobalVar.ppPC.getValue());
							}
							if (UIGlobalVar.connArm) {
								// 如果有连接就断开连接
								UIGlobalVar.brobotArm.connectBrobotPing(UIGlobalVar.ppArm.getValue(), (byte) 0x02);
								UIGlobalVar.brobotArm.disConnectBrobot(UIGlobalVar.ppArm.getValue());
							}
						}
					}).start();

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 控制电脑的UI
	 */
	public void middleInit() {
		Label labelTitle = new Label("黑色选手端");
		labelTitle.setAlignment(Pos.CENTER);
		labelTitle.setPrefWidth(500);
		labelTitle.setStyle("-fx-font-size:18;-fx-text-fill:white");

		/******************************** 端口栏 **********************************/
		// 显示黑色方的端口
		buttonConnMain = new Button("连接裁判端");
		buttonConnMain.setAlignment(Pos.CENTER);
		buttonConnMain.setLayoutX(15);
		buttonConnMain.setLayoutY(30);
		buttonConnMain.setId("button");

		comboBoxMain = new ComboBox<>();
		comboBoxMain.setLayoutX(120);
		comboBoxMain.setLayoutY(30);
		comboBoxMain.setId("comboBox");

		// 显示pc2的端口
		buttonConnArm = new Button("连接机械臂");
		buttonConnArm.setAlignment(Pos.CENTER);
		buttonConnArm.setLayoutX(255);
		buttonConnArm.setLayoutY(30);
		buttonConnArm.setId("button");

		comboBoxArm = new ComboBox<>();
		comboBoxArm.setLayoutX(360);
		comboBoxArm.setLayoutY(30);
		comboBoxArm.setId("comboBox");

		mainPane.getChildren().addAll(labelTitle, buttonConnMain, comboBoxMain, buttonConnArm, comboBoxArm);

		// 初始化ComboBox里的值

		/************************************** 设置栏 *********************************/

		// 参数设置
		setting = new Button("坐标设置");
		setting.setAlignment(Pos.CENTER);
		setting.setLayoutX(15);
		setting.setLayoutY(65);
		setting.setId("button");
		// 坐标
		labelLayout = new Label();
		labelLayout.setStyle("-fx-text-fill:white;-fx-font-size:16;");
		labelLayout.setLayoutX(120);
		labelLayout.setLayoutY(70);

		// 开始
		UIGlobalVar.textValue = new Label();
		UIGlobalVar.textValue.setPrefWidth(500);
		UIGlobalVar.textValue.setLayoutY(150);
		UIGlobalVar.textValue.setStyle("-fx-background-color:white;");
		UIGlobalVar.textValue.setAlignment(Pos.CENTER);

		// 初始化
		resetButton = new Button("手动发点");
		resetButton.setLayoutX(200);
		resetButton.setLayoutY(200);
		resetButton.setId("button");
		resetButton.setAlignment(Pos.CENTER);

		mainPane.getChildren().addAll(setting, resetButton, UIGlobalVar.textValue, labelLayout);

		/**********************************
		 * 图片拍照
		 ****************************************/

		photoButtonStargy = new Button("执行策略");
		photoButtonStargy.setLayoutX(200);
		photoButtonStargy.setLayoutY(300);
		photoButtonStargy.setId("button");
		photoButtonStargy.setAlignment(Pos.CENTER);

		// 发送给裁判端
		photoButtonToMain = new Button("发送信号");
		photoButtonToMain.setLayoutX(200);
		photoButtonToMain.setLayoutY(340);
		photoButtonToMain.setId("button");
		photoButtonToMain.setAlignment(Pos.CENTER);

		TextField textStartX = new TextField();
		textStartX.setPrefWidth(50);
		textStartX.setLayoutX(0);
		textStartX.setLayoutY(260);

		TextField textStartY = new TextField();
		textStartY.setPrefWidth(50);
		textStartY.setLayoutX(60);
		textStartY.setLayoutY(260);

		TextField textStartZ = new TextField();
		textStartZ.setPrefWidth(50);
		textStartZ.setLayoutX(120);
		textStartZ.setLayoutY(260);

		TextField textEndX = new TextField();
		textEndX.setPrefWidth(50);
		textEndX.setLayoutX(180);
		textEndX.setLayoutY(260);

		TextField textEndY = new TextField();
		textEndY.setPrefWidth(50);
		textEndY.setLayoutX(240);
		textEndY.setLayoutY(260);

		TextField textEndZ = new TextField();
		textEndZ.setPrefWidth(50);
		textEndZ.setLayoutX(300);
		textEndZ.setLayoutY(260);

		TextField textIndex = new TextField();
		textIndex.setPrefWidth(50);
		textIndex.setLayoutX(0);
		textIndex.setLayoutY(330);
		mainPane.getChildren().addAll(textStartX, textStartY, textStartZ, textEndX, textEndY, textEndZ, textIndex);

		mainPane.getChildren().addAll(photoButtonToMain, photoButtonStargy);

		// 手动发点
		resetButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				float[] start = new float[] { Float.parseFloat(textStartX.getText()),
						Float.parseFloat(textStartY.getText()), Float.parseFloat(textStartZ.getText()) };
				float[] end = new float[] { Float.parseFloat(textEndX.getText()), Float.parseFloat(textEndY.getText()),
						Float.parseFloat(textEndZ.getText()) };

				controlArmToMoveCopy(Integer.parseInt(textIndex.getText()), start, end, layout16);
			}
		});

	}

	/**
	 * 获取xml中的坐标
	 *
	 * @return 返回的x坐标集合和Y坐标集合的 集合
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<ArrayList<Float>> readFromXML() {
		ArrayList<ArrayList<Float>> lvauesXY = new ArrayList<>();

		ArrayList<Float> valuesX = new ArrayList<>();
		ArrayList<Float> valuesY = new ArrayList<>();
		if (valuesX.size() != 0) {
			valuesX.clear();
		}
		if (valuesY.size() != 0) {
			valuesY.clear();
		}
		SAXReader reader = new SAXReader();
		Document document;

		try {
			document = reader.read(new File("layoutXML/layoutValue.xml"));

			Element elementRoot = document.getRootElement(); // 获取跟节点

			Iterator it = elementRoot.elementIterator();

			while (it.hasNext()) {
				Element number = (Element) it.next();

				Iterator itt = number.elementIterator();
				while (itt.hasNext()) {
					Element layoutX = (Element) itt.next();
					Element layoutY = (Element) itt.next();

					valuesX.add(Float.parseFloat(layoutX.getStringValue().trim()));
					valuesY.add(Float.parseFloat(layoutY.getStringValue().trim()));
				}

			}
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		lvauesXY.add(valuesX);
		lvauesXY.add(valuesY);
		return lvauesXY;
	}

	/**
	 * 获取串口并且显示到串口文本框里
	 */
	public void getCOMS() {
		ChessMovesInterface chessMovesInterface = new ChessMovesInterface();
		String[] brobotCOMS = chessMovesInterface.searchBrobot();
		if (brobotCOMS.length > 0) {
			comboBoxMain.setValue(brobotCOMS[0]);
			comboBoxArm.setValue(brobotCOMS[0]);
			for (int i = 0; i < brobotCOMS.length; i++) {
				comboBoxMain.getItems().addAll(brobotCOMS[i]);
				comboBoxArm.getItems().addAll(brobotCOMS[i]);
			}
		}
	}

	/**
	 * 执行策略
	 */
	public void stargyMethod() {
		// 得到策略object
		// (byte)0x00 红色策略, (byte)0x01 黑色策略
		Object object = demo.strategy(values, (byte) 0x00);

		if (object instanceof Integer) { // 识别失败
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					new AlertCorrectOrErrorALertBox().display(object, "策略计算失败", null);

				}
			});
		} else {

			// 初始化一系列坐标
			// 读取xml中的值
			ArrayList<ArrayList<Float>> valuesXY = WriteValueToXml.readFromXML();
			// 初始化棋盘在机械臂中的坐标
			resetLayout(valuesXY);

			int[] strategy = (int[]) object;

			int endIndex = 89 - (strategy[2] - 1) * 9 - (strategy[3] - 1); // 棋子移动终点的索引,用来判断终点没有没有棋子被吃
			System.out.print("##################" + endIndex + "##########" + values[endIndex]);
			// char val = (char) System.in.read();

			float[][] layoutStartAndEnd = conversion(strategy); // 将获得的策略信息转化为机械臂坐标的二维数组

			System.err.println("起点坐标    :    (" + layoutStartAndEnd[0][0] + ", " + layoutStartAndEnd[0][1] + ", "
					+ layoutStartAndEnd[0][2] + ")");
			System.err.println("终点坐标    :    (" + layoutStartAndEnd[1][0] + ", " + layoutStartAndEnd[1][1] + ", "
					+ layoutStartAndEnd[1][2] + ")");

			float[] start = layoutStartAndEnd[0];
			float[] end = layoutStartAndEnd[1];
			/**********************************
			 * 学生自己完成
			 *********************************************************/

			// 控制运动函数

			// controlArmToMove(endIndex, start, end, layout16);
			demo.moveChess(endIndex, values, start, end, layout16);

			/**********************************
			 * 学生自己完成
			 *********************************************************/
		}
	}

	/**
	 * 点击事件
	 */
	public void allClick() {
		// 连接PC
		buttonConnMain.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!UIGlobalVar.connPC) {

					new Thread(new ConnPCThread(UIGlobalVar.ChessPC, comboBoxMain, listenerPC, UIGlobalVar.ppPC))
							.start();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							System.out.println("55555");
							UIGlobalVar.ChessPC.connectBrobotPing(UIGlobalVar.ppPC.getValue(), (byte) 0x02);
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							UIGlobalVar.ChessPC.disConnectBrobot(UIGlobalVar.ppPC.getValue());
							System.out.println("6666666666");
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									UIGlobalVar.connPC = false; // 断开
									new AlertCorrectOrErrorALertBox().display(4, "和裁判端断开成功", null);
									buttonConnMain.setText("连接裁判端");
								}
							});
						}
					}).start();
				}
			}
		});
		// 连接机械臂
		buttonConnArm.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!UIGlobalVar.connArm) {
					UIGlobalVar.textValue.setText("");
					new Thread(new ConnARMThread(UIGlobalVar.brobotArm, comboBoxArm, listenerArm, UIGlobalVar.ppArm))
							.start();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							UIGlobalVar.brobotArm.setControlSignel(UIGlobalVar.ppArm.getValue(), (byte) 0x05);
							UIGlobalVar.brobotArm.connectBrobotPing(UIGlobalVar.ppArm.getValue(), (byte) 0x02);
							UIGlobalVar.brobotArm.disConnectBrobot(UIGlobalVar.ppArm.getValue());
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									UIGlobalVar.connArm = false; // 断开
									new AlertCorrectOrErrorALertBox().display(4, "和机械臂断开成功", null);
									buttonConnArm.setText("连接机械臂");
									labelLayout.setText("");
								}
							});
						}
					}).start();
				}
			}
		});
		// 参数设置
		setting.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new AlertLayoutXYZ().display();
			}
		});

		// // z执行策略

		photoButtonStargy.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				stargyMethod();

			}
		});
		photoButtonToMain.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
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
			}
		});

		// });

	}

	/**
	 *
	 * 控制机械臂移动执行策略的方法
	 *
	 * @param endIndex 终点索引 判断终点是否有棋子
	 * @param start    起点坐标
	 * @param end      终点坐标(吃棋子点坐标,高度要根据情况来定)
	 * @param layout16 棋盒的16个坐标的集合
	 */
	public void controlArmToMove(int endIndex, float[] start, float[] end, float[][] layout16) {
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
			int code = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20,
					new float[] { end[0], end[1], start[2] });
			if (code == 4) {
				try {
					moveFlag = true;

					new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环

					while (moveFlag) {
						System.out.println("aaaaaaaa");
						if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - end[0]) <= 10)
								&& (Math.abs(UIGlobalVar.COMM_Y_Layout1 - end[1]) <= 10)
								&& (Math.abs(UIGlobalVar.COMM_Z_Layout1 - start[2]) <= 10)) {
							tenMinutesFlag = false;// 结束10秒的倒计时
							moveFlag = false; // 结束本次循环

							UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x01);// 吸气泵

							Thread.sleep(1000);

							int code1 = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20,
									new float[] { layout16[UIGlobalVar.countChessBox][0],
											layout16[UIGlobalVar.countChessBox][1], layoutZ[1] }); // z轴要高一点
							if (code1 == 4) {
								moveFlag = true;
								new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环
								while (moveFlag) {
									System.out.println("bbbbbbbbbb");
									if ((Math.abs(
											UIGlobalVar.COMM_X_Layout1 - layout16[UIGlobalVar.countChessBox][0]) <= 10)
											&& (Math.abs(UIGlobalVar.COMM_Y_Layout1
													- layout16[UIGlobalVar.countChessBox][1]) <= 10)
											&& (Math.abs(UIGlobalVar.COMM_Z_Layout1 - layoutZ[1]) <= 10)) {
										tenMinutesFlag = false;// 结束10秒的倒计时
										startMove = true;
										moveFlag = false;
										UIGlobalVar.countChessBox++;
										if (UIGlobalVar.countChessBox == 16) {
											UIGlobalVar.countChessBox = 0;
										}
										UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(),
												(byte) 0x00); // 停

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
						startMove = false;// 不执行移开棋子后再走棋子的步骤
						new AlertCorrectOrErrorALertBox().display(code, "移动到吃棋子点错误", null);
					}
				});

			}

		}

		/*************************** 移开棋子后再走棋子的步骤 ******************************/
		if (startMove) { // 如果正确执行了之前的代码

			int code = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20, start);
			if (code == 4) {
				moveFlag = true;
				new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环
				while (moveFlag) {
					System.out.println("ccccccccccc");
					try {
						if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - start[0]) < 10)
								&& Math.abs(UIGlobalVar.COMM_Y_Layout1 - start[1]) < 10
								&& Math.abs(UIGlobalVar.COMM_Z_Layout1 - start[2]) < 10) {
							tenMinutesFlag = false;// 结束10秒倒计时
							moveFlag = false;
							UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x01);
							Thread.sleep(1000);
							// 运动到目标点
							int code1 = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20,
									end);
							if (code1 == 4) {
								moveFlag = true;
								new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环
								while (moveFlag) {
									System.out.println("dddddddddd");
									if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - end[0]) < 10)
											&& Math.abs(UIGlobalVar.COMM_Y_Layout1 - end[1]) < 10
											&& Math.abs(UIGlobalVar.COMM_Z_Layout1 - end[2]) < 10) {
										tenMinutesFlag = false;// 结束10秒倒计时
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
			if (startMove) { // 如果以上出现问题,则不执行以下代码
				try {
					Thread.sleep(500);
					UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x00); // 停气泵
					Thread.sleep(100);
					UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 0,
							new float[] { 0, -286, 150 });// 先回零
					Thread.sleep(1500);
					UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 0,
							new float[] { -277, -68, 150 });// 再移开
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

	/**
	 *
	 * 控制机械臂移动执行策略的方法
	 *
	 * @param endIndex 终点索引 判断终点是否有棋子
	 * @param start    起点坐标
	 * @param end      终点坐标(吃棋子点坐标,高度要根据情况来定)
	 * @param layout16 棋盒的16个坐标的集合
	 */
	public void controlArmToMoveCopy(int endIndex, float[] start, float[] end, float[][] layout16) {
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
			int code = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20,
					new float[] { end[0], end[1], start[2] });
			if (code == 4) {
				try {
					moveFlag = true;

					new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环

					while (moveFlag) {
						System.out.println("aaaaaaaa");
						if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - end[0]) <= 10)
								&& (Math.abs(UIGlobalVar.COMM_Y_Layout1 - end[1]) <= 10)
								&& (Math.abs(UIGlobalVar.COMM_Z_Layout1 - start[2]) <= 10)) {
							tenMinutesFlag = false;// 结束10秒的倒计时
							moveFlag = false; // 结束本次循环

							UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x01);// 吸气泵

							Thread.sleep(1000);

							int code1 = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20,
									new float[] { layout16[UIGlobalVar.countChessBox + 1][0],
											layout16[UIGlobalVar.countChessBox + 1][1], layoutZ[1] }); // z轴要高一点
							if (code1 == 4) {
								moveFlag = true;
								new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环
								while (moveFlag) {
									System.out.println("bbbbbbbbbb");
									if ((Math.abs(UIGlobalVar.COMM_X_Layout1
											- layout16[UIGlobalVar.countChessBox + 1][0]) <= 10)
											&& (Math.abs(UIGlobalVar.COMM_Y_Layout1
													- layout16[UIGlobalVar.countChessBox + 1][1]) <= 10)
											&& (Math.abs(UIGlobalVar.COMM_Z_Layout1 - layoutZ[1]) <= 10)) {
										tenMinutesFlag = false;// 结束10秒的倒计时
										startMove = true;
										moveFlag = false;
										UIGlobalVar.countChessBox++;
										if (UIGlobalVar.countChessBox == 16) {
											UIGlobalVar.countChessBox = 0;
										}
										UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(),
												(byte) 0x00); // 停

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
						startMove = false;// 不执行移开棋子后再走棋子的步骤
						new AlertCorrectOrErrorALertBox().display(code, "移动到吃棋子点错误", null);
					}
				});

			}

		}

		/*************************** 移开棋子后再走棋子的步骤 ******************************/
		if (startMove) { // 如果正确执行了之前的代码

			int code = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20, start);
			if (code == 4) {
				moveFlag = true;
				new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环
				while (moveFlag) {
					System.out.println("ccccccccccc");
					try {
						if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - start[0]) < 10)
								&& Math.abs(UIGlobalVar.COMM_Y_Layout1 - start[1]) < 10
								&& Math.abs(UIGlobalVar.COMM_Z_Layout1 - start[2]) < 10) {
							tenMinutesFlag = false;// 结束10秒倒计时
							moveFlag = false;
							UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x01);
							Thread.sleep(1000);
							// 运动到目标点
							int code1 = UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 20,
									end);
							if (code1 == 4) {
								moveFlag = true;
								new Thread(new TenMinutesThread()).start();// 开启倒计时10秒的循环
								while (moveFlag) {
									System.out.println("dddddddddd");
									if ((Math.abs(UIGlobalVar.COMM_X_Layout1 - end[0]) < 10)
											&& Math.abs(UIGlobalVar.COMM_Y_Layout1 - end[1]) < 10
											&& Math.abs(UIGlobalVar.COMM_Z_Layout1 - end[2]) < 10) {
										tenMinutesFlag = false;// 结束10秒倒计时
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
			if (startMove) { // 如果以上出现问题,则不执行以下代码
				try {
					Thread.sleep(500);
					UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x00); // 停气泵
					Thread.sleep(100);
					UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 0,
							new float[] { 0, -286, 150 });// 先回零
					Thread.sleep(1500);
					UIGlobalVar.brobotArm.controlDoorMovement(UIGlobalVar.ppArm.getValue(), 0,
							new float[] { -277, -68, 150 });// 再移开
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

	/**
	 * 利用12个点初始化106个坐标(包括90个棋盘的点和16个棋盒的坐标点)
	 */
	public void resetLayout(ArrayList<ArrayList<Float>> layoutXYS) {
		// 棋盘 红方半边的坐标偏差
		float detalOneValueX = (layoutXYS.get(0).get(1) - layoutXYS.get(0).get(0)) / 8.0f; // 第一行的X坐标间距
																							// ,第9个(红車)
																							// 减去第1个(红車)
		float detalOneValueY = (layoutXYS.get(1).get(1) - layoutXYS.get(1).get(0)) / 8.0f; // 第一行的Y坐标间距
																							// ,第9个(红車)
																							// 减去第1个(红車)

		float detalFiveValueX = (layoutXYS.get(0).get(3) - layoutXYS.get(0).get(2)) / 8.0f; // 第5行的X坐标间距
																							// ,第45个
																							// 减去第37个
		float detalFiveValueY = (layoutXYS.get(1).get(3) - layoutXYS.get(1).get(2)) / 8.0f; // 第5行的Y坐标间距
																							// ,第45个
																							// 减去第37个

		// 棋盘 黑方半边的坐标偏差
		float detalSixValueX = (layoutXYS.get(0).get(5) - layoutXYS.get(0).get(4)) / 8.0f; // 第6行的X坐标间距
																							// ,第54个
																							// 减去第46个
		float detalSixValueY = (layoutXYS.get(1).get(5) - layoutXYS.get(1).get(4)) / 8.0f; // 第6行的Y坐标间距
																							// ,第54个
																							// 减去第54个

		float detalTenValueX = (layoutXYS.get(0).get(7) - layoutXYS.get(0).get(6)) / 8.0f; // 第10行的X坐标间距
																							// ,第90个(黑車)
																							// 减去第82个(黑車)
		float detalTenValueY = (layoutXYS.get(1).get(7) - layoutXYS.get(1).get(6)) / 8.0f; // 第10行的Y坐标间距
																							// ,第90个(黑車)
																							// 减去第82个(黑車)

		for (int i = 0; i < 90; i++) {
			if (i >= 0 && i <= 8) { // 第一横条的值
				layout90[i][0] = layoutXYS.get(0).get(0) + (detalOneValueX) * i;
				layout90[i][1] = layoutXYS.get(1).get(0) + (detalOneValueY) * i;
			} else if (i >= 36 && i < 45) { // 第五横条的值
				layout90[i][0] = layoutXYS.get(0).get(2) + (detalFiveValueX) * (i % 9);
				layout90[i][1] = layoutXYS.get(1).get(2) + (detalFiveValueY) * (i % 9);
			}

			if (i >= 45 && i < 54) { // 第6横条的值
				layout90[i][0] = layoutXYS.get(0).get(4) + (detalSixValueX) * (i % 9);
				layout90[i][1] = layoutXYS.get(1).get(4) + (detalSixValueY) * (i % 9);
			} else if (i >= 81 && i < 90) { // 第10横条的值
				layout90[i][0] = layoutXYS.get(0).get(6) + (detalTenValueX) * (i % 9);
				layout90[i][1] = layoutXYS.get(1).get(6) + (detalTenValueY) * (i % 9);
			}
		}

		// 红方棋盘部分
		for (int i = 0; i < 45; i++) {

			if (i % 9 == 0 && i != 0 && i != 36) { // 第一竖条的值 9 18 27 36

				layout90[i][0] = (float) (layout90[0][0]
						+ ((float) (layout90[36][0] - layout90[0][0]) / 4.00) * (i / 9)); // X
				layout90[i][1] = (float) (layout90[0][1]
						+ ((float) (layout90[36][1] - layout90[0][1]) / 4.00) * (i / 9)); // Y

			} else if (i % 9 == 1 && i != 1 && i != 37) {// 第2竖条的值 10 19 28 37

				layout90[i][0] = (float) (layout90[1][0]
						+ ((float) (layout90[37][0] - layout90[1][0]) / 4.00) * ((i - 1) / 9)); // X
				layout90[i][1] = (float) (layout90[1][1]
						+ ((float) (layout90[37][1] - layout90[1][1]) / 4.00) * ((i - 1) / 9)); // Y

			} else if (i % 9 == 2 && i != 2 && i != 38) {// 第3竖条的值 11 20 29 38

				layout90[i][0] = (float) (layout90[2][0]
						+ ((float) (layout90[38][0] - layout90[2][0]) / 4.00) * ((i - 2) / 9)); // X
				layout90[i][1] = (float) (layout90[2][1]
						+ ((float) (layout90[38][1] - layout90[2][1]) / 4.00) * ((i - 2) / 9)); // Y

			} else if (i % 9 == 3 && i != 3 && i != 39) {// 第4竖条的值

				layout90[i][0] = (float) (layout90[3][0]
						+ ((float) (layout90[39][0] - layout90[3][0]) / 4.00) * ((i - 3) / 9)); // X
				layout90[i][1] = (float) (layout90[3][1]
						+ ((float) (layout90[39][1] - layout90[3][1]) / 4.00) * ((i - 3) / 9)); // Y

			} else if (i % 9 == 4 && i != 4 && i != 40) {// 第5竖条的值

				layout90[i][0] = (float) (layout90[4][0]
						+ ((float) (layout90[40][0] - layout90[4][0]) / 4.00) * ((i - 4) / 9)); // X
				layout90[i][1] = (float) (layout90[4][1]
						+ ((float) (layout90[40][1] - layout90[4][1]) / 4.00) * ((i - 4) / 9)); // Y

			} else if (i % 9 == 5 && i != 5 && i != 41) {// 第6竖条的值

				layout90[i][0] = (float) (layout90[5][0]
						+ ((float) (layout90[41][0] - layout90[5][0]) / 4.00) * ((i - 5) / 9)); // X
				layout90[i][1] = (float) (layout90[5][1]
						+ ((float) (layout90[41][1] - layout90[5][1]) / 4.00) * ((i - 5) / 9)); // Y

			} else if (i % 9 == 6 && i != 6 && i != 42) {// 第7竖条的值

				layout90[i][0] = (float) (layout90[6][0]
						+ ((float) (layout90[42][0] - layout90[6][0]) / 4.00) * ((i - 6) / 9)); // X
				layout90[i][1] = (float) (layout90[6][1]
						+ ((float) (layout90[42][1] - layout90[6][1]) / 4.00) * ((i - 6) / 9)); // Y

			} else if (i % 9 == 7 && i != 7 && i != 43) {// 第8竖条的值

				layout90[i][0] = (float) (layout90[7][0]
						+ ((float) (layout90[43][0] - layout90[7][0]) / 4.00) * ((i - 7) / 9)); // X
				layout90[i][1] = (float) (layout90[7][1]
						+ ((float) (layout90[43][1] - layout90[7][1]) / 4.00) * ((i - 7) / 9)); // Y

			} else if (i % 9 == 8 && i != 8 && i != 44) {// 第9竖条的值

				layout90[i][0] = (float) (layout90[8][0]
						+ ((float) (layout90[44][0] - layout90[8][0]) / 4.00) * ((i - 8) / 9)); // X
				layout90[i][1] = (float) (layout90[8][1]
						+ ((float) (layout90[44][1] - layout90[8][1]) / 4.00) * ((i - 8) / 9)); // Y

			}

		}

		// 黑方棋盘部分
		for (int i = 45; i < 90; i++) {

			if (i % 9 == 0 && i != 45 && i != 81) { // 第一竖条的值

				layout90[i][0] = (float) (layout90[45][0]
						+ ((float) (layout90[81][0] - layout90[45][0]) / 4.00) * (i / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[45][1]
						+ ((float) (layout90[81][1] - layout90[45][1]) / 4.00) * (i / 9 - 5)); // Y

			} else if (i % 9 == 1 && i != 46 && i != 82) {// 第2竖条的值

				layout90[i][0] = (float) (layout90[46][0]
						+ ((float) (layout90[82][0] - layout90[46][0]) / 4.00) * ((i - 1) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[46][1]
						+ ((float) (layout90[82][1] - layout90[46][1]) / 4.00) * ((i - 1) / 9 - 5)); // Y

			} else if (i % 9 == 2 && i != 47 && i != 83) {// 第3竖条的值

				layout90[i][0] = (float) (layout90[47][0]
						+ ((float) (layout90[83][0] - layout90[47][0]) / 4.00) * ((i - 2) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[47][1]
						+ ((float) (layout90[83][1] - layout90[47][1]) / 4.00) * ((i - 2) / 9 - 5)); // Y

			} else if (i % 9 == 3 && i != 48 && i != 84) {// 第4竖条的值

				layout90[i][0] = (float) (layout90[48][0]
						+ ((float) (layout90[84][0] - layout90[48][0]) / 4.00) * ((i - 3) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[48][1]
						+ ((float) (layout90[84][1] - layout90[48][1]) / 4.00) * ((i - 3) / 9 - 5)); // Y

			} else if (i % 9 == 4 && i != 49 && i != 85) {// 第5竖条的值

				layout90[i][0] = (float) (layout90[49][0]
						+ ((float) (layout90[85][0] - layout90[49][0]) / 4.00) * ((i - 4) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[49][1]
						+ ((float) (layout90[85][1] - layout90[49][1]) / 4.00) * ((i - 4) / 9 - 5)); // Y

			} else if (i % 9 == 5 && i != 50 && i != 86) {// 第6竖条的值

				layout90[i][0] = (float) (layout90[50][0]
						+ ((float) (layout90[86][0] - layout90[50][0]) / 4.00) * ((i - 5) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[50][1]
						+ ((float) (layout90[86][1] - layout90[50][1]) / 4.00) * ((i - 5) / 9 - 5)); // Y

			} else if (i % 9 == 6 && i != 51 && i != 87) {// 第7竖条的值

				layout90[i][0] = (float) (layout90[51][0]
						+ ((float) (layout90[87][0] - layout90[51][0]) / 4.00) * ((i - 6) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[51][1]
						+ ((float) (layout90[87][1] - layout90[51][1]) / 4.00) * ((i - 6) / 9 - 5)); // Y

			} else if (i % 9 == 7 && i != 52 && i != 88) {// 第8竖条的值

				layout90[i][0] = (float) (layout90[52][0]
						+ ((float) (layout90[88][0] - layout90[52][0]) / 4.00) * ((i - 7) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[52][1]
						+ ((float) (layout90[88][1] - layout90[52][1]) / 4.00) * ((i - 7) / 9 - 5)); // Y

			} else if (i % 9 == 8 && i != 53 && i != 89) {// 第9竖条的值

				layout90[i][0] = (float) (layout90[53][0]
						+ ((float) (layout90[89][0] - layout90[53][0]) / 4.00) * ((i - 8) / 9 - 5)); // X
				layout90[i][1] = (float) (layout90[53][1]
						+ ((float) (layout90[89][1] - layout90[53][1]) / 4.00) * ((i - 8) / 9 - 5)); // Y

			}

		}

		float shortValueX = (layoutXYS.get(0).get(9) - layoutXYS.get(0).get(8)) / 6.0f; // 短一行的X坐标间距
		float shortValueY = (layoutXYS.get(1).get(9) - layoutXYS.get(1).get(8)) / 6.0f; // 短一行的Y坐标间距

		float longValueX = (layoutXYS.get(0).get(11) - layoutXYS.get(0).get(10)) / 8.0f; // 长一行的X坐标间距
		float longValueY = (layoutXYS.get(1).get(11) - layoutXYS.get(1).get(10)) / 8.0f; // 长一行的Y坐标间距

		for (int j = 0; j < layout16.length; j++) {
			if (j < 7) { // 短的一行
				layout16[j][0] = layoutXYS.get(0).get(8) + (shortValueX) * j;
				layout16[j][1] = layoutXYS.get(1).get(8) + (shortValueY) * j;

			} else { // 长的一行
				layout16[j][0] = layoutXYS.get(0).get(10) + (longValueX) * (j - 7);
				layout16[j][1] = layoutXYS.get(1).get(10) + (longValueY) * (j - 7);
			}
		}
		// Z轴坐标
		layoutZ[0] = layoutXYS.get(0).get(12);
		layoutZ[1] = layoutXYS.get(1).get(12);

		// for (int j = 0; j < twelveLayout.length; j++) {
		// if (j == 8) {
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// } else if(j==17){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j == 26){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j==35){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j==44){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j==53){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j==62){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j==71){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j==80){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// }else if(j==89){
		// System.out.println("("+twelveLayout[j][0] +",
		// "+twelveLayout[j][1]+"), " );
		// } else {
		// System.out.print("("+twelveLayout[j][0] +", "+twelveLayout[j][1]+"),
		// " );
		// }
		// }

	}

	/**
	 * 将获得的策略信息转化为坐标数组
	 *
	 * @param strategy 策略计算出的棋盘的行列信息
	 * @return 返回到是一个坐标的二维数组
	 */
	public float[][] conversion(int[] strategy) {

		float[][] layoutStartAndEnd = new float[2][3];

		int indexStart = (strategy[0] - 1) * 9 + (strategy[1] - 1);

		int indexEnd = (strategy[2] - 1) * 9 + (strategy[3] - 1);
		System.err.println(strategy[0] + "    " + strategy[1] + "    " + strategy[2] + "    " + strategy[3]);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				UIGlobalVar.textValue.setText("接收成功:棋子从( " + strategy[0] + " ，" + strategy[1] + " )    移动到( "
						+ strategy[2] + " ，" + strategy[3] + " )");
			}
		});
		// 起点的坐标
		layoutStartAndEnd[0][0] = layout90[indexStart][0];

		layoutStartAndEnd[0][1] = layout90[indexStart][1];

		layoutStartAndEnd[0][2] = layoutZ[0];// 位置较低
		// 终点的坐标
		layoutStartAndEnd[1][0] = layout90[indexEnd][0];

		layoutStartAndEnd[1][1] = layout90[indexEnd][1];

		layoutStartAndEnd[1][2] = layoutZ[1];// 位置较高

		return layoutStartAndEnd;

	}

	/**
	 * 连接PC线程
	 *
	 * @author yuancheng
	 *
	 */
	class ConnPCThread implements Runnable {
		private int code; // 连接的状态码

		private ChessMovesInterface chess;
		private ComboBox<String> comboBox;
		private ChessInfoListener listener;
		private PointerByReference pp; //

		/**
		 *
		 * @param whichConn       1连接PC 2连接ARM
		 * @param chess           pc接口
		 * @param brobot          arm接口
		 * @param comboBox        端口号
		 * @param listener        pc监听,如连接的是arm,此参数没用
		 * @param armDataListener armji
		 * @param pp
		 */
		public ConnPCThread(ChessMovesInterface chess, ComboBox<String> comboBox, ChessInfoListener listener,
				PointerByReference pp) {
			this.chess = chess;
			this.comboBox = comboBox;
			this.listener = listener;
			this.pp = pp;
		}

		@Override
		public void run() {

			new Thread(new Runnable() {

				@Override
				public void run() {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							alertConncet = new AlertCorrectOrErrorALertBox();
							alertConncet.display(101, "正在连接中", null);
						}
					});
				}
			}).start();
			code = chess.connectBrobot(comboBox.getValue(), 115200, listener, pp);
			System.out.println("连接成功code         " + code);
			if (code == 0) { // 连接成功
				try {
					chess.setCmdRepeat(pp.getValue(), 5);
					chess.setCmdTimeOut(pp.getValue(), 100);
					Thread.sleep(1000);
					alertConncet.closeWindow();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						UIGlobalVar.countChessBox = 0;
						UIGlobalVar.connPC = true; // 连接成功
						buttonConnMain.setText("断开连接");

						new AlertCorrectOrErrorALertBox().display(4, "连接成功", null);
					}
				});
			} else if (code == 1) {
				try {
					chess.connectBrobotPing(pp.getValue(), (byte) 0x02);
					Thread.sleep(200);
					chess.disConnectBrobot(pp.getValue());
					alertConncet.closeWindow();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new AlertCorrectOrErrorALertBox().display(code, "没有找到端口", null);

					}
				});

			} else if (code == 2) {
				try {
					chess.connectBrobotPing(pp.getValue(), (byte) 0x02);
					Thread.sleep(200);
					chess.disConnectBrobot(pp.getValue());
					alertConncet.closeWindow();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new AlertCorrectOrErrorALertBox().display(code, "端口被占用", null);
					}
				});

			} else if (code == 3) {
				try {
					chess.connectBrobotPing(pp.getValue(), (byte) 0x02);
					Thread.sleep(200);
					chess.disConnectBrobot(pp.getValue());
					alertConncet.closeWindow();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new AlertCorrectOrErrorALertBox().display(code, "端口连接失败", null);
					}
				});

			}

		}

	}

	/**
	 * 连接PC线程
	 *
	 * @author yuancheng
	 *
	 */
	class ConnARMThread implements Runnable {
		private int code; // 连接的状态码

		private BrobotUserInterface brobot;
		private ComboBox<String> comboBox;
		private ArmDataListener listener;
		private PointerByReference pp; //

		/**
		 *
		 * @param whichConn       1连接PC 2连接ARM
		 * @param chess           pc接口
		 * @param brobot          arm接口
		 * @param comboBox        端口号
		 * @param listener        pc监听,如连接的是arm,此参数没用
		 * @param armDataListener armji
		 * @param pp
		 */
		public ConnARMThread(BrobotUserInterface brobot, ComboBox<String> comboBox, ArmDataListener listener,
				PointerByReference pp) {
			this.brobot = brobot;
			this.comboBox = comboBox;
			this.listener = listener;
			this.pp = pp;
		}

		@Override
		public void run() {

			new Thread(new Runnable() {

				@Override
				public void run() {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							alertConncet = new AlertCorrectOrErrorALertBox();
							alertConncet.display(101, "正在连接中", null);
						}
					});
				}
			}).start();
			code = brobot.connectBrobot(comboBox.getValue(), 115200, listener, pp);
			System.out.println("连接成功code         " + code);
			if (code == 0) { // 连接成功
				try {
					brobot.setCmdRepeat(pp.getValue(), 5);
					brobot.setCmdTimeOut(pp.getValue(), 100);
					alertConncet.closeWindow();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						UIGlobalVar.connArm = true; // 连接成功
						buttonConnArm.setText("断开连接");

						new AlertCorrectOrErrorALertBox().display(4, "连接成功", null);
					}
				});
			} else if (code == 1) {
				try {
					brobot.connectBrobotPing(pp.getValue(), (byte) 0x02);
					Thread.sleep(200);
					brobot.disConnectBrobot(pp.getValue());
					alertConncet.closeWindow();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new AlertCorrectOrErrorALertBox().display(code, "没有找到端口", null);

					}
				});

			} else if (code == 2) {
				try {
					brobot.connectBrobotPing(pp.getValue(), (byte) 0x02);
					Thread.sleep(200);
					brobot.disConnectBrobot(pp.getValue());
					alertConncet.closeWindow();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new AlertCorrectOrErrorALertBox().display(code, "端口被占用", null);
					}
				});

			} else if (code == 3) {
				try {
					brobot.connectBrobotPing(pp.getValue(), (byte) 0x02);
					Thread.sleep(200);
					brobot.disConnectBrobot(pp.getValue());
					alertConncet.closeWindow();
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new AlertCorrectOrErrorALertBox().display(code, "端口连接失败", null);
					}
				});

			}

		}

	}

	/***************** 连接机械臂的 *************************/
	class MyListenerArm extends ArmDataListener {
		@Override
		public void onDataFrame(ArmDataFrame frame) {
			// 将xyz的坐标赋值给全局变量,以供机械臂1使用
			UIGlobalVar.COMM_X_Layout1 = frame.getX();
			UIGlobalVar.COMM_Y_Layout1 = frame.getY();
			UIGlobalVar.COMM_Z_Layout1 = frame.getZ();

			// 界面显示的实时坐标
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					labelLayout.setText("x:" + UIGlobalVar.COMM_X_Layout1 + "  y:" + UIGlobalVar.COMM_Y_Layout1 + "  z:"
							+ UIGlobalVar.COMM_Z_Layout1);
				}
			});
			if (frame.getBtnSignal() == 1) {
				count10Second = -1;// 到技术10秒立即结束

				System.out.println("countcountcount     " + count10Second);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						UIGlobalVar.textValue.setText("坐标超限,请重新设置");
					}
				});
			}
			super.onDataFrame(frame);
		}
	}

	/***************** 连接裁判机的 *************************/

	class MyListenerPC extends ChessInfoListener {

		@Override
		public void onDataChessInfo(chessInfoModel frame) {

			values = frame.getParam(); // 获取的byte数组
			System.out.println("接收到了从裁判端的数据                 " + frame.getParam().length);

			Platform.runLater(new Runnable() {
				public void run() {
					UIGlobalVar.textValue.setText("接收才判断信息成功");
					// new
					// AlertCorrectOrErrorALertBox().display(101,
					// "运动完毕,请点击\"发送\"按钮继续",
					// null);

				}
			});

			super.onDataChessInfo(frame);

		}
	}

	/**
	 * 发送一个运动指令10秒的倒计时,如超过10秒没有到目的地则结束所有运动,并且关闭控制信号
	 * 
	 * @author yuancheng
	 *
	 */
	public class TenMinutesThread implements Runnable {

		@Override
		public void run() {
			count10Second = 10;
			tenMinutesFlag = true;
			while (tenMinutesFlag) {
				try {
					System.out.println(count10Second);
					if (count10Second < 0) {
						// 弹出移动异常的提示

						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								new AlertCorrectOrErrorALertBox().display(101, "移动机械臂失败,结束当前指令", null);
							}
						});
						moveFlag = false;// 结束运动点的循环
						tenMinutesFlag = false;// 结束本循环
						startMove = false;// 不执行移开棋子后再走棋子的步骤
						UIGlobalVar.brobotArm.controlAirPumpAction(UIGlobalVar.ppArm.getValue(), (byte) 0x00); // 停气泵
						Thread.sleep(100);
						UIGlobalVar.brobotArm.setControlSignel(UIGlobalVar.ppArm.getValue(), (byte) 0x05); // 关闭运动信号,结束当前操作

					}
					count10Second = count10Second - 1;

					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
