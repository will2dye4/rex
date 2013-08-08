package edu.gatech.cs3240.project1;

/**
 * Implementation of the Token interface.
 * @author William Dye
 */
public class TokenImpl implements Token
{

	private TokenType type;
	private String string;

    /**
     * Default constructor. Creates a <code>TokenImpl</code> with <code>null</code> type and value.
     */
	public TokenImpl()
	{
		this(null, null);
	}

    /**
     * Creates a <code>TokenImpl</code> with the specified <code>type</code> and <code>null</code> value.
     * @param type the type of the token
     */
	public TokenImpl(TokenType type)
	{
		this(type, null);
	}

    /**
     * Creates a <code>TokenImpl</code> with the specified <code>type</code> and <code>string</code>.
     * @param type the type of the token
     * @param string the string value of the token
     */
	public TokenImpl(TokenType type, String string)
	{
		this.type = type;
		this.string = string;
	}

    @Override
	public TokenType getTokenType()
	{
		return type;
	}

    @Override
	public String getTokenString()
	{
		return string;
	}

}
