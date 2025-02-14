package org.example.threadpackage;

import java.util.List;

/**
 * Classe singleton permettant de partager l'état de la meilleure solution et des blocs de connexion
 * entre différents threads exécutant un algorithme génétique.
 */
public class SharedSolution {
    private static SharedSolution instance;

    private List<CityConnections> connectionsBlocks;
    private double bestDistance = Double.MAX_VALUE;
    private boolean distanceUpdated = false;
    private boolean agImprovedSolution = false;
    private volatile boolean stop = false;


    SharedSolution() {
    }


    public synchronized void setConnectionsBlocks(List<CityConnections> blocks) {
        this.connectionsBlocks = blocks;
        notifyAll();
    }

    public synchronized List<CityConnections> getConnectionsBlocks() {
        while (connectionsBlocks == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return connectionsBlocks;
    }

    public synchronized void setBestDistance(double bestDistance) {
        if (bestDistance < this.bestDistance) {
            this.bestDistance = bestDistance;
            this.distanceUpdated = true;
            this.agImprovedSolution = true;
        }
    }

    public synchronized double getBestDistance() {
        this.distanceUpdated = false;
        return bestDistance;
    }


    public boolean isStopped() {
        return stop;
    }

    public void stopExecution() {
        this.stop = true;
    }
}
