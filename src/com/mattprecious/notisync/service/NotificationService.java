/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

import com.mattprecious.notisync.db.DbAdapter;
import com.mattprecious.notisync.message.BaseMessage;
import com.mattprecious.notisync.message.CustomMessage;
import com.mattprecious.notisync.message.GtalkMessage;
import com.mattprecious.notisync.model.PrimaryProfile;
import com.mattprecious.notisync.util.MyLog;
import com.mattprecious.notisync.util.Preferences;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationService extends AccessibilityService {
	private final static String TAG = "NotificationService";

	private final static List<String> gtalkPackageNames = Arrays.asList(new String[] {
			"com.google.android.talk", "com.google.android.apps.gtalkservice",
			"com.google.android.gsf",
	});

	// TODO: Locale issues? This pattern isn't really global...
	private final Pattern gtalkPattern = Pattern.compile("(.*): (.*)");

	private static boolean running = false;

	private LocalBroadcastManager broadcastManager;
	private DbAdapter dbAdapter;

	@Override
	public void onCreate() {
		super.onCreate();

		running = true;

		broadcastManager = LocalBroadcastManager.getInstance(this);
		dbAdapter = new DbAdapter(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		running = false;
	}

	public static boolean isRunning() {
		return running;
	}

	@Override
	protected void onServiceConnected() {
		MyLog.d(TAG, "Service connected");
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		info.flags = AccessibilityServiceInfo.DEFAULT;
		info.notificationTimeout = 100;
		setServiceInfo(info);
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		MyLog.d(TAG, "onAcessibilityEvent()");
		if (!Preferences.isPrimary(this)) {
			MyLog.d(TAG, "not primary mode");
			return;
		}

		if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			String packageName = (String) event.getPackageName();
			Notification notification = (Notification) event.getParcelableData();
			if (notification == null) {
				MyLog.d(TAG, "notification is null");
				return;
			}

			CharSequence tickerText = notification.tickerText;

			if (packageName == null) {
				return;
			}

			// handle gtalk messages
			if (gtalkPackageNames.contains(packageName)) {
				if (!Preferences.getPrimaryGtalkEnabled(this)) {
					return;
				}

				if (tickerText == null) {
					MyLog.e(TAG, "gtalk ticker text is null");
					return;
				}

				Matcher matcher = gtalkPattern.matcher(tickerText);
				if (matcher.matches()) {
					String sender = matcher.group(1);
					String message = matcher.group(2);

					GtalkMessage gtalkMessage = new GtalkMessage.Builder().sender(sender)
							.message(message).build();

					sendMessage(gtalkMessage);
				} else {
					MyLog.d(TAG, "Pattern does not match: " + tickerText);
				}
				return;
			} else {
				dbAdapter.openReadable();
				PrimaryProfile profile = dbAdapter.getPrimaryProfileByPackage(packageName);
				dbAdapter.close();

				if (profile != null && profile.isEnabled()) {
					String message = notification.tickerText == null ? null
							: notification.tickerText.toString();
					/*CustomMessage customMessage = new CustomMessage.Builder()
					.tag(profile.getTag())
					.appName(profile.getName())
					.messageTitle(message)
					.build();*/

					CustomMessage customMessage = getCustomMessage(profile, notification);
					
					sendMessage(customMessage);
				}
			}

			MyLog.d(TAG, "packageName: " + packageName);
			// MyLog.d(TAG, notification.tickerText);
		}

	}

	//From http://stackoverflow.com/questions/9292032/extract-notification-text-from-parcelable-contentview-or-contentintent
	@SuppressLint("NewApi")
	private CustomMessage getCustomMessage(PrimaryProfile profile, Notification notification){
		RemoteViews views = null;
		if (android.os.Build.VERSION.SDK_INT >= 16){
			views = notification.bigContentView;
		}
		MyLog.d(TAG, "views: " + views);
		if (views == null){
			views = notification.contentView;
		}
		if (views == null){
			return null;
		}
		Class secretClass = views.getClass();
		ArrayList<String> list = new ArrayList<String>();
		try {
			Map<Integer, String> text = new HashMap<Integer, String>();
			//int counter = 0;


			Field outerFields[] = secretClass.getDeclaredFields();

			for (int i = 0; i < outerFields.length; i++) {
				if (!outerFields[i].getName().equals("mActions")) continue;

				outerFields[i].setAccessible(true);

				ArrayList<Object> actions = (ArrayList<Object>) outerFields[i]
						.get(views);
				for (Object action : actions) {
					Field innerFields[] = action.getClass().getDeclaredFields();

					Object value = null;
					Integer type = null;
					Integer viewId = null;
					for (Field field : innerFields) {
						field.setAccessible(true);
						if (field.getName().equals("value")) {
							value = field.get(action);
							//MyLog.d(TAG, "value: " + value);
						} else if (field.getName().equals("type")) {
							type = field.getInt(action);
							//MyLog.d(TAG, "type: " + type);
						} else if (field.getName().equals("viewId")) {
							viewId = field.getInt(action);
							//MyLog.d(TAG, "viewId: " + viewId);
						} 

						if (type != null) {
							if (type == 5 || type == 9 || type == 10) {
								
								
								//if (viewId != null && value != null){
								list.add(value.toString());
								
								//text.put(type + 100 * counter, value.toString());
								//counter ++;
								//}
							}
						}
					}

					//System.out.println("title is: " + text.get(16908310));
					//System.out.println("info is: " + text.get(16909082));
					//System.out.println("text is: " + text.get(16908358));
				}
			}
			
			String testStr = "";
			for(int i = 0; i < list.size(); i++){
				testStr += ", " + list.get(i);
			}

			MyLog.d(TAG, "testStr: " + testStr);
			
			/*col = text.values();
			String testStr = "";
			for(String el : col){
				testStr += ", " + el;
			}

			MyLog.d(TAG, "testStr: " + testStr);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*String bitmapString = bitmapToString(notification.largeIcon);
		Log.d("NotificationService", "BitmapString: " + bitmapString);
		
		Drawable icon = null;
		try {
			icon = getPackageManager().getApplicationIcon(profile.getPackageName());
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String iconString = drawableToString(icon);
		//imageView.setImageDrawable(icon);*/
		
		if (list.size() > 2){
			return new CustomMessage.Builder()
			.tag(profile.getTag())
			.appName(profile.getName())
			.messageTitle(list.get(0))
			.time(list.get(1))
			.message(list.get(3))
			.packageName(profile.getPackageName())
			/*.smallIcon(iconString)
			.largeIcon(bitmapString)*/
			.build();
		}
		else {
			return new CustomMessage.Builder()
			.tag(profile.getTag())
			.appName(profile.getName())
			.messageTitle(list.get(0))
			.time(list.get(1))
			.message(list.get(2))
			.packageName(profile.getPackageName())
			/*.smallIcon(iconString)
			.largeIcon(bitmapString)*/
			.build();
		}
	}

	private String drawableToString(Drawable drawable){
		if (drawable != null){
			return bitmapToString(((BitmapDrawable)drawable).getBitmap());
		}
		return "null";
	}
	
	/**
     * @param bitmap
     * @return converting bitmap and return a string
     */
     private String bitmapToString(Bitmap bitmap){
         if (bitmap != null){
        	 ByteArrayOutputStream baos = new  ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
             byte [] b=baos.toByteArray();
             String temp=Base64.encodeToString(b, Base64.DEFAULT);
             return temp;
         }
         return "null";
    }
	
	@Override
	public void onInterrupt() {
	}

	private void sendMessage(BaseMessage message) {
		Intent intent = new Intent(PrimaryService.ACTION_SEND_MESSAGE);
		intent.putExtra(PrimaryService.EXTRA_MESSAGE, BaseMessage.toJsonString(message));
		broadcastManager.sendBroadcast(intent);
	}

}
