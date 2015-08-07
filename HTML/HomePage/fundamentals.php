<?php 
$host="10.120.30.4";
$port=3306;
$socket="";
$user="threela";
$password="123456";
$dbname="threela";

$con = new mysqli($host, $user, $password, $dbname, $port, $socket)
or die ('Could not connect to the database server' . mysqli_connect_error());
$query = "SELECT sr.Stockid,concat(sr.StockId,'-',com.SampleName) as stockname,Year,Season,individual_ROI,y_ROI,i_yROI  FROM threela.stockrecomd as sr join  company as com on sr.stockid=com.stockid where year= ? and season= ?  order by year desc,season";
?>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>基本面推薦</title>
	<!-- Bootstrap Styles-->
    <link href="assets/css/bootstrap.css" rel="stylesheet" />
     <!-- FontAwesome Styles-->
    <link href="assets/css/font-awesome.css" rel="stylesheet" />
        <!-- Custom Styles-->
    <link href="assets/css/custom-styles.css" rel="stylesheet" />
     <!-- Google Fonts-->
   <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
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
                                	<h1 class="page-header">基本面選股</h1>
                                	   
                            </div>
                       
                            <div class="panel-body">
                          		<div class="row">
                            		<div class="col-lg-6">
                            			<form role="form" method="post" action="<?php echo $_SERVER["PHP_SELF"]; ?>">
                            				<div class="controls form-inline">
                                            	 <label>年度: </label>
                                            	<select id='year' name='year'>
                                                	<option value='2015'  <?php if (isset($_POST['year']) and $_POST['year']==2015) {echo "selected='selected'"; } ?>>2015</option>
                                                	<option  value='2014' <?php if (isset($_POST['year']) and $_POST['year']==2014) {echo "selected='selected'"; } ?>>2014</option>
                                               	 	<option  value='2013' <?php if (isset($_POST['year']) and $_POST['year']==2013) {echo "selected='selected'"; } ?>>2013</option>
                                                	<option  value='2012' <?php if (isset($_POST['year']) and $_POST['year']==2012) {echo "selected='selected'"; } ?>>2012</option>
                                                	<option  value='2011' <?php if (isset($_POST['year']) and $_POST['year']==2011) {echo "selected='selected'"; } ?>>2011</option>
                                                	<option  value='2010' <?php if (isset($_POST['year']) and $_POST['year']==2010) {echo "selected='selected'"; } ?>>2010</option>
                                                	<option  value='2009' <?php if (isset($_POST['year']) and $_POST['year']==2009) {echo "selected='selected'"; } ?>>2009</option>
                                                	<option  value='2008' <?php if (isset($_POST['year']) and $_POST['year']==2008) {echo "selected='selected'"; } ?>>2008</option>
                                                	<option  value='2007' <?php if (isset($_POST['year']) and $_POST['year']==2007) {echo "selected='selected'"; } ?>>2007</option>
                                            	</select>                                         	
                                            	 <label> 季: </label>
                                            	<select id='season' name='season'>
                                                	<option value='1' <?php if (isset($_POST['season']) and $_POST['season']=='1') {echo "selected='selected'"; } ?>>1</option>
                                                	<option value='3' <?php if (isset($_POST['season']) and $_POST['season']=='3') {echo "selected='selected'"; } ?>>3</option>                                             
                                            	</select>
                                            	<button type="submit" class="btn btn-default btn-lg">查詢</button>
                                            	
                                        	</div>
                            			</form>
                            		</div>
                            	</div>
                            	 
                                <div class="table-responsive">
                                    <table class="table table-striped table-bordered table-hover" id="stockRecommdTable">
                                        <thead>
                                            <tr>
                                                <th>股票代號-名稱</th>
                                                <th>年份</th>
                                                <th>季</th>
                                                <th>個股報酬率</th>
                                                <th>年報酬率</th>
                                                <th>大盤報酬率</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                           		<?php                                            		
												if ($stmt = $con->prepare($query)) {
													
													if (isset($_POST["year"]) and isset($_POST["season"])){
														$stmt->bind_param('ii', $_POST["year"], $_POST["season"]);
													
													}else{
														$stmt->bind_param('ii', $first=2015,$second=1);
													}
																										
													$stmt->execute();
													$stmt->bind_result($Stockid, $stockname, $Year, $Season, $individual_ROI, $y_ROI, $i_yROI);
													while ($stmt->fetch()) {
														echo "<tr>";														
														echo "<td><a href='chart.php?enterid=",$StockId,"'>",$stockname,'<a/></td>';
														echo "<td>",$Year,'</td>';
														echo "<td>",$Season,'</td>';
														echo "<td>",$individual_ROI,'</td>';
														echo "<td>",$y_ROI,'</td>';
														echo "<td>",$i_yROI,'</td>';
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
      <!-- DATA TABLE SCRIPTS -->
    <script src="assets/js/dataTables/jquery.dataTables.js"></script>
    <script src="assets/js/dataTables/dataTables.bootstrap.js"></script>
        <script>
            $(document).ready(function () {
                //$('#stockRecommdTable').dataTable();
            });
		</script>
         <!-- Custom Js -->
    <script src="assets/js/custom-scripts.js"></script>
   
</body>
</html>
<?php 
$con->close();  
?>