package pl.coderslab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import pl.coderslab.models.Solution;

public class UserApp {

	private static Connection conn = ConnectionManager.getConnection();

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		String adminAction = "";

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT Users.id, username FROM Users ORDER BY Users.id");
			ResultSet rs = ps.executeQuery();
			System.out.println("Select user (type their id): \n");
			while (rs.next()) {
				int id = rs.getInt("id");
				String username = rs.getString("username");
				System.out.println(username + ", id=" + id);
			}
			System.out.println();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		int user_id = enterUserId();
		while (!"quit".equals(adminAction)) {

			System.out.println("Do you want to 'add', 'view', or 'quit' User App: ");
			adminAction = whatToDo();

			if ("add".equals(adminAction)) {
				ArrayList<Integer> solutionIdsToComplete = new ArrayList<>();
				try {
					PreparedStatement ps1 = conn
							.prepareStatement("SELECT * FROM Solution WHERE description='' and users_id=?");
					ps1.setInt(1, user_id);
					ResultSet rs1 = ps1.executeQuery();
					if (!rs1.next()) {
						System.out.println("You don't have any exercises to complete.");
					} else {
						PreparedStatement ps2 = conn.prepareStatement(
								"SELECT id, exercise_id, description FROM Solution WHERE description='' and users_id=?");
						ps2.setInt(1, user_id);
						ResultSet rs2 = ps2.executeQuery();
						int counter = 1;
						while (rs2.next()) {
							int sol_id = rs2.getInt("id");
							int exer_id = rs2.getInt("exercise_id");
							// String desc = rs2.getString("description");
							System.out.println(counter + ". Exercise=" + exer_id + " (id." + sol_id + ")");
							solutionIdsToComplete.add(sol_id);
							counter++;
						}
						System.out.println("Enter solution id which you wish to complete:");
						int sol_id = enterSolId(solutionIdsToComplete);
						System.out.println("Enter description of solution which you wish to complete:");
						String desc = sc.nextLine();
						Solution updSolution = Solution.loadSolutionById(conn, sol_id);
						updSolution.setDescription(desc);
						updSolution.saveToDB(conn);
						System.out.println("Solution update saved to DB.");

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				System.out.println();

			}
			if ("view".equals(adminAction)) {

				try {
					PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM Solution WHERE users_id=?");
					ps1.setInt(1, user_id);
					ResultSet rs1 = ps1.executeQuery();
					System.out.println("List of solutions for user:");
					if (!rs1.next()) {
						System.out.println("You don't have any solutions.");
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
		System.out.println("User App terminated.");

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

	public static int enterSolId(ArrayList<Integer> solutionIdsToComplete) {
		Scanner sc = new Scanner(System.in);
		while (true) {
			if (sc.hasNextInt()) {
				int solId = sc.nextInt();
				if (solutionIdsToComplete.contains(solId)) {
					return solId;
				} else {
					System.out.println("Enter a valid solution id:");
					sc.nextLine();
				}
			} else {
				System.out.println("Enter a valid solution id:");
				sc.nextLine();
			}
		}
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

}
