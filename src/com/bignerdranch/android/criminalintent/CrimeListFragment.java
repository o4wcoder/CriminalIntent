package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class CrimeListFragment extends ListFragment {
	
	/***************************************************/
	/*                  Local Data                     */
	/***************************************************/
	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisible;
	private static final String TAG = "CrimeListFragment";
	private Button mAddCrimeButton;
	
	/***************************************************/
	/*                Override Methods                 */
	/***************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Tell the Fragment Manager that CrimeListFragment needs
		//to receive options menu callbacks
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.crimes_title);
		
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		
		//hook up custom adapter
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		
		setListAdapter(adapter);
		
		//retain the instance on rotation
		setRetainInstance(true);
		
		//Init subtitle boolean
		mSubtitleVisible = false;
			
	}
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
	    //View v = super.onCreateView(inflater, parent, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_empty_list, parent, false);
		//ListView listView = (ListView)v.findViewById(android.R.id.list);
		//listView.setEmptyView(v.findViewById(android.R.id.empty));
		
		//Check if subtitle should be shown
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(this.mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
			
		}
		
		//Add Crime Button is visible where there are no crimes in the list
		mAddCrimeButton = (Button)v.findViewById(R.id.emptyButtonAddCrime);
		mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Create new crime
				newCrime();
			}
			
		});
		
		//Register the Listview for using a context menu
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			//Use floating context menues for Froyo and Gingerbread
			registerForContextMenu(listView);
		}
		else {
			//Use contextual action bar on Honeycomb and higher.
			//Setting choice to MULTIPLE_MODAL allows multiselect of items
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			
			//Set listener for context menu in action mode
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode,
					MenuItem item) {
				   switch (item.getItemId()) {
				      case R.id.menu_item_delete_crime:
				    	  CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
				    	  CrimeLab crimeLab = CrimeLab.get(getActivity());
				    	  
				    	  //Delete selected crimes
				    	  for(int i = adapter.getCount() - 1; i >= 0; i--) {
				    		  if(getListView().isItemChecked(i)) {
				    			  crimeLab.deleteCrime(adapter.getItem(i));
				    		  }
				    	  }
				    	  
				    	  //Destroy Action mode context menu
				    	  mode.finish();
				    	  
				    	  adapter.notifyDataSetChanged();
				    	  return true;
				      default:
				    	  return false;
				   }
			}

			@Override
			public boolean onCreateActionMode(android.view.ActionMode mode,
					Menu menu) {
				
				   MenuInflater inflater = mode.getMenuInflater();
				   inflater.inflate(R.menu.crime_list_item_context, menu);
				   
				   return true;
			}

			@Override
			public void onDestroyActionMode(android.view.ActionMode mode) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onPrepareActionMode(android.view.ActionMode mode,
					Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(android.view.ActionMode mode,
					int position, long id, boolean checked) {
				// TODO Auto-generated method stub
				
			}

			   
			});
		}

		return v;
	}
	
	@Override
	public void onListItemClick(ListView l,View v,int position, long id) {
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
		
		//Start CrimePagerActivity with this Crime
		Intent i = new Intent(getActivity(),CrimePagerActivity.class); 
		
		//Tell Crime Fragment which Crime to display by making
		//mCrimeId and Intent extra
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID,c.getId());
		startActivity(i);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//Update the List of Crimes when you come back to the CrimeListFragment
		//After we've made changes to a Crime on another Activity
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.fragment_crime_list, menu);
		
		//Make sure correct menu item is visible on rotation
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		
		if(this.mSubtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
	}
	
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Get menu option by it's ID
		switch(item.getItemId()) {
		    //New Crime menu item
		    case R.id.menu_item_new_crime:
                newCrime();
		    	//Return true, no further processing is necessary
		    	return true;
		    case R.id.menu_item_show_subtitle:	
		    	//Toggle menu item between show and hide
		    	if(getActivity().getActionBar().getSubtitle() == null) {
		    	   getActivity().getActionBar().setSubtitle(R.string.subtitle);
		    	   
		    	   //Subtitle visible
		    	   this.mSubtitleVisible = true;
		    	   item.setTitle(R.string.hide_subtitle);
		    	}
		    	else {
		    	   getActivity().getActionBar().setSubtitle(null);
		    	   
		    	   //Subtitle not visible
		    	   mSubtitleVisible = false;
		    	   item.setTitle(R.string.show_subtitle);
		    	}
		    	return true;
		    default:
		    	return super.onOptionsItemSelected(item);
		}
	}
	
	//Context Menu
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		//Get menu item in context menu. ListView is a subclass of AdapterView
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
		
		Crime crime = adapter.getItem(position);
		
		switch (item.getItemId()) {
		   case R.id.menu_item_delete_crime:
			   CrimeLab.get(getActivity()).deleteCrime(crime);
			   adapter.notifyDataSetChanged();
			   return true;
		}
		
		return super.onContextItemSelected(item);
	}
	
	/***********************************************************/
	/*                   Private Methods                       */
	/***********************************************************/
	private void newCrime()
	{
    	//Add crime to the static List Array of Crimes
    	Crime crime = new Crime();
    	CrimeLab.get(getActivity()).addCrime(crime);
    	
    	//Create intent to start up CrimePagerActivity after selecting "New Crime" menu
    	Intent i = new Intent(getActivity(),CrimePagerActivity.class);
    	
    	//Send the crime ID in the intent to CrimePagerActivity
    	i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
    	
    	//Start CrimePagerActivity
    	startActivityForResult(i,0);
	
	}
	/***********************************************************/
	/*                     Sub Classes                         */
	/***********************************************************/

	//Adapter to show Crime specific data in the list
	private class CrimeAdapter extends ArrayAdapter<Crime> {
		
		//Constructor
		public CrimeAdapter(ArrayList<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}
		
		//Override getView to return a view inflated from the custom
		//layout and inflated with Crime Data
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//If we weren't given a view, inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime,null);
			}
			
			//Configure the view for this Crime
			Crime c = getItem(position);
			
			//Get a reference to each widget and set them with the Crime data
			
			//Set Title
			TextView titleTextView = (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
			titleTextView.setText(c.getTitle());
			//Set Date
			TextView dateTextView = (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
			dateTextView.setText(c.getDate().toString());
			//Set Solved Check Box
			CheckBox solvedCheckBox = (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.isSolved());
			
			return convertView;
			}
		
	}
	

}
