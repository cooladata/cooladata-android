package com.cooladata.android;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.SharedPreferences;

public class UtmData implements Observer {
	private String ReferrerValue	= "";
	private String UtmCampaign 		= "";
	private String UtmContent 		= "";
	private String UtmMedium 		= "";
	private String UtmSource 		= "";
	private String UtmTerm 			= "";
	private boolean HasReferrer		= false;
	private SharedPreferences 		mPref;
	
	/* 
		This class will hold the Referrer and the Campaign data.
		It created once at the CoolaDataTracker setup.
		This class implement the Observer in order to get the Referrer data while active - if gotten at that point.
		the value is being checked once while instantiate and then only when change is caught by the receiver. 
		
		this way we will not have to check the preference for each event tracking.
	 */

	public UtmData(Context context){
		ReferrerObserver.getObservable().addObserver(this);
		mPref = context.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE);
		if (ReferrerValue.equals("")){
			ReferrerValue	= mPref.getString(Constants.INSTALL_REFERRER_KEY, "");
			UtmCampaign 	= mPref.getString(Constants.UTM_CAMPAIGN_KEY, "");
			UtmContent 		= mPref.getString(Constants.UTM_CONTENT_KEY, "");
			UtmMedium 		= mPref.getString(Constants.UTM_MEDIUM_KEY, "");
			UtmSource 		= mPref.getString(Constants.UTM_SOURCE_KEY, "");
			UtmTerm 		= mPref.getString(Constants.UTM_TERM_KEY, "");
		}
		if (ReferrerValue == null){ ReferrerValue = ""; }
		HasReferrer = !ReferrerValue.equals("");
	}
	
	public String getReferrerValue(){
		if (ReferrerValue.equals("")){
			ReferrerValue = mPref.getString(Constants.INSTALL_REFERRER_KEY, "");
		}
		
		return ReferrerValue;
	}
	
	public String getUtmCampaign(){
		if (UtmCampaign.equals("")){
			UtmCampaign 	= mPref.getString(Constants.UTM_CAMPAIGN_KEY, "");
		}
		
		return UtmCampaign;
	}
	
	public String getUtmContent(){
		if (UtmContent.equals("")){
			UtmContent 	= mPref.getString(Constants.UTM_CONTENT_KEY, "");
		}

		return UtmContent;
	}
	
	public String getUtmMedium(){
		if (UtmMedium.equals("")){
			UtmMedium 	= mPref.getString(Constants.UTM_MEDIUM_KEY, "");
		}

		return UtmMedium;
	}
	
	public String getUtmSource(){
		if (UtmSource.equals("")){
			UtmSource 	= mPref.getString(Constants.UTM_SOURCE_KEY, "");
		}

		return UtmSource;
	}
	
	public String getUtmTerm(){
		if (UtmTerm.equals("")){
			UtmTerm 	= mPref.getString(Constants.UTM_TERM_KEY, "");
		}

		return UtmTerm;
	}

	public boolean HasInstallReferrer(){
		return HasReferrer;
	}
	
	@Override
	public void update(Observable observable, Object data) {
		ReferrerValue	= mPref.getString(Constants.INSTALL_REFERRER_KEY, "");
		UtmCampaign 	= mPref.getString(Constants.UTM_CAMPAIGN_KEY, "");
		UtmContent 		= mPref.getString(Constants.UTM_CONTENT_KEY, "");
		UtmMedium 		= mPref.getString(Constants.UTM_MEDIUM_KEY, "");
		UtmSource 		= mPref.getString(Constants.UTM_SOURCE_KEY, "");
		UtmTerm 		= mPref.getString(Constants.UTM_TERM_KEY, "");
		HasReferrer = !ReferrerValue.equals("");
	}
	
}
