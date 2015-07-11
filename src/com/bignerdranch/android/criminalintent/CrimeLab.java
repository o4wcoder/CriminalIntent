package com.bignerdranch.android.criminalintent;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

/****************************************************************/
/* Class: CrimeLab.java                                         */
/*                                                              */
/* Singleton Class to store one instance of ArrayList of Crimes */
/****************************************************************/
public class CrimeLab {
	
	/***************************************************/
	/*                  Local Data                     */
	/***************************************************/
	//Store array of crimes
	private ArrayList<Crime> mCrimes;
	//s prefix for static variable
	private static CrimeLab sCrimeLab;
	
	private CriminalIntentJSONSerializer mSerializer;
	
	//Context parameter allows singleton to start activities,
	//access project resources, find application's private storage, etc
	private Context mAppContext;
	
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	
	/***************************************************/
	/*                  Constructors                   */
	/***************************************************/
	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		
		mSerializer = new CriminalIntentJSONSerializer(mAppContext,FILENAME);
		
		//Load Crimes from JSON file
		try {
			mCrimes = this.mSerializer.loadCrimes();
		} catch (Exception e) {
			//No Crimes stored. Create empty list
			mCrimes = new ArrayList<Crime>();
			Log.e(TAG,"Error loading crimes: ", e);
		}

	}
	
	/**************************************************/
	/*                Public Methods                  */
	/**************************************************/
	//Serialize crimes and return if successful
	public boolean saveCrimes() {
		try {
			mSerializer.saveCrimes(mCrimes);
			Log.d(TAG,"crimes saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG,"Error saving crimes: ",e);
			return false;
		}
	}
	public void addCrime(Crime c) {
		mCrimes.add(c);
	}
	
	public void deleteCrime(Crime c) {
		mCrimes.remove(c);
	}
	
	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}
	
	public Crime getCrime(UUID id) {
		for (Crime c : mCrimes) {
			if(c.getId().equals(id))
				return c;
		}
		
		return null;
	}

	public static CrimeLab get(Context c) {
		if(sCrimeLab == null) {
			//To ensure that the singleton has a long-term Context to work with,
			//call getApplicationContext() and trade the passed-in Context for the
			//application context
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}

}
