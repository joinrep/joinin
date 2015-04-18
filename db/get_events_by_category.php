<?php
$response = array();
if (isset($_GET["category_id"])) {
    $category_id = $_GET['category_id'];
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());

	$query = "SELECT * FROM Event WHERE canceled = 'N' AND category = $category_id";
	if (isset($_GET['canceled'])) {
		if ($_GET['canceled'] === 'Y') {
			$query = "SELECT * FROM Event WHERE category = $category_id";
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