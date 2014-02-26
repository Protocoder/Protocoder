/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import org.protocoder.utils.TimeUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CustomCameraView extends TextureView {

	public static final int MODE_COLOR_BW = 0;
	public static final int MODE_COLOR_COLOR = 1;
	public static final int MODE_CAMERA_FRONT = 2;
	public static final int MODE_CAMERA_BACK = 3;
	int modeColor;
	int modeCamera;

	protected String TAG = "Camera";

	// camera
	protected Camera mCamera;

	// saving info
	private String _rootPath;
	private String _fileName;
	private String _path;

	private Vector<CameraListener> listeners;
	private Context c;

	public interface CameraListener {

		public void onPicTaken();

		public void onVideoRecorded();

	}

	public CustomCameraView(Context context, int id) {
		super(context);
		c = context;
		modeCamera = id;
		listeners = new Vector<CameraListener>();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// takePic();
			}
		});

		this.setSurfaceTextureListener(new SurfaceTextureListener() {

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {

			}

			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

			}

			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				// mCamera.stopPreview();
				// mCamera.release();
				return true;
			}

			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

				if (modeCamera == MODE_CAMERA_FRONT) {
					int cameraId = getFrontCameraId();
					Log.d(TAG, "" + cameraId);
					if (cameraId == -1)
						Log.d(TAG, "there is no camera");
					mCamera = Camera.open(cameraId);

				} else {
					mCamera = Camera.open();
				}

				Log.d("qq", "qq1 " + mCamera);
				try {

					Camera.Parameters parameters = mCamera.getParameters();
					Log.d("qq", "qq2 " + mCamera);

					if (modeColor == MODE_COLOR_BW && parameters.getSupportedColorEffects() != null) {
						// parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
					}
					Log.d("qq", "qq3 " + mCamera);

					if (c.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
						// parameters.set("orientation", "portrait"); // For
						// Android Version 2.2 and above
						Log.d("qq", "qq4 " + mCamera);
						mCamera.setDisplayOrientation(90);
						Log.d("qq", "qq5" + mCamera);
						// For Android Version 2.0 and above
						parameters.setRotation(90);
						Log.d("qq", "qq6" + mCamera);
					} else if (modeCamera == MODE_CAMERA_FRONT) {

					}

					Log.d("qq", "qq 7" + mCamera);
					List<Size> supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
					parameters.setPreviewSize(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height);

					mCamera.setParameters(parameters);
					mCamera.setPreviewTexture(surface);
					Log.d("qq", "primer mCamera " + mCamera);
				} catch (IOException exception) {
					Log.d("qq", "camara released");
					mCamera.release();
				}

				mCamera.startPreview();

			}
		});

	}

	protected void stopCamera() {
		Log.d("qq", "segunda mCamera " + mCamera);

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopCamera();
	}

	File dir = null;
	File file = null;
	String fileName;

	public String takePic(final String path) {
		// final CountDownLatch latch = new CountDownLatch(1);

		AudioManager mgr = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

		// SoundPool soundPool = new SoundPool(1,
		// AudioManager.STREAM_NOTIFICATION, 0);
		// final int shutterSound = soundPool.load(this, R.raw.camera_click, 0);

		Log.d("qq", "tercera mCamera " + mCamera);

		// System.gc();
		mCamera.setPreviewCallback(null);
		mCamera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Log.d("qq", "" + data.length);
				Log.d("qq", "" + camera);

				Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);

				// soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);

				FileOutputStream outStream = null;
				try {

					file = new File(path);

					outStream = new FileOutputStream(file);
					outStream.write(data);
					outStream.flush();
					outStream.close();
					Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);

					for (CameraListener l : listeners) {
						l.onPicTaken();
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}

				Log.d(TAG, "onPictureTaken - jpeg");

				camera.startPreview();
				// latch.countDown();

			}
		});

		/*
		 * try { latch.await(); } catch (InterruptedException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 */

		return fileName;

	}

	private MediaRecorder recorder;
	private boolean recording = false;

	public void recordVideo(String file) {

		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
		mCamera.setParameters(parameters);

		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

		CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		recorder.setProfile(cpHigh);
		recorder.setOutputFile(file);
		recorder.setMaxDuration(5000 * 1000); // 50 seconds
		recorder.setMaxFileSize(5000 * 1000000); // Approximately 5 megabytes

		// CamcorderProfile camcorderProfile =
		// CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		// recorder.setProfile(camcorderProfile);

		// recorder.setPreviewDisplay(mTextureView.getSurfaceTexture());
		// recorder.setPreviewDisplay(holder.getSurface());

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			// finish();
		} catch (IOException e) {
			e.printStackTrace();
			// finish();
		}

		if (recording) {
			recorder.stop();
			recorder.release();
			recording = false;
			Log.d(TAG, "Recording Stopped");
			// Let's initRecorder so we can record again
			// prepareRecorder();
		} else {
			recording = true;
			recorder.start();
			Log.d(TAG, "Recording Started");
		}

	}

	// @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	// public void onPictureTaken(byte[] data, Camera camera) {
	// Log.i(TAG, "photo taken");
	//
	// _fileName = TimeUtils.getCurrentTime() + ".jpg";
	// _path = _rootPath + _fileName;
	//
	// new File(_rootPath).mkdirs();
	// File file = new File(_path);
	// Uri outputFileUri = Uri.fromFile(file);
	//
	// // Uri imageFileUri = getContentResolver().insert(
	// // Media.EXTERNAL_CONTENT_URI, new ContentValues());
	//
	// try {
	// OutputStream imageFileOS =
	// c.getContentResolver().openOutputStream(outputFileUri);
	// imageFileOS.write(data);
	// imageFileOS.flush();
	// imageFileOS.close();
	//
	// } catch (FileNotFoundException e) {
	// Toast t = Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT);
	// t.show();
	// } catch (IOException e) {
	// Toast t = Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT);
	// t.show();
	// }
	//
	// camera.startPreview();
	// camera.release();
	//
	// AudioManager mgr = (AudioManager)
	// c.getSystemService(Context.AUDIO_SERVICE);
	// mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
	//
	// Log.i(TAG, "photo saved");
	// }

	@SuppressLint("NewApi")
	private int getFrontCameraId() {
		CameraInfo ci = new CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, ci);
			if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
				return i;
		}
		return -1; // No front-facing camera found
	}

	public void addListener(CameraListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CameraListener listener) {
		listeners.remove(listener);
	}

}
