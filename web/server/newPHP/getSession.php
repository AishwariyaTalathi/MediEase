<?php

if (isset($_GET['uuid']) ){

$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');

	// Check connection
    if ($conn->connect_error) {
        die("Error: Connection failed: " . $conn->connect_error);
    }
	
		 $uuid = $_GET['uuid'];
		 
		 $sql = "SELECT uuid,session_token FROM userInfo where uuid = '$uuid'";
		 $result = $conn->query($sql);
		 
		 if ($result->num_rows > 0) {
			$row = $result->fetch_assoc(); 
				$uuid = $row["uuid"];
			 $sessionID = $row["session_token"];
			 $output[] = array("response"=>"Success","uuid"=>$uuid,"session"=>$sessionID);
			 echo(json_encode($output));
		}
		else{
		 	$output[] = array("response"=>"wrong uuid");
			echo(json_encode($output));
		}
}
else{
			$output=[];
			$output[] = ["response"=>"Wrong Parameters"];
			echo(json_encode($output));	
}


?>