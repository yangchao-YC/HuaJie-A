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
import android.widget.TextView;
import android.widget.Toast;


public class FinanciaBorrowActivity extends Activity {
	private String main = null;
	private Button backButton;
	private Button historyButton;
	private EditText searchEditText;
	private ImageView searchImageView;
	private TextView tabberTextView;
	private ListView listView = null;
	private String MIDkey = null;
	private String mName = null;
	private ArrayList<String>xidStrings = new ArrayList<String>();//存储信息ID
	private ArrayList<String> titleStrings = new ArrayList<String>();//存储标题信息
	private ArrayList<String>mNameList = new ArrayList<String>();//存储模块名称
	UserData userData;
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String,String>>();
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.financia_borrow);
		PublicWay.activityList.add(this);
		main = (String) getIntent().getExtras().get("main");// 接收传递数据
		MIDkey = (String)getIntent().getExtras().get("MIDkey");//接收子模块ID
		mName = (String)getIntent().getExtras().get("MName");
		
		listView = (ListView) findViewById(R.id.Financia_ListView);
		backButton = (Button) findViewById(R.id.Financia_Button_back);
		backButton.setOnClickListener(new backButtonClickListener());	
		searchImageView = (ImageView) findViewById(R.id.Financia_imageButton_search);
		searchEditText = (EditText) findViewById(R.id.Financia_search_EditText);
		tabberTextView = (TextView)findViewById(R.id.Financia_tabBarText);
		historyButton = (Button)findViewById(R.id.Financia_history);
		
		searchImageView.setOnClickListener(new searcButtonOnClick());// 搜索图片
		historyButton.setOnClickListener(new historyButtonOnClick());
		
		userData = new UserData(this);
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			thread();
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
		
	}
	private void thread() {
		progressDialog = ProgressDialog.show(FinanciaBorrowActivity.this, "", getResources().getString(R.string.get_date), true, false);
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
			if (blockdata.get(0).size()>0) {
				
				tabberTextView.setText(mNameList.get(0));
				mName = mNameList.get(0).toString();
			ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < xidStrings.size(); i++) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				//itemMap.put("image_left", R.drawable.oficial_green);
				itemMap.put("title_text",titleStrings.get(i).toString());
				itemMap.put("image_right", R.drawable.l);
				mData.add(itemMap);
			}
			SimpleAdapter adapter = new SimpleAdapter(FinanciaBorrowActivity.this, mData,
					R.layout.financia_listview, new String[] {
							"title_text", "image_right" }, new int[] {
							R.id.Financia_Listview_text,
							R.id.Financia_Listview_image_right });

			listView.setAdapter(adapter);
			// 点击触发
			listView.setOnItemClickListener(new OnItemClickListener() {
				Intent intent;

				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
						intent = new Intent(FinanciaBorrowActivity.this,
								OficialManagementDetailsActivity.class);
						intent.putExtra("main", main);
						intent.putExtra("MIDkey", MIDkey);
						intent.putExtra("guid", xidStrings.get(arg2));
						intent.putExtra("title", titleStrings.get(arg2));
						intent.putExtra("MName", mNameList.get(arg2));
						startActivity(intent);
						finish();
				}
			});
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.deviant, Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	private void date() {
		Log.v("---size   145--", blockdata.size() + "");
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(MIDkey);
		inputparams.add("");
		returnparams.add("strguid");
		returnparams.add("TITLE");
		returnparams.add("MName");
	    blockdata = soap.getSoapDataWithParam("getModuleList",inputparams,returnparams);//查询信息
	    Log.v("---size   145--", blockdata.size() + "");
	    Log.v("---size--", blockdata.get(0).size() + "");
	    if (blockdata.get(0).size()>0) {
	    	for (int i = 0; i < blockdata.get(0).size(); i++) {
				xidStrings.add(blockdata.get(0).get(String.valueOf(i)));
				titleStrings.add(blockdata.get(1).get(String.valueOf(i)));
				mNameList.add(blockdata.get(2).get(String.valueOf(i)));
			}
	    	
		}
	    Log.v("----FinanciaBorrowA", blockdata.size() + "---");
		
	}

	
	class historyButtonOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			intent = new Intent(FinanciaBorrowActivity.this,HistoryBorrowActivity.class);
			intent.putExtra("main",main);
			intent.putExtra("MIDkey",MIDkey);
			intent.putExtra("MName", mName);
			startActivity(intent);
			finish();
		}
		
	};
	
	class searcButtonOnClick implements OnClickListener {//搜索按钮

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (searchEditText.getText().toString().equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FinanciaBorrowActivity.this);
				builder.setTitle(getResources().getString(R.string.Prompt));
				builder.setMessage(getResources().getString(
						R.string.oficial_Prompt));
				builder.setPositiveButton(
						getResources().getString(R.string.Determine), null);
				builder.create().show();
			} else {
				Intent intent = new Intent(FinanciaBorrowActivity.this,SearchBorrowActivity.class);
				intent.putExtra("main", main);
				intent.putExtra("MIDkey", MIDkey);
				intent.putExtra("search", searchEditText.getText().toString());
				startActivity(intent);
				finish();
			}
		}
	}
	
	class backButtonClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(FinanciaBorrowActivity.this,
					MainActivity.class);
			intent.putExtra("main", main);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(FinanciaBorrowActivity.this,
					MainActivity.class);

			intent.putExtra("main", main);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
