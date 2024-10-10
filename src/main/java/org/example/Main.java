package org.example;

public class Main {
    public static void main(String[] args) {
        final var path = "C:\\Users\\piotr\\IdeaProjects\\graphs\\graph7.txt";

        final var path2 = "graph7.txt";

        DifferentGraph g2;
        g2 = new DifferentGraph(path2);

        g2.AlgorithmCompleteRoots();

    }

}