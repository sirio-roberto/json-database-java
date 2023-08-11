package server;

public interface JsonDBDao {
    String set(int id, String newInfo);
    String get(int id);
    String delete(int id);
}
