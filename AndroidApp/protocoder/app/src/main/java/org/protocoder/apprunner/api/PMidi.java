package org.protocoder.apprunner.api;
//package org.protocoder.apprunner.api;
//
//import java.util.List;
//import java.util.Set;
//
//import jp.kshoji.driver.midi.device.MidiInputDevice;
//import jp.kshoji.driver.midi.device.MidiOutputDevice;
//import jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener;
//import jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener;
//import jp.kshoji.driver.midi.listener.OnMidiInputEventListener;
//import jp.kshoji.driver.midi.thread.MidiDeviceConnectionWatcher;
//import jp.kshoji.driver.midi.util.Constants;
//import jp.kshoji.driver.midi.util.UsbMidiDeviceUtils;
//import jp.kshoji.driver.usb.util.DeviceFilter;
//import android.content.Context;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbDeviceConnection;
//import android.hardware.usb.UsbManager;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.os.Message;
//import android.util.Log;
//
//public class SimpleMidi {
//
//	private final Context c;
//
//	public SimpleMidi(Context c) {
//		this.c = c;
//	}
//
//	UsbDevice device = null;
//	UsbDeviceConnection deviceConnection = null;
//	MidiInputDevice midiInputDevice = null;
//	MidiOutputDevice midiOutputDevice = null;
//	OnMidiDeviceAttachedListener deviceAttachedListener = null;
//	OnMidiDeviceDetachedListener deviceDetachedListener = null;
//	Handler deviceDetachedHandler = null;
//	private MidiDeviceConnectionWatcher deviceConnectionWatcher = null;
//
//	final class OnMidiDeviceAttachedListenerImpl implements OnMidiDeviceAttachedListener {
//		private final UsbManager usbManager;
//
//		/**
//		 * constructor
//		 * 
//		 * @param usbManager
//		 */
//		public OnMidiDeviceAttachedListenerImpl(UsbManager usbManager) {
//			this.usbManager = usbManager;
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener#
//		 * onDeviceAttached(android.hardware.usb.UsbDevice,
//		 * android.hardware.usb.UsbInterface)
//		 */
//		@Override
//		public synchronized void onDeviceAttached(final UsbDevice attachedDevice) {
//			if (device != null) {
//				// already one device has been connected
//				return;
//			}
//
//			deviceConnection = usbManager.openDevice(attachedDevice);
//			if (deviceConnection == null) {
//				return;
//			}
//
//			List<DeviceFilter> deviceFilters = DeviceFilter.getDeviceFilters(c.getApplicationContext());
//
//			Set<MidiInputDevice> foundInputDevices = UsbMidiDeviceUtils.findMidiInputDevices(attachedDevice,
//					deviceConnection, deviceFilters, new OnMidiInputEventListener() {
//
//						@Override
//						public void onMidiSystemExclusive(MidiInputDevice sender, int cable, byte[] systemExclusive) {
//						}
//
//						@Override
//						public void onMidiSystemCommonMessage(MidiInputDevice sender, int cable, byte[] bytes) {
//						}
//
//						@Override
//						public void onMidiSingleByte(MidiInputDevice sender, int cable, int byte1) {
//						}
//
//						@Override
//						public void onMidiRPNReceived(MidiInputDevice sender, int cable, int channel, int function,
//								int valueMSB, int valueLSB) {
//						}
//
//						@Override
//						public void onMidiProgramChange(MidiInputDevice sender, int cable, int channel, int program) {
//						}
//
//						@Override
//						public void onMidiPolyphonicAftertouch(MidiInputDevice sender, int cable, int channel,
//								int note, int pressure) {
//						}
//
//						@Override
//						public void onMidiPitchWheel(MidiInputDevice sender, int cable, int channel, int amount) {
//						}
//
//						@Override
//						public void onMidiNoteOn(MidiInputDevice sender, int cable, int channel, int note, int velocity) {
//						}
//
//						@Override
//						public void onMidiNoteOff(MidiInputDevice sender, int cable, int channel, int note, int velocity) {
//						}
//
//						@Override
//						public void onMidiNRPNReceived(MidiInputDevice sender, int cable, int channel, int function,
//								int valueMSB, int valueLSB) {
//						}
//
//						@Override
//						public void onMidiMiscellaneousFunctionCodes(MidiInputDevice sender, int cable, int byte1,
//								int byte2, int byte3) {
//						}
//
//						@Override
//						public void onMidiControlChange(MidiInputDevice sender, int cable, int channel, int function,
//								int value) {
//						}
//
//						@Override
//						public void onMidiChannelAftertouch(MidiInputDevice sender, int cable, int channel, int pressure) {
//						}
//
//						@Override
//						public void onMidiCableEvents(MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {
//						}
//					});
//			if (foundInputDevices.size() > 0) {
//				midiInputDevice = (MidiInputDevice) foundInputDevices.toArray()[0];
//			}
//
//			Set<MidiOutputDevice> foundOutputDevices = UsbMidiDeviceUtils.findMidiOutputDevices(attachedDevice,
//					deviceConnection, deviceFilters);
//			if (foundOutputDevices.size() > 0) {
//				midiOutputDevice = (MidiOutputDevice) foundOutputDevices.toArray()[0];
//			}
//
//			Log.d(Constants.TAG, "Device " + attachedDevice.getDeviceName() + " has been attached.");
//
//			this.onDeviceAttached(attachedDevice);
//		}
//	}
//
//	/**
//	 * Implementation for single device connections.
//	 * 
//	 * @author K.Shoji
//	 */
//	final class OnMidiDeviceDetachedListenerImpl implements OnMidiDeviceDetachedListener {
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener#
//		 * onDeviceDetached(android.hardware.usb.UsbDevice)
//		 */
//		@Override
//		public synchronized void onDeviceDetached(final UsbDevice detachedDevice) {
//
//			AsyncTask<UsbDevice, Void, Void> task = new AsyncTask<UsbDevice, Void, Void>() {
//
//				@Override
//				protected Void doInBackground(UsbDevice... params) {
//					if (params == null || params.length < 1) {
//						return null;
//					}
//
//					UsbDevice usbDevice = params[0];
//
//					if (midiInputDevice != null) {
//						midiInputDevice.stop();
//						midiInputDevice = null;
//					}
//
//					if (midiOutputDevice != null) {
//						midiOutputDevice.stop();
//						midiOutputDevice = null;
//					}
//
//					if (deviceConnection != null) {
//						deviceConnection.close();
//						deviceConnection = null;
//					}
//					device = null;
//
//					Log.d(Constants.TAG, "Device " + usbDevice.getDeviceName() + " has been detached.");
//
//					Message message = Message.obtain(deviceDetachedHandler);
//					message.obj = usbDevice;
//					deviceDetachedHandler.sendMessage(message);
//					return null;
//				}
//			};
//			task.execute(detachedDevice);
//		}
//	}
//
//	public void start() {
//
//		UsbManager usbManager = (UsbManager) c.getApplicationContext().getSystemService(Context.USB_SERVICE);
//		deviceAttachedListener = new OnMidiDeviceAttachedListenerImpl(usbManager);
//		deviceDetachedListener = new OnMidiDeviceDetachedListenerImpl();
//
//		deviceDetachedHandler = new Handler(new Callback() {
//			/*
//			 * (non-Javadoc)
//			 * 
//			 * @see
//			 * android.os.Handler.Callback#handleMessage(android.os.Message)
//			 */
//			@Override
//			public boolean handleMessage(Message msg) {
//				UsbDevice usbDevice = (UsbDevice) msg.obj;
//				deviceDetachedListener.onDeviceDetached(usbDevice);
//				return true;
//			}
//		});
//
//		deviceConnectionWatcher = new MidiDeviceConnectionWatcher(c.getApplicationContext(), usbManager,
//				deviceAttachedListener, deviceDetachedListener);
//	}
//
// }
