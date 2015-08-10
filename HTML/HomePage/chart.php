<?php
    $con1 = mysql_connect("10.120.30.4","threela","123456");

    if (!$con1) {
        die('Could not connect: ' . mysql_error());
    }

    mysql_select_db("threela", $con1);

?>


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

   <link href='assets/css/companyIndustry.css' rel="stylesheet" />

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
                
                    <div>
                        <?php
                           
                            $eid = isset($_GET["enterId"]) ? $_GET["enterId"] : 1101;
                            $stockname = isset($_GET["enterId"]) ? $_GET["enterId"] : '台泥';                                   
                                
                            
                            $sql = "
                                select c1.StockId as StockId , c2.SampleName as SampleName, c1.SubIndustryName as SubIndustryName
                                from comindustry as c1 join company as c2 on (c1.StockId = c2.StockId)
                                where c1.StockId = '".$eid."'or c2.SampleName like '%".$stockname."%'" ;
                            $result = $conn->query($sql);

                            
                            // $result1 = $conn->query($sql1);
                            // $row1 = mysql_fetch_row($result1);
                        ?>
                        <form method="get" action="<?php echo $_SERVER["PHP_SELF"]; ?>">
                        <input type="text"  name="enterId" id="stockCode"></input>
                        <input type="submit" value="GO" onclick="GetStockPrice(document.getElementById('stockCode').value);" ></input>

                        <?php

                            if($result !=null && $result->num_rows > 0){
                                $count = 0 ;
                                
                                while($row = $result->fetch_assoc()){
                                    if ($count == 0){
                                        echo '<span class="ComIndustry">'.$row["StockId"]."-".$row['SampleName']."</span>";
                                        echo '<span >';
                                        echo ' '."產業鏈：";
                                    }
                                    echo '<p style="margin: 5px;display:inline;">'.$row["SubIndustryName"]."</p>";
                                    $count++;
                                }
                                echo "</span>";
                            }else{
                                echo "<br>請輸入正確的代號<br>";
                            }

                            $conn->close();
                            
                           
                             
                        ?> 
                        
                         <!-- or c.SampleName like '%".$stockname."%' -->
                        </form>
                        <?php
                        $result1 = mysql_query("
                            select round(m.RevMonthGrowthRate,2),round(m.RevYearGrowthRate,2),s.EPS,s.ROE,t.PE 
                            from month as m join season as s on m.StockId = s.StockId 
                            join trading as t on m.StockId = t.StockId join company as c on m.StockId = c.StockId 
                            where m.StockId ='".$eid."' and m.Year='2015' and m.month='6' and s.Year='2015' and s.season ='1' and t.TimeId<=now() order by t.TimeId desc limit 0,1;")
                        ?>    
                    </div>
                    <?PHP
                        $row1 = mysql_fetch_row($result1);
                        echo '<span style="font-family:  Microsoft JhengHei;font-weight: bolder;color:orange;">'."營收成長率(月)&nbsp;".$row1[0]."&nbsp;&nbsp;營收成長率(年)&nbsp;".$row1[1]."&nbsp;&nbsp;EPS&nbsp;".$row1[2]."&nbsp;&nbsp;ROE&nbsp;".$row1[3]."&nbsp;&nbsp;PE&nbsp;".$row1[4].'</span>';
                    ?>

                <div class="row">
                    <div class="col-md-12">
                        <div class="panel panel-default">
                            <div class="panel-body">
                                
                                <div id="test_coco" style="height:800px; min-width: :310px; "></div>
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

    <script src="http://code.highcharts.com/stock/highstock.js"></script>
    <script src="http://code.highcharts.com/stock/modules/exporting.js"></script>

    <script src="test1.js"></script>
    
     <script>
        stockid=<?php echo '"'.$eid.'"'; ?>;
        GetStockPrice(stockid);
    </script>
    
    <!--<script src="text-coco.js"></script>-->
    
</body>
</html>