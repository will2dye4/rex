package edu.gatech.cs3240.project2;

import java.text.ParseException;
import java.util.*;
import java.io.*;

import edu.gatech.cs3240.project1.*;

/**
 * Evaluates ASTs by performing the operations specified by the trees' nodes.
 * @author William Dye
 * @author TJ Harrison
 * @author Taylor Holden
 * @author Matthew Hooper
 * @author Ryan McCaffrey
 */
public class ASTEvaluator
{

	protected Map<String, List<MiniREString>> matchLists;
    protected Map<String, Integer> ints;
    protected AST tree;
	
    public ASTEvaluator(AST ast)
    {
        tree = ast;
        matchLists = new LinkedHashMap<String, List<MiniREString>>();
        ints = new LinkedHashMap<String, Integer>();
    }

    /**
     * Evaluates the specified AST.
     */
    public void evaluate()
    {
    	ASTNode curNode = tree.getStartNode();
    	while(curNode != null) {
            evaluateNode(curNode);
            curNode = curNode.getNext();
    	}
    }

    /* Helper method for evaluate(). Evaluates a single node in the tree. */
	private void evaluateNode(ASTNode node)
    {
        boolean recursive = false;
        switch (node.getNodeType()) {
            case BEGIN:
            case END:
                break;  /* do nothing */
            case ASSIGN:
           	    String id = node.getChild().getTokenString(); /* get identifier name */
                ASTNode value = node.getChild().getNext();
                if (ASTNodeType.COUNT == value.getNodeType())
			        ints.put(id, evaluateCountNode(value));
                else if (ASTNodeType.MAX_FREQ_STRING == value.getNodeType())
                    matchLists.put(id, evaluateMaxFreqStringNode(value));
                else if (ASTNodeType.IDENTIFIER == value.getNodeType())
                    matchLists.put(id, matchLists.get(value.getTokenString()));
                else
                    matchLists.put(id, evaluateFindNode(value));
			    break;
		    case PRINT:
			    ASTNode child = node.getChild();
			    List<MiniREString> restrings;
			    while (child != null) {
                    String name = child.getTokenString();
                    if (matchLists.containsKey(name)) {
				        restrings = matchLists.get(name);
                        for (MiniREString str : restrings)
					        System.out.print(str.getString() + " ");
                        System.out.print("\n");
                    } else if (ints.containsKey(name))
                        System.out.println(ints.get(name));
                    else
				        System.out.println("Invalid identifier: \"" + name + "\"");
				    child = child.getNext();
			    }
                break;
		    case RECURSIVE_REPLACE:
			    recursive = true;
                /* fall through intended */
		    case REPLACE:
                ASTNode filenames = node.getChild().getNext().getNext();
                String source = filenames.getChild().getTokenString();
                String dest = filenames.getChild().getNext().getTokenString();
                String ascii = node.getChild().getNext().getTokenString();
			    ASTNode findNode = new ASTNodeImpl(ASTNodeType.FIND); /* create new find node to find the matches of the regex */
			    findNode.setChild(node.getChild()); /* add regex child */
			    findNode.getChild().setNext(filenames.getChild()); /* add source filename */
			    List<MiniREString> matches = find(findNode);
                try {
                    replace(matches, ascii, source, dest, recursive);
                } catch (IOException ex) {
                    System.out.println("Caught I/O exception during replace: " + ex.getLocalizedMessage());
                }
			    break;
		    default:
			    throw new IllegalStateException("AST contains illegal node at top level: " + node.getNodeType());
        }
    }

    /* Helper method for evaluating count nodes. */
    private Integer evaluateCountNode(ASTNode node)
    {
        List<MiniREString> strings;
        if (ASTNodeType.IDENTIFIER == node.getChild().getNodeType()) /* an identifier to count */
			strings = matchLists.get(node.getChild().getTokenString());
		else /* an expression to count */
            strings = evaluateFindNode(node.getChild());

		int count = 0;
		for (MiniREString string : strings) {
			for (Metadata meta : string.getAllMetadata())
				count += meta.getMatches().size();
		}
		return count;
    }

    /* Helper method for evaluating find nodes. */
    private List<MiniREString> evaluateFindNode(ASTNode node)
    {
        List<MiniREString> list;
        if(node.getChild().getNext().hasNext()) {
			ASTNode opNode = node.getChild().getNext().getNext();
			switch(opNode.getTokenType()) {
			    case DIFF:
				    list = diffOp(find(node), evaluateFindNode(opNode.getChild()));
                    break;
			    case INTERS:
				    list = intersOp(find(node), evaluateFindNode(opNode.getChild()));
                    break;
			    case UNION:
                    list = unionOp(find(node), evaluateFindNode(opNode.getChild()));
                    break;
			    default:
		    	    throw new IllegalStateException("Find statement has illegal child " + opNode.getTokenType());
			}
		} else
            list = find(node);
        return list;
    }

    /* Helper method for evaluating the most frequent string. */
    private List<MiniREString> evaluateMaxFreqStringNode(ASTNode node)
    {
        List<MiniREString> strings = matchLists.get(node.getChild().getTokenString());
        List<MiniREString> result = new ArrayList<MiniREString>();
		if (strings == null || strings.isEmpty())
			result.add(new MiniREStringImpl("No matches found."));
		else {
		    MiniREString max = strings.get(0);
		    int counter = 0;
		    for (MiniREString s : strings) {
			    for (Metadata m : s.getAllMetadata()) {
				    if (m.getMatches().size() >= counter) {
					    max = s;
					    counter = m.getMatches().size();
				    }
			    }
		    }
		    result.add(max);
        }
		return result;
    }
    
