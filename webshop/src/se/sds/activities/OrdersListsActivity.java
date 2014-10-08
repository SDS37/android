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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrdersListsActivity extends MainActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orders_list);
		
		new GetOrders().execute();
		
	}

	class GetOrders extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {

			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/products-shopped-json"),
						new BasicResponseHandler());
				
				return new JSONArray(response);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		protected void onPostExecute(JSONArray result) {
			
			ListView listView = (ListView) findViewById(R.id.orders_list_view);
			listView.setAdapter(new OrdersListAdapter(result));
						
		}

	}
	
	class OrdersListAdapter extends BaseAdapter {

		private JSONArray orders;

		public OrdersListAdapter(JSONArray orders) {
			this.orders = orders;
		}

		@Override
		public int getCount() {
			return orders.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return orders.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {

			View orderListItem = getLayoutInflater().inflate(R.layout.orders_list_item, parent, false);
			
			TextView orderId = (TextView) orderListItem.findViewById(R.id.order_id);
			TextView username = (TextView) orderListItem.findViewById(R.id.order_user_username);
			TextView usernameAddress = (TextView) orderListItem.findViewById(R.id.order_user_address);
			TextView productName = (TextView) orderListItem.findViewById(R.id.order_product_name);
			TextView productQuantity = (TextView) orderListItem.findViewById(R.id.order_product_quantity);
			TextView productSubtotal = (TextView) orderListItem.findViewById(R.id.order_product_subtotal);
			TextView total = (TextView) orderListItem.findViewById(R.id.order_total);
			
			try {

				JSONObject order = orders.getJSONObject(index);
				
				orderId.setText(order.getString("orderId"));
				username.setText(order.getString("username"));
				usernameAddress.setText(order.getString("address"));
				productName.setText(order.getString("product"));
				productQuantity.setText(order.getString("productQuantity"));
				productSubtotal.setText(order.getString("subtotal"));
				total.setText(order.getString("productsTotal"));

			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			return orderListItem;
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu, menu);
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

