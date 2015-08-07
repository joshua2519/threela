
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
    $stockname =  isset($_GET["enterId"]) ? $_GET["enterId"] : '台泥';
	$trading = "select (UNIX_TIMESTAMP(t2.Date)+86400)*1000 as TimeStamp, t1.OpenPrice, t1.HighPrice , t1.LowPrice ,
	t1.ClosePrice,t1.BSRateMA20, t1.MA5 ,t1.MA20 , t1.MA60,t1.UBand,t1.LBand 
	FROM tradin2bsday20ma as t1 JOIN time as t2 ON t1.TimeId = t2.timeid where t1.StockId like '".$ee."' 
	or t1.StockId like (select StockId from company where SampleName like '%".$stockname."%')" ;
	$dailyOfStock = $conn->query($trading);

	// ' 
	
	// $jsonData['name'] = $eid;
	while($row = $dailyOfStock->fetch_assoc()){
		$jsonData []= array(
			$row['TimeStamp'],
			$row['OpenPrice'],
			$row['HighPrice'],
			$row['LowPrice'],
			$row['ClosePrice'],
			$row['BSRateMA20'],
			$row['MA5'],
			$row['MA20'],
			$row['MA60'],
			$row['UBand'],
			$row['LBand'],
			);

	}
	header( 'Content-Type: application/json' );
	
	echo json_encode($jsonData, JSON_NUMERIC_CHECK);
	
	$conn->close();

?>
