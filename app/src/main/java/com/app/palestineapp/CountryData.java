package com.app.palestineapp;

/**
 * Created by IntelliJ IDEA.
 * User: iali
 * Company: CargoSoft GmbH
 * Date: 06.11.2024
 * Time: 23:18
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryData {

    public   List<Country> fetchCountryData() {

        List<Country> countries = new ArrayList<>();
        String url = "https://iptv-org.github.io/api/countries.json"; // URL to your JSON

        try {
            // Create URL and HttpURLConnection
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JSONArray jsonArray = new JSONArray(content.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject countryJson = jsonArray.getJSONObject(i);

                // Populate the Country bean
                String name = countryJson.getString("name");
                String code = countryJson.getString("code");
                JSONArray languagesArray = countryJson.getJSONArray("languages");
                List<String> languages = new ArrayList<>();
                for (int j = 0; j < languagesArray.length(); j++) {
                    languages.add(languagesArray.getString(j));
                }
                String flag = countryJson.getString("flag");

                countries.add(new Country(name, code, languages, flag));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return countries;
    }
    public class Country {
        private String name;
        private String code;
        private List<String> languages;
        private String flag;

        // Constructor
        public Country(String name, String code, List<String> languages, String flag) {
            this.name = name;
            this.code = code;
            this.languages = languages;
            this.flag = flag;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public List<String> getLanguages() { return languages; }
        public void setLanguages(List<String> languages) { this.languages = languages; }

        public String getFlag() { return flag; }
        public void setFlag(String flag) { this.flag = flag; }

        @Override
        public String toString() {
            return "Country{" +
                    "name='" + name + '\'' +
                    ", code='" + code + '\'' +
                    ", languages=" + languages +
                    ", flag='" + flag + '\'' +
                    '}';
        }
    }

}
