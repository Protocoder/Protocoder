package com.makewithmoto.utils;

import android.os.Debug;
import android.util.Log;

public class MemoryLogger {
	static double lastavail;
	static double initavail;
	static boolean first = true;

	public static void showMemoryStats() {
	    showMemoryStats("");
	}

	public static void showMemoryStats(String message) {
	    Log.i("memory", message + "----------------------------------------------------------------------------------------");
	    double nativeUsage = Debug.getNativeHeapAllocatedSize(); 
	    Log.i("memory", "nativeUsage: " + (nativeUsage / 1048576d));
	    //current heap size 
	    double heapSize =  Runtime.getRuntime().totalMemory();
//	      Log.i("memory", "heapSize: " + (heapSize / 1048576d));
	    //amount available in heap 
	    double heapRemaining = Runtime.getRuntime().freeMemory();
//	      Log.i("memory", "heapRemaining: " + (heapRemaining / 1048576d)); 
	    double memoryAvailable = Runtime.getRuntime().maxMemory() - (heapSize - heapRemaining) - nativeUsage;
	    Log.i("memory", "memoryAvailable: " + (memoryAvailable / 1048576d));

	    if (first) {
	        initavail = memoryAvailable;
	        first = false;
	    }
	    if (lastavail > 0) {
	        Log.i("memory", "consumed since last: " + ((lastavail - memoryAvailable) / 1048576d));
	    }
	    Log.i("memory", "consumed total: " + ((initavail - memoryAvailable) / 1048576d));

	    lastavail = memoryAvailable;

	    Log.i("memory", "-----------------------------------------------------------------------------------------------");
	}
}
