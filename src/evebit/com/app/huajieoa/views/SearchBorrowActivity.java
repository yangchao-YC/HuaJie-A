package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchBorrowActivity extends Activity {
	private String main = null;
	private Button backButton;
	private ImageView searchButton;
	private String searchString;
	private EditText searchEditText;
	private ListView listView = null;
	private int listSum =0;
	private String MIDkey = null;
	private ArrayList<String>xidStrings = new ArrayList<String>();//存储信息ID
	private ArrayList<String> titleStrings = new ArrayList<String>();//存储标题信息
	private ArrayList<String>mNameList = new ArrayList<String>();//存储模块名称
	UserData userData;
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String,String>>();
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_borrow);
		PublicWay.activityList.add(this);
		searchString = (String) getIntent().getExtras().get("search");// 接收传递数据
		main = (String) getIntent().getExtras().get("main");// 接收传递数据
		MIDkey = (String)getIntent().getExtras().get("MIDkey");//接收子模块ID
		searchButton = (ImageView) findViewById(R.id.Search_imageButton_search);
		searchEditText = (EditText)findViewById(R.id.Search_search_EditText);
		listView = (ListView) findViewById(R.id.Search_ListView);
		backButton = (Button) findViewById(R.id.Search_Button_back);
		backButton.setOnClickListener(new backButtonClickListener());
		userData = new UserData(this);
		
		
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			thread();
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (searchEditText.getText().toString().equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SearchBorrowActivity.this);
					builder.setTitle(getResources().getString(R.string.Prompt));
					builder.setMessage(getResources().getString(
							R.string.oficial_Prompt));
					builder.setPositiveButton(
							getResources().getString(R.string.Determine), null);
					builder.create().show();
				} else {
					searchString = searchEditText.getText().toString();
					thread();
				}
			}
		});
		

	}
	private void thread() {
		progressDialog = ProgressDialog.show(SearchBorrowActivity.this, "", getResources().getString(R.string.get_date), true, false);
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
			if (listSum == 0) {
				ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < xidStrings.size(); i++) {
					Map<String, Object> itemMap = new HashMap<String, Object>();
					//itemMap.put("image_left", R.drawable.oficial_green);
					itemMap.put("title_text",titleStrings.get(i).toString());
					itemMap.put("image_right", R.drawable.oficial_right);
					mData.add(itemMap);
				}
				SimpleAdapter adapter = new SimpleAdapter(SearchBorrowActivity.this, mData,
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
							intent = new Intent(SearchBorrowActivity.this,
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
			
			
		}
		
	};
	private void date() {
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(MIDkey);
		inputparams.add(searchString);
		returnparams.add("strguid");
		returnparams.add("TITLE");
		returnparams.add("MName");
	    blockdata = soap.getSoapDataWithParam("getModuleList",inputparams,returnparams);//查询信息
    	Log.v("------main     -----MIDkey--", blockdata.toString() );
	    if (blockdata.size() == 0) {
	    	listSum = 1;	
		}
	    else {
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				xidStrings.add(blockdata.get(0).get(String.valueOf(i)));
				titleStrings.add(blockdata.get(1).get(String.valueOf(i)));
				mNameList.add(blockdata.get(2).get(String.valueOf(i)));
			}
		}
	    Log.v("----SearchBorrowA", blockdata.size() + "---");
	}

	class backButtonClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(SearchBorrowActivity.this,
					MainActivity.class);
			intent.putExtra("main", main);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(SearchBorrowActivity.this,
					MainActivity.class);
			intent.putExtra("main", main);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

}
