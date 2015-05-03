<?php
// array for JSON response
$response = array();

if (isset($_GET["user_id"])) {
    $user_id = $_GET['user_id'];
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());

	$query = "SELECT *, (SELECT COUNT(*) FROM Participate WHERE joined_event = E.event_id GROUP BY joined_event) participants, (SELECT 'true' FROM Participate WHERE joined_event = E.event_id AND participant_id = $user_id) isParticipant FROM Event E JOIN Category C ON E.category = C.category_id WHERE E.canceled = 'N'";
	if (isset($_GET['canceled'])) {
		if ($_GET['canceled'] === 'Y') {
			$query = "SELECT *, (SELECT COUNT(*) FROM Participate WHERE joined_event = E.event_id GROUP BY joined_event) participants, (SELECT 'true' FROM Participate WHERE joined_event = E.event_id AND participant_id = $user_id) isParticipant FROM Event E";
		}
	}

	$result = mysql_query($query) or die(mysql_error());
	if (mysql_num_rows($result) > 0) {
		$response["events"] = array();
		while ($row = mysql_fetch_array($result)) {
			$event = array();
			$event["id"] = $row["event_id"];
			$event["name"] = $row["event_name"];
			$event["start_time"] = $row["start_time"];
			$event["end_time"] = $row["end_time"];
			$event["size_limit"] = $row["size_limit"];
			$event["cost"] = $row["cost"];
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

			array_push($response["events"], $event);
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