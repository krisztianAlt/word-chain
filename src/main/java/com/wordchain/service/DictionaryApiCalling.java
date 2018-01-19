package com.wordchain.service;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Service;
import javax.net.ssl.HttpsURLConnection;
import javax.validation.constraints.Null;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;


@Service
public class DictionaryApiCalling {

    final String OxfordDictionaryApi = "https://od-api.oxforddictionaries.com:443/api/v1/entries/";
    final String language = "en";
    final String app_id = "e527f646";
    final String app_key = "22fe22705e0ebbe814343a76b88c86d3";

    public List<String> getMeanings(String word) {

        try {
            URL url = new URL(OxfordDictionaryApi + language + "/" + word);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            String fullJsonString = stringBuilder.toString();
            getMeaningsFromJson(fullJsonString);
            return getMeaningsFromJson(fullJsonString);

        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
            return new ArrayList<>();
        } catch (NullPointerException e){
            System.out.println(e.getMessage());
            return new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    private List<String> getMeaningsFromJson(String fullJsonString) {
        List<String> meanings = new ArrayList<>();

        JacksonJsonParser jacksonJsonParser = new JacksonJsonParser();
        ArrayList<LinkedHashMap<String, Object>> results = (ArrayList<LinkedHashMap<String, Object>>) jacksonJsonParser.parseMap(fullJsonString).get("results");
        for (LinkedHashMap<String, Object> result : results){
            ArrayList<LinkedHashMap<String, Object>> lexicalEntries = (ArrayList<LinkedHashMap<String, Object>>) result.get("lexicalEntries");
            for (LinkedHashMap<String, Object> lexicalEntry : lexicalEntries){
                ArrayList<LinkedHashMap<String, Object>> entries = (ArrayList<LinkedHashMap<String, Object>>) lexicalEntry.get("entries");
                for (LinkedHashMap<String, Object> entry : entries){
                    ArrayList<LinkedHashMap<String, Object>> senses = (ArrayList<LinkedHashMap<String, Object>>) entry.get("senses");
                    for (LinkedHashMap<String, Object> sense : senses){
                        ArrayList<String> definitions = (ArrayList<String>) sense.get("definitions");
                        for (String definition: definitions){
                            meanings.add(definition);
                        }

                    }
                }
            }
        }

        return meanings;
    }
}
