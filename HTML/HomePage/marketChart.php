<?php
    $servername = "10.120.30.4";
    $username = "threela";
    $password = "123456";
    $dbname = "threela";

    $conn = new mysqli($servername, $username, $password, $dbname);
    if($conn->connect_error){
        die("Connection failed:" . $conn->connect_error);
    }
    
	$trading = "select (UNIX_TIMESTAMP(t2.Date)+86400)*1000 as TimeStamp, d.OpenPrice, d.HighPrice , d.LowPrice ,d.ClosePrice , d.TAPI
FROM `index` as d JOIN time as t2 ON d.TimeId = t2.timeid " ;
	$dailyOfStock = $conn->query($trading);

	
	// $jsonData['name'] = $eid;
	while($row = $dailyOfStock->fetch_assoc()){
		$jsonData []= array(
			$row['TimeStamp'],
			$row['OpenPrice'],
			$row['HighPrice'],
			$row['LowPrice'],
			$row['ClosePrice'],
			$row['TAPI']
			);

		// $row_arry['TimeStamp']= $row['TimeStamp'];
		// $row_arry['OpenPrice']= $row['OpenPrice'];
		// array_push($jsonData, $row_arry);
	}
	header( 'Content-Type: application/json' );
	
	echo json_encode($jsonData, JSON_NUMERIC_CHECK);
	
	$conn->close();

?>
