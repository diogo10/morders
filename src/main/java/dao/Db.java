package dao;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

abstract class Db<T> {

    private String URL = "https://diogoprojects-617e2.firebaseio.com/";
    private String token;
    Gson gson;

    Db(){

        gson = new Gson();

        ClassLoader classLoader = getClass().getClassLoader();
        File file1 = new File(classLoader.getResource("diogoprojects-617e2-firebase-adminsdk-7alfu-bdc0cbf054.json").getFile());
        try {

            GoogleCredential googleCred;
            googleCred = GoogleCredential.fromStream(new FileInputStream(file1.getAbsolutePath()));
            GoogleCredential scoped = googleCred
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.database",
                            "https://www.googleapis.com/auth/userinfo.email"));
            scoped.refreshToken();
            setToken(scoped.getAccessToken());

        } catch (IOException ex) {
            System.out.println("we didn't find the config firebase file / DAO IO: " + ex.getMessage());
        }
    }

    private String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }

    HttpResponse<JsonNode> requestPost(String body, String node){

        try {
            return Unirest.post(URL + node + ".json?access_token=" + getToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
        } catch (UnirestException e) {
            return null;
        }

    }

    HttpResponse<JsonNode> requestGet(String node){

        try {
            return Unirest.get(URL + node + ".json?access_token=" + getToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            return null;
        }

    }

    HttpResponse<JsonNode> requestUpdate(String body,String node){

        try {
            return Unirest.patch(URL + node + ".json?access_token=" + getToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
        } catch (UnirestException e) {
            return null;
        }

    }


    //COMMON

    protected abstract JSONArray getAll(String reference);
    protected abstract Object get(String reference,String key);
    protected abstract boolean update(String reference,String key,T obj);
    protected abstract boolean update(String reference,String value);
    protected abstract Object add(String reference,T obj);

    //UTILS

    String generateUID(){
        return UUID.randomUUID().toString().replace("-","");
    }



}