CREATE TABLE Category (
	`id` INT(10) unsigned NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) COLLATE utf8_polish_ci NOT NULL,
	`icon_path` VARCHAR(255) COLLATE utf8_polish_ci NOT NULL
	PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

CREATE TABLE Event (
	`id` INT(10) unsigned NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) COLLATE utf8_polish_ci NOT NULL,
	`start_time` DATE NOT NULL,
	`end_time` DATE NOT NULL,
	`description` TEXT,
	`limit` INT(10),
	`cost` DECIMAL(11,2),
	`canceled` VARCHAR(1) DEFAULT 'N',
	`category` INT(10) REFERENCES Category(`id`)
	PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

INSERT INTO `Category` (`category_id`, `category_name`, `category_icon`) VALUES
(1, 'Piłka nożna', 'icon.png'),
(2, 'Koszykówka', 'icon.png');

    private int id;
    private String name;
    private Date startTime;
    private Date endTime;
    private String description;
    private int limit;
    private double cost;
    private boolean canceled;

    private Address location;
    private Category category;
    private User organizer;
    private List<User> participants;
    private List<Comment> comments;

CREATE TABLE IF NOT EXISTS `Category` (
  `category_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) COLLATE utf8_polish_ci NOT NULL,
  `category_icon` varchar(50) COLLATE utf8_polish_ci NOT NULL,

) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci AUTO_INCREMENT=3 ;

--
-- Zrzut danych tabeli `Category`
--

INSERT INTO `Category` (`category_id`, `category_name`, `category_icon`) VALUES
(1, 'Piłka nożna', 'icon.png'),
(2, 'Koszykówka', 'icon.png');

CREATE TABLE Event {

}

    private int id;
    private String name;
    private Date startTime;
    private Date endTime;
    private String description;
    private int limit;
    private double cost;
    private boolean canceled;

    private Address location;
    private Category category;
    private User organizer;
    private List<User> participants;
    private List<Comment> comments;
	
	
	    private int id;
    private String name;
    private String iconPath;

    private List<User> subscribers;
    private List<Event> events;