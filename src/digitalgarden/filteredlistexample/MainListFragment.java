package digitalgarden.filteredlistexample;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import digitalgarden.magicmerlin.scribe.Scribe;
import digitalgarden.magicmerlin.utils.Keyboard;

public class MainListFragment extends ListFragment implements 
	LoaderManager.LoaderCallbacks<List<SampleEntry>>, 
	ProgressObserver.OnProgressListener, 
	AdapterView.OnItemLongClickListener // for long-click check
	{
	/** ID for Loader */
	private final int LOADER_ID = 1;

	/** Registered progressObserver */
	private ProgressObserver progressObserver;

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
	
	
	private Button refreshButton;
	private EditText filter;

	private ProgressBar loaderProgressBar;
	private TextView loaderProgress;
	private ProgressBar filterProgressBar;
	private TextView filterProgress;
	
	private ProgressBar centralProgressBar;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
		Scribe.locus();
		//return super.onCreateView(inflater, container, savedInstanceState);
	
        View view = inflater.inflate(R.layout.main_list_fragment, container, false);

        refreshButton = (Button) view.findViewById(R.id.refresh_button);

        loaderProgressBar = (ProgressBar) view.findViewById(R.id.loader_progress_bar);
        loaderProgress = (TextView) view.findViewById(R.id.loader_progress);
        filterProgressBar = (ProgressBar) view.findViewById(R.id.filter_progress_bar);
        filterProgress = (TextView) view.findViewById(R.id.filter_progress);

        centralProgressBar = (ProgressBar) view.findViewById(R.id.central_progress_bar);
        
		filter = (EditText) view.findViewById(R.id.filter);
		filter.addTextChangedListener(new TextWatcher() 
			{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
				Scribe.debug("Filter text was changed!");
				((MainListAdapter)getListAdapter()).filter( s );
				}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
			});
		
        return view;
		}
			
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
		{
		Scribe.locus();
		super.onActivityCreated(savedInstanceState);
		
    	// Fragment has options menu
    	setHasOptionsMenu(true);

    	// Set up adapter
    	setListAdapter( new MainListAdapter( getActivity() ) );

    	// Data will be loaded by Loader, Loader will be controlled by LoaderManager
		getLoaderManager().initLoader( LOADER_ID , null, this);

		// for long-click check
		getListView().setOnItemLongClickListener( this );

		getListView().setOnTouchListener( new OnTouchListener()
			{
			@Override
			public boolean onTouch(View v, MotionEvent event)
				{
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
					Keyboard.hide( getActivity() );
				return false;
				}
			});
			 
		//((TextView)(getListView().getEmptyView())).setText("Changed empty text");
		
		refreshButton.setOnClickListener( new View.OnClickListener()
			{
			@Override
			public void onClick( View view )
				{
				Scribe.debug("Simulated data changes - sending broadcast");
				Intent intent = new Intent("DatasetChanged");
				LocalBroadcastManager.getInstance( getActivity() ).sendBroadcast(intent);
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
		
		if (progressObserver == null)
			{
			Scribe.debug("Progress observation was registered.");
			progressObserver = new ProgressObserver(getActivity(), this);
			}
		
		}
	
	@Override
	public void onPause()
		{
		Scribe.locus();
		super.onPause();
		
	    if ( progressObserver != null ) 
	    	{
	    	LocalBroadcastManager.getInstance( getActivity() ).unregisterReceiver( progressObserver );
	    	progressObserver = null;
			Scribe.debug("Progress observation was unregistered.");
			
			// If last messege arrives during paused state, then progress indicators could remain !!
	    	}

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
        	case R.id.restart:
        		Scribe.debug("Loader restart forced from menu.");
        		getLoaderManager().restartLoader( LOADER_ID , null, this);
	    		return true;

        	default:
            	return super.onOptionsItemSelected(item);
	        }
	    }
    
	@Override
	public void onListItemClick (ListView listView, View view, int position, long id)
		{
		Scribe.debug("List item " + position + " was SHORT clicked");
		Toast.makeText(getActivity(), "List item " + ((SampleEntry)(getListAdapter().getItem(position))).getString() + " was SHORT clicked", Toast.LENGTH_LONG).show();
		}
	
	// for long-click check
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
		Scribe.debug("List item " + position + " was LONG clicked");
		Toast.makeText(getActivity(), "List item " + ((SampleEntry)(getListAdapter().getItem(position))).getString() + " was LONG clicked", Toast.LENGTH_LONG).show();

		return true;
		}
	
	@Override
	public Loader<List<SampleEntry>> onCreateLoader(int id, Bundle args)
		{
	    Scribe.locus();
	    return new SampleEntryLoader(getActivity()); 
		}

	@Override
	public void onLoadFinished(Loader<List<SampleEntry>> loader, List<SampleEntry> data)
		{
	    Scribe.locus();
	    ((MainListAdapter)getListAdapter()).setData( data );
	    
		centralProgressBar.setVisibility( View.GONE );
		}

	@Override
	public void onLoaderReset(Loader<List<SampleEntry>> loader)
		{
		Scribe.locus();
		((MainListAdapter)getListAdapter()).setData( null );
		}

	public void onProgress(int who, int maxCycles, int cycle)
		{
		if (who == ProgressObserver.LOADER)
			{
			if ( maxCycles < 0 )
				{
				loaderProgressBar.setVisibility( View.GONE );
				loaderProgress.setVisibility( View.GONE );
				
				getActivity().setProgressBarIndeterminateVisibility( false );
				}
			else
				{
				loaderProgressBar.setVisibility( View.VISIBLE );
				loaderProgress.setVisibility( View.VISIBLE );
				loaderProgress.setText("Loader: " + cycle + "/" + maxCycles);
				
				centralProgressBar.setVisibility( View.VISIBLE );
				getActivity().setProgressBarIndeterminateVisibility( true );
				}
			}
		if (who == ProgressObserver.FILTER)
			{
			if ( maxCycles < 0 )
				{
				filterProgressBar.setVisibility( View.GONE );
				filterProgress.setVisibility( View.GONE );
				}
			else
				{
				filterProgressBar.setVisibility( View.VISIBLE );
				filterProgress.setVisibility( View.VISIBLE );
				filterProgress.setText("Filter: " + cycle + "/" + maxCycles);
				}
			}
		}
	}
