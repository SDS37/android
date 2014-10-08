package se.sds.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.sds.webshop.R;

public class ProductActivity extends MainActivity {

	private SendProductToServer AuthenticationTask = null;
	
	String productId = null;
	
	private String quantity;
	
	EditText quantitySubmitted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);

		productId = getIntent().getStringExtra(ProductsListActivity.ID_EXTRA);

		new GetProduct().execute();

		quantitySubmitted = (EditText) findViewById(R.id.product_quantity);

		findViewById(R.id.add_to_cart_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						submitAddAttempt();
					}
				});

		findViewById(R.id.submit_order_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						new MakeOrderOnServer().execute();
					}
				});

	}

	class GetProduct extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {

			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/products-json/" + productId),
						new BasicResponseHandler());

				return new JSONObject(response);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		protected void onPostExecute(final JSONObject result) {

			Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();

			ListView listView = (ListView) findViewById(R.id.product_view);
			listView.setAdapter(new ProductAdapter(result));

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int index, long id) {

					Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();

				}
			});

		}

	}

	class ProductAdapter extends BaseAdapter {

		private JSONObject productAsJsonObject;

		public ProductAdapter(JSONObject product) {
			this.productAsJsonObject = product;
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int index) {
			return index;
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			View productItem = getLayoutInflater().inflate(R.layout.product_item, parent, false);

			TextView productName = (TextView) productItem.findViewById(R.id.product_view_name);
			TextView productDescription = (TextView) productItem.findViewById(R.id.product_view_description);
			TextView productCost = (TextView) productItem.findViewById(R.id.product_view_cost);
			TextView productRRP = (TextView) productItem.findViewById(R.id.product_view_rrp);
			TextView productQuantityInStock = (TextView) productItem.findViewById(R.id.product_view_quantityInStock);

			TextView productCategory = (TextView) productItem.findViewById(R.id.product_view_category);

			try {

				productName.setText(productAsJsonObject.getString("name"));
				productDescription.setText(productAsJsonObject.getString("description"));
				productCost.setText(productAsJsonObject.getString("cost"));
				productRRP.setText(productAsJsonObject.getString("rrp"));
				productQuantityInStock.setText(productAsJsonObject.getString("quantityInStock"));

				String categoryName = null;

				JSONArray categoryJsonArray = productAsJsonObject.getJSONArray("category");

				for (int i = 0; i < categoryJsonArray.length(); i++) {
					JSONObject name = categoryJsonArray.getJSONObject(i);
					categoryName = name.getString("name");
				}

				productCategory.setText(categoryName);

			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			return productItem;
		}

	}

	public void submitAddAttempt() {

		boolean cancelProcess = false;
		View focusView = null;

		if (AuthenticationTask != null)
			return;

		quantitySubmitted.setError(null);

		quantity = quantitySubmitted.getText().toString();

		if (TextUtils.isEmpty(quantity)) {
			quantitySubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = quantitySubmitted;
			cancelProcess = true;
		}

		if (!TextUtils.isDigitsOnly(quantity)) {
			quantitySubmitted
					.setError(getString(R.string.integerRequiredError));
			focusView = quantitySubmitted;
			cancelProcess = true;
		}

		if (cancelProcess) {
			focusView.requestFocus();
		} else {
			AuthenticationTask = new SendProductToServer();
			AuthenticationTask.execute((Void) null);
		}
	}

	public class SendProductToServer extends AsyncTask<Void, Void, String> {

//		private String ip;

		public SendProductToServer() {}

		@Override
		protected String doInBackground(Void... params) {

			HttpPost post = new HttpPost("http://" + ip + ":9000/products/" + productId);
			DefaultHttpClient client = new DefaultHttpClient();

			String cookieActive = null;

			for (Cookie cookie : cookies) {
				client.getCookieStore().addCookie(cookie);
				cookieActive = cookie.getValue();
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("productQuantity", quantity));

			try {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			try {
				client.execute(post, new BasicResponseHandler());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return cookieActive;
		}

		@Override
		protected void onPostExecute(String cookieActive) {
			Toast.makeText(ProductActivity.this, "Product added", Toast.LENGTH_LONG).show();
			Toast.makeText(ProductActivity.this, "Cookie used = " + cookieActive, Toast.LENGTH_SHORT).show();
		}

	}

	public class MakeOrderOnServer extends AsyncTask<Void, Void, Boolean> {

		//private String ip;

		public MakeOrderOnServer() {}

		@Override
		protected Boolean doInBackground(Void... params) {

			HttpGet get = new HttpGet("http://" + ip + ":9000/order"); 

			DefaultHttpClient client = new DefaultHttpClient();

			for (Cookie cookie : cookies) {
				client.getCookieStore().addCookie(cookie);
			}

			try {
				client.execute(get, new BasicResponseHandler());
				return true;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success == true) {
				Toast.makeText(ProductActivity.this, "Order sent",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"The order was not sent", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu, menu);
		menu.findItem(R.id.adminMain).setVisible(false);
		menu.findItem(R.id.categoriesList).setVisible(false);
		menu.findItem(R.id.createCategory).setVisible(false);
		menu.findItem(R.id.createProduct).setVisible(false);
		menu.findItem(R.id.ordersList).setVisible(false);
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
