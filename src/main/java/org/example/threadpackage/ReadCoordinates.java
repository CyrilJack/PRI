package org.example.threadpackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadCoordinates {
    public static void main(String[] args) {
        String inputFilePath = "coordonnes.txt";
        String outputFilePath = "output.txt";

        List<String> coordinates = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    coordinates.add(String.format("{%d, %d}", x, y));
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write(String.join(", ", coordinates));
            System.out.println("Coordonnées écrites dans " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }
}