ALTER TABLE Borrow
    ADD CONSTRAINT fk_borrow_book_id FOREIGN KEY (borrowed_book_id) REFERENCES Book(book_id) on delete cascade,
    ADD CONSTRAINT fk_borrow_student_id FOREIGN KEY (borrower_id) REFERENCES Student(student_id) on delete cascade;


