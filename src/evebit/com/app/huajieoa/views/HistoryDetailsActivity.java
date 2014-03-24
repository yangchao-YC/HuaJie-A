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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author YangChao 详细信息
 */
public class HistoryDetailsActivity extends Activity {
	
	private Button backButton;
	private Button auditButton;
	private LinearLayout linearLayout;
	private WebView mWebView;
	private TextView titleTextView;
	String path = "file:///android_asset/details.html";
	private Button downloadButton;
	private TextView tabbarTextView;
	
	
	private String main=null;
	private String MIDkey = null;
	private String guid=null;
	private String title=null;
	private String mName=null;
	
	UserData userData ;
	private ArrayList<String>xidStrings = new ArrayList<String>();//存储信息ID
	private ArrayList<String>keyNameArrayList = new ArrayList<String>();//存储收文参数
	private ArrayList<String>keyIDArrayList = new ArrayList<String>();//存储收文参数
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String,String>>();
	private ProgressDialog progressDialog;
	
	String [] key = {"010100","010101","01040202","010408","01040600","011201"
			,"011007","011300","011301","011500","011501"};
	String [] html={
			"details.html",
			"sendfile.html",
			"articles.html",
			"logistics.html",
			"vehicle.html",
			"seal.html",
			"approval.html",
			"recruit.html",
			"tender.html",
			"money.html",
			"pin.html"};
	int keyint=0;//判定是哪个页面的ID
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oficial_management_details);
		PublicWay.activityList.add(this);
		
		main =(String)getIntent().getExtras().get("main");
		MIDkey = (String)getIntent().getExtras().get("MIDkey");//接收子模块ID	
		
		guid =(String)getIntent().getExtras().getString("guid");//接收此条项目的ID
		title = (String)getIntent().getExtras().getString("title");//接收标题
		mName = (String)getIntent().getExtras().getString("MName");//接受子栏目名称
		
		for (int i = 0; i < key.length; i++) {
			if (MIDkey.equals(key[i])) {
				keyint=i;
			}
		}
		keyName(keyint);
		Log.v("guid+++++++++++++++++++++++++++guid", guid);
		
		downloadButton = (Button)findViewById(R.id.Oficial_Details_Download);
		backButton = (Button)findViewById(R.id.Oficial_Details_back);
		auditButton = (Button)findViewById(R.id.Oficial_Details_bottom_button);
		titleTextView = (TextView)findViewById(R.id.Oficial_Details_FileNumber);
		linearLayout = (LinearLayout)findViewById(R.id.Oficial_Details_bottom_linearLayout);
		mWebView = (WebView)findViewById(R.id.Oficial_Details_WebView);	
		tabbarTextView = (TextView)findViewById(R.id.Oficial_Details_tabbarText);
		mWebView.setBackgroundColor(Color.parseColor("#FFFFFF"));//设置背景，避免闪屏
		
		tabbarTextView.setText(mName);
		
		titleTextView.setText(title);//设置标题	
		
		/**
		 * 绑定按钮事件
		 */
		backButton.setOnClickListener(new backClickListener());
		auditButton.setOnClickListener(new auditClickListener());	
		downloadButton.setOnClickListener(new downloadClickListener());
		auditButton.setVisibility(View.INVISIBLE);//隐藏审核按钮
		linearLayout.setVisibility(View.GONE);
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
			String []details= {
					"SymbolText",
					"CreateTimeText",
					"IncomeTimeText",
					"CreateNameText",
					"UnitText",
					"FileNameText",
					"ThemeText",
					"PageNumberText",
					"SummaryText"
					};
			String []detailskey = {
					"{SymbolText}",
					"{CreateTimeText}",
					"{IncomeTimeText}",
					"{CreateNameText}",
					"{UnitText}",
					"{FileNameText}",
					"{ThemeText}",
					"{PageNumberText}",
					"{SummaryText}"
					};
			for (int j = 0; j < details.length; j++) {
				keyNameArrayList.add(details[j]);
				keyIDArrayList.add(detailskey[j]);
			}
			break;
		case 1:
			String []sendfile = {
					"STRDOCNUMBER",
					"strCreateUserName",
					"dtCreateDate",
					"strDocTitle",
					"strUnit",
					"strSubject",
					"STRCOLLATEUSER"
					};
			String []sendfilekey = {
					"{STRDOCNUMBER}",
					"{strCreateUserName}",
					"{dtCreateDate}",
					"{strDocTitle}",
					"{strUnit}",
					"{strSubject}",
					"{STRCOLLATEUSER}"
					};
			for (int j = 0; j < sendfile.length; j++) {
				keyNameArrayList.add(sendfile[j]);
				keyIDArrayList.add(sendfilekey[j]);
			}
			break;
		case 2:
			String []articles = {
					"strConsumableName",
					"nBorrowNum",
					"strBorrowMan",
					"dtBorrowTime",
					"dtReturnedTime",
					"strAuditMan",
					"dtAuditTime",
					"nAudit"
					};
			String []articleskey = {
					"{strConsumableName}",
					"{nBorrowNum}",
					"{strBorrowMan}",
					"{dtBorrowTime}",
					"{dtReturnedTime}",
					"{strAuditMan}",
					"{dtAuditTime}",
					"{nAudit}"
					};
			for (int j = 0; j < articles.length; j++) {
				keyNameArrayList.add(articles[j]);
				keyIDArrayList.add(articleskey[j]);
			}
			break;
		case 3:
			String []logistics = {
					"strContractName",
					"strCreateMan",
					"strSignedUnit",
					"dtSignedTime",
					"dtDueTime",
					"fContractMoney"
					};
			String []logisticskey= {
					"{strContractName}",
					"{strCreateMan}",
					"{strSignedUnit}",
					"{dtSignedTime}",
					"{dtDueTime}",
					"{fContractMoney}"
					};
			for (int j = 0; j < logistics.length; j++) {
				keyNameArrayList.add(logistics[j]);
				keyIDArrayList.add(logisticskey[j]);
			}
			break;
		case 4:
			String []vehicle = {
					"DTAPPLYDATE",
					"STRVOITURENO",
					"STRMOTORMAN",
					"STRAPPLYUSER",
					"DTSTARTDATE",
					"DTENDDATE",
					"STRDESTINATION",
					"STRREASON"
					};
			String []vehiclekey = {
					"{DTAPPLYDATE}",
					"{STRVOITURENO}",
					"{STRMOTORMAN}",
					"{STRAPPLYUSER}",
					"{DTSTARTDATE}",
					"{DTENDDATE}",
					"{STRDESTINATION}",
					"{STRREASON}"
					};
			for (int j = 0; j < vehicle.length; j++) {
				keyNameArrayList.add(vehicle[j]);
				keyIDArrayList.add(vehiclekey[j]);
			}
			break;
		case 5:
			String [] seal = {
					"strSignetID",
					"strProjectid",
					"strBorrower",
					"strUse",
					"dtBorrowTime",
					"dtReturnedDate",
					"strAuditMan",
					"nAudit",
					"dtAuditDate"
					};
			String [] sealkey = {
					"{strSignetID}",
					"{strProjectid}",
					"{strBorrower}",
					"{strUse}",
					"{dtBorrowTime}",
					"{dtReturnedDate}",
					"{strAuditMan}",
					"{nAudit}",
					"{dtAuditDate}"
					};
			for (int j = 0; j < seal.length; j++) {
				keyNameArrayList.add(seal[j]);
				keyIDArrayList.add(sealkey[j]);
			}
			break;
		case 6:
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
		case 7:
			String [] recruit = {
					"strZbxm",
					"strZbbh",
					"strXmbh",
					"strXmmc",
					"strzblb",
					"dtLxrq",
					"nbds",
					"strHtmc",
					"strJdmc",
					"strSfwc",
					"strJd"
					};
			String [] recruitkey = {
					"{strZbxm}",
					"{strZbbh}",
					"{strXmbh}",
					"{strXmmc}",
					"{strzblb}",
					"{dtLxrq}",
					"{nbds}",
					"{strHtmc}",
					"{strJdmc}",
					"{strSfwc}",
					"{strJd}"
					};
			for (int j = 0; j < recruit.length; j++) {
				keyNameArrayList.add(recruit[j]);
				keyIDArrayList.add(recruitkey[j]);
			}
			break;
		case 8:
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
		case 9:
			String [] money = {
					"BorrowPeople",
					"strType",
					"BorrowSum",
					"ConfirmSum",
					"Use",
					"Approved",
					"BorrowTime",
					"AlsoSum",
					"AlsoTime"
					};
			String [] moneykey = {
					"{BorrowPeople}",
					"{strType}",
					"{BorrowSum}",
					"{ConfirmSum}",
					"{Use}",
					"{Approved}",
					"{BorrowTime}",
					"{AlsoSum}",
					"{AlsoTime}"
					};
			for (int j = 0; j < money.length; j++) {
				keyNameArrayList.add(money[j]);
				keyIDArrayList.add(moneykey[j]);
			}
			break;
		case 10:
			String [] pin = {
					"strName",
					"strDept",
					"strRating",
					"strProjectName",
					"fYuanLingJine",
					"fHexiaoJine",
					"fYBLHTHK",
					"strVerifier",
					"strBaoxiaoRen",
					"dtbaoxiaoShijian"
					};
			String [] pinkey = {
					"{strName}",
					"{strDept}",
					"{strRating}",
					"{strProjectName}",
					"{fYuanLingJine}",
					"{fHexiaoJine}",
					"{fYBLHTHK}",
					"{strVerifier}",
					"{strBaoxiaoRen}",
					"{dtbaoxiaoShijian}"
					};
			for (int j = 0; j < pin.length; j++) {
				keyNameArrayList.add(pin[j]);
				keyIDArrayList.add(pinkey[j]);
			}
			break;
		default:
			break;
		}
	
	}
	private void thread() {
		progressDialog = ProgressDialog.show(HistoryDetailsActivity.this, "", getResources().getString(R.string.get_date), true, false);
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
			mName = blockdata.get(blockdata.size()-3).get("0").toString();
			
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
			 * 判断是否有附件，如果有显示下载附件按钮
			 * 以下参数名参照html文件
			 */
			if (blockdata.get(blockdata.size()-2).get("0").toString().equals("1")) {
				downloadButton.setVisibility(View.VISIBLE);
			}
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
		returnparams.add("MName");
		returnparams.add("isFJ");
		 blockdata = soap.getSoapDataWithParam("getMDataInfo",
				inputparams,returnparams);//查询信息
		 Log.v("----HistoryDetailsA", blockdata.size() + "---");
	}
	/**
	 * 审核按钮
	 * @author yangchao
	 *
	 */
	class auditClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			Intent intent = new Intent(HistoryDetailsActivity.this,AuditActivity.class); 
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid", guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
			intent.putExtra("opinion", "");
			intent.putExtra("key", "0");//判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
			startActivity(intent);
			finish();
		}	
	}
	
	/**
	 * 返回按钮
	 * @author yangchao
	 *
	 */
	class backClickListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(HistoryDetailsActivity.this,HistoryBorrowActivity.class); 
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("MName", mName);
			startActivity(intent);
			finish();
		}	
	}
	/**
	 * 下载附件按钮
	 * @author yangchao
	 *
	 */
	class downloadClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(HistoryDetailsActivity.this,DownloadBorrowActivity.class);
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid", guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
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
        	Intent intent = new Intent(HistoryDetailsActivity.this,HistoryBorrowActivity.class); 
        	intent.putExtra("main", main);
        	intent.putExtra("MIDkey", MIDkey);
        	intent.putExtra("MName", mName);
        	startActivity(intent);
			
			 finish();
        	return true;
        } else  
            return super.onKeyDown(keyCode, event);  
    }
	
}
