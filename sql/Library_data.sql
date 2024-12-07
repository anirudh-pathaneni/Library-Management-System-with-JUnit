-- Insert Users
INSERT INTO User (user_name) VALUES 
('Alice'), ('Bob'), ('Charlie'), ('David');

-- Assign Users to Librarians and Students
INSERT INTO Librarian (librarian_id, librarian_password) VALUES 
(1, '1234');

INSERT INTO Student (student_id) VALUES 
(2), (3), (4);


INSERT INTO student(student_id, student_name)
VALUES
    ('1', 'David'),
    ('2', 'Alice'),
    ('3', 'Bob'),
    ('4', 'Eva'),
    ('5', 'Michael');



-- Insert Books
INSERT INTO Book (book_name, publication_year, num_copies, author_name) VALUES
('Introduction to Algorithms', 2009, 5, 'Thomas H. Cormen'),
('Clean Code', 2008, 3, 'Robert C. Martin'),
('Artificial Intelligence', 2020, 2, 'Stuart Russell');
    

