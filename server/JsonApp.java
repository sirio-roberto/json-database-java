package server;

import client.ClientRequest;
import com.google.gson.*;
import server.responses.AbstractResponse;
import server.responses.ErrorResponse;
import server.responses.OkResponse;

import java.util.HashSet;
import java.util.Set;

public class JsonApp {
    private final JsonDBDao jsonDB;
    private final Set<Command> commands;

    public JsonApp() {
        jsonDB = new JsonDBArrayImpl();

        commands = new HashSet<>();
        commands.add(new GetCommand("get"));
        commands.add(new SetCommand("set"));
        commands.add(new DeleteCommand("delete"));
        commands.add(new ExitCommand("exit"));
    }

    public String runAndGetResponse(String userCommand) {
        JsonElement jsonRequest = new Gson().fromJson(userCommand, JsonElement.class);

        JsonElement typeElement = getElementValueByKeyName(jsonRequest, "type");
        if (typeElement != null && typeElement.isJsonPrimitive()) {
            String typeStr = removeQuotes(typeElement.getAsString());

            for (Command command: commands) {
                if (typeStr.equals(command.name)) {
                    return command.execute(jsonRequest);
                }
            }
        }

        return new Gson().toJson(new ErrorResponse("ERROR", "Unknown command"));
    }

    abstract static class Command {
        private final String name;

        public Command(String name) {
            this.name = name;
        }

        abstract String execute(JsonElement jsonRequest);
    }

    private class GetCommand extends Command {

        public GetCommand(String name) {
            super(name);
        }

        @Override
        String execute(JsonElement jsonRequest) {
            JsonElement keyElement = getElementValueByKeyName(jsonRequest, "key");
            AbstractResponse response;

            if (keyElement != null) {
                if (keyElement.isJsonPrimitive()) {
                    String keyStr = removeQuotes(keyElement.getAsString());
                    response = getResponseFromString(jsonDB.get(keyStr));
                } else {
                    JsonArray keyArray = keyElement.getAsJsonArray();
                    String keyStr = removeQuotes(keyArray.get(0).getAsString());

                    if (keyArray.size() == 1) {
                        response = getResponseFromString(jsonDB.get(keyStr));
                    } else {
                        JsonElement elementToGet = new Gson().fromJson(jsonDB.get(keyStr), JsonElement.class);
                        for (int i = 1; i < keyArray.size(); i++) {
                            elementToGet = getElementValueByKeyName(elementToGet, removeQuotes(keyArray.get(i).getAsString()));
                        }
                        response = getResponseFromString(elementToGet.getAsString());
                    }
                }
                return new Gson().toJson(response);
            }
            return new Gson().toJson(new ErrorResponse("ERROR", "Invalid Json request"));
        }
    }

    private class SetCommand extends Command {

        public SetCommand(String name) {
            super(name);
        }

        @Override
        String execute(JsonElement jsonRequest) {
            JsonElement keyElement = getElementValueByKeyName(jsonRequest, "key");
            JsonElement valueElement = getElementValueByKeyName(jsonRequest, "value");

            if (keyElement != null && valueElement != null) {
                if (keyElement.isJsonPrimitive()) {
                    String keyStr = removeQuotes(keyElement.getAsString());
                    AbstractResponse response = getResponseFromString(jsonDB.set(keyStr, valueElement.toString()));
                    return new Gson().toJson(response);
                } else if (keyElement.isJsonArray()){
                    JsonArray keyArray = keyElement.getAsJsonArray();
                    JsonElement rootElementKey = keyArray.get(0);
                    String rootKeyStr = removeQuotes(rootElementKey.getAsString());

                    if (keyArray.size() == 1) {
                        AbstractResponse response = getResponseFromString(jsonDB.set(rootKeyStr, valueElement.toString()));
                        return new Gson().toJson(response);
                    }

                    if (rootElementKey.isJsonPrimitive()) {
                        String rootValueStr = jsonDB.get(rootKeyStr);
                        JsonElement rootValueElement = new Gson().fromJson(rootValueStr, JsonElement.class);
                        JsonElement elementToEdit = rootValueElement;
                        for (int i = 1; i < keyArray.size() - 1; i++) {
                            elementToEdit = getElementValueByKeyName(elementToEdit, removeQuotes(keyArray.get(i).getAsString()));
                        }
                        if (elementToEdit != null) {
                            JsonObject elementToEditObj = elementToEdit.getAsJsonObject();
                            elementToEditObj.addProperty(keyArray.get(keyArray.size() - 1).getAsString(), removeQuotes(valueElement.toString()));
                            AbstractResponse response = getResponseFromString(jsonDB.set(rootKeyStr, new Gson().toJson(rootValueElement)));
                            return new Gson().toJson(response);
                        }
                    }
                }
            }
            return new Gson().toJson(new ErrorResponse("ERROR", "Invalid Json request"));
        }
    }

    private class DeleteCommand extends Command {


        public DeleteCommand(String name) {
            super(name);
        }

        @Override
        String execute(JsonElement jsonRequest) {
            JsonElement keyElement = getElementValueByKeyName(jsonRequest, "key");
            AbstractResponse response;

            if (keyElement != null) {
                if (keyElement.isJsonPrimitive()) {
                    String keyStr = removeQuotes(keyElement.getAsString());
                    response = getResponseFromString(jsonDB.delete(keyStr));
                } else {
                    JsonArray keyArray = keyElement.getAsJsonArray();
                    String keyStr = removeQuotes(keyArray.get(0).getAsString());

                    if (keyArray.size() == 1) {
                        response = getResponseFromString(jsonDB.delete(keyStr));
                    } else {
                        JsonElement rootValueElement = new Gson().fromJson(jsonDB.get(keyStr), JsonElement.class);
                        JsonElement elementToEdit = rootValueElement;
                        for (int i = 1; i < keyArray.size() - 1; i++) {
                            elementToEdit = getElementValueByKeyName(elementToEdit, removeQuotes(keyArray.get(i).getAsString()));
                        }
                        if (elementToEdit != null) {
                            JsonObject elementToEditObj = elementToEdit.getAsJsonObject();
                            elementToEditObj.remove(keyArray.get(keyArray.size() - 1).getAsString());
                        }
                        response = getResponseFromString(jsonDB.set(keyStr, new Gson().toJson(rootValueElement)));
                    }
                }
                return new Gson().toJson(response);
            }
            return new Gson().toJson(new ErrorResponse("ERROR", "Invalid Json request"));
        }

    }
    private static class ExitCommand extends Command {
        public ExitCommand(String name) {
            super(name);
        }

        @Override
        String execute(JsonElement jsonRequest) {
            return new Gson().toJson(new OkResponse("OK", null));
        }

    }
    private AbstractResponse getResponseFromString(String dbMethodResponse) {
        return switch (dbMethodResponse) {
            case "ERROR" -> new ErrorResponse("ERROR", "No such key");
            case "OK" -> new OkResponse("OK", null);
            default -> new OkResponse("OK", dbMethodResponse);
        };
    }

    private JsonElement getElementValueByKeyName(JsonElement requestJson, String name) {
        if (requestJson.isJsonObject()) {
            JsonObject requestObj = requestJson.getAsJsonObject();
            if (requestObj.size() != 0) {
                for (String memberName : requestObj.keySet()) {
                    if (memberName.equals(name)) {
                        return requestObj.get(memberName);
                    }
                    getElementValueByKeyName(requestObj.get(memberName), name);
                }
            }
        }
        return null;
    }

    private String removeQuotes(String stringWithQuotes) {
        return stringWithQuotes.replaceAll("\"", "");
    }
}
