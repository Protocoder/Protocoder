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

package org.protocoder.utils;

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
	Log.i("memory", message
		+ "----------------------------------------------------------------------------------------");
	double nativeUsage = Debug.getNativeHeapAllocatedSize();
	Log.i("memory", "nativeUsage: " + (nativeUsage / 1048576d));
	// current heap size
	double heapSize = Runtime.getRuntime().totalMemory();
	// Log.i("memory", "heapSize: " + (heapSize / 1048576d));
	// amount available in heap
	double heapRemaining = Runtime.getRuntime().freeMemory();
	// Log.i("memory", "heapRemaining: " + (heapRemaining / 1048576d));
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

	Log.i("memory",
		"-----------------------------------------------------------------------------------------------");
    }
}
