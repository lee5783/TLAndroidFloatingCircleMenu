package com.thule.floatingcirclemenu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

import com.thule.floatingcirclemenu.hotspot.MXHotspot;

/**
 * @author thule
 * 
 */
public class MXService extends Service
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		MXHotspot.initHotspot(wm, getApplicationContext());
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
}