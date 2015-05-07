<?php
// array for JSON response
$response = array();
 
if(isset($_POST['event_id']) && isset($_POST['canceled'])) {
	
	$event_id = $_POST['event_id'];
	$canceled = $_POST['canceled'];
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());	

	mysql_query("UPDATE Event SET canceled='$canceled' WHERE event_id = $event_id");
		
	$response["success"] = 1;
    $response["message"] = "Participant successfully added.";
	
	echo json_encode($response);

} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>