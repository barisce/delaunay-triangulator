package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TriangleSoup {

	private List<Triangle2D> triangleSoup;

	TriangleSoup () {
		this.triangleSoup = new ArrayList<>();
	}

	void add (Triangle2D triangle) {
		this.triangleSoup.add(triangle);
	}

	void remove (Triangle2D triangle) {
		this.triangleSoup.remove(triangle);
	}

	List<Triangle2D> getTriangles () {
		return this.triangleSoup;
	}

	Triangle2D findContainingTriangle (Vector2D point) {
		for (Triangle2D triangle : triangleSoup) {
			if (triangle.contains(point)) {
				return triangle;
			}
		}
		return null;
	}

	Triangle2D findNeighbour (Triangle2D triangle, Edge2D edge) {
		for (Triangle2D triangleFromSoup : triangleSoup) {
			if (triangleFromSoup.isNeighbour(edge) && triangleFromSoup != triangle) {
				return triangleFromSoup;
			}
		}
		return null;
	}

	Triangle2D findOneTriangleSharing (Edge2D edge) {
		for (Triangle2D triangle : triangleSoup) {
			if (triangle.isNeighbour(edge)) {
				return triangle;
			}
		}
		return null;
	}

	Edge2D findNearestEdge (Vector2D point) {
		List<EdgeDistancePack> edgeList = new ArrayList<>();

		for (Triangle2D triangle : triangleSoup) {
			edgeList.add(triangle.findNearestEdge(point));
		}

		EdgeDistancePack[] edgeDistancePacks = new EdgeDistancePack[edgeList.size()];
		edgeList.toArray(edgeDistancePacks);

		Arrays.sort(edgeDistancePacks);
		return edgeDistancePacks[0].edge;
	}

	void removeTrianglesUsing (Vector2D vertex) {
		List<Triangle2D> trianglesToBeRemoved = new ArrayList<>();

		for (Triangle2D triangle : triangleSoup) {
			if (triangle.hasVertex(vertex)) {
				trianglesToBeRemoved.add(triangle);
			}
		}

		triangleSoup.removeAll(trianglesToBeRemoved);
	}

}