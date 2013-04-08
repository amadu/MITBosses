package com.example.tabactionbar;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MapFragment extends Fragment {
	public static FragmentManager fm;
	private static Fragment currentFrag;
	

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			setupInitialLayout();
			View view = inflater.inflate(R.layout.map, container, false);
			
			return view;
		}
		private void setupInitialLayout() {
			
			currentFrag = new MapFragFake();
			fm = getActivity().getFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(R.id.mapContentFragment, currentFrag);
			transaction.commit();
			
		}
	
	
	



}

