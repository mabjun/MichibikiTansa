package com.mabware.michibikitansa;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPrefs {
	
//	private static final long START_INTERVAL_SEC_DEFAULT = 0; // デフォルトは「無し」
//	
//	public static long getStartIntervalSec(final Context context) {
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//		String sInterval = prefs.getString(context.getString(R.string.prefStartIntervalKey), Long.toString(START_INTERVAL_SEC_DEFAULT));
//		return Long.parseLong(sInterval);
//	}
//	
//	public static long getStartIntervalMillis(final Context context) {
//		return getStartIntervalSec(context) * 1000L;
//	}
//	
//	private static final long RECORDING_DURATION_SEC_DEFAULT = 10;
//	
//	public static boolean setRecordingDurationSec(final Context context, long duration) {
//		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
//		edit.putLong(context.getString(R.string.prefRecordingDurationKey), duration);
//		return edit.commit();
//	}
//	
//	public static long getRecordingDurationSec(final Context context) {
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//		long interval = prefs.getLong(context.getString(R.string.prefRecordingDurationKey), RECORDING_DURATION_SEC_DEFAULT);
//		return interval;
//	}
//	
//	public static long getRecordingDurationMillis(final Context context) {
//		return (long)getRecordingDurationSec(context) * 1000L;
//	}
//	
//	public static int getMaxRecordingDurationSec(final Context context) {
//		return 360; // 5 分 プラスα
//	}
//	
//	public static long getMaxRecordingDurationMillis(final Context context) {
//		return getMaxRecordingDurationSec(context) * 1000L;
//	}
//	
//	public static boolean isIntervalRecordingActive(final Context context) {
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//		boolean interval = prefs.getBoolean(context.getString(R.string.prefIntervalRecordingActiveKey), true);
//		return interval;		
//	}
//	
	public static boolean setLocationReceiverEnabled(final Context context, boolean yesno) {
		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean(context.getString(R.string.prefLocationReceiverEnabledKey),yesno);
		return edit.commit();		
	}
	
	public static boolean getLocationReceverEnabled(final Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean wake = prefs.getBoolean(context.getString(R.string.prefLocationReceiverEnabledKey), false);
		return wake;		
	}
//	
//	// 
//	// Preferences を経由しない
//	private static final String PREF_LOG_ID_KEY = "LogId";
//	public static int getNextLogId(final Context context) {
//		int value = getCurrentLogId(context);
//		++value; // 次の値
//		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
//		edit.putInt(PREF_LOG_ID_KEY, value); // インクリメント
//		edit.commit();
//		return value;
//	}
//	
//	public static int getCurrentLogId(final Context context) {
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//		int value = prefs.getInt(PREF_LOG_ID_KEY, 0); // 現在の値
//		return value;
//	}
//	
//	public static boolean resetLogId(final Context context) {
//		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
//		edit.putInt(PREF_LOG_ID_KEY, 0); // リセット
//		return edit.commit();		
//	}
}
