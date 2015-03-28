DROP TABLE Event;
DROP TABLE Category;

CREATE TABLE Category (
	category_name VARCHAR(255) COLLATE utf8_polish_ci NOT NULL,
	icon_path VARCHAR(255) COLLATE utf8_polish_ci NOT NULL,
	PRIMARY KEY (category_name)
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
	category VARCHAR(255) REFERENCES Category(category_name),
	PRIMARY KEY (event_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

INSERT INTO Category (category_name, icon_path) VALUES
('Piłka nożna', 'icon.png'),
('Koszykówka', 'icon.png');

INSERT INTO Event (event_id, event_name, start_time, end_time, description, size_limit, cost, canceled, category) VALUES
(1, 'Gała po szkole', '2015-03-15 15:00:00', '2015-03-15 18:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 20, 0, 'N', 'Piłka nożna'),
(2, 'Faza grupowa klubu seniora', '2015-03-15 16:00:00', '2015-03-15 17:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 10, 5, 'N', 'Piłka nożna'),
(3, 'DUNK MASTER', '2015-03-22 12:00:00', '2015-03-22 18:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 10, 2.50, 'Y', 'Koszykówka'),
(4, 'Wiosenne niedzielne popołudnie z przyjaciółmi grając w koszykówkę', '2015-03-21 15:00:00', '2015-03-21 18:00:00', 'Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis. Jakiś fajny i długi opis.', 100, 5.50, 'N', 'Koszykówka');

FILIP:

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

CREATE TABLE User (
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
	organizer INT(10) unsigned NOT NULL REFERENCES User(facebook_id),
	address INT(10) unsigned NOT NULL REFERENCES Address(address_id),
	PRIMARY KEY (event_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Comment (
	comment_id INT(10) unsigned NOT NULL AUTO_INCREMENT,
	timestamp DATETIME NOT NULL,
	comment_body VARCHAR(255) COLLATE utf8_polish_ci,
	parent_comment INT(20) unsignded REFERENCES Comment(comment_id),
	event INT(10) unsigned NOT NULL REFERENCES Event(event_id),
	author INT(20) unsigned NOT NULL REFERENCES User(facebook_id)
	PRIMARY KEY (comment_id)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Points (
	participate_id INT(10) unsigned NOT NULL AUTO_INCREMENT,
	user_id INT(10) unsigned NOT NULL REFERENCES User(facebook_id),
	event_id INT(10) unsigned NOT NULL REFERENCES Event(event_id),
	positive INT(5),
	PRIMARY KEY (participate_id)
);
	
CREATE TABLE Favorites (
	subscriber INT(10) unsigned NOT NULL REFERENCES User(facebook_id), 
	category INT(10) unsigned NOT NULL REFERENCES Category(category_id),
	PRIMARY KEY (subscriber, category)
);


CREATE TABLE Following (
	user INT(10) unsigned NOT NULL REFERENCES User(facebook_id),
	follows INT(10) unsigned NOT NULL REFERNCES User(facebook_id),
	PRIMARY KEY (user, follows)
);
