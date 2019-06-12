package utilities;

public class EdgeDistancePack implements Comparable<EdgeDistancePack> {

	Edge2D edge;
	private double distance;

	EdgeDistancePack (Edge2D edge, double distance) {
		this.edge = edge;
		this.distance = distance;
	}

	@Override
	public int compareTo (EdgeDistancePack o) {
		return Double.compare(this.distance, o.distance);
	}

}