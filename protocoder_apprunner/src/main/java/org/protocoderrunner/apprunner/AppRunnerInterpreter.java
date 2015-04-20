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


import android.util.Log;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.debug.Debugger;
import org.protocoderrunner.utils.MLog;

import java.io.File;

public class AppRunnerInterpreter {

    private static final String TAG = AppRunnerInterpreter.class.getSimpleName();

    //result after line execution
    private static final int RESULT_OK = 1;
    private static final int RESULT_ERROR = 2;

    //rhino stuff
    private static ScriptContextFactory mScriptContextFactory;
    private Context rhino = null;
    private Scriptable scope;
    private InterpreterInfo mInterpreterListener;


    AppRunnerInterpreter() {
        init();
    }

    //init Rhino context
    public void init() {
        //this can be initiated only once
        if (mScriptContextFactory == null) {
            mScriptContextFactory = new ScriptContextFactory();
            ContextFactory.initGlobal(mScriptContextFactory);
        }
        mScriptContextFactory.setInterpreter(this);

        rhino = Context.enter();

        // give some android love
        rhino.setOptimizationLevel(-1);

        scope = rhino.initStandardObjects();

        //let rhino do some java <-> js transformations for us
        rhino.getWrapFactory().setJavaPrimitiveWrap(false);
    }

    //we will use this method for normal script execution
    public void eval(String jscode, String origin) {
        //null is the security domain :/

        try {
            Object result = rhino.evaluateString(scope, jscode, origin, 1, null);
            processResult(result, RESULT_OK);
        } catch (Exception e) {
            processResult(e, RESULT_ERROR);
        }
    }

    //we will use this method for live coding
    public void eval(String jscode) {
        eval(jscode, "liveCoding");
    }

    public void addJavaObjectToJs(String name, Object obj) {
        ScriptableObject.putProperty(scope, name, Context.javaToJS(obj, scope));
    }


    public Object getJsFunction(String name) {
        Object function = scope.get(name, scope);
        return function;
    }

    public void callJsFunction(String name, Object... params) {
        Object obj = getJsFunction(name);
        if (obj instanceof Function) {
            Function function = (Function) obj;
            NativeObject result = (NativeObject) function.call(rhino, scope, scope, params);
            processResult(result, RESULT_OK);
        }
    }

    public Object getObject(String name) {
        Object obj = scope.get(name, scope);
        if (obj == Scriptable.NOT_FOUND) {
            return null;
        }
        return obj;
    }

    public void processResult(Object result, int resultType) {
        switch (resultType) {
            case RESULT_OK:
                String msg = Context.toString(result);
                MLog.i(TAG, msg);
                //if (mInterpreterListener != null) mInterpreterListener.onError(msg);
                break;
            //basically we throw here the exception errors
            case RESULT_ERROR:
                String msg_e = result.toString();
                MLog.e(TAG, msg_e);
                if (mInterpreterListener != null) mInterpreterListener.onError(msg_e);
                break;
        }
    }

    /*
     * Native arrays
     */

    public Scriptable newArray() {
        return rhino.newArray(scope, 0);
    }

    public Scriptable newArray(File[] files) {
        return rhino.newArray(scope, files);
    }

    /*
    *   Errors and misc
    */
    interface InterpreterInfo {
        void onError(String message);
    }

    public void addListener(InterpreterInfo listener) {
        this.mInterpreterListener = listener;
    }

    //TODO not ready yet
    public void addDebugger(Debugger debugger) {
        rhino.setDebugger(debugger, scope);
    }

    public void stop() {
        Context.exit();
    }


    public static class ScriptContextFactory extends ContextFactory {
        private AppRunnerInterpreter mAppRunnerInterpretter;

        ScriptContextFactory() {
            super();
        }

        public void setInterpreter(AppRunnerInterpreter appRunnerInterpreter) {
            mAppRunnerInterpretter = appRunnerInterpreter;
        }

        @Override
        protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            try {
                return super.doTopCall(callable, cx, scope, thisObj, args);
            } catch (Throwable e) {
                Log.e(TAG, "ContextFactory catched error: " + e);
                mAppRunnerInterpretter.processResult(e, RESULT_ERROR);

                return e;
            }
        }
    }


}