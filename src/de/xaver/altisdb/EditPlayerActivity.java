package de.xaver.altisdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditPlayerActivity extends Activity {

	TextView txtName;
	TextView txtPlayerid;
	EditText txtCash;
	EditText txtBankacc;
	EditText txtCoplvl;
	EditText txtMediclvl;
	EditText txtAdminlvl;
	EditText txtDonatorlvl;
	Button btnSave;
	Button btnCancel;
	String uid;
	String db_url;
	String db_user;
	String db_pass;
	int success = 0;

	// Progress Dialog
	private ProgressDialog pDialog;

	// Node names
	private static final String TAG_URL = "url";
	private static final String TAG_USER = "user";
	private static final String TAG_PASS = "pass";
	private static final String TAG_UID = "uid";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_player);

		txtName = (TextView) findViewById(R.id.textName);
		txtPlayerid = (TextView) findViewById(R.id.textPlayerid);
		txtCash = (EditText) findViewById(R.id.inputCash);
		txtBankacc = (EditText) findViewById(R.id.inputBankacc);
		txtCoplvl = (EditText) findViewById(R.id.inputCoplvl);
		txtMediclvl = (EditText) findViewById(R.id.inputMediclvl);
		txtAdminlvl = (EditText) findViewById(R.id.inputAdminlvl);
		txtDonatorlvl = (EditText) findViewById(R.id.inputDonatorlvl);

		// save button
		btnSave = (Button) findViewById(R.id.btnSave);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		// getting Player and Connection from intent
		Intent i = getIntent();
		uid = i.getStringExtra(TAG_UID);
		db_url = i.getStringExtra(TAG_URL);
		db_user = i.getStringExtra(TAG_USER);
		db_pass = i.getStringExtra(TAG_PASS);

		// Getting complete player details in background thread
		new GetPlayerDetails().execute();

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update player
				new SavePlayerDetails().execute();
			}
		});

		// Delete button click event
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// go back
				Toast.makeText(getApplicationContext(), "Cancel",
						Toast.LENGTH_LONG).show();
				finish();
			}
		});

	}

	/**
	 * Background Async Task to Get complete player details
	 * */
	class GetPlayerDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditPlayerActivity.this);
			pDialog.setMessage("Loading Player details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting Player details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					try {

						Class.forName("com.mysql.jdbc.Driver");
						Connection con = DriverManager.getConnection(db_url,
								db_user, db_pass);

						// String result = "Database connection success\n";
						Statement st = con.createStatement();
						ResultSet rs = st
								.executeQuery("Select `name`,`playerid`,`cash`,`bankacc`,`coplevel`,`mediclevel`,`adminlevel`,`donatorlvl` From players Where uid = "
										+ uid);
						ResultSetMetaData rsmd=rs.getMetaData();
						 rsmd.getColumnType(1);

						while (rs.next()) {
							// display Player data in Textviews&EditText
							txtName.setText("Player Name: " + rs.getString(1));
							Log.d("SQL Type", String.valueOf(rsmd.getColumnType(1)));
							
							txtPlayerid.setText("Player ID:   "
									+ rs.getString(2));
							txtCash.setText(rs.getString(3));
							txtBankacc.setText(rs.getString(4));
							txtCoplvl.setText(rs.getString(5));
							Log.d("SQL Type", String.valueOf(rsmd.getColumnType(5)));
							txtMediclvl.setText(rs.getString(6));
							Log.d("SQL Type", String.valueOf(rsmd.getColumnType(6)));
							txtAdminlvl.setText(rs.getString(7));
							txtDonatorlvl.setText(rs.getString(8));
						}
						if (con != null) {
							try {
								con.close();
							} catch (Exception e) {
							}
						}
						;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to Save Player Details
	 * */
	class SavePlayerDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditPlayerActivity.this);
			pDialog.setMessage("Saving Player ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving Player
		 * */
		protected String doInBackground(String... args) {

			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection(db_url, db_user,
						db_pass);

				Statement st = con.createStatement();
				st.executeUpdate("Update players Set `cash`= '"
						+ txtCash.getText() + "' Where uid = " + uid);
				st.executeUpdate("Update players Set `bankacc`= '"
						+ txtBankacc.getText() + "' Where uid = " + uid);
				st.executeUpdate("Update players Set `coplevel`= '"
						+ txtCoplvl.getText() + "' Where uid = " + uid);
				st.executeUpdate("Update players Set `mediclevel`= '"
						+ txtMediclvl.getText() + "' Where uid = " + uid);
				st.executeUpdate("Update players Set `adminlevel`= '"
						+ txtAdminlvl.getText() + "' Where uid = " + uid);
				st.executeUpdate("Update players Set `donatorlvl`= '"
						+ txtDonatorlvl.getText() + "' Where uid = " + uid);
				success = 1;

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"ERROR SAVING PLAYER INFORMATION", Toast.LENGTH_LONG)
						.show();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once Player updated
			pDialog.dismiss();
			if (success == 1) {
				Toast.makeText(getApplicationContext(),
						"Player Information saved", Toast.LENGTH_LONG).show();
			}
			finish();
		}
	}
}
