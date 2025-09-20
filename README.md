# London Air Pollution Visualisation App

**Authors:** Anton Davidouski, Yaal Luka Edrey Gatignol, Rom Steinberg, Nicolás Alcalá Olea

---

## 🌍 Overview

This application provides a comprehensive and interactive platform for exploring, analyzing, and predicting air pollution levels in London and other cities. Structured within a user-friendly `TabPane`, the app offers multiple views to investigate historical, real-time, and future pollution data. The welcome screen greets the user and leads them to the main application window.

---

## ✨ Features

The application is divided into several functional tabs:

### 🗺️ London Map
**Dynamic Map Colouring:** Visualizes pollution data across a map of London using a smooth green-to-red gradient. This is achieved by generating a coloured overlay on a base map image and applying a box blur for a seamless effect.
**Interactive Data Points:** Users can hover over or click on the map to find the nearest data monitoring station and view its specific pollution levels. The nearest point is found using a Euclidean distance algorithm.
**Future Prediction:** Integrates a linear regression model to forecast pollution levels for a user-selected future year based on historical data from 2018-2023.

### 📊 Pollution Statistics
**Historical Data Analysis:** View historical pollution data for PM2.5, PM10, and NO2 from 2018 to 2023.
**Multiple Chart Formats:** Data can be displayed as line charts for time-based trends, bar charts for year-to-year comparisons, and pie charts to show the proportional contribution of each pollutant.

### 🏙️ UK Cities
**Nationwide Data:** Explore pollution data for other major UK cities, including Manchester, Bristol, Leeds, and Birmingham.
**Dynamic Loading:** City data is loaded dynamically based on user selection and predefined geographical boundaries.

### 🚇 Tube Journey Calculator
**Pollutant Exposure Calculation:** Calculate your estimated exposure to PM2.5 on a given Tube journey within Zone 1.
**Comparative Analysis:** The tool compares the calculated pollution exposure on the Tube with the average street-level pollution for the same route.

### 📡 Real-Time Data
**Global City Search:** Fetch live air pollution data for any city on the planet using the **OpenWeatherMap API**.
**Smart Search:** An autocomplete search box suggests locations as the user types by querying a geocoding API.
**Comparative Bar Chart:** Add multiple cities to a comparison chart. Pollutant values are scaled against a maximum healthy limit to provide a clear, standardized view of pollution levels.

---

## 🛠️ Technical Details

**Backend:** Data from CSV files is loaded, parsed, and stored efficiently using a data aggregation system. [cite_start]API calls are handled in a separate thread to keep the UI responsive.
**Frontend:** The user interface is built with JavaFX and styled using external CSS for a clean and modern look.
**Testing:** Key components, such as the `City` class and the tube journey algorithm, were rigorously tested using JUnit to ensure reliability and correctness.

---

## 🚀 Getting Started

To run this project:

1.  Clone the repository.
2.  Open the project in your preferred Java IDE.
3.  Ensure you have the required JavaFX libraries installed.
4.  Run the main application file.
