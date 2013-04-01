package com.example.locationapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;

public class LocationActivity extends Activity {

	private LocationManager locationManager;
	private LocationProvider providerGPS;
	private LocationProvider providerNet;
	private LocationProvider providerPass;
	private Criteria criteria;
	private final LocationListener listener = new LocationListener() {

	    @Override
	    public void onLocationChanged(Location location) {
	        // A new location update is received.  Do something useful with it.  In this case,
	        // we're sending the update to a handler which then updates the UI with the new
	        // location.
	        Log.d("Location","Lat/Lon: "+location.getLatitude() + ", " +location.getLongitude()
	        		+" Accuracy: "+location.getAccuracy());
	        }

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// GPS provider, which will provide fine location iff the individual is stable
		// or situated for long enough
		providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER);
		
		// Network Provider
		providerNet = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
		
		// Passive Provider
		providerPass = locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);
		
		// we could possibly use a criteria instead, but I think this would defeat the
		// testing purposes
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		// ... so on and so forth

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		        10000,          // 10-second interval.
		        10,             // 10 meters.
		        listener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
	    super.onStart();

	    // This verification should be done during onStart() because the system calls
	    // this method when the user returns to the activity, which ensures the desired
	    // location provider is enabled each time the activity resumes from the stopped state.
	    LocationManager locationManager =
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    // this aspect is variable based upon our needed test
	    final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	    if (!gpsEnabled) {
	    	new AlertDialog.Builder(this)
		    .setTitle("GPS Settings")
		    .setMessage("Enable the GPS Location Services")
		    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            enableLocationSettings();
		        	dialog.dismiss();
		        }
		     })
		    .show();
	    }
	}

	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}
	
	protected void onStop() {
	    super.onStop();
	    locationManager.removeUpdates(listener);
	}

}
