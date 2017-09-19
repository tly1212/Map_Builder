package cs311.hw8.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;




public class Graph<V, E> implements IGraph<V, E>
{
    
    public static void main(String args[] )
    {
        Graph<Integer, Double> g1 = new Graph<>();
        g1.setDirectedGraph();
        g1.addVertex("TestV1");
        g1.addVertex("TestV2");
        g1.addEdge("TestV1", "TestV2");
        
        g1.getVertices().forEach(v -> System.out.println(v.getVertexName()));
        g1.getEdges().forEach(e -> System.out.println(e.getVertexName1()+" : "+e.getVertexName2()));
        
    }
    
    
    
    
    private boolean directed = false;
    
    private final HashMap<String, Vertex<V>> vmap = new HashMap<>();
    private final LinkedHashMap<Vertex<V>, LinkedHashMap<Vertex, E>> edges = new LinkedHashMap<>();
    
    /**
	 * Set the boolean value to be true
	 * 
	 * @param directed
	 *            If the graph is directed then the boolean value is true.
	 */
    @Override
    public void setDirectedGraph()
    {
       directed = true;
    }

    /**
	 * Set the boolean value to be false. And if edge(u, v) is in the eList,
	 * check if edge(v, u) is in the eList. If not, add it to eList.
	 * 
	 * @param directed
	 *            If the graph is directed then the boolean value is true.
	 */
    @Override
    public void setUndirectedGraph()
    {
        if (directed)
        {
          directed = false;
          List<Edge<E>> edgelist = getEdges();
          Iterator<Edge<E>> iter = edgelist.iterator();
          while (iter.hasNext())
          {
              Edge<E> ed = iter.next();
              try {
				setEdgeData(ed.getVertexName1(), ed.getVertexName2(), null);
			} catch (cs311.hw8.graph.IGraph.NoSuchVertexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (cs311.hw8.graph.IGraph.NoSuchEdgeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
              if (!isEdge(ed.getVertexName2(), ed.getVertexName1() ))
              {
                  addEdge(ed.getVertexName1(), ed.getVertexName2());
              }
          }
        }
    }

    /**
	 * check if the graph is directed.
	 * 
	 * @return directed If the graph is directed then the boolean value is true.
	 */
    @Override
    public boolean isDirectedGraph()
    {
        return directed;
    }

    /**
	 * If the vertexName given is unique then add a vertex with this name as
	 * vertex name and null as vertex value to vList, otherwise throw exception.
	 * 
	 * @param vertexName
	 *            The unique name of the vertex.
	 * 
	 * @throws cs311.hw7.graph.IGraph.DuplicateVertexException
	 */
    @Override
    public void addVertex(String vertexName) throws DuplicateVertexException
    {
        if (vmap.containsKey(vertexName)) throw new DuplicateVertexException();
        vmap.put(vertexName, new Vertex<>(vertexName, null));
        edges.put(vmap.get(vertexName), new LinkedHashMap<>());
    }

    /**
	 * If the vertexName given is unique then add a vertex with this name as
	 * vertex name and vertexData as vertex value to vList, otherwise throw
	 * exception.
	 * 
	 * @param vertexName
	 * @param vertexData
	 * @throws cs311.hw6.graph.IGraph.DuplicateVertexException
	 */
    @Override
    public void addVertex(String vertexName, V vertexData) throws DuplicateVertexException
    {
        if (vmap.containsKey(vertexName)) throw new DuplicateVertexException();
        vmap.put(vertexName, new Vertex<>(vertexName, vertexData));
        edges.put(vmap.get(vertexName), new LinkedHashMap<>());
    }

    /**
	 * first check whether vertex x and y are in the vList. second if the graph
	 * is directed, then check if edge(x,y) is in the eList, if not add it to
	 * the list. If the graph is undirected, then check if edge(x,y) is in the
	 * eList, if not then add both edge(x, y) and edge(y, x) to the list.
	 * 
	 * @param vertex1
	 *            The first vertex in the edge.
	 * @param vertex2
	 *            The second vertex in the edge.
	 * 
	 * @throws cs311.hw8.graph.IGraph.DuplicateEdgeException
	 * @throws cs311.hw8.graph.IGraph.NoSuchVertexException
	 */
    @Override
    public void addEdge(String vertex1, String vertex2) throws DuplicateEdgeException, NoSuchVertexException
    {
        Vertex<V> v1 = vmap.get(vertex1);
        Vertex<V> v2 = vmap.get(vertex2);
        if (v1 == null || v2 == null) throw new NoSuchVertexException();
        if (edges.get(v1).containsKey(v2)) throw new DuplicateEdgeException();
        edges.get(v1).put(v2, null);
    }

    /**
	 * First check whether vertex x and y are in the vList, if not throw
	 * exception. Second if the graph is directed, then check if edge(x,y) is in
	 * the eList, if not add it to the list. If the graph is undirected, then
	 * check if edge(x,y) is in the eList, if not then add both edge(x, y) and
	 * edge(y, x) to the list.
	 * 
	 * @param vertex1
	 *            The first vertex in the edge.
	 * @param vertex2
	 *            The second vertex in the edge.
	 * @param edgeData
	 *            Thegeneric edge data.
	 * 
	 * @throws cs311.hw8.graph.IGraph.DuplicateEdgeException
	 * @throws cs311.hw8.graph.IGraph.NoSuchVertexException
	 */
    @Override
    public void addEdge(String vertex1, String vertex2, E edgeData) throws DuplicateEdgeException, NoSuchVertexException
    {
        Vertex<V> v1 = vmap.get(vertex1);
        Vertex<V> v2 = vmap.get(vertex2);
        if (v1 == null || v2 == null) throw new NoSuchVertexException();
        if (edges.get(v1).containsKey(v2)) throw new DuplicateEdgeException();
        edges.get(v1).put(v2, edgeData);
    }

    /**
	 * Check if the vertex exists, if not then throw exception. If no vertex
	 * data is associated with the vertex, then null is returned.
	 * 
	 * @param vertexName
	 *            Name of vertex to get data for
	 * 
	 * @return The generic vertex data
	 * 
	 * @throws cs311.hw7.graph.IGraph.NoSuchVertexException
	 */
    @Override
    public V getVertexData(String vertexName) throws NoSuchVertexException
    {
        if (!vmap.containsKey(vertexName)) throw new NoSuchVertexException();
        return vmap.get(vertexName).getVertexData();
    }

    /**
	 * If the vertex exists, remove the vertex and add a new vertex with the
	 * same name but new vertex data. Otherwise, throw exception.
	 * 
	 * @param vertexName
	 *            The name of the vertex.
	 * 
	 * @param vertexData
	 *            The generic vertex data.
	 * 
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 */
    @Override
    public void setVertexData(String vertexName, V vertexData) throws NoSuchVertexException
    {
        if (!vmap.containsKey(vertexName)) throw new NoSuchVertexException();
        vmap.remove(vertexName);
        vmap.put(vertexName, new Vertex<>(vertexName, vertexData));
    }

    /**
	 * First check whether vertex x and y are in the vList, if not throw
	 * exception. Second if edge(x, y) exists in eList then return value,
	 * otherwise throw exception.
	 * 
	 * @param vertex1
	 *            Vertex one of the edge.
	 * @param vertex2
	 *            Vertex two of the edge.
	 * 
	 * @return The generic edge data.
	 * 
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 * @throws cs311.hw6.graph.IGraph.NoSuchEdgeException
	 */
    @Override
    public E getEdgeData(String vertex1, String vertex2) throws NoSuchVertexException, NoSuchEdgeException
    {
        Vertex<V> v1 = vmap.get(vertex1);
        Vertex<V> v2 = vmap.get(vertex2);
        if (v1 == null || v2 == null) throw new NoSuchVertexException();
        if (!edges.get(v1).containsKey(v2)) throw new NoSuchEdgeException();
        return edges.get(v1).get(v2);
    }

    /**
	 * If the vertices do not exist, throw exception. If the edge exists, then
	 * remove the edge and add a new edge with the same name but new edge data.
	 * Otherwise, throw exception.
	 * 
	 * @param vertex1
	 *            Vertex one of the edge.
	 * @param vertex2
	 *            Vertex two of the edge.
	 * 
	 * @param edgeData
	 *            The generic edge data.
	 * 
	 * @throws cs311.hw6.graph.IGraph.NoSuchVertexException
	 * @throws cs311.hw6.graph.IGraph.NoSuchEdgeException
	 */
    @Override
    public void setEdgeData(String vertex1, String vertex2, E edgeData) throws NoSuchVertexException, NoSuchEdgeException
    {
        Vertex<V> v1 = vmap.get(vertex1);
        Vertex<V> v2 = vmap.get(vertex2);
        if (v1 == null || v2 == null) throw new NoSuchVertexException();
        if (!edges.get(v1).containsKey(v2)) throw new NoSuchEdgeException();
        edges.get(v1).put(v2, edgeData);
    }

    /**
	 * If there is a vertex has the given name, then the vertex. Otherwise
	 * return null;
	 * 
	 * @param VertexName
	 *            The name of the vertex.
	 * 
	 * @return The encapsulated vertex.
	 */
    @Override
    public Vertex<V> getVertex(String vertexName) throws NoSuchVertexException
    {
        if (!vmap.containsKey(vertexName)) throw new NoSuchVertexException();
        return vmap.get(vertexName);
    }

    /**
	 * If there is a edge has the given name, then the edge. Otherwise return
	 * null;
	 * 
	 * @param vertexName1
	 *            Vertex one of edge.
	 * @param vertexName2
	 *            Vertex two of edge.
	 * 
	 * @return e Encapsulated edge.
	 */
    @Override
    public Edge<E> getEdge(String vertexName1, String vertexName2)
    {
        Vertex<V> v1 = vmap.get(vertexName1);
        Vertex<V> v2 = vmap.get(vertexName2);
        if (v1 == null || v2 == null) throw new NoSuchVertexException();
        if (!edges.get(v1).containsKey(v2))
			try {
				throw new NoSuchEdgeException();
			} catch (cs311.hw8.graph.IGraph.NoSuchEdgeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return new Edge( vertexName1, vertexName2, edges.get(v1).get(v2));
    }

    /**
	 * Returns a list of all the vertices in the graph.
	 * 
	 * @return vList The List<Vertex> of vertices.
	 */
    @Override
    public List<Vertex<V>> getVertices()
    {
        return new ArrayList<>(vmap.values());
    }

    /**
	 * Returns all the edges in the graph.
	 * 
	 * @return eList The List<Edge<E>> of edges.
	 */
    @Override
    public List<Edge<E>> getEdges()
    {
         ArrayList<Edge<E>> retval = new ArrayList<>();
         vmap.values().forEach( v -> edges.get(v).forEach((nv, ed) -> retval.add( new Edge<>(v.getVertexName(), nv.getVertexName(), ed))));
         return retval;
    }

    /**
	 * Check all of the edges, if edge e contains the vertex as start vertex then check the
	 * other vertex v2 of e contained in neighbor list or not, if not add v2 to
	 * the neighbor list.
	 * 
	 * @param vertex
	 *            The vertex to return neighbors for.
	 * 
	 * @return vNeibourList The list of vertices that are the neighbors of the
	 *         specified vertex.
	 */
    @Override
    public List<Vertex<V>> getNeighbors(String vertex)
    {
        Vertex<V> v1 = vmap.get(vertex);
        if (v1 == null) throw new NoSuchVertexException();
        ArrayList<Vertex<V>> retval = new ArrayList<>();
        edges.get(v1).forEach((nv, ed) -> retval.add(nv));
        return retval;
    }
    
    /**
	 * check if there is an edge with vertices' name are v1 and v2.
	 * 
	 * @return isEdge(v1v, v2v ) true if the edge exists
	 */
    public boolean isEdge( String v1, String v2)
    {
        Vertex<V> v1v = vmap.get(v1);
        Vertex<V> v2v = vmap.get(v2);
        return isEdge(v1v, v2v );
    }
    
    
    /**
	 * check if there is an edge with vertices v1 and v2.
	 * 
	 * @return edges.get(v1).containsKey(v2) true if the edge exists
	 */
    public boolean isEdge( Vertex v1, Vertex v2)
    {
        return edges.get(v1).containsKey(v2);
    }
    
    /**
	 * check if an edge exists
	 * 
	 * @return true if the edge exists
	 */
    public boolean isEdge( Edge<E> e)
    {
        return isEdge( e.getVertexName1(), e.getVertexName2());
    }


}