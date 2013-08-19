package com.williamdye.rex.string;

import java.util.*;

/**
 * Implementation of the MiniREString interface.
 * @author TJ Harrison
 */
public class MiniREStringImpl implements MiniREString 
{

	private String string;
	private ArrayList<Metadata> meta;

    /**
     * Constructs a new <code>MiniREStringImpl</code> with the specified <code>string</code>.
     * @param string the string represented by this <code>MiniREStringImpl</code>
     */
	public MiniREStringImpl(String string)
	{
		this(string, null);
	}

    /**
     * Constructs a new <code>MiniREStringImpl</code> with the specified <code>string</code> and metadata.
     * @param string the string represented by this <code>MiniREStringImpl</code>
     * @param m the first metadata for this <code>MiniREStringImpl</code>
     */
	public MiniREStringImpl(String string, Metadata m)
	{
		this.string = string;
        this.meta = new ArrayList<Metadata>();
        if (m != null)
		    this.meta.add(m);
	}

	
	@Override
	public String getString()
	{
		return string;
	}
	
	@Override
	public List<Metadata> getAllMetadata()
	{
		return meta;
	}
	
	@Override
	public void addMetadata(Metadata m)
	{
		meta.add(m);
	}

    @Override
	public void addAllMetadata(List<Metadata> metas)
	{
		meta.addAll(metas);
	}
	
	@Override
	public boolean equals(Object o)
	{
        boolean same;
        if (o == null || o.getClass() != getClass())
            same = false;
        else if (o == this)
            same = true;
        else
		    same = getString().equals(((MiniREStringImpl) o).getString());
        return same;
	}

}
