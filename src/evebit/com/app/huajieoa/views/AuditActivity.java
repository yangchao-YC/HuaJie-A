package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.Hashtable;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
//import evebit.com.app.huajieoa.views.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author YangChao 审核页面
 */
public class AuditActivity extends Activity {

	private Button backButton;
	//private Button nextButton;
	private TextView auditName;
	//private CheckBox oneOneCheckBox;
	//private CheckBox oneTwoCheckBox;
	//private CheckBox withdrawCheckBox;
	//private CheckBox twoTwoCheckBox;
	//private CheckBox twoOneCheckBox;
	private TextView tabbarTextView;
	private EditText opinionEditText;
	private Button okButton;
	private Button noButton;
	
	
	UserData userData;
	private String main = null;
	private String MIDkey = null;
	private String guid = null;
	private String title = null;
	private String mName=null;
	private String keyString =null;////判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
	private ProgressDialog progressDialog;
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String, String>>();
	ArrayList<Hashtable<String, String>> auditdata = new ArrayList<Hashtable<String, String>>();
	private ArrayList<String> strGuid = new ArrayList<String>();// 存储节点ID
	private ArrayList<String> strNodeName = new ArrayList<String>();// 存储节点名称
	private ArrayList<String> strNodeKind = new ArrayList<String>();// 存储节点类型
	String checkBoxString = null;// 存储按钮ID
	String nodetype = null;// 存储节点类型
	String opinion = null;//存储审核意见
	String newnodeID=null;
	private int end = 0;//0代表同意  1代表不同意
	//int inten = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.audit);
		PublicWay.activityList.add(this);
		main = (String) getIntent().getExtras().get("main");
		MIDkey = (String) getIntent().getExtras().get("MIDkey");// 接收子模块ID
		guid = (String) getIntent().getExtras().getString("guid");// 接收此条项目ID
		title = (String) getIntent().getExtras().getString("title");
		mName = (String)getIntent().getExtras().getString("MName");//接受子栏目名称
		opinion = (String)getIntent().getExtras().getString("opinion");//接收审核意见
		keyString = (String)getIntent().getExtras().getString("key");//判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
		userData = new UserData(this);
		backButton = (Button) findViewById(R.id.Audit_back);
		
		okButton = (Button)findViewById(R.id.Audit_Button_OK);
		noButton = (Button)findViewById(R.id.Audit_Button_NO);
		
		//nextButton = (Button) findViewById(R.id.Audit_Next);
		auditName = (TextView) findViewById(R.id.Audit_Text_auditName);
		tabbarTextView = (TextView)findViewById(R.id.Audit_TextView_TabBar);
		//oneOneCheckBox = (CheckBox) findViewById(R.id.Audit_ChekBox_One_One);
		//oneTwoCheckBox = (CheckBox) findViewById(R.id.Audit_ChekBox_One_two);
		//withdrawCheckBox = (CheckBox) findViewById(R.id.Audit_ChekBox_Withdraw);
		//twoTwoCheckBox = (CheckBox) findViewById(R.id.Audit_ChekBox_Two_Two);
		opinionEditText = (EditText) findViewById(R.id.Audit_EditText_Opinion);

		tabbarTextView.setText(mName);
		opinionEditText.setText(opinion);
		auditName.setText(title);
		// 01040202 用品 010408 后勤 011200
	//	if (MIDkey.equals("01040202") || MIDkey.equals("010408")
	//			|| MIDkey.equals("011200")) {
	//		withdrawCheckBox.setText("不同意");
	//	}
		backButton.setOnClickListener(new backOnClick());
		okButton.setOnClickListener(new OkOnClick());
		noButton.setOnClickListener(new NoOnClick());
		
