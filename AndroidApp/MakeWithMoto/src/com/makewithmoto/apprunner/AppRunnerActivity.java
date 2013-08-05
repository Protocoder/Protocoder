package com.makewithmoto.apprunner;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.makewithmoto.MainActivity;
import com.makewithmoto.R;
import com.makewithmoto.apprunner.api.JAndroid;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.events.Project;
import com.makewithmoto.events.ProjectManager;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.media.Audio;
import com.makewithmoto.network.CustomWebsocketServer;

import de.greenrobot.event.EventBus;

/**
 * Original sourcecode from Droid Script :
 * https://github.com/divineprog/droidscript Copyright (c) Mikael Kindborg 2010
 * Source code license: MIT
 */
public class AppRunnerActivity extends BaseActivity {

	static ScriptContextFactory contextFactory;
	public Interpreter interpreter;

	String scriptFileName;
	private Project currentProject;
	private ActionBar actionBar;
	private CustomWebsocketServer ws;
	private static final String TAG = "AppRunner";

	static final String SCRIPT_PREFIX = "//Prepend text for all scripts \n"
			+ "var Test = Packages.com.makewithmoto.apprunner.api.Test; \n"
			+ "var test = Test(Activity);\n"
			+ "var JAndroid = Packages.com.makewithmoto.apprunner.api.JAndroid; \n"
			+ "var android = JAndroid(Activity);\n"
			+ "var JUI = Packages.com.makewithmoto.apprunner.api.JUI; \n"
			+ "var ui = JUI(Activity);\n"
			+ "var JBrowser = Packages.com.makewithmoto.apprunner.api.JBrowser; \n"
			+ "var browser = JBrowser(Activity);\n"
			+ "var JMakr = Packages.com.makewithmoto.apprunner.api.JMakr; \n"
			+ "var makr = JMakr(Activity);\n"
			+ "var JIOIO = Packages.com.makewithmoto.apprunner.api.JIOIO; \n"
			+ "var ioio = JIOIO(Activity);\n"
			+ "var JWebAppPlot = Packages.com.makewithmoto.apprunner.api.JWebAppPlot; \n"
			+ "var JWebApp = Packages.com.makewithmoto.apprunner.api.JWebApp; \n"
			+ "var webapp = JWebApp(Activity);\n"
			+ "var JMedia = Packages.com.makewithmoto.apprunner.api.JMedia; \n"
			+ "var media = JMedia(Activity);\n"
			+ "var JSensors = Packages.com.makewithmoto.apprunner.api.JSensors; \n"
			+ "var sensors = JSensors(Activity);\n"
			+ "// End of Prepend Section \n";

	static final String SCRIPT_POSTFIX = "//Appends text for all scripts \n"
			+ "//ui.postLayout(); \n"
			+ "function onSensorPause(){sensors.stopAccelerometer(); \n"
			+ "                         sensors.stopGPS();}          \n"
			+ "function onAndroidPause(){android.stopAllTimers();}  \n"
			+ "// End of Append Section" + "\n";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// websocket
		// TODO move this to onResume

		try {
			Log.d("pq", "starting websocket server");
			ws = CustomWebsocketServer.getInstance(this);
		} catch (UnknownHostException e) {
			Log.d("pq", "cannot start websocket server");
			e.printStackTrace();
		}

		// setContentView(R.layout.activity_apprunner);

		// testing camera
		/*
		 * RelativeLayout rl = (RelativeLayout)
		 * findViewById(R.id.app_runner_parent);
		 * 
		 * // Create the main layout. This is where all the items actually go
		 * FrameLayout fl = new FrameLayout(this); fl.setLayoutParams(new
		 * LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		 * fl.setId(12345); rl.addView(fl);
		 * 
		 * CameraFragment cameraFragment = new CameraFragment(); Bundle bundle =
		 * new Bundle(); bundle.putInt("color",
		 * CameraFragment.MODE_COLOR_COLOR); bundle.putInt("camera",
		 * CameraFragment.MODE_CAMERA_BACK);
		 * cameraFragment.setArguments(bundle);
		 * 
		 * FragmentTransaction ft =
		 * getSupportFragmentManager().beginTransaction(); // FIXME: Because we
		 * have no tagging system we need to use the int as a // tag, which may
		 * cause collisions ft.add(fl.getId(), cameraFragment,
		 * String.valueOf(fl.getId()));
		 * ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		 * ft.setCustomAnimations(android.R.anim.fade_in,
		 * android.R.anim.fade_out); if (true) { ft.addToBackStack(null); }
		 * ft.commit();
		 */

		String projectName = "";

