package evebit.com.app.huajieoa.views;

import java.util.Calendar;

import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
import android.app.Activity;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 
 * @author YangChao
 *系统设置
 */
public class SystemActivity extends Activity {
	private Button backButton;
	private TextView timeStartHourOfDay;
	private TextView timeStartMinute;
	private TextView timeEndHourOfDay;
	private TextView timeEndMinute;
	private TextView userTextView;
	private TextView aboutTextView;
	private CheckBox newsCheckBox;
	private CheckBox soundCheckBox;
	private CheckBox shockCheckBox;
	UserData userData = null;
	private	int mHour;
	private	int mMinute;
	private String main=null;
	private String startTime;
	private String endTime ;
	Calendar c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.system);
		PublicWay.activityList.add(this);
		
		main = (String)getIntent().getExtras().getString("main");
		userData = new UserData(this);
		timeStartHourOfDay = (TextView)findViewById(R.id.System_Time_Start_hourOfDay);
		timeStartMinute = (TextView)findViewById(R.id.System_Time_Start_minute);
		timeEndHourOfDay = (TextView)findViewById(R.id.System_Time_End_hourOfDay);
		timeEndMinute = (TextView)findViewById(R.id.System_Time_End_minute);
		aboutTextView = (TextView)findViewById(R.id.System_About);
		backButton = (Button)findViewById(R.id.System_back);
		userTextView = (TextView)findViewById(R.id.System_Text_SwitchUser);
		newsCheckBox = (CheckBox)findViewById(R.id.System_CheckBox_News);
		soundCheckBox = (CheckBox)findViewById(R.id.System_CheckBox_Sound);
		shockCheckBox = (CheckBox)findViewById(R.id.System_CheckBox_Shock);
		
		backButton.setOnClickListener(new ButtonClickListener());
		userTextView.setOnClickListener(new userOnClick());

		if (userData.getEndTime().toString().equals("")) {
			time();//记录时间
		}
		else {
			startTime = userData.getStartTime().toString();
			endTime = userData.getEndTime().toString();
			timeStartHourOfDay.setText(String.valueOf(startTime.charAt(0))+String.valueOf(startTime.charAt(1)));
			timeStartMinute.setText(String.valueOf(startTime.charAt(3))+String.valueOf(startTime.charAt(4)));
			timeEndHourOfDay.setText(String.valueOf(endTime.charAt(0))+String.valueOf(endTime.charAt(1)));
			timeEndMinute.setText(String.valueOf(endTime.charAt(3))+String.valueOf(endTime.charAt(4)));
		}
		
		
		if (userData.getRemind().equals("1")) {
			newsCheckBox.setChecked(true);
		}
		else {
			newsCheckBox.setChecked(false);
		}
		if (userData.getVoice().equals("1")) {
			soundCheckBox.setChecked(true);
		}
		else {
			soundCheckBox.setChecked(false);
		}
		if (userData.getShake().equals("1")) {
			shockCheckBox.setChecked(true);
		}
		else {
			shockCheckBox.setChecked(false);
		}
		newsCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				if (isChecked) {
					/*
					soundCheckBox.setChecked(true);
					shockCheckBox.setChecked(true);
					soundCheckBox.setVisibility(View.VISIBLE);
					shockCheckBox.setVisibility(View.VISIBLE);
					*/
					userData.saveRemind("1");
					Log.v("----check--------", userData.getRemind());
				}
				else {
					/*
					soundCheckBox.setChecked(false);
					shockCheckBox.setChecked(false);
					soundCheckBox.setVisibility(View.INVISIBLE);
					shockCheckBox.setVisibility(View.INVISIBLE);
					*/
					userData.saveRemind("0");
				}
				
			}
		});
		soundCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
				userData.saveVoice("1");
				}
				else {
				userData.saveVoice("0");
				}
				/**
				else {
					if (shockCheckBox.isChecked()==false) {
						Toast.makeText(getApplicationContext(), "必须选择一种提醒", Toast.LENGTH_SHORT).show();
						soundCheckBox.setChecked(true);
					}
				}
				*/
			}
		});
		
		shockCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					userData.saveShake("1");
				}
				else {
				userData.saveShake("0");
				}
				/**
				else {
					if (soundCheckBox.isChecked()==false) {
						Toast.makeText(getApplicationContext(), "必须选择一种提醒", Toast.LENGTH_SHORT).show();
						shockCheckBox.setChecked(true);
					}
				}
				*/
			}
		});
		aboutTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SystemActivity.this,
						AboutActivity.class);
				intent.putExtra("main",main);
				startActivity(intent);
				finish();
			}
		});
		
		 c= Calendar.getInstance();
		
		 
		/**
		 * 时间控件
		 * 第一个参数指当前指针，第三为小时，第四为分钟，第五为当前是否显示24小时制
		 * 点击设置后执行onTimeSet内方法
		 */
		 timeStartHourOfDay.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				c.setTimeInMillis(System.currentTimeMillis());
				mHour = c.get(Calendar.HOUR_OF_DAY);//获得当前时间小时
				mMinute = c.get(Calendar.MINUTE);//获得当前时间分
				new TimePickerDialog(SystemActivity.this, new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// TODO Auto-generated method stub   hourOfDay当前小时minute当前分钟
						int hour =Integer.parseInt(timeEndHourOfDay.getText().toString());
						int min = Integer.parseInt(timeEndMinute.getText().toString());
						
						if (hourOfDay<hour) {
							if (hourOfDay<10) {
								timeStartHourOfDay.setText("0"+String.valueOf(hourOfDay));
							}
							else {
								timeStartHourOfDay.setText(String.valueOf(hourOfDay));
								time();
							}
							if (minute<10) {
								timeStartMinute.setText( "0" + String.valueOf(minute));
							}
							else {
								timeStartMinute.setText(String.valueOf(minute));
								time();
							}
							
							
						}
						else if (hourOfDay == hour) {
							if (minute<min) {
								timeEndHourOfDay.setText(String.valueOf(hourOfDay));
								timeEndMinute.setText(String.valueOf(minute));
							}
							else {
								Toast.makeText(getApplicationContext(), "起始时间大于截至时间", Toast.LENGTH_SHORT).show();
								timeStartHourOfDay.setText("08");
								timeStartMinute.setText("00");
								timeEndHourOfDay.setText("24");
								timeEndMinute.setText("00");	
							}
						}
						else {
							Toast.makeText(getApplicationContext(), "起始时间大于截至时间", Toast.LENGTH_SHORT).show();
							timeStartHourOfDay.setText("08");
							timeStartMinute.setText("00");
							timeEndHourOfDay.setText("24");
							timeEndMinute.setText("00");
						}
						
					}
				}, mHour, mMinute, true).show();
			}
		});
		 timeEndHourOfDay.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				c.setTimeInMillis(System.currentTimeMillis());
				mHour = c.get(Calendar.HOUR_OF_DAY);//获得当前时间小时
				mMinute = c.get(Calendar.MINUTE);//获得当前时间分
				new TimePickerDialog(SystemActivity.this, new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// TODO Auto-generated method stub   hourOfDay当前小时minute当前分钟
						int hour =Integer.parseInt(timeStartHourOfDay.getText().toString());
						int min = Integer.parseInt(timeStartMinute.getText().toString());
						if (hourOfDay>hour) {
							if (hourOfDay<10) {
								timeEndHourOfDay.setText("0" + String.valueOf(hourOfDay));
							}
							else {
								timeEndHourOfDay.setText(String.valueOf(hourOfDay));
								time();
							}
							if (minute < 10) {
								timeEndMinute.setText("0" + String.valueOf(minute));
							}
							else {
								timeEndMinute.setText(String.valueOf(minute));
								time();
							}
							
							
						}
						else if (hourOfDay == hour) {
							if (minute>min) {
								timeEndHourOfDay.setText(String.valueOf(hourOfDay));
								timeEndMinute.setText(String.valueOf(minute));
							}
							else {
								Toast.makeText(getApplicationContext(), "起始时间大于截至时间", Toast.LENGTH_SHORT).show();
								timeStartHourOfDay.setText("08");
								timeStartMinute.setText("00");
								timeEndHourOfDay.setText("24");
								timeEndMinute.setText("00");	
							}
						}
						else {
							Toast.makeText(getApplicationContext(), "起始时间大于截至时间", Toast.LENGTH_SHORT).show();
							timeStartHourOfDay.setText("08");
							timeStartMinute.setText("00");
							timeEndHourOfDay.setText("24");
							timeEndMinute.setText("00");
						}
					}
				}, mHour, mMinute, true).show();
			}
		});
	}
	
	
	private void time() {
		startTime = timeStartHourOfDay.getText().toString() + ":"+ timeStartMinute.getText().toString();
		endTime = timeEndHourOfDay.getText().toString() + ":"+ timeEndMinute.getText().toString();
		userData.saveStartTime(startTime);
		userData.saveEndTime(endTime);
	}
	
	
	class userOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			userData.saveUserType("2"); 
			userData.savePassword("");
			Intent intent = new Intent(SystemActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}
		
	}
	
	class ButtonClickListener implements OnClickListener {
		// 返回登录页面
		public void onClick(View v) {
			// TODO Auto-generated method stub
			userData.saveUserType("2");
			Intent intent = new Intent(SystemActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			userData.saveUserType("2");
			Intent intent = new Intent(SystemActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
