package com.mattprecious.notisync.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattprecious.notisync.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

public class NotificationCardHeader extends CardHeader{

	private long mTime;
	
	public NotificationCardHeader(Context context){
		super(context, R.layout.notification_card_header);
	}
	
	public void setTime(long time){
		mTime = time;
	}
	
	@Override
    public void setupInnerViewElements(ViewGroup parent,View view){

        //Add title and time to header
        if (view!=null){
            TextView mTitleView = (TextView) view.findViewById(R.id.notification_card_header_title);
            if (mTitleView != null) {
            	mTitleView.setText(mTitle);
            }
            
            TextView mTimeView = (TextView) view.findViewById(R.id.notification_card_header_time);
            if (mTimeView != null) {
            	SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss");
            	String timeString = formatter.format(new Date(mTime));
            	mTimeView.setText(timeString);
            }
                
        }

    }
}
