package org.example.threadpackage;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.loop.monitors.IMonitorClose;
import org.chocosolver.solver.search.loop.monitors.IMonitorUpBranch;
import org.chocosolver.solver.variables.IntVar;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Classe implémentant un Solver de TSP en utilisant Choco-solver
 */
public class PPC {


    /**
     * Résout le problème du voyageur de commerce (TSP)
     * L'algorithme minimise la distance totale tout en respectant les connexions définies entre les villes.
     *
     * @param C                 Le nombre de villes.
     * @param D                 La matrice des distances entre les villes.
     * @param connectionsBlocks La liste des blocs de connexion représentant les routes possibles entre les villes.
     * @param sharedSolution    L'objet partagé contenant la meilleure solution trouvée.
     * @return Un modèle Choco-solver représentant le problème TSP.
     * @throws FileNotFoundException Si une erreur d'accès aux fichiers survient (non utilisé dans l'implémentation actuelle).
     */
    public static Model SolveTSP(int C, int[][] D, List<CityConnections> connectionsBlocks, SharedSolution sharedSolution) throws FileNotFoundException {


        Model model = new Model("TSP");
        // VARIABLES
        IntVar[] succ = model.intVarArray("succ", C, 0, C - 1);
        int max = 100000;
        IntVar[] dist = model.intVarArray("dist", C, 0, max);
        IntVar totDist = model.intVar("Total distance", 0, max * C);

        // CONSTRAINTS
        for (int i = 0; i < C; i++) {
            Tuples tuples = new Tuples(true);
            for (int j = 0; j < C; j++) {
                if (j != i) tuples.add(j, D[i][j]);
            }
            model.table(succ[i], dist[i], tuples).post();
        }
        model.subCircuit(succ, 0, model.intVar(C)).post();
        model.sum(dist, "=", totDist).post();
        model.arithm(totDist, "<", (int) Math.ceil(sharedSolution.getBestDistance())).post();
        model.setObjective(Model.MINIMIZE, totDist);

        Solver solver = model.getSolver();

        final int[] callCount = {0};
        final int[] SolutionFound = {0};


        solver.plugMonitor(new IMonitorUpBranch() {
            @Override
            public void afterUpBranch() {
                callCount[0]++;
                if (SolutionFound[0] >= 5) { // ne pas prendre les n (à définir) premières solutions
                    if (callCount[0] % 10000 == 0) {
                        int sol = totDist.getUB();
                        for (int i = 0; i < C; i++) {
                            for (int j = succ[i].getLB(); j <= succ[i].getUB(); j++) {
                                int connIndex = i * C + j;
                                int blockIndex = connIndex / 64;
                                int bitIndex = connIndex % 64;
                                if (succ[i].contains(j)) {
                                    connectionsBlocks.get(blockIndex).setConnection(63 - bitIndex, true);
                                } else {
                                    connectionsBlocks.get(blockIndex).setConnection(63 - bitIndex, false);
                                }
                            }
                        }
                        sharedSolution.setConnectionsBlocks(connectionsBlocks);


                        double agBestDistance = sharedSolution.getBestDistance();
                        solver.getEngine().flush();
                    }
                }
            }
        });

        solver.plugMonitor(new IMonitorClose() {
            @Override
            public void afterClose() {
                SolutionFound[0]++;
            }
        });


        return model;
    }

}
