package com.makewithmoto.hardware;


public class HardwareBase {
	protected HardwareCallback callback_;
	
	public HardwareBase(HardwareCallback callback) {
		this.callback_ = callback;
	}
	/**
	 * Power on the hardware board
	 */
	public void powerOn() {
	}
	
	/**
	 * Power off the hardware board
	 */
	public void powerOff() {
	}
	
	/**
	 * Erase internal memory of the board
	 */
	public void erase() {
	}

}
