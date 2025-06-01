package com.example.plantnetapp.back.api;

import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExternalBDDApi {
    private static ExternalBDDApi INSTANCE = null;
    private final String URL_NAME = "https://simpleapi-1u9p.onrender.com/api/";
    private ExternalBDDApi() {
        try{
            ReturnType apiStatus = checkStatus();
            if (apiStatus.status != 200){
                INSTANCE = null;
            }
        }catch (IOException e){
            INSTANCE = null;
        }
    }

    public static ExternalBDDApi createInstance() {
        if (INSTANCE == null){
            INSTANCE = new ExternalBDDApi();
        }
        return INSTANCE;
    }
    public void linkImageToRequest(HttpURLConnection connection, String boundary, JsonObject body, File file, String fileFieldName) throws IOException {
        try (OutputStream outputStream = connection.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(outputStream);
             FileInputStream fileInputStream = new FileInputStream(file)) {

            // Écriture des champs texte
            String[] fields = {
                    "plantCollectionID",
                    "name",
                    "cultural_condition",
                    "azote_fixation",
                    "upgrade_ground",
                    "water_fixation",
                    "azote_fixation_r",
                    "upgrade_ground_r",
                    "water_fixation_r"
            };
            for (String field : fields) {
                String part = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"" + field + "\"\r\n\r\n" +
                        body.get(field).getAsString() + "\r\n";
                bos.write(part.getBytes(StandardCharsets.UTF_8));
            }

            // Écriture de l'en-tête du fichier
            String fileHeader = "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\r\n\r\n";
            bos.write(fileHeader.getBytes(StandardCharsets.UTF_8));

            // Écriture du contenu du fichier
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.write("\r\n".getBytes(StandardCharsets.UTF_8));

            // Écriture de la fin
            String end = "--" + boundary + "--\r\n";
            bos.write(end.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        }
    }
    public ReturnType checkStatus() throws IOException{
        return sendHeadRequest("health");
    }

    public ReturnType sendHeadRequest(String endpoint) throws  IOException{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            return new ReturnType(status, contentType, null);
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
    public ReturnType sendPostRequest(String endpoint, JsonObject body, @Nullable File file) throws  IOException{
        HttpURLConnection connection = null;
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "*/*");
            if (file == null){
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
            }else{
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            }

            connection.setDoOutput(true);

            if (file == null){
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            else{
                linkImageToRequest(connection,boundary, body,file, "image");
            }


            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            JsonObject responseJson = null;
            InputStream responseStream = connection.getResponseCode() >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();
            //Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (!response.toString().isEmpty()) {
                    responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                } else {
                    responseJson = new JsonObject();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            return new ReturnType(status, contentType, responseJson != null ? responseJson : new JsonObject());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public ReturnType sendDeleteRequest(String endpoint, JsonObject body) throws  IOException{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            JsonObject responseJson = null;
            InputStream responseStream = connection.getResponseCode() >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();
            //Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (!response.toString().isEmpty()) {
                    responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                } else {
                    responseJson = new JsonObject();
                }
            }
            return new ReturnType(status, contentType, responseJson != null ? responseJson : new JsonObject());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
    public ReturnType sendGetRequest(String endpoint) throws  IOException{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            JsonObject responseJson = null;
            //Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (!response.toString().isEmpty()) {
                    responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                } else {
                    responseJson = new JsonObject();
                }
            }
            return new ReturnType(status, contentType, responseJson != null ? responseJson : new JsonObject());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    public ReturnType login(String username, String pswrd) throws IOException {

        try {
            JsonObject body = new JsonObject();
            body.addProperty("username", username);
            body.addProperty("pswrd", pswrd);
            ReturnType response = sendPostRequest("user/get",body, null);
            if (response.status != 200){
                throw new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public ReturnType addUser(User user) throws IOException {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("username", user.login);
            body.addProperty("pswrd", user.mdp);
            body.addProperty("email",user.mail);
            body.addProperty("firstname",user.firstName);
            body.addProperty("lastname",user.lastName);
            body.addProperty("phone",user.phone);
            ReturnType response = sendPostRequest("user/add",body, null);
            if (response.status != 201){
                throw new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public ReturnType deleteUser(String id) throws IOException{
        try {
                JsonObject body = new JsonObject();
            body.addProperty("id", id);
            ReturnType response = sendDeleteRequest("user/delete",body);
            if (response.status != 200){
                throw new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public ReturnType addPlantCollection(String userID, String collectionName) throws IOException {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("userID", userID);
            body.addProperty("name", collectionName);
            ReturnType response = sendPostRequest("user/addPlantCollection",body, null);
            if (response.status != 201){
                throw new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public ReturnType getPlantCollection(String userID, String collectionName) throws IOException {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("userID", userID);
            body.addProperty("name", collectionName);
            ReturnType response = sendPostRequest("user/getPlantCollection",body, null);
            if (response.status != 200){
                throw new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public ReturnType getAllPlantCollections(String userID) throws IOException {
        try{
            JsonObject body = new JsonObject();
            body.addProperty("userID", userID);
            ReturnType response = sendPostRequest("user/getAllPlantCollections",body, null);
            if (response.status != 200){
                throw new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public ReturnType deletePlantCollection(String userID, String collectionName) throws IOException {
        try{
            JsonObject body = new JsonObject();
            body.addProperty("userID", userID);
            body.addProperty("name", collectionName);
            ReturnType response = sendDeleteRequest("user/deletePlantCollection",body);
            if (response.status != 200){
                throw new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    public ReturnType addPlant(String collectionID, Plant plant,  File imageFile) throws IOException{
        try {
            JsonObject body = new JsonObject();
            body.addProperty("plantCollectionID", collectionID);
            body.addProperty("name", plant.name);
            body.addProperty("cultural_condition", plant.culturalCondition);
            body.addProperty("azote_fixation", plant.azoteFixing);
            body.addProperty("upgrade_ground", plant.upgradeGrnd);
            body.addProperty("water_fixation", plant.waterFixing);
            body.addProperty("azote_fixation_r", plant.azoteReliability);
            body.addProperty("upgrade_ground_r", plant.upgradeReliability);
            body.addProperty("water_fixation_r", plant.waterReliability);

            System.out.println(imageFile.isFile());
            if (!imageFile.exists() || !imageFile.isFile() || imageFile.length() == 0) {
                throw new IOException("Image file is invalid or not found: " + imageFile.getAbsolutePath());
            }

            ReturnType response = sendPostRequest("user/addPlant",body, imageFile);
            if (response.status != 201){
                if (Objects.equals(response.values.get("message").getAsString(), "Plant already exist")){
                    return response;
                }
                throw  new IOException(response.values.toString());
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            return null;
        }

    }
    private void addNumberWithoutDecimal(JsonObject obj, String key, float value) {
        if (value == (int) value) {
            int intValue = (int) value;
            obj.addProperty(key, intValue); // ajoute un entier si c’est "0.0"
        } else {
            obj.addProperty(key, value); // sinon float classique
        }
    }
    public ReturnType addPlantWithoutCollection(String userID, Plant plant,  File imageFile) throws IOException{
        JsonObject body = new JsonObject();
        String collectionID;
        ReturnType colResponse;
        try {
            colResponse = getPlantCollection(userID,"history");
        }catch (Exception e){
            addPlantCollection(userID, "history");
            colResponse = getPlantCollection(userID,"history");
        }

        collectionID = colResponse.values.getAsJsonObject("PlantCollection").get("id").getAsString();

        body.addProperty("plantCollectionID", collectionID);
        body.addProperty("name", plant.name);
        body.addProperty("cultural_condition", plant.culturalCondition);
        body.addProperty("azote_fixation", plant.azoteFixing);
        body.addProperty("upgrade_ground", plant.upgradeGrnd);
        body.addProperty("water_fixation", plant.waterFixing);
        body.addProperty("azote_fixation_r", plant.azoteReliability);
        body.addProperty("upgrade_ground_r", plant.upgradeReliability);
        body.addProperty("water_fixation_r", plant.waterReliability);


        if (!imageFile.exists() || !imageFile.isFile() || imageFile.length() == 0) {
            throw new IOException("Image file is invalid or not found: " + imageFile.getAbsolutePath());
        }

        ReturnType response = sendPostRequest("user/addPlant",body, imageFile);
        if (response.status != 201){
            if (Objects.equals(response.values.get("message").getAsString(), "Plant already exist")){
                return response;
            }
            throw  new IOException(response.values.toString());
        }
        return response;
    }

    public ReturnType getPlant(String collectionID, String plantName) throws IOException{
        JsonObject body = new JsonObject();
        body.addProperty("collectionid", collectionID);
        body.addProperty("name", plantName);
        ReturnType response = sendPostRequest("user/getPlant",body,null);
        if (response.status != 200){
            throw  new IOException(response.values.toString());
        }
        return response;
    }
    public ReturnType getAllPlants(String collectionID) throws IOException{
        JsonObject body = new JsonObject();
        body.addProperty("collectionid", collectionID);
        ReturnType response = sendPostRequest("user/getAllPlants",body,null);
        if (response.status != 200){
            throw  new IOException(response.values.toString());
        }
        System.out.println(response);
        return response;
    }
    public ReturnType deletePlant(String collectionID, String plantName) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("collectionid", collectionID);
        body.addProperty("name", plantName);
        ReturnType response = sendDeleteRequest("user/deletePlant",body);
        if (response.status != 200){
            throw  new IOException(response.values.toString());
        }
        System.out.println(response);
        return response;
    }
}
