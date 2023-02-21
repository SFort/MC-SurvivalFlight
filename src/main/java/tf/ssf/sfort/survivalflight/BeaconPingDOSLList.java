package tf.ssf.sfort.survivalflight;

import java.util.function.Predicate;

public class BeaconPingDOSLList<T> {
	public Node<T> first;
	public void add(T t, int i) {
		Node<T> prev = null;
		Node<T> next = first;
		int count = 0;
		while (next != null) {
			int c = count+next.i;
			if (c>i) break;
			count = c;
			prev = next;
			next = next.next;
		}
		if (prev == null) {
			prev = (first = new Node<>(t, count = i, first)).next;
		} else {
			prev = (prev.next = new Node<>(t, count = i-count, next)).next;
		}
		while (prev != null) {
			prev.i-=count;
			prev = prev.next;
		}
	}
	public boolean anyMatch(Predicate<T> p) {
		Node<T> node = first;
		while (node != null) {
			if (p.test(node.obj)) return true;
			node = node.next;
		}
		return false;
	}
	public void tick() {
		if (first == null) return;
		first.i--;
		do {
			if (first.i>0) break;
			first = first.next;
		} while (first != null);
	}

	public static class Node<T> {
		public T obj;
		public int i;
		public Node<T> next;

		public Node(T obj, int i, Node<T> next) {
			this.obj = obj;
			this.i = i;
			this.next = next;
		}
	}
}
