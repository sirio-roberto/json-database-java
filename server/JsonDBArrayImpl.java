package server;

import java.util.Arrays;

public class JsonDBArrayImpl implements JsonDBDao {

    private final String[] infoArray;

    public JsonDBArrayImpl() {
        infoArray = new String[1000];
        Arrays.fill(infoArray, "");
    }

    @Override
    public String set(int id, String newInfo) {
        if (isValidIndex(id)) {
            int arrayIndex = id - 1;
            infoArray[arrayIndex] = newInfo;

            return "OK";
        }
        return "ERROR";
    }

    @Override
    public String get(int id) {
        if (isValidIndex(id)) {
            int arrayIndex = id - 1;
            if (infoArray[arrayIndex].isBlank()) {
                return "ERROR";
            }
            return infoArray[arrayIndex];
        }
        return "ERROR";
    }

    @Override
    public String delete(int id) {
        if (isValidIndex(id)) {
            int arrayIndex = id - 1;
            infoArray[arrayIndex] = "";

            return "OK";
        }
        return "ERROR";
    }

    private boolean isValidIndex(int id) {
        return id >= 1 && id <= infoArray.length;
    }
}
