package edu.gatech.cs3240.project2;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementation of the AST interface.
 * @author William Dye
 */
public class ASTImpl implements AST
{

    protected ASTNode start;
    protected Set<ASTNode> nodes;

    /**
     * Creates an <code>ASTImpl</code> with no starting node.
     */
    public ASTImpl()
    {
        this(null);
    }

    /**
     * Constructs an <code>ASTImpl</code> with the specified starting node.
     * @param initial the starting node for the AST
     */
    public ASTImpl(ASTNode initial)
    {
        start = initial;
        nodes = new LinkedHashSet<ASTNode>();
        if (start != null)
            nodes.add(start);
    }

    @Override
    public ASTNode getStartNode()
    {
        return start;
    }

    @Override
    public void setStartNode(ASTNode initial)
    {
        nodes.remove(start);
        start = initial;
        nodes.add(start);
    }

}
