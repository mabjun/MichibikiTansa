package com.mabware.michibikitansa;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ReceiverService extends Service implements LocationListener, Listener, NmeaListener {
	private static final String LOGTAG = "ReceiverService";
	private static final boolean DEBUG = true;
	private boolean isActive = false;
	
	public static final String ACTION_INTERVAL_UPDATE = "com.mabware.michibikitansa.ACTION_INTERVAL_UPDATE";
	private static final int NOTIFICATION_ID = 24862;
	
	@Override
	public IBinder onBind(Intent intent) {
		// Bind はしない。
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		startProc(intent, startId);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		unregisterAlarmReceiver();
		if (isActive) {
			stopLocationUpdate();
			isActive = false;
		}
		cancelNotification();
		super.onDestroy();
	}
	
	private void startProc(Intent intent, int startId) {
		registerAlarmReceiver();
		setNextAlarm(this);
		startLocationUpdate();
		isActive = true;
	}
	
	// Location 関連
	private static LocationManager locationManager = null;
//	private String currentProvider = null;
	private static int latestPrn = 0;
	private static float latestAzimuth = 0;
	private static float latestElevation = 0;
	private static int latestSatellites = 0;
	private static long latestGpsStatusChanged;
	
	private void startLocationUpdate() {
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        locationManager.addGpsStatusListener(this);
        locationManager.addNmeaListener(this);
    }
	
	private void stopLocationUpdate() {
		if (null != locationManager) {
			locationManager.removeUpdates(this);
			locationManager.removeGpsStatusListener(this);
			locationManager.removeNmeaListener(this);
		}
	}
	
	// LocationListener
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNmeaReceived(long timestamp, String nmea) {
		if (DEBUG) Log.v(LOGTAG, "onNmeaReceived:" + Long.toString(timestamp) + ":" + nmea);
		
		String [] tokens = nmea.split(",", -1);
		if (0 == tokens.length) { return; }
		
		if (tokens[0].equals("$QZGSA")) {
			Log.v(LOGTAG, "qzgsa");
		} else if (tokens[0].equals("$QZGSV")) {
			Log.v(LOGTAG, "qzgsv");	
		}
				
//		String [] lines = nmea.split("\n");
//		for (String line : lines) {
//			if (DEBUG) Log.v(LOGTAG, "line:" + line);
//		}
		
	}

	@Override
	public void onGpsStatusChanged(int event) {
		if (DEBUG) Log.v(LOGTAG, "onGpsStatusChanged");
		if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			GpsStatus gpsStatus = null;
			latestPrn = 0;
			latestAzimuth = 0;
			latestElevation = 0;
			latestSatellites = 0;
			latestGpsStatusChanged = System.currentTimeMillis();
			
			gpsStatus = locationManager.getGpsStatus(null);
			if (null == gpsStatus) {
				return;
			}
			
			Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
			for (GpsSatellite sat : satellites) {
				latestSatellites++;
				int prn = sat.getPrn();
				if (DEBUG) Log.v(LOGTAG, "prn:" + prn);
				if (1 == prn || 193 == prn) {
					latestPrn = prn;
					latestAzimuth = sat.getAzimuth();
					latestElevation = sat.getElevation();
				}
			}
		}
	}
	
	// Alarm 関連
	private void cancelNotification() {
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
	}
	
	private static void setNextAlarm(Context context) {
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent();
		intent.setAction(ACTION_INTERVAL_UPDATE);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, 0);
		long now = System.currentTimeMillis() + 1;
		am.set(AlarmManager.RTC_WAKEUP, now + 1000, operation);		
	}
	
	private void cancelAlarm() {
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent();
		intent.setAction(ACTION_INTERVAL_UPDATE);
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.cancel(operation);
	}
	
	private void registerAlarmReceiver() {
		IntentFilter filter = new IntentFilter(ACTION_INTERVAL_UPDATE);
		registerReceiver(alarmReceiver, filter);
	}
	
	private void unregisterAlarmReceiver() {
		unregisterReceiver(alarmReceiver);
	}
	private static final AlarmReceiver alarmReceiver = new AlarmReceiver();
	
	private static class AlarmReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null == intent) { return; }
			
			if (intent.getAction().equals(ReceiverService.ACTION_INTERVAL_UPDATE)) {
				if (DEBUG) Log.v(LOGTAG, "onReceive");
				
				
				if (1 == latestPrn || 193 == latestPrn) {
					notifyMichibikiFound(context);
				} else if (0 != latestSatellites) {
					notifyGpsFound(context);
				} else {
					notifyGpsNotFound(context);
				}
				
				setNextAlarm(context);
			}
		}
		
		private void notifyMichibikiFound(Context context) {
			if (DEBUG) Log.v(LOGTAG, "notifyFound");
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pending = (PendingIntent)PendingIntent.getActivity(context, 0, intent, 0);
			
			Notification.Builder builder = new Notification.Builder(context);
			builder.setSmallIcon(R.drawable.ic_stat_michibiki_found);
			builder.setContentTitle(context.getString(R.string.notifyTitleFound));
			builder.setContentText(String.format("No.%d, 位置:%f/%f", latestPrn, latestAzimuth, latestElevation));
			builder.setContentIntent(pending);
			NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify("MT", NOTIFICATION_ID, builder.build());
		}

		private void notifyGpsFound(Context context) {
			if (DEBUG) Log.v(LOGTAG, "notifyGpsFound");
			Notification.Builder builder = new Notification.Builder(context);
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pending = (PendingIntent)PendingIntent.getActivity(context, 0, intent, 0);
			
			builder.setSmallIcon(R.drawable.ic_stat_gps_found);
			builder.setContentTitle(context.getString(R.string.notifyTitleNotFound));
			builder.setContentText(String.format("GPS 衛星を %d 機発見。みちびきは見つかりません", latestSatellites));
			builder.setContentIntent(pending);
			NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify("MT", NOTIFICATION_ID, builder.build());
		}
		
		private void notifyGpsNotFound(Context context) {
			if (DEBUG) Log.v(LOGTAG, "notifyNotFound");
			Notification.Builder builder = new Notification.Builder(context);
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pending = (PendingIntent)PendingIntent.getActivity(context, 0, intent, 0);
			
			builder.setSmallIcon(R.drawable.ic_stat_gps_notfound);
			builder.setContentTitle(context.getString(R.string.notifyTitleNotFound));
			builder.setContentText(String.format("GPS 衛星を %d 機発見。みちびきは見つかりません", latestSatellites));
			builder.setContentIntent(pending);
			NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify("MT", NOTIFICATION_ID, builder.build());
		}

	}
}
