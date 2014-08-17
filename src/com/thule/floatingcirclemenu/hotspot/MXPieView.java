package com.thule.floatingcirclemenu.hotspot;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.thule.floatingcirclemenu.hotspot.MXHotspot.HotspotDirection;
import com.thule.floatingcirclemenu.hotspot.MXHotspotButton.ButtonState;
import com.thule.floatingcirclemenu.hotspot.MXHotspotView.MXHotspotViewDelegate;

/**
 * @author thule
 *
 */
public class MXPieView extends RelativeLayout
{
	private ArrayList<MXHotspotButton> buttons;
	private MXHotspotViewDelegate _delegate;
	public MXPieView(Context context)
	{
		super(context);
		buttons = new ArrayList<MXHotspotButton>();

	}
	
	public void setHotspotDelegate(MXHotspotViewDelegate delegate)
	{
		_delegate = delegate;
	}

	public void setPieButton(ArrayList<MXHotspotButton> buttons)
	{
		this.buttons = buttons;
	}

	public void renderPieButton()
	{
		int SIZE = buttons.size();
		
		if(SIZE == 0) return;

		ViewGroup.LayoutParams prams = getLayoutParams();
		Rect rect = new Rect(0, 0, prams.width, prams.height);

		int R = rect.width() / 2 - MXHotspot.BUTTON_SIZE / 2;

		int startDegree = 90;

		HotspotDirection direction = MXHotspot.direction;

		int stepDegree = 0;
		if (SIZE > 1)
		{
			stepDegree = 180 / (SIZE - 1);
		}

		for (int i = 0; i < SIZE; i++)
		{
			int x = 0;
			int y = 0;

			if (i == 0)
			{
				x = rect.width() / 2;
				y = rect.height() / 2 - R;
			}
			else
			{
				int degree = 0;
				if (direction == HotspotDirection.Left)
				{
					degree = startDegree - i * stepDegree;
				}
				else
				{
					degree = startDegree + i * stepDegree;
				}

				double sin = Math.sin(Math.toRadians(degree));
				double cos = Math.cos(Math.toRadians(degree));

				
				x = (int) (rect.width() / 2 + R * cos);
				y = (int) (rect.height() / 2 - R * sin);
			}

			MXHotspotButton hotspotButton = buttons.get(i);
			Button button = new Button(getContext());
			button.setBackgroundResource(hotspotButton.resourceId);
			button.setTag(hotspotButton);
			RelativeLayout.LayoutParams params = new LayoutParams(MXHotspot.BUTTON_SIZE, MXHotspot.BUTTON_SIZE);
			params.setMargins(x - MXHotspot.BUTTON_SIZE / 2, y - MXHotspot.BUTTON_SIZE / 2, 0, 0);
			
			
			if(hotspotButton.state ==ButtonState.Disable)
			{
				button.setEnabled(false);
			} 
			else if(hotspotButton.state ==ButtonState.Normal)
			{
				button.setEnabled(true);
				button.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						MXHotspotButton buttonTag = (MXHotspotButton) v.getTag();
						if(_delegate != null)
						{
							_delegate.onPieViewButtonClick(buttonTag);
						}
					}
				});
			}
			
			this.addView(button, params);
		}
	}
}
