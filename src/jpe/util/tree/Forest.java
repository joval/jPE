// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jpe.util.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.regex.Pattern;

import jsaf.util.StringTools;

import jpe.intf.tree.IForest;
import jpe.intf.tree.ITree;
import jpe.intf.tree.INode;

/**
 * Generic implementation of a forest.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class Forest implements IForest, Iterable<ITree> {
    private String name, delimiter;
    private Hashtable<String, ITree> trees;

    public Forest(String name, String delimiter) {
	this.name = name;
	this.delimiter = delimiter;
	trees = new Hashtable<String, ITree>();
    }

    public Tree makeTree(String name) {
	Tree tree = new Tree(this, name, new Hashtable<String, Node>(), new Hashtable<String, String>());
	addTree(tree);
	return tree;
    }

    // Implement INode

    public ITree getTree() {
	return null;
    }

    public String getName() {
	return name;
    }

    public String getPath() {
	return "forest://" + name;
    }

    public String getCanonicalPath() {
	return getPath();
    }

    public Type getType() {
	return Type.FOREST;
    }

    public INode getChild(String name) throws NoSuchElementException {
	return getTree(name);
    }

    public Collection<INode> getChildren() {
	return getChildren(null);
    }

    public Collection<INode> getChildren(Pattern p) {
	Collection<INode> children = new Vector<INode>();
	for (ITree tree : this) {
	    if (p == null || p.matcher(tree.getName()).find()) {
		children.add(tree);
	    }
	}
	return children;
    }

    public boolean hasChildren() {
	return trees.size() > 0;
    }

    // Implement ITree

    public int size() {
	int count = 0;
	for (ITree tree : this) {
	    count += tree.size();
	    count++; // include the tree itself as a node
	}
	return count;
    }

    public Collection<String> search(Pattern p, boolean followLinks) {
	Collection<String> result = new Vector<String>();
	for (ITree tree : this) {
	    result.addAll(tree.search(p, followLinks));
	}
	return result;
    }

    public INode lookup(String path) {
	INode result = null;
	for (ITree tree : this) {
	    try {
		return ((Tree)tree).lookup(path);
	    } catch (NoSuchElementException e) {
	    }
	}
	throw new NoSuchElementException(path);
    }

    // Implement IForest

    public String getDelimiter() {
	return delimiter;
    }

    public ITree addTree(ITree tree) throws IllegalArgumentException {
	for (ITree t : this) {
	    //
	    // Make sure none of the existing trees have this tree as a path
	    //
	    try {
		Node node = (Node)t.lookup(tree.getName());
		switch(node.getType()) {
		  case TREE:
		    // ignore - the target is being replaced
		    break;
		  case LEAF:
		    ((Tree)node.getTree()).remove(node);
		    break;
		  default:
		    throw new IllegalArgumentException("Cannot add tree " + tree.getName() + " because tree " + t.getName() +
						       " has existing node of type " + node.getType());
		}
	    } catch (NoSuchElementException e) {
		// no problem
	    }

	    //
	    // Make sure this tree has none of the existing trees as a path
	    //
	    try {
		Node node = (Node)tree.lookup(t.getName());
		switch(node.getType()) {
		  case TREE:
		    // ignore - the target is being replaced
		    break;
		  case LEAF:
		    ((Tree)node.getTree()).remove(node);
		    break;
		  default:
		    throw new IllegalArgumentException("Cannot add tree " + tree.getName() + " because it contains the node " +
						       t.getName() + " of type " + node.getType());
		}
	    } catch (NoSuchElementException e) {
		// no problem
	    }
	}

	return trees.put(tree.getName(), tree);
    }

    public ITree getTree(String name) throws NoSuchElementException {
	if (trees.containsKey(name)) {
	    return trees.get(name);
	} else {
	    throw new NoSuchElementException(name);
	}
    }

    // Implement Iterable

    public Iterator<ITree> iterator() {
	return trees.values().iterator();
    }
}
