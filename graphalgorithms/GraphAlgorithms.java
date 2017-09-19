package cs311.hw8.graphalgorithms;

import cs311.hw8.graph.Graph;
import cs311.hw8.graph.IGraph;
import cs311.hw8.graph.IGraph.Edge;
import cs311.hw8.graph.IGraph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class GraphAlgorithms {
	/**
	 * Topological sort graph g
	 * 
	 * @param g
	 *            Graph to be sorted
	 * 
	 * @return topoSort The sorted result List;
	 */
	public static <V, E> List<Vertex<V>> TopologicalSort(IGraph<V, E> g) {

		ArrayList<Vertex<V>> visited = new ArrayList<Vertex<V>>();
		Map<String, Integer> indegreeMap = new HashMap<>();
		List<Vertex<V>> topoSort = new LinkedList<Vertex<V>>();

		for (Vertex<V> v : g.getVertices()) {
			indegreeMap.put(v.getVertexName(), 0);
		}

		for (Edge<E> e : g.getEdges()) {
			String name = e.getVertexName2();
			int indeg = indegreeMap.get(name) + 1;
			indegreeMap.put(name, indeg);
		}

		if (g.isDirectedGraph()) {
			topoSortUtil(g, indegreeMap, visited, topoSort);
			return topoSort; // Dummy return - replace this.
		} else
			return null;
	}

	/**
	 * Recursively search method for topological sort
	 * 
	 * @param v
	 *            vertex to start the sort.
	 * 
	 * @param topoSort
	 *            The sorted result List;
	 * 
	 * @param visited
	 *            list of vertices already visited.
	 * 
	 * @param g
	 *            Graph to be sorted.
	 */
	private static <V, E> void topoSortUtil(IGraph<V, E> g, Map<String, Integer> indegreeMap,
			ArrayList<Vertex<V>> visited, List<Vertex<V>> topoSort) throws NotDAGException {

		for (Vertex<V> v : g.getVertices()) {
			if (!visited.contains(v) && indegreeMap.get(v.getVertexName()) == 0) {
				visited.add(v);
				for (Vertex<V> vNeigbour : g.getNeighbors(v.getVertexName())) {
					String vName = vNeigbour.getVertexName();
					int indeg = indegreeMap.get(vName) - 1;
					indegreeMap.put(vName, indeg);
				}

				topoSort.add(v);
				topoSortUtil(g, indegreeMap, visited, topoSort);
				break;
			}
		}

		// System.out.println(topoSort.size() + ":" + g.getVertices().size());

		if (topoSort.size() != g.getVertices().size()) {
			throw new NotDAGException();
		}
	}

	/**
	 * All Topological sort graph g
	 * 
	 * @param g
	 *            Graph to be sorted
	 * 
	 * @return finalList All possible topological sort result List;
	 */
	public static <V, E> List<List<Vertex<V>>> AllTopologicalSort(IGraph<V, E> g) {
		boolean flag = false;
		ArrayList<Vertex<V>> marked = new ArrayList<Vertex<V>>();
		Map<String, Integer> indegree = new HashMap<>();
		List<List<Vertex<V>>> finalList = new LinkedList<List<Vertex<V>>>();
		List<Vertex<V>> list = new LinkedList<Vertex<V>>();

		for (Vertex<V> v : g.getVertices()) {
			indegree.put(v.getVertexName(), 0);
		}

		for (Edge<E> e : g.getEdges()) {
			String name = e.getVertexName2();
			int indeg = indegree.get(name) + 1;
			indegree.put(name, indeg);
		}

		if (g.isDirectedGraph()) {
			AllTopologicalSortUtil(g, indegree, marked, flag, list, finalList);
			return finalList; // Dummy return - replace this.
		} else
			return null;
	}

	/**
	 * Recursively search method for all topological sort
	 * 
	 * @param indegree
	 *            the HashMap take name of the vertex as key and indegree as
	 *            value.
	 * 
	 * @param marked
	 *            list of vertices already visited.
	 * 
	 * @param flag
	 *            check if one possible sort is finished
	 * 
	 * @param list
	 *            the list to store one topological sort result.
	 * 
	 * @param finalList
	 *            the list to store all topological sort result.
	 * 
	 * @param g
	 *            Graph to be sorted.
	 * @throws Exception
	 */
	public static <V, E> void AllTopologicalSortUtil(IGraph<V, E> g, Map<String, Integer> indegree,
			ArrayList<Vertex<V>> marked, boolean flag, List<Vertex<V>> list, List<List<Vertex<V>>> finalList)
			throws NotDAGException {

		flag = false;
		for (Vertex<V> v : g.getVertices()) {
			if (!marked.contains(v) && indegree.get(v.getVertexName()) == 0) {
				marked.add(v);
				for (Vertex<V> vNeigbour : g.getNeighbors(v.getVertexName())) {
					String vName = vNeigbour.getVertexName();
					int indeg = indegree.get(vName) - 1;
					indegree.put(vName, indeg);
				}

				list.add(v);
				AllTopologicalSortUtil(g, indegree, marked, flag, list, finalList);

				marked.remove(v);
				for (Vertex<V> vNeigbour : g.getNeighbors(v.getVertexName())) {
					String vName = vNeigbour.getVertexName();
					int indeg = indegree.get(vName) + 1;
					indegree.put(vName, indeg);
				}
				list.remove(v);
				flag = true;
			}
		}

		if (list.size() != g.getVertices().size()) {
			System.out.print(list.size() + ":" + g.getVertices().size());
			System.out.println();
			// throw new NotDAGException();
		}

		if (!flag) {
			finalList.add(list);
		}
	}

	/**
	 * Not DAG Exception
	 */
	public final static class NotDAGException extends RuntimeException {
	}

	/**
	 * Using Kruscal algrithm to get MST from graph g.
	 * 
	 * @param g
	 *            Graph to be found MST from.
	 * 
	 * @return mstG graph of MST.
	 */
	public static <V, E extends IWeight> IGraph<V, E> Kruscal(IGraph<V, E> g) {
		IGraph<V, E> mstG = new Graph<V, E>();
		mstG.setDirectedGraph();
		for (Vertex<V> v : g.getVertices()) {
			mstG.addVertex(v.getVertexName());
			;
		}

		SetUnion su = new SetUnion(g.getVertices());
		if (g.isDirectedGraph() == true) {
			g.setUndirectedGraph();
		}

		PriorityQueue<Edge<E>> pq = new PriorityQueue<Edge<E>>(g.getEdges().size(), new Comparator<Edge<E>>() {
			public int compare(Edge<E> o1, Edge<E> o2) {
				if (o1.getEdgeData().getWeight() > o2.getEdgeData().getWeight()) {
					return 1;
				} else if (o1.getEdgeData().getWeight() < o2.getEdgeData().getWeight()) {
					return -1;
				} else
					return 0;
			}
		});// create pq to
			// store
			// edges

		Map<String, Integer> idMap = new HashMap<String, Integer>(); // use
																		// hashMap
																		// to
																		// associtate
																		// the
																		// id
																		// number
																		// with
																		// vertex
																		// name

		int vSize = g.getVertices().size(), count = 0, id = 0;

		List<Edge<E>> edgeList = new LinkedList<Edge<E>>();// list to store
															// edges from MST

		for (Vertex<V> v : g.getVertices()) {// Set the id of each vertex
			idMap.put(v.getVertexName(), id);
			id += 1;
		}

		for (Edge<E> e : g.getEdges()) {// create pq
			// e.getEdgeData().getWeight();
			if (pq.contains(g.getEdge(e.getVertexName2(), e.getVertexName1())))
				continue;
			pq.add(e);
		}

		/* use this union find method */
		// while(count< vSize - 1){
		// Edge<E> edMin = pq.remove();
		// Vertex<V> v1 = g.getVertex(edMin.getVertexName1());
		// Vertex<V> v2 = g.getVertex(edMin.getVertexName2());
		//
		// if (!su.sameComponent(v1, v2)){
		// mstG.addEdge(v1.getVertexName(), v2.getVertexName());
		// su.mergeComponent(v1, v2);
		// }
		// count++;
		// }

		/* or use this hashMap method */
		while (count < vSize - 1) {
			Edge<E> minEege = pq.remove();
			int id1 = idMap.get(minEege.getVertexName1());
			int id2 = idMap.get(minEege.getVertexName2());
			if (id1 != id2) {// if the ids are different then they are not from
								// the same component
				Iterator it = idMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Integer> pair = (Map.Entry) it.next();
					if (pair.getValue() == id2)// Merge: iterate the whole map,
												// set the ids of vertices with
												// id2 to be id1
						pair.setValue(id1);
					// it.remove(); // avoids a ConcurrentModificationException
				}
				edgeList.add(minEege);
				count += 1;
			}
		}

		for (Edge<E> e : edgeList) {
			mstG.addEdge(e.getVertexName1(), e.getVertexName2(), e.getEdgeData());
		}

		return mstG;
	}

	/**
	 * find the shortest parth on given data from graph g.
	 * 
	 * @param g
	 *            Graph to be found MST from.
	 * @param vertexStart
	 *            start vertex
	 * @param vertexEnd
	 *            end vertex
	 * @return path the shortest path found.
	 */
	public static <V, E extends IWeight> List<Edge<E>> ShortestPath(IGraph<V, E> g, String vertexStart,
			String vertexEnd) {

		Set<Vertex<V>> settledNodes = new HashSet<>();
		Set<Vertex<V>> unSettledNodes = new HashSet<>();
		// Map<String, String> predecessors = new HashMap<>();
		// Map<String, Integer> distance = new HashMap<>();
		Map<Vertex<V>, Vertex<V>> predecessors = new HashMap<>();
		Map<Vertex<V>, Integer> distance = new HashMap<>(); // int or
															// E???????????????????

		if (!g.isDirectedGraph()) {
			g.setDirectedGraph();
		}

		// distance.put(vertexStart, 0);
		// unSettledNodes.add(arg0)
		
		List<Vertex<V>> vtList = g.getVertices();
		for (Vertex<V> v : vtList) {
			if (v.getVertexName().equals(vertexStart)) {
				distance.put(v, 0);
				unSettledNodes.add(v);
			}
		}

		while (unSettledNodes.size() > 0) {
			Vertex<V> node = getMinimum(distance, unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(g, node, distance, unSettledNodes, predecessors, settledNodes);
		}

		Vertex<V> endPoint = null;
		for (Vertex<V> v : g.getVertices()) {
			if (v.getVertexName().equals(vertexEnd)) {
				endPoint = v;
				break;
			}
		}

		List<Edge<E>> path = getPath(endPoint, predecessors, g);

		return path;
	}

	/**
	 * find the minimum distance(Weight) of a given node.
	 * 
	 */
	private static <V, E extends IWeight> void findMinimalDistances(IGraph<V, E> g, Vertex<V> node,
			Map<Vertex<V>, Integer> distance, Set<Vertex<V>> unSettledNodes, Map<Vertex<V>, Vertex<V>> predecessors,
			Set<Vertex<V>> settledNodes) {
		List<Vertex<V>> adjacentNodes = getNeighbors(node, g, settledNodes);
		for (Vertex<V> target : adjacentNodes) {
			if (getShortestDistance(distance, target) > getShortestDistance(distance, node)
					+ getDistance(node, target, g)) {
				distance.put(target, getShortestDistance(distance, node) + getDistance(node, target, g));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}
	}

	/**
	 * get the distance(Weight) of a given two nodes.
	 * @param node node1
	 * @param target node2
	 * @return dis the weight of found edge
	 */
	private static <V, E extends IWeight> int getDistance(Vertex<V> node, Vertex<V> target, IGraph<V, E> g) {
		for (Edge<E> edge : g.getEdges()) {
			if (edge.getVertexName1().equals(node.getVertexName())
					&& edge.getVertexName2().equals(target.getVertexName())) {
				int dis = (int) edge.getEdgeData().getWeight();
				return dis;
			}
		}
		throw new RuntimeException("Should not happen");
	}

	/**
	 * get the Neighbors of a given node from, which should exclude nodes processed.
	 * @param node given node
	 * @return neighbors the neighbors of given node
	 */
	private static <V, E extends IWeight> List<Vertex<V>> getNeighbors(Vertex<V> node, IGraph<V, E> g,
			Set<Vertex<V>> settledNodes) {
		List<Vertex<V>> neighbors = new ArrayList<Vertex<V>>();
		List<Vertex<V>> aNeighbors = g.getNeighbors(node.getVertexName());
		for (Vertex<V> v : aNeighbors) {
			if (!settledNodes.contains(v)) {
				neighbors.add(v);
			}
		}
		return neighbors;
	}

	/**
	 * get the vertex from unSettledNodes with min distance(weight) to SettledNodes.
	 *
	 * @return minimum the vertex with min weight
	 */
	private static <V> Vertex<V> getMinimum(Map<Vertex<V>, Integer> distance, Set<Vertex<V>> unSettledNodes) {
		Vertex<V> minimum = null;
		for (Vertex<V> vertex : unSettledNodes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(distance, vertex) < getShortestDistance(distance, minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	/**
	 * get the shortest distance of given vertex.
	 */
	private static <V> int getShortestDistance(Map<Vertex<V>, Integer> distance, Vertex<V> destination) {
		Integer d = distance.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	 /**
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
	private static <E, V> List<Edge<E>> getPath(Vertex<V> endPoint, Map<Vertex<V>, Vertex<V>> predecessors,
			IGraph<V, E> g) {
		LinkedList<Edge<E>> path = new LinkedList<>();
		Vertex<V> step = endPoint;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}

		while (predecessors.get(step) != null) {
			for (Edge<E> e : g.getEdges()) {
				if (e.getVertexName1().equals(predecessors.get(step).getVertexName())
						&& e.getVertexName2().equals(step.getVertexName())) {
					path.add(e);
				}
			}

			step = predecessors.get(step);
		}
		// // Put it into the correct order
		Collections.reverse(path);
		return path;
	}

}
