package se.sds.activities;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sds.webshop.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UserMainActivity extends MainActivity { 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_main);
		
		Toast.makeText(getApplicationContext(), "Select an option", Toast.LENGTH_SHORT).show();
		
		Button UserLogoutButton = (Button) findViewById(R.id.user_logout_button);

		UserLogoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				new MonitoringCookieTask().execute();
				Intent intent = new Intent(UserMainActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}

		});

	}
	
	class MonitoringCookieTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {

			String cookieActive = null;
			DefaultHttpClient client = new DefaultHttpClient();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					client.getCookieStore().addCookie(cookie);
					cookieActive = cookie.getValue();
				}
			}
			return cookieActive;
		}

		@Override
		protected void onPostExecute(String cookieActive) {
			Toast.makeText(UserMainActivity.this, "Cookie used = " + cookieActive, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user_menu, menu);
		menu.findItem(R.id.userMain).setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.userMain:
			startActivity(new Intent(this, UserMainActivity.class));
			Toast.makeText(getApplicationContext(), "User Main", Toast.LENGTH_LONG).show();
			break;
		case R.id.categoriesList:
			startActivity(new Intent(this, CategoriesListActivity.class));
			Toast.makeText(getApplicationContext(), "Categories list", Toast.LENGTH_LONG).show();
			break;
		case R.id.productsList:
			startActivity(new Intent(this, ProductsListActivity.class));
			Toast.makeText(getApplicationContext(), "Products list", Toast.LENGTH_LONG).show();
			break;
		}

		return true;
	}
	
}
