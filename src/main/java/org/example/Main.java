package org.example;

public class Main {
    public static void main(String[] args) {
        final var path = "C:\\Users\\piotr\\IdeaProjects\\graphs\\graph7.txt";

        final var path2 = "graph7.txt";

        DifferentGraph g2;
        g2 = new DifferentGraph(path2);
        /*
        System.out.println("first factor");
        g2.alghorithmCorrect().print();

        DifferentGraph g3 = new DifferentGraph(g2);
        g3.removeEdge(4,8);
        System.out.println("second factor");
        g3.split()[0].print();
        DifferentGraph g4 = new DifferentGraph(g3.split()[0]);
        System.out.println("factorization of second factor");
        g4.alghorithmCorrect().print();
        //it should work backwards
        DifferentGraph g5 = new DifferentGraph(g4);
        g5.removeEdge(3,4);
        System.out.println("factor factor");
        g5.split()[0].print();


         */
        //make it into a loop

//        for (DifferentGraph graph :
//                g2.completeAlghorithm()) {
//            System.out.println();
//            graph.print();
//        }

        g2.AlgorithmCompleteRoots();

    }

    static boolean isItInArray(int[] Array, int a){
        for(int x=0;x<Array.length;x++)
            if(Array[x]==a)
                return true;
        return false;
    }
}