package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class JsonDBArrayImpl implements JsonDBDao {
//    private final String FILE_DIR = "./JSON Database (Java)/task/src/server/data/db.json";
    private final String FILE_DIR = System.getProperty("user.dir") + "/src/server/data/db.json";

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private final Set<ServerInfo> infoSet;

    public JsonDBArrayImpl() {
        infoSet = new HashSet<>();
    }

    @Override
    public String set(String key, String value) {
        getDataFromFile();
        ServerInfo info = getInfoFromSet(key);
        if (info == null) {
            info = new ServerInfo(key, value);
        } else {
            info.setValue(value);
        }
        infoSet.add(info);
        saveDataToFile();
        return "OK";
    }

    @Override
    public String get(String key) {
        getDataFromFile();
        ServerInfo info = getInfoFromSet(key);
        if (info != null) {
            return info.getValue();
        }
        return "ERROR";
    }

    @Override
    public String delete(String key) {
        getDataFromFile();
        ServerInfo info = getInfoFromSet(key);
        if (info != null) {
            infoSet.remove(info);
            saveDataToFile();
            return "OK";
        }
        return "ERROR";
    }

    private ServerInfo getInfoFromSet(String key) {
        for (ServerInfo info : infoSet) {
            if (key.equals(info.getKey())) {
                return info;
            }
        }
        return null;
    }

    private void getDataFromFile() {
        infoSet.clear();
        readLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_DIR))) {
            Set<JsonObject> jsonObjs = reader.lines()
                    .map(l -> new Gson().fromJson(l, JsonObject.class))
                    .collect(Collectors.toSet());
            for (JsonObject obj : jsonObjs) {
                String key = obj.get("key").getAsString();
                String value = obj.get("value").toString();
                infoSet.add(new ServerInfo(key, value));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        readLock.unlock();
    }

    private void saveDataToFile() {
        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_DIR))) {
            for (ServerInfo info : infoSet) {
                JsonObject obj = new JsonObject();
                obj.addProperty("key", info.getKey());
                try {
                    obj.add("value", new Gson().fromJson(info.getValue(), JsonObject.class));
                } catch (Exception ex) {
                    obj.addProperty("value", info.getValue());
                }
                writer.write(new Gson().toJson(obj));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        writeLock.unlock();
    }
}
