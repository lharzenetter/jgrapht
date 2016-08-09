package org.jgrapht.alg.flow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumSourceSinkCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Map;

/**
 * Created by jkinable on 8/8/16.
 */
public abstract class MinimumSourceSinkCutTest extends MaximumFlowMinimumCutAlgorithmTestBase{

    abstract MinimumSourceSinkCutAlgorithm<Integer, DefaultWeightedEdge> createSolver(Graph<Integer, DefaultWeightedEdge> network);


    private void runTestDirected(DirectedGraph<Integer, DefaultWeightedEdge> network,
                                 int[] sources,
                                 int[] sinks,
                                 double[] expectedResults)
    {
        assertTrue(sources.length == sinks.length);

        MinimumSourceSinkCutAlgorithm<Integer, DefaultWeightedEdge> solver = createSolver(network);

        //Calculate the max flow for each source/sink pair
        for (int i = 0; i < sources.length; i++) {
            verifyDirected(sources[i], sinks[i], expectedResults[i], network, solver.buildMaximumFlow(sources[i], sinks[i]));
        }
    }

    static void verifyDirected(int source, int sink, double expectedResult, DirectedGraph<Integer, DefaultWeightedEdge> network, MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlow) {
        Double flowValue = maxFlow.getValue();
        Map<DefaultWeightedEdge, Double> flow = maxFlow.getFlow();

        //Verify that the maximum flow value
        assertEquals(
                expectedResult,
                flowValue,
                EdmondsKarpMFImpl.DEFAULT_EPSILON);

        //Verify that every edge is contained in the flow map
        for (DefaultWeightedEdge e : network.edgeSet()) {
            assertTrue(flow.containsKey(e));
        }

        //Verify that the flow on every arc is between [-DEFAULT_EPSILON, edge_capacity]
        for (DefaultWeightedEdge e : flow.keySet()) {
            assertTrue(network.containsEdge(e));
            assertTrue(
                    flow.get(e) >= -EdmondsKarpMFImpl.DEFAULT_EPSILON);
            assertTrue(
                    flow.get(e)
                            <= (network.getEdgeWeight(e)+ EdmondsKarpMFImpl.DEFAULT_EPSILON));
        }

        //Verify flow preservation: amount of incoming flow must equal amount of outgoing flow (exception for the source/sink vertices)
        for (Integer v : network.vertexSet()) {
            double balance = 0.0;
            for (DefaultWeightedEdge e : network.outgoingEdgesOf(v)) {
                balance -= flow.get(e);
            }
            for (DefaultWeightedEdge e : network.incomingEdgesOf(v)) {
                balance += flow.get(e);
            }
            if (v.equals(source)) {
                assertEquals(
                        -flowValue,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
            } else if (v.equals(sink)) {
                assertEquals(
                        flowValue,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
            } else {
                assertEquals(
                        0.0,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
            }
        }
    }

    private void runTestUndirected(
            UndirectedGraph<Integer, DefaultWeightedEdge> graph,
            int source,
            int sink,
            int expectedResult)
    {
        MinimumSourceSinkCutAlgorithm<Integer, DefaultWeightedEdge> solver = createSolver(graph);

        verifyUndirected(graph, source, sink, expectedResult, solver);
    }

    static void verifyUndirected(UndirectedGraph<Integer, DefaultWeightedEdge> graph, int source, int sink, int expectedResult, MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> solver) {
        MaximumFlowAlgorithm.MaximumFlow<DefaultWeightedEdge> maxFlow=solver.buildMaximumFlow(source, sink);
        Double flowValue = maxFlow.getValue();
        Map<DefaultWeightedEdge, Double> flow = maxFlow.getFlow();

        assertEquals(expectedResult, flowValue.intValue());

        //Verify that every edge is contained in the flow map
        for (DefaultWeightedEdge e : graph.edgeSet())
            assertTrue(flow.containsKey(e));

        //Verify that the flow on every arc is between [-DEFAULT_EPSILON, edge_capacity]
        for (DefaultWeightedEdge e : flow.keySet()) {
            assertTrue(graph.containsEdge(e));
            assertTrue(
                    flow.get(e) >= -EdmondsKarpMFImpl.DEFAULT_EPSILON);
            assertTrue(
                    flow.get(e)
                            <= (graph.getEdgeWeight(e)+ EdmondsKarpMFImpl.DEFAULT_EPSILON));
        }

        //Verify flow preservation: amount of incoming flow must equal amount of outgoing flow (exception for the source/sink vertices)
        for (Integer u : graph.vertexSet()) {
            double balance = 0.0;
            for(DefaultWeightedEdge e : graph.edgesOf(u)){
                Integer v=solver.getFlowDirection(e);
                if(u==v) //incoming flow
                    balance+=flow.get(e);
                else //outgoing flow
                    balance-=flow.get(e);
            }

            if (u.equals(source)) {
                assertEquals(
                        -flowValue,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
            } else if (u.equals(sink)) {
                assertEquals(
                        flowValue,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
            } else {
                assertEquals(
                        0.0,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
            }
        }

    }



    public void testDirectedN0()
    {
        runTestDirected(
                getDirectedN0(),
                new int[]{1},
                new int[]{4},
                new double[]{5.0});
    }

    public void testDirectedN1()
    {
        runTestDirected(
                getDirectedN1(),
                new int[]{1},
                new int[]{4057218},
                new double[]{0.0});
    }

    public void testDirectedN2() {
        runTestDirected(
                getDirectedN2(),
                new int[]{3},
                new int[]{6},
                new double[]{2});
    }

    public void testDirectedN3() {
        runTestDirected(
                getDirectedN3(),
                new int[]{5},
                new int[]{6},
                new double[]{4.0});
    }

    public void testDirectedN4() {
        runTestDirected(
                getDirectedN4(),
                new int[]{1},
                new int[]{4},
                new double[]{2000000000.0});
    }

    public void testDirectedN6() {
        runTestDirected(
                getDirectedN6(),
                new int[]{1},
                new int[]{50},
                new double[]{20.0}
        );
    }

    public void testDirectedN7() {
        runTestDirected(
                getDirectedN7(),
                new int[]{1},
                new int[]{50},
                new double[]{31.0}
        );
    }

    public void testDirectedN8(){
        runTestDirected(
                getDirectedN8(),
                new int[]{0},
                new int[]{5},
                new double[]{23}
        );
    }

    public void testDirectedN9(){
        runTestDirected(
                getDirectedN9(),
                new int[]{0},
                new int[]{8},
                new double[]{22}
        );
    }

    public void testDirectedN10(){
        runTestDirected(
                getDirectedN10(),
                new int[]{1},
                new int[]{99},
                new double[]{173}
        );
    }

    public void testDirectedN11(){
        runTestDirected(
                getDirectedN11(),
                new int[]{1},
                new int[]{99},
                new double[]{450}
        );
    }

    public void testDirectedN12(){
        runTestDirected(
                getDirectedN12(),
                new int[]{1},
                new int[]{99},
                new double[]{203}
        );
    }


    /*************** TEST CASES FOR UNDIRECTED GRAPHS ***************/

//    public void testUndirectedN1(){
//        int[][] edges={{0, 1, 12}, {0, 2, 15}, {0, 3, 20}, {1, 5, 5}, {1, 6, 2}, {1, 2, 5}, {2, 6, 6}, {2, 4, 3}, {2, 3, 11}, {3, 4, 4}, {3, 7, 8}, {4, 6, 6}, {4, 7, 1}, {5, 6, 9}, {5, 8, 18}, {6, 7, 7}, {6, 8, 13}, {7, 8, 10}};
//        runTestUndirected(edges, 0, 8, 28);
//    }

    public void testUndirectedN2(){
        runTestUndirected(getUndirectedN2(), 1, 4, 93);
    }

    public void testUndirectedN3(){
        runTestUndirected(getUndirectedN3(), 1, 49, 104);
    }

    public void testUndirectedN4(){
        runTestUndirected(getUndirectedN4(), 1, 99, 634);
    }

    public void testUndirectedN5(){
        runTestUndirected(getUndirectedN5(), 1, 49, 112);
    }

    public void testUndirectedN6(){
        runTestUndirected(getUndirectedN6(), 1, 69, 194);
    }

    public void testUndirectedN7(){
        runTestUndirected(getUndirectedN7(), 1, 69, 33);
    }

    public void testUndirectedN8(){
        runTestUndirected(getUndirectedN8(), 1, 99, 501);
    }
}
