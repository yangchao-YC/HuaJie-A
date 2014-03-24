package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
import evebit.com.app.huajieoa.views.XListView.IXListViewListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class OficialManagementActivity extends Activity implements
		IXListViewListener {
	private String main = null;// 存储主页面模块ID
	private String MIDkey = null;
	private TextView bottomLeftTextView;
	private TextView bottomRighTextView;
	private Button backButton;
	private Button searchButton;
	private EditText searchEditText;
	private ImageView searchImageView;
	private ProgressBar progressBar;
	private ListView listView = null;
	private ProgressDialog progressDialog;
	private XListView mListView;
	private SimpleAdapter mAdapter;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private static int loadmore = 0;
	private int page = 0;
	private Boolean LoadMore = true;
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String, String>>();
	ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> mDataDetailed = new ArrayList<Map<String, Object>>();// 存放详细进度
	
	private ArrayList<String>xidStrings = new ArrayList<String>();//存储信息ID
	private ArrayList<String> titleStrings = new ArrayList<String>();//存储标题信息
	
	private TextView dialogteTextView = null;
	private String[] dialogString = { "合同管理", "合同执行进度", "我的投标信息" };
	UserData userData = null;
	// 弹出对话框内控件
	private ListView dialogListView = null;
	private TextView dialogleftTextView = null;
	private TextView dialogrightTextView = null;

	public int tag = 3;// 为3时代表刚进入此页面
	public String tagString=null;
	private String leftString = null;
	private String rightString = null;
	private String searchString = null;//搜索参数
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.oficial_management);
		PublicWay.activityList.add(this);
		main = (String) getIntent().getExtras().get("main");// 接收传递数据
		MIDkey = (String) getIntent().getExtras().get("MIDkey");// 接收传递数据
		tagString =(String) getIntent().getExtras().get("tag");
		tag = Integer.parseInt(tagString);
		searchButton = (Button) findViewById(R.id.OficialManagement_search);
		progressBar = (ProgressBar) findViewById(R.id.OficialManagement_progressBar);
		// listView = (ListView) findViewById(R.id.oficial_ListView);
		searchEditText = (EditText) findViewById(R.id.OficialManagement_search_EditText);
		searchImageView = (ImageView) findViewById(R.id.OficialManagement_imageButton_search);
		dialogteTextView = (TextView) findViewById(R.id.OficialManagement_Text_Dialog);
		backButton = (Button) findViewById(R.id.OficialManagement_back);
		bottomLeftTextView = (TextView) findViewById(R.id.OficialManagement_Text_bottom_left);
		bottomRighTextView = (TextView) findViewById(R.id.OficialManagement_Text_bottom_right);
		backButton.setOnClickListener(new backButtonClickListener());// 返回按钮
		searchButton.setOnClickListener(new searcButtonOnClick());// 搜索按钮
		searchImageView.setOnClickListener(new searcButtonOnClick());// 搜索图片
		dialogteTextView.setText(dialogString[0]);
		dialogteTextView.setOnClickListener(new DialogTextClickListener());// 下拉列表选项

		mListView = (XListView) findViewById(R.id.xListView);
		mListView.setPullLoadEnable(true);
		mHandler = new Handler();
		userData = new UserData(this);
		leftString = getResources().getString(R.string.oficial_text_bottom_letf);// 合同信息
		rightString = getResources().getString(R.string.oficial_text_bottom_rigth);// 招标信息
		
		
		progressDialog = ProgressDialog.show(OficialManagementActivity.this,"", getResources().getString(R.string.get_date), true, false);
		// 获取下方数据数目
		
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			searchString = "";
			thread();
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (tag == 0 || tag == 2) {
					Intent intent;
					intent = new Intent(OficialManagementActivity.this,
							ManagementDetailsActivity.class);
					intent.putExtra("main", main);
					if (tag == 0) {
						intent.putExtra("MIDkey", "011007");
						intent.putExtra("mName", dialogString[0]);
					}
					else {
						intent.putExtra("MIDkey", "011301");
						intent.putExtra("mName", dialogString[2]);
					}
					intent.putExtra("guid", xidStrings.get(arg2-1));
					intent.putExtra("title", titleStrings.get(arg2-1));
					
					startActivity(intent);
					finish();
				}
				else {
					String   titleString = blockdata.get(1).get(String.valueOf(arg2-1).toString());
					String   keyString = blockdata.get(6).get(String.valueOf(arg2-1).toString());
					String   nameString = blockdata.get(7).get(String.valueOf(arg2-1).toString());
					String   conuntString = blockdata.get(8).get(String.valueOf(arg2-1).toString());
					showDialogPreview(titleString,keyString,nameString,conuntString);
				}
				
			}
		});
	}

	class DialogTextClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(
					OficialManagementActivity.this);
			builder.setTitle(getResources().getString(R.string.Select));
			builder.setItems(dialogString,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							progressDialog = ProgressDialog.show(OficialManagementActivity.this,"", getResources().getString(R.string.get_date), true, false);
							LoadMore =true;
							xidStrings.clear();
							titleStrings.clear();
							tag = which;
							searchString = "";
							onRefresh();
						}
					});
			builder.create().show();
		}
	}

	private void thread() {
		
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
				switch (tag) {
				case 0:
					manageDate();
					handler.sendEmptyMessage(0);
					break;
				case 1:
					ScheduleDate();
					handler.sendEmptyMessage(0);
					break;
				case 2:
					infoDate();
					handler.sendEmptyMessage(0);
					break;
				case 3:// 刚进入此页面，所以需要调用sum方法获取下方数据总量
					manageDate();
					sum();
					StartHandler.sendEmptyMessage(0);
					break;
				default:
					break;
				}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			if (blockdata.size()>0) {
			switch (tag) {
			case 0:
				dialogteTextView.setText(dialogString[tag]);
				break;
			case 1:
				dialogteTextView.setText(dialogString[tag]);
				break;
			case 2:
				dialogteTextView.setText(dialogString[tag]);
				break;
			default:
				break;
			}
			mAdapter = new SimpleAdapter(OficialManagementActivity.this,
					mData, R.layout.oficial_listview, new String[] {
							"image_left", "title_text", "author_text",
							"name_text", "date_tetx", "image_right" },
					new int[] { R.id.oficial_image_left,
							R.id.oficial_text_title,
							R.id.oficial_text_Author,
							R.id.oficial_text_Name, R.id.oficial_text_Date,
							R.id.oficial_image_right });
			mListView.setAdapter(mAdapter);
			onLoad();
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.deviant, Toast.LENGTH_SHORT).show();
			}
		}

	};
	private Handler StartHandler = new Handler() {// 刚开始进入程序的Handler执行

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			geneItems();
			bottomLeftTextView.setText(leftString);// 设置下方数据数目
			bottomRighTextView.setText(rightString);// 设置下方数据数目
			tag = 0;
			
		}
	};

	private void sum() {
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		returnparams.add("HTZXS");
		returnparams.add("HTZS");
		returnparams.add("XMZXS");
		returnparams.add("XMZS");
		blockdata = soap.getSoapDataWithParam("XMZLTopInfo", inputparams,
				returnparams);// 查询信息
		leftString = leftString + blockdata.get(2).get("0");
		rightString = rightString + blockdata.get(3).get("0");
	}

	private void manageDate() {// 合同管理数据获取
		Log.e("------page--------", String.valueOf(page * 10));
		Log.e("------搜索信息------", "---"+searchString+ "---");
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(dialogString[0]);// 固定参数"合同管理"
		inputparams.add("10");
		inputparams.add(String.valueOf(page * 10));
		inputparams.add(searchString);
		returnparams.add("GUID");
		returnparams.add("XMMC");
		returnparams.add("XMJL");
		returnparams.add("QZRQ");
		
		if (LoadMore == true) {
			blockdata = soap.getSoapDataWithParam("XMZLDataList", inputparams,
					returnparams);// 查询信息
			if (blockdata.get(0).size()<10) {
				LoadMore  = false;
			}
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				xidStrings.add(blockdata.get(0).get(String.valueOf(i)));
				titleStrings.add(blockdata.get(1).get(String.valueOf(i)));
			}
			
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put("image_left", R.drawable.oficial_green);
				itemMap.put("title_text",blockdata.get(1).get(String.valueOf(i).toString()));
				itemMap.put("author_text",getResources().getString(R.string.oficial_Author));
				itemMap.put("name_text",blockdata.get(2).get(String.valueOf(i).toString()));
				itemMap.put("date_tetx",blockdata.get(3).get(String.valueOf(i).toString()));
				itemMap.put("image_right", R.drawable.l);
				mData.add(itemMap);
			}
		}
		Log.v("----OficialManagementA", blockdata.size() + "---");
	//	yyyy();
	}

	private void ScheduleDate() {// 合同进度数据获取
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(dialogString[1]);// 固定参数"合同执行进度"
		inputparams.add("10");
		inputparams.add(String.valueOf(page * 10));
		inputparams.add(searchString);
		returnparams.add("GUID");
		returnparams.add("XMMC");
		returnparams.add("XMBH");
		returnparams.add("XMJL");
		returnparams.add("ZXZT");
		returnparams.add("ZBBH");
		returnparams.add("ZBMC");
		returnparams.add("ZXJD");
		if (LoadMore == true) {
			blockdata = soap.getSoapDataWithParam("XMZLDataList", inputparams,
					returnparams);// 查询信息
			if (blockdata.get(0).size()<10) {
				LoadMore  = false;
			}
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				xidStrings.add(blockdata.get(0).get(String.valueOf(i)));
				titleStrings.add(blockdata.get(1).get(String.valueOf(i)));
			}
			
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put("image_left", R.drawable.oficial_green);
				itemMap.put("title_text",blockdata.get(1).get(String.valueOf(i).toString()));
				itemMap.put("author_text",getResources().getString(R.string.oficial_Author));
				itemMap.put("name_text",blockdata.get(3).get(String.valueOf(i).toString()));
			//	itemMap.put("date_tetx",blockdata.get(3).get(String.valueOf(i).toString()));
				itemMap.put("image_right", R.drawable.l);
				mData.add(itemMap);
			}
		}
	}

	private void infoDate() {// 投标信息数据获取
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(dialogString[2]);// 固定参数"我的投标信息"	
		inputparams.add("10");
		inputparams.add(String.valueOf(page * 10));
		inputparams.add(searchString);
		returnparams.add("GUID");
		returnparams.add("XMMC");
		returnparams.add("FZR");
		returnparams.add("KBSJ");
		if (LoadMore == true) {
			blockdata = soap.getSoapDataWithParam("XMZLDataList", inputparams,
					returnparams);// 查询信息
			if (blockdata.get(0).size()<10) {
				LoadMore  = false;
			}
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				xidStrings.add(blockdata.get(0).get(String.valueOf(i)));
				titleStrings.add(blockdata.get(1).get(String.valueOf(i)));
			}
			
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put("image_left", R.drawable.oficial_green);
				itemMap.put("title_text",blockdata.get(1).get(String.valueOf(i).toString()));
				itemMap.put("author_text",getResources().getString(R.string.oficial_Author));
				itemMap.put("name_text",blockdata.get(2).get(String.valueOf(i).toString()));
				itemMap.put("date_tetx",blockdata.get(3).get(String.valueOf(i).toString()));
				itemMap.put("image_right", R.drawable.l);   
				mData.add(itemMap);
			}
		}
	}
