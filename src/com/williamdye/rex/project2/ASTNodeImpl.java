package com.williamdye.rex.project2;

/**
 * Implementation of the ASTNode interface.
 * @author William Dye
 */
public class ASTNodeImpl implements ASTNode
{

    protected ASTNodeType type;
    protected MiniRETokenType tokenType;
    protected String tokenString;
    protected ASTNode next;
    protected ASTNode child;

    /**
     * Constructs an <code>ASTNodeImpl</code> with the specified node type and no token.
     * @param type the type of the node
     */
    public ASTNodeImpl(ASTNodeType type)
    {
        this(null, type);
    }

    /**
     * Creates an <code>ASTNodeImpl</code> with the specified token and node type.
     * @param tok the token associated with the node
     * @param nodeType the type of the node
     */
    public ASTNodeImpl(MiniREToken tok, ASTNodeType nodeType)
    {
        type = nodeType;
        if (tok == null) {
            tokenType = null;
            tokenString = null;
        } else {
            tokenType = tok.getType();
            tokenString = tok.getTokenString();
        }
        next = null;
        child = null;
    }

    @Override
    public ASTNodeType getNodeType()
    {
        return type;
    }

    @Override
    public MiniRETokenType getTokenType()
    {
        return tokenType;
    }

    @Override
    public String getTokenString()
    {
        return tokenString;
    }

    @Override
    public ASTNode getNext()
    {
        return next;
    }
    
    @Override
    public boolean hasNext()
    {
    	return (next != null);
    }

    @Override
    public void setNext(ASTNode newNext)
    {
        next = newNext;
    }

    @Override
    public ASTNode getChild()
    {
        return child;
    }

    @Override
    public void setChild(ASTNode newChild)
    {
        child = newChild;
    }

}
