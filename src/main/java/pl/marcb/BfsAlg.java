package pl.marcb;

import pl.marcb.steps.BipartiteAlg;
import pl.marcb.steps.MatchingAlg;
import pl.marcb.steps.SharedData;
import pl.marcb.util.ResultGeneratorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BfsAlg {
    private SharedData sharedData = SharedData.getInstance();

    private BfsAlg(){}

    public BfsAlg(String path) throws IOException {
        sharedData.path = path;
        sharedData.lines = Files.readAllLines(Paths.get(path));
    }

    void generate() throws Exception {
        new BipartiteAlg().generate();
        new MatchingAlg().generate();
        ResultGeneratorUtil.createGif();
    }
}
