CREATE TABLE constraints_config (
 constraints_config_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 rule_id INT NOT NULL,
 constraint_value INT NOT NULL,
 constraint_description VARCHAR(200) NOT NULL,
 start_time TIMESTAMP(6) NOT NULL,
 end_time TIMESTAMP(6) NOT NULL
);

ALTER TABLE constraints_config ADD CONSTRAINT PK_constraints_config PRIMARY KEY (constraints_config_id);


CREATE TABLE instrument_details (
 instrument_details_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 brand VARCHAR(100) NOT NULL,
 price INT NOT NULL
);

ALTER TABLE instrument_details ADD CONSTRAINT PK_instrument_details PRIMARY KEY (instrument_details_id);


CREATE TABLE person (
 person_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 name VARCHAR(100) NOT NULL,
 person_number VARCHAR(12) NOT NULL
);

ALTER TABLE person ADD CONSTRAINT PK_person PRIMARY KEY (person_id);


CREATE TABLE phone_number (
 phone_number VARCHAR(100) NOT NULL,
 person_id INT NOT NULL
);

ALTER TABLE phone_number ADD CONSTRAINT PK_phone_number PRIMARY KEY (phone_number,person_id);

CREATE TYPE skill AS ENUM ('Beginner', 'Intermediate', 'Advanced');

CREATE TABLE price_schema (
 price_schema_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 price INT NOT NULL,
 skill_level skill NOT NULL,
 discount FLOAT(50) NOT NULL,
 instructor_payment INT NOT NULL,
 initial_time TIMESTAMP(6) NOT NULL
);

ALTER TABLE price_schema ADD CONSTRAINT PK_price_schema PRIMARY KEY (price_schema_id);


CREATE TABLE address (
 address_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 city VARCHAR(100) NOT NULL,
 zip VARCHAR(100) NOT NULL,
 street VARCHAR(100) NOT NULL,
 person_id INT NOT NULL
);

ALTER TABLE address ADD CONSTRAINT PK_address PRIMARY KEY (address_id);


CREATE TABLE contact_person (
 contact_person_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 person_id INT NOT NULL
);

ALTER TABLE contact_person ADD CONSTRAINT PK_contact_person PRIMARY KEY (contact_person_id);


CREATE TABLE email (
 email VARCHAR(100) NOT NULL,
 person_id INT NOT NULL
);

ALTER TABLE email ADD CONSTRAINT PK_email PRIMARY KEY (email,person_id);


CREATE TABLE instructor (
 instructor_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 person_id INT NOT NULL
);

ALTER TABLE instructor ADD CONSTRAINT PK_instructor PRIMARY KEY (instructor_id);


CREATE TABLE instructor_availability (
 instructor_id INT NOT NULL,
 date TIMESTAMP(6) NOT NULL,
 duration VARCHAR(100) NOT NULL
);

ALTER TABLE instructor_availability ADD CONSTRAINT PK_instructor_availability PRIMARY KEY (instructor_id);


CREATE TABLE lesson (
 lesson_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 instructor_id INT NOT NULL,
 start_time TIMESTAMP(6) NOT NULL,
 end_time TIMESTAMP(6) NOT NULL,
 place VARCHAR(100) NOT NULL,
 price_schema_id INT NOT NULL
);

ALTER TABLE lesson ADD CONSTRAINT PK_lesson PRIMARY KEY (lesson_id);


CREATE TABLE student (
 student_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 person_id INT NOT NULL,
 contact_person_id INT
);

ALTER TABLE student ADD CONSTRAINT PK_student PRIMARY KEY (student_id);


CREATE TABLE student_lesson (
 lesson_id INT NOT NULL,
 student_id INT NOT NULL
);

ALTER TABLE student_lesson ADD CONSTRAINT PK_student_lesson PRIMARY KEY (lesson_id,student_id);


CREATE TABLE student_sibling (
 student_id_1 INT NOT NULL,
 student_id_0 INT NOT NULL
);

