﻿<!DOCTYPE html>
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
   <!-- D3.js Styles -->
   <link rel=stylesheet href="/assets/style.css" type="text/css" media=screen>
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
                <div class="nav navbar-top-links navbar-right">
                 
                    <input type="button" value="基本面推薦" id="hideMsg" style="border-style:none;background-color:transparent;height:50px;width:160px;font-size:20px">
                    
                    <input type="button" value="籌碼面推薦" id="hideMsg1" style="border-style:none;background-color:transparent;height:50px;width:160px;font-size:20px">
                    
                </div>
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
                        <a href="chart.php" class="active-menu"><i class="fa fa-bar-chart-o"></i> 個股資訊</a>
                    </li>
                    <li>
                        <a href="map.php"><i class="fa fa-table"></i> 地圖分析</a>
                    </li>

                </ul>
            </div>
        </nav>
        <!-- /. NAV SIDE  -->
        
        <div id="page-wrapper" >
            <div id="page-inner">
                <div class="row">
                    <div class="col-md-12">
                        <input type="text" placeholder="Enter Stock Code Here" id="stockCode" />
            
                        <button type="button"  onClick="getValue()">Go</button>
                    </div>
                </div> 
                 <div class="row">
                    <div class="col-md-12">
                        <?php
                        $con = mysql_connect("10.120.30.4","threela","123456");
                        if (!$con) {
                            die('Could not connect: ' . mysql_error());
                        }
                        mysql_select_db("threela", $con);
                        $StockId = $_POST['StockId'];
                        $result = mysql_query("
                        SELECT StockId, SubIndustryName
                         FROM threela.comindustry
                         where StockId = '$StockId';
                        ")

                        $row = @mysql_fetch_row($result);
                        if($row[0]==$StockId)
                        {
                             $_SESSION['username'] = $StockId
                             echo $row[0]
                        } 

                        ?>
                        

                        <?php
                        while($row=mysql_fetch_array($result,MYSQL_ASSOC)){
                            printf('<h1 class="page-header" id="stockName">%s</h1>',$row['SubIndustryName']);
                        }
                        mysql_free_result($result);;
                        
                        ?>
                        
                        
                        <table id="message" style = "display:none;" onClick="changeValue()">
                            <tr>
                            <td>
                            <ul>
                                <li style="font-size:20px;"><a id="message1">3133-瓜瓜</a></li>
                                <li style="font-size:20px;"><a id="message2">1476-儒鴻</a></li>
                                <li style="font-size:20px;"><a id="message3">9521-興大</a></li>
                            </ul>
                            </td>
                            <td>
                            <ul>
                                <li style="font-size:20px;"><a id="message4">4444-宏達電</a></li>
                                <li style="font-size:20px;"><a id="message5">1456-遠東紡</a></li>
                                <li style="font-size:20px;"><a id="message6">9921-巨大</a></li>
                            </ul>
                            </td>
                            <td>
                            <ul>
                                <li style="font-size:20px;"><a id="message7">5890-彰銀</a></li>
                                <li style="font-size:20px;"><a id="message8">1314-碩名</a></li>
                                <li style="font-size:20px;"><a id="message9">1101-台泥</a></li>
                                <li style="font-size:20px;"><a id="message10">0050-台灣50</a></li>
                            </ul>
                            </td>
                            </tr>
                        </table>
                    </div>
                </div> 
                 <!-- /. ROW  -->
                <div class="row">
                    <div class="col-md-12">    
                    <!-- <div class="col-md-6 col-sm-12 col-xs-12"> -->  
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                    Bar Chart
                            </div>
                            <div class="panel-body">
                                <!-- <div id="morris-bar-chart"></div> -->
                                <div id="container" style="height: 400px; min-width: 310px"></div>
                            </div>
                        </div>            
                    </div>
                </div>
                 <!-- /. ROW  -->
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                Line Chart
                            </div>
                            <div class="panel-body">
                                <div id="morris-line-chart"></div>
                            </div>
                    
                        </div>  
                    </div>      
                </div> 
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                D3 Chart
                            </div>
                            <div class="panel-body">
                                <div id="chart1"></div>
                            </div>
                    
                        </div>  
                    </div>      
                </div> 
                 <!-- /. ROW  -->
                 <footer><p>All right reserved. Template by: <a href="http://webthemez.com">WebThemez</a></p></footer>
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
      <!-- Custom Js -->
    <script src="assets/js/custom-scripts.js"></script>
    
    <script src="http://code.highcharts.com/stock/highstock.js"></script>
    <script src="http://code.highcharts.com/stock/modules/exporting.js"></script>
    <script src='http://d3js.org/d3.v3.min.js' charset="utf-8"></script>
    <script src='https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.7.0/underscore-min.js' charset="utf-8"></script>
    
    <script src="test1.js"></script>
    <script src="script.js"></script>
    <script src="hideValue.js"></script>
    <script src="changeValues.js"></script>
</body>
</html>
