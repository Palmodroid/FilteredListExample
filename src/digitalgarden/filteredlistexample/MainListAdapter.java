package digitalgarden.filteredlistexample;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Simple list-adapter extends {@link BaseAdapter}
 * Data source is an external {@code ArrayList} of {@link SampleEntry}-s 
 */
public class MainListAdapter extends BaseAdapter
	{
	/** Needed by {@link getView}, set by constructor (using {@link Context}) */
	private LayoutInflater layoutInflater;

	/** Copy of external source of data */
	private ArrayList<SampleEntry> entries;
	
	/** Constructor */
	public MainListAdapter( Context context, ArrayList<SampleEntry> entries ) 
		{
		super();
		// context is only needed for layoutInflater
		this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.entries = entries;
		}

	@Override
	public int getCount()
		{
		if (entries == null)
			return 0;
		
		return entries.size();
		}

	@Override
	public SampleEntry getItem( int position )
		{
		return entries.get( position );
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
	}