ALTER TABLE student_sibling ADD CONSTRAINT PK_student_sibling PRIMARY KEY (student_id_1,student_id_0);


CREATE TYPE genres AS ENUM ('Hip hop', 'Jazz', 'Classical', 'Blues', 'Rock', 'Punk rock', 'Pop');

CREATE TABLE ensemble_lesson (
 lesson_id INT NOT NULL,
 genre genres NOT NULL,
 minimum_of_students VARCHAR(100) NOT NULL,
 maximum_of_students VARCHAR(100) NOT NULL
);

ALTER TABLE ensemble_lesson ADD CONSTRAINT PK_ensemble_lesson PRIMARY KEY (lesson_id);


CREATE TABLE present_skill (
 present_skill_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 student_id INT NOT NULL,
 skill_level skill NOT NULL
);

ALTER TABLE present_skill ADD CONSTRAINT PK_present_skill PRIMARY KEY (present_skill_id);


CREATE TABLE instrument_type (
 instrument_type_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 name VARCHAR(100) NOT NULL,
 present_skill_id INT
);

ALTER TABLE instrument_type ADD CONSTRAINT PK_instrument_type PRIMARY KEY (instrument_type_id);


CREATE TABLE instruments (
 instrument_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 instrument_type_id INT NOT NULL,
 instrument_details_id INT NOT NULL
);

ALTER TABLE instruments ADD CONSTRAINT PK_instruments PRIMARY KEY (instrument_id,instrument_type_id,instrument_details_id);


CREATE TABLE rented_instrument (
 rented_instrument_id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
 rental_start_time TIMESTAMP(6) NOT NULL,
 rental_end_time TIMESTAMP(6),
 instrument_id INT NOT NULL,
 student_id INT NOT NULL,
 instrument_type_id INT,
 instrument_details_id INT,
 constraints_config_id INT
);

ALTER TABLE rented_instrument ADD CONSTRAINT PK_rented_instrument PRIMARY KEY (rented_instrument_id);


CREATE TABLE group_lesson (
 lesson_id INT NOT NULL,
 skill_level skill NOT NULL,
 minimum_of_students VARCHAR(100) NOT NULL,
 maximum_of_students VARCHAR(100) NOT NULL,
 instrument_type_id INT NOT NULL
);

ALTER TABLE group_lesson ADD CONSTRAINT PK_group_lesson PRIMARY KEY (lesson_id);


CREATE TABLE individual_lesson (
 lesson_id INT NOT NULL,
 skill_level skill NOT NULL,
 instrument_type_id INT NOT NULL
);

ALTER TABLE individual_lesson ADD CONSTRAINT PK_individual_lesson PRIMARY KEY (lesson_id);


CREATE TABLE instructor_instrument_type (
 instructor_id INT NOT NULL,
 instrument_type_id INT NOT NULL
);

ALTER TABLE instructor_instrument_type ADD CONSTRAINT PK_instructor_instrument_type PRIMARY KEY (instructor_id,instrument_type_id);


ALTER TABLE phone_number ADD CONSTRAINT FK_phone_number_0 FOREIGN KEY (person_id) REFERENCES person (person_id);


ALTER TABLE address ADD CONSTRAINT FK_address_0 FOREIGN KEY (person_id) REFERENCES person (person_id);


ALTER TABLE contact_person ADD CONSTRAINT FK_contact_person_0 FOREIGN KEY (person_id) REFERENCES person (person_id);


ALTER TABLE email ADD CONSTRAINT FK_email_0 FOREIGN KEY (person_id) REFERENCES person (person_id);


ALTER TABLE instructor ADD CONSTRAINT FK_instructor_0 FOREIGN KEY (person_id) REFERENCES person (person_id);


ALTER TABLE instructor_availability ADD CONSTRAINT FK_instructor_availability_0 FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id);


