# 🛰️ Prospection de Terrain par Drones Synchronisés

Ce projet vise à développer un système de prospection de terrains inaccessibles en utilisant **deux drones Tello Edu** contrôlés de manière synchronisée.  
L'objectif principal est d'étudier des sites potentiels pour **l'implantation d'éoliennes**, en facilitant la cartographie et l’analyse des zones difficiles d’accès.

---

## ⚠️ Avertissement

> Ce projet est un **prototype de fin d’études**.  
> Certaines fonctionnalités sont **encore incomplètes**, notamment celles liées à l’**interaction directe des drones avec le monde réel** (évitement d’obstacles réel, communication sans fil en environnement perturbé, etc.).  
> Il est destiné à démontrer un **concept fonctionnel**, non une solution industrielle prête à déployer.

---

## 🧠 Contexte

Dans le cadre de notre **projet de fin d'études**, nous avons conçu une solution mêlant **robotique, vision par ordinateur et réalité virtuelle**.  
Deux drones autonomes sont utilisés pour survoler et analyser le terrain, tandis que les données captées sont exploitées dans un environnement 3D immersif (**CHAI3D**), afin de faciliter la prise de décision pour des projets d’aménagement du territoire.

---

## ⚙️ Technologies utilisées

- **Java** : Développement de l'application principale, du serveur de diffusion du contexte CHAI3D, et du client de réception.
- **C++** : Création de l’environnement 3D à l’aide du framework **CHAI3D**.
- **SDK Tello Edu** : Communication et contrôle des drones.
- **OpenCV** : Traitement d’image en temps réel et détection d'obstacles.
- **Android Studio** : Développement de l'application mobile pour le pilotage et la visualisation du contexte 3D.

---

## 🚀 Fonctionnalités principales

- 🔄 Contrôle **synchronisé** de deux drones Tello Edu.
- 📍 Planification de **missions automatisées** avec points de passage.
- 📷 **Capture d’images et de vidéos** pour l’analyse terrain.
- 🧠 Détection et **évitement d’obstacles** (implémentation partielle).
- 📱 Application Android avec **interface utilisateur intuitive** pour le paramétrage des missions.
- 🌐 Intégration d’un **environnement 3D interactif (CHAI3D)** pour la visualisation des données en réalité virtuelle.

---

## 📷 Aperçu visuel

- **Application Centrale JAVA**:
![image](https://github.com/user-attachments/assets/36f4d0fd-cb8b-4e86-ac73-3a9229af7546)

- **Environnement non VR CHAI3D**:
![CHAI3D](https://github.com/user-attachments/assets/9f88ce7b-ffdc-4f37-a9e5-286858382761)
![image](https://github.com/user-attachments/assets/2514ceff-8934-4051-931a-43beca338cc8)

- **Environnement VR CHAI3D**:
![image](https://github.com/user-attachments/assets/490facb0-7904-4129-811e-608ab71ab073)

---

## 🛠️ Installation & Lancement

> Prérequis : drones Tello Edu connectés au réseau, Java 11+.
> Lancement: Ouvrez votre IDE et lancez depuis celui-ci la classe principale Start.java.
