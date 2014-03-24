package evebit.com.app.huajieoa.views;

import java.util.Calendar;

import evebit.com.app.huajieoa.models.PublicWay;
import evebit.com.app.huajieoa.models.UserData;
import android.app.Activity;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 
 * @author YangChao
 *关于
 */
public class AboutActivity extends Activity {
	private String main=null;
	private Button backButton;
	Calendar c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
		
		PublicWay.activityList.add(this);
		main = (String)getIntent().getExtras().get("main");
		backButton = (Button)findViewById(R.id.About_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AboutActivity.this,MainActivity.class); 
				intent.putExtra("main",main);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(AboutActivity.this,
					MainActivity.class);
			intent.putExtra("main",main);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
