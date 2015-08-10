<?php
    $servername = "10.120.30.4";
    $username = "threela";
    $password = "123456";
    $dbname = "threela";
    $conn = new mysqli($servername, $username, $password, $dbname);
    if($conn->connect_error){
        die("Connection failed:" . $conn->connect_error);
    }
    $ee = isset($_GET["stockId"]) ? $_GET["stockId"] :"G";
 
	$trading = "select snk.brokderid,b.Name,b.lat,b.lon,snk.buy_one,snk.sell_one from threela.stocknearbroker as snk join  brokers as b on snk.brokderid=b.brokerid
	 where stockId = '".$ee."'";
	
	$dailyOfStock = $conn->query($trading);
	// ' 
	
	// $jsonData['name'] = $eid;
	while($row = $dailyOfStock->fetch_assoc()){
		$jsonData []= array(
			'brokerid' =>$row['brokderid'],
			'name' =>$row['Name'],
			'lat'=>$row['lat'],
			'lon'=>$row['lon'],
			'buy_one'=>$row['buy_one'],
			'sell_one'=>$row['sell_one'],
			);
	}
	header( 'Content-Type: application/json' );
	
	echo json_encode($jsonData, JSON_NUMERIC_CHECK);
	
	$conn->close();
?>