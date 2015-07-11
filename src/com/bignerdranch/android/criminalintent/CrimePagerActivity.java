package com.bignerdranch.android.criminalintent;
import java.util.ArrayList;
import java.util.UUID;

import com.bignerdranch.android.criminalintent.R;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;


/***************************************************************/
/* Class: CrimePagerActivity.java                              */
/*                                                             */
/* Sets up activity to be able to swipe between Crimes         */
/***************************************************************/
public class CrimePagerActivity extends FragmentActivity {
	
	/*****************************************************/
	/*                  Local Data                       */
	/*****************************************************/
	private ViewPager mViewPager;
	private ArrayList<Crime>mCrimes;
	
	/*****************************************************/
	/*                Override Methods                   */
	/*****************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Set up View Pager
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);
		
		//Get copy of List of Crimes
		mCrimes = CrimeLab.get(this).getCrimes();
		
		//Get Activities instance of the Fragment Manager
		FragmentManager fm = getSupportFragmentManager();
		
		//Create Fragment Adapter
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			//Number of items in the Array List of Crimes
			@Override
			public int getCount() {
				return mCrimes.size();
			}
			
			//Gets the Crime at the instance for the given position in the dataset
			@Override
			public Fragment getItem(int pos) {
				Crime crime = mCrimes.get(pos);
				//return new configured CrimeFragment
				return CrimeFragment.newInstance(crime.getId());
			}
			
		});
		
		//Listen for changes on the page
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				
				//Replace Activity's title on the action bar with current Crime
				Crime crime = mCrimes.get(pos);
				if(crime.getTitle() != null) {
					setTitle(crime.getTitle());
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});			

			
	
		//Code to start the list of crimes in the Pager Viewer on the one selected and not 
		//just at the first one. Gets the data from extra of calling Fragment
		UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		
		for(int i=0;i<mCrimes.size();i++) {
			if(mCrimes.get(i).getId().equals(crimeId)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}
		
	}

}
