package server;

public interface JsonDBDao {
    String set(String key, String value);
    String get(String key);
    String delete(String key);
}
