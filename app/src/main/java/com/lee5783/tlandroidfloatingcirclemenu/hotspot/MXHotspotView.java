package com.lee5783.tlandroidfloatingcirclemenu.hotspot;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/**
 * @author thule
 *
 */
public class MXHotspotView extends RelativeLayout
{
	private final int DEFAULT_DELAY_TIME_MS = 300;

	public Point hotspotAnchorPoint;

	private MXSpotView spotView;
	private MXPieView smallPieView;
	private MXPieView largePieView;

	private MXHotspotViewDelegate _delegate;

	public MXHotspotView(Context context)
	{
		super(context);

		spotView = new MXSpotView(getContext());

		RelativeLayout.LayoutParams spotParams = new RelativeLayout.LayoutParams(MXHotspot.SPOT_SIZE, MXHotspot.SPOT_SIZE);
		spotView.setLayoutParams(spotParams);
		addView(spotView);
		hotspotAnchorPoint = new Point(0, 0);
	}

	public void adjustHotspot(MXHotspot.HotspotPieState newState, final boolean animation)
	{
		// change spot params
		if (MXHotspot.state == MXHotspot.HotspotPieState.Close && newState != MXHotspot.HotspotPieState.Close)
		{
			RelativeLayout.LayoutParams spotViewParams = (LayoutParams) spotView.getLayoutParams();
			spotViewParams.setMargins((int) (hotspotAnchorPoint.x - MXHotspot.SPOT_SIZE / 2),
					(int) (hotspotAnchorPoint.y - MXHotspot.SPOT_SIZE / 2), 0, 0);
			removeAllViews();
			addView(spotView, spotViewParams);
		}

		if (newState == MXHotspot.HotspotPieState.Close)
		{
			if (MXHotspot.state == MXHotspot.HotspotPieState.OpenSmallPie)
			{
				closeSmallPie(animation, new HotspotOpenCloseCallback()
				{

					@Override
					public void actionCallback()
					{
						
						if (_delegate != null)
							_delegate.adjustWindowSize(false);
						
						MXHotspot.state = MXHotspot.HotspotPieState.Close;
						RelativeLayout.LayoutParams spotViewParams = (LayoutParams) spotView.getLayoutParams();
						spotViewParams.setMargins(0, 0, 0, 0);
						removeAllViews();
						addView(spotView, spotViewParams);
					}
				});
			}
			else if (MXHotspot.state == MXHotspot.HotspotPieState.OpenLargePie)
			{
				closeLargePie(animation, new HotspotOpenCloseCallback()
				{

					@Override
					public void actionCallback()
					{
						MXHotspot.state = MXHotspot.HotspotPieState.Close;
					}
				});
				closeSmallPie(animation, new HotspotOpenCloseCallback()
				{

					@Override
					public void actionCallback()
					{
						MXHotspot.state = MXHotspot.HotspotPieState.Close;
						RelativeLayout.LayoutParams spotViewParams = (LayoutParams) spotView.getLayoutParams();
						spotViewParams.setMargins(0, 0, 0, 0);
						removeAllViews();
						addView(spotView, spotViewParams);
						if (_delegate != null)
							_delegate.adjustWindowSize(false);
					}
				});
			} else if(MXHotspot.state == MXHotspot.HotspotPieState.Close)
			{
				RelativeLayout.LayoutParams spotViewParams = (LayoutParams) spotView.getLayoutParams();
				spotViewParams.setMargins(0, 0, 0, 0);
				removeAllViews();
				addView(spotView, spotViewParams);
			}
		}
		else if (newState == MXHotspot.HotspotPieState.OpenSmallPie)
		{
			if (MXHotspot.state == MXHotspot.HotspotPieState.Close)
			{
				openSmallPie(animation, null);
			}
			else if (MXHotspot.state == MXHotspot.HotspotPieState.OpenLargePie)
			{
				closeLargePie(false, null);
			}
		}
		else if (newState == MXHotspot.HotspotPieState.OpenLargePie)
		{
			if (MXHotspot.state == MXHotspot.HotspotPieState.OpenSmallPie)
			{
				openLargePie(false, null);
			}
		}
	}

