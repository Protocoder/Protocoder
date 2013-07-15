package com.makewithmoto.projectlist;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makewithmoto.R;
import com.makewithmoto.app.utils.Fonts;
import com.makewithmoto.app.utils.TextUtils;

public class ProjectItem extends LinearLayout {

	private WeakReference<View> v;
	//private Context c;
	private WeakReference<Context> c;

	public ProjectItem(Context context) {
		super(context);
		this.c = new WeakReference<Context>(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.v = new WeakReference<View>(inflater.inflate(R.layout.view_project_item, this, true));
	}

	public ProjectItem(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

	}

	public void setImage(int resId) {
		ImageView imageView = (ImageView) v.get().findViewById(R.id.customViewImage);
		imageView.setImageResource(resId);
	}

	public void setText(String text) {
		TextView textView = (TextView) v.get().findViewById(R.id.customViewText);
		//TextUtils.changeFont(c.get(), textView, Fonts.MENU_TITLE);
		textView.setText(text);
	}

}
