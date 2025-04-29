# 📱 GoEvent – Application mobile de découverte d'événements

**GoEvent** est une application mobile Android permettant aux utilisateurs de découvrir facilement des événements (concerts, festivals, expositions...) autour de leur localisation. Elle repose sur un backend développé en **FastAPI** et une base de données **PostgreSQL** pour la gestion des utilisateurs et des événements.Tous les évenement ont été recupérés grâce à l'Api data culture.

---

## 🌟 Fonctionnalités

- 🔐 Authentification sécurisée (inscription / connexion)
- 📍 Géolocalisation des événements
- 📅 Affichage des dates, adresses et distances
- 🌐 Multilingue : Français 🇫🇷 / Anglais 🇬🇧
- 📡 Connexion à une API REST (FastAPI)

---

## 🧑‍💻 Technologies utilisées

### ✅ Frontend (Android)

- Kotlin
- Retrofit2
- Jetpack Components (ViewModel, LiveData…)
- ViewBinding
- Système de ressources multilingues (`values-fr/`, `values-en/`)

### ✅ Backend (API REST)

- Python 3.10+
- FastAPI
- SQLAlchemy
- PostgreSQL
- Uvicorn (serveur ASGI)

---

## ⚙️ Installation

### 📲 Application Android

1. Cloner ce dépôt :
   ```bash
   git clone https://github.com/JosueAhobade/goevent.git
   ```

2. Ouvrir le projet avec **Android Studio**.

3. Vérifier que l'émulateur ou le téléphone est connecté, puis exécuter l’application.

---

### 🔧 Backend (FastAPI + PostgreSQL)

1. Aller dans le dossier `backend/` (si c’est un sous-dossier) :
   ```bash
   cd backend
   ```

2. Créer et activer un environnement virtuel :
   ```bash
   python -m venv env
   source env/bin/activate 
   ```

3. Installer les dépendances :
   ```bash
   pip install -r requirements.txt
   ```

4. Créer une base de données PostgreSQL appelée `eventdb`.

5. Configurer la chaîne de connexion dans `config.py` :
   ```python
   DATABASE_URL = "postgresql://postgres:motdepasse@localhost:5432/eventdb"
   ```

6. Créer la table suivante (exemple `user`) :

   ```sql
   CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       username VARCHAR(50),
       email VARCHAR(100),
       password VARCHAR(100)
   );
   ```

7. Lancer le serveur FastAPI :
   ```bash
   uvicorn main:app --reload
   ```

---

## 📡 Endpoints principaux de l’API

| Méthode | URL       | Description               |
|--------:|-----------|---------------------------|
| POST    | `/signup` | Création de compte        |
| POST    | `/login`  | Authentification utilisateur |

---

## 🌍 Support multilingue

L'application est disponible en :

- 🇫🇷 **Français** (`res/values-fr/`)
- 🇬🇧 **Anglais** (`res/values-en/`)

La langue affichée dépend des paramètres système de l'appareil Android.

---
