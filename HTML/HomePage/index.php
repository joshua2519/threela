<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>CoCo - $tock</title>
    <!-- Bootstrap Styles-->
    <link href="assets/css/bootstrap.css" rel="stylesheet" />
    <!-- FontAwesome Styles-->
    <link href="assets/css/font-awesome.css" rel="stylesheet" />
    <!-- Morris Chart Styles-->
    <link href="assets/js/morris/morris-0.4.3.min.css" rel="stylesheet" />
    <!-- Custom Styles-->
    <link href="assets/css/custom-styles.css" rel="stylesheet" />
    <!-- Google Fonts-->
    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css' />
    
    <link href="assets/css/price.css" rel="stylesheet" />
</head>
<!-- Connecting to MySQL -->
<?php
    $con = mysql_connect("10.120.30.4","threela","123456");

    if (!$con) {
        die('Could not connect: ' . mysql_error());
    }

    mysql_select_db("threela", $con);

    $result = mysql_query("
    select T.TimeId, T.Date ,I.ClosePrice, I.TAPI, T.TWSEOPEN 
    from `index` as I JOIN time as T on (I.TimeId = T.TimeId)
    where T.Date <= now() order by T.TimeId desc
    limit 0 ,1;")

?>
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
                        <a class="active-menu" href="index.php"><i class="fa fa-dashboard"></i> 大盤資訊</a>
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
        <?php
        $row = mysql_fetch_row($result);
        ?>
        
        <div id="page-wrapper">
            <div id="page-inner">  

                <div class="row">
                    <div class="col-md-12">
                        <div class="panel-heading">
                            <h1 class="page-header">
                                
                                <strong> 加權指數：<?php echo $row[2] ?></strong>
                                <strong> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;成交金額：<?php echo $row[3] ?>(億) </strong>
                            </h1>
                        </div>

                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-body">
                                <!-- <div style="height:600px;width:1350px;"> -->
                                    
                                        <div id="marketChart" style="height:795px ;min-width: :310px"></div>

                                     
                                <!-- </div> -->
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
    <!-- Morris Chart Js -->
    <script src="assets/js/morris/raphael-2.1.0.min.js"></script>
    <script src="assets/js/morris/morris.js"></script>
	
	
	<script src="assets/js/easypiechart.js"></script>
	<script src="assets/js/easypiechart-data.js"></script>
	
	
    <!-- Custom Js -->
    <!--<script src="assets/js/custom-scripts.js"></script> -->

    <script src="http://code.highcharts.com/stock/highstock.js"></script>
    <script src="http://code.highcharts.com/stock/modules/exporting.js"></script>
    <script src="marketChart.js"></script>
    
</body>

</html>
