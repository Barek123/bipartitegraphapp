package pl.marcb.util;

import pl.marcb.Parser;
import pl.marcb.lib.GifSequenceWriter;
import pl.marcb.steps.SharedData;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResultGeneratorUtil {
    public static void saveBipartiteStepToFile(Long index) throws IOException {
        SharedData shared = SharedData.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("graph {").append("\n");

        for (int i = 0; i < shared.points.size(); i++) {
            String value = shared.points.get(i).getValue();
            sb.append(value + "[color = " + shared.points.get(i).getColor().name() + "]");
        }
        sb.append("\n\n");

        for (int i = 0; i < shared.lines.size(); i++) {
            List<String> line = Arrays.stream(shared.lines.get(i).split(" ")).collect(Collectors.toList());
            sb.append("\t").append(String.join(" -- ", line)).append("\n");
        }
        sb.append("}");

        String fileName = "example/result/step-" + index + ".png";
        shared.images.add(fileName);
        new Parser("example/result/z-graph-for-step" + index + ".dot",
                fileName).parseFile(sb.toString());
    }

    public static void prepareDirectory() {
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

    public static void createGif() throws IOException {
        SharedData shared = SharedData.getInstance();
        if (shared.images.size() > 0) {
            BufferedImage first = ImageIO.read(new File(shared.images.get(0)));
            ImageOutputStream output = new FileImageOutputStream(new File("example/result/a-result.gif"));

            GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), 1000, true);
            writer.writeToSequence(first);


            for (int i = 1; i < shared.images.size(); i++) {
                BufferedImage next = ImageIO.read(new File(shared.images.get(i)));
                writer.writeToSequence(next);
            }

            writer.close();
            output.close();
        }
    }
}
