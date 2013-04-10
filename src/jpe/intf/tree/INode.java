// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.intf.tree;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * Representation of a node on a tree.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public interface INode {
    /**
     * Enumeration describing the different types of nodes.
     */
    enum Type {
	/**
	 * The type for an INode that is the child of a LINK node, whose underlying type (BRANCH, LEAF, LINK) has not
	 * yet been determined.
	 */
	UNRESOLVED(0),
	/**
	 * The type for an INode with no children.
	 */
	LEAF(1),
	/**
	 * The type for an INode that has children.
	 */
	BRANCH(2),
	/**
	 * The type for an ITree (i.e., a root node).
	 */
	TREE(3),
	/**
	 * The type for an IForest.
	 */
	FOREST(4),
	/**
	 * The type for an INode which is a link to another INode.
	 */
	LINK(5);

	private short val;

	Type(int val) {
	    this.val = (short)val;
	}

	public short getVal() {
	    return val;
	}

	public static Type fromVal(int val) {
	    switch(val) {
	    case 0:
		return UNRESOLVED;
	      case 2:
		return BRANCH;
	      case 3:
		return TREE;
	      case 4:
		return FOREST;
	      case 5:
		return LINK;
	      default:
	      case 1:
		return LEAF;
	    }
	}
    }

    /**
     * Return the tree in which this node resides.
     */
    ITree getTree();

    /**
     * Get all the children of this node. This method will follow links wherever they might lead. If you want to remain
     * inside of an ITree, then the best-practice is to ITree.lookup(child.getPath()) each child returned by this method.
     *
     * @throws UnsupportedOperationException if the node is of Type.LEAF.
     * @throws NoSuchElementException if this is a node of Type.LINK, but the destination doesn't exist.
     */
    Collection<INode> getChildren() throws NoSuchElementException, UnsupportedOperationException;

    /**
     * Get all the children of this node whose names match the specified Pattern.
     *
     * @throws UnsupportedOperationException if the node is of Type.LEAF.
     * @throws NoSuchElementException if this is a node of Type.LINK, but the destination doesn't exist.
     */
    Collection<INode> getChildren(Pattern p) throws NoSuchElementException, UnsupportedOperationException;

    /**
     * Get a child with a specific name.
     *
     * @throws UnsupportedOperationException if the node is of Type.LEAF.
     * @throws NoSuchElementException if there is no child with the specified name.
     */
    INode getChild(String name) throws NoSuchElementException, UnsupportedOperationException;

    /**
     * Get the name of this node.
     */
    String getName();

    /**
     * Get the path traversed in the tree to reach this node.
     */
    String getPath();

    /**
     * Get the absolute path of this node, irrespective of how it might have been reached using links.
     */
    String getCanonicalPath();

    /**
     * Get the type of this node.
     */
    Type getType();

    /**
     * Test whether or not the node has children.  This is particularly useful for a node of Type.ROOT or Tyoe.LINK, neither
     * of which is a BRANCH nor a LEAF.
     *
     * @throws NoSuchElementException if this is a node of Type.LINK, but the destination doesn't exist.
     */
    boolean hasChildren() throws NoSuchElementException;
}
