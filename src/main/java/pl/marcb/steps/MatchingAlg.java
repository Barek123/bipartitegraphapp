package pl.marcb.steps;

import pl.marcb.model.ColorEnum;
import pl.marcb.model.Link;
import pl.marcb.model.Point;
import pl.marcb.model.PointDetail;
import pl.marcb.util.CombinationUtil;
import pl.marcb.util.ConnectionUtil;
import pl.marcb.util.ResultGeneratorUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MatchingAlg implements AlgorithmInterface {
    private SharedData sharedData = SharedData.getInstance();
    private List<PointDetail> pointDetailList = new ArrayList<>();
    private List<List<String>> combinations = new ArrayList<>();
    private ColorEnum connectionFromType = SharedData.DEFAULT_COLOR;

    private Queue<Point> queuedPoints = new LinkedList<>();

    @Override
    public void generate() throws Exception {
        if (!sharedData.error) {
            pointDetailList = createPointDetails();
            connectionFromType = getConnectionFromType();
            combinations = getCombinationsForColor(connectionFromType);
            boolean availableMatching = checkConnections();
            System.out.println("\nmatching availability = " + availableMatching);
            if (availableMatching) {
                searchMatching();
            } else {
                sharedData.error = true;
            }
        }
    }

    private void searchMatching() throws IOException {
        List<Point> shortElementList = this.sharedData.points.stream().filter(c -> c.getColor().equals(connectionFromType)).collect(Collectors.toList());

        queuedPoints.addAll(shortElementList);
        while (sharedData.links.size() < shortElementList.size() && !sharedData.error) {
            searchMatchingForNextPoint();
        }
    }

    private void searchMatchingForNextPoint() throws IOException {
        Point first = queuedPoints.peek();
        if (first != null) {
            Optional<PointDetail> pointDetail = pointDetailList.stream().filter(c -> c.getPoint().getValue().equals(first.getValue())).findFirst();
            if (pointDetail.isPresent()) {
                List<Point> availableMatch = pointDetail.get().getConnections().stream().filter(c -> !linkToPointExist(c)).collect(Collectors.toList());
                if (availableMatch.size() > 0) {
                    queuedPoints.poll();
                    sharedData.links.add(new Link(first, availableMatch.get(0), SharedData.CONNECTED_LINK));
                    generateScreen();
                } else {
                    ColorEnum oldColor = first.getColor();
                    changeColorInPoint(first, SharedData.PROCESSING_COLOR);
                    generateScreen();

                    List<Point> usedMatch = pointDetail.get().getConnections().stream().filter(this::linkToPointExist).collect(Collectors.toList());
                    for (int i = 0; i < usedMatch.size(); i++) {
                        boolean resolved = tryResolveConflictForPoint(usedMatch.get(i));
                        if (resolved) {
                            changeColorInPoint(first, oldColor);
                            generateScreen();
                            i = usedMatch.size(); // exit
                        }
                    }

                    changeColorInPoint(first, oldColor);
                    generateScreen();
                    removeUsedMatch(usedMatch);
                }
            }
        }
    }

    private void removeUsedMatch(List<Point> usedMatch) throws IOException {
        List<String> usedMatchStrings = usedMatch.stream().map(Point::getValue).collect(Collectors.toList());
        List<Link> linksToRemove = sharedData.links.stream().filter(c -> usedMatchStrings.contains(c.getTo().getValue())).collect(Collectors.toList());

        for (int i = 0; i < linksToRemove.size(); i++) {
            changeColorInLink(linksToRemove.get(i), SharedData.PROCESSING_COLOR);
            generateScreen();
        }

        linksToRemove.stream().map(Link::getFrom)
                .forEach(c -> queuedPoints.add(c));
        sharedData.links = sharedData.links.stream().filter(c -> !usedMatchStrings.contains(c.getTo().getValue())).collect(Collectors.toList());
    }

    private boolean tryResolveConflictForPoint(Point usedPoint) throws IOException {
        Optional<Link> first = sharedData.links.stream().filter(c -> c.getTo().getValue().equals(usedPoint.getValue())).findFirst();
        if (first.isPresent()) {
            changeColorInLink(first.get(), SharedData.ERROR_COLOR);
            generateScreen();
            Optional<PointDetail> pointDetail = pointDetailList.stream().filter(c -> c.getPoint().getValue().equals(first.get().getFrom().getValue())).findFirst();
            if (pointDetail.isPresent()) {
                List<Point> availableMatch = pointDetail.get().getConnections().stream().filter(c -> !linkToPointExist(c)).collect(Collectors.toList());
                if (availableMatch.size() > 0) {
                    changeColorInLink(first.get(), SharedData.CONNECTED_LINK);
                    generateScreen();
                    return changeLink(first.get().getFrom(), availableMatch.get(0));
                }
            }
            changeColorInLink(first.get(), SharedData.CONNECTED_LINK);
        }

        return false;
    }

    private void changeColorInLink(Link link, ColorEnum color) {
        for (int i = 0; i < sharedData.links.size(); i++) {
            if (link.getFrom().getValue().equals(sharedData.links.get(i).getFrom().getValue()) &&
                    link.getTo().getValue().equals(sharedData.links.get(i).getTo().getValue())) {
                sharedData.links.get(i).setColor(color);
            }
        }
    }

    private void changeColorInPoint(Point point, ColorEnum color) {
        for (int i = 0; i < sharedData.points.size(); i++) {
            if (sharedData.points.get(i).getValue().equals(point.getValue())) {
                sharedData.points.get(i).setColor(color);
            }
        }
    }

    private boolean changeLink(Point from, Point newPoint) {
        for (int i = 0; i < sharedData.links.size(); i++) {
            if (sharedData.links.get(i).getFrom().getValue().equals(from.getValue())) {
                sharedData.links.get(i).setTo(newPoint);
                return true;
            }
        }
        return false;
    }


    private boolean linkToPointExist(Point to) {
        return sharedData.links.stream().map(c -> c.getTo().getValue()).collect(Collectors.toList()).contains(to.getValue());
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
        System.out.println("\t" + list.toString() + " size is " + ((result) ? "smaller or equal" : "bigger") + " than " + distinctPoints.toString());
        System.out.println("\t\t" + ((result) ? "correct " : "incorrect"));
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

    private void generateScreen() throws IOException {
        sharedData.stepIndex++;
        ResultGeneratorUtil.saveMatchingStepToFile(sharedData.stepIndex);
    }
}
