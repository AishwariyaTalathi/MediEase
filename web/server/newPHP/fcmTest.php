<?php
	if (isset($_GET['token']) ){
	
		$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');

	// Check connection
    if ($conn->connect_error) {
        die("Error: Connection failed: " . $conn->connect_error);
    }

	$token = $_GET['token'];
	
		$sql = "INSERT INTO gcmInfo (gcmToken)
			VALUES ('$token')";

		if ($conn->query($sql) === TRUE) {
			$output=[];
			$output[] = ["response"=>"Success"];
			echo(json_encode($output));
		} else {
$output=[];
			$output[] = ["response"=>"Failure"];
			echo(json_encode($output));
		}
	}else{
		$output=[];
			$output[] = ["response"=>"Wrong Params"];
			echo(json_encode($output));
		
	}
	
	
	
	?>