/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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


package com.makewithmoto.apprunner.api.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.Button;

import com.makewithmoto.base.AppSettings;

public class JButton extends Button implements JViewInterface {

	private int currentColor;

	public JButton(Context context) {
		super(context);
		currentColor = Color.argb(255, 255, 255, 255);

	}
	
	@Override
	public void move(float x, float y) { 
		this.animate().x(x).setDuration(AppSettings.animSpeed);
		this.animate().y(y).setDuration(AppSettings.animSpeed);
	} 
	
	@Override
	public void rotate(float deg) { 
		this.animate().rotation(deg).setDuration(AppSettings.animSpeed);
	}

	public void changeColor(int r, int g, int b) { 
		final int c = Color.argb(255, r, g, b);
		getBackground().setColorFilter( c, PorterDuff.Mode.MULTIPLY);						
		
//		final ValueAnimator anim = ValueAnimator.ofInt(currentColor, c);
//		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//			
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				getBackground().setColorFilter((Integer) anim.getAnimatedValue(), PorterDuff.Mode.MULTIPLY);						
//			}
//		});
//		
//		anim.addListener(new AnimatorListener() {
//			
//			@Override
//			public void onAnimationStart(Animator animation) {
//				
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animator animation) {
//				
//			}
//			
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				currentColor = c;
//			}
//			
//			@Override
//			public void onAnimationCancel(Animator animation) {
//				
//			}
//		});
//
//
//		anim.setDuration(500).start();
		
		
	}

}
