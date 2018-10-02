package Server;


public enum ServerCommand
{
    LOGIN_COMMAND("-login "),
    LOGOFF_COMMAND("-logoff");

    private String commandString;

    ServerCommand(String commandString) {
        this.commandString = commandString;
    }

    public String getCommandString() {
        return commandString;
    }

    @Override
    public String toString() {
        return commandString;
    }
}