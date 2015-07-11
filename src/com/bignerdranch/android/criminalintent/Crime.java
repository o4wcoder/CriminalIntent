package com.bignerdranch.android.criminalintent;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

/***************************************************************/
/* Class: Crime.java                                           */
/*                                                             */
/* Class to hold the data of each crime                        */
/***************************************************************/
public class Crime {
	
	/*****************************************************/
	/*                    Constatants                    */
	/*****************************************************/
	///JSON Constants
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_SUSPECT = "suspect";
	private static final String JSON_SUSPECT_NUMBER = "suspectNumber";
			
	/*****************************************************/
	/*                  Local Data                       */
	/*****************************************************/
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	private String mSuspect;
	private String mSuspectNumber;
	
	/*****************************************************/
	/*                  Constructors                     */
	/*****************************************************/
	public Crime() {
		//Generate unique identifier
		mId = UUID.randomUUID();
		mDate = new Date();
	}
	
	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		
		if(json.has(JSON_TITLE)) {
			mTitle = json.getString(JSON_TITLE);
		}
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));
		
		if(json.has(JSON_PHOTO))
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		
		if(json.has(JSON_SUSPECT))
			mSuspect = json.getString(JSON_SUSPECT);
		
		if(json.has(JSON_SUSPECT_NUMBER))
			mSuspectNumber = json.getString(JSON_SUSPECT_NUMBER);
	}

	/*****************************************************/
	/*                 Override Methods                  */
	/*****************************************************/
	@Override
	public String toString() {
		return mTitle;
	}

	/*****************************************************/
	/*                 Public Methods                    */
	/*****************************************************/
	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		
		//check for photo
		if(mPhoto != null)
			json.put(JSON_PHOTO, mPhoto.toJSON());
		
		if(mSuspect != null)
			json.put(JSON_SUSPECT, mSuspect);
		
		if(mSuspectNumber != null)
			json.put(JSON_SUSPECT_NUMBER, mSuspectNumber);
		
		return json;
	}
	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getId() {
		return mId;
	}
	
	public String getSuspect() {
		return mSuspect;
	}

	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}
	
	public String getSuspectNumber() {
		return mSuspectNumber;
	}

	public void setSuspectNumber(String suspectNumber) {
		mSuspectNumber = suspectNumber;
	}
	
	public void deletePhoto() {
		
		if(mPhoto != null) {
			
			//See if there is an existing photo stored. If so, delete it
		    File file = new File(mPhoto.getFilename());
		       
		    if(file.exists()) {
		       file.delete();
		    }
		    
		    //Clear out photo in the Crime
		    mPhoto = null;
		}
	}
	
	

	

}
