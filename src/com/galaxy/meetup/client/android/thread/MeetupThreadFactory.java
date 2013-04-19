/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author sihai
 *
 */
public class MeetupThreadFactory implements ThreadFactory {

	private final String 	prefix;							// 线程名前缀
	private ThreadGroup 	group;							// 线程组
	private boolean  		isDaemon;						// 是否设置为精灵线程
	private Integer  		priority = Thread.NORM_PRIORITY;// 
	final AtomicInteger 	tNo;							// 线程编号, 线程名的一部分
	
	public MeetupThreadFactory(String prefix) {
		this(prefix, null, false, Thread.NORM_PRIORITY);
	}
	
	public MeetupThreadFactory(String prefix, ThreadGroup group, boolean isDaemon, Integer priority) {
		tNo = new AtomicInteger(0);
		this.prefix = prefix;
		if(null != group) {
			this.group = group;
		} else {
			SecurityManager sm = System.getSecurityManager();
			group = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
		}
		this.isDaemon = isDaemon;
		this.priority = priority;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, new StringBuilder(prefix).append("-Thread-").append(tNo.getAndIncrement()).toString());
		t.setDaemon(isDaemon);
		if (null != priority) {
			t.setPriority(priority);
		}
		return t;
	}
}
