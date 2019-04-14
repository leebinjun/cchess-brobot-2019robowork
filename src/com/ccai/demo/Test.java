package com.ccai.demo;

import java.lang.*;
import java.io.*;
import java.util.HashMap;

class Main
{

    public static void main(String args[]) throws InterruptedException 
    {
        byte[] values = new byte[]{1, 2, 4, 5, 6, 5, 4, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17, 0, 17, 0, 17, 0, 17, 0, 17, 0, 13, 0, 0, 0, 0, 0, 13, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12, 14, 15, 16, 15, 14, 12, 11 };

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
		
        System.out.print("res: ");
        System.out.println(res);
		// return res;
    }
    


}
