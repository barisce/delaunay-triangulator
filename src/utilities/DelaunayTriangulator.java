package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelaunayTriangulator {

	private List<Vector2D> pointSet;
	private TriangleSoup triangleSoup;

	public DelaunayTriangulator (List<Vector2D> pointSet) {
		this.pointSet = pointSet;
		this.triangleSoup = new TriangleSoup();
	}

	public void triangulate () throws NotEnoughPointsException {
		triangleSoup = new TriangleSoup();

		if (pointSet == null || pointSet.size() < 3) {
			throw new NotEnoughPointsException("Less than three points in point set.");
		}

		double maxOfAnyCoordinate = 0.0d;

		for (Vector2D vector : getPointSet()) {
			maxOfAnyCoordinate = Math.max(Math.max(vector.x, vector.y), maxOfAnyCoordinate);
		}

		maxOfAnyCoordinate *= 16.0d;

		Vector2D p1 = new Vector2D(0.0d, 3.0d * maxOfAnyCoordinate);
		Vector2D p2 = new Vector2D(3.0d * maxOfAnyCoordinate, 0.0d);
		Vector2D p3 = new Vector2D(-3.0d * maxOfAnyCoordinate, -3.0d * maxOfAnyCoordinate);

		Triangle2D superTriangle = new Triangle2D(p1, p2, p3);

		triangleSoup.add(superTriangle);

		for (Vector2D vector2D : pointSet) {
			Triangle2D triangle = triangleSoup.findContainingTriangle(vector2D);

			if (triangle == null) {
				Edge2D edge = triangleSoup.findNearestEdge(vector2D);

				Triangle2D first = triangleSoup.findOneTriangleSharing(edge);
				Triangle2D second = triangleSoup.findNeighbour(first, edge);

				Vector2D firstNonEdgeVertex = first.getNonEdgeVertex(edge);
				Vector2D secondNonEdgeVertex = second.getNonEdgeVertex(edge);

				triangleSoup.remove(first);
				triangleSoup.remove(second);

				Triangle2D triangle1 = new Triangle2D(edge.a, firstNonEdgeVertex, vector2D);
				Triangle2D triangle2 = new Triangle2D(edge.b, firstNonEdgeVertex, vector2D);
				Triangle2D triangle3 = new Triangle2D(edge.a, secondNonEdgeVertex, vector2D);
				Triangle2D triangle4 = new Triangle2D(edge.b, secondNonEdgeVertex, vector2D);

				triangleSoup.add(triangle1);
				triangleSoup.add(triangle2);
				triangleSoup.add(triangle3);
				triangleSoup.add(triangle4);

				legalizeEdge(triangle1, new Edge2D(edge.a, firstNonEdgeVertex), vector2D);
				legalizeEdge(triangle2, new Edge2D(edge.b, firstNonEdgeVertex), vector2D);
				legalizeEdge(triangle3, new Edge2D(edge.a, secondNonEdgeVertex), vector2D);
				legalizeEdge(triangle4, new Edge2D(edge.b, secondNonEdgeVertex), vector2D);
			} else {
				Vector2D a = triangle.a;
				Vector2D b = triangle.b;
				Vector2D c = triangle.c;

				triangleSoup.remove(triangle);

				Triangle2D first = new Triangle2D(a, b, vector2D);
				Triangle2D second = new Triangle2D(b, c, vector2D);
				Triangle2D third = new Triangle2D(c, a, vector2D);

				triangleSoup.add(first);
				triangleSoup.add(second);
				triangleSoup.add(third);

				legalizeEdge(first, new Edge2D(a, b), vector2D);
				legalizeEdge(second, new Edge2D(b, c), vector2D);
				legalizeEdge(third, new Edge2D(c, a), vector2D);
			}
		}

		triangleSoup.removeTrianglesUsing(superTriangle.a);
		triangleSoup.removeTrianglesUsing(superTriangle.b);
		triangleSoup.removeTrianglesUsing(superTriangle.c);
	}

	private void legalizeEdge (Triangle2D triangle, Edge2D edge, Vector2D newVertex) {
		Triangle2D neighbourTriangle = triangleSoup.findNeighbour(triangle, edge);

		if (neighbourTriangle != null) {
			if (neighbourTriangle.isPointInCircumcircle(newVertex)) {
				triangleSoup.remove(triangle);
				triangleSoup.remove(neighbourTriangle);

				Vector2D noneEdgeVertex = neighbourTriangle.getNonEdgeVertex(edge);

				Triangle2D firstTriangle = new Triangle2D(noneEdgeVertex, edge.a, newVertex);
				Triangle2D secondTriangle = new Triangle2D(noneEdgeVertex, edge.b, newVertex);

				triangleSoup.add(firstTriangle);
				triangleSoup.add(secondTriangle);

				legalizeEdge(firstTriangle, new Edge2D(noneEdgeVertex, edge.a), newVertex);
				legalizeEdge(secondTriangle, new Edge2D(noneEdgeVertex, edge.b), newVertex);
			}
		}
	}

	public boolean flipEdge (Vector2D point) {
		boolean flipped = false;

		Edge2D edge = triangleSoup.findNearestEdge(point);

		Triangle2D first = triangleSoup.findOneTriangleSharing(edge);
		Triangle2D second = triangleSoup.findNeighbour(first, edge);

		if (second != null) {
			flipped = true;
			Vector2D firstNonEdgeVertex = first.getNonEdgeVertex(edge);
			Vector2D secondNonEdgeVertex = second.getNonEdgeVertex(edge);

			triangleSoup.remove(first);
			triangleSoup.remove(second);

			Triangle2D triangle2 = new Triangle2D(firstNonEdgeVertex, secondNonEdgeVertex, edge.b);
			Triangle2D triangle3 = new Triangle2D(firstNonEdgeVertex, secondNonEdgeVertex, edge.a);

			triangleSoup.add(triangle2);
			triangleSoup.add(triangle3);
		}

		return flipped;
	}

	private List<Vector2D> getPointSet () {
		return pointSet;
	}

	public List<Triangle2D> getTriangles () {
		return triangleSoup.getTriangles();
	}

}