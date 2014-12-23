package com.cooladata.android;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;


//******************************************************************************
public class ReferrerReceiver extends BroadcastReceiver
{
	private static final ObservableChanged _observable = new ObservableChanged();

	private final Pattern REFERRER_PATTERN = Pattern.compile("(^|&)referrer=([^&#=]*)([#&]|$)");
    private final Pattern UTM_SOURCE_PATTERN = Pattern.compile("(^|&)utm_source=([^&#=]*)([#&]|$)");
    private final Pattern UTM_MEDIUM_PATTERN = Pattern.compile("(^|&)utm_medium=([^&#=]*)([#&]|$)");
    private final Pattern UTM_CAMPAIGN_PATTERN = Pattern.compile("(^|&)utm_campaign=([^&#=]*)([#&]|$)");
    private final Pattern UTM_CONTENT_PATTERN = Pattern.compile("(^|&)utm_content=([^&#=]*)([#&]|$)");
    private final Pattern UTM_TERM_PATTERN = Pattern.compile("(^|&)utm_term=([^&#=]*)([#&]|$)");

	//--------------------------------------------------------------------------
	public static Observable getObservable()
	{
		return _observable;
	}

	//--------------------------------------------------------------------------
	public static String getReferrer(Context context)
	{
		// Return any persisted referrer value or null if we don't have a referrer.
		return context.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE).getString(Constants.INSTALL_REFERRER_KEY, null);
	}

	//--------------------------------------------------------------------------
	public ReferrerReceiver()
	{
	}

	//--------------------------------------------------------------------------
	@Override 
	public void onReceive(Context context, Intent intent)
	{

		try
		{
			// Make sure this is the intent we expect - it always should be.
			if ((null != intent) && (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")))
			{
				// This intent should have a referrer string attached to it.
				Bundle extra = intent.getExtras();
				for (String key : extra.keySet()) {
				    Object value = extra.get(key);
				    Log.d("CoolaData_Referrer", String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
				}
				
				
				String rawReferrer = URLDecoder.decode(extra.getString(Constants.INSTALL_REFERRER_KEY), "UTF-8");
				Log.d("CoolaData_Referrer", "rawReferrer = " + rawReferrer);
				Editor editor = context.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE).edit();
				if (null != rawReferrer)
				{
					// The string is usually URL Encoded, so we need to decode it.
					/*
					String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
					editor.putString(Constants.INSTALL_REFERRER_KEY, referrer);
					Log.d("CoolaData_Referrer", "referrer = " + referrer);
			        */
					final Matcher referrerMatcher = REFERRER_PATTERN.matcher(rawReferrer);
					final String referrer = find(referrerMatcher);
					if (null != referrer) {
						Log.d("CoolaData_Referrer", "referrer = " + referrer);
						editor.putString(Constants.INSTALL_REFERRER_KEY, referrer);
					}
					
			        final Matcher sourceMatcher = UTM_SOURCE_PATTERN.matcher(rawReferrer);
			        final String source = find(sourceMatcher);
			        if (null != source) {
						Log.d("CoolaData_Referrer", "source = " + source);
			        	editor.putString(Constants.UTM_SOURCE_KEY, source);
			        }

			        final Matcher mediumMatcher = UTM_MEDIUM_PATTERN.matcher(rawReferrer);
			        final String medium = find(mediumMatcher);
			        if (null != medium) {
						Log.d("CoolaData_Referrer", "medium = " + medium);
			        	editor.putString(Constants.UTM_MEDIUM_KEY, medium);
			        }
			        final Matcher campaignMatcher = UTM_CAMPAIGN_PATTERN.matcher(rawReferrer);
			        final String campaign = find(campaignMatcher);
			        if (null != campaign) {
						Log.d("CoolaData_Referrer", "campaign = " + campaign);
			        	editor.putString(Constants.UTM_CAMPAIGN_KEY, campaign);
			        }

			        final Matcher contentMatcher = UTM_CONTENT_PATTERN.matcher(rawReferrer);
			        final String content = find(contentMatcher);
			        if (null != content) {
						Log.d("CoolaData_Referrer", "content = " + content);
			        	editor.putString(Constants.UTM_CONTENT_KEY, content);
			        }

			        final Matcher termMatcher = UTM_TERM_PATTERN.matcher(rawReferrer);
			        final String term = find(termMatcher);
			        if (null != term) {
						Log.d("CoolaData_Referrer", "term = " + term);
			        	editor.putString(Constants.UTM_TERM_KEY, term);
			        }
			        editor.commit();
			        
					// Persist the referrer string.

					ReferrerObserver.NotifyChanges(referrer);
					// Let any listeners know about the change.
					_observable.notifyObservers(referrer);
				}
			}
		}
		catch (Exception e)
		{
		}
	}
	
	private String find(Matcher matcher) {
        if (matcher.find()) {
            final String encoded = matcher.group(2);
            if (null != encoded) {
                try {
                    return URLDecoder.decode(encoded, "UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    Log.e("CoolaData", "Could not decode a parameter into UTF-8");
                }
            }
        }
        return null;
    }

	//**************************************************************************
	protected static class ObservableChanged extends Observable
	{
		//----------------------------------------------------------------------
		@Override public boolean hasChanged()
		{
			return true;
		}
	}
}