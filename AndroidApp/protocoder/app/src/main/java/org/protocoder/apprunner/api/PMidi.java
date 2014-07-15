package org.protocoder.apprunner.api;


import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.PInterface;
import org.protocoder.utils.MLog;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener;
import jp.kshoji.driver.midi.listener.OnMidiInputEventListener;
import jp.kshoji.driver.midi.thread.MidiDeviceConnectionWatcher;
import jp.kshoji.driver.midi.util.Constants;
import jp.kshoji.driver.midi.util.UsbMidiDeviceUtils;
import jp.kshoji.driver.usb.util.DeviceFilter;

public class PMidi extends PInterface implements OnMidiDeviceDetachedListener, OnMidiDeviceAttachedListener, OnMidiInputEventListener {

    private static final String TAG = "PMidi";

    UsbDevice device = null;
    UsbDeviceConnection deviceConnection = null;
    MidiInputDevice midiInputDevice = null;
    MidiOutputDevice midiOutputDevice = null;
    OnMidiDeviceAttachedListener deviceAttachedListener = null;
    OnMidiDeviceDetachedListener deviceDetachedListener = null;
    Handler deviceDetachedHandler = null;
    private MidiDeviceConnectionWatcher deviceConnectionWatcher = null;

    ArrayAdapter<String> midiInputEventAdapter;
    ArrayAdapter<String> midiOutputEventAdapter;

