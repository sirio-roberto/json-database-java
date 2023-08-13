package server;

import com.google.gson.Gson;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class JsonDBArrayImpl implements JsonDBDao {
    private final String FILE_DIR = "JSON Database (Java)/task/src/server/data/db.json";

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    private final Set<ServerInfo> infoSet;

    public JsonDBArrayImpl() {
        infoSet = new HashSet<>();
    }

    @Override
    public String set(String key, String value) {
        getDataFromFile();
        ServerInfo info = new ServerInfo(key, value);
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
            infoSet.addAll(reader.lines()
                    .map(l -> new Gson().fromJson(l, ServerInfo.class))
                    .collect(Collectors.toSet()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        readLock.unlock();
    }

    private void saveDataToFile() {
        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_DIR))) {
            for (ServerInfo info : infoSet) {
                writer.write(new Gson().toJson(info));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        writeLock.unlock();
    }
}
