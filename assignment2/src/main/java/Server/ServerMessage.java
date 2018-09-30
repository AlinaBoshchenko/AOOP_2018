package Server;

public class ServerMessage {
    private ServerCommand command = null;
    private String arguments = null;
    private boolean valid = true;

    ServerMessage(String message) {
        if(message.startsWith("-")) {
            for(ServerCommand command : ServerCommand.values()) {
                if(message.startsWith(command.getCommandString())) {
                    this.command = command;
                    this.arguments = message.substring(command.getCommandString().length());
                    return;
                }
            }
        }
        arguments = message;
    }

    boolean isValid() {
        return valid;
    }

    boolean isCommand() {
        return command != null;
    }

    public ServerCommand getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }
}
