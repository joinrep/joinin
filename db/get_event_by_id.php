<?php
$response = array();
if (isset($_GET["event_id"])) {
    $event_id = $_GET['event_id'];
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());

	$query = "SELECT * FROM Event E LEFT JOIN Category C ON E.category = C.category_name LEFT JOIN Address A ON E.address = A.address_id LEFT JOIN MyUser U ON E.organizer = U.facebook_id WHERE E.event_id = $event_id";
	$result = mysql_query($query) or die(mysql_error());
	if (mysql_num_rows($result) > 0) {
		if ($row = mysql_fetch_array($result)) {
			$event = array();
			$event["id"] = $row["event_id"];
			$event["name"] = $row["event_name"];
			$event["start_time"] = $row["start_time"];
			$event["end_time"] = $row["end_time"];
			$event["description"] = $row["description"];
			$event["size_limit"] = $row["size_limit"];
			$event["canceled"] = $row["canceled"];
			$event["cost"] = $row["cost"];
			
			$address = array();
			$address["city"] = $row["city"];
			$address["street1"] = $row["street1"];
			$address["street2"] = $row["street2"];
			$address["location_name"] = $row["location_name"];
			$event["address"] = $address;
			
			$organizer = array();
			$organizer["facebook_id"] = $row["facebook_id"];
			$organizer["first_name"] = $row["first_name"];
			$organizer["last_name"] = $row["last_name"];
			$event["organizer"] = $organizer;
			
			$event["participants"] = array();
			$query = "SELECT * FROM Participate P JOIN MyUser U ON P.facebook_id = U.facebook_id WHERE joined_event = $event_id";
			$result = mysql_query($query) or die(mysql_error());
			if (mysql_num_rows($result) > 0) {
				while ($row = mysql_fetch_array($result)) {
					$user = array();
					$user["facebook_id"] = $row["facebook_id"];
					$user["first_name"] = $row["first_name"];
					$user["last_name"] = $row["last_name"];
								
					array_push($event["participants"], $user);
				}
			}
	
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
			}
			
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