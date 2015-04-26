<?php
// array for JSON response
$response = array();
 
if(isset($_POST['user_id'])) {
	
	$user_id = $_POST['user_id'];
	
	$response = array();
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());
	
	$result = mysql_query("SELECT * FROM Category") or die(mysql_error());
	if (mysql_num_rows($result) > 0) {
		while ($row = mysql_fetch_array($result)) {
			$category_id = $row["category_id"];
			if (isset($_POST["$category_id"])) {
				$is_favorite = $_POST["$category_id"];
				if ($is_favorite == 'Y') {
					$query = "INSERT INTO Favorites(subscriber, category) VALUES ('$user_id', $category_id)";
				} else {
					$query = "DELETE FROM Favorites WHERE subscriber = '$user_id' AND category = $category_id";
				}
				mysql_query($query);
			}
		}
	}
	
	$response["success"] = 1;
	$response["message"] = "Favorite successfully created.";
	
	echo json_encode($response);
	
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>