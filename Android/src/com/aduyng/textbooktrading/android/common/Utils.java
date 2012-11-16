package com.aduyng.textbooktrading.android.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Utils {

	public static String convertStreamToString(InputStream is) {
		 
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	public static byte[] convertBitmapToByteArray(Bitmap bitmap){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos); 
        return bos.toByteArray();
	}
	
//	public static String convertBitmapToBase64EncodedString(Bitmap bitmap){
//        byte[] bitmapData = Utils.convertBitmapToByteArray(bitmap);
//        
//        byte[] bitmapDataBase64Encoded = Base64.encode(bitmapData, Base64.DEFAULT);
//		return new String(bitmapDataBase64Encoded);
//	}
//	
//	public static Bitmap convertBase64EncodedStringToBitmap(String base64EncodedString){
//		byte[] logoBytes = Base64.decode(base64EncodedString.getBytes(), Base64.DEFAULT);
//		return BitmapFactory.decodeByteArray(logoBytes, 0, logoBytes.length);
//	}
	
}