ALTER TABLE lesson ADD CONSTRAINT FK_lesson_0 FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id);
ALTER TABLE lesson ADD CONSTRAINT FK_lesson_1 FOREIGN KEY (price_schema_id) REFERENCES price_schema (price_schema_id);


ALTER TABLE student ADD CONSTRAINT FK_student_0 FOREIGN KEY (person_id) REFERENCES person (person_id);
ALTER TABLE student ADD CONSTRAINT FK_student_1 FOREIGN KEY (contact_person_id) REFERENCES contact_person (contact_person_id);


ALTER TABLE student_lesson ADD CONSTRAINT FK_student_lesson_0 FOREIGN KEY (lesson_id) REFERENCES lesson (lesson_id);
ALTER TABLE student_lesson ADD CONSTRAINT FK_student_lesson_1 FOREIGN KEY (student_id) REFERENCES student (student_id);


ALTER TABLE student_sibling ADD CONSTRAINT FK_student_sibling_0 FOREIGN KEY (student_id_1) REFERENCES student (student_id);
ALTER TABLE student_sibling ADD CONSTRAINT FK_student_sibling_1 FOREIGN KEY (student_id_0) REFERENCES student (student_id);


ALTER TABLE ensemble_lesson ADD CONSTRAINT FK_ensemble_lesson_0 FOREIGN KEY (lesson_id) REFERENCES lesson (lesson_id);


ALTER TABLE present_skill ADD CONSTRAINT FK_present_skill_0 FOREIGN KEY (student_id) REFERENCES student (student_id);


ALTER TABLE instrument_type ADD CONSTRAINT FK_instrument_type_0 FOREIGN KEY (present_skill_id) REFERENCES present_skill (present_skill_id);


ALTER TABLE instruments ADD CONSTRAINT FK_instruments_0 FOREIGN KEY (instrument_type_id) REFERENCES instrument_type (instrument_type_id);
ALTER TABLE instruments ADD CONSTRAINT FK_instruments_1 FOREIGN KEY (instrument_details_id) REFERENCES instrument_details (instrument_details_id);


ALTER TABLE rented_instrument ADD CONSTRAINT FK_rented_instrument_0 FOREIGN KEY (instrument_id,instrument_type_id,instrument_details_id) REFERENCES instruments (instrument_id,instrument_type_id,instrument_details_id);
ALTER TABLE rented_instrument ADD CONSTRAINT FK_rented_instrument_1 FOREIGN KEY (student_id) REFERENCES student (student_id);
ALTER TABLE rented_instrument ADD CONSTRAINT FK_rented_instrument_2 FOREIGN KEY (constraints_config_id) REFERENCES constraints_config (constraints_config_id);


ALTER TABLE group_lesson ADD CONSTRAINT FK_group_lesson_0 FOREIGN KEY (lesson_id) REFERENCES lesson (lesson_id);
ALTER TABLE group_lesson ADD CONSTRAINT FK_group_lesson_1 FOREIGN KEY (instrument_type_id) REFERENCES instrument_type (instrument_type_id);


ALTER TABLE individual_lesson ADD CONSTRAINT FK_individual_lesson_0 FOREIGN KEY (lesson_id) REFERENCES lesson (lesson_id);
ALTER TABLE individual_lesson ADD CONSTRAINT FK_individual_lesson_1 FOREIGN KEY (instrument_type_id) REFERENCES instrument_type (instrument_type_id);


ALTER TABLE instructor_instrument_type ADD CONSTRAINT FK_instructor_instrument_type_0 FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id);
ALTER TABLE instructor_instrument_type ADD CONSTRAINT FK_instructor_instrument_type_1 FOREIGN KEY (instrument_type_id) REFERENCES instrument_type (instrument_type_id);













INSERT INTO person (name, person_number)
VALUES
  ('Oliver Ellis', 199403151234),
  ('Cassandra Levy', 199404151234),
  ('Cade Levy', 199203151324),
  ('Chase Britt', 200403151234),
  ('Kirby Mcpherson', 199403151234);


