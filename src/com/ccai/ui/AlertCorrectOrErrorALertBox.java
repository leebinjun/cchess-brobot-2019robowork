package com.ccai.ui;


import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 弹窗的设置成功或者失败的界面（界面背景为灰色）
 *
 * 如果返回码是4,则只需要输入 4 和 String mark, 而String remark则为null,就只显示 String
 * mark的文字提示信息,然后3秒消失 如果返回码不是4,则只需要输入 4 和 String mark, 而String
 * remark看情况输入,就只显示error code + String mark+String remark 的错误提示信息,窗体不消失
 *
 * @author yuancheng
 *
 */
public class AlertCorrectOrErrorALertBox {
	private Stage window;
	private Pane paneDelete;
	private Label close;
	private Label labelText;// 弹出框主文本
	private Label labelText1;// 弹出框备注文本
	private Scene scene;

	public AlertCorrectOrErrorALertBox() {
		paneDelete = new Pane();
		paneDelete.setPrefSize(300,150);
		window = new Stage();

	}

	public void display(Object o, String mark, String remark) {
		window.getIcons().add(new Image("/icon/LOGO.png"));
		// modality要使用Modality.APPLICATION_MODEL
		window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.TRANSPARENT);

		close = new Label("", new ImageView("/com/wxyztech/pic/btn_close_pop_up.png"));
		close.setPrefSize(30, 30);
		close.setLayoutX(270);
		close.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				window.close();

			}
		});

		labelText = new Label();
		labelText.setAlignment(Pos.CENTER);
		labelText.setPadding(new Insets(0, 20, 0, 20));
		labelText.setWrapText(true);
		labelText.setPrefWidth(300);
		if (remark == null) {
			labelText.setPrefHeight(150);
		} else {
			labelText.setPrefHeight(75);
			labelText.setAlignment(Pos.BOTTOM_CENTER);
		}
		labelText.setStyle("-fx-text-fill:#fe7156;-fx-font-size:18");
		labelText.setId("alertText");
		if ((int) o == 4) {
			labelText.setText(mark);
			new Thread(new CloseWindow()).start();
		} else if (((int) o >= 1 && (int) o < 4) || ((int) o > 4 && (int) o <= 13)) {// 定义的错误码
			labelText.setText(mark + ",错误码:" + o  ); // 错误码
		} else {// 其他信息
			labelText.setText(mark);
		}

		labelText1 = new Label(remark);
		labelText1.setAlignment(Pos.TOP_CENTER);
		labelText1.setPrefWidth(300);
		labelText1.setPrefHeight(75);
		labelText1.setWrapText(true);
		labelText1.setLayoutY(75 );
		labelText1.setStyle("-fx-font-size:18");
		labelText1.setId("alertText");

		paneDelete.getChildren().addAll(labelText, labelText1,close);
		scene = new Scene(paneDelete);
		paneDelete.setId("alertGreyBackground");
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		scene.setFill(new Color(1, 1, 1, 0)); //透明色
		window.setScene(scene);

		// 使用showAndWait()先处理这个窗口，而如果不处理，main中的那个窗口不能响应
		window.showAndWait();

	}

	public void closeWindow(){
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				window.close();
				System.out.println("关闭了连接弹窗");
			}
		});
	}
	/**
	 * 关闭窗体的类
	 * @author Administrator
	 *
	 */
	class CloseWindow implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(3000);
				closeWindow();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}




}
