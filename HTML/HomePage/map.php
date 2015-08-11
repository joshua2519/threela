<?php 
$host="10.120.30.4";
$port=3306;
$socket="";
$user="threela";
$password="123456";
$dbname="threela";

$con = new mysqli($host, $user, $password, $dbname, $port, $socket)
or die ('Could not connect to the database server' . mysqli_connect_error());


?>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
      <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>籌碼地緣分析</title>
	<!-- Bootstrap Styles-->
    <link href="assets/css/bootstrap.css" rel="stylesheet" />
     <!-- FontAwesome Styles-->
    <link href="assets/css/font-awesome.css" rel="stylesheet" />
     <!-- Morris Chart Styles-->
   
        <!-- Custom Styles-->
    <link href="assets/css/custom-styles.css" rel="stylesheet" />
     <!-- Google Fonts-->
   <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
     <!-- TABLE STYLES-->
    <link href="assets/js/dataTables/dataTables.bootstrap.css" rel="stylesheet" />
	
	<style>
		#map-canvas{
			width:100%;
			height:700px;
		}
		.dataTables_wrapper { font-size: 16px; }
	</style>
	    <script src="assets/js/jquery-1.10.2.js"></script>
      <!-- Bootstrap Js -->
    <script src="assets/js/bootstrap.min.js"></script>
    <!-- Metis Menu Js -->
    <script src="assets/js/jquery.metisMenu.js"></script>
     <!-- DATA TABLE SCRIPTS -->
    <script src="assets/js/dataTables/jquery.dataTables.js"></script>
    <script src="assets/js/dataTables/dataTables.bootstrap.js"></script>
	<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=places"></script>
        <script>
        	addedBrokers=[];
            $(document).ready(function () {
                $('#stockmap').dataTable({
					"bPaginate": true,
                    "bLengthChange": false,
                    "bFilter": false,
                    "bSort": true,
                    "bInfo": true,
                    "bAutoWidth": true,
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_",                        
                        "oPaginate": {
                            "sPrevious": "前一頁",
                            "sNext": "後一頁"
                          }                       
                    }
				});
				var pyrmont = new google.maps.LatLng(23.6,120.8);
				map = new google.maps.Map(document.getElementById('map-canvas'), {
				center: pyrmont,
				zoom: 8
				});
				infowindow = new google.maps.InfoWindow();
				commarker = new google.maps.Marker();
					    // Add the circle for this city to the map.
				  comCircle = new google.maps.Circle();
            });
			
			function showMap(stockid,stockname,address,lat,lon){
				 map.setCenter(new google.maps.LatLng(lat,lon));
				 map.setZoom(13)
				 
				 commarker.setMap(map);
				 commarker.setPosition(new google.maps.LatLng(lat,lon))
				 commarker.setIcon("assets/office-building.png");
			
				  
				  google.maps.event.addListener(commarker, 'click', function() {
					var infocontent="<label>"+stockname+"</lable></br>地址:<label>"+address+"</label>";
				    infowindow.setContent(infocontent);
				    infowindow.open(map, this);
				  });
				  var comCircleOptions = {
					      strokeColor: '#FF0000',
					      strokeOpacity: 0.8,
					      strokeWeight: 2,
					      fillColor: '#FFFF00',
					      fillOpacity: 0.35,
					      map: map,
					      center: new google.maps.LatLng(lat,lon),
					      radius: 6000
					    };

				  comCircle.setOptions(comCircleOptions);
				  showBrokers(stockid);

// 				  var comSymbol={
// 						    path:  google.maps.SymbolPath.CIRCLE,
// 						    fillColor: 'red',
// 						    fillOpacity: 1,
// 						    scale: 10,
// 						    strokeColor: 'black',
// 						    strokeWeight: 1
// 						  };
			}

			function showBrokers(stockid){
				//var brokerjson=[{"brokerid":"9202","lat":24.147796,"lon":120.67415,"buy_one":0,"sell_one":203,"anoma":0.1845},{"brokerid":"921A","lat":24.153671,"lon":120.677616,"buy_one":1506,"sell_one":0,"anoma":0.09}];
				//var obj = JSON.parse(brokerjson);
				$.getJSON("http://10.120.30.13/makeForMap.php?stockId="+stockid, function( data ) {
					clearBrokers();
					$.each(data,function(index,broker) {
						var brokermarker = new google.maps.Marker({
						    map: map,
						    position: new google.maps.LatLng(broker["lat"],broker["lon"])
						  });
						addedBrokers.push(brokermarker);	
						  if  (broker["buy_one"] > broker["sell_one"]){

			 				  var BrokSymbol={
							    path:  google.maps.SymbolPath.CIRCLE,
							    fillColor: 'red',
							    fillOpacity: 1,
							    scale: 10,
							    strokeColor: 'black',
	 						    strokeWeight: 1
							  };
							  brokermarker.setIcon(BrokSymbol);
							}else{
								var BrokSymbol={
									    path:  google.maps.SymbolPath.CIRCLE,
									    fillColor: 'lightgreen',
									    fillOpacity: 1,
									    scale: 10,
									    strokeColor: 'black',
			 						    strokeWeight: 1
									  };
									  brokermarker.setIcon(BrokSymbol);

							}
						  google.maps.event.addListener(brokermarker, 'click', function() {
								var infocontent="<label>"+broker["brokerid"]+"-"+broker["name"]+"</lable></br>" +
										         "買張:<label>"+broker["buy_one"]+"</label></br>"+
										         "賣張:<label>"+broker["sell_one"]+"</label></br>" ;
							    infowindow.setContent(infocontent);
							    infowindow.open(map, this);
							  });	
						  		  
					});
				});
				

			}
			
			function clearBrokers() {
				  for (var i = 0; i < addedBrokers.length; i++) {
					  //alert(addedBrokers[i]);
					  addedBrokers[i].setMap(null);
				  }
				  addedBrokers=[];
			}
			
		</script>
