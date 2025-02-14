package org.example.threadpackage;

public class CityConnections {
    private long connections;

    // Initialisation avec toutes les connexions possibles (tous les bits à 1)
    public CityConnections() {
        this.connections = -0L; // -1 en binaire est composé de 64 bits à 1
    }

    // Méthode pour interdire une connexion entre deux villes
    public void disableConnection(int fromCity, int toCity) {
        int bitPosition = fromCity * 8 + toCity; // Position dans le long
        connections &= ~(1L << bitPosition); // Met le bit à 0
    }

    public void setConnection(int cityIndex, boolean state) {
        if (state) {
            connections |= (1L << cityIndex); // Met le bit à 1 pour indiquer une connexion
        } else {
            connections &= ~(1L << cityIndex); // Met le bit à 0 pour supprimer une connexion
        }
    }

    // Méthode pour vérifier si une connexion est possible
    public boolean isConnectionSet(int bitPosition) {
        return (connections & (1L << bitPosition)) != 0;
    }

    // Méthode pour afficher les connexions sous forme binaire
    public String printConnections() {
        //System.out.println(Long.toBinaryString(connections));
        return String.format("%64s", Long.toBinaryString(connections)).replace(' ', '0');
    }


    @Override
    public String toString() {
        // Formatage pour afficher les bits du long, en remplaçant les espaces par des zéros
        return String.format("%64s", Long.toBinaryString(connections)).replace(' ', '0');
    }
}

