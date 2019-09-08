package pl.marcb.model;

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
