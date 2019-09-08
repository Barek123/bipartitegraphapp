package pl.marcb;


import pl.marcb.steps.SharedData;


public class Main {
    public static void main(String[] args) throws Exception {
        SharedData.getInstance().path = "example/graph";
        new BfsAlg("example/graph").generate();
    }
}



