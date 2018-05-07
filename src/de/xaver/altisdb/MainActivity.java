package de.xaver.altisdb;

import java.sql.Connection;
import java.sql.DriverManager;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static String db_url;
	private static String db_user;
	private static String db_pass;

	public static final String PREFS_IP = "MyPrefsIP";
	public static final String PREFS_USER = "MyPrefsUser";
	private static final String TAG_URL = "url";
	private static final String TAG_USER = "user";
	private static final String TAG_PASS = "pass";

	TextView tv = null;
	ImageView img_donate = null;
	String result = "Database connection success\n";
	Button btnLogin;
	EditText editDatabase_ip;
	EditText editUser;
	EditText editPass;
	String database_ip;
	String user;
	String pass;
	int loginerror = 0;

	// Progress Dialog
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		tv = (TextView) this.findViewById(R.id.text_view);
		img_donate = (ImageView) this.findViewById(R.id.img_donate);
		editDatabase_ip = (EditText) findViewById(R.id.editDatabase_ip);
		editUser = (EditText) findViewById(R.id.editUser);
		editPass = (EditText) findViewById(R.id.editPass);

		// Restore preferences
		SharedPreferences settings_ip = getSharedPreferences(PREFS_IP, 0);
		database_ip = settings_ip.getString("database_ip", database_ip);
		editDatabase_ip.setText(database_ip);
		SharedPreferences settings_user = getSharedPreferences(PREFS_USER, 0);
		user = settings_user.getString("user", user);
		editUser.setText(user);

		// Buttons
		btnLogin = (Button) findViewById(R.id.btnLogin);

		// view Players click event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				db_url = "jdbc:mysql://" + editDatabase_ip.getText().toString()
						+ "?verifyServerCertificate=false" + "&useSSL=true"
						+ "&requireSSL=false";
				db_user = editUser.getText().toString();
				db_pass = editPass.getText().toString();

				// We need an Editor object to make preference changes.
				// All objects are from android.context.Context
				SharedPreferences settings_ip = getSharedPreferences(PREFS_IP,
						0);
				SharedPreferences.Editor editor_ip = settings_ip.edit();
				editor_ip.putString("database_ip", editDatabase_ip.getText()
						.toString());
				// Commit the edits!
				editor_ip.commit();

				SharedPreferences settings_user = getSharedPreferences(
						PREFS_USER, 0);
				SharedPreferences.Editor editor_user = settings_user.edit();
				editor_user.putString("user", editUser.getText().toString());
				// Commit the edits!
				editor_user.commit();

				new LoginUser().execute();
			}
		});

		img_donate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri
						.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A6ZAC5MYKF9X8"));
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Background Async Task to Load all Player by making Database
	 * */
	class LoginUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * getting All Players from Database
		 * */
		protected String doInBackground(String... args) {

			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection(db_url, db_user,
						db_pass);

				con.createStatement();
				con.close();
				runOnUiThread(new Runnable() {
					public void run() {
						tv.setText(result);
						loginerror = 0;
					}
				});
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
					}
				}
				;

			} catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						loginerror = 1;
						Toast.makeText(getApplicationContext(), "Login Error",
								Toast.LENGTH_LONG).show();
						tv.setText(e.toString());
					}
				});
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all Players
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					if (loginerror == 0) {
						Toast.makeText(getApplicationContext(),
								"Login successfull", Toast.LENGTH_SHORT).show();

						// Starting new intent
						Intent i = new Intent(getApplicationContext(),
								AllPlayersActivity.class);
						// sending Logindata to next activity
						i.putExtra(TAG_URL, db_url);
						i.putExtra(TAG_USER, db_user);
						i.putExtra(TAG_PASS, db_pass);

						// starting new activity and expecting some response
						// back
						startActivity(i);
					}
				}
			});
		}
	}

}
