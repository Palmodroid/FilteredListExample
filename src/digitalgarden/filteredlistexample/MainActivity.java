package digitalgarden.filteredlistexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import digitalgarden.magicmerlin.scribe.Scribe;
import digitalgarden.magicmerlin.utils.Keyboard;


public class MainActivity extends FragmentActivity
	{
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
		Scribe.locus();
		super.onActivityResult(requestCode, resultCode, data);
		}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
		// Scribe for traditional log - not in use
		
		//Scribe.dumpSysLog();
		//Scribe.clearSysLog();
		
		
		// Scribe init
		Scribe.init(this);
		Scribe.checkLogFileLength();
		Scribe.logUncaughtExceptions();
		
		Scribe.title("FilteredListExample started.");
		Scribe.locus();
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);
		}

	@Override
	protected void onStart()
		{
		Scribe.locus();
		super.onStart();
		}
	
	@Override
	protected void onResume()
		{
		Scribe.locus();
		super.onResume();
		}
		
	@Override
	protected void onResumeFragments()
		{
		Scribe.locus();
		super.onResumeFragments();
		
		FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment mainListFragment = fragmentManager.findFragmentByTag("LIST");
		if (mainListFragment == null)
			{
			mainListFragment = MainListFragment.newInstance( );

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add( R.id.main_frame, mainListFragment, "LIST" );
			fragmentTransaction.commit();

			// New fragment always starts without keyboard
			Keyboard.hide( this );
			
			Scribe.debug("New LIST Fragment was created, added");
			}
		else
			{
			Scribe.debug("Old LIST Fragment was found");
			}		
		}
	
	@Override
	protected void onPause()
		{
		Scribe.locus();
		super.onPause();
		}
	
	@Override
	protected void onStop()
		{
		Scribe.locus();
		super.onStop();
		}
	
	@Override
	protected void onDestroy()
		{
		Scribe.locus();
		super.onDestroy();
		}

	
	@Override
	public void onBackPressed()
		{
		Scribe.locus();
		super.onBackPressed();
		}

	
	@Override
	public void onSaveInstanceState(Bundle outState)
		{
		Scribe.locus();
		super.onSaveInstanceState(outState);
		}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
		{
		Scribe.locus();
		super.onRestoreInstanceState(savedInstanceState);
		}
	

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
		{
		Scribe.locus();
		// Menu first-time initialization
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity, menu);
		// or
		// menu.add(R.id.about);
		return true;
		}

	@Override
	public boolean onPrepareOptionsMenu( Menu menu )
		{
		Scribe.locus();
		// Menu can be changed here before each appear
		
		return true; // true - menu will be displayed; false - menu will be omitted
		}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) 	
		{
		Scribe.locus();
		
		switch (item.getItemId())
			{ 	
			case R.id.about:
				{
				Scribe.debug("Menu 'About' was selected");
				Toast.makeText( this, "This is just a sample list.", Toast.LENGTH_LONG).show();		
				return true; 	
				}
			default: 	
				return super.onOptionsItemSelected(item); 	 
			}
		}
	}
