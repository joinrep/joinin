
DROP TABLE Following;
DROP TABLE Favorites;
DROP TABLE Points;
DROP TABLE Participate;
DROP TABLE MyComment;
DROP TABLE Event;
DROP TABLE MyUser;
DROP TABLE Category;
DROP TABLE Address;

CREATE TABLE Address (
	address_id INT(10) unsigned NOT NULL AUTO_INCREMENT,
	city VARCHAR(30) COLLATE utf8_polish_ci NOT NULL,
	street1 VARCHAR(30) COLLATE utf8_polish_ci,
	street2 VARCHAR(30) COLLATE utf8_polish_ci,
	location_name VARCHAR(40) COLLATE utf8_polish_ci NOT NULL,
	PRIMARY KEY (address_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Category (
	category_id INT(10) unsigned NOT NULL AUTO_INCREMENT,
	category_name VARCHAR(255) COLLATE utf8_polish_ci NOT NULL,
	icon_path VARCHAR(255) COLLATE utf8_polish_ci NOT NULL,
	PRIMARY KEY (category_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE MyUser (
	facebook_id INT(10) unsigned NOT NULL,
	first_name VARCHAR(20) COLLATE utf8_polish_ci NOT NULL,
	last_name VARCHAR(20) COLLATE utf8_polish_ci NOT NULL,
	PRIMARY KEY (facebook_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Event (
	event_id INT(10) unsigned NOT NULL AUTO_INCREMENT,
	event_name VARCHAR(255) COLLATE utf8_polish_ci NOT NULL,
	start_time DATETIME NOT NULL,
	end_time DATETIME NOT NULL,
	description TEXT,
	size_limit INT(10),
	cost DECIMAL(11,2),
	canceled VARCHAR(1) DEFAULT 'N' CHECK(canceled IN ('Y','N')),
	category INT(10) unsigned NOT NULL REFERENCES Category(category_id),
	organizer INT(10) unsigned NOT NULL REFERENCES MyUser(facebook_id),
	address INT(10) unsigned NOT NULL REFERENCES Address(address_id),
	PRIMARY KEY (event_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE MyComment (
	comment_id INT(10) unsigned NOT NULL AUTO_INCREMENT,
	comment_timestamp DATETIME NOT NULL,
	comment_body VARCHAR(255) COLLATE utf8_polish_ci,
	parent_comment INT(10) unsigned REFERENCES MyComment(comment_id),
	event INT(10) unsigned NOT NULL REFERENCES Event(event_id),
	author INT(10) unsigned NOT NULL REFERENCES MyUser(facebook_id),
	PRIMARY KEY (comment_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Participate (
	joined_event INT(10) unsigned REFERENCES Event(event_id),
	facebook_id INT(10) unsigned REFERENCES MyUser(user_id), 
	PRIMARY KEY (joined_event, facebook_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Points (
	joined_event INT(10) unsigned REFERENCES Participate(event_id),
	facebook_id INT(10) unsigned REFERENCES Participate(facebook_id),
	evaluator INT(10) unsigned NOT NULL REFERENCES MyUser(facebook_id),
	point INT(5),
	PRIMARY KEY (joined_event, facebook_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;
	
CREATE TABLE Favorites (
	subscriber INT(10) unsigned NOT NULL REFERENCES MyUser(facebook_id), 
	category INT(10) unsigned NOT NULL REFERENCES Category(category_id),
	PRIMARY KEY (subscriber, category)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Following (
	follower INT(10) unsigned NOT NULL REFERENCES MyUser(facebook_id),
	follows INT(10) unsigned NOT NULL REFERENCES MyUser(facebook_id),
	PRIMARY KEY (follower, follows)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

INSERT INTO Address (address_id, city, street1, street2, location_name) VALUES
(1, 'Wrocław', 'Grabiszyńska 40', '', 'Szkoła podstawowa nr 5');

INSERT INTO MyUser (facebook_id, first_name, last_name) VALUES
(1, 'Marek', 'Kos');

INSERT INTO Category (category_id, category_name, icon_path) VALUES
(10, 'Piłka nożna', 'ic_category_football'),
(20, 'Siatkówka', 'ic_category_volleyball'),
(30, 'Koszykówka', 'ic_category_basketball'),
(40, 'Wycieczki rowerowe', 'ic_category_bike'),
(50, 'Wycieczki piesze', 'ic_category_hiking'),
(60, 'Kręgle', 'ic_category_bowling'),
(70, 'Tenis', 'ic_category_tennis');

INSERT INTO Event (event_id, event_name, start_time, end_time, description, size_limit, cost, canceled, category, organizer, address) VALUES
(1, 'Gała po szkole', '2015-03-15 15:00:00', '2015-03-15 18:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 20, 0, 'N', 10, 1, 1),
(2, 'Faza grupowa klubu seniora', '2015-03-15 16:00:00', '2015-03-15 17:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 10, 5, 'N', 10, 1, 1),
(3, 'DUNK MASTER', '2015-03-22 12:00:00', '2015-03-22 18:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 10, 2.50, 'Y', 20, 1, 1),
(4, 'Wiosenne niedzielne popołudnie z przyjaciółmi grając w koszykówkę', '2015-03-21 15:00:00', '2015-03-21 18:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 100, 5.50, 'N', 20, 1, 1);
