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

package org.protocoderrunner.api.widgets;

import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import org.protocoderrunner.api.other.PHelper;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.apprunner.StyleProperties;

import java.util.Map;

public class PImage extends ImageView implements PViewMethodsInterface {

    private static final String TAG = PImage.class.getSimpleName();
    protected final AppRunner mAppRunner;

    public StyleProperties props = new StyleProperties();
    protected Styler styler;

    public PImage(AppRunner appRunner) {
        super(appRunner.getAppContext());
        this.mAppRunner = appRunner;

        styler = new Styler(appRunner, this, props);
        styler.apply();
    }

    @ProtoMethod(description = "Sets an image", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public PImage load(String imagePath) {
        new PHelper.SetImageTask(this).execute(mAppRunner.getProject().getFullPathForFile(imagePath));
        return this;
    }

    public PImage load(Bitmap bmp) {
        super.setImageBitmap(bmp);

        return this;
    }

    public PImage mode(String mode) {
        if (mode == null) mode = (String) this.props.get("srcMode");
        else this.props.put("srcMode", this.props, mode);

        switch (mode) {
            case "tiled":
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) this.getDrawable());
                // Bitmap bmp = bitmapDrawable .getBitmap();

                // BitmapDrawable bd = new BitmapDrawable(bmp);
                Shader.TileMode tileMode = Shader.TileMode.REPEAT;
                bitmapDrawable.setTileModeXY(tileMode, tileMode);

                setBackground(bitmapDrawable);
                setImageBitmap(null);
                //setScaleX(2);
                break;

            case "fit":
                this.setScaleType(ScaleType.FIT_CENTER);
                break;

            case "crop":
                this.setScaleType(ScaleType.CENTER_CROP);
                break;

            case "resize":
                this.setScaleType(ScaleType.FIT_XY);
                break;
        }


        return this;
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public void setStyle(Map style) {
        styler.setStyle(style);
    }

    @Override
    public Map getStyle() {
        return props;
    }
}
