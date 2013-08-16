package com.williamdye.rex.project2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the Metadata interface.
 * @author TJ Harrison
 */
public class MetadataImpl implements Metadata 
{

	private String filename;
	private LinkedHashSet<List<Integer>> matches;

    /**
     * Constructs a new <code>MetadataImpl</code> for the specified <code>file</code>,
     * with an initial match at the specified <code>line</code> and <code>index</code>.
     * @param file the file that this metadata represents
     * @param line the line number of the first match
     * @param index the index (within the line) of the first match
     */
	public MetadataImpl(String file, int line, int index)
	{
		filename = file;
        matches = new LinkedHashSet<List<Integer>>();
        if (line > 0)
		    addMatch(line, index);
	}

	@Override
	public void addMatch(int line, int index)
	{
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(line);
        list.add(index);
		matches.add(list);
	}

	@Override
	public String getFile() 
	{
		return filename;
	}

	@Override
	public Set<List<Integer>> getMatches()
	{
		return matches;
	}

}
