# PRI
Version de Choco-Solver : 4.10.14


Version de Jenetics : 8.1.0

Pour modifier la taille du problème, il faut modifier dans le main le nom du ficher texte et la variable nombre de ville en dessous

Attention si la taille de l'instance est trop grande il faut réduire le nombre de model choco-solver


Guide des classes : 

Ag : Algorithme génétique Jenetics

PPC : Partie Programmation par contrainte Choco-Solver

SharedSolution : Partie communication

CityConnection : Classe d'encodage

DistanceMatrix : Permet de créer une matrice de distance à partir de coordonnées

ReadCoordinates : Reformalise les coordonnées pour la classe DistanceMatrix

Main : Classe qui permet de lancer l'algo. 
