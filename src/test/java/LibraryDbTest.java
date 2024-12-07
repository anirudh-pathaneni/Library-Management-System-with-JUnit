import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Scanner;

public class LibraryDbTest {

    private Connection conn;
    private Statement stmt;

    @BeforeEach
    public void setUp() throws SQLException {

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load H2 driver", e);
        }
        // Set up the H2 in-memory database
        conn = DriverManager.getConnection("jdbc:h2:mem:librarydb;DB_CLOSE_DELAY=-1", "sa", "");
        stmt = conn.createStatement();
        Scanner scan = new Scanner(System.in);

        stmt.executeUpdate("DROP TABLE IF EXISTS Borrow");
        stmt.executeUpdate("DROP TABLE IF EXISTS Student");
        stmt.executeUpdate("DROP TABLE IF EXISTS Librarian");
        stmt.executeUpdate("DROP TABLE IF EXISTS Book");
        stmt.executeUpdate(""" 
                DROP TABLE IF EXISTS "User"
                """);

        // Creating the database schema
        String createSchemaSQL = """
                CREATE TABLE "User" (
                    user_id INT PRIMARY KEY AUTO_INCREMENT,
                    user_name VARCHAR(100) NOT NULL
                );

                CREATE TABLE Librarian (
                    librarian_id INT PRIMARY KEY,
                    librarian_password VARCHAR(100) NOT NULL,
                    FOREIGN KEY (librarian_id) REFERENCES "User"(user_id)
                );

                CREATE TABLE Student (
                    student_id INT PRIMARY KEY,
                    FOREIGN KEY (student_id) REFERENCES "User"(user_id)
                );

                CREATE TABLE Book (
                    book_id INT PRIMARY KEY AUTO_INCREMENT,
                    book_name VARCHAR(100) NOT NULL,
                    publication_year INT NOT NULL,
                    num_copies INT NOT NULL,
                    author_name VARCHAR(100) NOT NULL
                );

                CREATE TABLE Borrow (
                    borrower_id INT,
                    borrowed_book_id INT,
                    CONSTRAINT pk_borrower_book PRIMARY KEY (borrower_id, borrowed_book_id),
                    FOREIGN KEY (borrowed_book_id) REFERENCES Book(book_id) ON DELETE CASCADE,
                    FOREIGN KEY (borrower_id) REFERENCES Student(student_id) ON DELETE CASCADE
                );
                """;

        stmt.execute(createSchemaSQL);

