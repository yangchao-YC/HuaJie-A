package evebit.com.app.huajieoa.views;
import java.util.ArrayList;
import java.util.Hashtable;


import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author YangChao 详细信息
 */
public class ManagementDetailsActivity extends Activity {
	
	private Button backButton;
	private Button auditButton;
	private WebView mWebView;
	private TextView titleTextView;
	String path = "file:///android_asset/details.html";
	private TextView tabbarTextView;
	
	private String main=null;
	private String MIDkey = null;
	private String guid=null;
	UserData userData ;
	private String title=null;
	private String mName = null;
	private ArrayList<String>xidStrings = new ArrayList<String>();//存储信息ID
	private ArrayList<String>keyNameArrayList = new ArrayList<String>();//存储收文参数
	private ArrayList<String>keyIDArrayList = new ArrayList<String>();//存储收文参数
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String,String>>();
	private ProgressDialog progressDialog;
	
	String [] key = {"011007","011301"};
	String [] html={
			"approval.html",
			"tender.html",};
	int keyint=0;//判定是哪个页面的ID
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.management_details);
		PublicWay.activityList.add(this);
		main =(String)getIntent().getExtras().get("main");
		MIDkey = (String)getIntent().getExtras().get("MIDkey");//接收子模块ID	
		guid =(String)getIntent().getExtras().getString("guid");//接收此条项目的ID
		title = (String)getIntent().getExtras().getString("title");//接收标题
		mName = (String)getIntent().getExtras().getString("mName");
		
		for (int i = 0; i < key.length; i++) {
			if (MIDkey.equals(key[i])) {
				keyint=i;
			}
		}
		keyName(keyint);
		Log.v("guid+++++++++++++++++++++++++++guid", guid);
		
		backButton = (Button)findViewById(R.id.Management_Details_back);
		auditButton = (Button)findViewById(R.id.Management_Details_bottom_button);
		titleTextView = (TextView)findViewById(R.id.Management_Details_FileNumber);
		tabbarTextView = (TextView)findViewById(R.id.Management_Details_tabbarText);
		mWebView = (WebView)findViewById(R.id.Management_Details_WebView);		
		mWebView.setBackgroundColor(Color.parseColor("#FFFFFF"));//设置背景，避免闪屏
		titleTextView.setText(title);//设置标题	
		tabbarTextView.setText(mName);
		backButton.setOnClickListener(new backClickListener());
		
		auditButton.setVisibility(View.INVISIBLE);//屏蔽审核按钮
		
		
		auditButton.setOnClickListener(new auditClickListener());	
		userData =new UserData(this);
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			thread();
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
	}
	public void keyName(int i) {
		
		switch (i) {
		case 0:
			String [] approval = {
					"strTzf",
					"strCreateMan",
					"nLsh",
					"dtOperDate",
					"strZbbm",
					"strDd",
					"fYjsr",
					"strHtbh",
					"strXmjj",
					"strYzdw",
					"strXmlx",
					"strXmlb"
					};
			String [] approvalkey = {
					"{strTzf}",
					"{strCreateMan}",
					"{nLsh}",
					"{dtOperDate}",
					"{strZbbm}",
					"{strDd}",
					"{fYjsr}",
					"{strHtbh}",
					"{strXmjj}",
					"{strYzdw}",
					"{strXmlx}",
					"{strXmlb}"
					};
			for (int j = 0; j < approval.length; j++) {
				keyNameArrayList.add(approval[j]);
				keyIDArrayList.add(approvalkey[j]);
			}
			break;
		case 1:
			String [] tender = {
					"strXmmc",
					"strXmlx",
					"strZbbm",
					"strHtbh",
					"strFzr",
					"strShr",
					"fYjsr",
					"fCbxj",
					"fYgm",
					"nLsh"
					};
			String [] tenderkey = {
					"{strXmmc}",
					"{strXmlx}",
					"{strZbbm}",
					"{strHtbh}",
					"{strFzr}",
					"{strShr}",
					"{fYjsr}",
					"{fCbxj}",
					"{fYgm}",
					"{nLsh}"
					};
			for (int j = 0; j < tender.length; j++) {
				keyNameArrayList.add(tender[j]);
				keyIDArrayList.add(tenderkey[j]);
			}
			break;
		default:
			break;
		}
	
	}
	private void thread() {
		progressDialog = ProgressDialog.show(ManagementDetailsActivity.this, "", getResources().getString(R.string.get_date), true, false);
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
			ArrayList<String> txtStrings = new ArrayList<String>();
			for (int i = 0; i < blockdata.size()-1; i++) {
				if (blockdata.get(i).toString().equals("{0=}")) {
					txtStrings.add("无");
				}
				else {
					txtStrings.add(blockdata.get(i).get("0"));
				}
				
			}
			/**
			 * 以下参数名参照html文件
			 */
			initWeb(keyIDArrayList,txtStrings, html[keyint]);
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.deviant, Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	private void  date() {
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(MIDkey);
		inputparams.add(guid);
		
		for (int i = 0; i < keyNameArrayList.size(); i++) {
			returnparams.add(keyNameArrayList.get(i).toString());
		}
		
		 blockdata = soap.getSoapDataWithParam("getMDataInfo",
				inputparams,returnparams);//查询信息
		 Log.v("----ManagementDetailsA", blockdata.size() + "---");
	}
	
	class auditClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(ManagementDetailsActivity.this,AuditActivity.class); 
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid", guid);
			intent.putExtra("title", title);
			intent.putExtra("opinion", "");
			startActivity(intent);
			finish();
		}	
	}
	class backClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent(ManagementDetailsActivity.this,OficialManagementActivity.class); 
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			if (MIDkey.equals("011301")) {
				intent.putExtra("tag", "2");
			}
			else {
        		intent.putExtra("tag", "3");
			}
			startActivity(intent);
			finish();
		}	
	}
	
	private void initWeb(ArrayList<String> keyStrings,ArrayList<String> txtStrings,String html) {
	
		Normal normal = new Normal(this);
		String summary = normal.getFromAssets(html);
		for (int i = 0; i < keyStrings.size(); i++) {
			summary = summary.replace(keyStrings.get(i).toString(), txtStrings.get(i).toString());
		}
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8"); 
		//mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.loadDataWithBaseURL("file:///android_asset/",summary, "text/html", "UTF-8", "about:blank");
	
	}
	
	class whttpChromeclient extends WebChromeClient {

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			return super.onJsAlert(view, url, message, result);
		}
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
        	
        	Intent intent = new Intent(ManagementDetailsActivity.this,OficialManagementActivity.class); 
        	intent.putExtra("main", main);
        	intent.putExtra("MIDkey", MIDkey);
        	if (MIDkey.equals("011301")) {
        		intent.putExtra("tag", "2");
			}
        	else {
        		intent.putExtra("tag", "3");
			}
        	startActivity(intent);
			 finish();
        	return true;
        } else  
            return super.onKeyDown(keyCode, event);  
    }
	
}
