CREATE DATABASE librarydb;
USE librarydb;



CREATE TABLE User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(100) NOT NULL
);

CREATE TABLE Librarian (
    librarian_id INT PRIMARY KEY,
    librarian_password VARCHAR(100) NOT NULL,
    FOREIGN KEY (librarian_id) REFERENCES User(user_id)
);

CREATE TABLE Student (
    student_id INT PRIMARY KEY,
    FOREIGN KEY (student_id) REFERENCES User(user_id)
);

CREATE TABLE Book (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    book_name VARCHAR(100) NOT NULL,
    publication_year INT NOT NULL,
    num_copies INT NOT NULL,
    author_name VARCHAR(100) NOT NULL
);



CREATE TABLE Borrow(
    borrower_id INT ,
    borrowed_book_id  INT,
    CONSTRAINT pk_borrower_book PRIMARY KEY (borrower_id,borrowed_book_id) 
);




