package se.sds.services;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import se.sds.activities.OrdersListsActivity;

import com.sds.webshop.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class NewOrderNotifierService extends IntentService {

	int actualOrdersNumber, pastOrdersNumber = 0;
	
	public NewOrderNotifierService() {
		super("NewOrderNotifierService");
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {

		try {
			while (true) {
				
				Thread.sleep(1000 * 5);			
				new GetOrders().execute();
				
			}

		} catch (Exception e) {

			Log.e("Exception", e.getMessage());
		}

	}
	
	class GetOrders extends AsyncTask<Void, Void, String> {

		private String ip = "192.168.1.8";
		
		@Override
		protected String doInBackground(Void... params) {

			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/products-shopped-json"),
						new BasicResponseHandler());
				
				return response;

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		protected void onPostExecute(String result) {
			
			try {
				JSONArray jsonArray = new JSONArray(result);
				
				if (jsonArray.length() == 0) {
					return;
				} 
				
				actualOrdersNumber = jsonArray.length();
		
					if(actualOrdersNumber > pastOrdersNumber){
						sendNotification();					
					
				}
				
				pastOrdersNumber = actualOrdersNumber;
			
			} catch (JSONException e) {
				e.printStackTrace();
			}		
						
		}

	}
	
	@SuppressWarnings("deprecation")
	private void sendNotification(){
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		String reminderText = "A new order was made!";
		Notification notification = new Notification(R.drawable.bear_icon, reminderText, System.currentTimeMillis());

		String notificationTitle = "A new order was made...";
		String notificationText = "Open orders list";

		Intent intent = new Intent(getApplicationContext(), OrdersListsActivity.class);
		
		PendingIntent StartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		notification.setLatestEventInfo(getApplicationContext(), notificationTitle, notificationText, StartIntent);

		int NOTIFICATION_ID = 1;
		
		notificationManager.notify(NOTIFICATION_ID, notification);
		
	}
	
}


