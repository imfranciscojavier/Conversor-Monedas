import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class CurrencyConverter {
    private static final String API_KEY = "afb717f481930fc8ced99959";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Solicitar datos al usuario
            System.out.print("Ingresa la cantidad a convertir: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consumir salto de línea

            System.out.print("Ingresa la moneda de origen (por ejemplo, MXN, EUR, USD): ");
            String baseCurrency = scanner.nextLine().toUpperCase();

            System.out.print("Ingresa la moneda de destino (por ejemplo, USD, EUR MXN): ");
            String targetCurrency = scanner.nextLine().toUpperCase();

            // Llamada a la API
            double rate = getExchangeRate(baseCurrency, targetCurrency);

            // Mostrar el resultado
            if (rate != -1) {
                double convertedAmount = amount * rate;
                System.out.printf("%.2f %s = %.2f %s%n", amount, baseCurrency, convertedAmount, targetCurrency);
            } else {
                System.out.println("No se pudo obtener la tasa de cambio. Verifica las monedas ingresadas.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static double getExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            URL url = new URL(API_URL + baseCurrency);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // OK
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject conversionRates = jsonResponse.getJSONObject("conversion_rates");

                return conversionRates.getDouble(targetCurrency);
            } else {
                System.out.println("Error al conectar con la API. Código: " + responseCode);
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la tasa de cambio: " + e.getMessage());
            return -1;
        }
    }
}