/*
	public void yyyy() {
		for (int i = 0; i < 10; i++) {
			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("image_left", R.drawable.oficial_green);
			itemMap.put("title_text",
					getResources().getString(R.string.oficial_Details_Symbol_)
							+ String.valueOf(i));
			itemMap.put("author_text",
					getResources().getString(R.string.oficial_Author));
			itemMap.put(
					"name_text",
					"");
			itemMap.put(
					"date_tetx",
					"");
			itemMap.put("image_right", R.drawable.oficial_right);
			mData.add(itemMap);
		}
	}
*/
	private void geneItems() {// 添加数据
		if (tag != 3) {
			Normal normal = new Normal(this);// 判断是否有网络连接
			if (normal.note_Intent()) {// 判断是否有网络连接
				thread();
			} else {
				Toast.makeText(getApplicationContext(), "请连接网络",
						Toast.LENGTH_SHORT).show();
			}
			
		}

		mAdapter = new SimpleAdapter(OficialManagementActivity.this, mData,
				R.layout.oficial_listview, new String[] { "image_left",
						"title_text", "author_text", "name_text", "date_tetx",
						"image_right" }, new int[] { R.id.oficial_image_left,
						R.id.oficial_text_title, R.id.oficial_text_Author,
						R.id.oficial_text_Name, R.id.oficial_text_Date,
						R.id.oficial_image_right });
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(OficialManagementActivity.this);
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh() {// 下拉刷新
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				LoadMore =true;
				page =0 ;
				mData.clear();
				Normal normal = new Normal(OficialManagementActivity.this);// 判断是否有网络连接
				if (normal.note_Intent()) {// 判断是否有网络连接
					thread();
				} else {
					Toast.makeText(getApplicationContext(), "请连接网络",
							Toast.LENGTH_SHORT).show();
				}
					
			}
		}, 2000);
	}
	
	@Override
	public void onLoadMore() {// 加载更多
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
					page++;
					Log.e("current page", page + "");
					geneItems();
					mAdapter.notifyDataSetChanged();
					onLoad();
			}
		}, 2000);
	}

	private void showDialogPreview(String titleString ,String  keyString, String  nameString, String conuntString) {
		//四参数为   1项目名称 2招标编号  3  招标名称  4  执行阶段   
		
		Builder builder = new AlertDialog.Builder(
				OficialManagementActivity.this);
		builder.setTitle(titleString);
		LayoutInflater inflater = LayoutInflater
				.from(OficialManagementActivity.this);
		View dialogView = inflater.inflate(R.layout.dialog_main, null);
		dialogListView = (ListView) dialogView
				.findViewById(R.id.dialog_ListView);
		dialogleftTextView = (TextView) dialogView
				.findViewById(R.id.dialog_text_left);
		dialogrightTextView = (TextView) dialogView
				.findViewById(R.id.dialog_text_right);

		dialogleftTextView.setText("招标编码");
		dialogrightTextView.setText("招标项目名称");
		
		
		ListViewDATE( keyString, nameString,conuntString);
		builder.setView(dialogView);
		builder.show();
	}

	private void ListViewDATE(String  keyString, String  nameString, String conuntString ) {
		mDataDetailed.clear();
		String[] strings = { keyString, nameString,conuntString };

		//for (int i = 0; i < 5; i++) {
			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("left", strings[0]);
			itemMap.put("right", strings[1]);
			itemMap.put("text", strings[2]);

			mDataDetailed.add(itemMap);
		//}
		SimpleAdapter adapter = new SimpleAdapter(this, mDataDetailed,
				R.layout.dialog_list, new String[] { "left", "right", "text" },
				new int[] { R.id.dialog_list_left, R.id.dialog_list_right,
						R.id.dialog_list_Text_main });

		dialogListView.setAdapter(adapter);
	}

	class searcButtonOnClick implements OnClickListener {//搜索按钮

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if (searchEditText.getText().toString().equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						OficialManagementActivity.this);
				builder.setTitle(getResources().getString(R.string.Prompt));
				builder.setMessage(getResources().getString(
						R.string.oficial_Prompt));
				builder.setPositiveButton(
						getResources().getString(R.string.Determine), null);
				builder.create().show();
			} else {
				/*
				Intent intent = new Intent(OficialManagementActivity.this,SearchBorrowActivity.class);
				intent.putExtra("main", main);
				intent.putExtra("MIDkey", MIDkey);
				intent.putExtra("search", searchEditText.getText().toString());
				startActivity(intent);
				finish();
				*/
				
				
				progressDialog = ProgressDialog.show(OficialManagementActivity.this,"", "正在搜索，请稍后", true, false);
				LoadMore =true;
				xidStrings.clear();
				titleStrings.clear();
				
				searchString = searchEditText.getText().toString();
				onRefresh();
				
			}
			
			
		}
	}

	class backButtonClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(OficialManagementActivity.this,
					MainActivity.class);
			intent.putExtra("main", main);

			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(OficialManagementActivity.this,
					MainActivity.class);

			intent.putExtra("main", main);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
