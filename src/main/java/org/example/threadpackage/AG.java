/**
 * Classe implémentant un algorithme génétique pour résoudre le problème du voyageur de commerce
 * en utilisant la bibliothèque Jenetics.
 */
package org.example.threadpackage;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.ISeq;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Classe AG implémentant le problème du voyageur de commerce avec contraintes de connexions.
 */
public final class AG implements Problem<ISeq<Integer>, EnumGene<Integer>, Double> {

    private int[][] distanceMatrix;
    private final int[][] originalDistanceMatrix;
    private final List<CityConnections> connectionsBlocks;

    /**
     * Constructeur de la classe AG.
     *
     * @param distanceMatrix    Matrice des distances entre les villes.
     * @param connectionsBlocks Liste des connexions possibles entre les villes.
     */
    public AG(final int[][] distanceMatrix, final List<CityConnections> connectionsBlocks) {
        this.distanceMatrix = distanceMatrix;
        this.originalDistanceMatrix = copyMatrix(distanceMatrix);
        this.connectionsBlocks = connectionsBlocks;
    }

    /**
     * Copie une matrice d'entiers.
     *
     * @param matrix Matrice à copier.
     * @return Copie de la matrice.
     */
    private int[][] copyMatrix(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }

    /**
     * Initialise la matrice des distances en fonction des blocs de connexions.
     *
     * @param connectionsBlocks Liste des connexions entre les villes.
     */
    public void initializeWithConnectionsBlocks(List<CityConnections> connectionsBlocks) {
        int C = distanceMatrix.length;
        for (int i = 0; i < C; i++) {
            for (int j = 0; j < C; j++) {
                if (i != j) {
                    int connIndex = i * C + j;
                    int blockIndex = connIndex / 64;
                    int bitIndex = connIndex % 64;

                    if (!connectionsBlocks.get(blockIndex).isConnectionSet(63 - bitIndex)) {
                        distanceMatrix[i][j] = Integer.MAX_VALUE;
                    }
                }
            }
        }
    }

    /**
     * Fonction d'évaluation du fitness d'un chemin donné.
     *
     * @return Une fonction qui évalue la distance totale d'un chemin.
     */
    @Override
    public Function<ISeq<Integer>, Double> fitness() {
        return tour -> {
            double totalDistance = 0.0;
            for (int i = 0; i < tour.size() - 1; i++) {
                int from = tour.get(i);
                int to = tour.get(i + 1);
                if (distanceMatrix[from][to] == Integer.MAX_VALUE) {
                    return Double.MAX_VALUE;
                }
                totalDistance += distanceMatrix[from][to];
            }

            int last = tour.get(tour.size() - 1);
            int first = tour.get(0);
            if (distanceMatrix[last][first] == Integer.MAX_VALUE) {
                return Double.MAX_VALUE;
            }
            totalDistance += distanceMatrix[last][first];

            return totalDistance;
        };
    }

    /**
     * Définit le codec pour représenter les solutions sous forme de permutations.
     *
     * @return Codec représentant les permutations des villes.
     */
    @Override
    public Codec<ISeq<Integer>, EnumGene<Integer>> codec() {
        ISeq<Integer> cityIndices = IntStream.range(0, distanceMatrix.length)
                .boxed()
                .collect(ISeq.toISeq());
        return Codecs.ofPermutation(cityIndices);
    }

    /**
     * Exécute l'algorithme génétique pour trouver une solution optimale.
     *
     * @param generations    Nombre de générations pour l'évolution.
     * @param sharedSolution Référence vers l'objet stockant la meilleure solution trouvée.
     */
    public void runAlgorithm(int generations, SharedSolution sharedSolution) {

        final Engine<EnumGene<Integer>, Double> engine = Engine.builder(this)
                .optimize(Optimize.MINIMUM)
                .alterers(
                        new SwapMutator<>(0.15),
                        new PartiallyMatchedCrossover<>(0.15)
                )
                .build();

        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<EnumGene<Integer>, Double> best = engine.stream()
                .limit(generations) // Nombre maximal de générations
                .peek(result -> {
                    sharedSolution.setBestDistance(result.bestFitness());
                })
                .collect(EvolutionResult.toBestPhenotype());


        // Affichage des résultats
//        System.out.println(statistics);
//        System.out.println("Meilleur chemin (ordre des villes) : " + best.genotype());
//        System.out.println("Distance totale : " + best.fitness());
    }
}
