package server;

import java.util.Arrays;

public class JsonDBArrayImpl implements JsonDBDao {

    private final String[] infoArray;

    public JsonDBArrayImpl() {
        infoArray = new String[100];
        Arrays.fill(infoArray, "");
    }

    @Override
    public void set(int id, String newInfo) {
        if (isValidIndex(id)) {
            int arrayIndex = id - 1;
            infoArray[arrayIndex] = newInfo;

            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }

    @Override
    public void get(int id) {
        if (isValidIndex(id)) {
            int arrayIndex = id - 1;
            if (infoArray[arrayIndex].isBlank()) {
                System.out.println("ERROR");
            } else {
                System.out.println(infoArray[arrayIndex]);
            }
        } else {
            System.out.println("ERROR");
        }
    }

    @Override
    public void delete(int id) {
        if (isValidIndex(id)) {
            int arrayIndex = id - 1;
            infoArray[arrayIndex] = "";

            System.out.println("OK");
        } else {
            System.out.println("ERROR");
        }
    }

    private boolean isValidIndex(int id) {
        return id >= 1 && id <= 100;
    }
}
