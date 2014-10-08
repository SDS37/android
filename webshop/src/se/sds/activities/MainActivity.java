package se.sds.activities;

import java.util.List;

import org.apache.http.cookie.Cookie;

import se.sds.services.NewOrderNotifierService;

import com.sds.webshop.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	protected final String ip = "192.168.1.8";
	
	static protected List<Cookie> cookies; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Intent backgroundService = null;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		backgroundService = new Intent(this, NewOrderNotifierService.class);
		startService(backgroundService);
		
		Button adminLoginButton = (Button) findViewById(R.id.admin_login_button);
		Button userLoginButton = (Button) findViewById(R.id.user_login_button);

		adminLoginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),AdminLoginActivity.class);
				startActivity(intent);
				finish();
			}

		});
		
		userLoginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),UserLoginActivity.class);
				startActivity(intent);
				finish();
			}

		});
	}

}
