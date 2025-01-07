package de.drnutella.castigo.utils;

import de.drnutella.castigo.Castigo;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

public class JSONFileBuilder {

    String defaultPath;
    String defaultFileName;
    private Boolean defaultUsed = false;
    private JSONObject dataLoadObject;

    public void createJsonFile(String path, String filename) {
        JSONObject jsonObject = new JSONObject();

        File directory = new File(path);
        directory.mkdirs();

        path = path + "/";

        File file = new File(path + filename + ".json");

        if (!file.exists()) {

            try {
                FileWriter fileWriter = new FileWriter(path + filename + ".json");
                fileWriter.write(jsonObject.toJSONString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("§c IOException throws on " + this.getClass().getName() + " (Method Name: createJsonFile)");

            }
        }

    }
    public void createJsonFileFromTemplate(String newPath, String newFilename, String ressourcePath, String ressourceName){
        InputStream inputStream = Castigo.getInstance().getResourceAsStream(ressourcePath + ressourceName + ".json");

        File file = new File(newPath, newFilename);

        try {
            FileUtils.copyInputStreamToFile(inputStream, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createJsonFileFromTemplate(String newPath, String newFilename, String ressourceName){
        InputStream inputStream = Castigo.getInstance().getResourceAsStream(ressourceName + ".json");
        File file = new File(newPath, newFilename + ".json");

        try {
            if(!file.exists()){
                FileUtils.copyInputStreamToFile(inputStream, file);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    public void updateJsonFile(JSONObject jsonObject, String path, String filename) {
        if(defaultUsed){
            path = defaultPath;
            filename = defaultFileName;
        }

        path = path + "/";

        try {
            FileWriter fileWriter = new FileWriter(path + filename + ".json");
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("§c IOException throws on ConfigHandler (Method Name: updateJsonConfig)");

        }
    }

    public void setDefaultValues(String path, String fileName) {
        defaultPath = path;
        defaultFileName = fileName;
        defaultUsed = true;
    }
    public void disableDefaultValues() {
        defaultPath = "";
        defaultFileName = "";
        defaultUsed = false;
    }

    //Getter
    public JSONObject getJsonObjectFromFile(String path, String filename) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;


        try {
            Object fileObject = parser.parse(new FileReader(checkPath(path) + checkFile(filename)+ ".json"));
            jsonObject = (JSONObject) fileObject;
            return jsonObject;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("§cAn Exception throws Message:" + e.getMessage());

            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("§cAn Exception throws! Message:" + e.getMessage());
            return null;
        }
    }
    public JSONObject getJsonObjectFromFile() {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;


        try {
            Object fileObject = parser.parse(new FileReader(defaultPath + "/" + defaultFileName + ".json"));
            jsonObject = (JSONObject) fileObject;
            return jsonObject;

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStringFromFile(String path, String filename, String key) {
        dataLoadObject = getJsonObjectFromFile(checkPath(path), checkFile(filename));
        return (String) dataLoadObject.get(key);
    }
    public String getStringFromFile(String key) {
        dataLoadObject = getJsonObjectFromFile(defaultPath, defaultFileName);
        return (String) dataLoadObject.get(key);
    }

    public int getIntFromFIle(String path, String filename, String key) {
        dataLoadObject = getJsonObjectFromFile(checkPath(path), checkFile(filename));
        return (int) dataLoadObject.get(key);
    }
    public int getIntFromFile(String key) {
        dataLoadObject = getJsonObjectFromFile(defaultPath, defaultFileName);
        return (int) dataLoadObject.get(key);
    }

    public Long getLongFromFIle(String path, String filename, String key) {
        dataLoadObject = getJsonObjectFromFile(checkPath(path), checkFile(filename));
        return (Long) dataLoadObject.get(key);
    }
    public Long getLongFromFile(String key) {
        dataLoadObject = getJsonObjectFromFile(defaultPath, defaultFileName);
        return (Long) dataLoadObject.get(key);
    }

    public boolean getBooleanFromFile(String path, String filename, String key) {
        dataLoadObject = getJsonObjectFromFile(checkPath(path), checkFile(filename));
        return (boolean) dataLoadObject.get(key);
    }
    public boolean getBooleanFromFile(String key) {
        dataLoadObject = getJsonObjectFromFile(defaultPath, defaultFileName);
        return (boolean) dataLoadObject.get(key);
    }

    public double getDoubleFromFile(String path, String filename, String key) {
        dataLoadObject = getJsonObjectFromFile(checkPath(path), checkFile(filename));
        return (double) dataLoadObject.get(key);
    }
    public double getDoubleFromFile(String key) {
        dataLoadObject = getJsonObjectFromFile(defaultPath, defaultFileName);
        return (double) dataLoadObject.get(key);
    }

    public float getFloatFromFile(String path, String filename, String key) {
        dataLoadObject = getJsonObjectFromFile(checkPath(path), checkFile(filename));
        return (float) dataLoadObject.get(key);
    }
    public float getFloatFromFile(String key) {
        dataLoadObject = getJsonObjectFromFile(defaultPath, defaultFileName);
        return (float) dataLoadObject.get(key);
    }
    public JSONObject getCustomSizeArray(String... arrayNames) {
        JSONObject jsonObject = getJsonObjectFromFile();

        for (String arrayName : arrayNames) {
            JSONArray jsonArray = (JSONArray) jsonObject.get(arrayName);

            if (jsonArray != null && !jsonArray.isEmpty()) {
                jsonObject = (JSONObject) jsonArray.get(0);
            } else {
                return null;
            }
        }

        return jsonObject;
    }


//Setter

    public void setStringToFile(String path, String filename, String key, String value) {
        final String checkedPath = checkPath(path);
        final String checkedFileName = checkFile(filename);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);


        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }
    public void setStringToFile(String key, String value) {
        final String checkedPath = checkPath(defaultPath);
        final String checkedFileName = checkFile(defaultFileName);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }

    public void setIntToFile(String path, String filename, String key, int value) {
        final String checkedPath = checkPath(path);
        final String checkedFileName = checkFile(filename);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }
    public void setIntToFile(String key, int value) {
        final String checkedPath = checkPath(defaultPath);
        final String checkedFileName = checkFile(defaultFileName);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }

    public void setBooleanToFile(String path, String filename, String key, boolean value) {
        final String checkedPath = checkPath(path);
        final String checkedFileName = checkFile(filename);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }
    public void setBooleanToFile(String key, boolean value) {
        final String checkedPath = checkPath(defaultPath);
        final String checkedFileName = checkFile(defaultFileName);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }

    public void setDoubleToFile(String path, String filename, String key, double value) {
        final String checkedPath = checkPath(path);
        final String checkedFileName = checkFile(filename);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }
    public void setDoubleToFile(String key, double value) {
        final String checkedPath = checkPath(defaultPath);
        final String checkedFileName = checkFile(defaultFileName);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }

    public void setFloatToFile(String path, String filename, String key, float value) {
        final String checkedPath = checkPath(path);
        final String checkedFileName = checkFile(filename);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }
    public void setFloatToFile(String key, float value) {
        final String checkedPath = checkPath(defaultPath);
        final String checkedFileName = checkFile(defaultFileName);

        dataLoadObject = getJsonObjectFromFile(checkedPath, checkedFileName);
        dataLoadObject.put(key, value);

        updateJsonFile(dataLoadObject, checkedPath, checkedFileName);
    }

    public JSONArray getKeyCustomArray(Object... namesAndValues){
        JSONArray mainArray = new JSONArray();
        JSONObject datas = new JSONObject();

        for (int i = 0; i < namesAndValues.length;){
            datas.put(namesAndValues[i], namesAndValues[i + 1]);
            i = i + 2;
        }
        mainArray.add(datas);

        return mainArray;
    }
    public ArrayList getNormalCustomArray(Object... values){
        ArrayList datas = new ArrayList();
        for (int i = 0; i < values.length;){
            datas.add(values[i]);
            i = i + 2;
        }
        return datas;
    }

    public void createCustomArray(String arrayName, int id, Object... namesAndValues){
        JSONArray mainArray = new JSONArray();
        JSONObject datas = new JSONObject();

        for (int i = 0; i < namesAndValues.length;){
            datas.put(namesAndValues[i], namesAndValues[i + 1]);
            i = i + 2;
        }

        mainArray.add(datas);
        dataLoadObject = getJsonObjectFromFile(defaultPath, defaultFileName);
        if (id == 0){
            dataLoadObject.put(arrayName, mainArray);
        }else {
            dataLoadObject.put(arrayName + id, mainArray);
        }
        updateJsonFile(dataLoadObject, defaultPath, defaultFileName);
    }

    private String checkPath(String path){
        if(defaultUsed){
            path = defaultPath + "/";
            return path;
        }else {
            path = path + "/";
            return path;
        }
    }
    private String checkFile(String fileName){
        if(defaultUsed){
            fileName = defaultFileName;
            return fileName;
        }else {
            return fileName;
        }
    }

}
