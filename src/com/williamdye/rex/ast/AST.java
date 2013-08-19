package com.williamdye.rex.ast;

/**
 * Represents an abstract syntax tree.
 * @author William Dye
 */
public interface AST
{

    /**
     * Accessor for an AST's starting node.
     * @return the AST's start node
     */
    public ASTNode getStartNode();

    /**
     * Mutator for an AST's starting node.
     * @param start the new starting node for the AST
     */
    public void setStartNode(ASTNode start);

}
