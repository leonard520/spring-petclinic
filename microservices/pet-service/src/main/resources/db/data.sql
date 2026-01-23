INSERT INTO pet_types (id, name) VALUES (1, 'cat');
INSERT INTO pet_types (id, name) VALUES (2, 'dog');
INSERT INTO pet_types (id, name) VALUES (3, 'lizard');
INSERT INTO pet_types (id, name) VALUES (4, 'snake');
INSERT INTO pet_types (id, name) VALUES (5, 'bird');
INSERT INTO pet_types (id, name) VALUES (6, 'hamster');

INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (1, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (2, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (3, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (4, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (5, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (6, 'George', '2010-01-20', 4, 5);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (7, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (8, 'Max', '2012-09-04', 1, 6);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (9, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (10, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (11, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (12, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (13, 'Sly', '2012-06-08', 1, 10);

-- Reset identity sequences to avoid conflicts with new inserts
ALTER TABLE pet_types ALTER COLUMN id RESTART WITH 100;
ALTER TABLE pets ALTER COLUMN id RESTART WITH 100;
