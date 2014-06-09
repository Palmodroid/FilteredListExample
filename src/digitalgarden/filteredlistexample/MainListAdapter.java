package digitalgarden.filteredlistexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;

import android.content.Context;
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

	/** Copy of external source of data */
	private List<SampleEntry> originalEntries;
	/** List of filtered entries */
	private List<SampleEntry> filteredEntries;

	/** Filter */
	private EntryFilter entryFilter;

	
	/** Constructor */
	public MainListAdapter( Context context, ArrayList<SampleEntry> entries ) 
		{
		super();
		// context is only needed for layoutInflater
		this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.originalEntries = entries;
		this.filteredEntries = entries;
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
	
	/**
	 * Signs modification of external data set.
	 * At this point the program could cancel/ignore any filtering, and show all modified data.
	 * Supermethod should be called.
	 * (Implemented now.)
	 * <p>
	 * OR 
	 * <p>
	 * The program can force a new filtering. New data will be included in this filtering.  
	 * Filter will call the supermethod.
	 */
	@Override
	public void notifyDataSetChanged()
		{
		/*
		Scribe.debug("External data source was modified, filter ignored!");

		((EntryFilter)getFilter()).cancelFiltering();
		filteredEntries = originalEntries;
		super.notifyDataSetChanged();
		*/
		Scribe.debug("External data source was modified, filter forced to restart!");
		filter();
		// filter() will call super.notifyDataSetChanged()
		}

	/** Copy of the previous constraint */
	private CharSequence previousConstraint;

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
		((EntryFilter)getFilter()).cancelFiltering();
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
		/** True value will stop filtering. New filtering will set it to false before start. */
		private volatile boolean filteringCancelled;
		
		/** 
		 * Cancel filtering on the background thread
		 */
		public void cancelFiltering()
			{
			filteringCancelled = true;
			}
		
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
			filteringCancelled = false;

			// data might be empty
			if (originalEntries == null)
				{
				Scribe.debug("originalEntries is null, no filtering.");
				cancelFiltering();
				}

			// all entries containing 'constraint' as sub-string will be copied to a new list
			// the new list will be returned for publishResults
			else if (constraint != null && constraint.length() > 0 )
				{
				List<SampleEntry> filterList = new ArrayList<SampleEntry>();
				constraint = constraint.toString().toLowerCase( Locale.getDefault() );
				Scribe.debug("Filtering with <" + constraint + ">");

				try {
					for ( int i=0; i < originalEntries.size(); i++ )
						{
						if (filteringCancelled)
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
	
					filterResults.count = filterList.size();
					filterResults.values = filterList;
					}
				// Interesting - superclass will swallow all exceptions!?
				// InexOutofBounds expections can also occur - if we clear originalEntries 
				catch (Exception e)
					{
					Scribe.error("Filtering cancelled");
					}
				

/*				filteringCancelled = false;
				Iterator<SampleEntry> iterator = originalEntries.iterator();
				try {
					while ( iterator.hasNext() )
						{
						if (filteringCancelled)
							{
							Scribe.debug("Filter-cancel sign detected, throwing ConcurrentModificationException");
							throw new ConcurrentModificationException();
							}
						
						SampleEntry item = iterator.next();
						if ( item.getString() != null && 
						     item.getString().toLowerCase( Locale.getDefault() ).contains( constraint ) )
							{
							filterList.add( item );
							Scribe.debug("    + " + item.getString() + "[" + constraint + "]");
							}
						else
							Scribe.debug("    - " + item.getString() + "[" + constraint + "]");
						
						Rest.aBit();
						}
					
					filterResults.count = filterList.size();
					filterResults.values = filterList;
					}
				catch (ConcurrentModificationException cme)
					{
					Scribe.error("Concurrent Modification detected, filtering cancelled.");
					cancelFiltering();
					}
*/
				}
			
			// 'constraint' is empty; full dataset remains 
			else
				{
				Scribe.debug("constraint is empty, no filtering.");
				filterResults.count = originalEntries.size();
				filterResults.values = originalEntries;
				}
			
			return filterResults;
			}

		// http://stackoverflow.com/a/262416 - Type safety: Unchecked cast
		/**
		 * Invoked in the UI thread to publish the filtering results to filteredEntries.
		 * <p> 
		 * If {@link originalEntries} (and therefore filterResults.value) are null, 
		 * than the dataset might not be loaded yet (or filtering was cancelled). 
		 * In this case {@link filteredEntries} can not be changed, 
		 * because meanwhile background loading could be finished, 
		 * but the list will seem empty (because of the null-ed filteredEntries) 
		 * @return constraint the constraint used to filter the data
		 * @return filterResults the results of the filtering operation
		 */
		@SuppressWarnings("unchecked")
		protected void publishResults(CharSequence constraint, FilterResults filterResults)
			{
			Scribe.locus();

			if ( filterResults.values != null && !filteringCancelled)
				{
				filteredEntries = (List<SampleEntry>) filterResults.values;
				Scribe.debug("filteredEntries changed, contains " + filterResults.count + " items. [" + constraint + "]" );

				MainListAdapter.super.notifyDataSetChanged();
				}
			else
				Scribe.debug("filteredEntries remained, no changes");
			}	
		}

	}

