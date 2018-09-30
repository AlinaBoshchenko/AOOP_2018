import Client.LocalBot;
import Client.MigratoryBot;
import Server.ServerHandler;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args){
		ArrayList<ServerHandler> serverHandlers = new ArrayList<>();
		ServerHandler server1 = new ServerHandler(8189);
		serverHandlers.add(server1);
		ServerHandler server2 = new ServerHandler(8190);
		serverHandlers.add(server2);
		/*LocalBot testBot = new LocalBot(server1);
		LocalBot testBot1 = new LocalBot(server1);
		LocalBot testBot2 = new LocalBot(server1);*/
		MigratoryBot testBot1 = new MigratoryBot(server1, serverHandlers);
		MigratoryBot testBot2 = new MigratoryBot(server1, serverHandlers);
		MigratoryBot testBot3 = new MigratoryBot(server1, serverHandlers);
		//testBot.start();
		testBot1.start();
		testBot2.start();
		testBot3.start();
	}
}
