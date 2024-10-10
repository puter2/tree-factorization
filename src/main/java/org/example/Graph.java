package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Graph {
    int verticesNumber;
    ArrayList<Integer> activeVertices;
    ArrayList<ArrayList<Integer>> neighborhoods;
    ArrayList<ArrayList<Integer>> activeNeighborhoods;

    static int[] visitedVertices;

    static int counter = -1;


    public Graph(){
        verticesNumber = 0;
        activeNeighborhoods = new ArrayList<>();
        neighborhoods = new ArrayList<>();
        activeVertices = new ArrayList<>();
    }
    public Graph(String path) {
        File file = new File(path);
        //to input a graph you have to put neighbors lists into graph.txt
        try {
            Scanner myReader = new Scanner(file);
            //counting number of vertices
            int VertNum = 0;
            while (myReader.hasNextLine()) {
                VertNum++;
                myReader.nextLine();
            }
            verticesNumber = VertNum;
            myReader.close();
            activeVertices = new ArrayList<>();
            for (int x = 0; x < verticesNumber; x++)
                activeVertices.add(x);
            //making neighborhood lists
            neighborhoods = new ArrayList<>();
            activeNeighborhoods = new ArrayList<>();
            myReader = new Scanner(file);
            while(myReader.hasNextLine()){

                var data = myReader.nextLine().split(" ");
                ArrayList<Integer> neighborhood = new ArrayList<>();

                for(String element : data){
                    int help = Integer.parseInt(element.replaceAll("\\s",""));
                    neighborhood.add(help);
                }
                neighborhoods.add(neighborhood);
                activeNeighborhoods.add(neighborhood);
                //next vertex
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void activate(int vertex){
        if(!this.activeVertices.contains(vertex))
            this.activeVertices.add(vertex);
    }

    void activateAll(){
        for(int vertex=0;vertex<this.verticesNumber;vertex++)
            this.activate(vertex);
    }

    void print(){
        for(int vertex : this.activeVertices){
            var help = this.neighborhoods.get(vertex);
            System.out.println("neighborhood of vertex "+vertex+": "+help.stream().filter(i -> this.activeVertices.contains(i)).toList());
        }
    }

    Graph copy(){
        Graph g2 = new Graph();
        g2.activeVertices.addAll(this.activeVertices);
        g2.neighborhoods.addAll(this.neighborhoods);
        g2.activeNeighborhoods.addAll(this.activeNeighborhoods);
        g2.verticesNumber = this.verticesNumber;
        return g2;
    }

    int degree(int vertex){
        var help = this.neighborhoods.get(vertex);
        return help.stream().filter(i -> this.activeVertices.contains(i)).toList().size();
    }

    //debug function
    void printParams(){
        System.out.println("active vertices: "+this.activeVertices);
        System.out.println("neighborhoods: "+this.neighborhoods);
        System.out.println("active neighborhoods: "+this.activeNeighborhoods);
    }

    void deactivateVertex(int x){
        this.activeVertices.remove((Object)x);
    }

    Graph cutLeaves(){
        Graph result = this.copy();
        ArrayList<Integer> inactiveVertices = new ArrayList<>();

        //checking which vertex to cut
        for(int vertex : this.activeVertices){
            if(this.degree(vertex)==1){
                inactiveVertices.add(vertex);
            }
        }
        //deactivating vertices
        for(int element : inactiveVertices){
           // System.out.println(result.activeVertices.remove(element));
            result.deactivateVertex(element);
        }


        return result;
    }

    String findCentre(){
        Graph g = this.copy();
        while(g.activeVertices.size()>2)
            g=g.cutLeaves();

        if(g.activeVertices.size()==1)
            return g.activeVertices.get(0).toString();
        else
            return g.activeVertices.get(0).toString()+"-"+g.activeVertices.get(1).toString();
    }

    int height(){
        Graph g = this.copy();
        int height = 0;
        while (g.activeVertices.size()>2){
            height++;
            g=g.cutLeaves();
        }
        return height;
    }

    void resetDFS(){
        visitedVertices = new int[this.verticesNumber];
        for(int x=0;x<this.verticesNumber;x++)
            visitedVertices[x] = -1;
        counter=0;

    }
    void DFS(int start){

        visitedVertices[start]=counter;

        var help = this.neighborhoods.get(start);
        for(int vertex : help.stream().filter(i -> this.activeVertices.contains(i)).toList()){
            if(visitedVertices[vertex]==-1)
            {
                DFS(vertex);
                //counter++;
            }
        }
    }

    boolean checkConnectivity(){
        this.resetDFS();
        this.DFS(0);
        for(int x=0;x<this.verticesNumber;x++)
            if(visitedVertices[x]!=0)
                return false;
        return true;
    }

    int numberOfComponents(){
        this.resetDFS();
        for(int x=0;x<this.verticesNumber;x++)
            if(visitedVertices[x]==-1 & this.activeVertices.contains((Object)x)) {
                this.DFS(x);
                counter++;
            }
        return Arrays.stream(visitedVertices).max().getAsInt()+1;
    }

    void deactivateAll(){
        this.activeVertices.clear();
    }

    Graph[] split(){
        //split graph into two or more if it is not connected
        Graph[] components = new Graph[this.numberOfComponents()];
        for(int x=0;x<this.numberOfComponents();x++) {
            components[x] = this.copy();
            components[x].deactivateAll();
            for(int y = 0;y<this.verticesNumber;y++){
                if(visitedVertices[y]==x)
                    components[x].activate(y);
            }
        }

        return components;
    }

    Graph normalizeLabels(){
        Graph result = new Graph();
        //adding vertex
        for(int vertex : this.activeVertices){
            result.addVertex();
        }

        Dictionary dictionary = new Hashtable<Integer,Integer>();
        int counter = 0;
        for(int vertex : this.activeVertices){
            dictionary.put(vertex,counter);
            counter++;
        }

        //adding edges
        for(int vertex : this.activeVertices){
            var help = this.neighborhoods.get(vertex);
            for(int x : help.stream().filter(i -> this.activeVertices.contains(i)).toList()){
                result.addEdge((int)dictionary.get(vertex),(int)dictionary.get(x));
            }

        }
        result.activateAll();
        return result;

    }

    void addVertex(){
        this.activeVertices.add(verticesNumber);
        this.neighborhoods.add(new ArrayList<>());
        this.verticesNumber++;
    }

    void addEdge(int a, int b){
        if(!this.neighborhoods.get(a).contains(b)) {
            this.neighborhoods.get(a).add(b);
            this.neighborhoods.get(b).add(a);
        }
    }

    int[] getVerticesHeight(){
        int[] labels = new int[this.verticesNumber];
        for(int x=0;x<this.verticesNumber;x++)
            labels[x] = this.height();
        Graph help = this.copy();
        while (help.activeVertices.size()>2){
            help=help.cutLeaves();
            for (int x=0;x<this.verticesNumber;x++)
                if(!help.activeVertices.contains((Object)x))
                    labels[x]--;
        }
        return labels;
    }

    String encode(){
        //tree encoding, needed for isomorphism
        int[] heights = this.getVerticesHeight();
        if(this.findCentre().contains("-")){
            heights[Integer.parseInt(this.findCentre().split("-")[0])]++;
        }
        String[] codes = new String[this.verticesNumber];
        for(int x=0;x<this.verticesNumber;x++) {
                codes[x] = "";
        }

        boolean stop = true;
        int counter = 0;
        ArrayList<String> help = new ArrayList<>();
        while (true){
            for(int x=0;x<this.verticesNumber;x++){
                help.clear();
                if(this.getVerticesHeight()[x]==counter){
                    for(int vertex : this.neighborhoods.get(x)){
                        if(heights[vertex]<counter) {
                            help.add(codes[vertex]);
                        }
                    }
                    help.sort(Comparator.comparingInt(String::length));
                    for(String code : help){
                        codes[x]=code+codes[x];
                    }
                    codes[x]="("+codes[x]+")";
                }
            }
            counter++;
            stop = false;
            for(int x=0;x<this.verticesNumber;x++)
                if (Objects.equals(codes[x], "")) {
                    stop = true;
                    break;
                }

            if(!stop)
                break;
        }


        String result = "";
        for(String code : codes)
            if(code.length()>result.length())
                result=code;

        return result;
    }

    Boolean isIsomorphic(Graph g2){
        if(this.verticesNumber!=g2.verticesNumber ||this.height()!=this.height() || this.findCentre().length()!=this.findCentre().length()||this.activeVertices.size()!=g2.activeVertices.size())
            return false;

        if(this.encode().equals(g2.encode()) ||(this.activeVertices.size()==1 & g2.activeVertices.size()==1))
            return true;
        else
            return false;
    }

    void removeEdge(int a, int b){
        this.neighborhoods.get(b).remove((Object)a);
        this.neighborhoods.get(a).remove((Object)b);
    }


    void removeMultipleEdges(ArrayList<Integer> vertices){
        if(vertices.size()>1)
        for(int vertex1 : vertices){
            for(int vertex2 : vertices){
                this.removeEdge(vertex1,vertex2);
            }
            if(this.degree(vertex1)==0){
                this.deactivateVertex(vertex1);
            }
        }
    }

    Graph(Graph g){
        this.activeVertices = new ArrayList<>();
        for(int vertex : g.activeVertices)
            this.activeVertices.add(vertex);
        this.verticesNumber = g.verticesNumber;
        this.neighborhoods = new ArrayList<>();
        this.activeNeighborhoods = new ArrayList<>();
        int counter=0;
        for(ArrayList<Integer> list : g.neighborhoods){
            this.neighborhoods.add(new ArrayList<>());
            this.activeNeighborhoods.add(new ArrayList<>());
            for(int number : list){
                this.neighborhoods.get(counter).add(number);
                this.activeNeighborhoods.get(counter).add(number);
            }
            counter++;
        }
    }

    Graph alghorithmCorrect(){
        if(this.findCentre().contains("-")){
            Graph help;
            int height = this.height();
            Graph help2;
            Graph result = new Graph(this);
            boolean iso = true;
            while (true){
                help = new Graph(this);
                help2 = new Graph(this);
                iso = true;
                for(int x=0;x<height;x++){
                    help2=help2.cutLeaves();
                }
                help.removeMultipleEdges(help2.activeVertices);

                if(!help2.activeVertices.isEmpty() & help.split().length>1)
                    for(Graph component : help.split()){
                        if(!help.split()[0].isIsomorphic(component) || height==0) {
                            //help.print();
                            iso = false;
                        }
                    }
                if(iso)
                    break;
                height--;
            }

            help = this.copy();
            for(int x=0; x<height;x++)
                result = result.cutLeaves();

            return result;
        }
        else{
            Graph help;
            int height = this.height();
            Graph help2;
            Graph result = new Graph(this);
            boolean iso = true;
            while (true){
                help = new Graph(this);
                help2 = new Graph(this);
                iso = true;
                for(int x=0;x<height-1;x++){
                    help2=help2.cutLeaves();
                }
                help.removeMultipleEdges(help2.activeVertices);

                if(!help2.activeVertices.isEmpty() & help.split().length>1)
                    for(Graph component : help.split()){
                        if(!help.split()[0].isIsomorphic(component) || height==0) {
                            //help.print();
                            iso = false;
                        }
                    }
                if(iso)
                    break;
                height--;
            }

            help = this.copy();
            for(int x=0; x<height-1;x++)
                result = result.cutLeaves();

            return result;
        }
    }

    public ArrayList<Graph> completeAlghorithm() {
        Graph help = new Graph(this);
        if(help.equals(help.alghorithmCorrect())) {
            ArrayList<Graph> result = new ArrayList<>();
            result.add(help);
            return result;
        }
        else {
            Graph g1 = new Graph(help.alghorithmCorrect());
            Graph g2 = new Graph(help);

            for (int vertex : g1.activeVertices) {
                for (int vertex2 : g1.activeVertices) {
                    g2.removeEdge(vertex, vertex2);
                }
            }
            g2 = g2.split()[0];

            ArrayList<Graph> factorization1 = g1.completeAlghorithm();
            ArrayList<Graph> factorization2 = g2.completeAlghorithm();
            ArrayList<Graph> result = new ArrayList<>();
            for (Graph graph :
                    factorization1) {
                result.add(graph);
            }
            for (Graph graph :
                    factorization2) {
                result.add(graph);
            }
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph that = (Graph) o;
        return verticesNumber == that.verticesNumber && Objects.equals(activeVertices, that.activeVertices) && Objects.equals(neighborhoods, that.neighborhoods) && Objects.equals(activeNeighborhoods, that.activeNeighborhoods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verticesNumber, activeVertices, neighborhoods, activeNeighborhoods);
    }

    void AlgorithmCompleteRoots(){
        Graph first_factor = new Graph(this.alghorithmCorrect());
        Graph second_factor = new Graph(this);
        for (int vertex : first_factor.activeVertices) {
            for (int vertex2 : first_factor.activeVertices) {
                second_factor.removeEdge(vertex, vertex2);
            }
        }
        second_factor = second_factor.split()[0];
        ArrayList<Graph> first_factors = new ArrayList<>();
        first_factors.add(first_factor);
        ArrayList<Integer> roots = new ArrayList<>();
        int root=0;

        while (second_factor.activeVertices.size()>=2){
            for (int vertex1 : first_factor.activeVertices) {
                if(second_factor.activeVertices.contains(vertex1)){
                    root = vertex1;
                    break;
                }
            }
            roots.add(root);

            first_factor = new Graph(second_factor.alghorithmCorrect()) ;
            for (int vertex : first_factor.activeVertices) {
                for (int vertex2 : first_factor.activeVertices) {
                    second_factor.removeEdge(vertex, vertex2);
                }
            }
            second_factor=new Graph(second_factor.split()[0]);
            first_factors.add(first_factor);
        }

        System.out.println("factors: ");
        for(int i=0;i<first_factors.size();i++){
            System.out.println("g"+i+": ");
            first_factors.get(i).print();
        }

        //displaying roots, not implemented
//        ArrayList<Integer> rootsBackwards = new ArrayList<>();
//
//        for (int i=roots.size()-1;i>-1;i--){
//            rootsBackwards.add(roots.get(i));
//        }
//
//        System.out.println("roots: "+rootsBackwards);
//
//        StringBuilder result = new StringBuilder("g0");
//
//        for(int i=1;i<first_factors.size();i++){
//            result.append("+");
//            if(i!=first_factors.size()-1)
//                result.append("(");
//            result.append("g").append(i);
//        }
//        for(int i=1;i<first_factors.size()-1;i++)
//            result.append(")");
//        System.out.println(result);
//    }
    }

}
