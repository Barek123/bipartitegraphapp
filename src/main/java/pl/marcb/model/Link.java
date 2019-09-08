package pl.marcb.model;

public class Link {
    private Point from;
    private Point to;
    private ColorEnum color = ColorEnum.gray;

    public Link() {
    }

    public Link(Point from, Point to) {
        this.from = from;
        this.to = to;
    }

    public Link(Point from, Point to, ColorEnum color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }

    public Point getFrom() {
        return from;
    }

    public void setFrom(Point from) {
        this.from = from;
    }

    public Point getTo() {
        return to;
    }

    public void setTo(Point to) {
        this.to = to;
    }

    public ColorEnum getColor() {
        return color;
    }

    public void setColor(ColorEnum color) {
        this.color = color;
    }
}
