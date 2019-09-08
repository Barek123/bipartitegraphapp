package pl.marcb.steps;

import pl.marcb.model.ColorEnum;
import pl.marcb.model.Point;
import pl.marcb.model.PointDetail;
import pl.marcb.util.CombinationUtil;
import pl.marcb.util.ConnectionUtil;

import java.util.ArrayList;
import java.util.List;
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

    }

    private boolean checkConnections() {

        return false;
    }
    private boolean checkConnectionForPoints(List<String> list) {

        return false;
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
