package se.sds.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sds.webshop.R;

public class CreateCategoryActivity extends AdminMainActivity {

	private CreateCategoryOnServer AuthenticationTask = null;
	
	private String name;
	private String description;

	private EditText nameSubmitted;
	private EditText descriptionSubmitted;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_category);
		
		nameSubmitted = (EditText) findViewById(R.id.category_name);
		descriptionSubmitted = (EditText) findViewById(R.id.category_description);
		
		findViewById(R.id.create_category_button).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					createCategoryAttempt();
				}
			});

	}
	
	public void createCategoryAttempt() {

		boolean cancelProcess = false;
		View focusView = null;

		if (AuthenticationTask != null) return;

		nameSubmitted.setError(null);
		descriptionSubmitted.setError(null);

		name = nameSubmitted.getText().toString();
		description = descriptionSubmitted.getText().toString();
		
		if (TextUtils.isEmpty(name)) {
			nameSubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = nameSubmitted;
			cancelProcess = true;
		}

		if (TextUtils.isEmpty(description)) {
			descriptionSubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = descriptionSubmitted;
			cancelProcess = true;
		}

		if (cancelProcess) {
			focusView.requestFocus();
		} else {
			AuthenticationTask = new CreateCategoryOnServer(ip);
			AuthenticationTask.execute((Void) null);
		}
		
	}

	public class CreateCategoryOnServer extends AsyncTask<Void, Void, Boolean> {

		private String ip;

		public CreateCategoryOnServer(String ip) {
			this.ip = ip;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				HttpPost post = new HttpPost("http://" + ip + ":9000/new-category");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("name", name));
				nameValuePairs.add(new BasicNameValuePair("description",description));

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				new DefaultHttpClient().execute(post,
						new BasicResponseHandler());

				return true;

			} catch (Exception e) {

				Log.e("Error creating category", e.getMessage());

				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success == true) {
				Toast.makeText(getApplicationContext(), "Category added", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "The category was not created", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu, menu);
		menu.findItem(R.id.createCategory).setVisible(false);
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
