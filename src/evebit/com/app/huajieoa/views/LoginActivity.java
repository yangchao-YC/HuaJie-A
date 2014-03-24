package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.Hashtable;

import evebit.com.app.huajieoa.models.GetSoapAsyncTask;
import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.UserData;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import evebit.com.app.huajieoa.models.PublicWay;;
public class LoginActivity extends Activity implements
		android.view.View.OnClickListener {
	/** Called when the activity is first created. */
	private EditText userEditText = null;
	private EditText pwdEditText = null;
	private CheckBox pwdCheckBox = null;
	private CheckBox autoLoginCheckBox = null;
	private Button loginButton = null;
	private Button cancelButton = null;

	private String userNameString;
	private String pwdString;
	private ProgressDialog progressDialog;
	UserData userData = null;
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// 绑定控件
		userEditText = (EditText)findViewById(R.id.Login_EditText_user);
		pwdEditText = (EditText)findViewById(R.id.Login_EditText_pwd);
		pwdCheckBox = (CheckBox)findViewById(R.id.Login_CheckBox_password);
		autoLoginCheckBox = (CheckBox)findViewById(R.id.Login_CheckBox_autoLogin);
		loginButton = (Button) findViewById(R.id.Login_Button_login);
		cancelButton = (Button)findViewById(R.id.Login_Button_cancel);

		userData = new UserData(this);

		if (userData.getUserType().equals("0")) {
			
			Intent intent = null;
			Normal normal = new Normal(this);// 判断是否有网络连接
			if (normal.note_Intent()) {// 判断是否有网络连接
				userNameString = userData.getUserName();
				pwdString = userData.getPassword();
				threadAuto();
				
			} else {
				Toast.makeText(getApplicationContext(), "请连接网络",
						Toast.LENGTH_SHORT).show();
			}
		}
		userEditText.setText(userData.getUserName());
		pwdEditText.setText(userData.getPassword());
		loginButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		// pwdcheckbox事件监听
		this.pwdCheckBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub

					}
				});
	}

	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.Login_Button_login:
			Normal normal = new Normal(this);// 判断是否有网络连接
			if (normal.note_Intent()) {// 判断是否有网络连接
			    userNameString =userEditText.getText().toString();
				pwdString = pwdEditText.getText().toString();
				threadOnChick();
				
			} else {
				Toast.makeText(getApplicationContext(), "请连接网络",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.Login_Button_cancel:
			if (autoLoginCheckBox.isChecked()) {
				userData.saveUserType("0");
			}
			else {
				userData.saveUserType("1");
			}
			finish();
			break;
		default:
			break;
		}
		// startActivity(intent);
	}
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.v("--------120----", "---");
			progressDialog.dismiss();
			Intent intent = null;
			if (blockdata.size() > 0) {
				if (blockdata.get(0).size() > 0) {

					// 判断pwdCheckBox状态
					if (pwdCheckBox.isChecked()) {
						userData.saveUserName(userNameString);
						Log.v("测试测试测试环节001", userData.getUserName().toString());
						userData.savePassword(pwdEditText.getText()
								.toString());
						
						if (autoLoginCheckBox.isChecked()) {
							userData.saveUserType("0");// 0为自动登录
						} else {
							userData.saveUserType("1");// 1为不自动登录
						}
					} else {
						userData.saveUserName(userNameString);
						userData.savePassword("");
					}
					
					intent = new Intent(LoginActivity.this,
							MainActivity.class);
					intent.putExtra("main", "-");
					startActivity(intent);
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.login_OK),
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					pwdEditText.setText("");
					userData.savePassword("");
				
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.login_fail),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "服务器数据异常",
						Toast.LENGTH_SHORT).show();
				/*
				intent = new Intent(LoginActivity.this,
						MainActivity.class);
				intent.putExtra("main", "-");
				startActivity(intent);
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.login_OK),
						Toast.LENGTH_SHORT).show();
				finish();
				*/
			}
		}
		
	};
	
	private Handler handlerAuto = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Intent intent = null;
			if (blockdata.size() > 0) {
				if (blockdata.get(0).size() > 0) {
					
					intent = new Intent(LoginActivity.this,
							MainActivity.class);
					intent.putExtra("main", "-");
					startActivity(intent);
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.login_OK),
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					pwdEditText.setText("");
					userData.savePassword("");
					
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.login_fail),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.deviant,
						Toast.LENGTH_SHORT).show();
				/*
				intent = new Intent(LoginActivity.this,
						MainActivity.class);
				intent.putExtra("main", "-");
				startActivity(intent);
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.login_OK),
						Toast.LENGTH_SHORT).show();
						*/
			}
		}
		
	};
	
	private void threadOnChick() {
		progressDialog = ProgressDialog.show(LoginActivity.this, "",getResources().getString(R.string.login_intent), true, false);
		new Thread() {
            @Override
			public void run() {
            	try {
            		loginEquals();
            		handler.sendEmptyMessage(0);
				} catch (Exception e) {
					// TODO: handle exception
				}
            		
            		
            }
		}.start();
	}
	
	private void threadAuto() {
		progressDialog = ProgressDialog.show(LoginActivity.this, "", getResources().getString(R.string.login_intent), true, false);
		new Thread() {
            @Override
			public void run() {
            	try {
            		loginEquals();
    				
				} catch (Exception e) {
					// TODO: handle exception
				}
            	handler.sendEmptyMessage(0);
            }
		}.start();
	}
	public void loginEquals() {
	
		
			GetSoapData soap = new GetSoapData();
    		ArrayList<String> inputparams = new ArrayList<String>();
    		inputparams.add(userNameString);
    		inputparams.add(pwdString);
    		ArrayList<String> returnparams = new ArrayList<String>();
    		returnparams.add("STRGUID");
    		returnparams.add("STRUSERNAME");
    		returnparams.add("STRUSERACCOUNT");
    		returnparams.add("STRDEPARTCODE");
    		returnparams.add("STRDEPARTNAME");
    		blockdata = soap.getSoapDataWithParam("Login", inputparams,returnparams);
    		Log.v("----LoginA", blockdata.size() + "---");
    
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			/*
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				return true;
			}
			else {
				*/
			if (autoLoginCheckBox.isChecked()) {
				userData.saveUserType("0");
			}
			else {
				userData.saveUserType("1");
			}
			
			for(int i=0;i<PublicWay.activityList.size();i++){
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			
			finish();
			return true;
			}
			//} 
			//else
			return super.onKeyDown(keyCode, event);
	}
}
