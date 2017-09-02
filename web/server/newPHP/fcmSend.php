<?php

if (isset($_GET['token'])){

$token = $_GET['token'];
//$token1 = $_GET['token1'];

$ids= array($token);
//array_push($ids,$token);
//array_push($ids,$token1);

$res = array();
        $res['data']['title'] = "MAKRAND";
        $res['data']['message'] = "hope";

		
		$fields = array(
            'registration_ids' => $ids,
            'data' => $res,
        );


		//firebase server url to send the curl request
        $url = 'https://fcm.googleapis.com/fcm/send';
		
		//https://fcm.googleapis.com/fcm/send
		  //building headers for the request
        $headers = array(
            'Authorization: key=AAAAm_MZN0A:APA91bFEmZ8Ta8wtZopFldYII1WCrts8EYqycUtzu0YqbiRsKFztwNSV-srlPFIg_zm9DtcFPpJ_AIK9dJADGH1Mz3PJBTBR2g5APTIt7H04YIXYe2VZDFAAZPkv-hyYXZhhJyCm2FAF ',
            'Content-Type: application/json'
        );
 
        //Initializing curl to open a connection
        $ch = curl_init();
 
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
            die('Curl failed: ' . curl_error($ch));
        }
 
        //Now close the connection
        curl_close($ch);
 
        //and return the result 
        echo $result;
		


}else{
	
	
}



?>