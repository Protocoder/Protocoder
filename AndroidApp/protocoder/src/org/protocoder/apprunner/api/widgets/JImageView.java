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

package org.protocoder.apprunner.api.widgets;

import java.io.File;

import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.apprunner.api.other.JUIGeneric.DownloadImageTask;
import org.protocoder.apprunner.api.other.JUIGeneric.SetImageTask;

import android.content.Context;
import android.widget.ImageView;

public class JImageView extends ImageView implements JViewInterface {

	public JImageView(Context context) {
		super(context);
	}

	public void setImage(String imagePath) {

		if (imagePath.startsWith("http")) {
			// Add image asynchronously
			new DownloadImageTask(this).execute(imagePath);
		} else {
			// Add the image from file
			new SetImageTask(this).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator
					+ imagePath);

		}

	}

}
