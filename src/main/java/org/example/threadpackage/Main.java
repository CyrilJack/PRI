package org.example.threadpackage;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.ParallelPortfolio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {


        SharedSolution sharedSolution = new SharedSolution();
        ParallelPortfolio portfolio = new ParallelPortfolio();
        String filePath = "distance_matrix76.txt";
        int nbModels = 5;
        int cityCount = 76;

        int[][] distanceMatrix = readMatrixFromFile(filePath);
        portfolio.getModels().forEach(m -> m.getSolver().reset());

        int numBlocks = (cityCount * cityCount + 63) / 64;
        List<CityConnections> connectionsBlocks = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            connectionsBlocks.add(new CityConnections());
        }
        for (int s = 0; s < nbModels; s++) {
            portfolio.addModel(PPC.SolveTSP(cityCount, distanceMatrix, connectionsBlocks, sharedSolution));
        }
        portfolio.stealNogoodsOnRestarts();
        int nbSols = 0;

        Thread agThread = new Thread(() -> {

            AG agSolver = new AG(distanceMatrix, connectionsBlocks);
            while (!sharedSolution.isStopped()) {
                List<CityConnections> currentConnectionsBlocks = sharedSolution.getConnectionsBlocks();
                if (currentConnectionsBlocks != null) {

                    agSolver.initializeWithConnectionsBlocks(currentConnectionsBlocks);
                    agSolver.runAlgorithm(10000, sharedSolution);
                }


            }

        });


        agThread.start();


        while (portfolio.solve()) {
            Model finder = portfolio.getBestModel();
            System.out.println(finder.getObjective());
            sharedSolution.setBestDistance(finder.getObjective().asIntVar().getValue());
        }
        agThread.join();
        sharedSolution.stopExecution();

    }

    public static int[][] readMatrixFromFile(String filePath) {
        List<int[]> matrixList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignorer les lignes vides ou les commentaires éventuels
                if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                    continue;
                }

                // Nettoyer la ligne et séparer les valeurs en utilisant les espaces ou les virgules
                String[] values = line.trim().replaceAll("[{}]", "").split(",");
                int[] row = new int[values.length];

                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i].trim());
                }

                // Ajouter la ligne au tableau de la matrice
                matrixList.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Convertir la liste en tableau 2D
        return matrixList.toArray(new int[matrixList.size()][]);
    }
}
