<?php

/*
 * Following code will get all categories
 */

// array for JSON response
$response = array();

if (isset($_GET["user_id"])) {
    $user_id = $_GET['user_id'];
		
	// include db connect class
	require_once(dirname(__FILE__).'/db_connect.php');

	// connecting to db
	$db = new DB_CONNECT();

	// get all categories from categories table
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());
	$result = mysql_query("SELECT *, (SELECT 'Y' FROM Favorites F WHERE F.category = C.category_id AND F.subscriber = $user_id) subscriber FROM Category C ORDER BY category_name") or die(mysql_error());

	// check for empty result
	if (mysql_num_rows($result) > 0) {
		// looping through all results
		// products node
		$response["categories"] = array();

		while ($row = mysql_fetch_array($result)) {
			// temp user array
			$category = array();
			$category["categoryId"] = $row["category_id"];
			$category["name"] = $row["category_name"];
			$category["icon_path"] = $row["icon_path"];
			$category["category_color"] = $row["category_color"]; 
			if ($row["subscriber"]) {
				$category["is_favorite"] = true;
			} else {
				$category["is_favorite"] = false;
			}

			// push single category into final response array
			array_push($response["categories"], $category);
		}
		// success
		$response["success"] = 1;

		// echoing JSON response
		echo json_encode($response);

	} else {
		// no products found
		$response["success"] = 0;
		$response["message"] = "No categories found";

		// echo no users JSON
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