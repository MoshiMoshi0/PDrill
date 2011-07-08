package me.devcom.pdrill;

import java.util.ArrayList;
import java.util.logging.Logger;


public class JobManager {
	
	public boolean valid = false;
	public boolean hasJob = false;
	public final ArrayList< Job > JobDB = new ArrayList< Job >();
	public final Logger logger = Logger.getLogger("Minecraft");

	Drill drill;
	
	public JobManager(Drill drill){
		this.drill = drill;
		
		valid = true;
	}
	
	public boolean processScript( ArrayList<String> script){
		if(!valid) return false;
		
		for(int i = 0; i < script.size(); i++){
			String jobStr = script.get(i);
			String dir = jobStr.substring(0, 1);
			String len =  jobStr.substring(1);
			
			if( !dir.matches( "f|b|r|l|u|d") || !len.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){
				JobDB.clear();
				return false;
			}
			
			Job job = new Job(drill, dir, Integer.parseInt( len ));
			JobDB.add(job);
		}
		
		hasJob = true;
		return true;
	}
	
	public void doJob(){
		if(!valid || !hasJob) return;
		
		if(JobDB.size() >= 1){
			Job job = JobDB.get( 0 );
			if(job.valid){
				job.process();
			}else{
				JobDB.remove( 0 );
			}
		}else{
			hasJob = false;
		}
	}
	
	public Integer addScript(ArrayList<String> script){
		if(hasJob){
			return 2;
		}
		
		boolean ret = processScript( script );
		
		if(ret) return 1;
		else return 0;
	}
	
}
