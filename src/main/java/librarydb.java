import java.sql.*;
import java.util.Scanner;

public class librarydb {

   // JDBC constants
   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://localhost:3306/librarydb";
   static final String USER = "root";
   static final String PASS = "admin@123";

   public static void main(String[] args) {
      System.out.println("\nHello\n");

      Connection conn = null;
      Statement stmt = null;

      try {
         // Load the JDBC driver
         Class.forName(JDBC_DRIVER);

         // Establish a connection to the database
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         stmt = conn.createStatement();
         Scanner scan = new Scanner(System.in); // Create a Scanner object
         conn.setAutoCommit(false);
         clearScreen();
         System.out.println("\nWELCOME TO LIBRARY MANAGEMENT SERVICE\n");
         main_menu(stmt, scan, conn);

         scan.close();
         stmt.close();
         conn.close();
      } catch (SQLException se) {
         se.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (stmt != null)
               stmt.close();
         } catch (SQLException se2) {
         }
         try {
            if (conn != null)
               conn.close();
         } catch (SQLException se) {
            se.printStackTrace();
         }
      }
   }

   static void main_menu(Statement stmt, Scanner scan, Connection conn) {
      System.out.println("Login as a- ");
      System.out.println("1. Student");
      System.out.println("2. Librarian");

      System.out.println("0. Exit");

      System.out.print("\n\nENTER YOUR CHOICE : ");
      int choice = Integer.parseInt(scan.nextLine());
      clearScreen();

      switch (choice) {
         case 0:
            System.out.println("\nThank you for using this System!!\n\n");
            System.exit(0);
         case 1:
            check_student(stmt, scan);
            break;
         case 2:
            check_librarian(stmt, scan, conn);
            break;

         default:
            clearScreen();
            System.out.println("Please Enter a Valid Choice!!\n");
            break;
      }
      main_menu(stmt, scan, conn);
   }

   static void student_menu(Statement stmt, Scanner scan,Integer student_id) {

      

      System.out.println("Please select appropriate option-");
      System.out.println("1. List of available books");
      System.out.println("2. List of borrowed books");
      System.out.println("0. Exit");

      System.out.print("\n\nENTER YOUR CHOICE : ");
      int choice = Integer.parseInt(scan.nextLine());
      clearScreen();

      switch (choice) {
         case 0:
            return;
         case 1:
            list_of_books(stmt, scan, true);
            break;
         case 2:
            list_of_borrowed_books(stmt, scan,student_id);
            break;   
         default:
            clearScreen();
            System.out.println("Please Enter a Valid Choice!!\n");
            break;
      }
      student_menu(stmt, scan,student_id);
   }

   static boolean authentication(Statement stmt, Scanner scan, boolean isSuperAdmin) {
      System.out.print("Enter your ID: ");
      Integer id = Integer.parseInt(scan.nextLine());
      System.out.print("Enter your password: ");
      String password = scan.nextLine();

      clearScreen();
      boolean authenticated = false;

      if (isSuperAdmin) {
         String sql = "SELECT * from super_admin";
         ResultSet rs = executeSqlStmt(stmt, sql);

         try {
            while (rs.next()) {
               String possible_id = rs.getString("super_admin_id");
               String possible_password = rs.getString("super_admin_password");

               if (possible_id.equals(id) && password.equals(possible_password)) {
                  authenticated = true;
                  break;
               }
            }
         } catch (SQLException se) {
         }
      } else {
         String sql = String.format("SELECT * from Librarian where librarian_id='%d'",id);
         ResultSet rs = executeSqlStmt(stmt, sql);

         try {
            while (rs.next()) {
               Integer possible_id = rs.getInt("librarian_id");
               String possible_password = rs.getString("librarian_password");

               if (possible_id==(id) && password.equals(possible_password)) {
                  authenticated = true;
                  break;
               }
            }
         } catch (SQLException se) {
         }
      }

      return authenticated;
   }

   static void check_librarian(Statement stmt, Scanner scan, Connection conn) {
      if (authentication(stmt, scan, false)) {
         librarian_menu(stmt, scan, conn);
      } else {
         System.out.print("Entered details were incorrect. Do you want to try again (Y/N)? ");
         String input = scan.nextLine();
         if (input.equals("Y"))
            check_librarian(stmt, scan, conn);
         else
            return;
      }
   }

   static void check_student(Statement stmt, Scanner scan) {
      System.out.print("Enter your ID: ");
      Integer s_id = Integer.parseInt(scan.nextLine());
      String sql = String.format("SELECT * from Student where student_id='%d'",s_id);
      ResultSet rs = executeSqlStmt(stmt, sql);

         try {
             if(rs.next()) {
               Integer student_id=rs.getInt("student_id");
               student_menu(stmt, scan,student_id);
            }
            else{
               System.out.println("Incorrect Student ID");
            }
         } catch (SQLException se) {
         }
   }


   static void librarian_menu(Statement stmt, Scanner scan, Connection conn) {
      System.out.println("Please select appropriate option-");
      System.out.println("1. List of all books");
      System.out.println("2. List of available books");
      System.out.println("3. Issue a book");
      System.out.println("4. Return a book");
      System.out.println("5. Add a book");
      System.out.println("6. Delete a book");
      System.out.println("7. List of students");
      System.out.println("8. Find borrowed books of students");
      ;
      System.out.println("0. Exit");

      System.out.print("\n\nENTER YOUR CHOICE : ");
      int choice = Integer.parseInt(scan.nextLine());
      clearScreen();

      switch (choice) {
         case 0:
            return;
         case 1:
            list_of_books(stmt, scan, false);
            break;
         case 2:
            list_of_books(stmt, scan, true);
            break;
         case 3:
            issue_book(stmt, scan, conn);
            break;
         case 4:
            return_book(stmt, scan, conn);
            break;
         case 5:
            add_book(stmt, scan, conn);
            break;
         case 6:
            delete_book(stmt, scan, conn);
            break;
         case 7:
            list_of_students(stmt, scan);
            break;
         case 8:
            books_borrowed_by_students(stmt, scan);
            break;
         default:
            clearScreen();
            System.out.println("Please Enter a Valid Choice!!\n");
            break;
      }
      librarian_menu(stmt, scan, conn);
   }

   static boolean list_of_books(Statement stmt, Scanner scan, boolean checkAvailable) {
      String baseSql = "SELECT book_id, book_name, author_name, publication_year, num_copies FROM Book";
      if (checkAvailable) {
         baseSql += " WHERE num_copies > 0";
      }
      boolean noBooks = displayBooks(stmt, baseSql);

      if (!noBooks & !checkAvailable) {
         while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Filter the search results");
            System.out.println("2. Sort the search results");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scan.nextInt();
            scan.nextLine(); // Clear newline character

            switch (choice) {
               case 1:
                  baseSql = filterBooks(baseSql, scan);
                  displayBooks(stmt, baseSql);
                  break;
               case 2:
                  baseSql = sortBooks(baseSql, scan);
                  displayBooks(stmt, baseSql);
                  break;
               case 3:
                  return false;
               default:
                  System.out.println("Invalid choice. Please try again.");
            }
         }
      }
      return noBooks;
   }

   static boolean displayBooks(Statement stmt, String sql) {
      // System.out.println(sql);
      boolean noBooks = true;
      try {
         ResultSet rs = stmt.executeQuery(sql);
         System.out.println("\nList of books:\n");
         while (rs.next()) {
            int id = rs.getInt("book_id");
            String name = rs.getString("book_name");
            String author = rs.getString("author_name");
            int year = rs.getInt("publication_year");
            int numCopies = rs.getInt("num_copies");

            System.out.println("Book ID: " + id);
            System.out.println("Book Name: " + name);
            System.out.println("Author: " + author);
            System.out.println("Publication Year: " + year);
            System.out.println("Available Copies: " + numCopies);
            System.out.println("");
            noBooks = false;
         }
         if (noBooks) {
            System.out.println("Sorry, no books are available!");
         }
         rs.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return noBooks;
   }

   static String filterBooks(String baseSql, Scanner scan) {
      System.out.println("\nFilter Options:");
      System.out.println("1. By Book ID");
      System.out.println("2. By Name");
      System.out.println("3. By Author");
      System.out.println("4. By Publication Year");
      System.out.println("5. By Availability");
      System.out.print("Enter your filter choice: ");
      int filterChoice = scan.nextInt();
      scan.nextLine(); // Clear newline character

      String condition = "";
      switch (filterChoice) {
         case 1:
            System.out.print("Enter ID to filter by: ");
            int id = scan.nextInt();
            scan.nextLine();
            condition = "book_id = " + id;
            break;
         case 2:
            System.out.print("Enter name to filter by: ");
            String name = scan.nextLine();
            condition = "book_name LIKE '%" + name + "%'";
            break;
         case 3:
            System.out.print("Enter author to filter by: ");
            String author = scan.nextLine();
            condition = "author_name LIKE '%" + author + "%'";
            break;
         case 4:
            System.out.print("Enter publication year to filter by: ");
            int year = scan.nextInt();
            scan.nextLine();
            condition = "publication_year = " + year;
            break;
         case 5:
            condition = "num_copies > 0";
            break;
         default:
            System.out.println("Invalid filter choice. No filter applied.");
            return baseSql;
      }

      // Add the condition to baseSql
      if (baseSql.contains("WHERE")) {
         baseSql += " AND " + condition;
      } else {
         baseSql += " WHERE " + condition;
      }

      return baseSql;
   }

   static String sortBooks(String baseSql, Scanner scan) {
      System.out.println("\nSort Options:");
      System.out.println("1. By ID");
      System.out.println("2. By Name");
      System.out.println("3. By Author");
      System.out.println("4. By Publication Year");
      System.out.println("5. By Number of Copies");
      System.out.print("Enter your sort choice: ");
      int sortChoice = scan.nextInt();
      scan.nextLine(); // Clear newline character

      String orderBy = "";
      switch (sortChoice) {
         case 1:
            orderBy = "book_id";
            break;
         case 2:
            orderBy = "book_name";
            break;
         case 3:
            orderBy = "author_name";
            break;
         case 4:
            orderBy = "publication_year";
            break;
         case 5:
            orderBy = "num_copies";
            break;
         default:
            System.out.println("Invalid sort choice. No sorting applied.");
      }

      if (!orderBy.isEmpty()) {
         System.out.print("Sort in (1) Ascending or (2) Descending order? ");
         int sortOrder = scan.nextInt();
         scan.nextLine();
         String direction = sortOrder == 2 ? "DESC" : "ASC";
         baseSql += " ORDER BY " + orderBy + " " + direction;
      }

      return baseSql;
   }

   static void issue_book(Statement stmt, Scanner scan, Connection conn) {
      try {
         boolean noBooks = list_of_books(stmt, scan, true);
         if (!noBooks) {
            System.out.print("\nEnter book ID : ");
            Integer b_id = Integer.parseInt(scan.nextLine());
            String sql5 = String.format("SELECT num_copies FROM Book WHERE book_id = '%d'", b_id);
            ResultSet rs5 = executeSqlStmt(stmt, sql5);
            // Move cursor to the first row
            if (rs5.next()) {
               int numCopies = rs5.getInt("num_copies");
               if (numCopies > 0) {
                  System.out.print("Enter student id : ");
                  Integer s_id = Integer.parseInt(scan.nextLine());
                  clearScreen();

                  String sql1 = String.format("UPDATE Book SET num_copies = '%d' WHERE book_id = '%d'", numCopies - 1,
                        b_id);
                  int result = updateSqlStmt(stmt, sql1, conn);
                  String sql2 = String.format("INSERT INTO Borrow VALUES('%d', '%d');", s_id, b_id);
                  int result2 = updateSqlStmt(stmt, sql2, conn);

                  if (result != 0 && result2 != 0) {
                     System.out.println("Book has been issued successfully!!\n");
                     conn.commit();
                  } else {
                     System.out.println("Something went wrong!");
                     conn.rollback();
                     System.out.println("Update unsuccessful Rolling back the changes ...");
                  }
               } else {
                  System.out.println("Book is currently being borrowed by someone else");
               }
            } else {
               System.out.println("There is no such book");
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   static void return_book(Statement stmt, Scanner scan, Connection conn) {
      try {
         System.out.print("\nEnter book ID : ");
         Integer b_id = Integer.parseInt(scan.nextLine());
         System.out.print("\nEnter student ID : ");
         Integer s_id = Integer.parseInt(scan.nextLine());
         clearScreen();
         String sql5 = String.format("SELECT num_copies FROM Book WHERE book_id = '%d'", b_id);

         ResultSet rs5 = executeSqlStmt(stmt, sql5);
         // Move cursor to the first row
         if (rs5.next()) {
            int numCopies = rs5.getInt("num_copies");
            String sql = String.format("UPDATE Book SET num_copies = '%d' WHERE book_id = '%d'", numCopies+1,b_id);
            int result = updateSqlStmt(stmt, sql, conn);
            String sql2 = String.format(
                  "DELETE FROM Borrow WHERE borrowed_book_id = '%d' and borrower_id='%d'", b_id, s_id);
            int result2 = updateSqlStmt(stmt, sql2, conn);

            if (result != 0 && result2 != 0) {
               System.out.println("Book has been returned!!\n");
               conn.commit();
            } else {
               System.out.println("Something went wrong!\n");
               conn.rollback();
               System.out.println("Update unsuccessful Rolling back the changes ...");
            }

         } else {
            System.out.println("There is no such book");
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   static void add_book(Statement stmt, Scanner scan, Connection conn) {
      try {
         System.out.print("\nEnter book name : ");
         String name = scan.nextLine();
         System.out.print("\nEnter book author : ");
         String author = scan.nextLine();
         System.out.print("\nEnter book publication year : ");
         Integer year = Integer.parseInt(scan.nextLine());
         System.out.print("\nEnter Number of book copies : ");
         Integer num_copies = Integer.parseInt(scan.nextLine());

         String sql = String.format(
                 "INSERT INTO Book (book_name, author_name, publication_year, num_copies) VALUES ('%s', '%s', '%d', '%d')",
                 name, author, year, num_copies);

         int result = updateSqlStmt(stmt, sql, conn);
         if (result != 0) {
            System.out.println("Book has been added successfully!\n");
            conn.commit();
         } else {
            System.out.println("Error adding the book.");
            conn.rollback();
         }
      } catch (SQLException se) {
         se.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   static void delete_book(Statement stmt, Scanner scan, Connection conn) {
      try {
         System.out.print("\nEnter book ID: ");
         String id = scan.nextLine();

         clearScreen();

         // SQL to delete a book by ID
         String sql = String.format("DELETE FROM Book WHERE book_id = %s;", id);
         int result = updateSqlStmt(stmt, sql, conn);

         if (result != 0) {
            System.out.println("Book with ID " + id + " has been successfully deleted!\n");
            conn.commit();
         } else {
            System.out.println("No book found with ID " + id + ". Please try again.\n");
            conn.rollback();
         }
      } catch (Exception e) {
         try {
            System.out.println("An error occurred! Rolling back changes...");
            conn.rollback();
         } catch (SQLException rollbackEx) {
            System.out.println("Failed to rollback changes.");
            rollbackEx.printStackTrace();
         }
         e.printStackTrace();
      }
   }

   static void list_of_students(Statement stmt, Scanner scan) {
      String sql = "select student_id,user_name from Student,User where student_id=user_id";
      ResultSet rs = executeSqlStmt(stmt, sql);
      try {
         System.out.println("List of students:\n");
         while (rs.next()) {
            String id = rs.getString("student_id");
            String name = rs.getString("user_name");

            System.out.println("Student ID : " + id);
            System.out.println("Student Name: " + name);
            System.out.println("\n");
         }

         rs.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   static void list_of_borrowed_books(Statement stmt, Scanner scan, Integer student_id){
      String sql = String.format(
            "select book_id , book_name, author_name ,publication_year from Book,Borrow where '%s'=borrower_id and borrowed_book_id=book_id",
            student_id);
      ResultSet rs = executeSqlStmt(stmt, sql);
      boolean noBooks = true;
      try {
         System.out.println("Books borrowed by student with Student Id: " + student_id + " are:- ");
         while (rs.next()) {
            System.out.println("");
            String id = rs.getString("book_id");
            String name = rs.getString("book_name");
            String author = rs.getString("author_name");
            Integer year = rs.getInt("publication_year");

            System.out.println("Book ID : " + id);
            System.out.println("Book Name: " + name);
            System.out.println("Author : " + author);
            System.out.println("Publication year : " + year);

            noBooks = false;
         }
         if (noBooks == true) {
            System.out.println("No books are borrowed by student with Student ID: " + student_id);
         }
         System.out.println("");

         rs.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   static void books_borrowed_by_students(Statement stmt, Scanner scan) {
      System.out.print("\nEnter the Student ID : ");
      Integer student_id = Integer.parseInt(scan.nextLine());
      clearScreen();
      String sql = String.format(
            "select book_id , book_name, author_name ,publication_year from Book,Borrow where '%s'=borrower_id and borrowed_book_id=book_id",
            student_id);
      ResultSet rs = executeSqlStmt(stmt, sql);
      boolean noBooks = true;
      try {
         System.out.println("Books borrowed by student with Student Id:" + student_id + " are :");
         while (rs.next()) {
            System.out.println("");
            String id = rs.getString("book_id");
            String name = rs.getString("book_name");
            String author = rs.getString("author_name");
            Integer year = rs.getInt("publication_year");

            System.out.println("Book ID : " + id);
            System.out.println("Book Name: " + name);
            System.out.println("Author : " + author);
            System.out.println("Publication year : " + year);

            noBooks = false;
         }
         if (noBooks == true) {
            System.out.println("NO books are borrowed by student with Student ID: " + student_id);
         }
         System.out.println("");

         rs.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   static ResultSet executeSqlStmt(Statement stmt, String sql) {
      try {
         ResultSet rs = stmt.executeQuery(sql);
         return rs;
      } catch (SQLException se) {
         se.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   static int updateSqlStmt(Statement stmt, String sql, Connection conn) {
      try {
         int rs = stmt.executeUpdate(sql);
         if (rs != 0) {
            return rs;
         } else {
            System.out.println("Something went wrong!");
            conn.rollback();
            System.out.println("Update unsuccessful Rolling back the changes ...");
            return rs;
         }

      } catch (SQLException se) {
         // se.printStackTrace();
         try {
            if (conn != null)
               conn.rollback(); // Rollback if SQLException occurs
         } catch (SQLException se2) {
            se2.printStackTrace();
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
      return 0;
   }

   static void clearScreen() {
      System.out.println("\033[H\033[J");
      System.out.flush();
   }
}
