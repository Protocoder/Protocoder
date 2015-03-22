package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.widget.LinearLayout;

import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

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


    @ProtoMethod(description = "Adds a new row with n columns", example = "")
    @ProtoMethodParam(params = {"numColumns"})
    public PGridRow addRow(int cols) {
        PGridRow ll2 = new PGridRow(context, cols);
        this.addView(ll2);

        return ll2;
    }


    @ProtoMethod(description = "Specify the number of columns", example = "")
    @ProtoMethodParam(params = {"colums"})
    public PGrid columns(int cols) {
        this.columns = cols;

        return this;
    }

    //TODO Placeholder
    //
    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PGrid inPlace(int x, int y, int w, int h) {

        return this;
    }

    //TODO Placeholder

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PGrid using(NativeArray array) {

        return this;
    }

    //TODO placeholder

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PGrid build() {

        return this;
    }
}
