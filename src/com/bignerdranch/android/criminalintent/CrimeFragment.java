
package com.bignerdranch.android.criminalintent;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Target;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


/****************************************************************/
/* Class: CrimeFragment.java                                    */
/*                                                              */
/* Fragment that is used to enter in the data of a new crime.   */
/****************************************************************/
public class CrimeFragment extends Fragment {

	/*******************************************************/
	/*                  Constants                          */
	/*******************************************************/
	//Key for extra to store id what List item was selected from the ListView
	public static final String EXTRA_CRIME_ID =
			"com.bignerdranch.android.criminalintent.crim_id";
	//Tag for the date piker Dialog
	private static final String DIALOG_DATE = "date";
	//Tag for the time piker Dialog
	private static final String DIALOG_TIME = "time";
	//Tag for the image Dialog
	private static final String DIALOG_IMAGE = "image";
	
	//Constant for request code to Date Picker
	private static final int REQUEST_DATE = 0;
	//Constant for request code to Time Picker
	private static final int REQUEST_TIME = 1;
	//Constant for request code to photo
	private static final int REQUEST_PHOTO = 2;
	//Constant for request code to contact
	private static final int REQUEST_CONTACT = 3;
	
	//Log tags
	private static final String TAG = "CrimeFragment";
	/*******************************************************/
	/*                   Local Data                        */
	/*******************************************************/
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private Button mTimeButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	private Button mDeletePhotoButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
	
	/*******************************************************/
	/*               New Instance of Class                 */
	/*******************************************************/
	//Creates the fragment instance and bundles up and sets its arguments
	public static CrimeFragment newInstance(UUID crimeId) {
		//Create argument bundle
		Bundle args = new Bundle();
		//Add crime ID to bundle. Can have any number of args
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		//Create Fragment. This method is used instead of constructor
		CrimeFragment fragment = new CrimeFragment();
		//Attach arguments to fragment
		fragment.setArguments(args);
		
		return fragment;
		
	}
	
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		
		//Add the App Icon button as menu
		setHasOptionsMenu(true);
		
		//Get Fragment arguments and pull out ID of crime
		UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
		
