package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.jsoup.nodes.Node;

import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * @author YangChao
 *审核页面
 */
public class AuditEndActivity extends Activity implements android.view.View.OnClickListener{
	private Button backButton;
	private Button endButton;
	
	private RadioButton oneButton;
	private RadioButton twoButton;
	private RadioButton threeButton;
	private RadioButton fourButton;
	
	private TextView nameTextView;
	private TextView tabBarTextView;
	Context mContext;
	MyListAdapter adapter;
	List<Integer> listItemID = new ArrayList<Integer>();
	List<Person> persons ;
	
	private GridView gridView;
	private String main=null;
	private String MIDkey = null;
	private String guid=null;
	private String title=null;
	private String nodetype=null;//接收节点类型
	private String opinion=null;//接收审核意见
	private String checkBoxString=null;//存储当前选中的节点编号，用于查询相应的用户列表
	private int check = 0;//判断是否选中处理方法 0 为未选中
	
	private ProgressDialog progressDialog;
	private String mName=null;
	private String keyString =null;////判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
	UserData userData;
	
	private ArrayList<String> strGuid = new ArrayList<String>();// 存储节点ID
	private ArrayList<String> strNodeName = new ArrayList<String>();// 存储节点名称
	private ArrayList<String> strNodeKind = new ArrayList<String>();// 存储节点类型
	