	private void openSmallPie(boolean animation, final HotspotOpenCloseCallback callback)
	{

		if (animation)
		{

			RelativeLayout.LayoutParams animationParams = new LayoutParams(MXHotspot.SPOT_SIZE, MXHotspot.SPOT_SIZE);
			animationParams.setMargins((int) (hotspotAnchorPoint.x - MXHotspot.SPOT_SIZE / 2),
					(int) (hotspotAnchorPoint.y - MXHotspot.SPOT_SIZE / 2), 0, 0);

			smallPieView = new MXSmallPieView(getContext());
			smallPieView.setLayoutParams(animationParams);

			addView(smallPieView, 0, animationParams);

			new Handler().postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					final float scaleX = MXHotspot.SMALL_PIE_SIZE / MXHotspot.SPOT_SIZE;
					ScaleAnimation scalseAnimation = new ScaleAnimation(1.0f, scaleX, 1.0f, scaleX, MXHotspot.SPOT_SIZE / 2,
							MXHotspot.SPOT_SIZE / 2);
					scalseAnimation.setDuration(DEFAULT_DELAY_TIME_MS);

					scalseAnimation.setAnimationListener(new AnimationListener()
					{

						@Override
						public void onAnimationStart(Animation animation)
						{
							MXHotspot.state = MXHotspot.HotspotPieState.Animating;
						}

						@Override
						public void onAnimationRepeat(Animation animation)
						{

						}

						@Override
						public void onAnimationEnd(Animation arg0)
						{
							openSmallPie(false, callback);
						}
					});

					smallPieView.startAnimation(scalseAnimation);
				}
			}, 50);
		}
		else
		{
			RelativeLayout.LayoutParams smallPieParams = new RelativeLayout.LayoutParams(MXHotspot.SMALL_PIE_SIZE,
					MXHotspot.SMALL_PIE_SIZE);
			smallPieParams.setMargins((int) (hotspotAnchorPoint.x - MXHotspot.SMALL_PIE_SIZE / 2),
					(int) (hotspotAnchorPoint.y - MXHotspot.SMALL_PIE_SIZE / 2), 0, 0);

			removeView(smallPieView);
			smallPieView = new MXSmallPieView(getContext());
			addView(smallPieView, 0, smallPieParams);

			if (_delegate != null)
			{
				smallPieView.setHotspotDelegate(_delegate);
				smallPieView.setPieButton(_delegate.getSmallPieButtons());
			}

			smallPieView.renderPieButton();
			MXHotspot.state = MXHotspot.HotspotPieState.OpenSmallPie;

			if (callback != null)
			{
				callback.actionCallback();
			}
		}
	}

	private void closeSmallPie(boolean animation, final HotspotOpenCloseCallback callback)
	{

		if (animation)
		{
			new Handler().postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					final float scaleX = MXHotspot.SPOT_SIZE / MXHotspot.SMALL_PIE_SIZE;
					ScaleAnimation scalseAnimation = new ScaleAnimation(1.0f, scaleX, 1.0f, scaleX,
							MXHotspot.SMALL_PIE_SIZE / 2, MXHotspot.SMALL_PIE_SIZE / 2);
					scalseAnimation.setDuration(DEFAULT_DELAY_TIME_MS);

					scalseAnimation.setAnimationListener(new AnimationListener()
					{

						@Override
						public void onAnimationStart(Animation animation)
						{
							MXHotspot.state = MXHotspot.HotspotPieState.Animating;
						}

						@Override
						public void onAnimationRepeat(Animation animation)
						{

						}

						@Override
						public void onAnimationEnd(Animation arg0)
						{
							closeSmallPie(false, callback);
						}
					});

					smallPieView.startAnimation(scalseAnimation);
				}
			}, 50);
		}
		else
		{
			removeView(smallPieView);
			MXHotspot.state = MXHotspot.HotspotPieState.Close;

			if (callback != null)
			{
				callback.actionCallback();
			}
		}
	}

	private void openLargePie(boolean animation, final HotspotOpenCloseCallback callback)
	{

		// TODO : i don't understand why I animation not work properly?
		if (animation)
		{
			RelativeLayout.LayoutParams animationParams = new LayoutParams(MXHotspot.SMALL_PIE_SIZE, MXHotspot.SMALL_PIE_SIZE);
			animationParams.setMargins((int) (hotspotAnchorPoint.x - MXHotspot.SMALL_PIE_SIZE / 2),
					(int) (hotspotAnchorPoint.y - MXHotspot.SMALL_PIE_SIZE / 2), 0, 0);

			largePieView = new MXSmallPieView(getContext());
			largePieView.setLayoutParams(animationParams);

			addView(largePieView, 0, animationParams);

			new Handler().postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					final float scaleX = MXHotspot.LARGE_PIE_SIZE / MXHotspot.SMALL_PIE_SIZE;
					ScaleAnimation scalseAnimation = new ScaleAnimation(1.0f, scaleX, 1.0f, scaleX,
							MXHotspot.SMALL_PIE_SIZE / 2, MXHotspot.SMALL_PIE_SIZE / 2);
					scalseAnimation.setDuration(DEFAULT_DELAY_TIME_MS);

					scalseAnimation.setAnimationListener(new AnimationListener()
					{

						@Override
						public void onAnimationStart(Animation animation)
						{
							MXHotspot.state = MXHotspot.HotspotPieState.Animating;
						}

						@Override
						public void onAnimationRepeat(Animation animation)
						{

						}

						@Override
						public void onAnimationEnd(Animation arg0)
						{
							openLargePie(false, callback);
						}
					});

					largePieView.startAnimation(scalseAnimation);
				}
			}, 20);
		}
		else
		{
			RelativeLayout.LayoutParams largePieParams = new RelativeLayout.LayoutParams(MXHotspot.LARGE_PIE_SIZE,
					MXHotspot.LARGE_PIE_SIZE);
			largePieParams.setMargins((int) (hotspotAnchorPoint.x - MXHotspot.LARGE_PIE_SIZE / 2),
					(int) (hotspotAnchorPoint.y - MXHotspot.LARGE_PIE_SIZE / 2), 0, 0);

			removeView(largePieView);
			largePieView = new MXLargePieView(getContext());
			addView(largePieView, 0, largePieParams);

			if (_delegate != null)
			{
				largePieView.setHotspotDelegate(_delegate);
				largePieView.setPieButton(_delegate.getLargePieButtons());
			}

			largePieView.renderPieButton();
			MXHotspot.state = MXHotspot.HotspotPieState.OpenLargePie;

			if (callback != null)
			{
				callback.actionCallback();
			}
		}
	}

	private void closeLargePie(boolean animation, final HotspotOpenCloseCallback callback)
	{
		// TODO : i don't understand why I animation not work properly?
		if (animation)
		{
			new Handler().postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					final float scaleX = MXHotspot.SMALL_PIE_SIZE / MXHotspot.LARGE_PIE_SIZE;
					ScaleAnimation scalseAnimation = new ScaleAnimation(1.0f, scaleX, 1.0f, scaleX,
							MXHotspot.LARGE_PIE_SIZE / 2, MXHotspot.LARGE_PIE_SIZE / 2);
					scalseAnimation.setDuration(DEFAULT_DELAY_TIME_MS);

					scalseAnimation.setAnimationListener(new AnimationListener()
					{

						@Override
						public void onAnimationStart(Animation animation)
						{
							MXHotspot.state = MXHotspot.HotspotPieState.Animating;
						}

						@Override
						public void onAnimationRepeat(Animation animation)
						{

						}

						@Override
						public void onAnimationEnd(Animation arg0)
						{
							closeLargePie(false, callback);
						}
					});

					largePieView.startAnimation(scalseAnimation);
				}
			}, 50);
		}
		else
		{
			removeView(largePieView);
			MXHotspot.state = MXHotspot.HotspotPieState.OpenSmallPie;

			if (callback != null)
			{
				callback.actionCallback();
			}
		}
	}

	float _firstTouchX, _firstTouchY;
	float _lastTouchX, _lastTouchY;

	long _firstTouchTime;

	private final int DETECT_CLICK_EVENT_TIME = 200;
	private final float DETECT_CLICK_EVENT_DISTANCE = 1.0f;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (MXHotspot.state == MXHotspot.HotspotPieState.Animating)
		{
			// skip event
			return true;
		}

		switch (MXHotspot.state)
		{
			case Close:
				return processTouchHotspotClose(event);
			case OpenSmallPie:
				return processTouchHotspotOpenSmallPie(event);
			case OpenLargePie:
				return processTouchHotspotOpenLargePie(event);
			case Animating:
			default:
				return true;
		}
	}

	private boolean processTouchHotspotClose(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				_lastTouchX = event.getRawX();
				_lastTouchY = event.getRawY();

				// determine user click
				_firstTouchX = event.getRawX();
				_firstTouchY = event.getRawY();
				_firstTouchTime = System.currentTimeMillis();

				break;
			case MotionEvent.ACTION_MOVE:
			{
				int deltaX = (int) (event.getRawX() - _lastTouchX);
				int deltaY = (int) (event.getRawY() - _lastTouchY);

				WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) getLayoutParams();

				params.x += deltaX;
				params.y += deltaY;

				Rect rect = MXHotspot.rectFromLayoutParams(params);

				_lastTouchX = event.getRawX();
				_lastTouchY = event.getRawY();

				if (_delegate != null)
					_delegate.moveHotspotToFrame(rect, false);
			}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			{

				_lastTouchX = event.getRawX();
				_lastTouchY = event.getRawY();

				// check if user click button
				int deltaX = (int) Math.abs(event.getRawX() - _firstTouchX);
				int deltaY = (int) Math.abs(event.getRawY() - _firstTouchY);
				long time = System.currentTimeMillis();

				if ((time - _firstTouchTime) < DETECT_CLICK_EVENT_TIME && deltaX < DETECT_CLICK_EVENT_DISTANCE
						&& deltaY < DETECT_CLICK_EVENT_DISTANCE && MXHotspot.direction != MXHotspot.HotspotDirection.Invalid)
				{
					if (_delegate != null)
						_delegate.openHotpot(true);
				}
				else
				{
					WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) getLayoutParams();
					Rect rect = MXHotspot.rectFromLayoutParams(params);
					int width = rect.width();
					if (rect.left > 0 && rect.right < MXHotspot.sharedInstance().getScreenWidth())
					{
						if (rect.centerX() > MXHotspot.sharedInstance().getScreenWidth() / 2)
						{
							rect.right = MXHotspot.sharedInstance().getScreenWidth();
							rect.left = rect.right - width;
						}
						else
						{
							rect.left = 0;
							rect.right = rect.left + width;

						}

						if (_delegate != null)
							_delegate.moveHotspotToFrame(rect, true);
					}
				}

			}
				break;
		}
		return true;
	}

	private boolean processTouchHotspotOpenSmallPie(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				_firstTouchX = event.getX();
				_firstTouchY = event.getY();
				_firstTouchTime = System.currentTimeMillis();
				_lastTouchX = event.getX();
				_lastTouchY = event.getY();

				break;
			case MotionEvent.ACTION_MOVE:
			{
				_lastTouchX = event.getX();
				_lastTouchY = event.getY();
			}
				break;

			case MotionEvent.ACTION_UP:
			{
				_lastTouchX = event.getX();
				_lastTouchY = event.getY();

				int deltaX = (int) (event.getX() - _firstTouchX);
				int deltaY = (int) (event.getY() - _firstTouchY);
				long time = System.currentTimeMillis();
				if (time - _firstTouchTime < DETECT_CLICK_EVENT_TIME && deltaX < DETECT_CLICK_EVENT_DISTANCE
						&& deltaY < DETECT_CLICK_EVENT_DISTANCE)
				{
					// check if user click outside
					RectF spotRect = new RectF(MXHotspot.rectFromLayoutParams(spotView.getLayoutParams()));
					RectF smallPieRect = new RectF(MXHotspot.rectFromLayoutParams(smallPieView.getLayoutParams()));
					if (!smallPieRect.contains(event.getX(), event.getY()) || spotRect.contains(event.getX(), event.getY()))
					{
						// close hotspot
						if (_delegate != null)
							_delegate.closeHotspot(true);
					}
					else
					{
						return super.onTouchEvent(event);
					}

				}
			}
				break;

			default:
				break;
		}
		return true;
	}

	private boolean processTouchHotspotOpenLargePie(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				_firstTouchX = event.getX();
				_firstTouchY = event.getY();
				_firstTouchTime = System.currentTimeMillis();
				_lastTouchX = event.getX();
				_lastTouchY = event.getY();

				break;
			case MotionEvent.ACTION_MOVE:
			{
				_lastTouchX = event.getX();
				_lastTouchY = event.getY();
			}
				break;

			case MotionEvent.ACTION_UP:
			{
				_lastTouchX = event.getX();
				_lastTouchY = event.getY();

				int deltaX = (int) (event.getX() - _firstTouchX);
				int deltaY = (int) (event.getY() - _firstTouchY);
				long time = System.currentTimeMillis();
				if (time - _firstTouchTime < DETECT_CLICK_EVENT_TIME && deltaX < DETECT_CLICK_EVENT_DISTANCE
						&& deltaY < DETECT_CLICK_EVENT_DISTANCE)
				{
					// check if user click outside
					RectF spotRect = new RectF(MXHotspot.rectFromLayoutParams(spotView.getLayoutParams()));
					RectF smallPieRect = new RectF(MXHotspot.rectFromLayoutParams(largePieView.getLayoutParams()));
					if (!smallPieRect.contains(event.getX(), event.getY()) || spotRect.contains(event.getX(), event.getY()))
					{
						// close hotspot
						if (_delegate != null)
							_delegate.closeHotspot(true);
					}
					else
					{
						return super.onTouchEvent(event);
					}

				}
			}
				break;

			default:
				break;
		}
		return true;
	}

	/**
	 * @param hotspotViewDelegate
	 *            the hotspotMoveDelegate to set
	 */
	public void setHotspotViewDelegate(MXHotspotViewDelegate hotspotViewDelegate)
	{
		this._delegate = hotspotViewDelegate;
	}

	public interface MXHotspotViewDelegate
	{
		void moveHotspotToFrame(Rect rect, boolean animation);

		void openHotpot(boolean animation);

		void closeHotspot(boolean animation);

		ArrayList<MXHotspotButton> getSmallPieButtons();
		ArrayList<MXHotspotButton> getLargePieButtons();

		void adjustWindowSize(boolean expand);

		void onPieViewButtonClick(MXHotspotButton sender);
	}

	public interface HotspotOpenCloseCallback
	{
		void actionCallback();
	}

}
