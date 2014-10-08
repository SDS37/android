package se.sds.activities;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sds.webshop.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsListActivity extends MainActivity {

	public static String ID_EXTRA ="com.sds.webshop._ID"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.products_list);
		
		new GetProducts().execute();
		
	}

	class GetProducts extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {

			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/products-json"),
						new BasicResponseHandler());

				return new JSONArray(response);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		protected void onPostExecute(JSONArray result) {
			
			ListView listView = (ListView) findViewById(R.id.products_list_view);
			listView.setAdapter(new ProductsListAdapter(result));
	
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int index, long id) {
					
					Intent intent = new Intent(ProductsListActivity.this, ProductActivity.class); 
					intent.putExtra(ID_EXTRA, String.valueOf(id));
	                startActivity(intent);
					finish();
					
				}
			});
						
		}

	}

	class ProductsListAdapter extends BaseAdapter {

		private JSONArray products;

		public ProductsListAdapter(JSONArray products) {
			this.products = products;
		}

		@Override
		public int getCount() {
			return products.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return products.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			try {
				return products.getJSONObject(index).getInt("id");
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			View productListItem = getLayoutInflater().inflate(R.layout.products_list_item, parent, false);
			
			TextView productName = (TextView) productListItem.findViewById(R.id.product_name);
			TextView productDescription = (TextView) productListItem.findViewById(R.id.product_description);
			TextView productCost = (TextView) productListItem.findViewById(R.id.product_cost);
			TextView productRRP = (TextView) productListItem.findViewById(R.id.product_rrp);
			TextView productQuantityInStock = (TextView) productListItem.findViewById(R.id.product_quantityInStock);
			
			TextView productCategory = (TextView) productListItem.findViewById(R.id.product_category);

			try {

				JSONObject product = products.getJSONObject(index);
				productName.setText(product.getString("name"));
				productDescription.setText(product.getString("description"));
				productCost.setText(product.getString("cost"));
				productRRP.setText(product.getString("rrp"));
				productQuantityInStock.setText(product.getString("quantityInStock"));
				
				String categoryName = null;
				
				JSONArray categoryJsonArray = product.getJSONArray("category");
				
				for (int i = 0; i < categoryJsonArray.length(); i++) {
					JSONObject name = categoryJsonArray.getJSONObject(i);
					categoryName = name.getString("name");
				}
				
				productCategory.setText(categoryName);

			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			return productListItem;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu, menu);
		menu.findItem(R.id.adminMain).setVisible(false);
		menu.findItem(R.id.productsList).setVisible(false);
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
