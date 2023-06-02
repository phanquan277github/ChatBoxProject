package database;

import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	private static String URL = "jdbc:mysql://localhost:3306/chatbox";
    private static String USERNAME = "root";
    private static String PASSWORD = "quanphan066204008405";
    
    public ArrayList<String> getAccountList() throws SQLException {
    	Connection conn = null;
		String row = null;
		ArrayList<String> storage = new ArrayList<>();
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * from ta_acc_account");
			
			while(rs.next()) {
				int id = rs.getInt(1);		// các số 1, 2, 3 trong hàm get là thứ tự của cột trong DB
				String userName = rs.getString(2);
				String password = rs.getString(3);
				row = id + "<?>" + userName + "<?>" + password; 
				storage.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();	// ngắt kết nối sau khi hoàn thành truy vấn
		}
    	return storage;
    }
    
    public void createAccount(String userName, String password) throws SQLException {
    	Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
	    	String value = "('" + userName + "', '" + password + "')";	// phải có cặp nháy đơn '' bọc string để sql nhận ra đó là giá trị chuỗi
			st.executeUpdate("insert into ta_acc_account(T_userName, T_password) values " + value);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
    }
    
    public int getAccountIdByUserName(String userName) throws SQLException {
    	Connection conn = null;
		int row = 0;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select I_id from ta_acc_account where T_userName = '"+ userName +"'");
			
			while(rs.next()) {
				row = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();	// ngắt kết nối sau khi hoàn thành truy vấn
		}
    	return row;
    }
    
    public String getMemberInfoByAccountId(int accountId) throws SQLException {
    	Connection conn = null;
		String row = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * from ta_mbr_member where I_account_id = '"+ accountId+"'");
			
			while(rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				int accId = rs.getInt(3);
				row = id + "<?>" + name + "<?>" + accId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();	// ngắt kết nối sau khi hoàn thành truy vấn
		}
    	return row;
    }
    
    public void createMember(String memberName, int accountId) throws SQLException {
    	Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
	    	String value = "('" + memberName + "', '" + accountId + "')";
			st.executeUpdate("insert into ta_mbr_member(T_name, I_account_id) values " + value);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
    }
    
    public String checkGroupExistence(String groupName, int memberId) throws SQLException {
    	Connection conn = null;
    	String rep = "false";
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
	    	String where = "T_name = '" + groupName + "' AND I_member_id = " + memberId;
			ResultSet rs = st.executeQuery("SELECT * FROM ta_grp_group WHERE " + where);
			while(rs.next()) {
				System.err.println("debug: -"+rs.getString("T_name")+"-end");
				rep = rs.getString("T_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return rep.equals("false") ? "false" : "true";
    }
   
    public int getGroupIdByGroupNameAndMemberId(String groupName, int memberId) throws SQLException {
    	Connection conn = null;
    	int rep = 0;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
	    	String where = "T_name = '" + groupName + "' AND I_member_id = " + memberId;
			ResultSet rs = st.executeQuery("SELECT I_id FROM ta_grp_group WHERE " + where);
			
			while(rs.next()) {
				rep = rs.getInt("I_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return rep;
    }
     
    public void createGroup(String groupName, int memberId) throws SQLException {
    	Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
	    	String value = "('" + groupName + "', "+ memberId + ")";
			st.executeUpdate("insert into ta_grp_group(T_name, I_member_id) values " + value);
			
			int groupId = getGroupIdByGroupNameAndMemberId(groupName, memberId);
	    	String value2 = "(" + groupId + "," + memberId + ")";
			st.executeUpdate("insert into ta_grm_groupmembers(I_group_id, I_member_id) values " + value2);
			
			String value3 = "(" + groupId + ", 1 )";
			st.executeUpdate("insert into ta_grm_groupmembers(I_group_id, I_member_id) values " + value3);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
    }
    
    public int getMemberIdByUserName(String userName) throws SQLException {
    	Connection conn = null;
    	int memberId = 0;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select mb.I_id from (ta_acc_account as acc inner join ta_mbr_member as mb on acc.I_id = mb.I_account_id ) where acc.T_userName = '"+ userName + "'");
			
			while(rs.next()) {
				memberId = rs.getInt("I_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return memberId;
    }
    
    public ArrayList<String> getGroupListByMemberId(int memberId) throws SQLException {
    	Connection conn = null;
		String row = null;
		ArrayList<String> storage = new ArrayList<>();
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select grp.I_id, grp.T_name from (ta_grp_group as grp inner join ta_grm_groupmembers as grm on grp.I_id = grm.I_group_id) where grm.I_member_id = "+ memberId);
			
			while(rs.next()) {
				int groupId = rs.getInt("I_id");
				String groupName = rs.getString("T_name");
				row = groupId + "<?>" + groupName;
				storage.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
    	return storage;
    }
    
    public void addMemberToGroup(int groupId, String userName) throws SQLException {
    	Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
	    	int memberId = getMemberIdByUserName(userName);
	    	
	    	String value = "(" + groupId + ", " + memberId + ")";
			st.executeUpdate("insert into ta_grm_groupmembers(I_group_id, I_member_id) values " + value);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
    }
    
    public void addMessage(int groupId, int memberId, String content) throws SQLException {
    	Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
	    	
	    	String value = "(" + groupId + ", " + memberId + ", '"+ content + "')";
			st.executeUpdate("insert into ta_msg_message(I_group_id, I_member_id, T_content) values " + value);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
    }
    
    public String getMemberNameByUserName(String userName) throws SQLException {
    	Connection conn = null;
    	String memberName = "";
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select mb.T_name from (ta_acc_account as acc inner join ta_mbr_member as mb on acc.I_id = mb.I_account_id ) where acc.T_userName = '"+ userName + "'");
			
			while(rs.next()) {
				memberName = rs.getString("T_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return memberName;
    }
    
    public String getMemberNameByMemberId(int memberId) throws SQLException {
    	Connection conn = null;
    	String memberName = "";
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select T_name from ta_mbr_member where I_id = "+ memberId);
			
			while(rs.next()) {
				memberName = rs.getString("T_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return memberName;
    }
    
    public int getGroupIdByMemberId(int memberId) throws SQLException {
    	Connection conn = null;
    	int groupId = 0;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select I_id from ta_grp_group WHERE I_member_id = " + memberId + " ORDER BY I_id DESC LIMIT 1;");
			
			while(rs.next()) {
				groupId = rs.getInt("I_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return groupId;
    }
    
    public ArrayList<String> getMessagesListByGroupId(int groupId) throws SQLException {
    	Connection conn = null;
		String row = "";
		ArrayList<String> storage = new ArrayList<>();
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    	Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select mb.T_name, msg.T_content from (ta_msg_message as msg inner join ta_mbr_member as mb on msg.I_member_id = mb.I_id ) where msg.I_group_id = "+ groupId + " ORDER BY msg.I_id");
			
			while(rs.next()) {
				String memberName = rs.getString("T_name");
				String content = rs.getString("T_content");
				row = memberName + ": " + content; 
				storage.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
    	return storage;
    }
    
    public static void main(String[] args) throws SQLException {
    	Database db = new Database();
    	ArrayList<String> ts = new ArrayList<>();
    	try {
			ts = db.getGroupListByMemberId(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	for (String string : ts) {
			System.out.println(string);
		}
    	
	}
}
