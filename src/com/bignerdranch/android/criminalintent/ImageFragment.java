package com.bignerdranch.android.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
	
	/********************************************************/
	/*                   Constants                          */
	/********************************************************/
	public static final String EXTRA_IMAGE_PATH = 
			"com.bignerdranch.android.criminalintent.image_path";
	
	/********************************************************/
	/*                    Local Data                        */
	/********************************************************/
	private ImageView mImageView;
	
	/*******************************************************/
	/*               New Instance of Class                 */
	/*******************************************************/
	public static ImageFragment newInstance(String imagePath) {
		
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
		
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		//Create minimalist Dialog
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		
		return fragment;
	}
	
	/********************************************************/
	/*                   Override Methods                   */
	/********************************************************/
	@Override
	public View onCreateView(LayoutInflater inflator, ViewGroup parent,
			Bundle savedInstanceState) {
		mImageView = new ImageView(getActivity());
		
		String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
		BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);
		
		mImageView.setImageDrawable(image);
		
		return mImageView;
	}
	
	//Free up memory when image is not longer needed
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PictureUtils.cleanImageView(mImageView);
	}

}
