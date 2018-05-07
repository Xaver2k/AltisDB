package de.xaver.altisdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllPlayersActivity extends ListActivity {

	String db_url;
	String db_user;
	String db_pass;
	String uid;
	String name;
	private static final String TAG_URL = "url";
	private static final String TAG_USER = "user";
	private static final String TAG_PASS = "pass";

	// Progress Dialog
	private ProgressDialog pDialog;

	// Node names
	private static final String TAG_UID = "uid";
	private static final String TAG_NAME = "name";

	// List view
	private ListView lv;

	// Listview Adapter
	ArrayAdapter<HashMap<String, String>> adapter;

	// Search EditText
	EditText playerfilter;

	// ArrayList for Listview
	ArrayList<HashMap<String, String>> playerList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_players);

		// getting player details from intent
		Intent i = getIntent();

		// getting player id (pid) from intent
		db_url = i.getStringExtra(TAG_URL);
		db_user = i.getStringExtra(TAG_USER);
		db_pass = i.getStringExtra(TAG_PASS);

		// Hashmap for ListView
		playerList = new ArrayList<HashMap<String, String>>();
		playerfilter = (EditText) findViewById(R.id.playerfilter);
		lv = (ListView) findViewById(android.R.id.list);

		// Get listview
		lv = getListView();

		// Adding items to listview
		adapter = new ArrayAdapter<HashMap<String, String>>(this,
				android.R.id.list, R.id.player_name);

		lv.setAdapter(adapter);

		new LoadAllPlayers().execute();

		playerfilter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text set filter for listview
				((Filterable) AllPlayersActivity.this.lv.getAdapter()).getFilter().filter(cs);

			}

			@Override
			public void beforeTextChanged(CharSequence cs, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable cs) {
				if(cs.length()==0){
		            lv.clearTextFilter();
		        }
				lv.setTextFilterEnabled(false);
			}
		});

		// on seleting single Player
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String selected_uid = ((TextView) view.findViewById(R.id.uid)).getText().toString();
				
				// Starting new intent 
				Intent in = new Intent(getApplicationContext(), EditPlayerActivity.class); //
				//sending pid to next activity 
				in.putExtra(TAG_URL, db_url);
				in.putExtra(TAG_USER, db_user);
				in.putExtra(TAG_PASS, db_pass);
				in.putExtra(TAG_UID, selected_uid);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
				}
		});

	}

	// Response from Edit Player Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received
			// means user edited/deleted Player
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all Player by making Database
	 * */
	class LoadAllPlayers extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AllPlayersActivity.this);
			pDialog.setMessage("Loading Players. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
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

				// String result = "Database connection success\n";
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select * from players");

				while (rs.next()) {

					uid = String.valueOf(rs.getInt(1));
					name = rs.getString(2);
					
					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();
					// adding each child node to HashMap key => value
					map.put(TAG_UID, uid);
					map.put(TAG_NAME, name);

					// adding HashList to ArrayList
					playerList.add(map);
				}
				if (con != null)
				{
					try
					{
						con.close();
					}
					catch (Exception e){}
				};

			} catch (Exception e) {
				// This will catch any exception, because they are all descended
				// from Exception
				e.printStackTrace();
				// If Exception go back to MainActivity
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				// Closing all previous activities;
				startActivity(i);
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
					/**
					 * Updating data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							AllPlayersActivity.this, playerList,
							R.layout.list_item, new String[] { TAG_UID,
									TAG_NAME }, new int[] { R.id.uid,
									R.id.player_name });
					// updating listview
					setListAdapter(adapter);
				}
			});
		}
	}
}