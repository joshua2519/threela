<?php
    $servername = "10.120.30.4";
    $username = "threela";
    $password = "123456";
    $dbname = "threela";

    $conn = new mysqli($servername, $username, $password, $dbname);
    if($conn->connect_error){
        die("Connection failed:" . $conn->connect_error);
    }
    $ee = isset($_GET["enterId"]) ? $_GET["enterId"] : 1101;
	$trading = "select UNIX_TIMESTAMP(t2.Date)*1000 as TimeStamp, t1.OpenPrice, t1.HighPrice , t1.LowPrice ,t1.ClosePrice,t1.Volume 
	FROM trading as t1 JOIN time as t2 ON t1.TimeId = t2.timeid where t1.StockId = '".$ee."'" ;
	$dailyOfStock = $conn->query($trading);

	
	// $jsonData['name'] = $eid;
	while($row = $dailyOfStock->fetch_assoc()){
		$jsonData []= array(
			$row['TimeStamp'],
			$row['OpenPrice'],
			$row['HighPrice'],
			$row['LowPrice'],
			$row['ClosePrice'],
			$row['Volume']
			);

		// $row_arry['TimeStamp']= $row['TimeStamp'];
		// $row_arry['OpenPrice']= $row['OpenPrice'];
		// array_push($jsonData, $row_arry);
	}
	header( 'Content-Type: application/json' );
	
	echo json_encode($jsonData, JSON_NUMERIC_CHECK);
	
	$conn->close();

?>