		//nextButton.setOnClickListener(new nextOnClick());
		
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
		//	thread();// 获取数据
		} 
		else {
	//		inten = 0;
	//		oneOneCheckBox.setVisibility(View.INVISIBLE);
	//		oneTwoCheckBox.setVisibility(View.INVISIBLE);
	//		twoTwoCheckBox.setVisibility(View.INVISIBLE);
	//		withdrawCheckBox.setVisibility(View.INVISIBLE);
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
		
/*
		oneOneCheckBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							if (MIDkey.equals("01040202") || MIDkey.equals( "010408" ) || MIDkey .equals("011200")) {
								nextButton.setText("结束");
							}
							else {
								if (inten == 1) 
								{
								for (int i = 0; i < blockdata.get(0).size(); i++) 
								{
									if (oneOneCheckBox.getText().equals(strNodeName.get(i))) 
									{
										if (strNodeKind.get(i).equals("0002")) 
										{
											nextButton.setText("结束");
											break;
										} 
										else 
										{
											nextButton.setText("下一步");
										}
									}
								}
								}
							}
								
						}
					}
				});
		oneTwoCheckBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							if (MIDkey.equals("01040202") || MIDkey.equals( "010408" ) || MIDkey .equals("011200")) {
								nextButton.setText("结束");
							}
							else {
								if (inten == 1) 
								{
								for (int i = 0; i < blockdata.get(0).size(); i++) 
								{
									if (oneTwoCheckBox.getText().equals(strNodeName.get(i))) 
									{
										if (strNodeKind.get(i).equals("0002")) 
										{
											nextButton.setText("结束");
											break;
										} 
										else 
										{
											nextButton.setText("下一步");
										}
									}
								}}
							}
						}
					}
				});
		withdrawCheckBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {		
								nextButton.setText("结束");
						}
						else {
							nextButton.setText("下一步");
						}
					}
				});
		twoTwoCheckBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							if (MIDkey.equals("01040202") || MIDkey.equals( "010408" ) || MIDkey .equals("011201")) {
								nextButton.setText("结束");
							}
							else {
								if (inten == 1) 
								{
								for (int i = 0; i < blockdata.get(0).size(); i++) 
								{
									if (twoTwoCheckBox.getText().equals(strNodeName.get(i))) 
									{
										if (strNodeKind.get(i).equals("0002")) 
										{
											nextButton.setText("结束");
											break;
										} 
										else 
										{
											nextButton.setText("下一步");
										}
									}			
								}}
							}
						}
					}
				});
*/
	}
/*
	private void thread() {
		progressDialog = ProgressDialog.show(AuditActivity.this, "",
				getResources().getString(R.string.get_date), true, false);
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				date();
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
*/
	/**
	 * 不同意按钮
	 * @author yangchao
	 *
	 */
	class NoOnClick implements OnClickListener
	{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		end = 1;
		Normal normal = new Normal(AuditActivity.this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			
			if (opinionEditText.getText().toString().equals("")) {
				opinion = " ";
			}
			else {
				opinion = opinionEditText.getText().toString();
			}
			threadEND();
		} 
		else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
	}	
	}
	
	/**
	 * 同意按钮
	 */
	class OkOnClick implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (MIDkey.equals("01040202") || MIDkey.equals( "010408" ) || MIDkey .equals("011201")) {
				threadEND();
			}
			else {
				if (opinionEditText.getText().toString().equals("")) {
					opinion = " ";
				}
				else {
					opinion = opinionEditText.getText().toString();
				}
				
				Intent intent = new Intent(AuditActivity.this,
						AuditEndActivity.class);
				intent.putExtra("main", main);
				intent.putExtra("MIDkey", MIDkey);//传递子模块ID
				intent.putExtra("guid", guid);//传递信息ID
				intent.putExtra("title", title);//传递标题
				intent.putExtra("opinion", opinion);//传递审核意见
				intent.putExtra("key", keyString);//判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
				startActivity(intent);
				finish();
			}
			
		}
		
	}
	private void threadEND() {
		progressDialog = ProgressDialog.show(AuditActivity.this, "",
				getResources().getString(R.string.get_date), true, false);
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					endDateAudit();
					handlerEnd.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}.start();
	}
