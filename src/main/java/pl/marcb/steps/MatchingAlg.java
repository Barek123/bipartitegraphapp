package pl.marcb.steps;

import pl.marcb.model.ColorEnum;
import pl.marcb.model.Point;
import pl.marcb.model.PointDetail;
import pl.marcb.util.CombinationUtil;
import pl.marcb.util.ConnectionUtil;

import java.util.*;
import java.util.stream.Collectors;

public class MatchingAlg implements AlgorithmInterface {
    private SharedData sharedData = SharedData.getInstance();
    private List<PointDetail> pointDetailList = new ArrayList<>();
    private List<List<String>> combinations = new ArrayList<>();
    private ColorEnum connectionFromType = ColorEnum.gray;

    @Override
    public void generate() {
        pointDetailList = createPointDetails();
        connectionFromType = getConnectionFromType();
        combinations = getCombinationsForColor(connectionFromType);
        boolean availableMatching = checkConnections();
        System.out.println("\nmatching availability = " + availableMatching);

    }

    private boolean checkConnections() {
        List<Boolean> result = this.combinations.stream().map(this::checkConnectionForPoints).collect(Collectors.toList());
        return result.stream().allMatch(c -> c);
    }

    private boolean checkConnectionForPoints(List<String> list) {
        System.out.println("\ncheck for elements " + list.toString());
        Set<String> distinctPoints = new HashSet<>();
        list.forEach(c -> distinctPoints.addAll(getConnectionsForPoint(c)));
        System.out.println("\tavailable connections " + distinctPoints.toString());
        boolean result = list.size() <= distinctPoints.size();
        System.out.println("\t" + list.toString() + " size is " + ((result) ? "smaller or equal" : "bigger" ) + " than " + distinctPoints.toString());
        System.out.println("\t\t" + ((result) ? "correct " : "incorrect" ));
        return result;
    }

    private List<String> getConnectionsForPoint(String point) {
        Optional<PointDetail> first = this.pointDetailList.stream().filter(c -> point.equals(c.getPoint().getValue())).findFirst();
        return first.map(pointDetail -> pointDetail.getConnections().stream().map(Point::getValue).collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    private List<List<String>> getCombinationsForColor(ColorEnum color) {
        List<String> points = this.sharedData.points.stream()
                .filter(c -> c.getColor().equals(color))
                .map(Point::getValue)
                .collect(Collectors.toList());
        return CombinationUtil.getCombinations(points);
    }

    private ColorEnum getConnectionFromType() {
        long count1 = this.sharedData.points.stream().filter(c -> c.getColor().equals(SharedData.COLOR_1)).count();
        long count2 = this.sharedData.points.stream().filter(c -> c.getColor().equals(SharedData.COLOR_2)).count();
        return (count1 > count2) ? SharedData.COLOR_2 : SharedData.COLOR_1;
    }

    private List<PointDetail> createPointDetails() {
        return this.sharedData.points.stream().map(this::getPointDetail).collect(Collectors.toList());
    }

    private PointDetail getPointDetail(Point point) {
        return new PointDetail(point, ConnectionUtil.getLinksForPoint(point));
    }
}
