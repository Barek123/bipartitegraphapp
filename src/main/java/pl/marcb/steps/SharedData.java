package pl.marcb.steps;

import pl.marcb.model.ColorEnum;
import pl.marcb.model.Link;
import pl.marcb.model.Point;
import pl.marcb.model.PointDetail;

import java.util.ArrayList;
import java.util.List;

public class SharedData {
    // singleton
    private static SharedData instance;

    public List<String> lines = new ArrayList<>();
    public List<Point> points = new ArrayList<>();
    public List<String> images = new ArrayList<>();
    public List<Link> links = new ArrayList<>();

    public boolean error = false;
    public long stepIndex = 1;
    public String path;

    static ColorEnum DEFAULT_COLOR = ColorEnum.gray;
    static ColorEnum COLOR_1 = ColorEnum.blue;
    static ColorEnum COLOR_2 = ColorEnum.green;
    static ColorEnum ERROR_COLOR = ColorEnum.red;
    static ColorEnum CONNECTED_LINK = ColorEnum.blue;
    static ColorEnum PROCESSING_COLOR = ColorEnum.yellow;

    private SharedData() {
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }
}
