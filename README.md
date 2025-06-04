# 🛰️ Prospection de Terrain par Drones Synchronisés

Ce projet vise à développer un système de prospection de terrains inaccessibles en utilisant **deux drones Tello Edu** contrôlés de manière synchronisée.  
L'objectif principal est d'étudier des sites potentiels pour **l'implantation d'éoliennes**, en facilitant la cartographie et l’analyse des zones difficiles d’accès.

---

## ⚠️ Avertissement

> Ce projet est un **prototype de fin d’études**.  
> Certaines fonctionnalités sont **encore incomplètes**, notamment celles liées à l’**interaction directe des drones avec le monde réel** (évitement d’obstacles réels, communication sans fil en environnement perturbé, etc.).  
> Il s'agit d'une **preuve de concept**, non d’un produit final destiné à une utilisation en production.

---

## 🧠 Contexte

Dans le cadre de notre **projet de fin d'études** pour un **BTS Cybersécurité, Informatique et Électronique**, nous avons conçu une solution mêlant **robotique, vision par ordinateur et réalité virtuelle**.  
Deux drones autonomes sont utilisés pour survoler et analyser le terrain, tandis que les données captées sont exploitées dans un environnement 3D immersif (**CHAI3D**), facilitant ainsi la prise de décision pour des projets d’aménagement.

---

## ⚙️ Technologies utilisées

- **Java** : Développement de l'application principale, serveur de diffusion du contexte CHAI3D, client de réception.
- **C++** : Création de l’environnement 3D avec le framework **CHAI3D**.
- **SDK Tello Edu** : Contrôle et communication avec les drones.
- **OpenCV** : Traitement d’image en temps réel, détection d'obstacles (partiellement implémenté).
- **Android Studio** : Développement de l'application mobile pour le pilotage et la visualisation.

---

## 🚀 Fonctionnalités principales

- 🔄 Contrôle **synchronisé** de deux drones Tello Edu.
- 📍 Planification de **missions automatisées** avec points de passage.
- 📷 **Capture d’images et de vidéos** pour l’analyse de terrain.
- 🧠 Détection et **évitement d’obstacles** (fonctionnalité en cours).
- 📱 Interface Android intuitive pour le paramétrage et la visualisation.
- 🌐 Visualisation en **3D (CHAI3D)** en version standard et en réalité virtuelle.

---

## 📷 Aperçu visuel

### 🎛️ Application centrale Java
![image](https://github.com/user-attachments/assets/36f4d0fd-cb8b-4e86-ac73-3a9229af7546)

### 🌍 Environnement CHAI3D (non-VR)
![CHAI3D](https://github.com/user-attachments/assets/9f88ce7b-ffdc-4f37-a9e5-286858382761)  
![image](https://github.com/user-attachments/assets/2514ceff-8934-4051-931a-43beca338cc8)

### 🕶️ Environnement CHAI3D (réalité virtuelle)
![image](https://github.com/user-attachments/assets/490facb0-7904-4129-811e-608ab71ab073)

---

## 🛠️ Installation & Lancement

> **Prérequis** :  
> - Drones Tello Edu connectés au réseau local  
> - Java 11+  
> - Android Studio (si vous utilisez l’application mobile)  

### ▶️ Lancement rapide

> Ouvrez votre IDE Java (Eclipse, IntelliJ,...) puis exécuter la classe: src/main/java/Start.java

    
