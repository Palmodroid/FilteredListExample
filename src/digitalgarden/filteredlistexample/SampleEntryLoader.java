package digitalgarden.filteredlistexample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import digitalgarden.magicmerlin.scribe.Scribe;
import digitalgarden.magicmerlin.utils.Rest;


/**
 * An implementation of AsyncTaskLoader which loads sample entries {@code List<SampleEntry>}
 */
public class SampleEntryLoader extends AsyncTaskLoader<List<SampleEntry>> 
	{
	// We hold a reference to the Loader's data here.
	private List<SampleEntry> sampleEntries;

	// An observer to notify the Loader when data changed.
	private DataChangeObserver dataChangeObserver;
	

	public SampleEntryLoader(Context context) 
		{
	    // Loaders may be used across multiple Activitys (assuming they aren't
	    // bound to the LoaderManager), so NEVER hold a reference to the context
	    // directly. Doing so will cause you to leak an entire Activity's context.
	    // The superclass constructor will store a reference to the Application
	    // Context instead, and can be retrieved with a call to getContext().
	    super(context);
		}

	/**
	 * This method is called on a background thread and generates a List of
	 * {@link SampleEntry} objects. 
	 */
	@Override
	public List<SampleEntry> loadInBackground() 
		{
		Scribe.locus();

	    // Create an array of sample entries
	  	final int SIZE = 10;	  	
	  	List<SampleEntry> entries = new ArrayList<SampleEntry>( SIZE );
	    for (int i = 0; i < SIZE; i++) 
	    	{
	    	// Sending progress
			Intent intent = new Intent(ProgressObserver.ACTION_STRING);
			intent.putExtra( ProgressObserver.DATA_WHO, ProgressObserver.LOADER );
			intent.putExtra( ProgressObserver.DATA_CYCLE, i+1 );
			intent.putExtra( ProgressObserver.DATA_MAX_CYCLES, SIZE );
			LocalBroadcastManager.getInstance( getContext() ).sendBroadcast(intent);

			SampleEntry entry = new SampleEntry( );
	    	entries.add(entry);
	    	Scribe.debug("   * " + entry.getString() + " added");
	    	Rest.fraction( 5000, SIZE);
	    	}

	    // Finishing progress
		Intent intent = new Intent(ProgressObserver.ACTION_STRING);
		intent.putExtra( ProgressObserver.DATA_WHO, ProgressObserver.LOADER );
		intent.putExtra( ProgressObserver.DATA_MAX_CYCLES, -1 );
		LocalBroadcastManager.getInstance( getContext() ).sendBroadcast(intent);
	    
	    return entries;
		}

	/**
	 * Called when there is new data to deliver to the client. The superclass will
	 * deliver it to the registered listener (i.e. the LoaderManager), which will
	 * forward the results to the client through a call to onLoadFinished.
	 */
	@Override
	public void deliverResult(List<SampleEntry> entries) 
		{
		Scribe.locus();

		if (isStarted()) 
			{
			Scribe.debug("Delivering results to the LoaderManager for the ListFragment.");
			// If the Loader is in a started state, have the superclass deliver the
		    // results to the client.
		    super.deliverResult(entries);
		    }

		// Invalidate the old data as we don't need it any more.
		if (sampleEntries != entries) 
			{
			Scribe.debug("Releasing any old data associated with this Loader.");
		    releaseResources(sampleEntries);
sampleEntries = null;
// ????? onReset meghívásra kerül ?????????
		    }

		if (isReset()) 
	    	{
	    	Scribe.debug("Warning! Async task (data) ready, but the Loader was reset!");
		    // The Loader has been reset; ignore the result and invalidate the data.
		    // This can happen when the Loader is reset while an asynchronous query
		    // is working in the background. That is, when the background thread
		    // finishes its work and attempts to deliver the results to the client,
		    // it will see here that the Loader has been reset and discard any
		    // resources associated with the new data as necessary.
	    	releaseResources(entries);
	    	}
		else
			{
			sampleEntries = entries;
			}
		}

    /**
     * Loader has enetered in 'started' mode. Load, deliver and observe data!  
     */
	@Override
	protected void onStartLoading() 
		{
		Scribe.locus();

	    if (takeContentChanged()) 
	    	{
	    	// When the observer detects data changes, it will call
	    	// onContentChanged() on the Loader, which will cause the next call to
	    	// takeContentChanged() to return true. If this is ever the case (or if
	    	// the current data is null), we force a new load.
	    	Scribe.debug("A content change has been detected... so force load!");
	    	forceLoad();
	    	} 
	    else if (sampleEntries == null) 
	    	{
	    	// If the current data is null... then we should make it non-null! :)
	    	Scribe.debug("The current data is data is null... so force load!");
	    	forceLoad();
	    	}
	    else // if (sampleEntries != null) 
	    	{
	    	// Deliver any previously loaded data immediately.
	    	Scribe.debug("Delivering previously loaded data to the client.");
	    	deliverResult(sampleEntries);
	    	}

	    // Register the observers that will notify the Loader when changes are made.
	    if (dataChangeObserver == null) 
	    	{
	    	dataChangeObserver = new DataChangeObserver(this);
	    	}
		}

    /**
     * Loader has enetered in 'stopped' mode. 
     * Cancel load (and no delivery), but data-observation remains!  
     */
	@Override
	protected void onStopLoading() 
		{
		Scribe.locus();

	    // The Loader has been put in a stopped state, so we should attempt to
	    // cancel the current load (if there is one).
	    cancelLoad();

	    // Note that we leave the observer as is; Loaders in a stopped state
	    // should still monitor the data source for changes so that the Loader
	    // will know to force a new load if it is ever started again.
		}

    /**
     * Loader has enetered in 'Reset' mode. 
     * Cancel load, loaded data should released. No delivery, no data-observation.  
     */
	@Override
	protected void onReset() 
		{
		Scribe.locus();

	    // Ensure the loader is stopped.
	    onStopLoading();

	    // Release data
    	releaseResources(sampleEntries);
    	sampleEntries = null;

	    // Stop monitoring for changes.
	    if ( dataChangeObserver != null ) 
	    	{
	    	LocalBroadcastManager.getInstance( getContext() ).unregisterReceiver( dataChangeObserver );
	    	dataChangeObserver = null;
	    	}
		}

	/**
     * Background data loading task was canceled before it was completed.  
     * The result will be properly disposed.
     */
 	@Override
	public void onCanceled(List<SampleEntry> entries) 
		{
		Scribe.locus();

	    // The load has been canceled, so we should release the resources
	    // associated with 'entries'.
	    releaseResources(entries);
		}

	/**
	 * Helper method to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	private void releaseResources(List<SampleEntry> entries) 
		{
		Scribe.locus();

		if ( entries != null )
			{
			// For a simple List, there is nothing to do. For something like a Cursor,
			// we would close it in this method. All resources associated with the
			// Loader should be released here.
			}
		}

	}
