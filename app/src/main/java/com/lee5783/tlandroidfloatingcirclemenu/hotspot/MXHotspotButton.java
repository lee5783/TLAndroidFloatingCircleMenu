/**
 * 
 */
package com.lee5783.tlandroidfloatingcirclemenu.hotspot;


import com.lee5783.tlandroidfloatingcirclemenu.R;

/**
 * @author thule
 * 
 */
public class MXHotspotButton
{
	public ButtonState state;
	public int resourceId;
	public ButtonType type;

	public MXHotspotButton(ButtonType type)
	{
		this.type = type;
		this.state = ButtonState.Normal;
		switch (type)
		{
			case AddNew:
				resourceId = R.drawable.xml_add_new_btn;
				break;
				
			case Stop:
				resourceId = R.drawable.xml_stop_btn;
				break;
				
			case More:
				resourceId = R.drawable.xml_more_btn;
				break;
				
			case ManageSession:
				resourceId = R.drawable.xml_session_manager_btn;
				break;
				
			case Capture:
				resourceId = R.drawable.xml_capture_btn;
				break;
				
			case Edit:
				resourceId = R.drawable.xml_edit_btn;
				break;
				
			case Setting:
				resourceId = R.drawable.xml_setting_btn;
				break;

			default:
				break;
		}
	}

	public enum ButtonState
	{
		Normal, Disable, Highlight;
	}

	public enum ButtonType
	{
		AddNew, Stop, More, ManageSession, Capture, Edit, Setting
	}
}
