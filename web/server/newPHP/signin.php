<?php

	if (isset($_GET['email']) && isset($_GET['password'])&& isset($_GET['loginFlag'])){
	
				$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');
				
				// Check connection
			if ($conn->connect_error) {
				die("Error: Connection failed: " . $conn->connect_error);
			}
			
			$email = $_GET['email'];
			$password = $_GET['password'];
			$login_flag = $_GET['loginFlag'];
			
			$sql = "SELECT uuid,user_email FROM userInfo where user_email = '$email' AND user_password = '$password'";
			$result = $conn->query($sql);

	
			if ($result->num_rows > 0) {
					$row = $result->fetch_assoc();
					$uuid = $row["uuid"];
					$emailID = $row["user_email"];
					
					$sqlUpdate = "UPDATE userInfo SET session_token = 1 , login_flag = $login_flag WHERE user_email = 
					'$email'";
					
					if ($conn->query($sqlUpdate) === TRUE) {
							 $output[] = array("response"=>"Success","uuid"=>$uuid,"session"=>"1");
							echo(json_encode($output));
					} else {
							$output=[];
							$output[] = ["response"=>"Update Error"];
							echo(json_encode($output));
					}
			}else{
				$output=[];
				$output[] = ["response"=>"Error"];
				echo(json_encode($output));
				$conn->close();
			}		
	}else{
			$output=[];
			$output[] = ["response"=>"Wrong Parameters"];
			echo(json_encode($output));
	}
?>