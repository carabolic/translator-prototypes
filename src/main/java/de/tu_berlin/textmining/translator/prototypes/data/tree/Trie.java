package de.tu_berlin.textmining.translator.prototypes.data.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class Trie {

	private final static int UNLIMITED_DEGREE = 0;
	private final int maxDegree;
	private final TrieNode root;

	public Trie() {
		this(Trie.UNLIMITED_DEGREE);
	}

	public Trie(final int maxDegree) {
		this.maxDegree = maxDegree;
		this.root = new TrieNode(0);
	}

	public int getMaxDegree() {
		return this.maxDegree;
	}
	
	public void insert(final String word) {
		List<String> sentence = Lists.newArrayList();
		sentence.add(word);
		this.insert(sentence);
	}

	public void insert(List<String> sentence) {
		this.root.insert(sentence);
	}
	
	public long retrieve(String word) {
		List<String> sentence = Lists.newArrayList();
		sentence.add(word);
		return this.retrieve(sentence);
	}

	public long retrieve(List<String> sentence) {
		return this.root.retrieve(sentence);
	}

	static class TrieNode {

		private long count;
		private final Map<String, TrieNode> children;

		public TrieNode() {
			this(0l);
		}

		public TrieNode(final long count) {
			this.count = count;
			this.children = new HashMap<String, Trie.TrieNode>();
		}

		public void insert(final List<String> sentence) {
			this.count++;
			if (sentence.size() > 0) {
				String head = head(sentence);
				List<String> tail = tail(sentence);
				TrieNode child = this.children.get(head);
				if (child != null) {
					child.insert(tail);
				} else {
					if (!tail.isEmpty()) {
						child = new TrieNode();
						this.children.put(head, child);
						child.insert(tail);
					} else {
						child = new TrieNode(1);
						this.children.put(head, child);
					}
				}
			}
		}

		public long retrieve(final List<String> sentence) {
			long count = 0;
			if (sentence.size() > 0) {
				String head = head(sentence);
				List<String> tail = tail(sentence);
				TrieNode child = this.children.get(head);
				if (child != null) {
					if (!tail.isEmpty()) {
						count = child.retrieve(tail);
					} else {
						return child.count;
					}
				} else {
					return 0l;
				}
			}
			
			return count;
		}

		private static String head(final List<String> list) {
			if (list.size() > 0) {
				return list.get(0);
			} else {
				return "";
			}
		}

		private static List<String> tail(final List<String> list) {
			int len = list.size();
			if (len > 1) {
				return list.subList(1, len);
			} else {
				return Lists.newArrayList();
			}
		}
	}
	
	public static void main(String... args) {
		Trie t = new Trie();
		List<String> s1 = Lists.newArrayList();
		s1.add("Hallo");
		s1.add("Welt");
		List<String> s2 = Lists.newArrayList();
		s2.add("Hallo");
		s2.add("du");
		s2.add("wie");
		s2.add("geht's");
		List<String> s3 = Lists.newArrayList();
		s3.add("Hallo");
		s3.add("du");
		s3.add("wie");
		s3.add("geht");
		s3.add("es");
		t.insert(s1);
		t.insert(s2);
		t.insert(s3);
		System.out.println(s1 + " count: " + t.retrieve(s1));
		System.out.println(s2 + " count: " + t.retrieve(s2));
		System.out.println(s3 + " count: " + t.retrieve(s3));
		System.out.println("Hallo count: " + t.retrieve("Hallo"));
		List<String> sTest = Lists.newArrayList();
		sTest.add("Hallo");
		sTest.add("du");
		System.out.println(sTest + " count: " + t.retrieve(sTest));
	}
}
