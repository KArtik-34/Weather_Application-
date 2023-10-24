import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

class WeatherData {
    Main main;
    Weather[] weather;

    static class Main {
        double temp;
    }

    static class Weather {
        String description;
    }

    public double getTemperature() {
        return main.temp;
    }

    public String getDescription() {
        if (weather != null && weather.length > 0) {
            return weather[0].description;
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return "Temperature: " + getTemperature() + "°C, Description: " + getDescription();
    }
}

public class Main {
    private static final HashMap<String, WeatherData> savedLocations = new HashMap<>();
    private static final String API_KEY = "747388e7019500ef494c451b921e4cd4";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Weather Forecasting Application");
            System.out.println("1. Get Weather");
            System.out.println("2. Save Location");
            System.out.println("3. View Saved Locations");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter city name: ");
                    String city = scanner.nextLine();
                    WeatherData weatherData = fetchWeatherData(city);
                    if (weatherData != null) {
                        System.out.println("Weather: " + weatherData);
                    } else {
                        System.out.println("Weather data not available for the specified city.");
                    }
                }
                case "2" -> {
                    System.out.print("Enter location name: ");
                    String locationName = scanner.nextLine();
                    WeatherData locationWeatherData = fetchWeatherData(locationName);
                    if (locationWeatherData != null) {
                        savedLocations.put(locationName, locationWeatherData);
                        System.out.println("Location saved successfully!");
                    } else {
                        System.out.println("Weather data not available for the specified city.");
                    }
                }
                case "3" -> {
                    System.out.println("Saved Locations:");
                    for (String loc : savedLocations.keySet()) {
                        System.out.println(loc);
                    }
                    System.out.print("Enter location name to view weather data: ");
                    String selectedLocation = scanner.nextLine();
                    WeatherData selectedWeatherData = savedLocations.get(selectedLocation);
                    if (selectedWeatherData != null) {
                        System.out.println("Weather for " + selectedLocation + ": " + selectedWeatherData);
                    } else {
                        System.out.println("Location not found!");
                    }
                }
                case "4" -> {
                    System.out.println("Exiting Weather Application. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static WeatherData fetchWeatherData(String location) {
        WeatherData weatherData = null;

        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + API_KEY + "&units=metric");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Gson gson = new Gson();
                weatherData = gson.fromJson(response.toString(), WeatherData.class);
            } else {
                System.out.println("Error: Unable to fetch weather data. HTTP Response Code: " + responseCode);
            }

            connection.disconnect();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return weatherData;
    }
}
