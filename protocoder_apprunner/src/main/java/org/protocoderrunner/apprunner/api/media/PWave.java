package org.protocoderrunner.apprunner.api.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

public class PWave {

    private String TAG = PWave.class.getSimpleName();

    private Thread t;
    private int mSampleRate = 44100;
    private boolean isRunning = true;

    AudioTrack audioTrack;
    short samples[];

    private float mFreq;
    private float mPeriod;
    private float mHalfPeriod;
    int buffsize;

    int mAmp = 10000;


    public PWave() {
        WhatIsRunning.getInstance().add(this);

        // set the buffer size
        buffsize = AudioTrack.getMinBufferSize(mSampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        samples = new short[buffsize];

        // create an audiotrack object
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mSampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffsize,
                AudioTrack.MODE_STREAM);

        // start audio
        audioTrack.play();
    }


    public void qq() {
        t = new Thread() {
            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);

                double twopi = 2 * Math.PI;
                double fr = 440.f;
                double ph = 0.0;

                int count = 0;
                int sign = 1;

                // synthesis loop
                while (isRunning) {
                    mPeriod = mSampleRate / mFreq;
                    mHalfPeriod = mPeriod / 2f;

                    //sin wave
                    //for (int i = 0; i < buffsize; i++) {
                    //    samples[i] = (short) (amp * Math.sin(ph));
                    //    ph += twopi * mFreq / mSampleRate;
                    //}

                    //sawtooth wave
                    //http://michaelkrzyzaniak.com/AudioSynthesis/2_Audio_Synthesis/1_Basic_Waveforms/5_Sawtooth_Wave/
//                    for (int i = 0; i < buffsize; i++) {
//                        samples[i] = (short) (mAmp * (i % mPeriod) / mPeriod - mAmp / 2);
//                    }

                    //squarewave
                    for (int i = 0; i < buffsize; i++) {
                        //every half a period we change the sign
                        if (count++ % mHalfPeriod == 0) {
                           // sign *= -1;
                           // MLog.d(TAG, count + " " + mHalfPeriod);
                        }
                        //samples[i] = (short) ( (mFreq * 2 * i / mSampleRate) % 2 < 0 ? -1 : 1 );
                        //samples[i] *= mAmp;
                        samples[i] = (short) (mAmp * sign);
                    }

                        // if((i/NUM_CHANNELS % wavelength) < (wavelength*pulseWidth))
                   //     audioBuffer[i] = 1;
                   // else
                   //     audioBuffer[i] = 0;

                    //white noise
//                    for (int i = 0; i < buffsize; i++) {
//                        samples[i] = (short) (amp * Math.random() - amp / 2);
//                    }

                    //pulse


                    audioTrack.write(samples, 0, buffsize);
                }

                audioTrack.stop();
                audioTrack.release();
            }

        };
        t.start();
    }

    public void frequency(float val) {
        mFreq = val;
    }

    public void amplitude(int val) {
        mAmp = val;
    }

    public float[] getArray() {
        float[] arr = new float[buffsize];
        for (int i = 0; i < buffsize; i++) {
            arr[i] = samples[i];
        }
        return arr;
    }

//    public void writeSamples(float[] samples) {
//        fillBuffer( samples );
//        audioTrack.write( buffer, 0, samples.length );
//    }
//
//    private void fillBuffer( float[] samples ) {
//        if( buffer.length < samples.length )
//            buffer = new short[samples.length];
//
//        for( int i = 0; i < samples.length; i++ )
//            buffer[i] = (short)(samples[i] * Short.MAX_VALUE);;
//    }

    public void stop() {
        try {
            isRunning = false;
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t = null;
    }
}
