package pl.coderslab.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import pl.coderslab.ConnectionManager;
import pl.coderslab.models.User;

public class UserAdmin {

	private static Connection conn = ConnectionManager.getConnection();

	public static void main(String[] args) {

		String adminAction = "";

		while (!"quit".equals(adminAction)) {

			try {
				PreparedStatement ps = conn.prepareStatement(
						"SELECT Users.id, username, email, Usergroup.id AS group_id FROM Users JOIN Usergroup ON usergroup_id=Usergroup.id ORDER BY Users.id");
				ResultSet rs = ps.executeQuery();
				int counter = 1;
				System.out.println("List of users:");
				while (rs.next()) {
					int id = rs.getInt("id");
					String username = rs.getString("username");
					String email = rs.getString("email");
					int group = rs.getInt("group_id");
					System.out.println(
							counter + ". " + username + ": " + email + ", Users.id=" + id + ", Group.id=" + group);
					counter++;
				}
				System.out.println();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			System.out.println("Do you want to 'add', 'edit', 'delete', or 'quit' Users Admin: ");
			adminAction = whatToDo();
			if ("add".equals(adminAction)) {
				System.out.println("You are adding a new user. Please enter a username: ");
				String username = enterUsername();
				System.out.println("Enter a password (must be at least 4 characters): ");
				String password = enterPassword();
				System.out.println("Enter an email address: ");
				String email = enterEmail();
				System.out.println("Enter the group id to which the user will belong: ");
				int group_id = enterGroupId();
				User newUser = new User(username, password, email, group_id);
				try {
					newUser.saveToDB(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if ("edit".equals(adminAction)) {
				System.out.println("Enter the user id that you wish to edit: ");
				int user_id = enterUserId();
				System.out.println("You are editing user with id. " + user_id + ". Please enter a new username: ");
				String username = enterUsername();
				System.out.println("Enter a new password (must be at least 4 characters): ");
				String password = enterPassword();
				System.out.println("Enter an email address: ");
				String email = enterEmail();
				System.out.println("Enter the group id to which the user will belong: ");
				int group_id = enterGroupId();

				try {
					User editUser = User.loadUserById(conn, user_id);
					editUser.setUsername(username);
					editUser.setPassword(password);
					editUser.setEmail(email);
					editUser.setUsergroup_id(group_id);
					editUser.saveToDB(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if ("delete".equals(adminAction)) {
				System.out.println("Enter the user id that you wish to delete: ");
				int user_id = enterUserId();
				try {
					User deleteUser = User.loadUserById(conn, user_id);
					deleteUser.delete(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("User Admin terminated.");

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

	public static String enterUsername() {
		Scanner sc = new Scanner(System.in);
		String username = sc.nextLine();
		while (username.contains(" ") || username.length() > 10) {
			System.out.println("Enter a valid username: ");
			username = sc.nextLine();
		}
		return username;
	}

	public static String enterEmail() {
		Scanner sc = new Scanner(System.in);
		String email = sc.nextLine();
		while (checkIfEmailExists(email) || email.contains(" ") || !email.contains("@") || !email.contains(".")) {
			System.out.println("Enter a valid email: ");
			email = sc.nextLine();
		}
		return email;
	}

	public static boolean checkIfEmailExists(String email) {
		ArrayList<String> userEmails = new ArrayList<>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT email FROM Users");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String userEmail = rs.getString("email");
				userEmails.add(userEmail);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (userEmails.contains(email)) {
			return true;
		}
		return false;
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

	public static int enterUserId() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			if (sc.hasNextInt()) {
				int userId = sc.nextInt();
				if (checkIfUserIdExists(userId)) {
					return userId;
				} else {
					System.out.println("Enter a valid user id:");
					sc.nextLine();
				}
			} else {
				System.out.println("Enter a valid user id:");
				sc.nextLine();
			}
		}
	}

	public static boolean checkIfUserIdExists(int user) {
		ArrayList<Integer> userIds = new ArrayList<>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id FROM Users");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int userId = rs.getInt("id");
				userIds.add(userId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (userIds.contains(user)) {
			return true;
		}
		return false;
	}

	public static String enterPassword() {
		Scanner sc = new Scanner(System.in);
		String password = sc.nextLine();
		while (password.contains(" ") || password.length() < 4) {
			System.out.println("Enter a valid password: ");
			password = sc.nextLine();
		}
		System.out.println("Retype password: ");
		String retype = sc.nextLine();
		while (!retype.equals(password)) {
			System.out.println("Retype the same password to validate: ");
			retype = sc.nextLine();
		}
		return password;
	}

}
