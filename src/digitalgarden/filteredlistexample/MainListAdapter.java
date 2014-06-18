package digitalgarden.filteredlistexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import digitalgarden.magicmerlin.scribe.Scribe;
import digitalgarden.magicmerlin.utils.Rest;

/**
 * Simple list-adapter extends {@link BaseAdapter}implements {@link Filterable}. 
 * Data source is an external {@code ArrayList} of {@link SampleEntry}-s.
 */
public class MainListAdapter extends BaseAdapter implements Filterable
	{
	/** Needed by {@link getView}, set by constructor (using {@link Context}) */
	private LayoutInflater layoutInflater;
	/** Context needed by progress-messages */
	private Context context;

	/** Copy of external source of data */
	private List<SampleEntry> originalEntries;
	/** List of filtered entries */
	private List<SampleEntry> filteredEntries;

	/** Filter */
	private EntryFilter entryFilter;

	
	/** Constructor */
	public MainListAdapter( Context context ) 
		{
		super();
		this.context = context;
		this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

	/**
	 * Setup full dataset. Filter will be OFF.
	 * @param entries external datasource {@code ArrayList} of {@link SampleEntry}-s
	 */
	public void setData( List<SampleEntry> entries )
		{
		Scribe.locus();

		this.originalEntries = entries;
		
		// Filter forced on new data
		// filter() will cancel previous filtering
		// filter() will call super.notifyDataSetChanged() 
		filter();
		
		/* Filter OFF
		this.originalEntries = entries;
		this.filteredEntries = entries;
		
		// External notification is not allowed, super method should be used
		super.notifyDataSetChanged();
		*/
		}
	
	/**
	 * Modification of external data is not allowed.
	 * Original data source should be modified, and then reload should be forced.
	 * Loaded data will be changed at every reload (and any direct data changes will be lost).
	 * notifyDataSetChanged will throw an exception.
	 */
	@Override
	public void notifyDataSetChanged()
		{
		throw new UnsupportedOperationException("External data modification is not allowed! Reload will overwrite data!");
		}

	@Override
	public int getCount()
		{
		if (filteredEntries == null)
			return 0;
		
		return filteredEntries.size();
		}

	@Override
	public SampleEntry getItem( int position )
		{
		return filteredEntries.get( position );
		}

	@Override
	public long getItemId(int position)
		{
		return position;
		}

	@Override
	public int getViewTypeCount()
		{
		return 1;
		}

	@Override
	public int getItemViewType(int position)
		{
		return 0;
		}

	@Override
	public boolean areAllItemsEnabled()
		{
		return true;
		}

	@Override
	public boolean isEnabled(int position)
		{
		return true;
		}

	/**
	 * ViewHolder for Android View Holder Pattern
	 * http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
	 */
	private static class ViewHolder 
		{
		TextView longTextView;
		TextView stringTextView;
		}
	
	/** 
	 * {@inheritDoc}
	 * <p>
	 * {@link SampleEntry} cannot be null, 
	 * but it's data can contain a null String, in this case "- empty -" will be shown. 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
		{
		SampleEntry entry = getItem(position);
		ViewHolder viewHolder;
		
		if ( convertView == null )
			{
			convertView = layoutInflater.inflate( R.layout.sample_entry_row_view, null);
			
			viewHolder = new ViewHolder();
			viewHolder.longTextView = (TextView) convertView.findViewById( R.id.long_text_view );
			viewHolder.stringTextView = (TextView) convertView.findViewById( R.id.string_text_view );
			
			convertView.setTag(viewHolder);
			}
		else
			{
			viewHolder = (ViewHolder) convertView.getTag();
			}
		
		viewHolder.longTextView.setText( Long.toString( entry.getLong() ));
		viewHolder.stringTextView.setText( entry.getString() == null ? "- empty -" : entry.getString() );

		return convertView;
		}
	
	/** Copy of the previous constraint */
	private CharSequence previousConstraint;

	/** Counter for filterings. Only the last filtering can publish its results */
	private volatile int filterCounter = 0;
	
	/**
	 * Filter with the given constraint. Constraint will be stored for repeated filtering.
	 * @param constraint filtered list item contains this string
	 */
	public void filter(CharSequence constraint)
		{
		previousConstraint = constraint;
		filter();
		}
	
	/**
	 * Filter with the previous constraint
	 */
	public void filter()
		{
		filterCounter++; // not atomic, but this is not needed to be atomic.
						 // it's other readers will be started after this point
		getFilter().filter(previousConstraint);
		}
	
	// http://stackoverflow.com/a/13514663 - Search and Filter List
	public Filter getFilter()
		{
		Scribe.locus();

		if (entryFilter == null)
			entryFilter = new EntryFilter();

		return entryFilter;
		}	

	/**
	 * Filter contstrains data with the given sub-string
	 */
	private class EntryFilter extends Filter
		{
		/**
		 * Perform filtering operation on a background thread. 
		 * All entries containing constraint as sub-string will be selected. 
		 * No (empty or null) constraint means: no filtering.
		 * @param constraint the entries are checked for this sub-string, can be null
		 * @return result of the filtering (for {@link publishResults})
		 */
		protected FilterResults performFiltering(CharSequence constraint)
			{
			Scribe.locus();

			FilterResults filterResults = new FilterResults();

			// if filterCounter changes (new filtering) this filtering will be invalid 
			filterResults.count = filterCounter; 

			// data might be empty; OR constraint might be empty - no filtering needed
			if ( originalEntries == null || constraint == null || constraint.length() == 0 )
				{
				Scribe.debug("Filtering is not necessary, all entries will be included in the list");
				filterResults.values = originalEntries;
				}

			// all entries containing 'constraint' as sub-string will be copied to a new list
			// the new list will be returned for publishResults
			else
				{
				List<SampleEntry> filterList = new ArrayList<SampleEntry>();
				constraint = constraint.toString().toLowerCase( Locale.getDefault() );
				Scribe.debug("Filtering with <" + constraint + ">");

				try {
					for ( int i=0; i < originalEntries.size(); i++ )
						{
				    	// Sending progress
						Intent intent = new Intent(ProgressObserver.ACTION_STRING);
						intent.putExtra( ProgressObserver.DATA_WHO, ProgressObserver.FILTER );
						intent.putExtra( ProgressObserver.DATA_CYCLE, i+1 );
						intent.putExtra( ProgressObserver.DATA_MAX_CYCLES, originalEntries.size() );
						LocalBroadcastManager.getInstance( context ).sendBroadcast(intent);
						
						// New filtering has been started - stop this one!
						if ( filterCounter != filterResults.count )
							{
							Scribe.debug("Filter-cancel sign detected, loop cancelled!");
							throw new CancellationException();
							}
						
						if ( originalEntries.get(i).getString() != null && 
						     originalEntries.get(i).getString().toLowerCase( Locale.getDefault() ).contains( constraint ) )
							{
							filterList.add( originalEntries.get(i) );
							Scribe.debug("    + " + originalEntries.get(i).getString() + "[" + constraint + "]");
							}
						else
							Scribe.debug("    - " + originalEntries.get(i).getString() + "[" + constraint + "]");
	
						
						Rest.fraction(5000, originalEntries.size());
						}
	
					filterResults.values = filterList;
					}
				// Interesting - superclass will swallow all exceptions!?
				// InexOutofBounds expections can also occur - if we clear originalEntries 
				catch (Exception e)
					{
					// Other exceptions can arrive here - this will securely avoid publishing 
					filterResults.count = -1;
					
					Scribe.error("Filtering cancelled. Exception occured because of main-thread data change.");
					}
				finally
					{
				    // Finishing progress
					Intent intent = new Intent(ProgressObserver.ACTION_STRING);
					intent.putExtra( ProgressObserver.DATA_WHO, ProgressObserver.FILTER );
					intent.putExtra( ProgressObserver.DATA_MAX_CYCLES, -1 );
					LocalBroadcastManager.getInstance( context ).sendBroadcast(intent);
					}
				}
						
			return filterResults;
			}

		// http://stackoverflow.com/a/262416 - Type safety: Unchecked cast
		/**
		 * Invoked in the UI thread to publish the filtering results to filteredEntries.
		 * <p> 
		 * If new filtering has been started (filterResults.count differs from filterCounter) 
 		 * then {@link filteredEntries} can not be changed. 
		 * @return constraint the constraint used to filter the data
		 * @return filterResults the results of the filtering operation
		 */
		@SuppressWarnings("unchecked")
		protected void publishResults(CharSequence constraint, FilterResults filterResults)
			{
			Scribe.locus();

			// filterCounter has not been changed during filtering - this is the last filter request
			if ( filterResults.count == filterCounter )
				{
				filteredEntries = (List<SampleEntry>) filterResults.values;
				Scribe.debug("Filtering finished, filteredEntries changed, contains " + filterResults.count + " items. [" + constraint + "]" );

				MainListAdapter.super.notifyDataSetChanged();
				}
			// filterCounter (or filterRequest.count) has been changed - results are invalid
			else
				Scribe.debug("Filtering cancelled, filteredEntries remained.");
			}	
		}
	}

