package com.activ.bbpi.bean;

/** simple bean capturing user request and response data */
public class Bean {
	
	public static String NAME = "bean";

	////input///
  
	////output///
	private StringBuffer msg = new StringBuffer();   //message
	public boolean error = false;   //error or not

  public Bean(){}
  
  public void appendMessage(String m){
    msg.append(m);
  }
  
  public void appendErrorMessage(String m){
    msg.append(m);
    error = true;
  }

  public String getMessage() {return msg.toString();}
  public boolean hasMessage(){return msg.length() > 0;}
 

} 
