<?php

if (isset($_GET['uuid'])){

$response = array();
$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');
				
				// Check connection
			if ($conn->connect_error) {
				die("Error: Connection failed: " . $conn->connect_error);
			}
			
			$uuid = $_GET['uuid'];
			
			$sql = "SELECT user_name FROM userInfo where uuid = '$uuid'";
			$result = $conn->query($sql);
			if ($result->num_rows > 0) {
					
			$sqlData = "SELECT hname,message,username FROM chat ORDER BY created_at ASC";
			$resultData = $conn->query($sqlData);
			
			if ($resultData->num_rows > 0) {
					$e = array();
					$e["response"]="Success";
					array_push($response,$e);
					while($rowData = $resultData->fetch_assoc()) {
							array_push($ids,$rowData["gcmtoken"]);
							$res = array();
							$res["hname"]=$rowData["hname"];
							$res["uname"]=$rowData["username"];
							$res["message"]=$rowData["message"];
		
							array_push($response,$res); 
					}
					echo(json_encode($response));
					
			}else{
				$output=[];
				$output[] = ["response"=>"data retreiving error"];
				echo(json_encode($output));
				$conn->close();
				die();
			}

			
					
			}else{
				$output=[];
				$output[] = ["response"=>"UUID not Found"];
				echo(json_encode($output));
				$conn->close();
				die();
			}
			



}else{
			$output=[];
			$output[] = ["response"=>"Wrong Parameters"];
			echo(json_encode($output));
}


?>