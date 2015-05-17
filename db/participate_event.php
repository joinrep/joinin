<?php
// array for JSON response
$response = array();
 
if(isset($_POST['event_id']) && isset($_POST['user_id']) && isset($_POST['participate'])) {
	
	$event_id = $_POST['event_id'];
	$user_id = $_POST['user_id'];
	$participate = $_POST['participate'];
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());

	if ($participate == 'true') {
	
		$query = "SELECT COUNT(*) participants_count, (SELECT size_limit FROM Event WHERE event_id = '$event_id') size_limit FROM Participate WHERE joined_event = '$event_id'";
		$result = mysql_query($query) or die(mysql_error());
		
		if (mysql_num_rows($result) > 0) {
			$row = mysql_fetch_array($result);
			
			if (intval($row["size_limit"]) == -1 || (intval($row["size_limit"]) - intval($row["participants_count"])) > 0) {
			
				mysql_query("INSERT INTO Participate VALUES('$event_id', '$user_id')");
			
				$response["success"] = 1;
				$response["message"] = "Participant successfully added.";
				
			} else {
			
				$response["success"] = 0;
				$response["message"] = "Event is full";
				
			}
		} else {
			$response["success"] = 0;
			$response["message"] = "Event limit cannot be verified";
		}
		
	} else if ($participate == 'false') {
	
		mysql_query("DELETE FROM Participate WHERE joined_event='$event_id' AND participant_id='$user_id'");
		
		$response["success"] = 1;
        $response["message"] = "Participant successfully removed.";
		
	} else {
	
		$response["success"] = 0;
		$response["message"] = "Unknown command";
		
	}
	
	echo json_encode($response);

} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>