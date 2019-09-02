package pl.marcb;

import pl.marcb.lib.GifSequenceWriter;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BfsAlg {
    private List<String> lines = new ArrayList<>();

    private List<Point> points = new ArrayList<>();

    private ColorEnum DEFAULT_COLOR = ColorEnum.gray;
    private ColorEnum COLOR_1 = ColorEnum.blue;
    private ColorEnum COLOR_2 = ColorEnum.green;
    private ColorEnum ERROR_COLOR = ColorEnum.red;

    private long stepIndex = 1;
    private boolean error = false;

    private List<String> images = new ArrayList<>();

    public BfsAlg(String path) throws IOException {
        lines = Files.readAllLines(Paths.get(path));
    }

    void generate() throws IOException {
        generateDistinctGrayPoints();
        prepareDirectory();
        stepIndex = 0L;
        error = false;
        if (points.size() > 0) {
            saveStepToFile(stepIndex);
            stepIndex ++;
            points.get(0).setColor(COLOR_1); // set color to first element
            nextStep(points.get(0));
        }
        createGif();
    }

    private void nextStep(Point point) throws IOException {
        if (!error) {
            ColorEnum newColor = (point.getColor().equals(COLOR_1)) ? COLOR_2 : COLOR_1;

            List<Point> linksForPoint = getLinksForPointAndFilterByColor(point, newColor);

            if (linksForPoint.size() > 0) {
                if (linksForPoint.stream().anyMatch(c -> !c.getColor().equals(DEFAULT_COLOR))) {
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
        saveStepToFile(stepIndex);
        stepIndex++;
        for (int i = 0; i < linksForPoint.size(); i++) {
            nextStep(linksForPoint.get(i));
            stepIndex++;
        }
    }

    private void handleError(Point point) throws IOException {
        this.setColorForPoint(point, ERROR_COLOR);
        System.out.println("finished graph is not bipartite");
        error = true;
        saveStepToFile(stepIndex);
        stepIndex++;
    }

    private void setColorForPoint(Point point, ColorEnum color) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getValue().equals(point.getValue())) {
                points.get(i).setColor(color);
            }
        }
    }

    private void generateDistinctGrayPoints() {
        points = lines.stream().map(c -> Arrays.asList(c.split(" ")))
                .flatMap(Collection::stream).collect(Collectors.toSet())
                .stream().map(c -> new Point(c, DEFAULT_COLOR)).collect(Collectors.toList());
    }

    private List<Point> getLinksForPointAndFilterByColor(Point point, ColorEnum filteredColor) {
        return getLinksForPoint(point).stream()
                .filter(c -> !c.getColor().equals(filteredColor))
                .collect(Collectors.toList());
    }

    private List<Point> getLinksForPoint(Point point) {
        Set<String> result = new HashSet<>();
        lines.stream().map(c -> c.split(" "))
                .filter(c -> Arrays.asList(c).contains(point.value))
                .forEach(c -> result.addAll(getConnectedPointsInLine(point.value, c)));
        return result.stream().map(this::getPointByValue).collect(Collectors.toList());
    }

    private Point getPointByValue(String value) {
        return this.points.stream().filter(c -> c.value.equals(value)).findFirst().orElse(null);
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

    private void saveStepToFile(Long index) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("graph {").append("\n");

        for (int i = 0; i < points.size(); i++) {
            String value = points.get(i).value;
            sb.append(value + "[color = " + points.get(i).getColor().name() + "]");
        }
        sb.append("\n\n");

        for (int i = 0; i < lines.size(); i++) {
            List<String> line = Arrays.stream(lines.get(i).split(" ")).collect(Collectors.toList());
            sb.append("\t").append(String.join(" -- ", line)).append("\n");
        }
        sb.append("}");

        String fileName = "example/result/step-" + index + ".png";
        this.images.add(fileName);
        new Parser("example/result/z-graph-for-step" + index + ".dot",
                fileName).parseFile(sb.toString());
    }

    private void prepareDirectory() {
        Path path = Paths.get("example/result");
        if (path.toFile().exists()) {
            File[] files = path.toFile().listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
            path.toFile().delete();
        }
        new File("example/result").mkdirs();
    }

    private void createGif() throws IOException {
        if (images.size() > 0) {
            BufferedImage first = ImageIO.read(new File(images.get(0)));
            ImageOutputStream output = new FileImageOutputStream(new File("example/result/a-result.gif"));

            GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), 1000, true);
            writer.writeToSequence(first);


            for (int i = 1; i < images.size(); i++) {
                BufferedImage next = ImageIO.read(new File(images.get(i)));
                writer.writeToSequence(next);
            }

            writer.close();
            output.close();
        }
    }

    public class Point {
        private String value;
        private ColorEnum color;

        public Point(String value, ColorEnum color) {
            this.value = value;
            this.color = color;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public ColorEnum getColor() {
            return color;
        }

        public void setColor(ColorEnum color) {
            this.color = color;
        }
    }

    private enum ColorEnum {
        gray, blue, green, red, yellow;
    }

    public class Link {
        private String from;
        private String to;

        public Link(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }
}
