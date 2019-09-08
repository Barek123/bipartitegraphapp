package pl.marcb.steps;

import pl.marcb.model.ColorEnum;
import pl.marcb.model.Point;
import pl.marcb.util.ConnectionUtil;
import pl.marcb.util.ResultGeneratorUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BipartiteAlg implements AlgorithmInterface {
    private SharedData sharedData = SharedData.getInstance();

    @Override
    public void generate() throws Exception {
        generateDistinctGrayPoints();
        ResultGeneratorUtil.prepareDirectory();
        sharedData.stepIndex = 0L;
        sharedData.error = false;
        if (sharedData.points.size() > 0) {
            ResultGeneratorUtil.saveBipartiteStepToFile(sharedData.stepIndex);
            sharedData.stepIndex++;
            sharedData.points.get(0).setColor(SharedData.COLOR_1); // set color to first element
            nextStep(sharedData.points.get(0));
        }

        while (sharedData.points.stream().anyMatch(c -> c.getColor().equals(SharedData.DEFAULT_COLOR))) {
            Optional<Point> first = sharedData.points.stream().filter(c -> c.getColor().equals(SharedData.DEFAULT_COLOR)).findFirst();
            if (first.isPresent()) {
                nextStep(first.get());
            }
        }
    }

    private void nextStep(Point point) throws IOException {
        if (!sharedData.error) {
            ColorEnum newColor = (point.getColor().equals(SharedData.COLOR_1)) ? SharedData.COLOR_2 : SharedData.COLOR_1;

            List<Point> linksForPoint = getLinksForPointAndFilterByColor(point, newColor);

            if (linksForPoint.size() > 0) {
                if (linksForPoint.stream().anyMatch(c -> !c.getColor().equals(SharedData.DEFAULT_COLOR))) {
                    handleError(point);
                } else {
                    checkConnectedPoints(linksForPoint, newColor);
                }
            } else {
                handleSuccess();
            }
        }
    }

    private void handleSuccess() throws IOException {
        System.out.println("finished graph is bipartite");
    }

    private void checkConnectedPoints(List<Point> linksForPoint, ColorEnum color) throws IOException {
        linksForPoint.forEach(c -> setColorForPoint(c, color));
        ResultGeneratorUtil.saveBipartiteStepToFile(sharedData.stepIndex);
        sharedData.stepIndex++;
        for (int i = 0; i < linksForPoint.size(); i++) {
            nextStep(linksForPoint.get(i));
            sharedData.stepIndex++;
        }
    }

    private void handleError(Point point) throws IOException {
        this.setColorForPoint(point, SharedData.ERROR_COLOR);
        System.out.println("finished graph is not bipartite");
        sharedData.error = true;
        ResultGeneratorUtil.saveBipartiteStepToFile(sharedData.stepIndex);
        sharedData.stepIndex++;
    }

    private void setColorForPoint(Point point, ColorEnum color) {
        for (int i = 0; i < sharedData.points.size(); i++) {
            if (sharedData.points.get(i).getValue().equals(point.getValue())) {
                sharedData.points.get(i).setColor(color);
            }
        }
    }

    private void generateDistinctGrayPoints() {
        sharedData.points = sharedData.lines.stream().map(c -> Arrays.asList(c.split(" ")))
                .flatMap(Collection::stream).collect(Collectors.toSet())
                .stream().map(c -> new Point(c, SharedData.DEFAULT_COLOR)).collect(Collectors.toList());
    }

    private List<Point> getLinksForPointAndFilterByColor(Point point, ColorEnum filteredColor) {
        return ConnectionUtil.getLinksForPoint(point).stream()
                .filter(c -> !c.getColor().equals(filteredColor))
                .collect(Collectors.toList());
    }
}
