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
   <!-- D3.js Styles -->
   <link rel=stylesheet href="/assets/style.css" type="text/css" media=screen>

    <script type="text/javascript" src="getStockChart.js"></script>
</head>
<?php
    $servername = "10.120.30.4";
    $username = "threela";
    $password = "123456";
    $dbname = "threela";

    $conn = new mysqli($servername, $username, $password, $dbname);
    if($conn->connect_error){
        die("Connection failed:" . $conn->connect_error);
    }

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
                <div class="nav navbar-top-links navbar-right">
                    <?php
                        $queryTen = "select StockId , ComName from threela.company limit 0,10" ;
                        $tenStock = $conn->query($queryTen);
                    ?>

                    <form method="post" action="<?php echo $_SERVER["PHP_SELF"]; ?>">
                    <input type="button" value="基本面推薦" id="hideMsg" style="border-style:none;background-color:transparent;height:50px;width:160px;font-size:20px">
                    <input type="button" value="籌碼面推薦" id="hideMsg1" style="border-style:none;background-color:transparent;height:50px;width:160px;font-size:20px">
                    </form>

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
                        <a href="counter.php"><i class="fa fa-table"></i> 籌碼面分析</a>
                    </li>
                 
                    <li>
                        <a href="fundamentals.php"><i class="fa fa-edit"></i> 基本面分析</a>
                    </li>
                    <li>
                        <a href="map.php"><i class="fa fa-file"></i> 地圖分析</a>
                    </li>

                </ul>
            </div>
        </nav>
        <!-- /. NAV SIDE  -->
        
        <div id="page-wrapper" >
            <div id="page-inner">
                <div class="row">
                    <div class="col-md-12">
                        <?php
                           
                            $eid = isset($_POST["enterId"]) ? $_POST["enterId"] : 1101;
                                                                
                                
                            
                            $sql = "
                                select c1.StockId as StockId , c2.ComName as ComName, c1.SubIndustryName as SubIndustryName
                                from comindustry as c1 join company as c2 on (c1.StockId = c2.StockId)
                                where c1.StockId = '".$eid."'";
                                $result = $conn->query($sql);
                            
                        ?>
                        <form method="post" action="<?php echo $_SERVER["PHP_SELF"]; ?>">
                        <input type="text"  name="enterId" id="stockCode"></input>
                        <input type="submit" value="GO" onclick="GetStockPrice(document.getElementById('stockCode').value);" ></input>
                        </form>
                        <?php

                            if($result !=null && $result->num_rows > 0){
                                $count = 0 ;
                                
                                while($row = $result->fetch_assoc()){
                                    if ($count == 0){
                                        echo "<h1 >".$row["StockId"]."-".$row['ComName']."</h1>" ;
                                        echo "<br><h3>"."公司所屬產業類"."</h3>";
                                        echo '<ol >';
                                    }
                                    echo '<li style="margin: 20px;">'.$row["SubIndustryName"]."</li>";
                                    $count++;
                                }
                                echo "</ol>";
                            }else{
                                echo "<br>請輸入正確的代號<br>";
                            }
                            $conn->close();
                        ?>
                        
                    </div>
                </div> 
                 <div class="row">
                    <div class="col-md-12">
                        
                        <?php
                            echo '<table id="message" style = "display:none;" onClick="changeValue()">';
                            echo '<tr>';
                            echo '<td>';
                            while($row = $tenStock->fetch_assoc()){  
                                echo '<ul>';

                                echo '<li style="font-size:20px;"><a name="message1" id="message1">'.$row["StockId"]."-".$row['ComName']."</a></li>" ;
                                
                                echo '</ul >';
                                 
                            
                            }
                            echo '</td>';
                            echo '</tr>';
                            echo '</table>';

                        ?>
                        
                    </div>
                </div> 
                 <!-- /. ROW  -->
                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                HighChart
                            </div>
                            <div class="panel-body">
                                <div id="test_coco" style="height:400px ;min-width: :310px">
                                </div>
                            
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
   
    
    <script src="test1.js"></script>
    <script src="script.js"></script>
    <script src="hideValue.js"></script>
    <script src="changeValues.js"></script>
     <script>
        stockid=<?php echo '"'.$eid.'"'; ?>;
        GetStockPrice(stockid);
    </script>
     <!--<script src="text-coco.js"></script>-->
    
</body>
</html>
