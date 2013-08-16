package com.williamdye.rex.project2;

import com.williamdye.rex.project1.*;

/**
 * Implementation of the MiniREToken interface.
 * @author William Dye
 */
public class MiniRETokenImpl extends TokenImpl implements MiniREToken
{

    private MiniRETokenType type;

    /**
     * Creates a <code>MiniRETokenImpl</code> with the specified <code>type</code> and <code>null</code> value.
     * @param type the type of the token
     */
    public MiniRETokenImpl(MiniRETokenType type)
    {
        this(type, (type == null ? null : type.getString()));
    }

    /**
     * Creates a <code>MiniRETokenImpl</code> with the specified <code>tokenType</code> and <code>string</code>.
     * @param tokenType the type of the token
     * @param string the string value of the token
     */
    public MiniRETokenImpl(MiniRETokenType tokenType, String string)
    {
        super(null, string);
        type = tokenType;
    }

    @Override
    public MiniRETokenType getType()
    {
        return type;
    }

    @Override
    public TokenType getTokenType()
    {
        throw new IllegalStateException("getTokenType() called on MiniRETokenImpl -- use getType() instead");
    }

}
