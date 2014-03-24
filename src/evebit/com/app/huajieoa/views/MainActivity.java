package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.Hashtable;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {
	private TabHost myTabHost;
	private RadioGroup radioGroup;
	private RadioButton enterpriseRadioButton = null;
	private RadioButton projectRadioButton = null;
	private RadioButton financialRadioButton = null;
	private ProgressDialog progressDialog;
//	UserData userData = null;
	private String main;
	UserData userData;
	private Context mContext;
	private static String packgeName;
	private int vercode = -1;//存储版本号
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String, String>>();
//	ArrayList<String> nameStrings = new ArrayList<String>();
//	ArrayList<String> iDStrings = new ArrayList<String>();

	@Override
	/**
	 * 选项卡界面
	 * 此页面数据为固定数据，要修改此页面数据需进行版本更新，无法进行动态数据
	 * 如修改此页面数据需注意传递的对传递的mian进行修改
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Resources res = getResources();
	   userData = new UserData(this);
	   PublicWay.activityList.add(this);
		main = (String) getIntent().getExtras().get("main");
		if (main.equals("-")) {
			packgeName = this.getPackageName();
			vercode = MainActivity.getVerCode(this);//版本号
			Log.e("here", vercode+"");
			mContext = MainActivity.this;
			/*
			 * 执行推送消息
			 */
			serviceNotification();
			
			Normal normal = new Normal(this);// 判断是否有网络连接
			if (normal.note_Intent()) {// 判断是否有网络连接
				thread();
				
			} else {
				Toast.makeText(getApplicationContext(), "请连接网络",
						Toast.LENGTH_SHORT).show();
			}
			

		}
		radioGroup = (RadioGroup) findViewById(R.id.mainRadioGroup);
		enterpriseRadioButton = (RadioButton) findViewById(R.id.Main_Radio_enterprise);
		projectRadioButton = (RadioButton) findViewById(R.id.Main_Radio_project);
		financialRadioButton = (RadioButton) findViewById(R.id.Main_Radio_financial);

		myTabHost = getTabHost();
		TabSpec spec;
		Intent intent;
		intent = new Intent(MainActivity.this, EnterpriseActivity.class);
		intent.putExtra("main", "01");// 传递模块ID企事管理
		spec = myTabHost
				.newTabSpec("tab1")
				.setIndicator(
						getResources().getString(R.string.main_enterprise),
						res.getDrawable(R.drawable.main_enterprise))
				.setContent(intent);
		myTabHost.addTab(spec);
		intent = new Intent(MainActivity.this, ProjectActivity.class);
		intent.putExtra("main", "05");// 传递模块ID 项目管理
		spec = myTabHost
				.newTabSpec("tab2")
				.setIndicator(getResources().getString(R.string.main_project),
						res.getDrawable(R.drawable.main_project))
				.setContent(intent);
		myTabHost.addTab(spec);

		intent = new Intent(MainActivity.this, FinancialActivity.class);
		intent.putExtra("main", "0115");// 传递模块ID财务管理
		spec = myTabHost
				.newTabSpec("tab3")
				.setIndicator(
						getResources().getString(R.string.main_financial),
						res.getDrawable(R.drawable.main_financial))
				.setContent(intent);
		myTabHost.addTab(spec);

		intent = new Intent(MainActivity.this, SystemActivity.class);
		intent.putExtra("main", "3");// 传递模块ID
		spec = myTabHost
				.newTabSpec("tab4")
				.setContent(intent)
				.setIndicator(getResources().getString(R.string.main_system),
						res.getDrawable(R.drawable.main_system));
		myTabHost.addTab(spec);
		/**
		enterpriseRadioButton.setText(nameStrings.get(1).toString());// 设定企事管理
		projectRadioButton.setText(nameStrings.get(0).toString());
		financialRadioButton.setText(nameStrings.get(2).toString());
	*/
		enterpriseRadioButton.setText(getResources().getString(R.string.main_enterprise));// 设定企事管理
		projectRadioButton.setText(getResources().getString(R.string.main_project));
		financialRadioButton.setText(getResources().getString(R.string.main_financial));
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.Main_Radio_enterprise:
					myTabHost.setCurrentTab(0);
					break;
				case R.id.Main_Radio_project:
					myTabHost.setCurrentTab(1);
					break;
				case R.id.Main_Radio_financial:
					myTabHost.setCurrentTab(2);
					break;
				case R.id.Main_Radio_system:
					myTabHost.setCurrentTab(3);
					break;
				default:
					break;
				}
			}
		});
		/**
		 * 
		 * 传递回来的main的哪项管理，然后直接打开相应的主模块
		 */
		if (main.equals("-")) {
			myTabHost.setCurrentTab(0);
		} else if (main.equals("01")) {
			myTabHost.setCurrentTab(0);
		} else if (main.equals("05")) {
			myTabHost.setCurrentTab(1);
		} else if (main.equals("0115")) {
			myTabHost.setCurrentTab(2);
		} else if (main.equals("3")) {
			myTabHost.setCurrentTab(3);
		}
	}
	//执行推送消息
	private void serviceNotification() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, MyService.class);
		mContext.startService(intent);
	}
	
	private void thread() {
		//progressDialog = ProgressDialog.show(MainActivity.this, "正在获取版本号", getResources().getString(R.string.get_date), true, false);
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					vercodeSOPA();
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					// TODO: handle exception
				}
					
			}
			
		}.start();
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
		//	progressDialog.dismiss();
			String keyString = blockdata.get(1).get("0").toString();
			String verString = String.valueOf(vercode);
			Log.v("--------版本号-----------", verString);
		//	float key =    Double.valueOf(keyString);
			if (keyString.equals(verString)) {		
			}
			else {
				UpdateManager updateManager = new UpdateManager(MainActivity.this);
				updateManager.updateMsg = blockdata.get(0).get("0").toString();
			    updateManager.apkUrl = blockdata.get(2).get("0").toString();
				updateManager.checkUpdateInfo();
			}
			
		}
		
	};
	private void vercodeSOPA () {
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		
		
		returnparams.add("STRPARAMETERNAME");
		returnparams.add("STRPARAMETERVALUE");
		returnparams.add("STRREMARK");

		blockdata = soap.getSoapDataWithParam("getVersion",
				inputparams,returnparams);//查询信息
		Log.v("----MainA", blockdata.size() + "---");
	}
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(packgeName, 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e("log", e.getMessage());
		}
		return verCode;
	}

}
