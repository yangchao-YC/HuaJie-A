package evebit.com.app.huajieoa.views;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

/**
 * 启动Activity，可以通过在onCreate中设置跳转代码
 * 来确定程序的第一个页面.
 * @author iFlytek
 * @since 20120823
 */
public class LauncherActivity extends Activity {
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.launcher);
		ImageView iv = (ImageView)findViewById(R.id.launcher);
		AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
		aa.setDuration(2000);
		iv.startAnimation(aa);
		
		aa.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