/*
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			for (int i = 0; i < blockdata.get(0).size(); i++) {
				strGuid.add(blockdata.get(0).get(String.valueOf(i)));
				strNodeName.add(blockdata.get(1).get(String.valueOf(i)));
				strNodeKind.add(blockdata.get(2).get(String.valueOf(i)));
			}

			switch (strGuid.size()) {
			case 0:
				oneOneCheckBox.setVisibility(View.INVISIBLE);
				oneTwoCheckBox.setVisibility(View.INVISIBLE);
				twoTwoCheckBox.setVisibility(View.INVISIBLE);
				break;
			case 1:
				oneTwoCheckBox.setVisibility(View.INVISIBLE);
				twoTwoCheckBox.setVisibility(View.INVISIBLE);
				oneOneCheckBox.setText(strNodeName.get(0).toString());
				break;
			case 2:
				twoTwoCheckBox.setVisibility(View.INVISIBLE);
				oneOneCheckBox.setText(strNodeName.get(0).toString());
				oneTwoCheckBox.setText(strNodeName.get(1).toString());
				break;
			case 3:
				oneOneCheckBox.setText(strNodeName.get(0).toString());
				oneTwoCheckBox.setText(strNodeName.get(1).toString());
				twoTwoCheckBox.setText(strNodeName.get(2).toString());
				break;
			default:
				break;
			}
			progressDialog.dismiss();
		}
	};
*/

	
	
	private Handler handlerEnd = new Handler()
{

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		if (auditdata.size()>0) {
		if (auditdata.get(0).get("0").toString().equals("ok")) {
			String End = auditName.getText().toString()
					+ getResources().getString(R.string.audit_End_TextEnd);
			Toast.makeText(getApplicationContext(), End, Toast.LENGTH_SHORT)
					.show();
			Intent intent = new Intent(AuditActivity.this,
					MainActivity.class);
			intent.putExtra("main", main);
			startActivity(intent);
			finish();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					AuditActivity.this);
			builder.setTitle("警告");
			builder.setMessage(auditdata.get(0).get("0").toString());
			builder.setPositiveButton(
					getResources().getString(R.string.Determine), null);
			builder.create().show();

		}
		}
		else {
			Toast.makeText(getApplicationContext(), R.string.deviant,
					Toast.LENGTH_SHORT).show();
		}
	}
	
};
/*
	private void date() {
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(guid);
		inputparams.add(MIDkey);
		returnparams.add("strGuid");
		returnparams.add("strNodeName");
		returnparams.add("strNodeKind");

		blockdata = soap.getSoapDataWithParam("GetNextPushNodeList",
				inputparams, returnparams);// 查询信息
	}
*/
	// 结束审核
	public void endDateAudit() {
		/*
		String boollString = null;
		if (checkBoxString.equals("撤回") || checkBoxString.equals("不同意")) {
			boollString = "false";
		} else {
			boollString = "true";
		}
		*/
		String endString;
		if (end ==1) {
			endString = "false";
		}
		else {
			endString = "true";
		}
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(MIDkey);
		inputparams.add(guid);
		inputparams.add(opinion);
		inputparams.add(endString);
		inputparams.add(userData.getUserName().toString());// 选择执行人员
		inputparams.add("");//节点ID
		inputparams.add("0002");// 节点类型 根据选择项判断
		returnparams.add("returntxt");

		auditdata = soap.getSoapDataWithParam("DateAudit", inputparams, returnparams);// 查询信息
		
		
		Log.v("--auditA--", auditdata.size() + "");
	}
