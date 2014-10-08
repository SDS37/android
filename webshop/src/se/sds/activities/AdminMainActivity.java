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

public class AdminMainActivity extends MainActivity { 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_main);
		
		Toast.makeText(getApplicationContext(), "Select an option", Toast.LENGTH_SHORT).show();
		
		Button AdminLogoutButton = (Button) findViewById(R.id.admin_logout_button);

		AdminLogoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				new MonitoringCookieTask().execute();
				Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
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
			Toast.makeText(AdminMainActivity.this, "Cookie used = " + cookieActive, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu, menu);
		menu.findItem(R.id.adminMain).setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.adminMain:
			startActivity(new Intent(this, AdminMainActivity.class));
			Toast.makeText(getApplicationContext(), "Admin Main", Toast.LENGTH_LONG).show();
			break;
		case R.id.categoriesList:
			startActivity(new Intent(this, CategoriesListActivity.class));
			Toast.makeText(getApplicationContext(), "Categories list", Toast.LENGTH_LONG).show();
			break;
		case R.id.productsList:
			startActivity(new Intent(this, ProductsListActivity.class));
			Toast.makeText(getApplicationContext(), "Products list", Toast.LENGTH_LONG).show();
			break;
		case R.id.createCategory:
			startActivity(new Intent(this, CreateCategoryActivity.class));
			Toast.makeText(getApplicationContext(), "Create a category", Toast.LENGTH_LONG).show();
			break;
		case R.id.createProduct:
			startActivity(new Intent(this, CreateProductActivity.class));
			Toast.makeText(getApplicationContext(), "Create a product", Toast.LENGTH_LONG).show();
			break;
		case R.id.ordersList:
			startActivity(new Intent(this, OrdersListsActivity.class));
			Toast.makeText(getApplicationContext(), "Orders list", Toast.LENGTH_LONG).show();
			break;
		}

		return true;
	}

}