</head>
<body>
    <div id="wrapper">
        <nav class="navbar navbar-default top-navbar" role="navigation">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".sidebar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.php"><i class="fa fa-comments"></i> <strong>CoCo - $tock </strong></a>
            </div>

        </nav>
        <!--/. NAV TOP  -->
        <nav class="navbar-default navbar-side" role="navigation">
            <div class="sidebar-collapse">
                <ul class="nav" id="main-menu">

                     <li>
                        <a href="index.php"><i class="fa fa-dashboard"></i> 大盤資訊</a>
                    </li>
                    
					<li>
                        <a href="chart.php"><i class="fa fa-bar-chart-o"></i> 個股資訊</a>
                    </li>

                    <li>
                        <a href="counter.php"><i class="fa fa-table"></i> 籌碼面推薦</a>
                    </li>
               
                    <li>
                        <a  href="fundamentals.php"><i class="fa fa-edit"></i> 基本面推薦</a>
                    </li>
                    <li>
                        <a  href="fundpredict.php"><i class="fa fa-edit"></i> 基本面預測</a>
                    </li>
                    <li>
                        <a href="map.php"><i class="fa fa-file"></i> 籌碼地緣分析</a>
                    </li>

                </ul>

            </div>

        </nav>
        <!-- /. NAV SIDE  -->
        <div id="page-wrapper" >
            <div id="page-inner">
                <div class="row">
                    <div class="col-md-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                             鄰近劵商買賣異常公司列表
                            </div>
                            <div class="panel-body">
                                <div class="table-responsive">
                                    <table class="table table-striped table-bordered table-hover" id="stockmap">
                                        <thead>
                                            <tr>
                                                <th><h3>股票代號-名稱</h3></th>
                                                 <th><h3>功能</h3></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                           		<?php 
                                           		$query = "SELECT distinct(snb.stockid),concat(snb.StockId,'-',c.SampleName) as stockname,c.Address,snb.lat_d,snb.lon_d FROM threela.stocknearbroker as snb join company as c on snb.stockid=c.stockid";
                                           		 
												if ($stmt = $con->prepare($query)) {
													$stmt->execute();
													$stmt->bind_result($stockId,$stockname,$Address,$lat_d,$lon_d);
													while ($stmt->fetch()) {
														
														echo "<tr>";														
														echo "<td><h3><a href='chart.php?enterId=",$stockId,"'>",$stockname,'<a/></h3></td>';
														echo "<td><input type='button' class='btn btn-info' value='show map' onclick=showMap('$stockId','$stockname','$Address',$lat_d,$lon_d) /></td>";
														#echo "<td>",$Season,'</td>';
														#echo "<td>",$individual_ROI,'</td>';
														#echo "<td>",$y_ROI,'</td>';
														#echo "<td>",$i_yROI,'</td>';
														echo "</tr>";                                    	
													}
													$stmt->close();
												}												
											?> 

                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
					<div class="col-xs-8">
						<div class="panel panel-default">
							<div class="panel-body">
								<div id="map-canvas"></div>
							</div>
						</div>
					</div>
                </div>
            </div>
        </div>
           
    
	</div> 
    
   
</body>
</html>
<?php 
$con->close();  
?>