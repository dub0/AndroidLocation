package com.example.dubo.androidloaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

public class MainActivity extends Activity {
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		new Thread() {
			@Override
			public void run() {
				Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {
					double latitude = location.getLatitude(); // 经度
					double longitude = location.getLongitude(); // 纬度
					System.out.print(latitude+"============"+longitude);
				}
			}
		}.start();
	}
}