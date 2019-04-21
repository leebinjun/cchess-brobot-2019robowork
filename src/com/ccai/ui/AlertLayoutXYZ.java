package com.ccai.ui;

import java.util.ArrayList;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 90个坐标点的微调
 *
 * @author yuancheng
 *
 */
public class AlertLayoutXYZ {
	private Stage window;
	private Pane paneLayout;
	private Label close;
	private Label labelText;// 弹出框主文本
	private Scene scene;
	private Button buttonEnsure; // 保存的按钮


	//新的输入框
	private TextField redJu1X;
	private TextField redJu1Y;
	private TextField redJu2X;
	private TextField redJu2Y;
	private TextField blackJu1X;
	private TextField blackJu1Y;
	private TextField blackJu2X;
	private TextField blackJu2Y;
	private TextField oneTextFieldX;
	private TextField oneTextFieldy;
	private TextField twoTextFieldx;
	private TextField twoTextFieldy;
	private TextField threeTextFieldx;
	private TextField threeTextFieldy;
	private TextField fourTextFieldx;
	private TextField fourTextFieldy;
	private TextField startTextFieldz;
	private TextField endTextFieldz;
	private TextField[] textFieldX = new TextField[13]; //以上X坐标控件的集合 ,包含Z1
	private TextField[] textFieldY = new TextField[13]; //以上Y坐标控件的集合,包含Z2

	private ArrayList<ArrayList<Float>> values = new ArrayList<>(); //从xml读取的值
	private TextField red36x;
	private TextField red36y;
	private TextField red45x;
	private TextField red45y;
	private TextField black46x;
	private TextField black46y;
	private TextField black54x;
	private TextField black54y;

	public AlertLayoutXYZ() {
		paneLayout = new Pane();
		paneLayout.setPrefSize(450, 300);
		window = new Stage();

	}

	public void display() {
		window.getIcons().add(new Image("/icon/LOGO.png"));
		window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.TRANSPARENT);

		close = new Label("", new ImageView("/com/ccai/pic/btn_close_pop_up.png"));
		close.setPrefSize(30, 30);
		close.setLayoutX(420);

		labelText = new Label();
		labelText.setAlignment(Pos.CENTER);
		labelText.setPadding(new Insets(10, 20, 0, 20));
		labelText.setWrapText(true);
		labelText.setPrefWidth(450);
		labelText.setStyle("-fx-font-size:18");
		labelText.setId("alertText");
		labelText.setText("棋盘坐标设置");

		// initLayoutPutBox();//放置棋子的盒子坐标
		// initLayout();//初始化坐标的布局
		values = WriteValueToXml.readFromXML();
		initChessLayoutValue();// 初始化布局
		initChessPutDownValue();
		initTextField();
		buttonClick();
		paneLayout.getChildren().addAll(labelText, close);
		scene = new Scene(paneLayout);
		paneLayout.setId("alertGreyBackground");
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		scene.setFill(new Color(1, 1, 1, 0)); // 透明色
		window.setScene(scene);

