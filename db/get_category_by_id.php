<?php
 
/*
 * Following code will get single product details
 * A product is identified by category id (category_id)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once(dirname(__FILE__). '/db_connect.php');
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["category_id"])) {
    $category_id = $_GET['category_id'];
 
    // get a product from products table
    $result = mysql_query("SELECT *FROM Category WHERE category_id = $category_id");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
			// user node
            $result = mysql_fetch_array($result);
			
			$response["categories"] = array();
			
			 // temp user array
			$category = array();
			$category["category_id"] = $row["category_id"];
			$category["category_name"] = $row["category_name"];
			$category["category_icon"] = $row["category_icon"];
	 
			// push category into final response array
			array_push($response["categories"], $category);
			
            // success
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No category found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No category found";
 
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