		createInterpreter();
		// Read in the script given in the intent.
		Intent intent = getIntent();
		if (null != intent) {

			projectName = intent.getStringExtra("projectName");
			int projectType = intent.getIntExtra("projectType",
					ProjectManager.type);

			currentProject = ProjectManager.getInstance().get(projectName,
					projectType);

			// Set up the actionbar
			actionBar = getActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setTitle(projectName);
			}

			String script = SCRIPT_PREFIX
					+ ProjectManager.getInstance().getCode(currentProject)
					+ SCRIPT_POSTFIX;

			Log.d("AppRunnerActivity", script);
			if (null != script) {
				eval(script, projectName);
			}
		}

		// Call the onCreate JavaScript function.
		callJsFunction("onCreate", savedInstanceState);

	}

	public void onEventMainThread(ProjectEvent evt) {
		Log.d(TAG, "event -> " + evt.getAction());

		if (evt.getAction() == "run") {
			finish();
		}
	}

	public void changeTitle(String title) {
		Log.d(TAG, "change title to " + title);
		getActionBar().setTitle(title);
	}

	@Override
	public void onStart() {
		super.onStart();
		callJsFunction("onStart");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		callJsFunction("onRestart");
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);

		callJsFunction("onResume");
	}

	@Override
	public void onPause() {
		super.onPause();

		callJsFunction("onPause");
		callJsFunction("onSensorPause");
		callJsFunction("onAndroidPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		callJsFunction("onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		callJsFunction("onDestroy");
	}

	// @Override
	// public Object onRetainNonConfigurationInstance() {
	// // TODO: We will need to somehow also allow JS to save
	// data and rebuild the UI.
	// return interpreter;
	// }

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenu.ContextMenuInfo info) {
		callJsFunction("onCreateContextMenu", menu, view, info);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		callJsFunction("onContextItemSelected", item);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		callJsFunction("onCreateOptionsMenu", menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		callJsFunction("onPrepareOptionsMenu", menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		callJsFunction("onOptionsItemSelected", item);
		switch (item.getItemId()) {
		case android.R.id.home:
			// Up button pressed
			Intent intentHome = new Intent(this, MainActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);
			overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set,
					R.anim.splash_slide_out_anim_reverse_set);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public Object eval(final String code) {
		return eval(code, "");
	}

	public Object eval(final String code, final String sourceName) {
		final AtomicReference<Object> result = new AtomicReference<Object>(null);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					result.set(interpreter.eval(code, sourceName));
				} catch (Throwable e) {
					reportError(e);
					result.set(e);
				}
			}
		});
		while (null == result.get()) {
			Thread.yield();
		}

		return result.get();
	}

	/**
	 * This works because method is called from the "onXXX" methods which are
	 * called in the UI-thread. Thus, no need to use run on UI-thread. TODO:
	 * Could be a problem if someone calls it from another class, make private
	 * for now.
	 */
	private Object callJsFunction(String funName, Object... args) {
		try {
			return interpreter.callJsFunction(funName, args);
		} catch (Throwable e) {
			reportError(e);
			return false;
		}
	}

	protected void createInterpreter() {
		// Initialize global context factory with our custom factory.
		if (null == contextFactory) {
			contextFactory = new ScriptContextFactory();
			ContextFactory.initGlobal(contextFactory);
			Log.i("AppRunnerActivity", "Creating ContextFactory");
		}

		contextFactory.setActivity(this);

		if (null == interpreter) {
			// Get the interpreter, if previously created.
			Object obj = getLastNonConfigurationInstance();
			if (null == obj) {
				// Create interpreter.
				interpreter = new Interpreter();
			} else {
				// Restore interpreter state.
				interpreter = (Interpreter) obj;
			}
		}

		interpreter.setActivity(this);
	}

	public void reportError(Object e) {
		// Create error message.
		String message = "";
		if (e instanceof RhinoException) {
			RhinoException error = (RhinoException) e;
			message = error.getMessage()
					+ " "
					+ error.lineNumber()
					+ " ("
					+ error.columnNumber()
					+ "): "
					+ (error.sourceName() != null ? " " + error.sourceName()
							: "")
					+ (error.lineSource() != null ? " " + error.lineSource()
							: "") + "\n" + error.getScriptStackTrace();

			// TODO make this better!!
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			JSONObject obj = new JSONObject();
			try {
				obj.put("type", "error");
				obj.put("values", message);
				ws.send(obj);
			} catch (JSONException er1) {
				// TODO Auto-generated catch block
				er1.printStackTrace();
			}

		} else {
			message = e.toString();
		}

		// Log the error message.
		Log.i("AppRunnerActivity", "JavaScript Error: " + message);
	}

	public static String preprocess(String code) throws Exception {
		return preprocessMultiLineStrings(extractCodeFromAppRunnerTags(code));
	}

	public static String extractCodeFromAppRunnerTags(String code)
			throws Exception {
		String startDelimiter = "DROIDSCRIPT_BEGIN";
		String stopDelimiter = "DROIDSCRIPT_END";

		// Find start delimiter
		int start = code.indexOf(startDelimiter, 0);
		if (-1 == start) {
			// No delimiter found, return code untouched
			return code;
		}

		// Find stop delimiter
		int stop = code.indexOf(stopDelimiter, start);
		if (-1 == stop) {
			// No delimiter found, return code untouched
			return code;
		}

		// Extract the code between start and stop.
		String result = code.substring(start + startDelimiter.length(), stop);

		// Replace escaped characters with plain characters.
		// TODO: Add more characters here
		return result.replace("&lt;", "<").replace("&gt;", ">")
				.replace("&quot;", "\"");
	}

	public static String preprocessMultiLineStrings(String code)
			throws Exception {
		StringBuilder result = new StringBuilder(code.length() + 1000);

		String delimiter = "\"\"\"";
		int lastStop = 0;
		while (true) {
			// Find next multiline delimiter
			int start = code.indexOf(delimiter, lastStop);
			if (-1 == start) {
				// No delimiter found, append rest of the code
				// to result and break
				result.append(code.substring(lastStop, code.length()));
				break;
			}

			// Find terminating delimiter
			int stop = code.indexOf(delimiter, start + delimiter.length());
			if (-1 == stop) {
				// This is an error, throw an exception with error message
				throw new Exception("Multiline string not terminated");
			}

			// Append the code from last stop up to the start delimiter
			result.append(code.substring(lastStop, start));

			// Set new lastStop
			lastStop = stop + delimiter.length();

			// Append multiline string converted to JavaScript code
			result.append(convertMultiLineStringToJavaScript(code.substring(
					start + delimiter.length(), stop)));
		}

		return result.toString();
	}

	public static String convertMultiLineStringToJavaScript(String s) {
		StringBuilder result = new StringBuilder(s.length() + 1000);

		char quote = '\"';
		char newline = '\n';
		String backslashquote = "\\\"";
		String concat = "\\n\" + \n\"";

		result.append(quote);

		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c == quote) {
				result.append(backslashquote);
			} else if (c == newline) {
				result.append(concat);
			} else {
				result.append(c);
			}
			// Log.i("Multiline", result.toString());
		}

		result.append(quote);

		return result.toString();
	}

	public static class Interpreter {
		Context context;
		Scriptable scope;
		Require require;

		public Interpreter() {
			// Creates and enters a Context. The Context stores information
			// about the execution environment of a script.
			context = Context.enter();
			context.setOptimizationLevel(-1);

			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			scope = context.initStandardObjects();
		}

		public Interpreter setActivity(Activity activity) {
			// ScriptAssetProvider provider = new ScriptAssetProvider(activity);
			// Require require = new Require(context, scope, provider, null,
			// null, true);
			// require.install(scope);

			// Set the global JavaScript variable Activity.
			ScriptableObject.putProperty(scope, "Activity",
					Context.javaToJS(activity, scope));
			return this;
		}

		public Interpreter setErrorReporter(ErrorReporter reporter) {
			context.setErrorReporter(reporter);
			return this;
		}

		public void exit() {
			Context.exit();
		}

		public Object eval(String code, String sourceName) throws Throwable {
			String processedCode = preprocess(code);
			return context.evaluateString(scope, processedCode, sourceName, 1,
					null);
		}

		public Object callJsFunction(String funName, Object... args)
				throws Throwable {
			Object fun = scope.get(funName, scope);
			if (fun instanceof Function) {
				Log.i("AppRunnerActivity", "Calling JsFun " + funName);
				Function f = (Function) fun;
				Object result = f.call(context, scope, scope, args);
				return Context.toString(result); // Why did I use this?
			} else {
				// Log.i("AppRunnerActivity", "Could not find JsFun " +
				// funName);
				return null;
			}
		}
	}

	public static class ScriptContextFactory extends ContextFactory {
		AppRunnerActivity activity;

		public ScriptContextFactory setActivity(AppRunnerActivity activity) {
			this.activity = activity;
			return this;
		}

		@Override
		protected Object doTopCall(Callable callable, Context cx,
				Scriptable scope, Scriptable thisObj, Object[] args) {
			try {
				return super.doTopCall(callable, cx, scope, thisObj, args);
			} catch (Throwable e) {
				Log.i("AppRunnerActivity", "ContextFactory catched error: " + e);
				if (null != activity) {
					activity.reportError(e);
				}
				return e;
			}
		}
	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public String getCurrentDir() {
		return ProjectManager.getInstance().getProjectURL(currentProject);

	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.makewithmoto.base.BaseActivity#onKeyDown(int, android.view.KeyEvent)
	 * 
	 * key handling, it will pass it to the javascript interface
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		JAndroid.getInstance(this).onKeyDown(keyCode, event);
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
}
