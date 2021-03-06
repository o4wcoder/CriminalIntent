package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
	
	/*******************************************************/
	/*                   Local Data                        */
	/*******************************************************/
	protected abstract Fragment createFragment();
	
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		
		FragmentManager fm = getSupportFragmentManager();
		
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		//No Fragment yet, create it and add it to the fragment to the list
		if(fragment == null) {
			fragment = createFragment();
			//Create a new fragment transaction, include one add operation in it, 
			//and then commit it. Add fragment to fragment list
			fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
		
		
	}

}
