<?php



	if (isset($_GET['email']) && isset($_GET['password']) && isset($_GET['name']) && isset($_GET['number']) && isset($_GET['loginFlag'])){
		
		$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');

	// Check connection
    if ($conn->connect_error) {
        die("Error: Connection failed: " . $conn->connect_error);
    }

	$email = $_GET['email'];
    //$number = $_GET['number'];
    $password = $_GET['password'];
	$name = $_GET['name'];
	$number = $_GET['number'];
	$login_flag = $_GET['loginFlag'];
	

//	------
	
	$sql = "SELECT user_email FROM userInfo where user_email = '$email'";
	$result = $conn->query($sql);

	
	if ($result->num_rows > 0) {
				$output=[];
			$output[] = ["response"=>"User Already Exists"];
			echo(json_encode($output));
	
	}else{
		
		$uuid = uniqid();
		
		$sql = "INSERT INTO userInfo (uuid,user_email, user_password, user_name, user_number,session_token,login_flag)
			VALUES ('$uuid','$email','$password','$name',$number,1,$login_flag)";

		if ($conn->query($sql) === TRUE){
			$output=[];
			$output[] = ["response"=>"Success","uuid"=>$uuid,"session"=>"1"];
			echo(json_encode($output));
		} else {
			echo "Error: " . $sql . "<br>" . $conn->error;
		}
					
	}
	$conn->close();
	
}
else{
			$output=[];
			$output[] = ["response"=>"HELLO"];
			echo(json_encode($output));
}

?>