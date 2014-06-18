package digitalgarden.filteredlistexample;

import digitalgarden.magicmerlin.scribe.Scribe;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * A simulated observer that listens "DatasetChanged" messages.
 * Used by the {@link SampleEntryLoader}. 
 * Notifies the loader when changes are detected.
 */
public class DataChangeObserver extends BroadcastReceiver 
	{
	private SampleEntryLoader loader;

	public DataChangeObserver(SampleEntryLoader loader) 
		{
		Scribe.locus();
	    this.loader = loader;
	
	    // Register this Receiver to receive messages.
	    IntentFilter filter = new IntentFilter("DatasetChanged");
	    LocalBroadcastManager.getInstance( loader.getContext() ).registerReceiver( this, filter);
		}

	@Override
	public void onReceive(Context context, Intent intent) 
		{
		Scribe.locus();
		
		// Tell the loader about the change.
		loader.onContentChanged();
		}
	}