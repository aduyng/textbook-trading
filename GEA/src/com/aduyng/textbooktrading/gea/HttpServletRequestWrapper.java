package com.aduyng.textbooktrading.gea;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestWrapper {
	private HttpServletRequest request = null;

	public HttpServletRequestWrapper(HttpServletRequest request) {
		this.request = request;
	}

	public String getString(String index, boolean filter) {
		String val = request.getParameter(index);
		
		if (null != val && filter) {
			return val.trim();
		}

		return val;
	}

	public String getString(String index) {
		return getString(index, true);
	}

	public Long getLong(String index, long defaultValue) {
		try {
			String idString = this.getString(index);

			if (null == idString) {
				return defaultValue;
			}
			return Long.parseLong(idString);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public Long getLong(String index){
		return getLong(index, 0);
	}
	
	public int getInteger(String index, int defaultValue) {
		try {
			String idString = this.getString(index);

			if (null == idString) {
				return defaultValue;
			}
			return Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public int getInteger(String index){
		return getInteger(index, 0);
	}
	
	public int getPositiveInteger(String index, int defaultValue){
		int val = getInteger(index);
		if( val <= 0 ){
			return defaultValue;
		}
		
		return val;
	}
	
	public int getPositiveInteger(String index){
		return getPositiveInteger(index, 0);
	}
	
	
	public boolean getBoolean(String index, boolean defaultValue){
		String s = getString(index);
		
		try{
			return Boolean.parseBoolean(s);
			
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	public boolean getBoolean(String index){
		return getBoolean(index, false);
	}
	
	public double getDouble(String index, double defaultValue) {
		try {
			String idString = this.getString(index);

			if (null == idString) {
				return defaultValue;
			}
			return Double.parseDouble(idString);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public double getDouble(String index){
		return getDouble(index, 0);
	}
	
	
}
