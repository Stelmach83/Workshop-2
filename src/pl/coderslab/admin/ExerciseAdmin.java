package pl.coderslab.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import pl.coderslab.ConnectionManager;
import pl.coderslab.models.Exercise;

public class ExerciseAdmin {

	private static Connection conn = ConnectionManager.getConnection();

	public static void main(String[] args) {

		String adminAction = "";

		Scanner sc = new Scanner(System.in);

		while (!"quit".equals(adminAction)) {

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
			System.out.println("Do you want to 'add', 'edit', 'delete', or 'quit' Exercise Admin: ");
			adminAction = whatToDo();

			if ("add".equals(adminAction)) {
				System.out.println("Enter new exercise title: ");
				String title = sc.nextLine();
				System.out.println("Enter new exercise description: ");
				String desc = sc.nextLine();
				Exercise newExer = new Exercise(title, desc);
				try {
					newExer.saveToDB(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if ("edit".equals(adminAction)) {
				System.out.println("Enter exercise id to edit:");
				int exer_id = enterExerId();
				System.out.println("Enter new exercise title: ");
				String title = sc.nextLine();
				System.out.println("Enter new exercise description: ");
				String desc = sc.nextLine();
				try {
					Exercise editExer = Exercise.loadExerciseById(conn, exer_id);
					editExer.setTitle(title);
					editExer.setDescription(desc);
					editExer.saveToDB(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			if ("delete".equals(adminAction)) {
				System.out.println("Enter exercise id to delete:");
				int exer_id = enterExerId();
				try {
					Exercise delExer = Exercise.loadExerciseById(conn, exer_id);
					delExer.delete(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		System.out.println("Exercise Admin terminated.");

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
