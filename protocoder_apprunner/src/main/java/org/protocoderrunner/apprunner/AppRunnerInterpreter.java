/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.apprunner;

import android.app.Activity;
import android.util.Log;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.debug.Debugger;
import org.protocoderrunner.utils.MLog;

import java.util.concurrent.atomic.AtomicReference;


public class AppRunnerInterpreter {

    private static final String TAG = "AppRunnerInterpreter";

    private ScriptContextFactory contextFactory;
    public Interpreter interpreter;
    private final android.content.Context a;
    private InterpreterInfo mInterpreterListener;

    static String SCRIPT_PREFIX = "//Prepend text for all scripts \n" + "var window = this; \n";
    static final String SCRIPT_POSTFIX = "//Appends text for all scripts \n" + "function onAndroidPause(){ }  \n"
            + "// End of Append Section" + "\n";

    public AppRunnerInterpreter(android.content.Context context) {
        this.a = context;
    }

    public Object eval(final String code) {
        return eval(code, "");
    }

    // since service doesnt use UIs we dont have to use runOnUiThread
    public Object evalFromService(final String code) {
        final AtomicReference<Object> result = new AtomicReference<Object>(null);

        try {
            result.set(
                    interpreter.eval(code, "")
            );
        } catch (Throwable e) {
            reportError(e);
            result.set(e);
        }
        return result.get();

    }

    public Object eval(final String code, final String sourceName) {
        final AtomicReference<Object> result = new AtomicReference<Object>(null);
        ((Activity) a).runOnUiThread(new Runnable() {
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
     * Could be mContext problem if someone calls it from another class, make private
     * for now.
     */
    Object callJsFunction(String funName, Object... args) {
        try {
            return interpreter.callJsFunction(funName, args);
        } catch (Throwable e) {
            reportError(e);
            return false;
        }
    }

    public void createInterpreter(boolean isActivity) {
        // Initialize global mainScriptContext factory with our custom factory.
        if (null == contextFactory) {
            contextFactory = new ScriptContextFactory(this);
//			ContextFactory.initGlobal(contextFactory);
            Log.i(TAG, "Creating ContextFactory");
        }

        contextFactory.setActivity(a);

        if (null == interpreter) {
            // Get the interpreter, if previously created in activity
            if (isActivity) {
                Object obj = ((Activity) a).getLastNonConfigurationInstance();

                if (null == obj) {
                    // Create interpreter.
                    interpreter = new Interpreter();
                } else {
                    // Restore interpreter state.
                    interpreter = (Interpreter) obj;
                }
            } else {
                interpreter = new Interpreter();

            }
        }

        interpreter.setActivity(a);
    }

    public void addDebugger(Debugger debugger) {
        interpreter.mainScriptContext.setDebugger(debugger, interpreter.mainScriptContext);
    }

    interface InterpreterInfo {
        void onError(String message);
    }

    public void addListener(InterpreterInfo listener) {
        this.mInterpreterListener = listener;
    }

    public void reportError(Object e) {
        // Create error message.
        String message = "";
        if (e instanceof RhinoException) {

            RhinoException error = (RhinoException) e;
            message = error.getMessage() + " " + error.lineNumber() + " (" + error.columnNumber() + "): "
                    + (error.sourceName() != null ? " " + error.sourceName() : "")
                    + (error.lineSource() != null ? " " + error.lineSource() : "") + "\n" + error.getScriptStackTrace();

            MLog.d(TAG, " " + message);
            if (mInterpreterListener != null) mInterpreterListener.onError(message);

        } else if (e instanceof IllegalArgumentException) {
            IllegalArgumentException err = (IllegalArgumentException) e;
            Log.i(TAG, "IllegalArgumentException " + err.getMessage());
        } else {
            message = e.toString();
        }

        // Log the error message.
        Log.i(TAG, "JavaScript Error: " + message);
    }

    public static String preprocess(String code) throws Exception {
        return preprocessMultiLineStrings(extractCodeFromAppRunnerTags(code));
    }

    public static String extractCodeFromAppRunnerTags(String code) throws Exception {
        String startDelimiter = "PROTOCODERSCRIPT_BEGIN";
        String stopDelimiter = "PROTOCODERSCRIPT_END";

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
        return result.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
    }

    public static String preprocessMultiLineStrings(String code) throws Exception {
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
            result.append(convertMultiLineStringToJavaScript(code.substring(start + delimiter.length(), stop)));
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


    public String addInterface(Class c) {
        String pkg = "Packages." + c.getName().toString();
        String clsName = c.getSimpleName();

        String c1 = "var " + clsName + " = " + pkg + "; \n";
        String c2 = "var " + clsName.substring(1).toLowerCase() + "=" + clsName + "(Activity); \n";

        String prefix = c1 + c2;

        SCRIPT_PREFIX += prefix;

        return prefix;
    }

    public class Interpreter {
        public Context mainScriptContext;
        public Scriptable scope;
        Require require;

        public Interpreter() {
            // Creates and enters mContext Context. The Context stores information
            // about the execution environment of mContext script.
            mainScriptContext = Context.enter();
            mainScriptContext.getWrapFactory().setJavaPrimitiveWrap(false);
            mainScriptContext.setOptimizationLevel(-1);

            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // mContext scope object that we use in later calls.
            scope = mainScriptContext.initStandardObjects();
        }

        public Interpreter setActivity(android.content.Context a) {
            // Set the global JavaScript variable Activity.
            ScriptableObject.putProperty(scope, "Activity", Context.javaToJS(a, scope));
            return this;
        }

        public Interpreter setErrorReporter(ErrorReporter reporter) {
            mainScriptContext.setErrorReporter(reporter);
            return this;
        }

        public void exit() {
            Context.exit();
        }

        public Object eval(String code, String sourceName) throws Throwable {
            String processedCode = preprocess(code);
            return mainScriptContext.evaluateString(scope, processedCode, sourceName, 1, null);
        }

        public Object callJsFunction(String funName, Object... args) throws Throwable {
            Object fun = scope.get(funName, scope);

            if (fun instanceof Function) {
                Log.i(TAG, "Calling JsFun " + funName);
                Function f = (Function) fun;
                Object result = f.call(mainScriptContext, scope, scope, args);
                return Context.toString(result); // Why did I use this?
            } else {

                return null;
            }
        }

        public void addObjectToInterface(String name, Object obj) {
            ScriptableObject.putProperty(scope, name, Context.javaToJS(obj, scope));
        }
    }

    public static class ScriptContextFactory extends ContextFactory {
        android.content.Context c;
        private final AppRunnerInterpreter appRunnerInterpreter;

        ScriptContextFactory(AppRunnerInterpreter appRunnerInterpreter) {
            this.appRunnerInterpreter = appRunnerInterpreter;
        }

        public ScriptContextFactory setActivity(android.content.Context c) {
            this.c = c;
            return this;
        }

        @Override
        protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            try {
                return super.doTopCall(callable, cx, scope, thisObj, args);
            } catch (Throwable e) {
                Log.i(TAG, "ContextFactory catched error: " + e);
                if (null != c) {
                    appRunnerInterpreter.reportError(e);
                }
                return e;
            }
        }
    }


}