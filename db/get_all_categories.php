﻿<?php

/*
 * Following code will get all categories
 */

// array for JSON response
$response = array();

// include db connect class
require_once(dirname(__FILE__).'/db_connect.php');

// connecting to db
$db = new DB_CONNECT();

// get all categories from categories table
mysql_query("SET NAMES 'utf8'") or die(mysql_error());
$result = mysql_query("SELECT * FROM Category") or die(mysql_error());

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
?>