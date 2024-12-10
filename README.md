# Library Management System with JUnit Testing

This project is a console-based Library Management System implemented in Java, using a MySQL database for data storage and management. The system allows librarians and students to perform various actions such as adding, deleting, borrowing, and returning books, as well as viewing book and student records. Additionally, the project includes a suite of **JUnit 5** tests to validate core functionalities.

---

## Features

### For Librarians:
- View all books or only available books.
- Add new books to the database.
- Delete books from the library.
- Issue books to students.
- Accept returned books.
- View all students.
- View the list of books borrowed by a student.

### For Students:
- View the list of all available books.
- View the list of books they have borrowed.

---

## Prerequisites

1. **MySQL** installed and configured on your system.
2. **Java Development Kit (JDK)**
3. **IntelliJ** (preferred) or **Maven** installed and configured.
4. Files included in this project:
    - Java source files in `src/`.
    - SQL scripts in the `sql/` folder.
    - The `mysql-connector-j-8.3.0.jar` file for database connectivity.
    - Maven configuration (`pom.xml`) for managing dependencies and running JUnit tests.

---

### Project Structure

- `src/main/java/`: Contains Java source files.
- `src/test/java/`: Contains JUnit test cases.
- `sql/`: Contains SQL scripts to create, alter, and populate the database.
- `mysql-connector-j-8.3.0.jar`: Dependency for database connectivity.
- `pom.xml`: Maven configuration file for managing dependencies.


## Setup Instructions

### Running JUnit Tests

#### To run all tests from IntelliJ using Maven:

- Open the **Maven** toolbar (located on the right side of the IntelliJ window).
- In the toolbar, click on **Lifecycle**
- Click on **Test** or **Install** to run the tests.

#### To run all tests from Terminal

The project includes JUnit 5 test cases for validating core functionalities. To run the tests:

1. **Ensure that Maven is set up correctly and all dependencies are resolved.**
2. **From the terminal, run:**

   ```bash
   mvn test
   ```

### Running the Application

### Configuration

- Update the database connection details in `librarydb.java` with your MySQL credentials:

   ```java
   static final String DB_URL = "jdbc:mysql://localhost:3306/librarydb";
   static final String USER = "<your_mysql_username>";
   static final String PASS = "<your_mysql_password>";
    ```
### Database Setup

1. Open the MySQL terminal:
   ```bash
   mysql -u <your_username> -p
    ```
2. Execute the following SQL scripts in the provided order:
    ```sql
    SOURCE Library_create.sql;
    
    SOURCE Library_alter.sql;
    
    SOURCE Library_data.sql;
   ```

##### From the Terminal

1.	Navigate to the src/ directory containing the librarydb.java file and the mysql-connector-j-8.3.0.jar file.
2.  Set the CLASSPATH to include the current directory (.) and the mysql-connector-j-8.3.0.jar file:
    ``` bash
     export CLASSPATH='.:mysql-connector-j-8.3.0.jar'
    ```
3. Compile the application:
    ``` bash
    javac librarydb.java
    ```
4.	Run the application:
   ``` bash   
    java librarydb
   ```

#### Using IntelliJ IDEA

1. **Open the project in IntelliJ IDEA.**
2. **Add the mysql-connector-j-8.3.0.jar file to the project:**
    - Right-click on the project in the Project Explorer.
    - Select **Add Framework Support** or **Libraries** (depending on your IntelliJ version).
    - Add the **mysql-connector-j-8.3.0.jar** file from the directory.
3. **Ensure the src/ directory is marked as the Source Root:**
    - Right-click on the **src/** folder and select **Mark Directory as > Sources Root**.
4. **Run the `librarydb` file:**
    - Right-click on the file in the Project Explorer.
    - Select **Run ‘librarydb’**.

### Dependencies

The project relies on the following dependencies:
- **MySQL Connector/J** for database connectivity.
- **JUnit 5** for testing.