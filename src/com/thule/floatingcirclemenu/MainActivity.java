package com.thule.floatingcirclemenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.thule.floatingcirclemenu.service.MXService;

/**
 * @author thule
 *
 */
public class MainActivity extends Activity
{

	private Button button;
	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button);
		image = (ImageView) findViewById(R.id.image);
		
		button.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				ScaleAnimation scalseAnimation = new ScaleAnimation(0.0f, 3.0f, 0.0f, 3.0f, 50, 50);
				scalseAnimation.setDuration(1000);				
				image.startAnimation(scalseAnimation);
				
//				TranslateAnimation translateAnimation = new TranslateAnimation(0, 200, 0, 0);
//				translateAnimation.setDuration(1000);
//				image.startAnimation(translateAnimation);
			}
		});
	}

	@Override
	protected void onResume()
	{
		Intent serviceIntent = new Intent(MainActivity.this, MXService.class);
		startService(serviceIntent);
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		Intent serviceIntent = new Intent(MainActivity.this, MXService.class);
		stopService(serviceIntent);
		super.onPause();
	}
	
	
}
