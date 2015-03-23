<?php
$response = array();
require_once(dirname(__FILE__).'/db_connect.php');
$db = new DB_CONNECT();
mysql_query("SET NAMES 'utf8'") or die(mysql_error());

$query = "SELECT * FROM Event E LEFT JOIN Category C ON E.category = C.category_name WHERE E.canceled = 'N'";
if (isset($_GET['canceled'])) {
	if ($_GET['canceled'] === 'Y') {
		$query = "SELECT * FROM Event E LEFT JOIN Category C ON E.category = C.category_name";
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
        $event["category_name"] = $row["category_name"];
		$event["category_icon"] = $row["icon_path"];
					
        array_push($response["events"], $event);
    }
    $response["success"] = 1;
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No events found";
    echo json_encode($response);
}
?>