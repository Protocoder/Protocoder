package org.protocoderrunner.apprunner;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;


// http://stackoverflow.com/questions/10246030/stopping-the-rhino-engine-in-middle-of-execution
public class ObservingDebugger implements Debugger {
    boolean isDisconnected = false;

    private DebugFrame debugFrame = null;

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public void setDisconnected(boolean isDisconnected) {
        this.isDisconnected = isDisconnected;
        if(debugFrame != null){
            ((ObservingDebugFrame)debugFrame).setDisconnected(isDisconnected);
        }
    }

    public ObservingDebugger() {

    }

    public DebugFrame getFrame(Context cx, DebuggableScript fnOrScript)
    {
        if(debugFrame == null){
            debugFrame = new ObservingDebugFrame(isDisconnected);
        }
        return debugFrame;
    }

    @Override
    public void handleCompilationDone(Context arg0, DebuggableScript arg1, String arg2) {   } }
// internal ObservingDebugFrame class
class ObservingDebugFrame implements DebugFrame
{
    boolean isDisconnected = false;

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public void setDisconnected(boolean isDisconnected) {
        this.isDisconnected = isDisconnected;
    }

    ObservingDebugFrame(boolean isDisconnected)
    {
        this.isDisconnected = isDisconnected;
    }

    public void onEnter(Context cx, Scriptable activation,
                        Scriptable thisObj, Object[] args)
    { }

    public void onLineChange(Context cx, int lineNumber)
    {
        if(isDisconnected){
            throw new RuntimeException("The project just stopped executing due to some errors :/");
        }
    }

    public void onExceptionThrown(Context cx, Throwable ex)
    { }

    public void onExit(Context cx, boolean byThrow,
                       Object resultOrException)
    { }

    @Override
    public void onDebuggerStatement(Context arg0) { } }