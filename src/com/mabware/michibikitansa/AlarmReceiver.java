package com.mabware.michibikitansa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == intent) { return; }
		
		if (intent.getAction().equals(ReceiverService.ACTION_INTERVAL_UPDATE)) {
			
		}

	}

}
