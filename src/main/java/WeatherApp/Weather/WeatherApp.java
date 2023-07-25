package WeatherApp.Weather;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@SpringBootApplication
public class WeatherApp {

    private static final String API_KEY = "b6907d289e10d714a6e88b30761fae22";
    private static final String API_URL = "https://samples.openweathermap.org/data/2.5/forecast/hourly";

    public static void main(String[] args) {
        SpringApplication.run(WeatherApp.class, args);

        Scanner scanner = new Scanner(System.in);
        RestTemplate restTemplate = new RestTemplate();

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Get weather");
            System.out.println("2. Get Wind Speed");
            System.out.println("3. Get Pressure");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter the date and time in 'YYYY-MM-DD HH:mm:ss' format: ");
                    String dateTimeString1 = scanner.nextLine();
                    processWeatherData(dateTimeString1, restTemplate);
                    break;
                case 2:
                    System.out.print("Enter the date and time in 'YYYY-MM-DD HH:mm:ss' format: ");
                    String dateTimeString2 = scanner.nextLine();
                    processWindSpeedData(dateTimeString2, restTemplate);
                    break;
                case 3:
                    System.out.print("Enter the date and time in 'YYYY-MM-DD HH:mm:ss' format: ");
                    String dateTimeString3 = scanner.nextLine();
                    processPressureData(dateTimeString3, restTemplate);
                    break;
                case 0:
                    System.out.println("Exiting the program.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void processWeatherData(String dateTimeString, RestTemplate restTemplate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateTime = dateFormat.parse(dateTimeString);
            ResponseEntity<String> response = restTemplate.getForEntity(buildUrl(dateTime), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray weatherList = jsonResponse.getJSONArray("list");

                long nearestTimestampDiff = Long.MAX_VALUE;
                JSONObject nearestWeatherData = null;

                for (int i = 0; i < weatherList.length(); i++) {
                    JSONObject weatherData = weatherList.getJSONObject(i);
                    long forecastTimestamp = weatherData.getLong("dt") * 1000;
                    long timestampDiff = Math.abs(forecastTimestamp - dateTime.getTime());

                    if (timestampDiff < nearestTimestampDiff) {
                        nearestTimestampDiff = timestampDiff;
                        nearestWeatherData = weatherData;
                    }
                }

                if (nearestWeatherData != null) {
                    System.out.println("Weather data for " + dateTimeString + ":");
                    System.out.println(nearestWeatherData.toString());
                } else {
                    System.out.println("Weather data not available for the specified date and time.");
                }
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Invalid date and time format or error processing the API response.");
        }
    }

    private static void processWindSpeedData(String dateTimeString, RestTemplate restTemplate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateTime = dateFormat.parse(dateTimeString);
            ResponseEntity<String> response = restTemplate.getForEntity(buildUrl(dateTime), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray weatherList = jsonResponse.getJSONArray("list");

                boolean dataFound = false;
                for (int i = 0; i < weatherList.length(); i++) {
                    JSONObject weatherData = weatherList.getJSONObject(i);
                    long forecastDateTime = weatherData.getLong("dt");

                    if (forecastDateTime * 1000 == dateTime.getTime()) {
                        JSONObject wind = weatherData.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");

                        System.out.println("Wind speed data for " + dateTimeString + ":");
                        System.out.println("Wind Speed: " + windSpeed + " m/s");
                        dataFound = true;
                        break;
                    }
                }

                if (!dataFound) {
                    System.out.println("Wind speed data not available for the specified date and time.");
                }
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Invalid date and time format or error processing the API response.");
        }
    }

    private static void processPressureData(String dateTimeString, RestTemplate restTemplate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateTime = dateFormat.parse(dateTimeString);

            // Ensure the date is in the future (after the current date)
            if (dateTime.before(new Date())) {
                System.out.println("Invalid date and time. Please enter a date and time in the future.");
                return;
            }

            ResponseEntity<String> response = restTemplate.getForEntity(buildUrl(dateTime), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray weatherList = jsonResponse.getJSONArray("list");

                long nearestTimestampDiff = Long.MAX_VALUE;
                JSONObject nearestWeatherData = null;

                for (int i = 0; i < weatherList.length(); i++) {
                    JSONObject weatherData = weatherList.getJSONObject(i);
                    long forecastTimestamp = weatherData.getLong("dt") * 1000;
                    long timestampDiff = Math.abs(forecastTimestamp - dateTime.getTime());

                    if (timestampDiff < nearestTimestampDiff) {
                        nearestTimestampDiff = timestampDiff;
                        nearestWeatherData = weatherData;
                    }
                }

                if (nearestWeatherData != null) {
                    JSONObject main = nearestWeatherData.getJSONObject("main");
                    double pressure = main.getDouble("pressure");

                    System.out.println("Pressure data for " + dateTimeString + ":");
                    System.out.println("Pressure: " + pressure + " hPa");
                } else {
                    System.out.println("Pressure data not available for the specified date and time.");
                }
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("Invalid date and time format or error processing the API response.");
        }
    }

    private static String buildUrl(Date dateTime) {
        String location = "London,us";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = dateFormat.format(dateTime);
        return API_URL + "?q=" + location + "&appid=" + API_KEY + "&dt_txt=" + dateTimeString;
    }
}


