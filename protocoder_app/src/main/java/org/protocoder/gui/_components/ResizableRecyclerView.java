package org.protocoder.gui._components;

import android.support.v7.widget.RecyclerView;

public class ResizableRecyclerView extends RecyclerView {

    public ResizableRecyclerView(android.content.Context context) {
        super(context);
    }

    public ResizableRecyclerView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableRecyclerView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //apparently RecyclerView throws an error here rather we override the method
    //http://stackoverflow.com/questions/28428409/java-lang-unsupportedoperationexception-recyclerview-does-not-support-scrolling
    @Override
    public void scrollTo(int x, int y) {
    }
}