    Handler midiInputEventHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (midiInputEventAdapter != null) {
                midiInputEventAdapter.add((String)msg.obj);
            }
            // message handled successfully
            return true;
        }
    });

    Handler midiOutputEventHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (midiOutputEventAdapter != null) {
                midiOutputEventAdapter.add((String)msg.obj);
            }
            // message handled successfully
            return true;
        }
    });


    @Override
    public void onDeviceAttached(UsbDevice usbDevice) {
        Toast.makeText(a.get(), "USB MIDI Device " + usbDevice.getDeviceName() + " has been attached.", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDeviceDetached(UsbDevice usbDevice) {
        Toast.makeText(a.get(), "USB MIDI Device " + usbDevice.getDeviceName() + " has been detached.", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMidiMiscellaneousFunctionCodes(MidiInputDevice midiInputDevice, int i, int i2, int i3, int i4) {

    }

    @Override
    public void onMidiCableEvents(MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "CableEvents cable: " + cable + ", byte1: " + byte1 + ", byte2: " + byte2 + ", byte3: " + byte3));
    }

    @Override
    public void onMidiSystemCommonMessage(MidiInputDevice sender, int cable, byte[] bytes) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SystemCommonMessage cable: " + cable + ", bytes: " + Arrays.toString(bytes)));

    }

    @Override
    public void onMidiSystemExclusive(MidiInputDevice sender, int cable, byte[] systemExclusive) {

    }

    @Override
    public void onMidiNoteOff(MidiInputDevice sender, int cable, int channel, int note, int velocity) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NoteOff cable: " + cable + ", channel: " + channel + ", note: " + note + ", velocity: " + velocity));

       // MLog.d(TAG, cable + " " + channel + " " + note + " " + velocity);
    }

    @Override
    public void onMidiNoteOn(MidiInputDevice sender, int cable, int channel, int note, int velocity) {
        midiOutputEventHandler.sendMessage(Message.obtain(midiOutputEventHandler, 0, "NoteOn cable: " + cable + ",  channel: " + channel + ", note: " + note + ", velocity: " + velocity));

       // MLog.d(TAG, cable + " " + channel + " " + note + " " + velocity);
    }

    @Override
    public void onMidiPolyphonicAftertouch(MidiInputDevice sender, int cable, int channel, int note, int pressure) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PolyphonicAftertouch cable: " + cable + ", channel: " + channel + ", note: " + note + ", pressure: " + pressure));

    }

    @Override
    public void onMidiControlChange(MidiInputDevice sender, int cable, int channel, int function, int value) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ControlChange cable: " + cable + ", channel: " + channel + ", function: " + function + ", value: " + value));

        midiEvent.event(cable, channel, function, value);

      //  MLog.d(TAG, "onMidiControlChange " + cable + " " + channel + " " + function + " " + value);

    }

    @Override
    public void onMidiProgramChange(MidiInputDevice sender, int cable, int channel, int program) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ProgramChange cable: " + cable + ", channel: " + channel + ", program: " + program));

      //  MLog.d(TAG, "onMidiProgramChange " + cable + " " + channel + " " + program);
    }

    @Override
    public void onMidiChannelAftertouch(MidiInputDevice sender, int cable, int channel, int pressure) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ChannelAftertouch cable: " + cable + ", channel: " + channel + ", pressure: " + pressure));

      //  MLog.d(TAG, "onMidiChannelAfterTouch " + cable + " " + channel + " " + pressure);

    }

    @Override
    public void onMidiPitchWheel(MidiInputDevice sender, int cable, int channel, int amount) {
        midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PitchWheel cable: " + cable + ", channel: " + channel + ", amount: " + amount));

      //  MLog.d(TAG, "onMidiPitchWheel " + cable + " " + channel + " " + amount);
    }

    @Override
    public void onMidiSingleByte(MidiInputDevice sender, int cable, int byte1) {

    }

    @Override
    public void onMidiRPNReceived(MidiInputDevice sender, int cable, int channel, int function, int valueMSB, int valueLSB) {

    }

    @Override
    public void onMidiNRPNReceived(MidiInputDevice sender, int cable, int channel, int function, int valueMSB, int valueLSB) {

    }

    /**
     * Implementation for single device connections.
     *
     * @author K.Shoji
     */
    final class OnMidiDeviceAttachedListenerImpl implements OnMidiDeviceAttachedListener {
        private final UsbManager usbManager;

        /**
         * constructor
         *
         * @param usbManager
         */
        public OnMidiDeviceAttachedListenerImpl(UsbManager usbManager) {
            this.usbManager = usbManager;
        }

        /*
         * (non-Javadoc)
         * @see jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener#onDeviceAttached(android.hardware.usb.UsbDevice, android.hardware.usb.UsbInterface)
         */
        @Override
        public synchronized void onDeviceAttached(final UsbDevice attachedDevice) {
            if (device != null) {
                // already one device has been connected
                return;
            }

            deviceConnection = usbManager.openDevice(attachedDevice);
            if (deviceConnection == null) {
                return;
            }

            List<DeviceFilter> deviceFilters = DeviceFilter.getDeviceFilters(a.get().getApplicationContext());

            Set<MidiInputDevice> foundInputDevices = UsbMidiDeviceUtils.findMidiInputDevices(attachedDevice, deviceConnection, deviceFilters, PMidi.this);
            if (foundInputDevices.size() > 0) {
                midiInputDevice = (MidiInputDevice) foundInputDevices.toArray()[0];
            }

            Set<MidiOutputDevice> foundOutputDevices = UsbMidiDeviceUtils.findMidiOutputDevices(attachedDevice, deviceConnection, deviceFilters);
            if (foundOutputDevices.size() > 0) {
                midiOutputDevice = (MidiOutputDevice) foundOutputDevices.toArray()[0];
            }

            MLog.d(Constants.TAG, "Device " + attachedDevice.getDeviceName() + " has been attached.");

            PMidi.this.onDeviceAttached(attachedDevice);
        }
    }

    /**
     * Implementation for single device connections.
     *
     * @author K.Shoji
     */
    final class OnMidiDeviceDetachedListenerImpl implements OnMidiDeviceDetachedListener {
        /*
         * (non-Javadoc)
         * @see jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener#onDeviceDetached(android.hardware.usb.UsbDevice)
         */
        @Override
        public synchronized void onDeviceDetached(final UsbDevice detachedDevice) {

            AsyncTask<UsbDevice, Void, Void> task = new AsyncTask<UsbDevice, Void, Void>() {

                @Override
                protected Void doInBackground(UsbDevice... params) {
                    if (params == null || params.length < 1) {
                        return null;
                    }

                    UsbDevice usbDevice = params[0];

                    if (midiInputDevice != null) {
                        midiInputDevice.stop();
                        midiInputDevice = null;
                    }

                    if (midiOutputDevice != null) {
                        midiOutputDevice.stop();
                        midiOutputDevice = null;
                    }

                    if (deviceConnection != null) {
                        deviceConnection.close();
                        deviceConnection = null;
                    }
                    device = null;

                    MLog.network(a.get(), Constants.TAG, "Device " + usbDevice.getDeviceName() + " has been detached.");

                    Message message = Message.obtain(deviceDetachedHandler);
                    message.obj = usbDevice;
                    deviceDetachedHandler.sendMessage(message);
                    return null;
                }
            };
            task.execute(detachedDevice);
        }
    }


    // --------- startVoiceRecognition ---------//
    interface MidiDeviceEventCB {
        void event(int cable, int channel, int function, int value);
    }

    MidiDeviceEventCB midiEvent;

    public PMidi(AppRunnerActivity a) {
        super(a);
    }

    public PMidi(AppRunnerActivity a, MidiDeviceEventCB midiEvent) {
		super(a);
        this.midiEvent = midiEvent;

        UsbManager usbManager = (UsbManager) a.getApplicationContext().getSystemService(Context.USB_SERVICE);
        deviceAttachedListener = new OnMidiDeviceAttachedListenerImpl(usbManager);
        deviceDetachedListener = new OnMidiDeviceDetachedListenerImpl();

        deviceDetachedHandler = new Handler(new Handler.Callback() {
            /*
             * (non-Javadoc)
             * @see android.os.Handler.Callback#handleMessage(android.os.Message)
             */
            @Override
            public boolean handleMessage(Message msg) {
                UsbDevice usbDevice = (UsbDevice) msg.obj;
                PMidi.this.onDeviceDetached(usbDevice);
                return true;
            }
        });

        deviceConnectionWatcher = new MidiDeviceConnectionWatcher(a.getApplicationContext(), usbManager, deviceAttachedListener, deviceDetachedListener);
	}


    public void stop() {
        if (deviceConnectionWatcher != null) {
            deviceConnectionWatcher.stop();

            MLog.network(a.get(), TAG, "trying to stop deviceConnectionWatcher ");
        }
        deviceConnectionWatcher = null;

        if (midiInputDevice != null) {
            midiInputDevice.stop();
            midiInputDevice = null;

            MLog.network(a.get(), TAG, "trying to stop midiInputDevice ");

        }

        midiOutputDevice = null;
        deviceConnection = null;

        midiInputEventHandler = null;
        midiOutputEventHandler = null;

        MLog.network(a.get(), TAG, "trying to stop pMidi " + midiOutputDevice + " " + deviceConnection);

    }


}
