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
    <title>基本面預測</title>
	<!-- Bootstrap Styles-->
    <link href="assets/css/bootstrap.css" rel="stylesheet" />
     <!-- FontAwesome Styles-->
    <link href="assets/css/font-awesome.css" rel="stylesheet" />
        <!-- Custom Styles-->
    <link href="assets/css/custom-styles.css" rel="stylesheet" />
     <!-- Google Fonts-->
   <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
   <style>
	   .dataTables_wrapper { font-size: 24px; }
	   .dataTables_filter, .dataTables_info { display: none; }
   </style>
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
                                	<h1 class="page-header">個股預測</h1>   
                            </div> 
                            <div class="panel-body">
                            <div class="row">
                            		<div class="col-lg-10">
                            		 <div class="form-group form-inline">                            		
	                            			<div class="col-xs-3" data-column="0">
												  <label for="stockSearch">股票 : </label>
												  <input class="form-control column_filter" id="stockSearch" type="text">
											</div>
											<div class="col-xs-3">
												  <label for="yearSearch" data-column="1">年 : </label>
												  <input class="form-control column_filter" id="yearSearch" type="text">
											</div>
											<div class="col-xs-3">
												  <label for="seasonSearch" data-column="2">季 : </label>
												  <input class="form-control column_filter" id="seasonSearch" type="text">
											</div>
										</div>
                            		</div>
                            	</div>
                                <div class="table-responsive">
                                    <table class="table table-striped table-bordered table-hover" id="tableStockPredict">
                                        <thead>
                                            <tr>
                                                <th>股票代號-名稱</th>
                                                <th>年份</th>
                                                <th>季</th>
                                                <th>起算日</th>
                                                <th>30日後</th>
                                                <th>60日後</th>
                                                <th>180日後</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                           		<?php
												$query = "SELECT sp.StockId,concat(sp.StockId,'-',com.SampleName) as stockname,st.Year,st.Season,st.Date, sp.PredictMon1,sp.PredictMon2,sp.PredictMon3 FROM threela.stockpredict as sp join  company as com on sp.stockid=com.stockid   left join time as st on st.timeid=sp.TimeId";
                                           		                                           		 
												if ($stmt = $con->prepare($query)) {
													$stmt->execute();
													$stmt->bind_result($Stockid, $stockname, $Year, $Season,$Date, $PredictMon1, $PredictMon2, $PredictMon3);
													while ($stmt->fetch()) {
														echo "<tr>";														
														echo "<td><a href='chart.php?enterId=",$Stockid,"'>",$stockname,'<a/></td>';
														echo "<td>",$Year,"</td>";
														echo "<td>",$Season,"</td>";	
														echo "<td>",$Date,"</td>";
														if ($PredictMon1=='1'){
															echo  "<td><img src='assets/Stock-Index-Down-icon_24.png'  title='跌' /></td>";
														}else{
															echo  "<td><img src='assets/Stock-Index-Up-icon_24.png'  title='漲' /></td>";
														}													
														if ($PredictMon2=='1'){
															echo  "<td><img src='assets/Stock-Index-Down-icon_24.png' title='跌' /></td>";
														}else{
															echo  "<td><img src='assets/Stock-Index-Up-icon_24.png'  title='漲' /></td>";
														}
														if ($PredictMon3=='1'){
															echo  "<td><img src='assets/Stock-Index-Down-icon_24.png' title='跌' /></td>";
														}else{
															echo  "<td><img src='assets/Stock-Index-Up-icon_24.png'  title='漲'  /></td>";
														}														
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
               
			</div>
            <!-- /. PAGE INNER  -->
        </div>
        <!-- /. PAGE WRAPPER  -->
    </div>
    <!-- /. WRAPPER  -->
    <!-- JS Scripts-->
    <!-- jQuery Js -->
    <script src="assets/js/jquery-1.10.2.js"></script>
      <!-- Bootstrap Js -->
    <script src="assets/js/bootstrap.min.js"></script>
    <!-- Metis Menu Js -->
    <script src="assets/js/jquery.metisMenu.js"></script>
    <script src="assets/js/dataTables/jquery.dataTables.js"></script>
    <script src="assets/js/dataTables/dataTables.bootstrap.js"></script>
     	<script>
            $(document).ready(function () {
                $('#tableStockPredict').dataTable( {
                    "bPaginate": true,
                    "bLengthChange": false,
                    "bFilter": true,
                    "bSort": true,
                    "bInfo": true,                   
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

                $('input.column_filter').on( 'keyup click', function () {                    
                    filterColumn( $(this).attr('id'),$(this).parents('div').attr('data-column') );
                } );               
            });

            function filterColumn (inputid,colindex) {
                $('#tableStockPredict').DataTable().column(colindex).search(
                    $('#'+inputid).val(),true,true
                ).draw();
            }
            
		</script>
     <!-- Custom Js -->
    <script src="assets/js/custom-scripts.js"></script>
</body>
</html>
<?php 
$con->close();  
?>