package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
import evebit.com.app.huajieoa.views.ProjectActivity.ButtonClickListener;
import evebit.com.app.huajieoa.views.ProjectActivity.ItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author YangChao 财务管理
 */
public class FinancialActivity extends Activity {
	private int[] images = { R.drawable.hand_present, R.drawable.currency_yen };
	private String [] Mid={"011500","011501"};
	private ArrayList<String> nameStrings = new ArrayList<String>();//存储子模块名称
	private ArrayList<String>countStrings = new ArrayList<String>();//存储子模块未处理信息个数
	private ArrayList<String>midStrings = new ArrayList<String>();//存储子模块ID
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String,String>>();
	private ProgressDialog progressDialog;
	
	private GridView gridView;
	private Button button = null;
	UserData userData=null;
	private String main=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.financial);
		PublicWay.activityList.add(this);
		main = (String)getIntent().getExtras().getString("main");

		userData =new UserData(this);
		gridView = (GridView) findViewById(R.id.Financial_GridView);
		button = (Button) findViewById(R.id.Financial_back);
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
		progressDialog = ProgressDialog.show(FinancialActivity.this, "", getResources().getString(R.string.get_date), true, false);
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
			ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < images.length; i++) {
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
			SimpleAdapter adapter = new SimpleAdapter(FinancialActivity.this, arrayList,
					R.layout.enterprise_gridview, new String[] { "ItemImage",
							"ItemText", "ItemTextUntreated" }, new int[] {
							R.id.Enterprise_Grid_Image,
							R.id.Enterprise_Grid_Text_Name,
							R.id.Enterprise_Grid_Text_Untreated });
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new ItemClickListener());
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.deviant, Toast.LENGTH_SHORT).show();
			}
			
		}
		
	};
	private void date() {
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
		
		
		/**
		 * 以下循环添加相应信息
		 * midStrings:子模块ID
		 * nameStrings:子模块名称
		 * countStrings:子模块可处理信息个数
		 */
		if (blockdata.size()>0) {
		for (int i = 0; i < blockdata.get(0).size(); i++) {
			midStrings.add(blockdata.get(0).get(String.valueOf(i)));
			nameStrings.add(blockdata.get(1).get(String.valueOf(i)));
			countStrings.add(blockdata.get(4).get(String.valueOf(i)));
		}
		}
		
		Log.v("----FinancialA", blockdata.size() + "---");
	}
	
	class ItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
		//	HashMap<String, Object> map = (HashMap<String, Object>) arg0
		//			.getItemAtPosition(arg2);
			// Toast.makeText(getApplicationContext(), arg2+"",
			// Toast.LENGTH_SHORT).show();判断点击的项
				Intent intent;
				if (Integer.parseInt(countStrings.get(arg2))>0) {
					intent = new Intent(FinancialActivity.this,FinanciaBorrowActivity.class);
					intent.putExtra("main",main);
					intent.putExtra("MIDkey",midStrings.get(arg2));
					startActivity(intent);
					finish();
				}
				else {
					intent = new Intent(FinancialActivity.this,HistoryBorrowActivity.class);
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
			Intent intent = new Intent(FinancialActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			userData.saveUserType("2");
			Intent intent = new Intent(FinancialActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