/*
	class nextOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int count = 0;
			if (oneOneCheckBox.isChecked()) {
				count++;
			}
			if (withdrawCheckBox.isChecked()) {
				count++;
			}
			if (oneTwoCheckBox.isChecked()) {
				count++;
			}
			if (twoTwoCheckBox.isChecked()) {
				count++;
			}

			if (nextButton.getText().equals("结束")) {
				if (count == 0 || count > 1) {
					nextButton.setText("下一步");
					AlertDialog.Builder builder = new AlertDialog.Builder(
							AuditActivity.this);
					builder.setTitle(getResources().getString(R.string.Warning));
					builder.setMessage(getResources().getString(
							R.string.audit_Dialog_Title));
					builder.setPositiveButton(
							getResources().getString(R.string.Determine), null);
					builder.create().show();
				} else {
					checkBoxName();//判断当前选择是那个cheCkBox 存储相应的值  并且获取审核意见
					
					Normal normal = new Normal(AuditActivity.this);// 判断是否有网络连接
					if (normal.note_Intent()) {// 判断是否有网络连接
						threadEND();
					} else {
						Toast.makeText(getApplicationContext(), "请连接网络",
								Toast.LENGTH_SHORT).show();
					}
					
				}
			} else {
				if (count == 0 || count > 1) {
					nextButton.setText("下一步");
					AlertDialog.Builder builder = new AlertDialog.Builder(
							AuditActivity.this);
					builder.setTitle(getResources().getString(R.string.Warning));
					builder.setMessage(getResources().getString(
							R.string.audit_Dialog_Title));
					builder.setPositiveButton(
							getResources().getString(R.string.Determine), null);
					builder.create().show();
				} else {
					
					checkBoxName();//判断当前选择是那个cheCkBox  存储相应的值  并且获取审核意见
					
					Intent intent = new Intent(AuditActivity.this,
							AuditEndActivity.class);
					intent.putExtra("main", main);
					intent.putExtra("MIDkey", MIDkey);//传递子模块ID
					intent.putExtra("guid", guid);//传递信息ID
					intent.putExtra("title", title);//传递标题
					intent.putExtra("nodetype", nodetype);//传递节点类型
					intent.putExtra("opinion", opinion);//传递审核意见
					intent.putExtra("checkBoxString",newnodeID );//传递按钮ID
					intent.putExtra("MName", mName);//传递模块名称
					intent.putExtra("key", keyString);//判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
					startActivity(intent);
					finish();
				}
			}
		}
	}
	*/
/*
	private void checkBoxName()//判断当前选择是那个cheCkBox  存储相应的值  并且获取审核意见
{
	
	if (withdrawCheckBox.isChecked()) {//当选择撤销的时候
		checkBoxString = withdrawCheckBox.getText().toString();
		newnodeID = "";
		nodetype = "0002";
	}
	else {
		if (oneTwoCheckBox.isChecked()) {
			checkBoxString = oneTwoCheckBox.getText().toString();
			newnode( oneTwoCheckBox.getText().toString());
		}
		if (twoTwoCheckBox.isChecked()) {
			checkBoxString = twoTwoCheckBox.getText().toString();
			newnode( twoTwoCheckBox.getText().toString());
		}
		if (oneOneCheckBox.isChecked()) {		
			checkBoxString = oneOneCheckBox.getText().toString();
			newnode( oneOneCheckBox.getText().toString());
		}
	}
	if (opinionEditText.getText().toString().equals("")) {
		opinion = " ";
	}
	else {
		opinion = opinionEditText.getText().toString();
	}
}
*/
	/*
private void newnode(String checkName) {//查询节点ID
	
	for (int i = 0; i < blockdata.get(0).size(); i++) {
		if (checkName.equals(strNodeName.get(i))) {
			nodetype = strNodeKind.get(i);// 找到节点类型
			newnodeID = strGuid.get(i);//找到节点ID
		}
	}
}*/
	/**
	 * 返回按钮
	 * @author yangchao
	 *
	 */
	class backOnClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent;
			if (keyString.equals("1")) {
				intent	 = new Intent(AuditActivity.this,
						ManagementDetailsActivity.class);
			}
			else {
				 intent = new Intent(AuditActivity.this,
						OficialManagementDetailsActivity.class);
			}
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid", guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent;
			if (keyString.equals("1")) {
				intent	 = new Intent(AuditActivity.this,
						ManagementDetailsActivity.class);
			}
			else {
				 intent = new Intent(AuditActivity.this,
						OficialManagementDetailsActivity.class);
			}
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid", guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
