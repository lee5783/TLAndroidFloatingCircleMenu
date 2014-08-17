package com.thule.floatingcirclemenu.hotspot;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.thule.floatingcirclemenu.hotspot.MXHotspotButton.ButtonType;
import com.thule.floatingcirclemenu.hotspot.MXHotspotView.MXHotspotViewDelegate;

/**
 * @author thule
 * 
 */
public class MXHotspot implements MXHotspotViewDelegate
{
	public static int SPOT_SIZE = 0;
	public static int SMALL_PIE_SIZE = 0;
	public static int LARGE_PIE_SIZE = 0;

	public static int BUTTON_SIZE = 0;

	public static HotspotPieState state;
	public static HotspotDirection direction;

	private Context _context;
	private WindowManager _windowManager;
	private MXHotspotView hotspotView;

	static MXHotspot _hotspot;

	public static MXHotspot sharedInstance()
	{
		if (_hotspot == null)
		{
			Log.e("MXHotspot", "Please create hotspot first");
		}
		return _hotspot;
	}

	MXHotspot(WindowManager windowManager, Context context)
	{
		this._windowManager = windowManager;
		this._context = context;
		state = HotspotPieState.Close;
		direction = HotspotDirection.Left;
		SPOT_SIZE = dpToPixels(50, context);
		SMALL_PIE_SIZE = dpToPixels(150, context);
		LARGE_PIE_SIZE = dpToPixels(250, context);
		BUTTON_SIZE = dpToPixels(45, context);

		// DisplayMetrics metrics = _context.getResources().getDisplayMetrics();

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.width = SPOT_SIZE;
		params.height = SPOT_SIZE;
		params.x = 0;
		params.y = 0;

		hotspotView = new MXHotspotView(_context);
		hotspotView.setHotspotViewDelegate(this);

		_windowManager.addView(hotspotView, params);

		hotspotView.hotspotAnchorPoint = new Point(SPOT_SIZE / 2, SPOT_SIZE / 2);
		// moveHotspotToFrame(new Rect(0, 200, SPOT_SIZE, SPOT_SIZE),
		// false);
	}

	public static MXHotspot initHotspot(WindowManager windowManager, Context context)
	{
		if (_hotspot == null)
		{
			_hotspot = new MXHotspot(windowManager, context);
		}
		return _hotspot;
	}

	@Override
	public void moveHotspotToFrame(final Rect toRect, boolean animation)
	{
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) hotspotView.getLayoutParams();

		final Rect fromRect = rectFromLayoutParams(params);

		params.x = toRect.left;
		params.y = toRect.top;
		params.width = toRect.width();
		params.height = toRect.height();

		if (params.x < 0)
			params.x = 0;
		if (params.y < 0)
			params.y = 0;
		if (params.x + params.width > getScreenWidth())
			params.x = getScreenWidth() - params.width;
		if (params.y + params.height > getScreenHeight())
			params.y = getScreenHeight() - params.height;

