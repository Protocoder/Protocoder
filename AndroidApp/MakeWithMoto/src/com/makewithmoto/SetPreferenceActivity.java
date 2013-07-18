package com.makewithmoto;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SetPreferenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
		
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
