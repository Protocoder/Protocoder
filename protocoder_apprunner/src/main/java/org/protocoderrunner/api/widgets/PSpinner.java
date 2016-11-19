package org.protocoderrunner.api.widgets;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;

public class PSpinner extends Spinner {
    private String[] mData;

    public PSpinner(Context context) {
        super(context);
    }

    public PSpinner onSelected(final ReturnInterface callback) {
        this.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReturnObject r = new ReturnObject(PSpinner.this);
                r.put("selected", mData[position]);
                r.put("selectedId", position);
                callback.event(r);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return this;
    }

    public PSpinner setData(String[] data) {
        this.mData = data;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, mData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);

        return this;
    }

}
