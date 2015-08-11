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
<title>籌碼面推薦</title>
<!-- Bootstrap Styles-->
<link href="assets/css/bootstrap.css" rel="stylesheet" />
<!-- FontAwesome Styles-->
<link href="assets/css/font-awesome.css" rel="stylesheet" />
<!-- Morris Chart Styles-->

<!-- Custom Styles-->
<link href="assets/css/custom-styles.css" rel="stylesheet" />
<!-- Google Fonts-->
<link href='http://fonts.googleapis.com/css?family=Open+Sans'
	rel='stylesheet' type='text/css' />
<!-- TABLE STYLES-->
<link href="assets/js/dataTables/dataTables.bootstrap.css"
	rel="stylesheet" />
	<style>
   			.dataTables_wrapper { font-size: 24px; }
   </style>
</head>
<body>
	<div id="wrapper">
		<nav class="navbar navbar-default top-navbar" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".sidebar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.php"><i class="fa fa-comments"></i>
					<strong>CoCo - $tock </strong></a>
			</div>

		</nav>
		<!--/. NAV TOP  -->
		<nav class="navbar-default navbar-side" role="navigation">
			<div class="sidebar-collapse">
				<ul class="nav" id="main-menu">

					<li><a href="index.php"><i class="fa fa-dashboard"></i> 大盤資訊</a></li>

					<li><a href="chart.php"><i class="fa fa-bar-chart-o"></i> 個股資訊</a>
					</li>
					<li><a href="counter.php"><i class="fa fa-table"></i> 籌碼面推薦</a></li>

					<li><a href="fundamentals.php"><i class="fa fa-edit"></i> 基本面推薦</a>
					</li>
					<li><a href="fundpredict.php"><i class="fa fa-edit"></i> 基本面預測</a>
					</li>
					<li><a href="map.php"><i class="fa fa-file"></i> 籌碼地緣分析</a></li>
				</ul>

			</div>

		</nav>
		<!-- /. NAV SIDE  -->
		<div id="page-wrapper">
			<div id="page-inner">
				<div class="row">
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h1 class="page-header">個股進出場訊號</h1>
							</div>
							<div class="panel-body">
								<ul class="nav nav-pills">
									<li class="active"><a href="#tendays" data-toggle="tab">近十日</a></li>
									<li class=""><a href="#history" data-toggle="tab">歷史資料</a>
									</li>									
								</ul>
								<div class="tab-content">
									<div class="tab-pane fade active in" id="tendays">
										<div class="table-responsive">
											<table class="table table-striped table-bordered table-hover"
												id="dataTables-tendays">
												<thead>
													<tr>
														<th>類型</th>
														<th>股票代號-名稱</th>
														<th>進場/放空警示</th>
														<th>進場/放空日期</th>
														<th>進場/放空價格</th>
														<th>出場/回補警示</th>
														<th>出場/回補日期</th>
														<th>出場/回補價格</th>
														<th>投資報酬率</th>
													</tr>
												</thead>
												<tbody>
													<?php
													$query = "SELECT case trans.Cate when 11 then '做多' when 21 then '放空' End as Type,trans.StockId,concat(trans.StockId,'-',com.SampleName) as stockname,fst.date as findstartdate,st.date as startdate,trans.Startprice, fet.date as findenddate,et.Date as enddate,trans.endprice,trans.ROI    FROM threela.transaction as trans   join  company as com on trans.stockid=com.stockid   left join time as st on st.timeid=trans.Starttimeid  left join time as fst on fst.timeid=trans.FindStarttimeid  left join time as et on et.timeid=trans.Endtimeid left join time as fet on fet.timeid=trans.FindEndtimeid where   st.Date >= DATE_ADD(CURDATE(), INTERVAL -10 DAY) or fst.Date >= DATE_ADD(CURDATE(), INTERVAL -10 DAY) or et.Date >=DATE_ADD(CURDATE(), INTERVAL -10 DAY) or fet.Date >= DATE_ADD(CURDATE(), INTERVAL -10 DAY) order by startdate desc,enddate desc";
														
													if ($stmt = $con->prepare ( $query )) {
														$stmt->execute ();
														$stmt->bind_result ( $Type, $StockId, $stockname,$findstartdate, $startdate, $Startprice,$findenddate, $enddate, $endprice, $ROI );
														while ( $stmt->fetch () ) {
															echo "<tr>";
															echo "<td>", $Type, '</td>';
															echo "<td><a href='chart.php?enterId=", $StockId, "'>", $stockname, '<a/></td>';
															echo "<td>", $findstartdate,'</td>';
															echo "<td>", $startdate,'</td>';
																echo "<td>",$Startprice,'</td>';
																echo "<td>",$findenddate,'</td>';
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
									<div class="tab-pane fade" id="history">				
										<div class="table-responsive">
											<table class="table table-striped table-bordered table-hover"
												id="dataTables-history">
												<thead>
													<tr>
														<th>類型</th>
														<th>股票代號-名稱</th>
														<th>進場/放空警示</th>
														<th>進場/放空日期</th>
														<th>進場/放空價格</th>
														<th>出場/回補警示</th>
														<th>出場/回補日期</th>
														<th>出場/回補價格</th>
														<th>投資報酬率</th>
													</tr>
												</thead>
												<tbody>
													<?php
													$query = "SELECT case trans.Cate when 11 then '做多' when 21 then '放空' End as Type,trans.StockId,concat(trans.StockId,'-',com.SampleName) as stockname,fst.date as findstartdate,st.date as startdate,trans.Startprice, fet.date as findenddate,et.Date as enddate,trans.endprice,trans.ROI    FROM threela.transaction as trans   join  company as com on trans.stockid=com.stockid   left join time as st on st.timeid=trans.Starttimeid  left join time as fst on fst.timeid=trans.FindStarttimeid  left join time as et on et.timeid=trans.Endtimeid left join time as fet on fet.timeid=trans.FindEndtimeid where   st.Date < DATE_ADD(CURDATE(), INTERVAL -10 DAY) or fst.Date < DATE_ADD(CURDATE(), INTERVAL -10 DAY) or et.Date < DATE_ADD(CURDATE(), INTERVAL -10 DAY) or fet.Date < DATE_ADD(CURDATE(), INTERVAL -10 DAY) order by startdate desc,enddate desc";
													if ($stmt = $con->prepare ( $query )) {
														$stmt->execute ();
														$stmt->bind_result ( $Type, $StockId, $stockname,$findstartdate, $startdate, $Startprice,$findenddate, $enddate, $endprice, $ROI);
														while ( $stmt->fetch () ) {
															echo "<tr>";
															echo "<td>", $Type, '</td>';
															echo "<td><a href='chart.php?enterId=", $StockId, "'>", $stockname, '<a/></td>';
															echo "<td>", $findstartdate,'</td>';
															echo "<td>", $startdate,'</td>';
																echo "<td>",$Startprice,'</td>';
																echo "<td>",$findenddate,'</td>';
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
                $('#dataTables-tendays').dataTable( {
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
                } );

                $('#dataTables-history').dataTable( {
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
                } );
            });
		</script>
	<!-- Custom Js -->
	<script src="assets/js/custom-scripts.js"></script>


</body>
</html>

<?php 
$con->close();  
?>