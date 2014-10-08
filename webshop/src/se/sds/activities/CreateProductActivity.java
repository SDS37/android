package se.sds.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sds.webshop.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CreateProductActivity extends AdminMainActivity {

	private CreateProductOnServer AuthenticationTask = null;
	
	private String name;
	private String description;
	private String cost;
	private String rrp;
	private String quantityInStock;
	
	private String categoryId;

	EditText nameSubmitted;
	EditText descriptionSubmitted;
	EditText costSubmitted;
	EditText rrpSubmitted;
	EditText quantityInStockSubmitted;
	
	Spinner categorySelected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_product);
				
		new GetCategoriesFromServer().execute();
		
		nameSubmitted = (EditText) findViewById(R.id.product_name);
		descriptionSubmitted = (EditText) findViewById(R.id.product_description);
		costSubmitted = (EditText) findViewById(R.id.product_cost);
		rrpSubmitted = (EditText) findViewById(R.id.product_rrp);
		quantityInStockSubmitted = (EditText) findViewById(R.id.product_quantity_in_stock);
	
		categorySelected = (Spinner) findViewById(R.id.categories_list_spinner);
		
		findViewById(R.id.create_product_button).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					createProductAttempt();
				}
			});
		
	}
		
	public void createProductAttempt() {

		boolean cancelProcess = false;
		View focusView = null;

		if (AuthenticationTask != null) return;

		nameSubmitted.setError(null);
		descriptionSubmitted.setError(null);
		costSubmitted.setError(null);
		rrpSubmitted.setError(null);
		quantityInStockSubmitted.setError(null);

		name = nameSubmitted.getText().toString();
		description = descriptionSubmitted.getText().toString();
		cost = costSubmitted.getText().toString();
		rrp = rrpSubmitted.getText().toString();
		quantityInStock = quantityInStockSubmitted.getText().toString();
		
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
		
		if (TextUtils.isEmpty(cost)) {
			costSubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = costSubmitted;
			cancelProcess = true;
		} 
				
		else if (!TextUtils.isDigitsOnly(cost)) {
			costSubmitted.setError(getString(R.string.integerRequiredError));
			focusView = costSubmitted;
			cancelProcess = true;
		}
		
		if (TextUtils.isEmpty(rrp)) {
			rrpSubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = rrpSubmitted;
			cancelProcess = true;
		} 
		
		else if (!TextUtils.isDigitsOnly(rrp)) {
			rrpSubmitted.setError(getString(R.string.integerRequiredError));
			focusView = rrpSubmitted;
			cancelProcess = true;
		}
		
		if (TextUtils.isEmpty(quantityInStock)) {
			quantityInStockSubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = quantityInStockSubmitted;
			cancelProcess = true;
		} 
		
		else if (!TextUtils.isDigitsOnly(quantityInStock)) {
			quantityInStockSubmitted.setError(getString(R.string.integerRequiredError));
			focusView = quantityInStockSubmitted;
			cancelProcess = true;
		}

		if (cancelProcess) {
			focusView.requestFocus();
		} else {
			AuthenticationTask = new CreateProductOnServer(ip);
			AuthenticationTask.execute((Void) null);
		}
	}

	public class CreateProductOnServer extends AsyncTask<Void, Void, Boolean> {
		
		private String ip;
		
		public CreateProductOnServer(String ip) {
			this.ip = ip;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				HttpPost post = new HttpPost("http://" + ip + ":9000/new-product");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("name", name));
				nameValuePairs.add(new BasicNameValuePair("description", description));
				nameValuePairs.add(new BasicNameValuePair("cost", cost));
				nameValuePairs.add(new BasicNameValuePair("RRP", rrp));
				nameValuePairs.add(new BasicNameValuePair("quantityInStock", quantityInStock));	
				
				categoryId = Integer.toString((int) categorySelected.getSelectedItemId());
				
				nameValuePairs.add(new BasicNameValuePair("category-id", categoryId)); 
				
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				new DefaultHttpClient().execute(post, new BasicResponseHandler());
				
				return true;
			
			} catch (Exception e) {			
				Log.e("Error creating product", e.getMessage());
				return false;
			}
		}
			
		@Override
		protected void onPostExecute(Boolean success) {
			if (success == true) {
				Toast.makeText(getApplicationContext(), "Product added",Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "The product was not created",Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	class GetCategoriesFromServer extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/categories-json"),
						new BasicResponseHandler());
				return new JSONArray(response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		protected void onPostExecute(JSONArray result) {
			categorySelected = (Spinner) findViewById(R.id.categories_list_spinner);
			categorySelected.setAdapter(new CategoriesListAdapter(result));
		}
	}
	
	class CategoriesListAdapter extends BaseAdapter implements SpinnerAdapter{

		private JSONArray categories;

		public CategoriesListAdapter(JSONArray categories) {
			this.categories = categories;
		}

		@Override
		public int getCount() {
			return categories.length();
		}

		@Override
		public Object getItem(int index) {			
			try {
				return categories.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			try {
				return categories.getJSONObject(index).getInt("id");
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			
			View categoryListItem = getLayoutInflater().inflate(R.layout.categories_list_spinner, parent, false);
						
			TextView categoryName = (TextView) categoryListItem.findViewById(R.id.category_name);

			try {
					
				JSONObject category = categories.getJSONObject(index);		
				categoryName.setText(category.getString("name"));

			} catch (JSONException e){
				throw new RuntimeException(e);
			}
		
			return categoryListItem;	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu, menu);
		menu.findItem(R.id.createProduct).setVisible(false);
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
