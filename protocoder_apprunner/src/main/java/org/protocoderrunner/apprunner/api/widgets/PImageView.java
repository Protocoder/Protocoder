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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.ProtocoderScript;

import java.io.File;

public class PImageView extends ImageView implements PViewInterface {

	public PImageView(Context context) {
		super(context);
	}


    @ProtocoderScript
    @APIMethod(description = "Sets an image", example = "")
    @APIParam(params = { "imageName" })
	public PImageView setImage(String imagePath) {

		if (imagePath.startsWith("http")) {
			// Add image asynchronously
			new PUIGeneric.DownloadImageTask(this, false).execute(imagePath);
		} else {
			// Add the image from file
			new PUIGeneric.SetImageTask(this, false).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator
					+ imagePath);

		}

        return this;
	}

    public void setImage(Bitmap bmp) {
        this.setImageBitmap(bmp);
    }

    @ProtocoderScript
    @APIMethod(description = "Sets a tiled image", example = "")
    @APIParam(params = { "imageName" })
	public PImageView setTiledImage(String imagePath) {

		if (imagePath.startsWith("http")) {
			// Add image asynchronously
			new PUIGeneric.DownloadImageTask(this, true).execute(imagePath);
		} else {
			// Add the image from file
			new PUIGeneric.SetImageTask(this, true).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator
					+ imagePath);

		}

        return this;
	}

    public PImageView setRepeat() {

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) this.getDrawable());
       // Bitmap bmp = bitmapDrawable .getBitmap();

       // BitmapDrawable bd = new BitmapDrawable(bmp);
        Shader.TileMode mode = Shader.TileMode.REPEAT;
        bitmapDrawable.setTileModeXY(mode, mode);

        setBackground(bitmapDrawable);
        setImageBitmap(null);
        //setScaleX(2);

        return this;
    }

}
