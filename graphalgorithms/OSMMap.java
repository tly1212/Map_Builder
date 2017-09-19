package cs311.hw8.graphalgorithms;

import org.w3c.dom.*;

import cs311.hw8.graph.Graph;
import cs311.hw8.graph.IGraph;
import cs311.hw8.graph.Weight;
import cs311.hw8.graph.IGraph.Edge;
import cs311.hw8.graph.IGraph.NoSuchEdgeException;
import cs311.hw8.graph.IGraph.NoSuchVertexException;
import cs311.hw8.graph.IGraph.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.*;

public class OSMMap {

	/**
	 * inner class to store vertex data
	 */
	class Vert {
		String name;
		double lat;
		double lon;

		/**
		 * vertex constructor
		 */
		protected Vert(String name, double lat, double lon) {
			this.name = name;
			// this.lat = Double.parseDouble(lat);
			// this.lon = Double.parseDouble(lon);
			this.lat = lat;
			this.lon = lon;
			;
		}

		/**
		 * get the name of vertex
		 * 
		 * @return name the name
		 */
		public String getVertName() {
			return name;
		}

		/**
		 * get the latitude of vertex
		 * 
		 * @return lat the latitude
		 */
		public double getLat() {
			return lat;
		}

		/**
		 * get the lontitude of vertex
		 * 
		 * @return lon the lontitude
		 */
		public double getLon() {
			return lon;
		}
	}

	/**
	 * inner class to store edge data
	 */
	class Eg implements IWeight {
		private final Vert vertex1;
		private final Vert vertex2;
		private final String eName;
		private final double edgeData;

		/**
		 * edge constructor
		 */
		protected Eg(Vert v1, Vert v2, String eName) {
			vertex1 = v1;
			vertex2 = v2;
			edgeData = EgWeight(v1.getLat(), v1.getLon(), v2.getLat(), v2.getLon());
			this.eName = eName;
		}

		/**
		 * get the name of vertex1
		 * 
		 * @return vertex1 the name of vertex1
		 */
		public Vert getVert1() {
			return vertex1;
		}

		/**
		 * get the name of vertex2
		 * 
		 * @return vertex1 the name of vertex2
		 */
		public Vert getVertName2() {
			return vertex2;
		}

		/**
		 * get the data of edge
		 * 
		 * @return edgeData the data of edge
		 */
		public double getEgData() {
			return edgeData;
		}

		/**
		 * get the name of edge
		 * 
		 * @return eName the name of edge
		 */
		public String getEgName() {
			return eName;
		}

		/**
		 * get the weight of edge
		 * 
		 * @return edgeData the weight of edge
		 */
		@Override
		public double getWeight() {
			return edgeData;
		}

	}

	/**
	 * calculate the distance of two vertices
	 * 
	 * @param lat1
	 *            latitude of vertex1
	 * @param lat2
	 *            latitude of vertex2
	 * @param lon1
	 *            longitude of vertex1
	 * @param lon2
	 *            longitude of vertex2
	 * @return dist the distance
	 * 
	 *         EgDistance method comes from
	 *         "https://dzone.com/articles/distance-calculation-using-3"
	 */

	public double EgWeight(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		return (dist);
	}

	/*
	 * This function converts decimal degrees to radians :
	 */

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*
	 * This function converts radians to decimal degrees
	 */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	Graph<Vert, Eg> map;

	/*
	 * constructor of OSMMap
	 */
	public OSMMap() {
		map = new Graph<Vert, Eg>();
	}