	ArrayList<Hashtable<String, String>> nodedata =new ArrayList<Hashtable<String,String>>();
	ArrayList<Hashtable<String, String>> blockdata =new ArrayList<Hashtable<String,String>>();
	ArrayList<Hashtable<String, String>> auditdata = new ArrayList<Hashtable<String, String>>();
	private ArrayList<String> nameStrings = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.audit_end);
		PublicWay.activityList.add(this);
		main =(String)getIntent().getExtras().get("main");
		MIDkey = (String)getIntent().getExtras().get("MIDkey");//接收子模块ID
		guid =(String)getIntent().getExtras().getString("guid");//接收此条项目的ID
		title = (String)getIntent().getExtras().getString("title");
		//nodetype = (String)getIntent().getExtras().getString("nodetype");
		opinion = (String)getIntent().getExtras().getString("opinion");
		//checkBoxString = (String)getIntent().getExtras().getString("checkBoxString");
		
		mName = (String)getIntent().getExtras().getString("MName");//接受子栏目名称
		keyString = (String)getIntent().getExtras().getString("key");//判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
		userData = new UserData(this);
		gridView = (GridView) findViewById(R.id.AuditEND_GridView);
		backButton = (Button)findViewById(R.id.AuditEND_Button_back);
		endButton = (Button)findViewById(R.id.AuditEND_Button_End);
		nameTextView = (TextView)findViewById(R.id.AuditEND_Text_auditName);
		tabBarTextView = (TextView)findViewById(R.id.AuditEND_TextView_TabBar);
		
		oneButton = (RadioButton)findViewById(R.id.Audit_End_Radio_ONE);
		twoButton = (RadioButton)findViewById(R.id.Audit_End_Radio_TWO);
		threeButton = (RadioButton)findViewById(R.id.Audit_End_Radio_Three);
		fourButton = (RadioButton)findViewById(R.id.Audit_End_Radio_Four);
		
		oneButton.setOnClickListener(this);
		twoButton.setOnClickListener(this);
		threeButton.setOnClickListener(this);
		fourButton.setOnClickListener(this);
		
		tabBarTextView.setText(mName);
		nameTextView.setText(title);
		
		backButton.setOnClickListener(new backOnClick());
		endButton.setOnClickListener(new endOnClick());
		mContext = getApplicationContext();
		
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			thread();
		} else {
			Toast.makeText(getApplicationContext(), "请连接网络",
					Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	
	
	private void thread() {
		progressDialog = ProgressDialog.show(AuditEndActivity.this, "", getResources().getString(R.string.get_date), true, false);
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					nodeList();
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
			
		}.start();
	}
	
	
	private void threadUser() {
		progressDialog = ProgressDialog.show(AuditEndActivity.this, "", getResources().getString(R.string.get_date), true, false);
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					date();
					handlerCheCkBox.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
		}.start();
	}
	
	
	private void threadEND() {
		progressDialog = ProgressDialog.show(AuditEndActivity.this, "",
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
	
	private Handler handlerEnd = new Handler()
{	//结束处理事件
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		if (auditdata.size()>0) {

		if (auditdata.get(0).get("0").toString().equals("ok")) {
			String End=nameTextView.getText().toString() + getResources().getString(R.string.audit_End_TextEnd);
			Toast.makeText(getApplicationContext(),End , Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(AuditEndActivity.this,MainActivity.class);
			intent.putExtra("main", main);
			startActivity(intent);
			finish();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					AuditEndActivity.this);
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
	/**
	 * 设置单选框值
	 */
	private Handler handler = new Handler(){
		//设置人物选项
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			
			if (nodedata.size()>0) {
				for (int i = 0; i < nodedata.get(0).size(); i++) {
					strGuid.add(nodedata.get(0).get(String.valueOf(i)));
					strNodeName.add(nodedata.get(1).get(String.valueOf(i)));
					strNodeKind.add(nodedata.get(2).get(String.valueOf(i)));
				}

				switch (strGuid.size()) {
				case 1:
					oneButton.setVisibility(View.VISIBLE);
					oneButton.setText(strNodeName.get(0).toString());
					break;
				case 2:
					oneButton.setVisibility(View.VISIBLE);
					twoButton.setVisibility(View.VISIBLE);
					oneButton.setText(strNodeName.get(0).toString());
					twoButton.setText(strNodeName.get(1).toString());
					break;
				case 3:
					oneButton.setVisibility(View.VISIBLE);
					twoButton.setVisibility(View.VISIBLE);
					threeButton.setVisibility(View.VISIBLE);
					oneButton.setText(strNodeName.get(0).toString());
					twoButton.setText(strNodeName.get(1).toString());
					threeButton.setText(strNodeName.get(2).toString());
					break;
				case 4:
					oneButton.setVisibility(View.VISIBLE);
					twoButton.setVisibility(View.VISIBLE);
					threeButton.setVisibility(View.VISIBLE);
					fourButton.setVisibility(View.VISIBLE);
					oneButton.setText(strNodeName.get(0).toString());
					twoButton.setText(strNodeName.get(1).toString());
					threeButton.setText(strNodeName.get(2).toString());
					fourButton.setText(strNodeName.get(3).toString());
					break;
				default:
					break;
				}
				if (strGuid.size() == 1) {
					oneButton.setChecked(true);
					if (strNodeKind.get(0).equals("0002")) {
						Log.v("------", "------");
						nodetype = "0002";
						checkBoxString = strGuid.get(0);
						check = 3;
						threadEND();
					}
					else {
						Node(oneButton.getText().toString());
					}
					
				}
				else {
					//dialogF();//提示
				}
				
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.deviant,
						Toast.LENGTH_SHORT).show();
			}	
		}	
	};
	/**
	 * 设置checkBox值
	 */
	private Handler handlerCheCkBox = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			if (blockdata.size()>0) {
				
				adapter = new MyListAdapter(persons);
				gridView.setAdapter(adapter);
				
				if (blockdata.get(1).size() == 1 && strGuid.size() == 1) {//当只有一个流程节点并且只有一个人员的时候直接提交审核
					threadEND();
				}
				
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.deviant,
						Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
    //自定义ListView适配器
    class MyListAdapter extends BaseAdapter{
    	List<Boolean> mChecked;
    	List<Person> listPerson;
    	HashMap<Integer,View> map = new HashMap<Integer,View>(); 
    	
    	public MyListAdapter(List<Person> list){
    		listPerson = new ArrayList<Person>();
    		listPerson = list;
    		
    		mChecked = new ArrayList<Boolean>();
    		for(int i=0;i<list.size();i++){
    			mChecked.add(false);
    		}
    	}

		@Override
		public int getCount() {
			return listPerson.size();
		}

		@Override
		public Object getItem(int position) {
			return listPerson.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = null;
			
			if (map.get(position) == null) {
				Log.e("MainActivity","position1 = "+position);
				LayoutInflater mInflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = mInflater.inflate(R.layout.audit_gridview, null);
				holder = new ViewHolder();
				holder.selected = (CheckBox)view.findViewById(R.id.audit_GridView_CheckBox);
				holder.name = (TextView)view.findViewById(R.id.audit_GridView_Text);
				final int p = position;
				map.put(position, view);
				holder.selected.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox)v;
						mChecked.set(p, cb.isChecked());
					}
				});
				view.setTag(holder);
			}else{
				Log.e("MainActivity","position2 = "+position);
				view = map.get(position);
				holder = (ViewHolder)view.getTag();
			}	
			holder.selected.setChecked(mChecked.get(position));
			holder.name.setText(listPerson.get(position).getName());
			return view;
		}
    	
    }
    
    static class ViewHolder{
    	CheckBox selected;
    	TextView name;
    }
	private void date()
	{
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(checkBoxString);
		inputparams.add(guid);
		returnparams.add("STRGUID");//唯一ID
		returnparams.add("STRUSERNAME");//用户名称
		returnparams.add("STRUSERACCOUNT");//帐号
		returnparams.add("STRDEPARTCODE");	//部门ID
		returnparams.add("STRDEPARTNAME");//部门名称

		blockdata = soap.getSoapDataWithParam("GetNodeUserList",
				inputparams,returnparams);//查询信息

		Person mPerson;
    	for(int i=0;i<blockdata.get(0).size();i++){
    		mPerson = new Person();
    		mPerson.setName(blockdata.get(1).get(String.valueOf(i)));
    		persons.add(mPerson);
    		
    		nameStrings.add(blockdata.get(1).get(String.valueOf(i)));
    	}
    	
	}
	/**
	 * 查询节点
	 */
	private void nodeList() {
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(guid);
		inputparams.add(MIDkey);
		returnparams.add("strGuid");
		returnparams.add("strNodeName");
		returnparams.add("strNodeKind");

		nodedata = soap.getSoapDataWithParam("GetNextPushNodeList",
				inputparams, returnparams);// 查询信息
		
		 Log.v("----AuditEndA", nodedata.size() + "---nodedata");
	}
	
	public void endDateAudit() {
		String namaeString;
		if (check == 3) {
			namaeString = "";
			Log.v("---427---", "------");
		}
		
		else {

			if (blockdata.get(1).size() == 1 && strGuid.size() == 1) {
				namaeString = blockdata.get(1).get("0").toString();
			}
		else {
			namaeString=blockdata.get(1).get(String.valueOf(listItemID.get(0)))  ;
			for(int i=1;i<listItemID.size();i++){
					namaeString =namaeString +"|"+ blockdata.get(1).get(String.valueOf(listItemID.get(i)));
			}
		}
		}
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		inputparams.add(MIDkey);
		inputparams.add(guid);
		inputparams.add(opinion);
		inputparams.add("true");// 审核或者撤回
		inputparams.add(namaeString);// 选择执行人员
		inputparams.add(checkBoxString);
		inputparams.add(nodetype);// 节点类型 根据选择项判断
		returnparams.add("returntxt");
		auditdata = soap.getSoapDataWithParam("DateAudit", inputparams, returnparams);// 查询信息
		Log.v("----AuditEndA", auditdata.size() + "---auditdata");
	}
	
	class endOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//结束按钮
			if (check == 0) {
				//dialogF();
			}
		    else if (check ==3) 
		    {
		    	Normal normal = new Normal(AuditEndActivity.this);// 判断是否有网络连接
				if (normal.note_Intent()) {// 判断是否有网络连接
					threadEND();
				} else {
					Toast.makeText(getApplicationContext(), "请连接网络",
							Toast.LENGTH_SHORT).show();
				}
			}
			else {
			listItemID.clear();
			for(int i=0;i<adapter.mChecked.size();i++){
				if(adapter.mChecked.get(i)){
					listItemID.add(i);
				}
			}
			
			if(listItemID.size()==0){
				AlertDialog.Builder builder1 = new AlertDialog.Builder(AuditEndActivity.this);
				builder1.setMessage("没有选择执行人员");
				builder1.show();
			}else{
				/*
				StringBuilder sb = new StringBuilder();
				
				for(int i=0;i<listItemID.size();i++){
					sb.append("ItemID="+blockdata.get(1).get(String.valueOf(listItemID.get(i)))+" . ");
				}
				AlertDialog.Builder builder2 = new AlertDialog.Builder(AuditEndActivity.this);
				builder2.setMessage(sb.toString());
				builder2.show();
				*/
				
				Normal normal = new Normal(AuditEndActivity.this);// 判断是否有网络连接
				if (normal.note_Intent()) {// 判断是否有网络连接
					threadEND();
				} else {
					Toast.makeText(getApplicationContext(), "请连接网络",
							Toast.LENGTH_SHORT).show();
				}
				}
				
			}
		
		}
		
	}
	class backOnClick implements OnClickListener{
		//返回按钮
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(AuditEndActivity.this,AuditActivity.class);
			intent.putExtra("main", main);
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid", guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
			intent.putExtra("opinion", opinion);
			intent.putExtra("key", keyString);//判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
			startActivity(intent);
			finish();
		}	
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
        	Intent intent = new Intent(AuditEndActivity.this,
        			AuditActivity.class);
        	intent.putExtra("main", main);
        	intent.putExtra("MIDkey", MIDkey);
        	intent.putExtra("guid", guid);
			intent.putExtra("title", title);
			intent.putExtra("MName", mName);
			intent.putExtra("opinion", opinion);
			intent.putExtra("key", keyString);//判定是否是从项目总览内跳转的投标管理与合同管理，0为不是
			startActivity(intent);
			finish();
        	return true;
        } else  
            return super.onKeyDown(keyCode, event);  
    }
	
	
	



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Audit_End_Radio_ONE:
			threeButton.setChecked(false);
			fourButton.setChecked(false);
			Node(oneButton.getText().toString());
			
			break;
		case R.id.Audit_End_Radio_TWO:
			threeButton.setChecked(false);
			fourButton.setChecked(false);
			Node(twoButton.getText().toString());
			
			break;
		case R.id.Audit_End_Radio_Three:
			oneButton.setChecked(false);
			twoButton.setChecked(false);
			Node(threeButton.getText().toString());
			break;
		case R.id.Audit_End_Radio_Four:
			oneButton.setChecked(false);
			twoButton.setChecked(false);
			Node(fourButton.getText().toString());
			break;
		default:
			break;
		}
	}
	/**
	 * 找到节点的ID  类型
	 */
	private void Node(String checkName) {
		for (int i = 0; i < nodedata.get(0).size(); i++) {
			if (checkName.equals(strNodeName.get(i))) {
				nodetype = strNodeKind.get(i);// 找到节点类型
				checkBoxString = strGuid.get(i);//找到节点ID
			}
		}
		check = 1;
		persons = new ArrayList<Person>();//初始化checkbox值
		if (nodetype.equals("0002")) {
			check = 3;
			gridView.setVisibility(View.INVISIBLE);
		}
		else {
			gridView.setVisibility(View.VISIBLE);
			threadUser();//查询人员列表
		}
		
	}
	/**
	 * 提示：选择执行方案
	 */
	private void dialogF() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(AuditEndActivity.this);
		builder1.setMessage("请选择执行方案");
		builder1.show();
	}
}
