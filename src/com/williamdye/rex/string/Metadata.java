package com.williamdye.rex.string;

import java.util.*;

/**
 * Represents a list of indices in a certain file that pertain to a certain string.
 * @author TJ Harrison
 */
public interface Metadata 
{

	/**
	 * Adds a given index to the list of indices.
	 * @param line the line number of the match
     * @param index the index within the line
	 */
	public void addMatch(int line, int index);
	
	/**
	 * Accessor method for the filename for this metadata.
	 * @return the file this Metadata is representing 
	 */
	public String getFile();
	
	/**
	 * Accessor method for this Metadata's list of matches.
	 * @return the list of indices for this Metadata
	 */
	public Set<List<Integer>> getMatches();

}
