<?php

/* $res = array();
        $res['data']['title'] = "TITLE";
        $res['data']['message'] = "MESSAGE";
        $res['data']['image'] = "IMAGE";
		
		echo(json_encode($res));

 $fields = array(
            'registration_ids' => "ID",
            'data' => $res,
        );		
		
		echo(json_encode($fields));
		
		
		*/
		
		$res = array();
		$res["name"]="name";
		$res["hname"]="hname";
		$res["lat"]="lat";
		
		$res1 = array();
		$res1["name"]="name";
		$res1["hname"]="hname";
		$res1["lat"]="lat";
		
		$fields = array();
		array_push($fields,$res);
		array_push($fields,$res1);
				echo(json_encode($fields));
		
		
		
		?>