		// 使用showAndWait()先处理这个窗口，而如果不处理，main中的那个窗口不能响应
		window.showAndWait();

	}

	/**
	 * 坐标框统一设置样式
	 *
	 * @param textField
	 */
	public void setStyleTextField(TextField textField) {

		textField.setPrefWidth(60);
		textField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("^(\\-?[0-9]{0,3})(\\.[0-9]{0,2})?$")) {// 正则匹配判断
					textField.setText(oldValue);
				}
			}
		});

	}

	/**
	 * 统一设置label
	 */
	public void setStyleLabel(Label label) {
		label.setAlignment(Pos.CENTER);
		label.setId("alertText");
		label.setWrapText(true);

	}

	/**
	 * 棋盘四个角的坐标设置布局初始化
	 */
	public void initChessLayoutValue() {
		// 红車1
		Label labelRedJu1 = new Label("红車1");
		setStyleLabel(labelRedJu1);
		labelRedJu1.setLayoutX(20);
		labelRedJu1.setLayoutY(40);

		redJu1X = new TextField();
		setStyleTextField(redJu1X);
		redJu1X.setLayoutX(70);
		redJu1X.setLayoutY(40);
		redJu1X.setText(values.get(0).get(0)+"");

		redJu1Y = new TextField();
		setStyleTextField(redJu1Y);
		redJu1Y.setLayoutX(140);
		redJu1Y.setLayoutY(40);
		redJu1Y.setText(values.get(1).get(0)+"");

		// 红車2
		Label labelRedJu2 = new Label("红車2");
		setStyleLabel(labelRedJu2);
		labelRedJu2.setLayoutX(245);
		labelRedJu2.setLayoutY(40);

		redJu2X = new TextField();
		setStyleTextField(redJu2X);
		redJu2X.setLayoutX(295);
		redJu2X.setLayoutY(40);
		redJu2X.setText(values.get(0).get(1)+"");

		redJu2Y = new TextField();
		setStyleTextField(redJu2Y);
		redJu2Y.setLayoutX(365);
		redJu2Y.setLayoutY(40);
		redJu2Y.setText(values.get(1).get(1)+"");

		//第36个位置
		Label label36 = new Label("第36");
		setStyleLabel(label36);
		label36.setLayoutX(20);
		label36.setLayoutY(70);

		red36x = new TextField();
		setStyleTextField(red36x);
		red36x.setLayoutX(70);
		red36x.setLayoutY(70);
		red36x.setText(values.get(0).get(2)+"");

		red36y = new TextField();
		setStyleTextField(red36y);
		red36y.setLayoutX(140);
		red36y.setLayoutY(70);
		red36y.setText(values.get(1).get(2)+"");

		// 第45个位置
		Label label45 = new Label("第45");
		setStyleLabel(label45);
		label45.setLayoutX(245);
		label45.setLayoutY(70);

		red45x = new TextField();
		setStyleTextField(red45x);
		red45x.setLayoutX(295);
		red45x.setLayoutY(70);
		red45x.setText(values.get(0).get(3)+"");

		red45y = new TextField();
		setStyleTextField(red45y);
		red45y.setLayoutX(365);
		red45y.setLayoutY(70);
		red45y.setText(values.get(1).get(3)+"");

		// 第46
		Label label46= new Label("第46");
		setStyleLabel(label46);
		label46.setLayoutX(20);
		label46.setLayoutY(100);

		black46x = new TextField();
		setStyleTextField(black46x);
		black46x.setLayoutX(70);
		black46x.setLayoutY(100);
		black46x.setText(values.get(0).get(4)+"");

		black46y = new TextField();
		setStyleTextField(black46y);
		black46y.setLayoutX(140);
		black46y.setLayoutY(100);
		black46y.setText(values.get(1).get(4)+"");

		// 第54
		Label label54 = new Label("第54");
		setStyleLabel(label54);
		label54.setLayoutX(245);
		label54.setLayoutY(100);

		black54x = new TextField();
		setStyleTextField(black54x);
		black54x.setLayoutX(295);
		black54x.setLayoutY(100);
		black54x.setText(values.get(0).get(5)+"");

		black54y = new TextField();
		setStyleTextField(black54y);
		black54y.setLayoutX(365);
		black54y.setLayoutY(100);
		black54y.setText(values.get(1).get(5)+"");

		// 黑車1
		Label labelBlackJu1 = new Label("黑車1");
		setStyleLabel(labelBlackJu1);
		labelBlackJu1.setLayoutX(20);
		labelBlackJu1.setLayoutY(130);

		blackJu1X = new TextField();
		setStyleTextField(blackJu1X);
		blackJu1X.setLayoutX(70);
		blackJu1X.setLayoutY(130);
		blackJu1X.setText(values.get(0).get(6)+"");

		blackJu1Y = new TextField();
		setStyleTextField(blackJu1Y);
		blackJu1Y.setLayoutX(140);
		blackJu1Y.setLayoutY(130);
		blackJu1Y.setText(values.get(1).get(6)+"");

		// 黑車2
		Label labelBlackJu2 = new Label("黑車2");
		setStyleLabel(labelBlackJu2);
		labelBlackJu2.setLayoutX(245);
		labelBlackJu2.setLayoutY(130);

		blackJu2X = new TextField();
		setStyleTextField(blackJu2X);
		blackJu2X.setLayoutX(295);
		blackJu2X.setLayoutY(130);
		blackJu2X.setText(values.get(0).get(7)+"");

		blackJu2Y = new TextField();
		setStyleTextField(blackJu2Y);
		blackJu2Y.setLayoutX(365);
		blackJu2Y.setLayoutY(130);
		blackJu2Y.setText(values.get(1).get(7)+"");


		paneLayout.getChildren().addAll(labelRedJu1, redJu1X, redJu1Y, labelRedJu2, redJu2X, redJu2Y,label36,red36x,red36y,
				label45,red45x,red45y,label46,black46x,black46y,label54,black54x,black54y,labelBlackJu1,
				blackJu1X, blackJu1Y, labelBlackJu2, blackJu2X, blackJu2Y);

	}

	/**
	 * 棋盘放置点坐标
	 */
	public void initChessPutDownValue() {
		Separator separator = new Separator();
		separator.setPrefWidth(1920);
		separator.setLayoutY(160);

		// 1:
		Label labelOne = new Label("1:");
		setStyleLabel(labelOne);
		labelOne.setLayoutX(50);
		labelOne.setLayoutY(170);

		oneTextFieldX = new TextField();
		setStyleTextField(oneTextFieldX);
		oneTextFieldX.setLayoutX(70);
		oneTextFieldX.setLayoutY(170);
		oneTextFieldX.setText(values.get(0).get(8)+"");

		oneTextFieldy = new TextField();
		setStyleTextField(oneTextFieldy);
		oneTextFieldy.setLayoutX(140);
		oneTextFieldy.setLayoutY(170);
		oneTextFieldy.setText(values.get(1).get(8)+"");

		// 第二个
		Label labelTwo = new Label("2:");
		setStyleLabel(labelTwo);
		labelTwo.setLayoutX(245);
		labelTwo.setLayoutY(170);

		twoTextFieldx = new TextField();
		setStyleTextField(twoTextFieldx);
		twoTextFieldx.setLayoutX(265);
		twoTextFieldx.setLayoutY(170);
		twoTextFieldx.setText(values.get(0).get(9)+"");

		twoTextFieldy = new TextField();
		setStyleTextField(twoTextFieldy);
		twoTextFieldy.setLayoutX(335);
		twoTextFieldy.setLayoutY(170);
		twoTextFieldy.setText(values.get(1).get(9)+"");

		// 第三个
		Label labelThree = new Label("3:");
		setStyleLabel(labelThree);
		labelThree.setLayoutX(20);
		labelThree.setLayoutY(200);

		threeTextFieldx = new TextField();
		setStyleTextField(threeTextFieldx);
		threeTextFieldx.setLayoutX(40);
		threeTextFieldx.setLayoutY(200);
		threeTextFieldx.setText(values.get(0).get(10)+"");

		threeTextFieldy = new TextField();
		setStyleTextField(threeTextFieldy);
		threeTextFieldy.setLayoutX(110);
		threeTextFieldy.setLayoutY(200);
		threeTextFieldy.setText(values.get(1).get(10)+"");

		// 第四个
		Label labelFour = new Label("4:");
		setStyleLabel(labelFour);
		labelFour.setLayoutX(275);
		labelFour.setLayoutY(200);

		fourTextFieldx = new TextField();
		setStyleTextField(fourTextFieldx);
		fourTextFieldx.setLayoutX(295);
		fourTextFieldx.setLayoutY(200);
		fourTextFieldx.setText(values.get(0).get(11)+"");

		fourTextFieldy = new TextField();
		setStyleTextField(fourTextFieldy);
		fourTextFieldy.setLayoutX(365);
		fourTextFieldy.setLayoutY(200);
		fourTextFieldy.setText(values.get(1).get(11)+"");

		Separator separator2 = new Separator();
		separator2.setPrefWidth(1920);
		separator2.setLayoutY(230);

		// 取棋点Z轴
		Label labelStart = new Label("Z1:");
		setStyleLabel(labelStart);
		labelStart.setLayoutX(20);
		labelStart.setLayoutY(240);

		startTextFieldz = new TextField();
		setStyleTextField(startTextFieldz);
		startTextFieldz.setLayoutX(70);
		startTextFieldz.setLayoutY(240);
		startTextFieldz.setText(values.get(0).get(12)+"");

		// 终点Z轴
		Label labelEnd = new Label("Z2:");
		setStyleLabel(labelEnd);
		labelEnd.setLayoutX(295);
		labelEnd.setLayoutY(240);

		endTextFieldz = new TextField();
		setStyleTextField(endTextFieldz);
		endTextFieldz.setLayoutX(365);
		endTextFieldz.setLayoutY(240);
		endTextFieldz.setText(values.get(1).get(12)+"");



		paneLayout.getChildren().addAll(labelOne, oneTextFieldX, oneTextFieldy, labelTwo, twoTextFieldx, twoTextFieldy,
				labelThree, threeTextFieldx, threeTextFieldy, labelFour, fourTextFieldx, fourTextFieldy, labelStart,
				startTextFieldz, labelEnd, endTextFieldz, separator, separator2);

		buttonEnsure = new Button("保存");
		buttonEnsure.setId("button");
		buttonEnsure.setLayoutX((450 - 80) / 2);
		buttonEnsure.setLayoutY(265);
		paneLayout.getChildren().add(buttonEnsure);

	}
	/**
	 * 将坐标框添加到集合
	 */
	public void initTextField(){
		//x包含z1
		textFieldX[0]=redJu1X;
		textFieldX[1]=redJu2X;
		textFieldX[2]=red36x;
		textFieldX[3]=red45x;
		textFieldX[4]=black46x;
		textFieldX[5]=black54x;
		textFieldX[6]=blackJu1X;
		textFieldX[7]=blackJu2X;
		//棋盒
		textFieldX[8]=oneTextFieldX;
		textFieldX[9]=twoTextFieldx;
		textFieldX[10]=threeTextFieldx;
		textFieldX[11]=fourTextFieldx;
		//z
		textFieldX[12]=startTextFieldz;

		//y包含z2
		textFieldY[0]=redJu1Y;
		textFieldY[1]=redJu2Y;
		textFieldY[2]=red36y;
		textFieldY[3]=red45y;
		textFieldY[4]=black46y;
		textFieldY[5]=black54y;
		textFieldY[6]=blackJu1Y;
		textFieldY[7]=blackJu2Y;

		//棋盒
		textFieldY[8]=oneTextFieldy;
		textFieldY[9]=twoTextFieldy;
		textFieldY[10]=threeTextFieldy;
		textFieldY[11]=fourTextFieldy;
		//z轴
		textFieldY[12]=endTextFieldz;



	}
	/**
	 * 点击事件
	 */
	public void buttonClick() {

		buttonEnsure.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				WriteValueToXml.writeToXML(textFieldX,textFieldY);

			}
		});
		// 关闭窗体
		close.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				window.close();

			}
		});

	}



	/**
	 * 关闭窗体的类
	 *
	 * @author Administrator
	 *
	 */
	class CloseWindow implements Runnable {

		@Override
		public void run() {
			try {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						window.close();
					}
				});
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
