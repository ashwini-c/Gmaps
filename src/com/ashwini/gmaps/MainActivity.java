package com.ashwini.gmaps;



import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements LocationListener {
	private GoogleMap mMap;
	private static final LatLng eg = new LatLng(42.093230818037,11.7971813678741);
	private LocationManager locationManager;
	private String provider;
	Marker curr ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMap();
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabledGPS = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean enabledWiFi = service
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


		if (!enabledGPS) {
			Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);


		if (location != null) {
			Toast.makeText(this, "Selected Provider " + provider,
					Toast.LENGTH_SHORT).show();
			onLocationChanged(location);
		} 


	}
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat =  location.getLatitude();
		double lng = location.getLongitude();
		/*Toast.makeText(this, "Location " + lat+","+lng,
				Toast.LENGTH_LONG).show();*/
		LatLng coordinate = new LatLng(lat, lng);
		/*Toast.makeText(this, "Location " + coordinate.latitude+","+coordinate.longitude,
				Toast.LENGTH_LONG).show();*/

		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				new LatLng(lat,lng)).zoom(18).build();

		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		if(curr!=null)
			curr.remove();
		curr = mMap.addMarker(new MarkerOptions()
		.position(coordinate)
		.snippet("Current Location")
		.draggable(true)
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		curr.setDraggable(true);
		//mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter( location));
	}


	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}


	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();

	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpMap()
	{
		if (mMap == null)
		{
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			if (mMap != null)
			{
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				mMap.getUiSettings().setCompassEnabled(true);
				mMap.getUiSettings().setRotateGesturesEnabled(true);
				mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
				{
					@Override
					public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
					{
						marker.showInfoWindow();
						return true;
					}
				});
				
				mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
					
					@Override
					public void onMarkerDragStart(Marker marker) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onMarkerDragEnd(Marker marker) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onMarkerDrag(Marker marker) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			else
				Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
		}
	}

	public class MarkerInfoWindowAdapter implements InfoWindowAdapter {


		Location lc;
		public MarkerInfoWindowAdapter(Location location) {
			// TODO Auto-generated constructor stub
			lc = location;
		}

		@Override
		public View getInfoContents(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			View v  = getLayoutInflater().inflate(R.layout.infowin, null);


			TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

			markerLabel.setText("LAT: "+lc.getLatitude()+" LONGITUDE: "+ lc.getLongitude());
			markerLabel = (TextView)v.findViewById(R.id.timezone);
			Date d = new Date(lc.getTime());
			markerLabel.setText("TimeZone: "+ TimeZone.getDefault().getID());
			markerLabel = (TextView)v.findViewById(R.id.utc);
			DateFormat df = DateFormat.getTimeInstance();
			//df.setTimeZone(TimeZone.getTimeZone("utc"));
			markerLabel.setText("Local time: "+df.format(d));
			df.setTimeZone(TimeZone.getTimeZone("utc"));
			markerLabel = (TextView)v.findViewById(R.id.local);
			markerLabel.setText("UTC time: "+df.format(d));
			Location dest = new Location("EROAD");
			dest.setLatitude(-36.722375);
			dest.setLongitude(174.707047);
			lc.distanceTo(dest);
			markerLabel = (TextView)v.findViewById(R.id.distance);
			markerLabel.setText("Distance to EROAD: "+ lc.distanceTo(dest) / 1000 + " KM");

			return v;
		}

	}

}
