package server;

public interface JsonDBDao {
    void set(int id, String newInfo);
    void get(int id);
    void delete(int id);
}
