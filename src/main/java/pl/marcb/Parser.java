package pl.marcb;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class Parser {
    private String graphFile;
    private String imageFile;

    public Parser(String graphFile, String imageFile) {
        this.graphFile = graphFile;
        this.imageFile = imageFile;
    }

    public void parseFile() throws IOException {
        MutableGraph g = guru.nidi.graphviz.parse.Parser.read(new File(graphFile));
        Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File(imageFile));
    }

    public void parseFile(String dotFile) throws IOException {
        Files.write(Paths.get(graphFile), Collections.singleton(dotFile), StandardCharsets.UTF_8);
        MutableGraph g = guru.nidi.graphviz.parse.Parser.read(dotFile);
        Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File(imageFile));
    }
}