    /* Helper method for evaluating the intersection of two sets. */
    private List<MiniREString> intersOp(List<MiniREString> a, List<MiniREString> b)
    {
    	List<MiniREString> intersection = new ArrayList<MiniREString>();
    	
    	for(MiniREString s : a) {
    		if (b.contains(s)) {
    			List<Metadata> bMetas = b.get(b.indexOf(s)).getAllMetadata();
    			s.addAllMetadata(bMetas);
    			intersection.add(s);
    		}
    	}
    	return intersection;
    }
    
    /* Helper method for evaluating the difference of two sets. */
    private List<MiniREString> diffOp(List<MiniREString> a, List<MiniREString> b)
    {
    	List<MiniREString> difference = new ArrayList<MiniREString>();
    	
    	for (MiniREString s : a) {
    		if (!b.contains(s))
    			difference.add(s);
    	}
    	return difference;
    }
    
    /* Helper method for evaluating the union of two sets. */
    private List<MiniREString> unionOp(List<MiniREString> a, List<MiniREString> b)
    {
    	List<MiniREString> union = new ArrayList<MiniREString>();
    	
    	for(MiniREString s : a)
    		union.add(s);
    	
    	for(MiniREString s : b)
    	{
    		if (!union.contains(s))
    			union.add(s);
    		else {
    			MiniREString str = union.get(union.indexOf(s));
    			str.addAllMetadata(s.getAllMetadata());
    		}
    	}
    	return union;
    }
    
    /* Implements the find function. */
    private List<MiniREString> find(ASTNode node)
    {
    	List<MiniREString> list = new ArrayList<MiniREString>();
        String regex = node.getChild().getTokenString(); /* get string representation of regex from first child */
        String filename = node.getChild().getNext().getTokenString();
		RecursiveDescentParser parser = new RecursiveDescentParserImpl(new TokenizerImpl(regex), new LinkedHashSet<NFA>());
        NFA nfa;
		try {
			nfa = parser.parse();
		} catch (ParseException e) {
            System.out.println("Invalid regular expression: \"" + regex + "\"" + "(" + e.getMessage() + ")");
			return list;
		}
        Map<String, Set<State>> map = new LinkedHashMap<String, Set<State>>();
        map.put(MiniRETableWalkerImpl.MATCH_TOKEN, nfa.getAcceptingStates());
		DFA dfa = NFAToDFAConverter.convert(nfa, map);

        File file = new File(filename);
		if (!file.exists()) {
			System.out.println("The file you are trying to read (" + filename + ") does not exist.");
			return list;
        }
        MiniRETableWalkerImpl walker = new MiniRETableWalkerImpl(dfa, file);
		Map<String, MiniREString> matches = new LinkedHashMap<String, MiniREString>();
		SourceToken token;
		while (walker.hasNextToken()) {
			token = walker.getNextToken();
            if (MiniRETableWalkerImpl.MATCH_TOKEN.equals(token.getTokenType())) {
			    String tokenString = token.getTokenString();
                if (!matches.containsKey(tokenString)) {
                    matches.put(tokenString, new MiniREStringImpl(tokenString,
                            new MetadataImpl(file.toString(), walker.getBufferLineNumber(),
                                    walker.getBufferIndex() - tokenString.length())));
                } else {
                    for (Metadata meta : matches.get(tokenString).getAllMetadata()) {
                        if (filename.equals(meta.getFile())) {
                            meta.addMatch(walker.getBufferLineNumber(), walker.getBufferIndex() - tokenString.length());
                            break;
                        }
                    }
                }
            }
		}

		for (MiniREString s : matches.values())
			list.add(s);

		return list;
    }

    @SuppressWarnings("unchecked")
    /* Implements the replace function. */
    private void replace(List<MiniREString> matches, String replace, String source,
                                String dest, boolean recursive) throws IOException
    {
        File sourceFile = new File(source);
        File destFile = new File(dest);
        int numReplaced = 0;

        if (!sourceFile.exists())
            throw new IllegalStateException("Source file \"" + source + "\"does not exist.");
        if (!destFile.exists() && !destFile.createNewFile())
            throw new IllegalStateException("Unable to create new file \"" + dest + "\".");

        ArrayList<String> lines = new ArrayList<String>();
        Scanner scan = new Scanner(sourceFile);
        while (scan.hasNextLine())
            lines.add(scan.nextLine());

        ArrayList<MiniREString> reversedMatches = new ArrayList<MiniREString>();
        MiniREString[] arr = new MiniREString[matches.size()];
        arr = matches.toArray(arr);
        for (int j = arr.length - 1; j >= 0; j--)
            reversedMatches.add(arr[j]);

        for (MiniREString match : reversedMatches) {
            for (Metadata data : match.getAllMetadata()) {
                if (source.equals(data.getFile())) {
                    Set<List<Integer>> reversed = new LinkedHashSet<List<Integer>>();
                    List[] array = new ArrayList[data.getMatches().size()];
                    array = data.getMatches().toArray(array);
                    for (int i = array.length - 1; i >= 0; i--)
                        reversed.add(array[i]);

                    for (List<Integer> place : reversed) {
                        String line = lines.get(place.get(0) - 1);
                        String replaced = "";
                        if (place.get(1) > 0)
                            replaced += line.substring(0, place.get(1));
                        replaced += replace;
                        if (place.get(1) + match.getString().length() < line.length())
                            replaced += line.substring(place.get(1) + match.getString().length());
                        lines.set(place.get(0) - 1, replaced);
                        numReplaced++;
                    }
                }
            }
        }

        FileWriter writer = new FileWriter(destFile);
        for (String line : lines)
            writer.write(line);
        writer.close();

        if (recursive && numReplaced > 0)
            replace(matches, replace, dest, dest, recursive);
    }

}