INSERT INTO address (street,city,zip, person_id)
VALUES
  ('Ap #335-700 Sed Road','Borlänge','38477', 1),
  ('153-2547 Imperdiet, Av.','Kovel','21209', 2),
  ('415-1987 Ac Rd.','Ceuta','K1 2WD', 3),
  ('P.O. Box 412, 7917 Gravida Road','Lochgilphead','981840', 4),
  ('Ap #540-9335 In St.','Pocatello','447875', 5);

INSERT INTO phone_number (phone_number, person_id)
VALUES
  ('1-761-738-7608', 1),
  ('1-823-878-5272', 2),
  ('1-445-543-2492', 3),
  ('1-932-346-8165', 4),
  ('(579) 141-3366', 5);

INSERT INTO email (email, person_id)
VALUES
  ('ultricies.ornare@google.com', 1),
  ('quis.turpis.vitae@icloud.edu', 2),
  ('ac.turpis@outlook.com', 3),
  ('magna.a@icloud.net', 4),
  ('sit.amet@aol.org', 5);

INSERT INTO contact_person (person_id)
VALUES
  (2);


INSERT INTO student (person_id, contact_person_id)
VALUES
  (3, NULL),
  (4, 1),
  (5, 1);

INSERT INTO instructor (person_id)
VALUES
	(1);

INSERT INTO price_schema (price, skill_level, discount, instructor_payment, initial_time)
VALUES
(100, 'Beginner', 0.2, 50, NOW()),
(100, 'Intermediate', 0.2, 50, NOW()),
(200, 'Advanced', 0.2, 100, NOW());

