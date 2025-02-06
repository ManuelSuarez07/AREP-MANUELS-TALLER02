package com.eci.webservert1;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Manuel S
 */
public class CurrencyConverter {

    private static final Map<String, Double> exchangeRates = new HashMap<>();

    static {
        exchangeRates.put("USD", 1.0);     
        exchangeRates.put("EUR", 0.85);    
        exchangeRates.put("GBP", 0.75);    
        exchangeRates.put("JPY", 130.0);   
        exchangeRates.put("AUD", 1.5);     
        exchangeRates.put("CAD", 1.35);    
        exchangeRates.put("CHF", 0.92);    
        exchangeRates.put("CNY", 6.5);     
        exchangeRates.put("INR", 83.0);    
        exchangeRates.put("BRL", 5.3);     
        exchangeRates.put("MXN", 18.5);    
        exchangeRates.put("ARS", 350.0);   
        exchangeRates.put("KRW", 1200.0);  
        exchangeRates.put("ZAR", 18.0);   
        exchangeRates.put("COP", 4000.0);  
        exchangeRates.put("SEK", 10.5);    
        exchangeRates.put("TRY", 19.0);    
        exchangeRates.put("RUB", 85.0);    
        exchangeRates.put("SGD", 1.35);    
        exchangeRates.put("NZD", 1.6);     
        exchangeRates.put("MYR", 4.5);     
        exchangeRates.put("IDR", 15000.0); 
        exchangeRates.put("SAR", 3.75);    
    }

    public static double convert(double amount, String fromCurrency, String toCurrency) {
        if (exchangeRates.containsKey(fromCurrency) && exchangeRates.containsKey(toCurrency)) {
            double fromRate = exchangeRates.get(fromCurrency);
            double toRate = exchangeRates.get(toCurrency);
            return amount * (toRate / fromRate);
        }
        throw new IllegalArgumentException("Moneda no soportada");
    }
}
