package com.makewithmoto;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.makewithmoto.base.BaseActivity;

@SuppressLint("NewApi")
public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutHolder";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        //Create the action bar programmatically
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.about_activity_name);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
    }

    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case android.R.id.home:
            // Up button pressed
            Intent intentHome = new Intent(this, MainActivity.class);
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentHome);
            overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }

    }

}
