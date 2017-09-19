package cs311.hw8.graphalgorithms;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import cs311.hw8.graph.IGraph.Vertex;

public class SetUnion <V> {
    private Map<Vertex<V>, Vertex<V>> parents = new HashMap<Vertex<V>, Vertex<V>>();
    private Map<Vertex<V>, Integer> sizes = new HashMap<Vertex<V>, Integer>();
    
    public SetUnion(List<Vertex<V>> vertices) {
        for (Vertex<V> v :
             vertices) {
            parents.put(v, v);
            sizes.put(v, 1);
        }
    }
    
    public Vertex<V> find(Vertex<V> u) {
        if (parents.get(u) == u)
            return u;
        else
            return find(parents.get(u));
    }
    
    public void union(Vertex<V> u, Vertex<V> v) {
        Vertex<V> u1 = find(u);
        Vertex<V> u2 = find(v);
        
        if (u1 == u2)
            return;
        
        if(sizes.get(u1) >= sizes.get(u2)) {
            sizes.put(u1, sizes.get(u1) + sizes.get(u2));
            parents.put(u2, u1);
        } else {
            sizes.put(u2, sizes.get(u1) + sizes.get(u2));
            parents.put(u1, u2);
        }
    }
    
    public boolean sameComponent(Vertex<V> u, Vertex<V> v) {
        return (find(u) == find(v));
    }
    
    public void mergeComponent(Vertex<V> u, Vertex<V> v) {
        union(u, v);
    }
}