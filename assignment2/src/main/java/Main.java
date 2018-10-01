import Client.IndependentBot;
import Client.LocalBot;
import Client.MigratoryBot;
import Server.ServerHandler;
import view.ChatBox;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args){
		ArrayList<ServerHandler> serverHandlers = new ArrayList<>();
		//PLEASE ADD NEW SERVERS OR NEW BOTS IF YOU WANT TO TEST ANY MORE FUNCTIONALITY
		ServerHandler server1 = new ServerHandler(8189);
		serverHandlers.add(server1);
		ServerHandler server2 = new ServerHandler(8190);
		serverHandlers.add(server2);
		LocalBot testBot = new LocalBot(server1);
		IndependentBot toxicBot = new IndependentBot(server2, serverHandlers);
		MigratoryBot testBot1 = new MigratoryBot(server1, serverHandlers);
		MigratoryBot testBot2 = new MigratoryBot(server1, serverHandlers);
		MigratoryBot testBot3 = new MigratoryBot(server1, serverHandlers);


		testBot.start();
		testBot1.start();
		testBot2.start();
		testBot3.start();
		toxicBot.start();

	}
}
