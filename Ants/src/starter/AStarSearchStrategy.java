package starter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class AStarSearchStrategy implements SearchStrategy {

	private Ants ants;

	@Override
	public List<Tile> bestPath(Tile from, Tile to) {
		if (from.equals(to))
			return Arrays.asList(to);

		Set<Tile> explored = new HashSet<Tile>();
		PriorityQueue<Node> frontier = new PriorityQueue<Node>();
		frontier.add(new Node(from, null, 0));
		while (!frontier.isEmpty()) {
			Node node = frontier.poll();
			explored.add(node.getState());
			for (Node child : expand(node)) {
				if (frontier.contains(child)
						|| explored.contains(child.getState()))
					continue;
				if (child.equals(to))
					return path(child); // success
				frontier.add(child);
			}
		}
		return null; // failure
	}

	public SortedSet<Node> expand(Node node) {
		SortedSet<Node> children = new TreeSet<Node>();
		final Tile north = ants.getTile(node.getState(), Aim.NORTH);
		final Tile south = ants.getTile(node.getState(), Aim.SOUTH);
		final Tile west = ants.getTile(node.getState(), Aim.WEST);
		final Tile east = ants.getTile(node.getState(), Aim.EAST);
		addChild(node, children, north);
		addChild(node, children, south);
		addChild(node, children, west);
		addChild(node, children, east);
		return children;
	}

	private void addChild(Node parent, SortedSet<Node> children,
			final Tile childState) {
		if (ants.getIlk(childState).isUnoccupied())
			children.add(new Node(childState, parent, getCost(parent,
					childState)));
	}

	private int getCost(Node current, Tile dest) {
		return current.getCost() + current.getState().manhattanDistanceTo(dest);
	}

	private List<Tile> path(Node child) {
		List<Tile> path = new ArrayList<Tile>();
		addToPath(path, child);
		return path;
	}

	private void addToPath(List<Tile> path, Node child) {
		if (child == null)
			return;
		path.add(child.getState());
		addToPath(path, child.getParent());
	}
}
