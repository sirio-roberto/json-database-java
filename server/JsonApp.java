package server;

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

    public void run() {
        isRunning = true;
        try (Scanner scan = new Scanner(System.in)) {
            while (isRunning) {
                String[] userCommand = scan.nextLine().trim().split(" ");
                commands.forEach(command -> {
                    if (command.name.equals(userCommand[0])) {
                        command.execute(Arrays.copyOfRange(userCommand, 1, userCommand.length));
                    }
                });
            }
        }
    }

    public String runAndGetResponse(String userCommand) {
        String[] userCommandArray = userCommand.trim().split(" ");
        for (Command command: commands) {
            if (command.name.equals(userCommandArray[0])) {
                return command.execute(Arrays.copyOfRange(userCommandArray, 1, userCommandArray.length));
            }
        }
        return "ERROR";
    }

    abstract static class Command {
        private final String name;

        public Command(String name) {
            this.name = name;
        }

        abstract String execute(String... args);
    }

    private class GetCommand extends Command {

        public GetCommand(String name) {
            super(name);
        }

        @Override
        String execute(String... args) {
            int id = Integer.parseInt(args[0]);
            return jsonDB.get(id);
        }
    }

    private class SetCommand extends Command {

        public SetCommand(String name) {
            super(name);
        }

        @Override
        String execute(String... args) {
            int id = Integer.parseInt(args[0]);

            return jsonDB.set(id, concatArrayInString(Arrays.copyOfRange(args,1, args.length)));
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
        String execute(String... args) {
            int id = Integer.parseInt(args[0]);
            return jsonDB.delete(id);
        }
    }
    private class ExitCommand extends Command {

        public ExitCommand(String name) {
            super(name);
        }

        @Override
        String execute(String... args) {
            isRunning = false;
            return "OK";
        }
    }
}
