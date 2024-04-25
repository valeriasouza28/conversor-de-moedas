package com.oracle.one.conversormoedas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.oracle.one.conversormoedas.Service.ApiKey;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.matches;

public class Main {

    public static void main(String[] args) {
        Scanner readInput =  new Scanner(System.in);
       // Substitua por sua api  key: String apikey = "sua api key"
        ApiKey getApiKey = new ApiKey();
        String apiKey = getApiKey.getApiKey();
        System.out.println("Digite a moeda que de origem");
        String baseCurrency = readInput.next().toUpperCase();
        System.out.println("Digite a moeda que de origem");
        String targetCurrency = readInput.next().toUpperCase();

        try {
            if (!isValidCurrency(baseCurrency) || !isValidCurrency(targetCurrency)) {
                throw new IllegalArgumentException("A sua moeda deve ter 3 letras.");
            }
            String urlStr = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + baseCurrency + "/" + targetCurrency;
            URL url = new URL(urlStr);
            // Abrir conexão HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Ler a resposta
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Converter a resposta JSON para um objeto JsonObject usando Gson
            JsonObject jsonResponse = new Gson().fromJson(response.toString(), JsonObject.class);

            // Verificar se a requisição foi bem sucedida
            if (jsonResponse.get("result").getAsString().equals("success")) {
                String baseCode = jsonResponse.get("base_code").getAsString();
                String targetCode = jsonResponse.get("target_code").getAsString();
                double conversionRate = jsonResponse.get("conversion_rate").getAsDouble();

                System.out.println("Taxa de conversão de " + baseCode + " para " + targetCode + ": " + conversionRate);
            } else {
                System.out.println("Falha ao obter taxa de conversão.");
            }

            // Fechar conexão
            connection.disconnect();

        } catch (IOException e) {
            System.out.println("Erro ao processar requisição HTTP: " + e.getMessage());
        }


    }

    public static boolean isValidCurrency(String currency) {
    // Verifica se a moeda tem 3 letras (maiúsculas ou minúsculas)
    String regex = "^[a-zA-Z]{3}$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(currency);
    return matcher.matches();
}
}

