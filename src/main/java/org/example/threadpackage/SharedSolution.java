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

    /**
     * Définit la liste des blocs de connexion et notifie les threads en attente.
     *
     * @param blocks Liste des blocs de connexion à définir.
     */
    public synchronized void setConnectionsBlocks(List<CityConnections> blocks) {
        this.connectionsBlocks = blocks;
        notifyAll();
    }

    /**
     * Retourne la liste des blocs de connexion. Si elle n'est pas encore définie,
     * la méthode attend qu'une autre thread la définisse.
     *
     * @return La liste des blocs de connexion.
     */
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

    /**
     * Met à jour la meilleure distance trouvée si elle est inférieure à la précédente.
     * Marque également la distance comme mise à jour et indique qu'une meilleure solution
     * a été trouvée.
     *
     * @param bestDistance Nouvelle meilleure distance trouvée.
     */
    public synchronized void setBestDistance(double bestDistance) {
        if (bestDistance < this.bestDistance) {
            this.bestDistance = bestDistance;
            this.distanceUpdated = true;
            this.agImprovedSolution = true;
        }
    }

    /**
     * Retourne la meilleure distance actuellement enregistrée et réinitialise l'indicateur
     * de mise à jour de la distance.
     *
     * @return La meilleure distance trouvée.
     */
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
