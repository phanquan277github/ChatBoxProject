package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import database.Database;

public class ServerThread extends Thread {
	private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Database db;
    private boolean isClosed;
    
	public BufferedReader getIn() {
		return in;
	}
	public PrintWriter getOut() {
		return out;
	}

	public ServerThread(Socket socket) {
    	this.socket = socket;
    	isClosed = false;
    }
    
    @Override
    public void run() {
    	try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			db = new Database();
			
			String message;
			while(!isClosed){
				message = in.readLine();
				System.out.println("debug server message "+message);
				if(message.startsWith("request<?>")) {
                	String[] handle = message.split("\\<\\?\\>");
                	switch(handle[1]) {
                		case "createAccount": 
                			String reply = userNameIsExits(handle[2]); 
                			if(reply.equals("true")) {	// nếu tồn tại userName
                    			out.println("false"); // không cho phép người dùng đăng ký
                			}else {
                				out.println("true");
                				db.createAccount(handle[2], handle[3]);
                			}
                			break;
                		case "checkAccount":
                			String reply1 = checkAccount(handle[2], handle[3]);
                			out.println(reply1);	//gọi hàm trả về nếu account đúng trả về memberId của account đó
                			break;
                		case "createMember": 
                			db.createMember(handle[2], Integer.parseInt(handle[3]));
                			out.println("true");
                			break;
                		case "getAccountIdByUserName":
                			int accountId = db.getAccountIdByUserName(handle[2]);
                			out.println(accountId);
                			break;
                		case "getMemberInfoByAccountId":
                			String memberName = db.getMemberInfoByAccountId(Integer.parseInt(handle[2]));
                			out.println(memberName);
                    		break;
                		case "createGroup":
                			String check = db.checkGroupExistence(handle[2], Integer.parseInt(handle[3])); // nếu tồn tại group, memberId thì trả về true
                			 
                			if(check.equals("true")) {
                				out.println("false");
                			}else if(check.equals("false")){
                				db.createGroup(handle[2], Integer.parseInt(handle[3]));
                				int newGroupId = db.getGroupIdByMemberId(Integer.parseInt(handle[3]));
                				String memberName2 = db.getMemberNameByMemberId(Integer.parseInt(handle[3]));
                				db.addMessage(newGroupId, 1, memberName2+" đã tạo nhóm!"); // hệ thống thông báo đã tạo nhóm
                				out.println("true");
                			}
                			break;
                		case "getGroupListByMemberId":
                			String reply2 = "";
                			ArrayList<String> grList = db.getGroupListByMemberId(Integer.parseInt(handle[2]));
            				for (String gr : grList) {
            					reply2 += gr + "<#>";
            				}
                			out.println(reply2);
                			break;
                		case "addMemberToGroup":
                			db.addMemberToGroup(Integer.parseInt(handle[2]), handle[3]);
                			String memberName2 = db.getMemberNameByUserName(handle[3]);
                			String message2 = handle[4]+" đã thêm "+ memberName2 + " vào nhóm!";
                			db.addMessage(Integer.parseInt(handle[2]), 1, message2);
                			
                			out.println("true");
                			break;
                		case "checkUserName":
                			String reply3 = userNameIsExits(handle[2]); // true nếu tồn tại userName
                			out.println(reply3);
                			break;
                		case "getMessagesListByGroupId": 
                			String reply4 = "";
                			ArrayList<String> messageList = db.getMessagesListByGroupId(Integer.parseInt(handle[2]));
            				for (String gr : messageList) {
            					reply4 += gr + "<?>";
            				}
            				out.println(reply4);
                			break;
                		case "sendMessage":
                			db.addMessage(Integer.parseInt(handle[2]), Integer.parseInt(handle[3]), handle[4]);
                			out.println("true");
                			Server.mThreadManager.notifyNewMessage(Integer.parseInt(handle[2]));
                			break;
                		case "updateMessageForAll":
                			
                			out.println("newMessage<?>"+Integer.parseInt(handle[2]));
                			
                			break;
                	}
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void write(String message) {
    	System.err.println("write cua ServerThread duoc goi");
    	out.println(message);
    }
    
    private String userNameIsExits(String userName) throws SQLException {
    	ArrayList<String> accList = db.getAccountList();
			for (String acc : accList) {
				String[] tmp = acc.split("\\<\\?\\>");
				if(tmp[1].equals(userName)) {
					return "true";	// trả về true nếu tồn tại account
				}
			}
		return "false";	// trả về true chưa tồn tại account nào
    }
    
    private String checkAccount(String userName, String password) throws SQLException {
	   	 ArrayList<String> accList = db.getAccountList();
			for (String acc : accList) {
				String[] tmp = acc.split("\\<\\?\\>");
				if(tmp[1].equals(userName) && tmp[2].equals(password)) {
					return "true";	// trả về accountId
				}
			}
		 return "false";	// trả về false nếu tk, mk đúng
    }
}
