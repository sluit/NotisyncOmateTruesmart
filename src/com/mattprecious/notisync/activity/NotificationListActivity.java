package com.mattprecious.notisync.activity;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.db.NotificationDatabaseAdapter;
import com.mattprecious.notisync.model.NotificationCard;
import com.mattprecious.notisync.util.MyLog;

public class NotificationListActivity extends SherlockActivity  {

	private NotificationDatabaseAdapter notificationDbAdapter;
	private CardListView cardListView;
	private ArrayList<Card> cards = new ArrayList<Card>();
	
	private Context context;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        
        this.context = this;
        notificationDbAdapter = new NotificationDatabaseAdapter(this);
        
        new AddNotificationCard().execute();
	}

	private void init(){
		CardArrayAdapter cardArrayAdapter = new CardArrayAdapter(this, cards);
		
		cardListView = (CardListView) findViewById(R.id.notification_card_list);
		if (cardListView != null){
			cardListView.setAdapter(cardArrayAdapter);
		}

	}

	private class AddNotificationCard extends AsyncTask<Void, Void, Void>{

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
		}
	}
	
	private void openNotificationDatabaseRead(){
		try {
			notificationDbAdapter.openRead(); 
		} catch (Exception e){
			MyLog.e("Error", "opening database read");
		}
	}
	
	private NotificationCard cursorToNotificationCard(Cursor cursor){
		String header = cursor.getString(NotificationDatabaseAdapter.KEY_APP_NAME_INDEX);
		String title = cursor.getString(NotificationDatabaseAdapter.KEY_MESSAGE_TITLE_INDEX);
		String message = cursor.getString(NotificationDatabaseAdapter.KEY_MESSAGE_INDEX);
		String packageName = cursor.getString(NotificationDatabaseAdapter.KEY_PACKAGE_NAME_INDEX);
		Long time = cursor.getLong(NotificationDatabaseAdapter.KEY_PACKAGE_NAME_INDEX);

		return new NotificationCard(context, header, title, message, packageName, time);
	}
	
}
