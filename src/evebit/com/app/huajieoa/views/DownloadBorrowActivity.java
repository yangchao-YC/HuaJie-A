package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
import evebit.com.app.huajieoa.views.OficialManagementActivity.searcButtonOnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class DownloadBorrowActivity extends Activity {
	
	private String main=null;
	private String MIDkey = null;
	private String guid=null;
	private String title=null;
	private String mName=null;
	
	private int listSum =0;
	private Button backButton;
	private ListView listView = null;
	private ArrayList<String>fnameString = new ArrayList<String>();//存储信息名称
	private ArrayList<String> urlString = new ArrayList<String>();//存储信息地址
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String,String>>();
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.download_borrow);
		PublicWay.activityList.add(this);
		
		main =(String)getIntent().getExtras().get("main");
		MIDkey = (String)getIntent().getExtras().get("MIDkey");//接收子模块ID	
		guid =(String)getIntent().getExtras().getString("guid");//接收此条项目的ID
		title = (String)getIntent().getExtras().getString("title");//接收标题
		mName = (String)getIntent().getExtras().getString("MName");//接受子栏目名称
		listView = (ListView) findViewById(R.id.Download_ListView);
		
		backButton = (Button) findViewById(R.id.Download_Button_back);
		backButton.setOnClickListener(new backButtonClickListener());	
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			thread();
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
		
	}
	private void thread() {
		progressDialog = ProgressDialog.show(DownloadBorrowActivity.this, "", getResources().getString(R.string.get_date), true, false);
		new Thread(){
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
			if (listSum == 0) {
			ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < fnameString.size(); i++) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				//itemMap.put("image_left", R.drawable.oficial_green);
				itemMap.put("title_text",fnameString.get(i).toString());
				mData.add(itemMap);
			}
			SimpleAdapter adapter = new SimpleAdapter(DownloadBorrowActivity.this, mData,
					R.layout.financia_listview, new String[] {
							"title_text"}, new int[] {
							R.id.Financia_Listview_text });

			listView.setAdapter(adapter);
			// 点击触发
			listView.setOnItemClickListener(new OnItemClickListener() {
				

				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					String url = urlString.get(arg2).toString();
					Intent intent = new Intent();        
					intent.setAction("android.intent.action.VIEW");    
					Uri content_url = Uri.parse(url);   
					intent.setData(content_url);  
					startActivity(intent);
				}
			});
			}
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
		inputparams.add(MIDkey);
		inputparams.add(guid);
		returnparams.add("fname");
		returnparams.add("url");
		
		blockdata = soap.getSoapDataWithParam("getFJList",
				inputparams,returnparams);//查询信息

	    if (blockdata.size() == 0) {
	    	listSum = 1;	
		}
	    else {
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				fnameString.add(blockdata.get(0).get(String.valueOf(i)));
				urlString.add(blockdata.get(1).get(String.valueOf(i)));
			}
		}
	    
	    Log.v("----DownloadBorrowA", blockdata.size() + "---");
	}

	class backButtonClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(DownloadBorrowActivity.this,
					OficialManagementDetailsActivity.class);
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid",guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(DownloadBorrowActivity.this,
					OficialManagementDetailsActivity.class);

			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid",guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
