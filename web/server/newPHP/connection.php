<?php

$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');

// Check connection
    if ($conn->connect_error) {
        die("Error: Connection failed: " . $conn->connect_error);
    }else{
			$output=[];
	$output[] = ["response"=>"Success"];
			echo(json_encode($output));
		echo "Connection Successful";
	}


?>