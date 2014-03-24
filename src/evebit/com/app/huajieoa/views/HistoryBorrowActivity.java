package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;

import evebit.com.app.huajieoa.views.HistoryBorrowTestActivity.historyButtonOnClick;
import evebit.com.app.huajieoa.views.HistoryBorrowTestActivity.searcButtonOnClick;
import evebit.com.app.huajieoa.views.XListView.IXListViewListener;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class HistoryBorrowActivity extends Activity implements 
IXListViewListener{
	
	
	private Button backButton;
	private Button historyButton;
	private EditText searchEditText;
	private ImageView searchImageView;
	private TextView tabberTextView;
	private XListView mListView;
	
	
	private SimpleAdapter mAdapter;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;
	
	
	private String main = null;
	private String MIDkey = null;
	private String searchString = "";//搜索参数
	private String mName = null;
	
	private int page = 0;
	private ArrayList<String>xidStrings = new ArrayList<String>();//存储信息ID
	private ArrayList<String> titleStrings = new ArrayList<String>();//存储标题信息
	private ArrayList<String>mNameList = new ArrayList<String>();//存储模块名称
	UserData userData;
	ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String,String>>();
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.history_borrow);
		PublicWay.activityList.add(this);
		main = (String) getIntent().getExtras().get("main");// 接收传递数据
		MIDkey = (String)getIntent().getExtras().get("MIDkey");//接收子模块ID
		mName = (String)getIntent().getExtras().get("MName");
	//	geneItems();
		Log.v("--------mName----", mName);
		backButton = (Button) findViewById(R.id.History_Button_back);
		searchImageView = (ImageView) findViewById(R.id.History_imageButton_search);
		searchEditText = (EditText) findViewById(R.id.History_search_EditText);
		tabberTextView = (TextView)findViewById(R.id.History_tabBarText);
		historyButton = (Button)findViewById(R.id.History_history);
		mListView = (XListView) findViewById(R.id.History_ListView);
		
		mListView.setPullLoadEnable(true);
		//	mAdapter = new ArrayAdapter<String>(this, R.layout.list_item, items);
		//	mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		mHandler = new Handler();
		
		historyButton.setText("已办事务");
		tabberTextView.setText(mName);
		
		Toast.makeText(getApplicationContext(), "您现在查看的是历史数据", Toast.LENGTH_SHORT).show();
		
		userData = new UserData(this);
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			progressDialog = ProgressDialog.show(HistoryBorrowActivity.this, "", getResources().getString(R.string.get_date), true, false);
			
			thread();
			
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
		
	
		backButton.setOnClickListener(new backButtonClickListener());
		searchImageView.setOnClickListener(new searcButtonOnClick());// 搜索图片
		historyButton.setOnClickListener(new historyButtonOnClick());
		
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			
				Intent intent;
				intent = new Intent(HistoryBorrowActivity.this,
						HistoryDetailsActivity.class);
				
				intent.putExtra("main", main);
				intent.putExtra("MIDkey", MIDkey);
				intent.putExtra("guid", xidStrings.get(arg2-1));
				intent.putExtra("title", titleStrings.get(arg2-1));
				intent.putExtra("MName", mName);
				Log.v("--------mName----", mName);
				startActivity(intent);
				finish();
			}	
		});
		
		
	}
	
	
	class historyButtonOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			intent = new Intent(HistoryBorrowActivity.this,FinanciaBorrowActivity.class);
			intent.putExtra("main",main);
			intent.putExtra("MIDkey",MIDkey);
			intent.putExtra("MName", mName);
			startActivity(intent);
			finish();
		}
		
	};
	
	private void thread() {
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

				mAdapter = new SimpleAdapter(HistoryBorrowActivity.this,
						mData, R.layout.financia_listview, new String[] {
							 "title_text",  "image_right" },
						new int[] { 
								R.id.Financia_Listview_text,
								R.id.Financia_Listview_image_right });
			mListView.setAdapter(mAdapter);
			mListView.setXListViewListener(HistoryBorrowActivity.this);
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
		inputparams.add(MIDkey);
		inputparams.add(searchString);
		inputparams.add("10");
		inputparams.add(String.valueOf(page * 10));
		returnparams.add("strguid");
		returnparams.add("TITLE");
		returnparams.add("MName");
	    blockdata = soap.getSoapDataWithParam("getOldModuleList", inputparams, returnparams);
	    if (blockdata.size()>0) {
	    	for (int i = 0; i < blockdata.get(0).size(); i++) {
				xidStrings.add(blockdata.get(0).get(String.valueOf(i)));
				titleStrings.add(blockdata.get(1).get(String.valueOf(i)));
				mNameList.add(blockdata.get(2).get(String.valueOf(i)));
			}	
	    	for (int i = 0; i < blockdata.get(0).size(); i++) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put("title_text",blockdata.get(1).get(String.valueOf(i).toString()));
				itemMap.put("image_right", R.drawable.l);
				mData.add(itemMap);
			}
		}
	    
	    Log.v("----HistoryBorrowA", blockdata.size() + "");
		
	}
	
	
	
	
	private void geneItems() {
		
	}
	
	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				start = ++refreshCnt;
				page =0 ;
				mData.clear();
				Normal normal = new Normal(HistoryBorrowActivity.this);// 判断是否有网络连接
				if (normal.note_Intent()) {// 判断是否有网络连接
					thread();
				} else {
					Toast.makeText(getApplicationContext(), "请连接网络",
							Toast.LENGTH_SHORT).show();
				}
				onLoad();
			}
		}, 2000);
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				page++;
				Normal normal = new Normal(HistoryBorrowActivity.this);// 判断是否有网络连接
				if (normal.note_Intent()) {// 判断是否有网络连接
					thread();
					mAdapter.notifyDataSetChanged();
					onLoad();
					
				} else {
					Toast.makeText(getApplicationContext(), "请连接网络",
							Toast.LENGTH_SHORT).show();
				}
				
			}
		}, 2000);
		
	}

	
	class searcButtonOnClick implements OnClickListener {//搜索按钮

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if (searchEditText.getText().toString().equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						HistoryBorrowActivity.this);
				builder.setTitle(getResources().getString(R.string.Prompt));
				builder.setMessage(getResources().getString(
						R.string.oficial_Prompt));
				builder.setPositiveButton(
						getResources().getString(R.string.Determine), null);
				builder.create().show();
			} else {
				progressDialog = ProgressDialog.show(HistoryBorrowActivity.this,"", "正在搜索，请稍后", true, false);
				xidStrings.clear();
				titleStrings.clear();
				mNameList.clear();
				searchString = searchEditText.getText().toString();
				Log.v("----搜索----", "------搜索-----");
				onRefresh();	
			}
		}
	}

	
	
	
	class backButtonClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(HistoryBorrowActivity.this,
					MainActivity.class);
			intent.putExtra("main", main);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(HistoryBorrowActivity.this,
					MainActivity.class);

			intent.putExtra("main", main);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
