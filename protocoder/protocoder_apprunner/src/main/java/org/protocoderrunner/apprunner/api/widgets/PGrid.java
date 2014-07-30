package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.mozilla.javascript.NativeArray;

/**
 * Created by victormanueldiazbarrales on 28/07/14.
 */
public class PGrid extends LinearLayout {
    private final Context context;
    private int columns = 1;

    public PGrid(Context context) {
        super(context);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);


    }

    public PGridRow addRow(int cols) {
        PGridRow ll2 = new PGridRow(context, cols);
        this.addView(ll2);

        return ll2;
    }

    public PGrid columns(int cols) {
        this.columns = cols;
        
        return this;
    }

    public PGrid inPlace(int x, int y, int w, int h) {

        return this;
    }

    public PGrid using(NativeArray array) {

        return this;
    }

    public PGrid build() {

        return this;
    }
}
