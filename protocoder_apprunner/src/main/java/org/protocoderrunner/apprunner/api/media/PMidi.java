package org.protocoderrunner.apprunner.api.media;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;

import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;

import java.util.Arrays;

import jp.kshoji.driver.midi.device.MidiInputDevice;

public class PMidi extends PInterface {

    private static final String TAG = "PMidi";

    // User interface
    final Handler midiInputEventHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }
    });

    final Handler midiOutputEventHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }
    });


    private void callback(final int cable, final int channel, final int function, final int value) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMidiEvent != null) mMidiEvent.event(cable, channel, function, value);
            }
        });

    }

    private UsbMidiDriver usbMidiDriver;

    // --------- startVoiceRecognition ---------//
    public interface MidiConnectedCB {
        void event(boolean connected);
    }


    // --------- startVoiceRecognition ---------//
    public interface MidiDeviceEventCB {
        void event(int cable, int channel, int function, int value);
    }

    MidiDeviceEventCB mMidiEvent;

    public PMidi(Context appActivity) {
        super(appActivity);

        WhatIsRunning.getInstance().add(this);


        usbMidiDriver = new UsbMidiDriver(appActivity) {
            @Override
            public void onDeviceAttached(UsbDevice usbDevice) {
                Toast.makeText(getContext(), "USB MIDI Device " + usbDevice.getDeviceName() + " has been attached.", Toast.LENGTH_LONG).show();
                //mConnectedCallback.event(true);
            }

            @Override
            public void onDeviceDetached(UsbDevice usbDevice) {
                //  Toast.makeText(UsbMidiDriverSampleActivity.this, "USB MIDI Device " + usbDevice.getDeviceName() + " has been detached.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMidiNoteOff(final MidiInputDevice sender, int cable, int channel, int note, int velocity) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NoteOff from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", note: " + note + ", velocity: " + velocity));

                callback(cable, channel, note, velocity);
            }

            @Override
            public void onMidiNoteOn(final MidiInputDevice sender, int cable, int channel, int note, int velocity) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NoteOn from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ",  channel: " + channel + ", note: " + note + ", velocity: " + velocity));

                callback(cable, channel, note, velocity);
            }

            @Override
            public void onMidiPolyphonicAftertouch(final MidiInputDevice sender, int cable, int channel, int note, int pressure) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PolyphonicAftertouch from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", note: " + note + ", pressure: " + pressure));

                callback(cable, channel, note, pressure);
            }

            @Override
            public void onMidiControlChange(final MidiInputDevice sender, final int cable, final int channel, final int function, final int value) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ControlChange from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", function: " + function + ", value: " + value));

                callback(cable, channel, function, value);
            }

            @Override
            public void onMidiProgramChange(final MidiInputDevice sender, int cable, int channel, int program) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ProgramChange from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", program: " + program));
                callback(cable, channel, channel, program);

            }

            @Override
            public void onMidiChannelAftertouch(final MidiInputDevice sender, int cable, int channel, int pressure) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ChannelAftertouch from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", pressure: " + pressure));
                callback(cable, channel, channel, pressure);
            }

            @Override
            public void onMidiPitchWheel(final MidiInputDevice sender, int cable, int channel, int amount) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PitchWheel from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", channel: " + channel + ", amount: " + amount));

                callback(cable, channel, channel, amount);
            }

            @Override
            public void onMidiSystemExclusive(final MidiInputDevice sender, int cable, final byte[] systemExclusive) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SystemExclusive from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", data:" + Arrays.toString(systemExclusive)));


            }

            @Override
            public void onMidiSystemCommonMessage(final MidiInputDevice sender, int cable, final byte[] bytes) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SystemCommonMessage from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", bytes: " + Arrays.toString(bytes)));

            }

            @Override
            public void onMidiSingleByte(final MidiInputDevice sender, int cable, int byte1) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SingleByte from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", data: " + byte1));


            }

            @Override
            public void onMidiMiscellaneousFunctionCodes(final MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "MiscellaneousFunctionCodes from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", byte1: " + byte1 + ", byte2: " + byte2 + ", byte3: " + byte3));


            }

            @Override
            public void onMidiCableEvents(final MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {
                midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "CableEvents from: " + sender.getUsbDevice().getDeviceName() + ", cable: " + cable + ", byte1: " + byte1 + ", byte2: " + byte2 + ", byte3: " + byte3));


            }
        };

        usbMidiDriver.open();

    }

    public PMidi onChange(final PMidi.MidiDeviceEventCB callbackfn) {
        mMidiEvent = callbackfn;

        return this;
    }

    public void stop() {
        usbMidiDriver.close();
    }


}
