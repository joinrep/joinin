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
	
		mysql_query("INSERT INTO Participate VALUES('$event_id', '$user_id')");
		
		$response["success"] = 1;
        $response["message"] = "Participant successfully added.";
		
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