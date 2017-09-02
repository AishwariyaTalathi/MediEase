<?php

if (isset($_GET['uuid']) && isset($_GET['message']) ){
//	&& isset($_GET['hname'])
	
	//from uuid => get username -> store it 
//	from uuid => get all gcm tokens -> add to arr
	
//	add uname,message,hname => table 
//	 send gcm to to every gcm tokens
	 
			$ids = array();

	 		$conn = new mysqli('localhost', 'root', 'android', 'wikiNew');
				
				// Check connection
			if ($conn->connect_error) {
				die("Error: Connection failed: " . $conn->connect_error);
			}
			
			$uuid = $_GET['uuid'];
			$message = $_GET['message'];
	//		$hname = $_GET['hname'];
$hname = "Mak";		
		
		echo "okayy";
	//	die();
			$sql = "SELECT user_name FROM userInfo where uuid = '$uuid'";
			$result = $conn->query($sql);

	
			if ($result->num_rows > 0) {
	echo "okayy2";
	//	die();
			
		$row = $result->fetch_assoc();
					$uname = $row["user_name"];
					
			}else{
				$output=[];
				$output[] = ["response"=>"UUID not Found"];
				echo(json_encode($output));
				$conn->close();
				die();
			}
			
			$sqlToken = "SELECT gcmtoken FROM gcmInfo where NOT uuid = '$uuid'";
			$resultToken = $conn->query($sqlToken);
			
			if ($resultToken->num_rows > 0) {
					while($rowToken = $resultToken->fetch_assoc()) {
							array_push($ids,$rowToken["gcmtoken"]);
					}
					
			}else{
				$output=[];
				$output[] = ["response"=>"gcmToken Finding Error"];
				echo(json_encode($output));
				$conn->close();
				die();
			}
echo "okayy3";
		//die();
			
			$sqlInsert = "INSERT INTO chat (hname,message, username)
			VALUES ('$hname','$message','$uname')";

		if ($conn->query($sqlInsert) === TRUE){
				$res = array();
				$res['data']['hname'] = $hname;
				$res['data']['uname'] = $uname;
				$res['data']['message'] = $message;
				
				$fields = array(
            'registration_ids' => $ids,
            'data' => $res,
        );
		echo "okayy4";
	//	die();
			
			
				//firebase server url to send the curl request
        $url = 'https://fcm.googleapis.com/fcm/send';
		
		//https://fcm.googleapis.com/fcm/send
		  //building headers for the request
        $headers = array(
            'Authorization: key=AAAAqDVjJbA:APA91bFYaZ7LXV4dtks9EDfWE7La2fxjbk-bfce-99bBh9AQ4LctMYRkHsXynj_N6-ItMdH-IBvkp5sKxPmkf7qDIZSaX2ohzlUWe-VOlmO_Nyx8szW20Tme4xbzD3Wc1pHub4MOF6uQ',
            'Content-Type: application/json'
        );
 
 
 		echo "okayy6";
		//die();

        //Initializing curl to open a connection
        $ch = curl_init();
 
 curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 		echo "okayy5";
		//die();
        //Setting the curl url
        curl_setopt($ch, CURLOPT_URL, $url);
        
        //setting the method as post
        curl_setopt($ch, CURLOPT_POST, true);
 
        //adding headers 
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        //disabling ssl support
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        
        //adding the fields in json format 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
        //finally executing the curl request 
        $result = curl_exec($ch);
        if ($result === FALSE) {
		echo "okayy9";
		die();

		die('Curl failed: ' . curl_error($ch));
        }
 
        //Now close the connection
        curl_close($ch);
 
        //and return the result 
        echo $result;
		
		} 
		else {
			$output=[];
			$output[] = ["response"=>"Insert Data Error"];
			echo(json_encode($output));
			die();
		}
			
	
}
else{
			$output=[];
			$output[] = ["response"=>"Wrong Parameters"];
			echo(json_encode($output));
	}




?>