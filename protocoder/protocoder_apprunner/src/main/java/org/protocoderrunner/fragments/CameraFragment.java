/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

package org.protocoderrunner.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;

import org.protocoderrunner.R;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.TimeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CameraFragment extends Fragment {

	public static final int MODE_COLOR_BW = 0;
	public static final int MODE_COLOR_COLOR = 1;
	public static final int MODE_CAMERA_FRONT = 2;
	public static final int MODE_CAMERA_BACK = 3;
	int modeColor;
	int modeCamera;
	private int cameraId;

	protected String TAG = "Camera";

	// camera
	TextureView mTextureView;
	protected Camera mCamera;

	// saving info
	private String _rootPath;
	private String _fileName;
	private String _path;
	private View v;

	private Vector<CameraListener> listeners;
	private boolean flashAvailable;

	public interface CameraListener {

		public void onPicTaken();

		public void onVideoRecorded();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		listeners = new Vector<CameraListener>();

		v = inflater.inflate(R.layout.fragment_camera, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();

		this.modeColor = bundle.getInt("color");
		this.modeCamera = bundle.getInt("camera");
		flashAvailable = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		/*
		 * final Window win = getWindow();
		 * win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
		 * WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		 * win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
		 * WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		 * 
		 * 
		 * setContentView(R.layout.camera);
		 */

		mTextureView = (TextureView) v.findViewById(R.id.CameraView);
		mTextureView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// takePic();
			}
		});

		mTextureView.setSurfaceTextureListener(new SurfaceTextureListener() {

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
					cameraId = getFrontCameraId();
					MLog.d(TAG, "" + cameraId);
					if (cameraId == -1) {
						MLog.d(TAG, "there is no camera");
					}
					mCamera = Camera.open(cameraId);
				} else {
					// cameraId = 0;
					mCamera = Camera.open();
				}

				try {

					Camera.Parameters parameters = mCamera.getParameters();

					if (modeColor == MODE_COLOR_BW && parameters.getSupportedColorEffects() != null) {
						// parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
					}

					if (getActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
						// parameters.set("orientation", "portrait"); // For
						// Android Version 2.2 and above
						mCamera.setDisplayOrientation(90);
						// For Android Version 2.0 and above
						parameters.setRotation(90);
					} else if (modeCamera == MODE_CAMERA_FRONT) {

					}
					mCamera.setParameters(parameters);

					mCamera.setPreviewTexture(surface);

				} catch (IOException exception) {
					mCamera.release();
				}

				mCamera.startPreview();
				mCamera.setPreviewCallback(new PreviewCallback() {

					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {
                        Camera.Parameters parameters = camera.getParameters();
                        Camera.Size size = parameters.getPreviewSize();

                        final YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
                                size.width, size.height, null);

                        Rect rect = new Rect(0, 0, size.width, size.height);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        image.compressToJpeg(rect, 50, out);

                        //PDashboard.get().sendCameraData(out);
                       // MLog.network(getActivity(), "", out);
					}
				});

				// mTextureView.animate()/*.rotation(200)*/.alpha((float)
				// 0.5).scaleX(0.2f).scaleY(0.2f).setDuration(2000);

			}
		});

		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_MOVE:
					// v.setX(event.getX());
					// v.setY(event.getY());

					// MLog.d(TAG, "" + event.getX());
					break;

				}
				return false;
			}
		});

	}

	protected void stopCamera() {

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		stopCamera();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopCamera();
	}

	File dir = null;
	File file = null;
	String fileName;

	public String takePic(final String path) {
		// final CountDownLatch latch = new CountDownLatch(1);

		AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

		SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
		// final int shutterSound = soundPool.load(this, R.raw.camera_click, 0);

		mCamera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);

				// soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);

				FileOutputStream outStream = null;
				try {

					file = new File(path);

					outStream = new FileOutputStream(file);
					outStream.write(data);
					outStream.flush();
					outStream.close();
					MLog.d(TAG, "onPictureTaken - wrote bytes: " + data.length);

					for (CameraListener l : listeners) {
						l.onPicTaken();
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}

				MLog.d(TAG, "onPictureTaken - jpeg");

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

	public void stopRecordingVideo() {
		recorder.stop();
		recorder.release();
		mCamera.lock();
		recording = false;
		MLog.d(TAG, "Recording Stopped");
	}

	public void recordVideo(String file) {

		// Camera.Parameters parameters = mCamera.getParameters();
		// MLog.d(TAG, "parameters " + parameters);
		// parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
		// mCamera.setParameters(parameters);

		recorder = new MediaRecorder();
		MLog.d(TAG, "recorder " + recorder);

		mCamera.unlock();
		recorder.setCamera(mCamera);
		// recorder.setVideoFrameRate(15);
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		// mCamera.getParameters().
		CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);
		recorder.setProfile(profile);
		MLog.d(TAG, "setting profile ");
		// CamcorderProfile cpHigh = CamcorderProfile.get(cameraId,
		// CamcorderProfile.QUALITY_HIGH);
		// MLog.d(TAG, "profile set " + cpHigh);
		// recorder.setProfile(cpHigh);
		MLog.d(TAG, "profile set 1 " + file);
		// recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		recorder.setOutputFile(file);
		MLog.d(TAG, "profile set 2");
		// recorder.setMaxDuration(5000 * 1000); // 50 seconds
		// recorder.setMaxFileSize(5000 * 1000000); // Approximately 5 megabytes

		// CamcorderProfile camcorderProfile =
		// CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		// recorder.setProfile(camcorderProfile);

		// recorder.setPreviewDisplay(mTextureView.getSurfaceTexture());
		// recorder.setPreviewDisplay(holder.getSurface());
		// recorder.setP

		try {
			MLog.d(TAG, "preparing ");
			recorder.prepare();
			MLog.d(TAG, "prepare ");

		} catch (IllegalStateException e) {
			e.printStackTrace();
			// finish();
		} catch (IOException e) {
			e.printStackTrace();
			// finish();
		}

		if (recording) {
			stopRecordingVideo();
			// Let's initRecorder so we can record again
			// prepareRecorder();
		} else {
			recording = true;
			recorder.start();
			MLog.d(TAG, "Recording Started");
		}

	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i(TAG, "photo taken");

		_fileName = TimeUtils.getCurrentTime() + ".jpg";
		_path = _rootPath + _fileName;

		new File(_rootPath).mkdirs();
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		// Uri imageFileUri = getContentResolver().insert(
		// Media.EXTERNAL_CONTENT_URI, new ContentValues());

		try {
			OutputStream imageFileOS = getActivity().getContentResolver().openOutputStream(outputFileUri);
			imageFileOS.write(data);
			imageFileOS.flush();
			imageFileOS.close();

		} catch (FileNotFoundException e) {
			Toast t = Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT);
			t.show();
		} catch (IOException e) {
			Toast t = Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT);
			t.show();
		}

		camera.startPreview();
		camera.release();

		AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);

		// WindowManager.LayoutParams params = getWindow().getAttributes();
		// params.flags |= LayoutParams.FLAG_KEEP_SCREEN_ON;
		// params.screenBrightness = 0;
		// getWindow().setAttributes(params);

		Log.i(TAG, "photo saved");

		// this.finish();

	}

	@SuppressLint("NewApi")
	private int getFrontCameraId() {
		CameraInfo ci = new CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, ci);
			if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) {
				return i;
			}
		}
		return -1; // No front-facing camera found
	}

	public void turnOnFlash(boolean b) {
		if (flashAvailable) {
			Parameters p = mCamera.getParameters();
			if (b) {
				p.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(p);
				// mCamera.startPreview();
			} else {
				p.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(p);
				// mCamera.startPreview();
			}
		}
	}

	public void addListener(CameraListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CameraListener listener) {
		listeners.remove(listener);
	}

	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & (yuv420sp[yp])) - 16;
				if (y < 0) {
					y = 0;
				}
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0) {
					r = 0;
				} else if (r > 262143) {
					r = 262143;
				}
				if (g < 0) {
					g = 0;
				} else if (g > 262143) {
					g = 262143;
				}
				if (b < 0) {
					b = 0;
				} else if (b > 262143) {
					b = 262143;
				}

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

}