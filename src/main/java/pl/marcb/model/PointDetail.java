package pl.marcb.model;

import java.util.List;

public class PointDetail {
    private Point point;
    private List<Point> connections;

    public PointDetail() {
    }

    public PointDetail(Point point, List<Point> connections) {
        this.point = point;
        this.connections = connections;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public List<Point> getConnections() {
        return connections;
    }

    public void setConnections(List<Point> connections) {
        this.connections = connections;
    }

    public int getConnectionsCount() {
        return this.connections.size();
    }
}
