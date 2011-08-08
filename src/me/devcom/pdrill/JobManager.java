package me.devcom.pdrill;

import java.util.ArrayList;
import java.util.logging.Logger;


public class JobManager {
	
	public boolean valid = false;
	public boolean hasJob = false;
	public final ArrayList< Job > JobDB = new ArrayList< Job >();

	public final Logger logger = Logger.getLogger("Minecraft");
	public final String prefix = "[PDrill] ";

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
			
			if(jobStr.matches( "LOOP.*\\(" )){
				Integer start = i+1;
				if(start >= script.size()){
					drill.owner.sendMessage( prefix + "Loop ["+ jobStr +"] is invalid");
					return false;
				}
				
				Integer loopCount = Integer.parseInt( jobStr.substring(4, jobStr.indexOf( "(") ) );
				
				Integer j = start;
				Integer end = -1;
				String node = script.get(j);
				while(!node.equalsIgnoreCase(")")){
					j++;
					if( j >= script.size() ){
						break;
					}
					node = script.get(j);
				}
				
				
				if( node.equalsIgnoreCase( ")" )){
					end = j;
				}else{
					drill.owner.sendMessage( prefix + "Loop ["+ jobStr +"] probably has no closing parentesis");
					return false;
				}
				
				for(Integer n = 0; n < loopCount; n++) 
				for(Integer k = start; k < end; k++){
					jobStr = script.get(k);
					dir = jobStr.substring(0, 1);
					len =  jobStr.substring(1);
					
					if( !checkJob( dir, len) ){
						drill.owner.sendMessage( prefix + "Wrong job string [" + jobStr + "]");
						JobDB.clear();
						return false;
					}
					
					Job job = new Job(drill, dir, Integer.parseInt( len ));	
					JobDB.add(job);
				}
				
				i = end;
				continue;
			}
			
			if( !checkJob( dir, len) ){
				drill.owner.sendMessage( prefix + "Wrong job [" + jobStr + "]");
				JobDB.clear();
				return false;
			}
			
			Job job = new Job(drill, dir, Integer.parseInt( len ));	
			JobDB.add(job);
		}

		hasJob = true;
		return true;
	}
	
	private boolean checkJob(String dir, String len) {
		if( !dir.matches( "f|b|r|l|u|d") || !len.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")){
			return false;
		}
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
	
	public Job getCurrentJob(){
		Job job = JobDB.get( 0 );
		if(job.valid){
			job.process();
			return job;
		}else{
			JobDB.remove( 0 );
			return JobDB.get( 0 );
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
