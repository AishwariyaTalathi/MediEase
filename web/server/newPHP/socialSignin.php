<?php


	if (isset($_GET['email']) && isset($_GET['name'])&& isset($_GET['loginFlag'])){
			
			$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');

			// Check connection
			if ($conn->connect_error) {
				die("Error: Connection failed: " . $conn->connect_error);
			}

			$email = $_GET['email'];
			//$number = $_GET['number'];
			$name = $_GET['name'];
			$login_flag = $_GET['loginFlag'];
			
			$sql = "SELECT user_email,uuid,login_flag FROM userInfo where user_email = '$email'";
			$result = $conn->query($sql);

			if ($result->num_rows > 0) {
				$row = $result->fetch_assoc(); 
				$user_email = $row["user_email"];
				$uuid = $row["uuid"];
				$logFlag = $row["login_flag"];
				
						
				if($login_flag == $logFlag){
					
					$sqlUpdate = "UPDATE userInfo SET session_token = 1 , login_flag = $login_flag WHERE user_email = 
					'$email' AND login_flag = $login_flag";
					
					if ($conn->query($sqlUpdate) === TRUE) {
							 $output[] = array("response"=>"Success","uuid"=>$uuid,"session"=>"1");
							echo(json_encode($output));
					}else{
							$output=[];
							$output[] = ["response"=>"Update Error"];
							echo(json_encode($output));						
					}
				}else{
								$output=[];
							$output[] = ["response"=>"Different Account"];
							echo(json_encode($output));
					
				}
			}else{
				//email id doesnt exist
				$uuid = uniqid();
				
				$sqlInsert = "INSERT INTO userInfo (uuid,user_email, user_name,session_token,login_flag)
				VALUES ('$uuid','$email','$name',1,$login_flag)";

				if ($conn->query($sqlInsert) === TRUE) {
					$output=[];
					$output[] = ["response"=>"Success","uuid"=>$uuid,"session"=>"1"];
					echo(json_encode($output));
				} else {
					//echo "Error: " . $sql . "<br>" . $conn->error;
				}
				
				
			}
	}else{
			$output=[];
			$output[] = ["response"=>"Wrong Parameters"];
			echo(json_encode($output));
	}



?>