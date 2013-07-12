package com.makewithmoto.apprunner.hardware;


public class HardwareRunnable implements Runnable {
	private volatile static boolean shouldContinue_ = false;

	@Override
	public void run() {}

	public static boolean shouldContinue() {
		return shouldContinue_;
	}

	public void setShouldContinue(boolean shouldContinue_) {
		shouldContinue_ = shouldContinue_;
	}
}