package com.ccai.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javafx.scene.control.TextField;

public class WriteValueToXml {
	/**
	 * 将输入的值保存到xml中
	 *
	 * @param layoutX
	 * @param layoutY
	 */
	public static void writeToXML(TextField[] layoutX, TextField[] layoutY) {

		Document doc = DocumentHelper.createDocument();
		Element rootelem =  doc.addElement("LayoutValue");//添加跟节点

		for (int i = 0; i < 13; i++) {
			if (i<8) {
				Element elementChildren = rootelem.addElement("chess_"+(i+1));
				if (layoutX[i].getText().equals("")) {
					elementChildren.addElement("x").addText("0");
				} else {
					elementChildren.addElement("x").addText(layoutX[i].getText());
				}
				if (layoutY[i].getText().equals("")) {
					elementChildren.addElement("y").addText("-247");
				} else {
					elementChildren.addElement("y").addText(layoutY[i].getText());
				}
			} else if (i>=8 &&i<12) {
				Element elementChildren = rootelem.addElement("box_"+(i+1));
				if (layoutX[i].getText().equals("")) {
					elementChildren.addElement("x").addText("0");
				} else {
					elementChildren.addElement("x").addText(layoutX[i].getText());
				}

				if (layoutY[i].getText().equals("")) {
					elementChildren.addElement("y").addText("-247");
				} else {
					elementChildren.addElement("y").addText(layoutY[i].getText());
				}

			} else {//Z轴
				Element elementChildren = rootelem.addElement("z");
				if (layoutX[i].getText().equals("")) {
					elementChildren.addElement("z1").addText("150");
				} else {
					elementChildren.addElement("z1").addText(layoutX[i].getText());
				}

				if (layoutY[i].getText().equals("")) {
					elementChildren.addElement("z2").addText("150");
				} else {
					elementChildren.addElement("z2").addText(layoutY[i].getText());
				}
			}

		}
		try {
		FileOutputStream fileOutputStream = new FileOutputStream(new File("layoutXML/layoutValue.xml"));
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		XMLWriter writer = new XMLWriter(fileOutputStream, format);

		writer.write(doc);
		writer.close();

		new AlertCorrectOrErrorALertBox().display(4, "保存成功", null);
		} catch (Exception e) {
		}

	}
	/**
	 * 读取xml的值
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<ArrayList<Float>> readFromXML() {
		ArrayList<ArrayList<Float>> values = new ArrayList<>();

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

		values.add(valuesX);
		values.add(valuesY);
		return values;
	}


}
