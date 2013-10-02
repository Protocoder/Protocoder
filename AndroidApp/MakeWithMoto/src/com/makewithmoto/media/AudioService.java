package com.makewithmoto.media;


public class AudioService {

	/*
	public static PdService pdService = null;
	public static String file;

	public static final ServiceConnection pdConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder) service).getService();

			Log.d("qq", "service connected");
			
			try {
				initPd();
				//loadPatchFromResources();
				loadPatchFromDirectory(file);
			} catch (IOException e) {
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) { 
			stop();
		}
		
		public void stop() { 
			Log.d("qq", "stoping audio");
			pdService.stopAudio();		
			try {
				pdService.unbindService(pdConnection);
			} catch (IllegalArgumentException e) {
				// already unbound
				pdService = null;
			}
		}

	};

	
	private static void initPd() throws IOException {

		// configure audio glue
		int sampleRate = AudioParameters.suggestSampleRate(); 
		int micChannels = AudioParameters.suggestInputChannels();
		L.d("MIC", "mic channels" + micChannels);
		pdService.initAudio(sampleRate, micChannels, 2, 8);
		start();


	}

	public static void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(pdService, AppRunnerActivity.class);
			pdService.startAudio();
		}
	}

	protected static void sendMessage(String message, String value) {
		if (value.isEmpty()) {
			PdBase.sendBang(message);
		} else if(value.matches("[0-9]+")) {
			PdBase.sendFloat(message, Float.parseFloat(value));
		} else { 
			PdBase.sendSymbol(message, value);
		}
	}

	protected static void triggerNote(int value) {
		int m = (int) (Math.random() * 5);
		Log.d("qq", "" + m);
		PdBase.sendFloat("midinote", value); // m);
		PdBase.sendBang("trigger");
	}
	
	protected static void sendBang(int value) {
		PdBase.sendBang("button"+value);
	}

	protected static void changeSpeed(int value) {
		PdBase.sendFloat("tempo", value); // m);

	}

	protected static void finish() {
	}

	private static void loadPatchFromResources() throws IOException {

		File dir = pdService.getFilesDir();
		IoUtils.extractZipResource(pdService.getResources().openRawResource(R.raw.tuner), dir, true);
		File patchFile = new File(dir, "tuner/sampleplay.pd");
		Log.d("qq", patchFile.getAbsolutePath());
		PdBase.openPatch(patchFile.getAbsolutePath());

	}

	private static void loadPatchFromDirectory(String file2) throws IOException {
		PdBase.openPatch(file);
	}

	*/
}
