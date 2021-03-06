package io.github.wreed12345;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionAsker implements Runnable{
	
	//booleans are atomic already woop woop
	//is this needed?
	private volatile boolean questioning;
	private volatile boolean stopAsking = false;//volatile so other threads can see the new value
	private long time = 0;
	private AtomicLong startTime = new AtomicLong(0);
	private final long intermediateTime = 10000;//10 seconds
	
	//may not be necesary, but would be for multiple stage questions
	private AtomicInteger questionSequence;
	//populate with various questions
	private ArrayList<Question> questions = new ArrayList<>();
	
	//TODO: reset start time once something has been said in chat
	
	@Override
	public void run() {
		startTime.set(System.currentTimeMillis());
		while (true) {
			time = System.currentTimeMillis();
			//if 10 seconds is up ask a question
			//probably should synchronize so not acting on stale values
			if((time - startTime.get() > intermediateTime) && !questioning && !stopAsking) {
				questioning = true;
				System.out.println("<todo> ask a question");
				resetTime();
				//TODO: ask a question
			}
			//if 20 seconds disengage question mode and say something
			//probably should synchronize so not acting on stale values
			if((time - startTime.get() > 20000) && !stopAsking){
				questioning = false;
				System.out.println("<todo>cease asking question");
				stopAsking = true;
				resetTime();
			}
			
			//dont spam the loop stalling a second should be ok
			try {
				//get rid of wait, considered an 'old' method of stalling thread,
				//need java concurrency in practice
				synchronized(this){
					wait(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * True once designated time interval has ran out. (10 seconds)
	 * Disengages after 30 seconds of no reply.
	 */
	public boolean isQuestioning() {
		return questioning;
	}
	
	/**
	 * @return true if 30 seconds total has occured since the last time the user said anything
	 */
	public boolean isNotAsking() {
		return stopAsking;
	}
	
	/**
	 * Should be used once the user has started talking again after a delay
	 */
	public void startAsking() {
		stopAsking = false;
	}
	
	/**
	 * Resets the timer
	 */
	public void resetTime() {
		startTime.set(System.currentTimeMillis());
	}

}

//Once x time has been reached ask a random question
//Each question has a set answer and a place to put it in the Persons profile
//repeat process
