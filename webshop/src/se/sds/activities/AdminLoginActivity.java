package se.sds.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.sds.webshop.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminLoginActivity extends MainActivity {

	private LoginTask AuthenticationTask = null;

	private String username;
	private String password;

	private EditText usernameSubmitted;
	private EditText passwordSubmitted;

	private View LoginFormView;
	private View LoginStatusView;
	private TextView LoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_admin);

		Toast.makeText(getApplicationContext(), "Admin area", Toast.LENGTH_SHORT).show();
		
		usernameSubmitted = (EditText) findViewById(R.id.admin_username);
		passwordSubmitted = (EditText) findViewById(R.id.admin_password);

		LoginFormView = findViewById(R.id.login_form);
		LoginStatusView = findViewById(R.id.login_status);
		LoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.admin_submit_button).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					loginAttempt();
				}
			});
	}

	public void loginAttempt() {

		boolean cancelProcess = false;
		View focusView = null;

		if (AuthenticationTask != null) return;

		usernameSubmitted.setError(null);
		passwordSubmitted.setError(null);

		username = usernameSubmitted.getText().toString();
		password = passwordSubmitted.getText().toString();

		if (TextUtils.isEmpty(username)) {
			usernameSubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = usernameSubmitted;
			cancelProcess = true;
		}

		if (TextUtils.isEmpty(password)) {
			passwordSubmitted.setError(getString(R.string.fieldRequiredError));
			focusView = passwordSubmitted;
			cancelProcess = true;
		}

		if (cancelProcess) {
			focusView.requestFocus();
		} else {
			LoginStatusMessageView.setText(R.string.loginSigningProgress);
			showProgress(true);
			AuthenticationTask = new LoginTask(ip);
			AuthenticationTask.execute((Void) null);
		}
	}

	public class LoginTask extends AsyncTask<Void, Void, Boolean> {
		
		private String ip;

		public LoginTask (String ip) {
			this.ip = ip;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {

			HttpPost post = new HttpPost("http://" + ip + ":9000/admin-login");
			DefaultHttpClient currentClient = new DefaultHttpClient();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			try {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			try {
				currentClient.execute(post, new BasicResponseHandler());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			cookies = currentClient.getCookieStore().getCookies();

			boolean loggedIn = false;

			for (Cookie cookie : cookies) {
			
				if ("PLAY_SESSION".equals(cookie.getName())) {
					loggedIn = true;
				}
			}

			return loggedIn;

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			AuthenticationTask = null;
			showProgress(false);

			if (success == true) {

				Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
				startActivity(intent);
				finish();
				
			} else {
				Toast.makeText(getApplicationContext(), "No success", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			LoginStatusView.setVisibility(View.VISIBLE);
			LoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							LoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			LoginFormView.setVisibility(View.VISIBLE);
			LoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			LoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
