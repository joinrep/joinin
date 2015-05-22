<?php
// array for JSON response
$response = array();
 
if(isset($_POST['event_id']) && isset($_POST['event_name']) && isset($_POST['start_time']) && isset($_POST['end_time']) && isset($_POST['description']) &&
	isset($_POST['notes']) && isset($_POST['limit']) && isset($_POST['cost']) && isset($_POST['category']) && isset($_POST['location']) && 
	isset($_POST['city']) && isset($_POST['street1']) && isset($_POST['street2']) && isset($_POST['location_name'])&& isset($_POST['organizer'])) {

	date_default_timezone_set("Europe/Warsaw");
	
	$event_id = $_POST['event_id'];
	$event_name = $_POST['event_name'];
	$start_time = date("Y-m-d H:i:s",$_POST['start_time']/1000);
	$end_time = date("Y-m-d H:i:s",$_POST['end_time']/1000);
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
	
	$response = array();
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());
	

	if ($location == 0) {
		mysql_query("INSERT INTO Address(address_id, city, street1, street2, location_name) VALUES(NULL, '$city', '$street1', '$street2', '$location_name')");
		$result = mysql_query("SELECT address_id FROM Address WHERE city='$city' AND street1='$street1' AND street2='$street2' AND location_name='$location_name'");
		if ($row = mysql_fetch_array($result)) {
			$location = $row["address_id"];
		}	
	}
	
	$query = "UPDATE Event SET event_name = '$event_name', start_time = '$start_time', end_time = '$end_time', description = '$description', size_limit = $limit, cost = $cost, category = $category, organizer = $organizer, address = $location WHERE event_id = $event_id";
	$result = mysql_query($query);
 
    // check if row inserted or not
    if ($result) {
		
		// successfully inserted into database
		$response["success"] = 1;
		$response["message"] = "Event successfully updated. Query: ".$query;
 
		// echoing JSON response
		echo json_encode($response);

    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred. Query: ".$query;
 
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