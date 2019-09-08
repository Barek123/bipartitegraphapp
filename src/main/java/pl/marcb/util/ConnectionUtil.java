package pl.marcb.util;

import pl.marcb.model.Point;
import pl.marcb.steps.SharedData;

import java.util.*;
import java.util.stream.Collectors;

public class ConnectionUtil {
    private SharedData sharedData = SharedData.getInstance();

    private ConnectionUtil(){}


    public static List<Point> getLinksForPoint(Point point) {
        return new ArrayList<>(new ConnectionUtil().getConnectionsForPoint(point));
    }

    private Set<Point> getConnectionsForPoint(Point point) {
        Set<String> result = new HashSet<>();
        sharedData.lines.stream().map(c -> c.split(" "))
                .filter(c -> Arrays.asList(c).contains(point.getValue()))
                .forEach(c -> result.addAll(getConnectedPointsInLine(point.getValue(), c)));
        return result.stream().map(this::getPointByValue).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private Point getPointByValue(String value) {
        return sharedData.points.stream().filter(c -> c.getValue().equals(value)).findFirst().orElse(null);
    }

    private Set<String> getConnectedPointsInLine(String point, String[] line) {
        Set<String> result = new HashSet<>(); // set for distinct result
        for (int i = 0; i < line.length; i++) {
            if (line[i].equals(point)) {
                if (line.length > i + 1) { // get next point
                    result.add(line[i + 1]);
                }
                if (i - 1 >= 0) { //get previous point
                    result.add(line[i - 1]);
                }
            }
        }
        return result;
    }
}
