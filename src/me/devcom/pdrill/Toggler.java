package me.devcom.pdrill;

public class Toggler {
	String on;
	String off;
	String data;
	
	public Toggler( String on, String off){
		this.on = on;
		this.off = off;
		this.data = on;
	}
	
	public void toggle(){
		if( data.equals( on )){
			data = off;
		}else{
			data = on;
		}
	}
	
	public String data(){
		return data;
	}
}
