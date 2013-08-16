package com.williamdye.rex.project2;

/**
 * Represents a node in an abstract syntax tree.
 * @author William Dye
 */
public interface ASTNode
{

    /**
     * Accessor for an AST node's type.
     * @return the type of the node
     */
    public ASTNodeType getNodeType();

    /**
     * Accessor for the type of the token associated with an AST node.
     * Provided for convenience, to eliminate calls such as <code>anASTNode.getToken().getType()</code>.
     * @return the type of the node's token
     */
    public MiniRETokenType getTokenType();

    /**
     * Accessor for the token string associated with an AST node.
     * @return the node's token string
     */
    public String getTokenString();

    /**
     * Accessor for an AST node's next (or sibling) node.
     * @return the node's next node, or <code>null</code> if the node has no siblings
     */
    public ASTNode getNext();
    
    /**
     * Tests if a node has a next sibling.
     * @return <code>true</code> if the node has a sibling, <code>false</code> otherwise
     */
    public boolean hasNext();

    /**
     * Mutator for an AST node's next (or sibling) node.
     * @param next the node's new next node
     */
    public void setNext(ASTNode next);

    /**
     * Accessor for an AST node's child node.
     * @return the node's child node, or <code>null</code> if the node is a leaf
     */
    public ASTNode getChild();

    /**
     * Mutator for an AST node's child node.
     * @param child the node's new child node
     */
    public void setChild(ASTNode child);

}
