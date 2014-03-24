package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author YangChao 企事管理
 */
public class EnterpriseActivity extends Activity {
	private int[] images = { 
			R.drawable.oficialadd,
			R.drawable.oficial,
			R.drawable.articles,
			R.drawable.logistics,
			R.drawable.vehicle,
			R.drawable.seal  
			};//存储图片
	private String [] Mid = {
			"010100",
			"010101",
			"01040202",
			"010408",
			"01040600",
			"011201" 
			};//存储对应栏目ID
	
	private Timer timer = new Timer();
	private TimerTask task;
	ArrayList<Hashtable<String, String>> blockdata =new ArrayList<Hashtable<String,String>>();
	private ArrayList<String> nameStrings = new ArrayList<String>();//存储子模块名称
	private ArrayList<String>countStrings = new ArrayList<String>();//存储子模块未处理信息个数
	private ArrayList<String>midStrings = new ArrayList<String>();//存储子模块ID
	private ProgressDialog progressDialog;
	
	private GridView gridView;
	private Button button = null;
	private TextView untreatedTextView;
	private String main=null;
	UserData userData;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enterprise);
		PublicWay.activityList.add(this);
		userData =new UserData(this);
		main = (String)getIntent().getExtras().getString("main");//获取传递的模块ID
		gridView = (GridView) findViewById(R.id.Enterprise_GridView);
		button = (Button) findViewById(R.id.Enterprise_back);
		untreatedTextView = (TextView) findViewById(R.id.Enterprise_Grid_Text_Untreated);
		button.setOnClickListener(new ButtonClickListener());
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			thread();
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
		

	}

	private void thread() {
		progressDialog = ProgressDialog.show(EnterpriseActivity.this, "", getResources().getString(R.string.get_date), true, false);
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					date();
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
			progressDialog.dismiss();
			if (blockdata.size()>0) {
				/**
				 * 以下循环添加相应信息
				 * midStrings:子模块ID
				 * nameStrings:子模块名称
				 * countStrings:子模块可处理信息个数
				 */	
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				midStrings.add(blockdata.get(0).get(String.valueOf(i)));//因为blockdata内数据组内索引为字符串形式，所以需转换
				nameStrings.add(blockdata.get(1).get(String.valueOf(i)));
				countStrings.add(blockdata.get(4).get(String.valueOf(i)));
			}
			ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemText", nameStrings.get(i).toString());
				//以下循环检测此模块图片是否对应
				for (int j = 0; j < Mid.length; j++) {
					if (midStrings.get(i).equals(Mid[j])) {
						map.put("ItemImage", images[j]);
						break;
					}	
				}
				map.put("ItemTextUntreated", countStrings.get(i));
				arrayList.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(EnterpriseActivity.this, arrayList,
					R.layout.enterprise_gridview, new String[] { "ItemImage",
							"ItemText", "ItemTextUntreated" }, new int[] {
							R.id.Enterprise_Grid_Image, R.id.Enterprise_Grid_Text_Name,
							R.id.Enterprise_Grid_Text_Untreated });
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new ItemClickListener());
			
			//time();
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.deviant, Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	private void time() {
		task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				timeThread();
			}
		};
		
		timer.schedule(task, 10000,10000);
	}
	private void timeThread() {
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					date();
					handlerTime.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO: handle exception
				}	
			}
			
		}.start();
	}
	
	private Handler handlerTime = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (blockdata.size()>0) {
				for (int i = 0; i < blockdata.get(0).size(); i++) {
					if (countStrings.get(i).equals(blockdata.get(4).get(String.valueOf(i)))) {
						handler.sendEmptyMessage(1);
					}
				}
			}
		}
		
	};
	
	private void date() {
		/**
		 * 此时只查询公文管理的数据，需建立一张表，表内存储各种类型未处理的数据的数量
		 * 查询此表就可以得到该显示的提示数字，否则查询多张表格会导致程序很卡
		*/
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(main);
		
		returnparams.add("MID");
		returnparams.add("MNAME");
		returnparams.add("TNAME");
		returnparams.add("LID");	
		returnparams.add("NCOUNT");

		blockdata = soap.getSoapDataWithParam("getModuleInfoList",
				inputparams,returnparams);//查询信息
		Log.v("----EnterpriseA", blockdata.size() + "---");
		
	}
	
	class ItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
		//	HashMap<String, Object> map = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
			// Toast.makeText(getApplicationContext(), arg2+"",
			// Toast.LENGTH_SHORT).show();判断点击的项
			Intent intent;
			if (Integer.parseInt(countStrings.get(arg2))>0) {
				
				intent = new Intent(EnterpriseActivity.this,FinanciaBorrowActivity.class);
				intent.putExtra("main",main);
				intent.putExtra("MIDkey",midStrings.get(arg2));
				startActivity(intent);
				finish();
			}
			else {
				//当无数据时跳转到历史数据处列表
				intent = new Intent(EnterpriseActivity.this,HistoryBorrowActivity.class);
				intent.putExtra("main",main);
				intent.putExtra("MIDkey",midStrings.get(arg2));
				intent.putExtra("MName", nameStrings.get(arg2));
				startActivity(intent);
				finish();
			}
		}
	}

	class ButtonClickListener implements OnClickListener {
		// 返回登录页面
		public void onClick(View v) {
			// TODO Auto-generated method stub
			userData.saveUserType("2");
			Intent intent = new Intent(EnterpriseActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			userData.saveUserType("2");
			Intent intent = new Intent(EnterpriseActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
