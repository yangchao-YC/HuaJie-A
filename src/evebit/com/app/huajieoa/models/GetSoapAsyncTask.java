package evebit.com.app.huajieoa.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class GetSoapAsyncTask extends AsyncTask<String, Void, ArrayList<Hashtable<String, String>>> {

	ArrayList<String> inputparams= new ArrayList<String>();
	ArrayList<String> returnparams = new ArrayList<String>();
	private ProgressDialog progressDialog;
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String, String>>();
	public GetSoapAsyncTask(ArrayList<String> inputparams,ArrayList<String> returnparams) {
		// TODO Auto-generated constructor stub
		this.inputparams = inputparams;
		this.returnparams = returnparams;
	}
	public ArrayList<Hashtable<String, String>> Data()
	{
		return blockdata;
		
	}
	@Override
	protected ArrayList<Hashtable<String, String>> doInBackground(String... arg0) {//参数对应继承类第一个参数类型，返回值对应第三参数类型
		// TODO Auto-generated method stub
		GetSoapData soap = new GetSoapData();
		String string=arg0[0];
		blockdata = soap.getSoapDataWithParam(string, inputparams, returnparams);
		return null;
	}
	
	@Override
	protected void onPostExecute(ArrayList<Hashtable<String, String>> result) {//对应第三个参数
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		//progressDialog = progressDialog.show(, "转转", "当前", true, false);
	}

	@Override
	protected void onProgressUpdate(Void... values) {//参数对应第二个参数
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

}
