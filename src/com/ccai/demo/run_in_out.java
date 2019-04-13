package com.ccai.demo;

import java.lang.*;
import java.io.*;

class Test {
	static Process p;
	static public void openApplication(String filePath) throws InterruptedException{
		
		StringBuilder sb =new StringBuilder();
		
        try{
			System.out.print("hello!\n");
			String cm2 = "position fen rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR b - - 0 1";
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
			while ((line=bufferedReader.readLine()) != null) {
				if(line.length() > 12)
    				ress = line.substring(9, 13);
				System.out.println(line);
				System.out.print("res: ");
				System.out.println(ress);
			}
			System.out.print("byebye!\n");
			
			System.out.print("final res: ");
			System.out.println(ress);


	    }catch (IOException ex) {
			// ex.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws InterruptedException 
	{
		openApplication(".\\dll\\cyclone.exe");
	}
}
