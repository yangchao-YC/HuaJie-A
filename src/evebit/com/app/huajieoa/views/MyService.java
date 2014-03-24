package evebit.com.app.huajieoa.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import evebit.com.app.huajieoa.models.GetSoapData;
import evebit.com.app.huajieoa.models.Normal;
import evebit.com.app.huajieoa.models.UserData;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	private Timer timer = new Timer();
	private TimerTask task;
	UserData userData;
    private	String [] MID = {"010100","010101","01040202","010408","01040600","011201"
			,"011007","011300","011301","011500","011501"};
	ArrayList<Hashtable<String, String>> blockdata =new ArrayList<Hashtable<String,String>>();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		userData =new UserData(this);
		//设置定时器
		task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		
		timer.schedule(task, 10000,10000);
	}

	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (userData.getRemind().toString().equals("1")) {//判断是否勾选消息推送
				int start = time(userData.getStartTime().toString());
				int end = time(userData.getEndTime().toString());
				if (start == 2 && end  == 1) {
					Normal normal = new Normal(MyService.this);// 判断是否有网络连接
					if (normal.note_Intent()) {// 判断是否有网络连接
						thread();//查询数据
					} 
					else {
						Log.v("-----", "张家朝打牛");
					}
				}
				
			}
			
		}
		
	};
	/**
	 * 执行查询推送信息多线程
	 */
	private void thread() {
		
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					date();
					handlerDate.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
		}.start();
	}
	/**
	 * 判断是否有信息   有的话执行推送方法
	 */
	Handler handlerDate = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (blockdata.size()>0) {
			for (int i = 0; i < blockdata.get(0).size(); i++) {
				addNotificaction(
						 i
						,blockdata.get(0).get(String.valueOf(i)).toString()
						,blockdata.get(2).get(String.valueOf(i)).toString()
						,blockdata.get(1).get(String.valueOf(i)).toString());
				}
			upThread();
			}
			
			
		}
		
	};
	
    private void upThread()
    {
    	new Thread()
    	{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					upDateZNDX();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
    		
    	}.start();
    }
	/**
	 * 调用upDateZNDX推送消息返回接口
	 */
    private void upDateZNDX() {
    	if (blockdata.size()>0) {
    	String guidSUM=blockdata.get(3).get("0").toString();
    	if (blockdata.get(1).size() > 1) {
    		for (int i = 1; i < blockdata.get(3).size(); i++) {
    			guidSUM = guidSUM + "," +blockdata.get(3).get(String.valueOf(i)).toString();
    		}
		}
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(guidSUM);
		ArrayList<Hashtable<String, String>> upDateZNDX = soap.getSoapDataWithParam("UpDateZNDX", inputparams,returnparams);
		Log.v("查看id是否传递", guidSUM);
		Log.v("测试推送是否传递", upDateZNDX.toString());
    	}
	}
    
	/**
	 * 查询推送信息
	 */
	private void date() {
		GetSoapData soap = new GetSoapData();
		ArrayList<String> inputparams = new ArrayList<String>();
		ArrayList<String> returnparams = new ArrayList<String>();
		inputparams.add(userData.getUserName().toString());
		returnparams.add("TITLE");
		returnparams.add("strGuid");
		returnparams.add("MID");
		returnparams.add("ID");
		blockdata = soap.getSoapDataWithParam("ZNDX",
				inputparams,returnparams);//查询信息
		Log.v("----MyServiceA", blockdata.size() + "---");
	}
	/**
	 * 比较时间
	 */
	public int time( String timeString) {
		
		Calendar calendar = Calendar.getInstance();//取得当前时间
		String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(calendar.get(Calendar.MINUTE));
		//Log.v("---HOUR_OF_DAY----",String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));	//取得当前小时
		//Log.v("---MINUTE----",String.valueOf(calendar.get(Calendar.MINUTE)));	//取得当前分钟
		String s1="2008-01-25 " + timeString +":10";  
		String s2="2008-01-25 " +hour+":"+minute+":10";  
		Log.v(s1, s2);
		java.text.DateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		java.util.Calendar c1=java.util.Calendar.getInstance();  
		java.util.Calendar c2=java.util.Calendar.getInstance();  
		try  
		{  
		c1.setTime(df.parse(s1));  
		c2.setTime(df.parse(s2));  
		}
		catch(java.text.ParseException e){  
		Log.v("-----------------", "格式不正确");  
		}  
		int result=c1.compareTo(c2);  
		if(result==0)  
		{
			Log.v("-----------------", "c1相等c2");  
			return 0;
		
		}
		else if(result>0)  
		{
			Log.v("-----------------", "c1小于c2");  
			return 1;
		}
		else  
		{
			Log.v("-----------------", "c1大于c2"); 
			return 2;	 
		}
	}
	
	/**
	 * 消息推送方法     参数为推送内容 
	 */
	private void addNotificaction(int PushID ,String conent,String MIDkey,String guid ) {
		NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "有新消息";
		
		//设置手机震动
		if (userData.getShake().toString().equals("1")) {//判断是否勾选震动
			notification.defaults = notification.DEFAULT_VIBRATE;
			long[]vib = {0,100,200,300};
			notification.vibrate = vib;
		}
		if (userData.getVoice().toString().equals("1")) {//判断是否勾选声音
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;//设置提示声音
		}		
		int push = 0 ;//手机处理判断辨别   值为0则不是手机可处理   为1 则是   做跳转动作
		for (int i = 0; i < MID.length; i++) {//判断是子模块ID是否是手机可处理
			if (MIDkey.equals(MID[i])) {
				push = 1;
			}
		}
		if (push == 0) {
			Intent intent = new  Intent();//(this,OficialManagementActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			
			notification.setLatestEventInfo(this, "内容提示", conent, pendingIntent);
			manager.notify(PushID+1,notification);
		}
		else {
			Intent intent = new  Intent(this,MessagePushDetailsActivity.class);
			intent.putExtra("main", "01");
			intent.putExtra("MIDkey", MIDkey);
			intent.putExtra("guid", guid);
			intent.putExtra("title",conent);
			intent.putExtra("PushID",String.valueOf(blockdata.get(0).size()));
			
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			notification.setLatestEventInfo(this, "内容提示", conent, pendingIntent);
			manager.notify(PushID+1,notification);
		
		}
		
		
	}
	
}
