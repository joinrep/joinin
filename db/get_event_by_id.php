<?php
$response = array();
if (isset($_GET["event_id"]) && isset($_GET["user_id"])) {
    $event_id = $_GET['event_id'];
	$user_id = $_GET['user_id'];
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());


	$query = "SELECT *, (SELECT COUNT(*) FROM Participate WHERE joined_event = E.event_id GROUP BY joined_event) participants, (SELECT 'true' FROM Participate WHERE joined_event = E.event_id AND participant_id = $user_id) isParticipant FROM Event E JOIN Category C ON E.category = C.category_id LEFT JOIN Address A  ON E.address = A.address_id LEFT JOIN MyUser U ON E.organizer = U.user_id WHERE E.event_id = $event_id";
	$result = mysql_query($query) or die(mysql_error());
	if (mysql_num_rows($result) > 0) {
		if ($row = mysql_fetch_array($result)) {
			$event = array();
			$event["id"] = $row["event_id"];
			$event["name"] = $row["event_name"];
			$event["start_time"] = $row["start_time"];
			$event["end_time"] = $row["end_time"];
			$event["size_limit"] = $row["size_limit"];
			$event["description"] = $row["description"];
			$event["cost"] = $row["cost"];
			$event["canceled"] = $row["canceled"];
			$event["category_id"] = $row["category_id"];
			if ($row["participants"]) {
				$event["participants"] = $row["participants"];
			} else {
				$event["participants"] = 0;
			}
			if ($row["isParticipant"]) {
				$event["is_participant"] = $row["isParticipant"];
			} else {
				$event["is_participant"] = 'false';
			}
			if ($row["location_name"]) {
				$event["location_name"] = $row["location_name"];
			} else {
				$event["location_name"] = '';
			}
			
			$address = array();
			$address["city"] = $row["city"];
			$address["street1"] = $row["street1"];
			$address["street2"] = $row["street2"];
			$address["location_name"] = $row["location_name"];
			$event["address"] = $address;
			
			$organizer = array();
			$organizer["user_id"] = $row["user_id"];
			$organizer["facebook_id"] = $row["facebook_id"];
			$organizer["google_id"] = $row["google_id"];
			$organizer["first_name"] = $row["first_name"];
			$organizer["last_name"] = $row["last_name"];
			$event["organizer"] = $organizer;
			
			$event["participants_array"] = array();
			$query = "SELECT * FROM Participate P JOIN MyUser U ON P.participant_id = U.user_id WHERE joined_event = $event_id";
			$result = mysql_query($query) or die(mysql_error());
			if (mysql_num_rows($result) > 0) {
				while ($row = mysql_fetch_array($result)) {
					$user = array();
					$user["user_id"] = $row["user_id"];
					$user["facebook_id"] = $row["facebook_id"];
					$user["google_id"] = $row["google_id"];
					$user["first_name"] = $row["first_name"];
					$user["last_name"] = $row["last_name"];
								
					array_push($event["participants_array"], $user);
				}
			}
	
			/*
			$event["comments"] = array();
			$query = "SELECT * FROM MyComment C JOIN MyUser U ON C.author = U.facebook_id WHERE event = $event_id";
			$result = mysql_query($query) or die(mysql_error());
			if (mysql_num_rows($result) > 0) {
				while ($row = mysql_fetch_array($result)) {
					$comment = array();
					$comment["comment_id"] = $row["comment_id"];
					$comment["comment_timestamp"] = $row["comment_timestamp"];
					$comment["comment_body"] = $row["comment_body"];
					$comment["parent_comment"] = $row["parent_comment"];
					
					$author = array();
					$author["facebook_id"] = $row["facebook_id"];
					$author["first_name"] = $row["first_name"];
					$author["last_name"] = $row["last_name"];
					$comment["author"] = $author;
						
					array_push($event["comments"], $comment);
				}
			}*/
			
			$response["event"] = $event;
		}
		
		$response["success"] = 1;
		
		echo json_encode($response);
		
	} else {
	
		$response["success"] = 0;
		$response["message"] = "No events found";
		
		echo json_encode($response);
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>