		if (animation && Math.abs(fromRect.left - toRect.left) > 0)
		{
			ValueAnimator translateAnimation = ValueAnimator.ofInt(fromRect.left, toRect.left);
			translateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
			{
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator)
				{
					int val = (Integer) valueAnimator.getAnimatedValue();
					WindowManager.LayoutParams params = (WindowManager.LayoutParams) hotspotView.getLayoutParams();
					params.x = val;

					updateHotspoViewParams(params);
					Rect hotSpotRect = rectFromLayoutParams(params);
					hotspotView.hotspotAnchorPoint.x = hotSpotRect.centerX();
					hotspotView.hotspotAnchorPoint.y = hotSpotRect.centerY();
				}
			});
			translateAnimation.addListener(new AnimatorListener()
			{

				@Override
				public void onAnimationStart(Animator animation)
				{
					state = HotspotPieState.Animating;
					direction = HotspotDirection.Invalid;
				}

				@Override
				public void onAnimationRepeat(Animator animation)
				{
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					if (toRect.left == 0)
					{
						direction = HotspotDirection.Left;
					}
					else if (toRect.right == getScreenWidth())
					{
						direction = HotspotDirection.Right;
					}
					else
					{
						Log.e("Hotspot direction", "End move hostpot : INVALID Direction");
					}

					state = HotspotPieState.Close;
				}

				@Override
				public void onAnimationCancel(Animator animation)
				{
					state = HotspotPieState.Close;
				}
			});

			translateAnimation.setDuration(500);
			translateAnimation.start();
		}
		else
		{
			updateHotspoViewParams(params);
			Rect hotSpotRect = rectFromLayoutParams(params);
			hotspotView.hotspotAnchorPoint.x = hotSpotRect.centerX();
			hotspotView.hotspotAnchorPoint.y = hotSpotRect.centerY();
		}

	}

	private void updateHotspoViewParams(WindowManager.LayoutParams params)
	{
		if (params.x == 0)
		{
			direction = HotspotDirection.Left;
		}
		else if (params.x + params.width == getScreenWidth())
		{
			direction = HotspotDirection.Right;
		}
		else
		{
			direction = HotspotDirection.Invalid;
		}

		_windowManager.updateViewLayout(hotspotView, params);
	}

	@Override
	public void adjustWindowSize(boolean expand)
	{
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) hotspotView.getLayoutParams();
		if (expand)
		{
			params.x = params.y = 0;
			params.width = getScreenWidth() + SMALL_PIE_SIZE;
			params.height = getScreenHeight();
			_windowManager.removeView(hotspotView);
			_windowManager.addView(hotspotView, params);
		}
		else
		{
			params.x = hotspotView.hotspotAnchorPoint.x - SPOT_SIZE / 2;
			params.y = hotspotView.hotspotAnchorPoint.y - SPOT_SIZE / 2;
			params.width = SPOT_SIZE;
			params.height = SPOT_SIZE;

		}

		_windowManager.removeViewImmediate(hotspotView);
		_windowManager.addView(hotspotView, params);
	}

	@Override
	public void openHotpot(final boolean animation)
	{
		if (hotspotView.hotspotAnchorPoint.y < LARGE_PIE_SIZE / 2
				|| hotspotView.hotspotAnchorPoint.y > (getScreenHeight() - LARGE_PIE_SIZE / 2))
		{
			WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) hotspotView.getLayoutParams();
			Rect fromRect = rectFromLayoutParams(params);

			// move hotspot to open menu
			if (hotspotView.hotspotAnchorPoint.y < LARGE_PIE_SIZE / 2)
			{
				params.y = LARGE_PIE_SIZE / 2 - SPOT_SIZE / 2;
			}
			else if (hotspotView.hotspotAnchorPoint.y > (getScreenHeight() - LARGE_PIE_SIZE / 2))
			{
				params.y = getScreenHeight() - LARGE_PIE_SIZE / 2 - SPOT_SIZE / 2;
			}

			Rect toRect = rectFromLayoutParams(params);

			if (Math.abs(toRect.top - fromRect.top) > 0)
			{
				// animate move hotspot
				ValueAnimator translateAnimation = ValueAnimator.ofInt(fromRect.top, toRect.top);
				translateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator valueAnimator)
					{
						int val = (Integer) valueAnimator.getAnimatedValue();
						WindowManager.LayoutParams hotspotParams = (WindowManager.LayoutParams) hotspotView.getLayoutParams();
						hotspotParams.y = val;
						updateHotspoViewParams(hotspotParams);
						Rect hotSpotRect = rectFromLayoutParams(hotspotParams);
						hotspotView.hotspotAnchorPoint.x = hotSpotRect.centerX();
						hotspotView.hotspotAnchorPoint.y = hotSpotRect.centerY();
					}
				});
				translateAnimation.addListener(new AnimatorListener()
				{

					@Override
					public void onAnimationStart(Animator animator)
					{
						state = HotspotPieState.Animating;
					}

					@Override
					public void onAnimationRepeat(Animator animator)
					{
					}

					@Override
					public void onAnimationEnd(Animator animator)
					{
						state = HotspotPieState.Close;

						new Handler().postAtTime(new Runnable()
						{

							@Override
							public void run()
							{
								adjustWindowSize(true);
								hotspotView.adjustHotspot(HotspotPieState.OpenSmallPie, animation);
							}
						}, 200);

					}

					@Override
					public void onAnimationCancel(Animator animator)
					{
						onAnimationEnd(animator);
					}
				});

				translateAnimation.setDuration(300);
				translateAnimation.start();
			}
			else
			{
				adjustWindowSize(true);
				hotspotView.adjustHotspot(HotspotPieState.OpenSmallPie, animation);
			}
		}
		else
		{
			adjustWindowSize(true);
			hotspotView.adjustHotspot(HotspotPieState.OpenSmallPie, animation);
		}

	};

	@Override
	public void closeHotspot(boolean animation)
	{
		hotspotView.adjustHotspot(HotspotPieState.Close, animation);
	}

	@Override
	public ArrayList<MXHotspotButton> getSmallPieButtons()
	{
		ArrayList<MXHotspotButton> result = new ArrayList<MXHotspotButton>();

		MXHotspotButton addnewBtn = new MXHotspotButton(ButtonType.AddNew);
		result.add(addnewBtn);
		MXHotspotButton stopBtn = new MXHotspotButton(ButtonType.Stop);
		result.add(stopBtn);
		MXHotspotButton moreBtn = new MXHotspotButton(ButtonType.More);
		result.add(moreBtn);

		return result;
	}

	@Override
	public ArrayList<MXHotspotButton> getLargePieButtons()
	{
		ArrayList<MXHotspotButton> result = new ArrayList<MXHotspotButton>();

		MXHotspotButton manageSessionBtn = new MXHotspotButton(ButtonType.ManageSession);
		result.add(manageSessionBtn);
		MXHotspotButton captureBtn = new MXHotspotButton(ButtonType.Capture);
		result.add(captureBtn);
		MXHotspotButton editBtn = new MXHotspotButton(ButtonType.Edit);
		result.add(editBtn);
		MXHotspotButton settingBtn = new MXHotspotButton(ButtonType.Setting);
		result.add(settingBtn);

		return result;
	}

	public void onPieViewButtonClick(MXHotspotButton sender)
	{
		Log.i("Pieview", "Click " + sender.type.name());

		if (sender.type == ButtonType.More)
		{
			if (state == HotspotPieState.OpenLargePie)
			{
				hotspotView.adjustHotspot(HotspotPieState.OpenSmallPie, true);
			}
			else
			{
				hotspotView.adjustHotspot(HotspotPieState.OpenLargePie, true);
			}
		}
	};

	LayoutParams layoutParamsWithPosition(int x, int y, int width, int height)
	{
		Log.i("MXPlorerService", ">>>>>>>>> width: " + width + " x: " + x);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.width = width;
		params.height = height;
		params.x = x;
		params.y = y;

		return params;
	}

	public int getScreenWidth()
	{
		DisplayMetrics metrics = _context.getResources().getDisplayMetrics();
		return metrics.widthPixels;
	}

	public int getScreenHeight()
	{
		DisplayMetrics metrics = _context.getResources().getDisplayMetrics();
		return metrics.heightPixels;
	}

	public static int dpToPixels(int dpSize, Context context)
	{
		float screenDensity = context.getResources().getDisplayMetrics().density;
		int pixelSize = (int) (dpSize * screenDensity + 0.5f);
		return pixelSize;
	}

	public static Rect rectFromLayoutParams(ViewGroup.LayoutParams params)
	{
		if (params instanceof WindowManager.LayoutParams)
		{
			WindowManager.LayoutParams wdParams = (WindowManager.LayoutParams) params;
			return new Rect(wdParams.x, wdParams.y, wdParams.x + wdParams.width, wdParams.y + wdParams.height);
		}
		else if (params instanceof RelativeLayout.LayoutParams)
		{
			RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) params;
			return new Rect(rlParams.leftMargin, rlParams.topMargin, rlParams.leftMargin + rlParams.width, rlParams.topMargin
					+ rlParams.height);
		}
		else
		{
			return new Rect(0, 0, params.width, params.height);
		}

	}

	public static Rect rectFromView(View view)
	{
		return new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
	}

	public enum HotspotPieState
	{
		Close, Animating, OpenSmallPie, OpenLargePie
	}

	public enum HotspotDirection
	{
		Left, Right, Invalid
	}
}
