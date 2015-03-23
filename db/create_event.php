<?php
// array for JSON response
$response = array();
 
if(isset($_POST['event_name']) && isset($_POST['start_time']) && isset($_POST['end_time']) && isset($_POST['description']) &&
	isset($_POST['notes']) && isset($_POST['limit']) && isset($_POST['cost']) && isset($_POST['category']) && isset($_POST['location']) && 
	isset($_POST['city']) && isset($_POST['street1']) && isset($_POST['street2']) && isset($_POST['location_name'])&& isset($_POST['organizer'])) {
	
	$event_name = $_POST['event_name'];
	$start_time = $_POST['start_time'];
	$end_time = $_POST['end_time'];
	$description = $_POST['description'];
	$notes = $_POST['notes'];
	$limit = $_POST['limit'];
	$cost = $_POST['cost'];
	$category = $_POST['category'];
	$location = $_POST['location'];
	$city = $_POST['city'];
	$street1 = $_POST['street1'];
	$street2 = $_POST['street2'];
	$location_name = $_POST['location_name'];
	$organizer = $_POST['organizer'];
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());
	

	if ($location == 0) {
		$result = mysql_query("INSERT INTO Address(address_id, city, street1, street2, location_name) VALUES(NULL, '$city', '$street1', '$street2', '$location_name')");
		$location = mysql_insert_id()
	}
	
    $result = mysql_query("INSERT INTO Events(event_id, event_name, start_time, end_time, description, notes, limit, cost, canceled, #category, #location, 
		#organizer)  VALUES(NULL, '$event_name', '$start_time', '$end_time', '$description', '$notes', $limit, $cost, 'N', $category, $location, $organizer)");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Event successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
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