		//Fetch the Crime from the CrimeLab ArrayList
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}
	
	//Inflate the layout for Fragment's view
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		//Enable app icon to work as button and display caret
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			 //Do only if there is a parent in metadata
			if(NavUtils.getParentActivityName(getActivity()) != null) {
			   getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		//Set up Photo View
		mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Photo p = mCrime.getPhoto();
				if(p == null)
					return;
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			
				
			}
		});
		
		//Set up Photo Button
		mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Start up Camera Activity
				Intent i = new Intent(getActivity(),CrimeCameraActivity.class);
				startActivityForResult(i,REQUEST_PHOTO);
				
			}
		});
		
		//If camera is not available, disable camera functionality
		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
				pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
				(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
				Camera.getNumberOfCameras() > 0);
		
		if(!hasACamera) {
			mPhotoButton.setEnabled(false);
		}
		
		
		//Set up text field where crime is entered
		mTitleField = (EditText)v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		//Create text field listener to trigger when text is being entered
		mTitleField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				mCrime.setTitle(c.toString());
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				
			}
			
			public void afterTextChanged(Editable c) {
				
			}
		
		});
		
		//Setup Date Button and put date of crime on it
		mDateButton = (Button)v.findViewById(R.id.crime_date);
	    updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Start Date Picker Dialog on CrimeFragment after clicking on Date button
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				//Make Crime Fragment the target fragment of the DatePickerFragment instance
				dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
				
			}
		});
		
		//Setup Time Button and put the time of crime on it
		mTimeButton = (Button)v.findViewById(R.id.crime_time);
		updateTime();
		mTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Start Time Picker Dialog on CrimeFragment after clicking Time Button
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
				//Make Crime Fragment the target fragment of the TimePickerFragment instance
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
				dialog.show(fm, DIALOG_TIME);
				
			}
		});
		
		
		
		//Setup "is Crime Solved" Check Box
		mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton butonView,boolean isChecked) {
				//Set the crime's solved property
				mCrime.setSolved(isChecked);
			}
		});
		
		//Setup Submit Report Button
		mReportButton = (Button)v.findViewById(R.id.crime_reportButton);
		mReportButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Create Implicit Intent
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				//Create chooser to display the activities that respond to the intent
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
				
			}
		});
		
		//Setup Suspect Button
		mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.e(TAG,"Got suspect button click");
				Intent i = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i,REQUEST_CONTACT);
				
			}
			
		});
		
		//Setup Call Suspect Button
		mCallSuspectButton = (Button)v.findViewById(R.id.crime_callSuspectButton);
		updateCallSuspect();
		mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_DIAL);
				i.setData(Uri.parse("tel:" + mCrime.getSuspectNumber()));
                startActivity(i);
				
				
			}
		});
		
		//Show Suspects Name on Button if the Crime has one
		if(mCrime.getSuspect() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
			mCallSuspectButton.setEnabled(true);
		}
		else {
			mCallSuspectButton.setEnabled(false);
		}
		
		return v;
	}
	
	//Get results from Dialog boxes and other Activities
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.e(TAG,"In onActivityResult with requestCode " + String.valueOf(requestCode));
		if(resultCode != Activity.RESULT_OK)
			return;
		
		if(requestCode == REQUEST_DATE) {
		    //Retrieve the extra from the DatePicker
			Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			//Set the date on the Crime
			mCrime.setDate(date);
			//Refresh the text of the date button
			updateDate();
		}
		else if(requestCode == REQUEST_TIME) {
			//Retrieve the extra from the  TimePicker
			Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			//Set the date on the Crime
			mCrime.setDate(date);
			//Refresh the text of the time button
			updateTime();
		}
		else if(requestCode == REQUEST_PHOTO) {
			//Create a new Photo object and attach it to the crime
			String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			
			if(filename != null) {
				
			   //See if there is an existing photo on the device. If so, delete it
               mCrime.deletePhoto();
		         
			   //Create new photo object and set it to the current crime
			   Photo p = new Photo(filename);
			   mCrime.setPhoto(p);
			   //Make sure we see the photo when we return from CrimeCameraActivity
			   showPhoto();
				
			}
		}
		else if(requestCode == REQUEST_CONTACT) {
			Uri contactUri = data.getData();
			Log.i(TAG,"Got Request Contact");
			//Specify which fields you want your query to return values for
			//In this case just the display names
			String[] queryFields = new String[] {
					ContactsContract.Contacts.DISPLAY_NAME,
					ContactsContract.Contacts._ID,
					ContactsContract.Contacts.HAS_PHONE_NUMBER
			};
			
			//Perform your query - the contactUri is like a "where" clause here
			Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
			
			//Double-check that you actually got results
			if(c.getCount() == 0) {
				Log.i(TAG,"Did not get contact result!!");
				c.close();
				return;
			}
			
			Log.i(TAG,"Have contact count of " + String.valueOf(c.getCount()));
			
			//Since we only have one data item (display name).Move to the first item and
			//Pull out the first column of the first row of data
			//that is your suspect's name.
			c.moveToFirst();
			//String suspect = c.getString(0);
			int contactIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
			int hasPhoneNumberIndex = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
			String suspect = c.getString(contactIndex);
			String id = c.getString(idIndex);
			String hasNum = c.getString(hasPhoneNumberIndex);
			Log.i(TAG,"Got suspect " + suspect + " has number " + hasNum);
			
			c.close();
			//Set suspect in Crime
			mCrime.setSuspect(suspect);
			//Set suspect to be displayed on button
			mSuspectButton.setText(suspect);
			
			//See if the Contact has a phone number to call
			if(hasNum.equals("1")) {
				Log.i(TAG,"Have phones. Parse them");
			    //Enable Call Suspect Button
			    mCallSuspectButton.setEnabled(true);
			    
			    //Get all phone numbers
				Log.i(TAG,"Get Cursor to phones");
			    Cursor cPhones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
			    		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+ id, null, null);
			    Log.i(TAG,"Sucess getting Cursor to phones");
			    while(cPhones.moveToNext()) {
			    	Log.i(TAG,"Walk through phones");
			       int iPhoneNumberIndex = cPhones.getColumnIndex(Phone.NUMBER);
			       String number = cPhones.getString(iPhoneNumberIndex);
			       int type = cPhones.getInt(cPhones.getColumnIndex(Phone.TYPE));
			    
			       switch(type) {
			          case Phone.TYPE_HOME:
			    	     Log.e(TAG, "Got Home phone " + number);
			    	     break;
			          case Phone.TYPE_MOBILE:
			    	     Log.e(TAG,"Got mobile phone " + number);
			    	     mCrime.setSuspectNumber(number);
			    	     updateCallSuspect();
			    	     break;
			          case Phone.TYPE_WORK:
			    	     Log.e(TAG,"Got work phone" + number);
			    	     break;
			    	
			       }
			    }
			    
			    cPhones.close();
			}
		}

		
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    //App Icon home button
		    case android.R.id.home:
		    	//Check for parent activity in meta data
		    	if(NavUtils.getParentActivityName(getActivity()) != null) {
		    		//navigate up to parent activity
		    		NavUtils.navigateUpFromSameTask(getActivity());
		    	}
		    	return true;
		    case R.id.menu_item_delete_photo:
				//Remove photo from the image view
				mPhotoView.setImageDrawable(null);
				//Delete photo from the Crime
				mCrime.deletePhoto();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);		
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.fragment_crime, menu);
	}
	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//Display photo as soon as the CrimeFragments view becomes visible
		showPhoto();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	/***************************************************************/
	/*                     Private Methods                         */
	/***************************************************************/
	//Update text of Date Button
	private void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}
	
	private void updateTime() {
		
		//Create a Calendar to get the time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mCrime.getDate());
		
		int hour = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
		String time = String.valueOf(hour) + ":" + String.valueOf(min);
		mTimeButton.setText(time);
	}
	
	private void updateCallSuspect() {
		
		String number = mCrime.getSuspectNumber();
		
		if(number != null) 	
		   mCallSuspectButton.setText(getString(R.string.crime_call_suspect_text) + ": " + number);
		else
		   mCallSuspectButton.setText(getString(R.string.crime_call_suspect_text));
	}
	
	//Set a scaled image on the ImageView
	private void showPhoto() {
		//(Re)set the image button's image based on our photo
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if(p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}
	
	//Create Crime Report
	private String getCrimeReport() {
		String solvedString = null;
		
		if(mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		}
		else {
			solvedString = getString(R.string.crime_report_unsolved);
		}
		
		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
		
		String suspect = mCrime.getSuspect();
		
		if(suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		}
		else {
			suspect = getString(R.string.crime_report_suspect,suspect);
		}
		
		String report = getString(R.string.crime_report,
				mCrime.getTitle(),dateString,solvedString,suspect);
		
		return report;
	}
	
	
}