        // Inserting sample data
        String insertDataSQL = """
                INSERT INTO "User" (user_name) VALUES ('Alice'), ('Bob'), ('Charlie'), ('David');

                INSERT INTO Librarian (librarian_id, librarian_password) VALUES (1, '1234');

                INSERT INTO Student (student_id) VALUES (2), (3), (4);

                INSERT INTO Book (book_name, publication_year, num_copies, author_name) VALUES
                    ('Introduction to Algorithms', 2009, 5, 'Thomas H. Cormen'),
                    ('Clean Code', 2008, 3, 'Robert C. Martin'),
                    ('Artificial Intelligence', 2020, 2, 'Stuart Russell');
                """;
        stmt.execute(insertDataSQL);
        System.out.println("Database initialized with tables and data.");
    }

    @Test
    @DisplayName("Authentication Valid")
    public void testAuthenticationCorrectLibrarian() {
        Scanner scan = new Scanner("1\n1234\n");

        boolean isAuthenticated = librarydb.authentication(stmt, scan, false);
        assertTrue(isAuthenticated, "Authentication should succeed for a valid librarian.");
    }

    @Test
    @DisplayName("Authentication Invalid")
    public void testAuthenticationIncorrectLibrarian() {
        Scanner scan = new Scanner("1\nwrong_password\n");

        boolean isAuthenticated = librarydb.authentication(stmt, scan, false);
        assertFalse(isAuthenticated, "Authentication should fail for an invalid librarian.");
    }

    @Test
    @DisplayName("Books List")
    public void testListOfBooks() {
        Scanner scan = new Scanner("");
        boolean noBooks = librarydb.list_of_books(stmt, scan, true);
        assertFalse(noBooks, "Books should be listed successfully.");
    }

    @Test
    @DisplayName("Issue Book")
    public void testIssueBook() throws SQLException {
        Scanner scan = new Scanner("1\n2\n"); // Mock input: book_id = 1, student_id = 2
        librarydb.issue_book(stmt, scan, conn);

        // Verify that the book's num_copies has decreased
        var rs = stmt.executeQuery("SELECT num_copies FROM Book WHERE book_id = 1");
        assertTrue(rs.next());
        assertEquals(4, rs.getInt("num_copies"), "The number of copies should decrease after issuing a book.");

        // Verify that a borrow record has been created
        rs = stmt.executeQuery("SELECT * FROM Borrow WHERE borrower_id = 2 AND borrowed_book_id = 1");
        assertTrue(rs.next(), "A borrow record should be created for the issued book.");
    }

    @Test
    @DisplayName("Return Book")
    public void testReturnBook() throws SQLException {
        Scanner scan = new Scanner("1\n2\n"); // Mock input: book_id = 1, student_id = 2

        // First, issue the book to set up the scenario
        stmt.executeUpdate("INSERT INTO Borrow (borrower_id, borrowed_book_id) VALUES (2, 1)");
        stmt.executeUpdate("UPDATE Book SET num_copies = 4 WHERE book_id = 1");

        // Test the return process
        librarydb.return_book(stmt, scan, conn);

        // Verify that the book's num_copies has increased
        var rs = stmt.executeQuery("SELECT num_copies FROM Book WHERE book_id = 1");
        assertTrue(rs.next());
        assertEquals(5, rs.getInt("num_copies"), "The number of copies should increase after returning a book.");

        // Verify that the borrow record has been deleted
        rs = stmt.executeQuery("SELECT * FROM Borrow WHERE borrower_id = 2 AND borrowed_book_id = 1");
        assertFalse(rs.next(), "The borrow record should be deleted after returning the book.");
    }

    @Test
    @DisplayName("Add Book")
    public void testAddBook() throws SQLException {
        Scanner scan = new Scanner("New Book\nNew Author\n2023\n10\n");
        librarydb.add_book(stmt, scan, conn);

        // Verify that the book is added to the database
        var rs = stmt.executeQuery("SELECT * FROM Book WHERE book_name = 'New Book'");
        assertTrue(rs.next(), "The new book should be added to the database.");
        assertEquals("New Author", rs.getString("author_name"), "The author name should match.");
        assertEquals(2023, rs.getInt("publication_year"), "The publication year should match.");
        assertEquals(10, rs.getInt("num_copies"), "The number of copies should match.");
    }

    @Test
    @DisplayName("Books Available")
    public void testListAvailableBooks() throws SQLException {
        stmt.executeUpdate("UPDATE Book SET num_copies = 0 WHERE book_id = 1"); // Mark a book as unavailable
        Scanner scan = new Scanner(""); // No user input
        boolean noBooks = librarydb.list_of_books(stmt, scan, true);
        assertFalse(noBooks, "Available books should be listed if copies exist.");
    }

    @Test
    @DisplayName("Books Unavailable")
    public void testNoAvailableBooks() throws SQLException {
        stmt.executeUpdate("UPDATE Book SET num_copies = 0"); // Make all books unavailable
        Scanner scan = new Scanner(""); // No user input
        boolean noBooks = librarydb.list_of_books(stmt, scan, true);
        assertTrue(noBooks, "There should be no books listed when all are unavailable.");
    }

    @Test
    @DisplayName("Delete Book")
    public void testDeleteBook() throws SQLException {
        Scanner scan = new Scanner("1"); // Delete book with book_id = 1
        librarydb.delete_book(stmt, scan, conn);

        ResultSet rs = stmt.executeQuery("SELECT * FROM Book WHERE book_id = 1");
        assertFalse(rs.next(), "The book should be deleted from the database.");
    }

    @Test
    @DisplayName("Delete Nonexistent")
    public void testDeleteNonExistingBook() throws SQLException {
        Scanner scan = new Scanner("999"); // Non-existent book ID
        librarydb.delete_book(stmt, scan, conn);

        // Ensure the number of books hasn't changed
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total_books FROM Book");
        rs.next();
        assertEquals(3, rs.getInt("total_books"), "No book should be deleted.");
    }

    @Test
    @DisplayName("Borrowed Books")
    public void testListBorrowedBooks() throws SQLException {
        stmt.executeUpdate("INSERT INTO Borrow (borrower_id, borrowed_book_id) VALUES (2, 1)"); // Borrow book_id = 1 for student_id = 2
        Scanner scan = new Scanner("");
        librarydb.list_of_borrowed_books(stmt, scan, 2);

        // Verify borrow details
        ResultSet rs = stmt.executeQuery("SELECT * FROM Borrow WHERE borrower_id = 2 AND borrowed_book_id = 1");
        assertTrue(rs.next(), "Borrowed book details should be listed.");
    }

    @Test
    @DisplayName("Student Borrowed")
    public void testBooksBorrowedByStudent() throws SQLException {
        stmt.executeUpdate("INSERT INTO Borrow (borrower_id, borrowed_book_id) VALUES (2, 2)"); // Borrow book_id = 2 for student_id = 2
        Scanner scan = new Scanner("2"); // Input student_id = 2
        librarydb.books_borrowed_by_students(stmt, scan);

        // Verify borrow details
        ResultSet rs = stmt.executeQuery("SELECT * FROM Borrow WHERE borrower_id = 2 AND borrowed_book_id = 2");
        assertTrue(rs.next(), "Borrowed book details should be displayed.");
    }

    @Test
    @DisplayName("Filter by Name")
    void testFilterBooksByName() {
        String baseSql = "SELECT * FROM books";
        Scanner scan = new Scanner("2\nHarry Potter\n");
        String expected = "SELECT * FROM books WHERE book_name LIKE '%Harry Potter%'";
        String result = librarydb.filterBooks(baseSql, scan);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Sort by Name")
    void testSortBooksByIdDescending() {
        String baseSql = "SELECT * FROM books";
        Scanner scan = new Scanner("1\n2\n");
        String expected = "SELECT * FROM books ORDER BY book_id DESC";
        String result = librarydb.sortBooks(baseSql, scan);
        assertEquals(expected, result);
    }


    @AfterEach
    public void tearDown() throws SQLException {
        // Close resources after each test
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
}