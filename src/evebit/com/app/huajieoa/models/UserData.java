package evebit.com.app.huajieoa.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserData extends SQLiteOpenHelper{
	private static final String DATABASE_NAME="whuss.db";
	private static final String TABLE_NAME="user";
	private static final String TABLE_NAME_PASSWORD ="password";
	private static final String TABLE_NAME_USERTYPE ="usertype";
	private static final String TABLE_NAME_REMIND ="remind";//是否提醒
	private static final String TABLE_NAME_VOICE ="voice";//是否声音
	private static final String TABLE_NAME_SHAKE ="shake";//是否震动
	private static final String TABLE_NAME_STARTTIME ="starttime";//存放提醒时间  开始
	private static final String TABLE_NAME_ENDTIME ="endtime";//存放提醒时间    结束
	private static final int DATABASE_VERSION=1;
	
	public UserData(Context ctx) {		
		super(ctx,DATABASE_NAME,null,DATABASE_VERSION);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createSql = "CREATE TABLE ["+TABLE_NAME+"] ("
							+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
							+"[username] TEXT  NULL"
							+")";
		db.execSQL(createSql);
		createSql = 	   "CREATE TABLE ["+TABLE_NAME_PASSWORD+"] ("
							+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
							+"[password] TEXT  NULL"
							+")";
		db.execSQL(createSql);
		createSql = 	   "CREATE TABLE ["+TABLE_NAME_USERTYPE+"] ("
				+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
				+"[usertype] TEXT  NULL"
				+")";
		db.execSQL(createSql);
		
		
		createSql = 	   "CREATE TABLE ["+TABLE_NAME_REMIND+"] ("
				+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
				+"[remind] TEXT  NULL"
				+")";
		db.execSQL(createSql);
		createSql = 	   "CREATE TABLE ["+TABLE_NAME_VOICE+"] ("
				+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
				+"[voice] TEXT  NULL"
				+")";
		db.execSQL(createSql);
		createSql = 	   "CREATE TABLE ["+TABLE_NAME_SHAKE+"] ("
				+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
				+"[shake] TEXT  NULL"
				+")";
		db.execSQL(createSql);
		
		createSql = 	   "CREATE TABLE ["+TABLE_NAME_STARTTIME+"] ("
				+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
				+"[starttime] TEXT  NULL"
				+")";
		db.execSQL(createSql);
		createSql = 	   "CREATE TABLE ["+TABLE_NAME_ENDTIME+"] ("
				+"[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"							
				+"[endtime] TEXT  NULL"
				+")";
		db.execSQL(createSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("drop table if exists " + TABLE_NAME);
		//onCreate(db);
	}
	
	private static String[] FROM = {"id", "username"};		
	public String getUserName(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME,FROM,null,null, null, null, null);		
		String rstr = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}
	
	public void saveUserName(String value){
		String key = getUserName();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `"+TABLE_NAME+"` (`username`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `"+TABLE_NAME+"` set  username = '"+value+"'");
		}		
	}
	
	private static String[] FROMTEPIRES = {"id", "password"};		
	public String getPassword(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_PASSWORD,FROMTEPIRES,null,null, null, null, null);		
		String rstr   = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}	
	
	public void savePassword(String value){
		String key = getPassword();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `" + TABLE_NAME_PASSWORD + "` (`password`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `" + TABLE_NAME_PASSWORD + "` set  password = '"+value+"'");
		}		
	}
	
	private static String[] USERTYPE = {"id", "usertype"};		
	public String getUserType(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_USERTYPE,USERTYPE,null,null, null, null, null);		
		String rstr   = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}	
	
	public void saveUserType(String value){
		String key = getUserType();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `" + TABLE_NAME_USERTYPE + "` (`usertype`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `" + TABLE_NAME_USERTYPE + "` set  usertype = '"+value+"'");
		}		
	}
	
	private static String[] REMIND = {"id", "remind"};		
	public String getRemind(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_REMIND,REMIND,null,null, null, null, null);		
		String rstr   = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}	
	
	public void saveRemind(String value){
		String key = getRemind();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `" + TABLE_NAME_REMIND + "` (`remind`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `" + TABLE_NAME_REMIND + "` set  remind = '"+value+"'");
		}		
	}
	
	private static String[] VOICE = {"id", "voice"};		
	public String getVoice(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_VOICE,VOICE,null,null, null, null, null);		
		String rstr   = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}	
	
	public void saveVoice(String value){
		String key = getVoice();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `" + TABLE_NAME_VOICE + "` (`voice`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `" + TABLE_NAME_VOICE + "` set  voice = '"+value+"'");
		}		
	}
	
	private static String[] SHAKE = {"id", "shake"};		
	public String getShake(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_SHAKE,SHAKE,null,null, null, null, null);		
		String rstr   = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}	
	
	public void saveShake(String value){
		String key = getShake();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `" + TABLE_NAME_SHAKE + "` (`shake`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `" + TABLE_NAME_SHAKE + "` set  shake = '"+value+"'");
		}		
	}
	
	
	private static String[] STARTTIME = {"id", "starttime"};		
	public String getStartTime(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_STARTTIME,STARTTIME,null,null, null, null, null);		
		String rstr   = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}	
	
	public void saveStartTime(String value){
		String key = getStartTime();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `" + TABLE_NAME_STARTTIME + "` (`starttime`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `" + TABLE_NAME_STARTTIME + "` set  starttime = '"+value+"'");
		}		
	}
	
	private static String[] ENDTIME = {"id", "endtime"};		
	public String getEndTime(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_ENDTIME,ENDTIME,null,null, null, null, null);		
		String rstr   = "";
		while(cursor.moveToNext()){
			rstr = cursor.getString(1);			
		}	
		cursor.close();
		return rstr;		
	}	
	
	public void saveEndTime(String value){
		String key = getEndTime();
		SQLiteDatabase db = getReadableDatabase();
		if(key.equals("")){
			db.execSQL("INSERT INTO `" + TABLE_NAME_ENDTIME + "` (`endtime`) VALUES ('"+value+"')");
		}else{
			db.execSQL("update `" + TABLE_NAME_ENDTIME + "` set  endtime = '"+value+"'");
		}		
	}
}
