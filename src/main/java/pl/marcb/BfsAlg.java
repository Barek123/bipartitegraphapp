package pl.marcb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BfsAlg {
    public String path;
    private List<String> lines = new ArrayList<>();

    private List<Point> points = new ArrayList<>();

    private int genIndex = 0;

    public BfsAlg(String path) throws IOException {
        this.path = path;
        lines = Files.readAllLines(Paths.get(path));
    }

    public void generate() throws IOException {
        String firstValue = getFirstValue();
        generateGrayPoints();
        List<Link> linksFromValue = getLinksFromValue(firstValue);
        points = points.stream().map(c -> {
            return c.getValue().equals("1") ? new Point(c.value, ColorEnum.blue) : c;
        }).collect(Collectors.toList());
        generatePoints(1L);
        String st = "";
    }


    private void generatePoints(Long index) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("graph {").append("\n");

        for (int i = 0; i < points.size(); i++) {
            String value = points.get(i).value;
            sb.append(value + "[color = " + getPointColor(value) + "]");
        }
        sb.append("\n\n");

        for (int i = 0; i < lines.size(); i++) {
            List<String> line = Arrays.stream(lines.get(i).split(" ")).collect(Collectors.toList());
            sb.append("\t").append(String.join(" -- ", line)).append("\n");
        }
        sb.append("}");

        new Parser("C:\\Users\\bartek\\Desktop\\mgr\\bipartitegraphapp\\example\\graph2.dot",
                "C:\\Users\\bartek\\Desktop\\mgr\\bipartitegraphapp\\example\\ex4-" + index + ".png").parseFile(sb.toString());
    }

    private String getPointColor(String value) {
        List<Point> point = points.stream().filter(c -> c.getValue().equals(value)).collect(Collectors.toList());
        if (point.size() == 0) {
            return ColorEnum.gray.name();
        } else {
            return point.get(0).getColor().name();
        }
    }

    private void generateGrayPoints() {
        points = lines.stream().map(c -> Arrays.asList(c.split(" ")))
                .flatMap(Collection::stream).collect(Collectors.toSet())
                .stream().map(c -> new Point(c, ColorEnum.gray)).collect(Collectors.toList());
    }

    private List<Link> getLinksFromValue(String value) {
        return lines.stream().map(c -> c.split(" "))
                .filter(c -> c[0].equals(value))
                .map(c -> new Link(value, c[1]))
                .collect(Collectors.toList());
    }

    private String getFirstValue() {
        return lines.get(0).split(" ")[0];
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
        gray, blue, red, yellow;
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
