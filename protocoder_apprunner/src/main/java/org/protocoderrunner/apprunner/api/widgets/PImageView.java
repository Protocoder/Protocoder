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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;

import java.io.File;

public class PImageView extends ImageView implements PViewInterface {

    private Context mContext;

    public PImageView(Context context) {
        super(context);
        this.mContext = context;
    }


    @ProtoMethod(description = "Sets an image", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public PImageView setImage(String imagePath) {

        if (imagePath.startsWith("http")) {
            // Add image asynchronously
            new PUIGeneric.DownloadImageTask(this, false).execute(imagePath);
        } else {

            // Add the image
            new PUIGeneric.SetImageTask(this, false).execute(
                    AppRunnerSettings.get().project.getStoragePath()
                            + File.separator
                            + imagePath);
        }

        return this;
    }

    public void setImage(Bitmap bmp) {
        this.setImageBitmap(bmp);
    }


    @ProtoMethod(description = "Sets a tiled image", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public PImageView setTiledImage(String imagePath) {

        if (imagePath.startsWith("http")) {
            // Add image asynchronously
            new PUIGeneric.DownloadImageTask(this, true).execute(imagePath);
        } else {

            // Add the image
            new PUIGeneric.SetImageTask(this, true).execute(
                    AppRunnerSettings.get().project.getStoragePath()
                            + File.separator
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
