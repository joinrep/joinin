<?php
// array for JSON response
$response = array();
 
if(isset($_POST['user_id']) && isset($_POST['category_id']) && isset($_POST['is_favorite'])) {
	
	$user_id = $_POST['user_id'];
	$category_id = $_POST['category_id'];
	$is_favorite = $_POST['is_favorite'];
	
	$response = array();
	require_once(dirname(__FILE__).'/db_connect.php');
	$db = new DB_CONNECT();
	mysql_query("SET NAMES 'utf8'") or die(mysql_error());
	

	if ($is_favorite == 'Y') {
		$query = "INSERT INTO Favorites(subscriber, category) VALUES ('$user_id', $category_id)";
	} else {
		$query = "DELETE FROM Favorites WHERE subscriber = '$user_id' AND category = $category_id";
	}

    $result = mysql_query($query);
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Favorite successfully created. Query: ".$query;
 
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