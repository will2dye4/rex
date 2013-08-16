package com.williamdye.rex.project2;

import java.util.List;

/**
 * Represents a string along with it's given Metadata for each file.
 * @author TJ Harrison
 */
public interface MiniREString 
{	
	/**
     * Accessor for an MiniREString's string.
     * @return the string represented by this MiniREString
     */
	public String getString();
	
	/**
     * Accessor for a MiniREString's list of metadata.
     * @return ar array list of metadata for this MiniREString
     */
	public List<Metadata> getAllMetadata();
	
	/**
     * Adds the given metadata to a MiniREString's list
     * @param meta the metadata to add to this MiniREString
     */
	public void addMetadata(Metadata meta);
	
	/**
	 * Adds all the given metadata in the specified list
     * @param metas the list of metadata to add
	 */
	public void addAllMetadata(List<Metadata> metas);

}
