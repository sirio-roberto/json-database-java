package server;

import client.ClientRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.responses.AbstractResponse;
import server.responses.ErrorResponse;
import server.responses.OkResponse;

import java.util.HashSet;
import java.util.Set;

public class JsonApp {
    private boolean isRunning;
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
        JsonElement requestJson = new Gson().fromJson(userCommand, JsonElement.class);

        JsonElement typeElement = getElementByName(requestJson, "type");
        if (typeElement != null && typeElement.isJsonPrimitive()) {
            String typeStr = typeElement.getAsString().replaceAll("\"", "");

            for (Command command: commands) {
                if (typeStr.equals(command.name)) {
                    return command.execute(userCommand);
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

        abstract String execute(String jsonCommand);
    }

    private class GetCommand extends Command {

        public GetCommand(String name) {
            super(name);
        }

        @Override
        String execute(String jsonCommand) {
            ClientRequest request = new Gson().fromJson(jsonCommand, ClientRequest.class);
            // TODO: workaround for simple scenario
            String keyStr = request.getKey()[request.getKey().length - 1];

            AbstractResponse response = getResponseFromString(jsonDB.get(keyStr));
            return new Gson().toJson(response);
        }
    }

    private class SetCommand extends Command {

        public SetCommand(String name) {
            super(name);
        }

        @Override
        String execute(String jsonCommand) {
            JsonElement requestJson = new Gson().fromJson(jsonCommand, JsonElement.class);

            JsonElement keyElement = getElementByName(requestJson, "key");
            JsonElement valueElement = getElementByName(requestJson, "value");

            if (keyElement != null && valueElement != null && keyElement.isJsonPrimitive()) {
                String typeStr = keyElement.getAsString().replaceAll("\"", "");
                AbstractResponse response = getResponseFromString(jsonDB.set(typeStr, valueElement.getAsString()));
                return new Gson().toJson(response);
            }
            return new Gson().toJson(new ErrorResponse("ERROR", "Invalid Json request"));
        }

    }

    private class DeleteCommand extends Command {


        public DeleteCommand(String name) {
            super(name);
        }

        @Override
        String execute(String jsonCommand) {
            ClientRequest request = new Gson().fromJson(jsonCommand, ClientRequest.class);
            // TODO: workaround for simple scenario
            String keyStr = request.getKey()[request.getKey().length - 1];

            AbstractResponse response = getResponseFromString(jsonDB.delete(keyStr));
            return new Gson().toJson(response);
        }

    }
    private class ExitCommand extends Command {
        public ExitCommand(String name) {
            super(name);
        }

        @Override
        String execute(String jsonCommand) {
            isRunning = false;
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

    private JsonElement getElementByName(JsonElement requestJson, String name) {
        if (requestJson.isJsonObject()) {
            JsonObject requestObj = requestJson.getAsJsonObject();
            if (requestObj.size() != 0) {
                for (String memberName : requestObj.keySet()) {
                    if (memberName.equals(name)) {
                        return requestObj.get(memberName);
                    }
                    getElementByName(requestObj.get(memberName), name);
                }
            }
        }
        return null;
    }
}
