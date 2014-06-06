package com.mattprecious.notisync.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.db.NotificationDatabaseAdapter;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

public class NotificationCard extends Card {

	private String header;
	private String title;
	private String message;
	private String packageName;
	private long time;
	private int resourceIdThumb;
	
	public NotificationCard(Context context, String header, String title, String message, String packageName, long time){
		super(context, R.layout.notification_card_inner);
		this.title = title;
		this.message = message;
		this.header = header;
		this.packageName = packageName;
		this.time = time;
		
		init();
	}
	
	private void init(){
		NotificationCardHeader cardHeader = new NotificationCardHeader(mContext);
		cardHeader.setTitle(header);
		cardHeader.setTime(time);
		addCardHeader(cardHeader);
		
		if (packageName.equals("com.whatsapp")){
			Log.d("NotificationCard", "whatsapp");
			resourceIdThumb = R.drawable.package_com_whatsapp;
		}
		else {
			resourceIdThumb = R.drawable.ic_stat_logo;
			Log.d("NotificationCard", "other");
		}
		
		CardThumbnail cardThumbnail = new CardThumbnail(mContext);
		cardThumbnail.setDrawableResource(resourceIdThumb);
		addCardThumbnail(cardThumbnail);

	}
	
	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		TextView mTitleTextView = (TextView) parent.findViewById(R.id.notification_card_inner_simple_title);
		TextView mMessageTextView = (TextView) parent.findViewById(R.id.notification_card_inner_simple_message);
		
		if (mTitleTextView != null){
			mTitleTextView.setText(title);
		}
		
		if (mMessageTextView != null){
			mMessageTextView.setText(message);
		}
	}
	
}
