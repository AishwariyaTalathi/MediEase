<?php

if (isset($_GET['id1']) && isset($_GET['id2'])&& isset($_GET['id3']) && isset($_GET['id4'])&& isset($_GET['id5'])){
	
			
			
			$places = array($_GET['id1'],$_GET['id2'],$_GET['id3'],$_GET['id4'],$_GET['id5']);
			//echo (json_encode($places));
	//	$places = array("ChIJJzEPrukz3YARwwRn9p-4lCo","ChIJwTvKDWAu3YARauGlk3CBWkc","ChIJ7XEJze8z3YARoT9nVMuq5BY","ChIJ51dGTGMy3YARu9jGaHFI5Zg","ChIJg4nE3e4z3YARBfVOBZgXeKQ");
			$ratings = array();
$names = array();
$long = "";
$lat = "";
$longitude = array();
$latitude =array();
$index=0;
$address = array();
$response = array();

foreach ($places as $place){
	
	global $index;
	$url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=$place&key=AIzaSyDIBE77Ok17o2r8ZjPFkfFpJgJ00zoRy78";
	$json = file_get_contents($url);
	$json_data = json_decode($json, true);
	
	global $long;
 	$long = $json_data['result']['geometry']['location']['lng'];
	array_push($longitude,$long);
	$lat =  $json_data['result']['geometry']['location']['lat'];
	array_push($latitude,$lat);
	$name = $json_data['result']['name'];
	array_push($names,$name);
	$location1 = $json_data['result']['address_components'][1]['long_name'];
	$location2 = $json_data['result']['address_components'][2]['long_name'];
	$location = $location1 . ', ' .  $location2;
	array_push($address,$location);

	$place_reviews = array();
	$count = 0;
	foreach ($json_data['result']['reviews'] as $result) {
		$review = preg_replace("/[^0-9a-zA-Z ]/", "", $result['text']);
		array_push($place_reviews,$review);
		$ans = shell_exec('/usr/bin/python2.7 test.py ' . escapeshellarg($review));

		if ($ans == null)
		echo "Error";
		
		if ($ans == 1){
		global $count;
		$count++;
		}			
	}
	$var = $ratings[$index];
	$ratings[$index]=$count;
	
	$index++;
}
$e = array();
	$e["response"]="Success";
	array_push($response,$e);

arsort($ratings);
foreach ($ratings as $key => $value) {
	$res = array();
	$res["place_id"]=$places[$key];
	$res["hname"]=$names[$key];
	$res["lat"]=$latitude[$key];
	$res["lng"]=$longitude[$key];
	$res["addr"]=$address[$key];
	
	array_push($response,$res); 
}
echo(json_encode($response));

/*$r = array("success");

	$fields= array(
		"response" => $r,
		"data" => $response
	)		
			
		echo(json_encode($fields));	
			
	*/		
			
			
}
else{
	
	$output=[];
			$output[] = ["response"=>"Wrong Parameters"];
			echo(json_encode($output));
}		
?>