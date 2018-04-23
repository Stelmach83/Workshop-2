package pl.coderslab.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import pl.coderslab.ConnectionManager;
import pl.coderslab.models.Group;

public class GroupAdmin {

	private static Connection conn = ConnectionManager.getConnection();

	public static void main(String[] args) {

		String adminAction = "";

		while (!"quit".equals(adminAction)) {

			try {
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM Usergroup");
				ResultSet rs = ps.executeQuery();
				int counter = 1;
				System.out.println("List of groups:");
				while (rs.next()) {
					int id = rs.getInt("id");
					String name = rs.getString("name");
					System.out.println(counter + ". " + name + ", id=" + id);
					counter++;
				}
				System.out.println();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Do you want to 'add', 'edit', 'delete', or 'quit' Group Admin: ");
			adminAction = whatToDo();

			if ("add".equals(adminAction)) {
				System.out.println("You are adding a new group. Please enter a group name: ");
				String name = enterGroupname();
				Group newGroup = new Group(name);
				try {
					newGroup.saveToDB(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if ("edit".equals(adminAction)) {
				System.out.println("Enter group id to edit:");
				int group_id = enterGroupId();
				System.out.println("You are editing a group. Please enter a new group name: ");
				String name = enterGroupname();
				try {
					Group editGroup = Group.loadGroupById(conn, group_id);
					editGroup.setName(name);
					editGroup.saveToDB(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			if ("delete".equals(adminAction)) {
				System.out.println("Enter group id to delete:");
				int group_id = enterGroupId();
				try {
					Group deleteGroup = Group.loadGroupById(conn, group_id);
					deleteGroup.delete(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		System.out.println("Group Admin terminated.");

	}

	public static String whatToDo() {
		Scanner sc = new Scanner(System.in);
		String answer = sc.nextLine();
		while (!(answer.equals("add")) && !(answer.equals("edit")) && !(answer.equals("delete"))
				&& !(answer.equals("quit")) || answer.contains(" ")) {
			System.out.println("Type 'add', 'edit', 'delete', or 'quit':");
			answer = sc.nextLine();
		}
		return answer;
	}

	public static String enterGroupname() {
		Scanner sc = new Scanner(System.in);
		String group = sc.nextLine();
		while (group.contains(" ") || group.length() > 6) {
			System.out.println("Enter a valid groupname: ");
			group = sc.nextLine();
		}
		return group;
	}

	public static int enterGroupId() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			if (sc.hasNextInt()) {
				int groupId = sc.nextInt();
				if (checkIfGroupIdExists(groupId)) {
					return groupId;
				} else {
					System.out.println("Enter a valid group id:");
					sc.nextLine();
				}
			} else {
				System.out.println("Enter a valid group id:");
				sc.nextLine();
			}
		}
	}

	public static boolean checkIfGroupIdExists(int group) {
		ArrayList<Integer> groupIds = new ArrayList<>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id FROM Usergroup");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int groupId = rs.getInt("id");
				groupIds.add(groupId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (groupIds.contains(group)) {
			return true;
		}
		return false;
	}

}
