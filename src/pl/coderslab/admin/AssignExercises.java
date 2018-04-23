package pl.coderslab.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import pl.coderslab.ConnectionManager;
import pl.coderslab.models.Solution;

public class AssignExercises {

	private static Connection conn = ConnectionManager.getConnection();

	public static void main(String[] args) {

		String adminAction = "";

		while (!"quit".equals(adminAction)) {

			System.out.println("Select an option: 'add', 'view', 'quit'");
			adminAction = whatToDo();

			if ("add".equals(adminAction)) {

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
				System.out.println("Which user id would you like to assign a solution to: ");
				int user_id = enterUserId();
				try {
					PreparedStatement ps = conn.prepareStatement("SELECT * FROM Exercise");
					ResultSet rs = ps.executeQuery();
					int counter = 1;
					System.out.println("List of exercises:");
					while (rs.next()) {
						int id = rs.getInt("id");
						String title = rs.getString("title");
						String desc = rs.getString("description");
						System.out.println(counter + ". " + title + " (" + desc + "), id=" + id);
						counter++;
					}
					System.out.println();

				} catch (SQLException e) {
					e.printStackTrace();
				}

				System.out.println("Which exercise id would you like to assign a solution to: ");
				int exer_id = enterExerId();
				Solution newSolution = new Solution("", exer_id, user_id);
				try {
					newSolution.saveToDB(conn);
					System.out.println("Solution saved.");
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			if ("view".equals(adminAction)) {

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
				System.out.println("Which user id would you like to view solutions of: ");
				int user_id = enterUserId();

				try {
					PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM Solution WHERE users_id=?");
					ps1.setInt(1, user_id);
					ResultSet rs1 = ps1.executeQuery();
					System.out.println("List of solutions for user:");
					if (!rs1.next()) {
						System.out.println("This user doesn't have any solutions.");
					}
					PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM Solution WHERE users_id=?");
					ps2.setInt(1, user_id);
					ResultSet rs2 = ps2.executeQuery();
					int counter = 1;
					while (rs2.next()) {
						int id = rs2.getInt("id");
						String created = rs2.getString("created");
						String desc = rs2.getString("description");
						int exer_id = rs2.getInt("exercise_id");
						System.out.println(counter + ". " + desc + " - solution_id=" + id + " - exercise_id=" + exer_id
								+ ", created: " + created);
						counter++;

					}

					System.out.println();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		}

		System.out.println("Assign Exercises terminated.");

	}

	public static String whatToDo() {
		Scanner sc = new Scanner(System.in);
		String answer = sc.nextLine();
		while (!(answer.equals("add")) && !(answer.equals("view")) && !(answer.equals("quit"))
				|| answer.contains(" ")) {
			System.out.println("Type 'add', 'view', or 'quit':");
			answer = sc.nextLine();
		}
		return answer;
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

	public static int enterExerId() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			if (sc.hasNextInt()) {
				int exerId = sc.nextInt();
				if (checkIfExerIdExists(exerId)) {
					return exerId;
				} else {
					System.out.println("Enter a valid exercise id:");
					sc.nextLine();
				}
			} else {
				System.out.println("Enter a valid exercise id:");
				sc.nextLine();
			}
		}
	}

	public static boolean checkIfExerIdExists(int exer) {
		ArrayList<Integer> exerIds = new ArrayList<>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT id FROM Exercise");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int exerId = rs.getInt("id");
				exerIds.add(exerId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (exerIds.contains(exer)) {
			return true;
		}
		return false;
	}

}
