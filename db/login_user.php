<?php
$response = array();
if (isset($_GET["facebook_id"]) || isset($_GET["google_id"])) {
	if (isset($_GET["facebook_id"])) {
		$facebook_id = $_GET["facebook_id"];
		$query = "SELECT * FROM MyUser WHERE facebook_id = '$facebook_id'";
	}
	if (isset($_GET["google_id"])) {
		$google_id = $_GET["google_id"];
		$query = "SELECT * FROM MyUser WHERE google_id = '$google_id'";
	}
	
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());

	$result = mysql_query($query) or die(mysql_error());
	if (mysql_num_rows($result) > 0) {
		if ($row = mysql_fetch_array($result)) {
			$user = array();
			$user["user_id"] = $row["user_id"];
			$user["first_name"] = $row["first_name"];
			$user["last_name"] = $row["last_name"];
			
			$response["user"] = $user;
		}
		
		$response["success"] = 1;
		
		echo json_encode($response);
		
	} else {
		if (isset($_GET["first_name"]) && isset($_GET["last_name"])) {
		
			$first_name = $_GET["first_name"];
			$last_name = $_GET["last_name"];
			
			if (isset($facebook_id )) {
				$query_add = "INSERT INTO MyUser VALUES (NULL, '$facebook_id', NULL, '$first_name', '$last_name')";
			}
			if (isset($google_id)) {
				$query_add = "INSERT INTO MyUser VALUES (NULL, NULL, '$google_id', '$first_name', '$last_name')";
			}
		
			$result = mysql_query($query_add) or die(mysql_error());
			
			$result = mysql_query($query) or die(mysql_error());
			if (mysql_num_rows($result) > 0) {
				if ($row = mysql_fetch_array($result)) {
					$user = array();
					$user["user_id"] = $row["user_id"];
					$user["first_name"] = $row["first_name"];
					$user["last_name"] = $row["last_name"];
					
					$response["user"] = $user;
				}
				
				$response["success"] = 1;
				
				echo json_encode($response);
				
			} else {
				$response["success"] = 0;
				$response["message"] = "No user found";
				
				echo json_encode($response);
			}
		} else {
			// required field is missing
			$response["success"] = 0;
			$response["message"] = "Required field(s) is missing";
		 
			// echoing JSON response
			echo json_encode($response);
		}
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>