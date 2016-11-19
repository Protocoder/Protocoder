package org.protocoderrunner.api.media;

import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.api.ProtoBase;

import java.util.Arrays;

import jp.kshoji.driver.midi.device.MidiInputDevice;

public class PMidi extends ProtoBase {

    private static final String TAG = PMidi.class.getSimpleName();

    private ReturnInterface mMidiEvent;

    private void callback(final int cable, final int channel, final int function, final int value) {

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
                callback(cable, channel, note, velocity);
            }

            @Override
            public void onMidiNoteOn(final MidiInputDevice sender, int cable, int channel, int note, int velocity) {
                callback(cable, channel, note, velocity);
            }

            @Override
            public void onMidiPolyphonicAftertouch(final MidiInputDevice sender, int cable, int channel, int note, int pressure) {
                callback(cable, channel, note, pressure);
            }

            @Override
            public void onMidiControlChange(final MidiInputDevice sender, final int cable, final int channel, final int function, final int value) {
                callback(cable, channel, function, value);
            }

            @Override
            public void onMidiProgramChange(final MidiInputDevice sender, int cable, int channel, int program) {
                callback(cable, channel, channel, program);
            }

            @Override
            public void onMidiChannelAftertouch(final MidiInputDevice sender, int cable, int channel, int pressure) {
                callback(cable, channel, channel, pressure);
            }

            @Override
            public void onMidiPitchWheel(final MidiInputDevice sender, int cable, int channel, int amount) {
                callback(cable, channel, channel, amount);
            }

            @Override
            public void onMidiSystemExclusive(final MidiInputDevice sender, int cable, final byte[] systemExclusive) {

            }

            @Override
            public void onMidiSystemCommonMessage(final MidiInputDevice sender, int cable, final byte[] bytes) {

            }

            @Override
            public void onMidiSingleByte(final MidiInputDevice sender, int cable, int byte1) {

            }

            @Override
            public void onMidiMiscellaneousFunctionCodes(final MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {

            }

            @Override
            public void onMidiCableEvents(final MidiInputDevice sender, int cable, int byte1, int byte2, int byte3) {

            }
        };

        usbMidiDriver.open();
    }

    public PMidi onChange(final ReturnInterface callbackfn) {
        mMidiEvent = callbackfn;

        return this;
    }


    @Override
    public void __stop() {
        mMidiEvent = null;
        usbMidiDriver.close();
    }


}
