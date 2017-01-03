package org.protocoderrunner.api.media;

import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.protocoderrunner.api.ProtoBase;
import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.MLog;

import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.util.UsbMidiDriver;

public class PMidi extends ProtoBase {

    private static final String TAG = PMidi.class.getSimpleName();

    private ReturnInterface mMidiEvent;
    public MidiOutputDevice mMidiOutputDevice;

    private void callback(final int cable, final int channel, final int function, final int value) {

        MLog.d(TAG, "new val + " + cable + " " + channel + " " + function + " " + value);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ReturnObject o = new ReturnObject();
                o.put("cable", cable);
                o.put("channel", channel);
                o.put("function", function);
                o.put("value", value);
                if (mMidiEvent != null) mMidiEvent.event(o);
            }
        });

    }

    private UsbMidiDriver usbMidiDriver;

    // --------- startVoiceRecognition ---------//
    public interface MidiConnectedCB {
        void event(boolean connected);
    }

    public PMidi(AppRunner appRunner) {
        super(appRunner);

        usbMidiDriver = new UsbMidiDriver(appRunner.getAppContext()) {

            @Override
            public void onMidiMiscellaneousFunctionCodes(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onMidiCableEvents(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onMidiSystemCommonMessage(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {

            }

            @Override
            public void onMidiSystemExclusive(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {

            }

            @Override
            public void onMidiNoteOff(MidiInputDevice midiInputDevice, int cable, int channel, int note, int velocity) {
                callback(cable, channel, note, velocity);
            }

            @Override
            public void onMidiNoteOn(MidiInputDevice midiInputDevice, int cable, int channel, int note, int velocity) {
                callback(cable, channel, note, velocity);
            }

            @Override
            public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int note, int pressure) {
                callback(cable, channel, note, pressure);
            }

            @Override
            public void onMidiControlChange(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int function, int value) {
                callback(cable, channel, function, value);
            }

            @Override
            public void onMidiProgramChange(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int program) {
                callback(cable, channel, channel, program);
            }

            @Override
            public void onMidiChannelAftertouch(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int pressure) {
                callback(cable, channel, channel, pressure);
            }

            @Override
            public void onMidiPitchWheel(@NonNull MidiInputDevice midiInputDevice, int cable, int channel, int amount) {
                callback(cable, channel, channel, amount);
            }

            @Override
            public void onMidiSingleByte(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiSongSelect(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiSongPositionPointer(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {

            }

            @Override
            public void onMidiTuneRequest(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiTimingClock(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiStart(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiContinue(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiStop(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiActiveSensing(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiReset(@NonNull MidiInputDevice midiInputDevice, int i) {

            }

            @Override
            public void onMidiRPNReceived(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onMidiNRPNReceived(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onDeviceDetached(@NonNull UsbDevice usbDevice) {

            }

            @Override
            public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {

            }

            @Override
            public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
                Toast.makeText(getContext(), "USB MIDI Output Device deatached" + midiOutputDevice.getUsbDevice().getDeviceName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeviceAttached(@NonNull UsbDevice usbDevice) {

            }

            @Override
            public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {

            }

            @Override
            public void onMidiOutputDeviceAttached( MidiOutputDevice midiOutputDevice) {
                Toast.makeText(getContext(), "USB MIDI Output Device " + midiOutputDevice.getUsbDevice().getDeviceName() + " has been attached.", Toast.LENGTH_LONG).show();

                mMidiOutputDevice = midiOutputDevice;

                /*
                Set<MidiOutputDevice> midiOutputDevices = usbMidiDriver.getMidiOutputDevices(midiOutputDevice.getUsbDevice());
                MLog.d(TAG, "midioutputdevices: " + midiOutputDevices.size());
                if (midiOutputDevices.size() > 0) {
                    midiOutputDevice = (MidiOutputDevice) midiOutputDevices.toArray()[0];
                    MLog.d(TAG, "midiOutputDevice " + midiOutputDevice);
                    midiOutputDevice.sendMidiNoteOn(0, 0, 50, 68);
                }
                */
            }
        };

        usbMidiDriver.open();
    }

    public PMidi onChange(final ReturnInterface callbackfn) {
        mMidiEvent = callbackfn;

        return this;
    }

    public void sendNoteOn(int cable, int channel, int note, int velocity) {
        mMidiOutputDevice.sendMidiNoteOn(cable, channel, note, velocity);
    }

    public void sendNoteOff(int cable, int channel, int note, int velocity) {
        mMidiOutputDevice.sendMidiNoteOff(cable, channel, note, velocity);
    }


    @Override
    public void __stop() {
        MLog.d(TAG, "close");
        if (mMidiOutputDevice != null) mMidiOutputDevice.suspend();
        mMidiEvent = null;
        usbMidiDriver.close();
    }


}
