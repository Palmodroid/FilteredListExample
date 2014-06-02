package digitalgarden.filteredlistexample;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import digitalgarden.magicmerlin.scribe.Scribe;

public class MainListFragment extends ListFragment implements 
	AdapterView.OnItemLongClickListener // for long-click check
	{
	/** Data source - not reserved during restart*/
	private ArrayList<SampleEntry> entries = new ArrayList<SampleEntry>();
	
	// static factory method
	// http://www.androiddesignpatterns.com/2012/05/using-newinstance-to-instantiate.html
	 /**
	  * Creates a new MainListFragment instance
	  * (Parameters should be converted to arguments)
	  */
	public static ListFragment newInstance( )
		{
		ListFragment listFragmenet = new MainListFragment();
		/* args... can be used, too
		Bundle args = new Bundle();
		args.putString( "TITLE", exampleTitle);
		listFragmenet.setArguments(args);
		*/
		return listFragmenet;
		}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
		{
		Scribe.locus();
		super.onCreate(savedInstanceState);
		}
	
	
	private Button addButton;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
		Scribe.locus();
		//return super.onCreateView(inflater, container, savedInstanceState);
	
        View view = inflater.inflate(R.layout.main_list_fragment, container, false);
		addButton = (Button) view.findViewById(R.id.add_button);

        return view;
		}
			
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
		{
		Scribe.locus();
		super.onActivityCreated(savedInstanceState);
		
    	// Fragment has options menu
    	setHasOptionsMenu(true);

		setListAdapter( new MainListAdapter( getActivity(), entries) );

		// for long-click check
		getListView().setOnItemLongClickListener( this );

		//((TextView)(getListView().getEmptyView())).setText("Changed empty text");
		
		addButton.setOnClickListener( new View.OnClickListener()
			{
			@Override
			public void onClick( View view )
				{
				Scribe.debug("New SampleEntry added to entries by user.");
				entries.add( new SampleEntry() );
				((MainListAdapter)getListAdapter()).notifyDataSetChanged();
				}
			});
		}
	
	@Override
	public void onStart()
		{
		Scribe.locus();
		super.onStart();
		}
	
	@Override
	public void onResume()
		{
		Scribe.locus();
		super.onResume();		
		}
	
	@Override
	public void onPause()
		{
		Scribe.locus();
		super.onPause();
		}
	
	@Override
	public void onStop()
		{
		Scribe.locus();
		super.onStop();
		}
	
	@Override
	public void onDestroyView()
		{
		Scribe.locus();
		super.onDestroyView();
		}
	
	@Override
	public void onDestroy()
		{
		Scribe.locus();
		super.onDestroy();
 		}
	
	@Override
	public void onDetach()
		{
		Scribe.locus();
		super.onDetach();
		}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
		{
		Scribe.locus();
		// Menu first-time initialization
		
		inflater.inflate(R.menu.main_list_fragment, menu);
		// or
		// menu.add(R.string.clear_menu);
		}
	
	@Override
	public void onPrepareOptionsMenu( Menu menu )
		{
		Scribe.locus();
		// Menu can be changed here before each appear
		}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    	{
        switch (item.getItemId()) 
        	{
        	case R.id.clear:
        		Scribe.debug("Entries cleared from menu.");
        		entries.clear();
        		((MainListAdapter)getListAdapter()).notifyDataSetChanged();
	    		return true;

        	default:
            	return super.onOptionsItemSelected(item);
	        }
	    }
    
	@Override
	public void onListItemClick (ListView listView, View view, int position, long id)
		{
		Scribe.debug("List item " + position + " was SHORT clicked");
		Toast.makeText(getActivity(), "List item " + entries.get(position).getString() + " was SHORT clicked", Toast.LENGTH_LONG).show();
		}
	
	// for long-click check
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
		Scribe.debug("List item " + position + " was LONG clicked");
		Toast.makeText(getActivity(), "List item " + entries.get(position).getString() + " was LONG clicked", Toast.LENGTH_LONG).show();

		return true;
		}
	}
