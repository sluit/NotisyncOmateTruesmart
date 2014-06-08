package com.mattprecious.notisync.activity;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mattprecious.notisync.BuildConfig;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.db.NotificationDatabaseAdapter;
import com.mattprecious.notisync.devtools.DevToolsActivity;
import com.mattprecious.notisync.fragment.AccessibilityDialogFragment;
import com.mattprecious.notisync.model.NotificationCard;
import com.mattprecious.notisync.preferences.SettingsActivity;
import com.mattprecious.notisync.util.Helpers;
import com.mattprecious.notisync.util.MyLog;

public class NotificationListActivity extends SherlockActivity  {

	private NotificationDatabaseAdapter notificationDbAdapter;
	private CardListView cardListView;
	private CardArrayAdapter cardArrayAdapter;
	private ArrayList<Card> cards = new ArrayList<Card>();
	
	private Context context;
	private boolean busy;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        
        this.context = this;
        notificationDbAdapter = new NotificationDatabaseAdapter(this);
        
        busy = true;
        new InitNotificationCards().execute();
	}

	private void init(){
		cardArrayAdapter = new CardArrayAdapter(this, cards);
		
		cardListView = (CardListView) findViewById(R.id.notification_card_list);
		if (cardListView != null){
			cardListView.setAdapter(cardArrayAdapter);
		}

	}

	private class InitNotificationCards extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... voids) {
			openNotificationDatabaseRead();
			Cursor cursor = notificationDbAdapter.fetchNotifications();
			cursor.moveToFirst(); 

			//Log.d("FragmentHistory", "cursor size " + cursor.getCount());
			while(!cursor.isAfterLast()){
				NotificationCard notificationCard = cursorToNotificationCard(cursor);
				cards.add(notificationCard);
				cursor.moveToNext();
			}
			//Log.d("FragmentHistory", "history list " + historyList.size());
			cursor.close();
			notificationDbAdapter.close();

			return null;
		}


		@Override
		protected void onPostExecute(Void void1) {
			init();
			busy = false;
		}
	}
	
	private class AddNotificationCards extends AsyncTask<Void, Card, Void>{

		@Override
		protected void onPreExecute(){
			cards.clear();
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			openNotificationDatabaseRead();
			Cursor cursor = notificationDbAdapter.fetchNotifications();
			cursor.moveToFirst(); 

			//Log.d("FragmentHistory", "cursor size " + cursor.getCount());
			while(!cursor.isAfterLast()){
				NotificationCard notificationCard = cursorToNotificationCard(cursor);
				publishProgress(notificationCard);
				cursor.moveToNext();
			}
			//Log.d("FragmentHistory", "history list " + historyList.size());
			cursor.close();
			notificationDbAdapter.close();

			return null;
		}
		
		protected void onProgressUpdate(Card... progress) {
			cards.add(progress[0]);
	    }

		@Override
		protected void onPostExecute(Void voids) {
			cardArrayAdapter.notifyDataSetChanged();
			busy = false;
		}
	}
	
	
	private void openNotificationDatabaseRead(){
		try {
			notificationDbAdapter.openRead(); 
		} catch (Exception e){
			MyLog.e("Error", "opening database read");
		}
	}
	
	private void openNotificationDatabaseWrite(){
		try {
			notificationDbAdapter.openWrite(); 
		} catch (Exception e){
			MyLog.e("Error", "opening database write");
		}
	}
	
	private NotificationCard cursorToNotificationCard(Cursor cursor){
		String header = cursor.getString(NotificationDatabaseAdapter.KEY_APP_NAME_INDEX);
		String title = cursor.getString(NotificationDatabaseAdapter.KEY_MESSAGE_TITLE_INDEX);
		String message = cursor.getString(NotificationDatabaseAdapter.KEY_MESSAGE_INDEX);
		String packageName = cursor.getString(NotificationDatabaseAdapter.KEY_PACKAGE_NAME_INDEX);
		Long time = cursor.getLong(NotificationDatabaseAdapter.KEY_TIME_INDEX);

		return new NotificationCard(context, header, title, message, packageName, time);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.notifications, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	finish();
                return false;
            case R.id.menu_refresh:
            	if (!busy){
            		busy = true;
                    new AddNotificationCards().execute();
            	}
            	return true;
            case R.id.menu_delete:
            	clearDatabase();
            	return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void clearDatabase(){
    	openNotificationDatabaseWrite();
    	notificationDbAdapter.deleteAllNotifications();
    	notificationDbAdapter.close();
    	
    	if (!busy){
    		busy = true;
            new AddNotificationCards().execute();
    	}
    }
	
}