INSERT INTO lesson (instructor_id, start_time, end_time, place, price_schema_id)
VALUES
	(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 hours', 'room 301', 1),
	(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 hours', 'room 302', 2),
	(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 hours', 'room 303', 3);


INSERT INTO instrument_type(name, present_skill_id)
VALUES
	('Guitar', NULL),
	('Piano', NULL);

INSERT INTO individual_lesson(lesson_id, skill_level, instrument_type_id)
VALUES
	(1,'Beginner', 1);

INSERT INTO instrument_details (brand, price)
VALUES
	('Gibson', 420);

INSERT INTO ensemble_lesson(lesson_id, genre, minimum_of_students, maximum_of_students)
VALUES
	(2, 'Jazz', 5, 10);


INSERT INTO group_lesson(lesson_id, skill_level, minimum_of_students, maximum_of_students, instrument_type_id)
VALUES
	(3, 'Advanced', 5, 10, 2);
	
INSERT INTO instruments (instrument_type_id, instrument_details_id)
VALUES
	(1, 1);

INSERT INTO constraints_config (rule_id, constraint_value, constraint_description, start_time, end_time)
VALUES (1, 2, 'maximum rented instruments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '12 months');
INSERT INTO instructor_instrument_type (instructor_id, instrument_type_id)
VALUES (1, 1),
	(1, 2);


INSERT INTO student_lesson(lesson_id, student_id)
VALUES
	(1, 1),
	(2, 2),
	(2, 3),
	(3, 2),
	(3, 3);

UPDATE ensemble_lesson
SET minimum_of_students = '2'
WHERE minimum_of_students = '5';



UPDATE group_lesson
SET minimum_of_students = '2'
WHERE minimum_of_students = '5';


INSERT INTO instructor_availability (instructor_id, date, duration)
VALUES
(1, CURRENT_TIMESTAMP + INTERVAL '1 months', 60);





INSERT INTO person (name, person_number)
VALUES
  ('Christopher Barnes', 201112101603),
  ('Fitzgerald Strickland', 199903089481),
  ('Jamal Espinoza', 201307172138),
  ('Jared Whitaker', 193312263241),
  ('Brianna Wong', 196402284761),
  ('Quynn Leon', 197606014939),
  ('Indigo Goodman', 194411054747),
  ('Levi Parrish', 197805289878),
  ('Macey Kidd', 198704122186),
  ('Axel Hernandez', 199802250226);


INSERT INTO address (city,street,zip,person_id)
VALUES
  ('Cork','P.O. Box 831, 7761 Fusce Rd.','711985',6),
  ('Sakhalin','P.O. Box 580, 4203 Tempus St.','2273 YU',7),
  ('Wichita','Ap #512-5500 Ipsum Rd.','107004',8),
  ('Empangeni','Ap #692-6662 Pretium Road','08467',9),
  ('Delft','780-6053 Felis Av.','43581',10),
  ('Melilla','157-4723 Purus. St.','1052 XM',11),
  ('Whitehorse','5236 Sed Rd.','9135',12),
  ('Fuenlabrada','143-2098 Vulputate Avenue','5384',13),
  ('Chapadinha','P.O. Box 489, 7574 In Avenue','53997-587',14),
  ('Gore','Ap #208-4726 Purus Rd.','3601 QI',15);

INSERT INTO phone_number (phone_number,person_id)
VALUES
  ('(373) 205-8435',6),
  ('(487) 758-5337',7),
  ('1-676-613-4165',8),
  ('1-963-854-7685',9),
  ('1-847-107-1664',10),
  ('(779) 824-6537',11),
  ('(485) 774-3686',12),
  ('1-654-562-3835',13),
  ('1-344-856-8247',14),
  ('1-488-368-4465',15);

INSERT INTO email (email,person_id)
VALUES
  ('vitae@protonmail.ca',6),
  ('molestie.sodales@yahoo.couk',7),
  ('aliquam@google.net',8),
  ('mauris.ut@google.edu',9),
  ('lacus.etiam@aol.org',10),
  ('nisl.nulla@hotmail.com',11),
  ('bibendum.donec@google.couk',12),
  ('semper.et.lacinia@hotmail.ca',13),
  ('et.pede@yahoo.net',14),
  ('in.cursus.et@protonmail.couk',15);

INSERT INTO instructor (person_id)
VALUES
(6),
(7),
(8);

INSERT INTO student (person_id)
VALUES
(9),
(10),
(11),
(12),
(13),
(14),
(15);

INSERT INTO lesson (instructor_id, start_time, end_time, place, price_schema_id)
VALUES
	(1, CURRENT_TIMESTAMP + INTERVAL '3 months', CURRENT_TIMESTAMP  + INTERVAL '3 months' + INTERVAL '2 hours', 'room 301', 1),
	(2, CURRENT_TIMESTAMP + INTERVAL '4 months', CURRENT_TIMESTAMP  + INTERVAL '4 months' + INTERVAL '2 hours', 'room 302', 2),
	(3, CURRENT_TIMESTAMP + INTERVAL '5 months', CURRENT_TIMESTAMP  + INTERVAL '5 months' + INTERVAL '2 hours', 'room 303', 3),
	(4, CURRENT_TIMESTAMP + INTERVAL '6 months', CURRENT_TIMESTAMP  + INTERVAL '6 months' + INTERVAL '2 hours', 'room 301', 1),
	(2, CURRENT_TIMESTAMP + INTERVAL '7 months', CURRENT_TIMESTAMP  + INTERVAL '7 months' + INTERVAL '2 hours', 'room 302', 2),
	(3, CURRENT_TIMESTAMP + INTERVAL '7 months', CURRENT_TIMESTAMP  + INTERVAL '7 months' + INTERVAL '2 hours', 'room 303', 3),
	(4, CURRENT_TIMESTAMP + INTERVAL '8 months', CURRENT_TIMESTAMP  + INTERVAL '8 months' + INTERVAL '2 hours', 'room 301', 1),
	(1, CURRENT_TIMESTAMP + INTERVAL '9 months', CURRENT_TIMESTAMP  + INTERVAL '9 months' + INTERVAL '2 hours', 'room 302', 2),
	(2, CURRENT_TIMESTAMP + INTERVAL '9 months', CURRENT_TIMESTAMP  + INTERVAL '9 months' + INTERVAL '2 hours', 'room 303', 3),
	(3, CURRENT_TIMESTAMP + INTERVAL '10 months', CURRENT_TIMESTAMP  + INTERVAL '10 months' + INTERVAL '2 hours', 'room 301', 1),
	(4, CURRENT_TIMESTAMP + INTERVAL '2 months', CURRENT_TIMESTAMP  + INTERVAL '2 months' + INTERVAL '2 hours', 'room 302', 2),
	(1, CURRENT_TIMESTAMP + INTERVAL '1 months', CURRENT_TIMESTAMP  + INTERVAL '1 months' + INTERVAL '2 hours', 'room 303', 3),
	(2, CURRENT_TIMESTAMP + INTERVAL '11 months', CURRENT_TIMESTAMP  + INTERVAL '11 months' + INTERVAL '2 hours', 'room 301', 1),
	(2, CURRENT_TIMESTAMP + INTERVAL '12 months', CURRENT_TIMESTAMP  + INTERVAL '12 months' + INTERVAL '2 hours', 'room 302', 2),
	(3, CURRENT_TIMESTAMP + INTERVAL '12 months', CURRENT_TIMESTAMP  + INTERVAL '12 months' + INTERVAL '2 hours', 'room 303', 3),

    (1, CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP - INTERVAL '3 months' + INTERVAL '2 hours', 'room 301', 1),
    (2, CURRENT_TIMESTAMP - INTERVAL '4 months', CURRENT_TIMESTAMP - INTERVAL '4 months' + INTERVAL '2 hours', 'room 302', 2),
    (3, CURRENT_TIMESTAMP - INTERVAL '5 months', CURRENT_TIMESTAMP - INTERVAL '5 months' + INTERVAL '2 hours', 'room 303', 3),
    (4, CURRENT_TIMESTAMP - INTERVAL '6 months', CURRENT_TIMESTAMP - INTERVAL '6 months' + INTERVAL '2 hours', 'room 301', 1),
    (2, CURRENT_TIMESTAMP - INTERVAL '7 months', CURRENT_TIMESTAMP - INTERVAL '7 months' + INTERVAL '2 hours', 'room 302', 2),
    (3, CURRENT_TIMESTAMP - INTERVAL '7 months', CURRENT_TIMESTAMP - INTERVAL '7 months' + INTERVAL '2 hours', 'room 303', 3),
    (4, CURRENT_TIMESTAMP - INTERVAL '8 months', CURRENT_TIMESTAMP - INTERVAL '8 months' + INTERVAL '2 hours', 'room 301', 1),
    (1, CURRENT_TIMESTAMP - INTERVAL '9 months', CURRENT_TIMESTAMP - INTERVAL '9 months' + INTERVAL '2 hours', 'room 302', 2),
    (2, CURRENT_TIMESTAMP - INTERVAL '9 months', CURRENT_TIMESTAMP - INTERVAL '9 months' + INTERVAL '2 hours', 'room 303', 3),
    (3, CURRENT_TIMESTAMP - INTERVAL '10 months', CURRENT_TIMESTAMP - INTERVAL '10 months' + INTERVAL '2 hours', 'room 301', 1),
    (4, CURRENT_TIMESTAMP - INTERVAL '2 months', CURRENT_TIMESTAMP - INTERVAL '2 months' + INTERVAL '2 hours', 'room 302', 2),
    (1, CURRENT_TIMESTAMP - INTERVAL '1 months', CURRENT_TIMESTAMP - INTERVAL '1 months' + INTERVAL '2 hours', 'room 303', 3),
    (2, CURRENT_TIMESTAMP - INTERVAL '11 months', CURRENT_TIMESTAMP - INTERVAL '11 months' + INTERVAL '2 hours', 'room 301', 1),
    (2, CURRENT_TIMESTAMP - INTERVAL '12 months', CURRENT_TIMESTAMP - INTERVAL '12 months' + INTERVAL '2 hours', 'room 302', 2),
    (3, CURRENT_TIMESTAMP - INTERVAL '12 months', CURRENT_TIMESTAMP - INTERVAL '12 months' + INTERVAL '2 hours', 'room 303', 3);

INSERT INTO individual_lesson(lesson_id, skill_level, instrument_type_id)
VALUES
	(4,'Beginner', 2),
	(7,'Beginner', 1),
	(10,'Beginner', 2),
	(13,'Beginner', 1),
	(16,'Beginner', 2),
	(19,'Beginner', 1),
	(22,'Beginner', 1),
	(25,'Beginner', 2),
	(28,'Beginner', 1),
	(31,'Beginner', 1);


INSERT INTO group_lesson(lesson_id, skill_level, minimum_of_students, maximum_of_students, instrument_type_id)
VALUES
	(5, 'Advanced', 5, 10, 1),
	(8, 'Advanced', 5, 10, 2),
	(11, 'Advanced', 5, 10, 1),
	(14, 'Advanced', 5, 10, 2),
	(17, 'Advanced', 5, 10, 1),
	(20, 'Advanced', 5, 10, 2),
	(23, 'Advanced', 5, 10, 1),
	(26, 'Advanced', 5, 10, 2),
	(29, 'Advanced', 5, 10, 2),
	(32, 'Advanced', 5, 10, 1);

INSERT INTO ensemble_lesson(lesson_id, genre, minimum_of_students, maximum_of_students)
VALUES
	(6, 'Punk rock', 5, 10),
	(9, 'Classical', 5, 10),
	(12, 'Rock', 5, 10),
	(15, 'Jazz', 5, 10),
	(18, 'Rock', 5, 10),
	(21, 'Jazz', 5, 10),
	(24, 'Punk rock', 5, 10),
	(27, 'Jazz', 5, 10),
	(30, 'Punk rock', 5, 10),
	(33, 'Jazz', 5, 10);

INSERT INTO student_sibling (student_id_0, student_id_1)
VALUES
(1, 3),
(3, 1),
(2, 3),
(3, 2),
(4, 5),
(5, 4),
(6, 7),
(7, 6),
(1, 2),
(2, 1);

INSERT INTO lesson (instructor_id, start_time, end_time, place, price_schema_id)
VALUES
	(4, CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP  + INTERVAL '3 days' + INTERVAL '2 hours', 'room 301', 1),
	(3, CURRENT_TIMESTAMP + INTERVAL '4 days', CURRENT_TIMESTAMP  + INTERVAL '4 days' + INTERVAL '2 hours', 'room 302', 2),
	(2, CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP  + INTERVAL '5 days' + INTERVAL '2 hours', 'room 303', 3),
	(4, CURRENT_TIMESTAMP + INTERVAL '6 days', CURRENT_TIMESTAMP  + INTERVAL '6 days' + INTERVAL '2 hours', 'room 301', 1),
	(3, CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP  + INTERVAL '7 days' + INTERVAL '2 hours', 'room 302', 2),
	(2, CURRENT_TIMESTAMP + INTERVAL '8 days', CURRENT_TIMESTAMP  + INTERVAL '8 days' + INTERVAL '2 hours', 'room 303', 3);

INSERT INTO lesson (instructor_id, start_time, end_time, place, price_schema_id)
VALUES
	(4, CURRENT_TIMESTAMP + INTERVAL '8 days', CURRENT_TIMESTAMP  + INTERVAL '8 days' + INTERVAL '2 hours', 'room 301', 1),
	(4, CURRENT_TIMESTAMP + INTERVAL '9 days', CURRENT_TIMESTAMP  + INTERVAL '9 days' + INTERVAL '2 hours', 'room 301', 1),
	(4, CURRENT_TIMESTAMP + INTERVAL '10 days', CURRENT_TIMESTAMP  + INTERVAL '10 days' + INTERVAL '2 hours', 'room 301', 1);



INSERT INTO ensemble_lesson(lesson_id, genre, minimum_of_students, maximum_of_students)
VALUES
	(34, 'Punk rock', 5, 10),
	(35, 'Classical', 5, 10),
	(36, 'Rock', 5, 10),
	(37, 'Jazz', 5, 10),
	(38, 'Rock', 5, 10),
	(39, 'Jazz', 5, 10);

INSERT INTO ensemble_lesson(lesson_id, genre, minimum_of_students, maximum_of_students)
VALUES
	(40, 'Jazz', 5, 10),
	(41, 'Rock', 5, 10),
	(42, 'Jazz', 5, 10);


-- Cast the existing data in minimum_of_students to INT
ALTER TABLE ensemble_lesson
ALTER COLUMN minimum_of_students TYPE INT
USING minimum_of_students::INT;

-- Cast the existing data in maximum_of_students to INT
ALTER TABLE ensemble_lesson
ALTER COLUMN maximum_of_students TYPE INT
USING maximum_of_students::INT;

INSERT INTO student_lesson (lesson_id, student_id)
VALUES
	(38, 1),
	(38, 2),
	(38, 3),
	(38, 4),
	(38, 5),
	(38, 6),
	(38, 7),
	(38, 8),
	(38, 9),
	(38, 10);

INSERT INTO student_lesson (lesson_id, student_id)
VALUES
	(40, 1),
	(40, 2),
	(40, 3),
	(40, 4),
	(40, 5),
	(40, 6),
	(40, 7),
	(40, 8);



-- Inserted more data for rented instruments i task 4

insert into instrument_details (brand, price)
VALUES('Yamaha', 1000);

insert into instruments (instrument_type_id, instrument_details_id)
Values(2,2);

INSERT INTO instrument_type (name)
VALUES
	('Trumpet'),
	('Clarinet'),
	('Bagpipe'),
	('Flute'),
	('Drums'),
	('Violin'),
	('Saxophone'),
	('Cello');
INSERT INTO instrument_details (brand, price)
VALUES
	('Yamaha', 500),
	('Selmer', 800),
	('O.Keefe', 1200),
	('Yamaha', 400),
	('Yamaha', 1200),
	('SONOR', 1300),
	('Cecilio', 1200),
	('Cecilio', 1400);

INSERT INTO instruments (instrument_type_id, instrument_details_id)
VALUES
	(3,3),
	(4,4),
	(5,5),
	(6,6),
	(7,7),
	(8,8),
	(9,9),
	(10,10),
	(1,1),
	(1,1);

INSERT INTO rented_instrument (rental_start_time, instrument_id, student_id)
VALUES
	(NOW() - interval '2 weeks', 1, 1),
	(NOW() - interval '2 weeks', 2, 2);
	
 --Trigger for business logic

CREATE OR REPLACE FUNCTION prevent_insert_trigger_function()
RETURNS TRIGGER AS $$
DECLARE
    row_count INT;
BEGIN
    -- Get the count of rows for the specific student_id where rental_end_time IS NULL
    SELECT COUNT(*) INTO row_count 
    FROM rented_instrument
    WHERE student_id = NEW.student_id AND rental_end_time IS NULL;

    -- Check the condition and raise an error if count is greater than or equal to 2
    IF row_count >= 2 THEN
        RAISE EXCEPTION 'Insertion not allowed. Row count is >= 2 for the specified student_id.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_insert_trigger
BEFORE INSERT ON rented_instrument
FOR EACH ROW
EXECUTE FUNCTION prevent_insert_trigger_function();
