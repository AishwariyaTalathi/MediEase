<?php


if (isset($_GET['uuid']) && isset($_GET['gcmtoken'])){
//	echo "hello";
	
				$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');
				
				// Check connection
			if ($conn->connect_error) {
				die("Error: Connection failed: " . $conn->connect_error);
			}
			
			$uuid = $_GET['uuid'];
			$gcmtoken = $_GET['gcmtoken'];
			
			
	$sql = "SELECT uuid FROM gcm where uuid = '$uuid'";
	$result = $conn->query($sql);

	
	if ($result->num_rows > 0) {
				
					$sqlUpdate = "UPDATE gcm SET gcmtoken = '$gcmtoken' WHERE uuid = '$uuid' ";
					
					if ($conn->query($sqlUpdate) === TRUE) {
							 $output[] = array("response"=>"Success");
							echo(json_encode($output));
					} else {
							$output=[];
							$output[] = ["response"=>"Update Error"];
							echo(json_encode($output));
					}
	
	} 
	else{
			

//echo $uuid;
//echo $gcmtoken;
	 	$sql = "INSERT INTO `gcmInfo` (`uuid`,`gcmtoken`) VALUES ('$uuid','$gcmtoken')";

	//		echo $sql;
			
			
		if ($conn->query($sql) === TRUE){
			$output=[];
			$output[] = ["response"=>"Success"];
			echo(json_encode($output));
		} else {
$output=[];
			$output[] = ["response"=>"Failure"];
			echo(json_encode($output));
		}			//$sqlInsert = "INSERT INTO gcmInfo (uuid,gcmtoken) VALUES ('$uuid','$gcmtoken')";
//echo $sqlInsert ;
	/*		
		if ($conn->query($sqlInsert) === TRUE)) {
			$output=[];
			$output[] = ["response"=>"Success"];
			echo(json_encode($output));
		} else {
			//echo "Error: " . $sql . "<br>" . $conn->error;
			$output=[];
			$output[] = ["response"=>"Error in Insert"];
			echo(json_encode($output));
		}
		*/	
	//} 		
}		
}
else{
	
			$output=[];
			$output[] = ["response"=>"Wrong Parameters"];
			echo(json_encode($output));
	
}
	
?>