package server;

import client.ClientRequest;
import com.google.gson.Gson;
import server.responses.AbstractResponse;
import server.responses.ErrorResponse;
import server.responses.OkResponse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
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
        String[] userCommandArray = userCommand.trim().split(",");
        for (Command command: commands) {
            if (userCommandArray[0].contains(command.name)) {
                return command.execute(userCommand);
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

            AbstractResponse response = getResponseFromString(jsonDB.get(request.getKey()));
            return new Gson().toJson(response);
        }
    }

    private class SetCommand extends Command {

        public SetCommand(String name) {
            super(name);
        }

        @Override
        String execute(String jsonCommand) {
            ClientRequest request = new Gson().fromJson(jsonCommand, ClientRequest.class);

            AbstractResponse response = getResponseFromString(jsonDB.set(request.getKey(), request.getValue()));
            return new Gson().toJson(response);
        }

        private String concatArrayInString(String[] strings) {
            StringBuilder sb = new StringBuilder();
            for (String s: strings) {
                sb.append(s).append(" ");
            }
            return sb.toString().trim();
        }
    }

    private class DeleteCommand extends Command {

        public DeleteCommand(String name) {
            super(name);
        }

        @Override
        String execute(String jsonCommand) {
            ClientRequest request = new Gson().fromJson(jsonCommand, ClientRequest.class);

            AbstractResponse response = getResponseFromString(jsonDB.delete(request.getKey()));
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
}
