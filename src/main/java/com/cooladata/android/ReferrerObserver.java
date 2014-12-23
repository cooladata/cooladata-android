package com.cooladata.android;

import java.util.Observable;

public abstract class ReferrerObserver {
	private static final ObservableChanged    _observable = new ObservableChanged();
	
    public static Observable getObservable()
    {
        return _observable;
    }
    
    public static void NotifyChanges(String referrer){
    	_observable.notifyObservers(referrer);
    }

    protected static class ObservableChanged extends Observable
    {
        //----------------------------------------------------------------------
        @Override public boolean hasChanged()
        {
            return true;
        }
    }
}
