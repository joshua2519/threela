<?php 
$host="10.120.30.4";
$port=3306;
$socket="";
$user="threela";
$password="123456";
$dbname="threela";

$con = new mysqli($host, $user, $password, $dbname, $port, $socket)
or die ('Could not connect to the database server' . mysqli_connect_error());
$query = "select distinct(stockId) FROM stocknearbroker";


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
			height:800px;
		}
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
            $(document).ready(function () {
                $('#stockmap').dataTable({
					"bPaginate": true,
                    "bLengthChange": false,
                    "bFilter": false,
                    "bSort": true,
                    "bInfo": true,
                    "bAutoWidth": true,
				});
				var pyrmont = new google.maps.LatLng(23.5,120.8);
				map = new google.maps.Map(document.getElementById('map-canvas'), {
				center: pyrmont,
				zoom: 8
				});
            });
			
			function showMap(stockid){
				alert(stockid)
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
                                Map
                            </div>
                            <div class="panel-body">
                                <div class="table-responsive">
                                    <table class="table table-striped table-bordered table-hover" id="stockmap">
                                        <thead>
                                            <tr>
                                                <th>股票代號-名稱</th>
                                                 <th>功能</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                           		<?php 
												if ($stmt = $con->prepare($query)) {
													$stmt->execute();
													$stmt->bind_result($stockId);
													while ($stmt->fetch()) {
														
														echo "<tr>";														
														echo "<td>",$stockId,'</td>';
														echo "<td><input type='button' value='show map' onclick='showMap(",$stockId,")' /></td>";
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