	/**
	 * load the map from given file
	 * 
	 * @param filename
	 *            source file name
	 */
	public void LoadMap(String filename) {
		map = new Graph<Vert, Eg>();
		map.setDirectedGraph();
		try {
			File inputFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("node");//"node" list
			NodeList wList = doc.getElementsByTagName("way");//"way" list		
			
			// for each node add the vertex to map
			int nLen = nList.getLength();
			for (int temp = 0; temp < nLen; temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					double lon = Double.parseDouble(eElement.getAttribute("lon"));
					double lat = Double.parseDouble(eElement.getAttribute("lat"));
					String vname = eElement.getAttribute("id");
					Vert v = new Vert(vname, lat, lon);
					map.addVertex(vname, v);
				}
			}

			//parse childnode from each "way" node
			int wLen = wList.getLength();
			for (int temp = 0; temp < wLen; temp++) {
				List<String> refList = new ArrayList<String>();
				List<String> kList = new ArrayList<String>();
				List<String> vList = new ArrayList<String>();
				String streetName = null;
				Node wNode = wList.item(temp);
				NodeList childList = wNode.getChildNodes();
				int cLen = childList.getLength();
				for (int j = 0; j < cLen; j++) {
					if (childList.item(j) instanceof Element == false)
						continue;
					Element aElement = (Element) childList.item(j);

					if (aElement.getTagName().equals("nd")) {
						String refVal = aElement.getAttribute("ref");
						refList.add(refVal);
					}

					if (aElement.getTagName().equals("tag")) {
						String kVal = aElement.getAttribute("k");
						kList.add(kVal);
						String vVal = aElement.getAttribute("v");
						vList.add(vVal);
					}
				}

				//  add edges to map
				if (kList.contains("highway") && kList.contains("name")) {
					streetName = vList.get(kList.indexOf("name"));
					//if "oneway" is yes then add edge on one idrection
					if (kList.contains("oneway") && vList.get(kList.indexOf("oneway")).equals("yes")) {
						for (int i = 0; i < refList.size() - 1; i++) {
							Vertex<Vert> v1 = null, v2 = null;
							for (Vertex<Vert> v : map.getVertices()) {
								if (v.getVertexName().equals(refList.get(i))) {
									v1 = v;
								}
								if (v.getVertexName().equals(refList.get(i + 1))) {
									v2 = v;
								}
								if (v1 != null && v2 != null)
									break;
							}
							Vert av1 = new Vert(v1.getVertexData().getVertName(), v1.getVertexData().getLat(),
									v1.getVertexData().getLon());
							Vert av2 = new Vert(v2.getVertexData().getVertName(), v2.getVertexData().getLat(),
									v2.getVertexData().getLon());
							Eg egData = new Eg(av1, av2, streetName);

							map.addEdge(refList.get(i), refList.get(i + 1), egData);

						}
					} else {   //otherwise add edges of two direction
						for (int i = 0; i < refList.size() - 1; i++) {
							Vertex<Vert> v1 = null, v2 = null;

							for (Vertex<Vert> v : map.getVertices()) {

								if (v.getVertexName().equals(refList.get(i))) {
									v1 = v;
								}
								if (v.getVertexName().equals(refList.get(i + 1))) {
									v2 = v;
								}
								if (v1 != null && v2 != null)
									break;
							}
							if (v1 == null)
								System.out.println(" v1 is null");

							Vert av1 = new Vert(v1.getVertexData().getVertName(), v1.getVertexData().getLat(),
									v1.getVertexData().getLon());
							Vert av2 = new Vert(v2.getVertexData().getVertName(), v2.getVertexData().getLat(),
									v2.getVertexData().getLon());
							Eg egData = new Eg(av1, av2, streetName);

							map.addEdge(refList.get(i), refList.get(i + 1), egData);
							map.addEdge(refList.get(i + 1), refList.get(i), egData);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * calculate the Total Distance of a map from given file
	 * 
	 * @param filename
	 *            source file name
	 * @return totalDis Total Distance
	 */
	public double TotalDistance(String filename) {

		List<Edge<Eg>> eList = map.getEdges();

		double totalDis = 0;
		for (Edge<Eg> e : eList) {
			totalDis += e.getEdgeData().getEgData();
		}

		return totalDis / 2;
	}

	/**
	 * main method of part 2
	 */
	public static void main2(String[] args) {
		OSMMap aMap = new OSMMap();
		// aMap.LoadMap("maptest.txt");
		// double dis = aMap.TotalDistance("maptest.txt");
		aMap.LoadMap("AmesMap1.txt"); // change!!!!!!!!!!!
		double dis = aMap.TotalDistance("AmesMap1.txt");
		System.out.println(dis);
	}

	/**
	 * location class
	 */
	public static class Location {
		private double lat;
		private double lon;

		/**
		 * constructor of location
		 */
		public Location(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		/**
		 * get the latitude value
		 */
		public double getLatitude() {
			return this.lat;
		}

		/**
		 * get the longitude value
		 */
		public double getLongtitude() {
			return this.lon;
		}
	}

	/**
	 * get the closest vertex id for given location
	 * 
	 * @param location
	 *            the given position
	 * @return ID the name of the closest vertex
	 */
	public String ClosestRoad(Location location) {
		String ID = null;
		double aDis, Dis = Integer.MAX_VALUE;
		for (Vertex<Vert> v : map.getVertices()) {
			aDis = EgWeight(v.getVertexData().getLat(), v.getVertexData().getLon(), location.getLatitude(),
					location.getLongtitude());
			if (aDis < Dis && map.getNeighbors(v.getVertexName()).size() != 0) {
				Dis = aDis;

				ID = v.getVertexName();

			}
		}
		return ID;
	}

	/**
	 * get the shortest route for given locations
	 * 
	 * @param fromlocation
	 *            the start position
	 * @param tolocation
	 *            the end position
	 * @return route list of vertices that the route go through
	 */
	public List<String> ShortestRoute(Location fromLocation, Location toLocation) {
		List<String> route = new ArrayList<String>();
		String from = ClosestRoad(fromLocation);
		String to = ClosestRoad(toLocation);
		List<Edge<Eg>> sp = GraphAlgorithms.ShortestPath(map, from, to);// list for shortest path
		route.add(from);
		for (Edge<Eg> e : sp) {
			route.add(e.getVertexName2());
		}
		
		
		return route;
	}

	/**
	 * get the name list for a list of vertices
	 * 
	 * @param list
	 *            the name list of vertices
	 *
	 * @return streetName list of street names
	 */
	public List<String> StreetRoute(List<String> list) {
		List<String> streetName = new ArrayList<String>();
		List<String> streetName_noConsecDup = new ArrayList<String>();

		 for (int i = 0; i < list.size() - 1; i++) {
			if (map.isEdge(list.get(i), list.get(i + 1))) {
				try {
					streetName.add(map.getEdgeData(list.get(i), list.get(i + 1)).getEgName());
				} catch (NoSuchVertexException e) {
					e.printStackTrace();
				} catch (NoSuchEdgeException e) {
					e.printStackTrace();
				}
			}
		}
		
		streetName_noConsecDup.add(streetName.get(0));		
		// remove consecutive duplication
		for (int i = 1; i < streetName.size(); i++) {
			if (streetName.get(i - 1) != streetName.get(i)) {
				streetName_noConsecDup.add(streetName.get(i));
			}
		}
		return streetName_noConsecDup;
	}

	/**
	 * main method of part 3
	 */
	public static void main3(String[] args) throws IOException {
		String mapFile = args[0];
		String route = args[1];
		OSMMap aMap = new OSMMap();
		aMap.LoadMap(mapFile);
		List<Location> loList = new ArrayList<Location>();// list to store the locations from route file
		List<String> ShortestRout = new ArrayList<String>();
		List<String> subShortestRout;
		File file = new File(route);
		Scanner input = new Scanner(file);
		while (input.hasNextLine()) {
			String line = input.nextLine();
			String[] parts = line.split(" ");
			double part1 = Double.parseDouble(parts[0]);
			double part2 = Double.parseDouble(parts[1]);
			loList.add(new Location(part1, part2));
		}

		for (int i = 0; i < loList.size() - 1; i++) {
			subShortestRout = aMap.ShortestRoute(loList.get(i), loList.get(i + 1));
			for (int j = 0; j < subShortestRout.size(); j++) {
				ShortestRout.add(subShortestRout.get(j));
			}
		}
	
		List<String> streetRoute = aMap.StreetRoute(ShortestRout);
		List<String> streetRoute_noConsecDup = new ArrayList<String>();
		streetRoute_noConsecDup.add(streetRoute.get(0));	
		
		// remove consecutive duplication
		for (int i = 1; i < streetRoute.size(); i++) {
			if (streetRoute.get(i - 1) != streetRoute.get(i)) {
				streetRoute_noConsecDup.add(streetRoute.get(i));
			}
		}
		
		for (int i = 0; i < streetRoute_noConsecDup.size(); i++) {
			System.out.println(streetRoute_noConsecDup.get(i));
		}
	}

}
