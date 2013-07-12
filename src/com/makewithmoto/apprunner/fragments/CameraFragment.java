package com.makewithmoto.apprunner.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.makewithmoto.R;
import com.makewithmoto.base.BaseFragment;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CameraFragment extends BaseFragment {

	public static final int MODE_COLOR_BW = 0;
	public static final int MODE_COLOR_COLOR = 1;
	public static final int MODE_CAMERA_FRONT = 2;
	public static final int MODE_CAMERA_BACK = 3;
	int modeColor;
	int modeCamera;

	SurfaceView cameraView;
	SurfaceHolder surfaceHolder;
	Camera camera;

	private String _rootPath;
	private String _fileName;
	private String _path;
	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.camera, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();

		this.modeColor = bundle.getInt("color");
		this.modeCamera = bundle.getInt("camera");

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

		cameraView = (SurfaceView) v.findViewById(R.id.CameraView);
		surfaceHolder = cameraView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(new Callback() {

			private Camera c;

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				stopCamera();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {

				if (modeCamera == MODE_CAMERA_FRONT) {
					int index = getFrontCameraId();
					if (index == -1)
						Log.d("qq", "there is no camera");
					c = Camera.open(index);
				} else {
					camera = Camera.open();
				}

				// camera = Camera.open(1);

				try {
					camera.setPreviewDisplay(holder);
					Camera.Parameters parameters = camera.getParameters();

					if (modeColor == MODE_COLOR_BW) {
						parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
					}

					if (getActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
						parameters.set("orientation", "portrait");
						// For Android Version 2.2 and above
						camera.setDisplayOrientation(90);

						// For Android Version 2.0 and above
						// parameters.setRotation(90);
					}

					/*
					 * // Effects are for Android Version 2.0 and higher
					 * List<String> colorEffects =
					 * parameters.getSupportedColorEffects(); Iterator<String>
					 * cei = colorEffects.iterator();
					 * 
					 * while (cei.hasNext()) { String currentEffect =
					 * cei.next(); if
					 * (currentEffect.equals(Camera.Parameters.EFFECT_SOLARIZE))
					 * { parameters
					 * .setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
					 * break; } } // End Effects for Android Version 2.0 and
					 * higher
					 */

					camera.setParameters(parameters);
				} catch (IOException exception) {
					camera.release();
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				camera.startPreview();

			}
		});

		cameraView.setFocusable(true);
		cameraView.setFocusableInTouchMode(true);
		cameraView.setClickable(true);

		cameraView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				takePic();

			}
		});

		_rootPath = /* MyApp.ROOT_FOLDER */"/sdcard/" + "/camera/";

		/*
		 * EditText text = (EditText) findViewById(R.id.editText1);
		 * 
		 * text.setOnEditorActionListener(new TextView.OnEditorActionListener()
		 * {
		 * 
		 * @Override public boolean onEditorAction(TextView v, int actionId,
		 * KeyEvent event) {
		 * 
		 * if (actionId == EditorInfo.IME_ACTION_SEND) {
		 * //text.startAnimation(shake); Log.d("qq", "qq"); }
		 * 
		 * return true; }
		 * 
		 * 
		 * });
		 */

		/*
		 * Button btn = (Button) v.findViewById(R.id.button1);
		 * btn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { takePic();
		 * 
		 * } });
		 */

	}

	protected void stopCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
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

	public void takePic() {

		AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

		// TOFIX
		camera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);

				// SoundPool soundPool = new SoundPool(1,
				// AudioManager.STREAM_NOTIFICATION, 0);
				// int shutterSound = soundPool.load(this,
				// R.raw.camera_click, 0);

				// soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);

				FileOutputStream outStream = null;
				try {
					// write to local sandbox file system
					// outStream =
					// CameraDemo.this.openFileOutput(String.format("%d.jpg",
					// System.currentTimeMillis()), 0);
					// Or write to sdcard

					File q1 = new File("/sdcard/dcim/qq/");
					q1.mkdirs();
					File q2 = new File("/sdcard/dcim/qq/" + System.currentTimeMillis() + ".jpg");
					outStream = new FileOutputStream(q2);
					outStream.write(data);
					outStream.flush();
					outStream.close();
					Log.d("qq", "onPictureTaken - wrote bytes: " + data.length);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}

				Log.d("qq", "onPictureTaken - jpeg");

				camera.startPreview();
			}
		});

	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i("qq2", "photo taken");

		_fileName = Utils.getCurrentTime() + ".jpg";
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

		Log.i("qq2", "photo saved");

		// this.finish();

	}

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

}
