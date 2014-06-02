package digitalgarden.filteredlistexample;

import java.util.Locale;

import digitalgarden.magicmerlin.scribe.Scribe;

/**
 * Instances of this class are containig the time of their construction,
 * and a sample string derived from this value. 
 * The class implements {@link Comparable}, using the string value for comparison.
 */
public class SampleEntry implements Comparable<SampleEntry>
	{	
	/** Sample strings used by {@link sampleString} */
	final private String[] SAMPLE_STRINGS = { 
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December" };
	
	/** Sample class-variable (long) */
	private long sampleLong;
	/** Sample class-variable (String) */
	private String sampleString;

	
	/** 
	 * Constructor
	 * <p>
	 * {@link sampleLong} will be the time of construction,
	 * {@link sampleString} contains one of the {@link SAMPLE_STRINGS},
	 * definied by the last digit of {@link sampleLong}.
	 */
	SampleEntry( )
		{
		sampleLong = System.currentTimeMillis();  
		sampleString = SAMPLE_STRINGS[ (int)(sampleLong % 12L) ];
		
		Scribe.debug("Sample entry " + sampleLong + " [" + sampleString + "] has been created.");
		}

	/** Value of {@link sampleLong} */
	long getLong()
		{
		return sampleLong;
		}

	/** Value of {@link sampleString} */
	String getString()
		{
		return sampleString;
		}

	/**
	 * Compares the {@link sampleString} values, converted to lower-case strings previously.
	 * 'null' value means an empty string.
	 */
	@Override
	public int compareTo( SampleEntry thatEntry ) 
		{
		String thisString;

		if ( getString() == null )
			thisString = "";
		else 
			thisString = getString().toLowerCase( Locale.getDefault() );

		String thatString;

		if ( thatEntry == null || thatEntry.getString() == null )
			thatString = "";
		else
			thatString = thatEntry.getString().toLowerCase( Locale.getDefault() );

		return thisString.compareTo( thatString );
		}
	}
