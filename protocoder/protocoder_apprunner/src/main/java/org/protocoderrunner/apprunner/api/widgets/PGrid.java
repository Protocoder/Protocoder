package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

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

    @ProtocoderScript
    @APIMethod(description = "Adds a new row with n columns", example = "")
    @APIParam(params = { "numColumns" })
    public PGridRow addRow(int cols) {
        PGridRow ll2 = new PGridRow(context, cols);
        this.addView(ll2);

        return ll2;
    }

    @ProtocoderScript
    @APIMethod(description = "Specify the number of columns", example = "")
    @APIParam(params = { "colums" })
    public PGrid columns(int cols) {
        this.columns = cols;
        
        return this;
    }

    //TODO Placeholder
    //@ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "" })
    public PGrid inPlace(int x, int y, int w, int h) {

        return this;
    }

    //TODO Placeholder
    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "" })
    public PGrid using(NativeArray array) {

        return this;
    }

    //TODO placeholder
    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "" })
    public PGrid build() {

        return this;
    }
}
