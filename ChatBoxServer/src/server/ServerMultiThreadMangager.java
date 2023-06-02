package server;

import java.util.ArrayList;

public class ServerMultiThreadMangager {
	private ArrayList<ServerThread> serverThreadList;
	
	public ArrayList<ServerThread> getServerThreadList() {
		return serverThreadList;
	}

	public ServerMultiThreadMangager() {
		serverThreadList = new ArrayList<>();
	}
	
	public void add(ServerThread thread) {
		serverThreadList.add(thread);
	}
	
	public void notifyNewMessage(int groupId) {
		for (ServerThread serverThread : Server.mThreadManager.getServerThreadList()) {
			System.err.println("notifyNewMessage ben server duoc goi");
			serverThread.getOut().println("updateMessageForAll<?>"+groupId);
		}
	}
	
	
}
