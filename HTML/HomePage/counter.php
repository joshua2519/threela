<?php 
$host="10.120.30.4";
$port=3306;
$socket="";
$user="threela";
$password="123456";
$dbname="threela";

$con = new mysqli($host, $user, $password, $dbname, $port, $socket)
or die ('Could not connect to the database server' . mysqli_connect_error());
$query = "SELECT case trans.Cate when 0 then '做多' when 1 then '放空' End as Type,trans.StockId,concat(trans.StockId,'-',com.SampleName) as stockname,st.date as startdate,trans.Startprice, et.Date as enddate,trans.endprice,trans.ROI   FROM threela.transaction as trans  join  company as com on trans.stockid=com.stockid  left join time as st on st.timeid=trans.Starttimeid left join time as et on et.timeid=trans.Endtimeid order by startdate desc,enddate desc";



?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
      <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>籌碼面推薦</title>
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
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h1 class="page-header">個股進出場訊號</h1>                            	
							</div>
							<div class="panel-body">
								<div class="table-responsive">
									<table class="table table-striped table-bordered table-hover" id="dataTables-example">
										<thead>
											<tr>
												<th>類型</th>
												<th>股票代號-名稱</th>
												<th>進場日期</th>
												<th>進場價格</th>
												<th>出場日期</th>
												<th>出場價格</th>
												<th>投資報酬率</th>
											</tr>
										</thead>
										<tbody>
											<?php 
												if ($stmt = $con->prepare($query)) {
													$stmt->execute();
													$stmt->bind_result($Type,$StockId, $stockname, $startdate, $Startprice, $enddate, $endprice, $ROI);
													while ($stmt->fetch()) {
														echo "<tr>";
														echo "<td>",$Type,'</td>';
														echo "<td><a href='chart.php?enterid=",$StockId,"'>",$stockname,'<a/></td>';
														echo "<td>",$startdate,'</td>';
														echo "<td>",$Startprice,'</td>';
														echo "<td>",$enddate,'</td>';
														echo "<td>",$endprice,'</td>';
														echo "<td>",$ROI,'</td>';
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
                    <!--End Advanced Tables -->           
					</div>
				</div>
			</div>
        </div>
           
           
    
	</div>
         <!-- /. PAGE WRAPPER  -->
     <!-- /. WRAPPER  -->
    <!-- JS Scripts-->
    <!-- jQuery Js -->
    <script src="assets/js/jquery-1.10.2.js"></script>
      <!-- Bootstrap Js -->
    <script src="assets/js/bootstrap.min.js"></script>
    <!-- Metis Menu Js -->
    <script src="assets/js/jquery.metisMenu.js"></script>
     <!-- DATA TABLE SCRIPTS -->
    <script src="assets/js/dataTables/jquery.dataTables.js"></script>
    <script src="assets/js/dataTables/dataTables.bootstrap.js"></script>
        <script>
            $(document).ready(function () {
                $('#dataTables-example').dataTable();
            });
		</script>
         <!-- Custom Js -->
    <script src="assets/js/custom-scripts.js"></script>
    
   
</body>
</html>

<?php 
$con->close();  
?>