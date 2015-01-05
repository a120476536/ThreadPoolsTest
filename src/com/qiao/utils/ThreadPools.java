package com.qiao.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

/**
 * 线程池管理线程
 * @author gaoming
 *
 */
public class ThreadPools {
	private static final String TAG="ThreadPools";
	private static ExecutorService cachedThreadPool;

	/**
	 * 开启线程
	 * @param callback
	 */
	public static void startThread(final ThreadPool callback) {
		cachedThreadPool = Executors.newCachedThreadPool();
		if (cachedThreadPool.isShutdown()) {
			cachedThreadPool = Executors.newCachedThreadPool();
		}
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				callback.start();
				Log.v(TAG, "==-->线程启动60s不操作 关闭线程");
			}
		});
	}
	/**
	 * 关闭线程
	 */
	public static void endThread() {
		cachedThreadPool.shutdown();
		Log.v(TAG, "==-->关闭线程");
	}

}
