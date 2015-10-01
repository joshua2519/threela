import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class cale1BuySell3 {
	public static void Insertsql(){
	/*  
	call store_BuySell(1301,20150702)
	show create procedure store_BuySell
	drop procedure store_BuySell;
		 
	delimiter $$
	CREATE procedure store_BuySell(IN p_stockid varchar(11), In p_timeid int)
	Begin
	
	Insert INTO threela.tradingTmp(
		StockId, TimeId, OpenPrice, ClosePrice, HighPrice, LowPrice, Volume, YieldRate, PE, 
	    Buy20,Sell20,BuySell,
	    BSDay20, BSRate, BSRateMA20, MA5 ,MA20, MA60, UBand, LBand
	)
		SELECT 
	    ins.StockId, ins.TimeId, ins.OpenPrice, ins.ClosePrice, ins.HighPrice, ins.LowPrice, 
	    ins.Volume, ins.YieldRate, ins.PE, 
	    result.Buy,result.Sell,result.BuySell,
	    ins.BSDay20,(result.BuySell/ ins.Volume )*100 as BSRate ,ins.BSRateMA20,
	    ins.ma5, ins.ma20, ins.ma60,  ins.UBand,ins.LBand
	    FROM threela.trading as ins 
			 JOIN  
			 ( 
	
				select d2.stockid, d2.TimeId , sum(sub.Buy)as Buy , sum(sub2.Sell) as Sell , (sum(sub.Buy) - sum(sub2.Sell) ) as BuySell  
			 	from threela.daily as d2 
			 	left JOIN 	( 
			 		select BrokerId,stockid , TimeId, Buy, Sell  
			 		from threela.daily 
			 		where stockid = p_stockid and TimeId =  p_timeid
			 		order by Buy desc 
			 		limit 0,20 
			 	) as sub 
			 	ON d2.stockid = sub.stockid and d2.TimeId = sub.TimeId and d2.BrokerId = sub.BrokerId 
			  
			 	left JOIN 	( 
			 		select BrokerId,stockid , TimeId, Buy, Sell  
			 		from threela.daily 
			 		where stockid = p_stockid and TimeId =  p_timeid
			 		order by Sell desc 
			 		limit 0,20 
			 	) as sub2 
			 	ON d2.stockid = sub2.stockid and d2.TimeId = sub2.TimeId and d2.BrokerId = sub2.BrokerId 
			 	where d2.stockid = p_stockid and d2.TimeId = p_timeid            
	            
			 ) as result 
	ON ins.StockId = result.StockId and ins.TimeId = result.TimeId ;
	end $$
	delimiter ;

	*/
	}
	
	public static void updateDailyBuySell(){
		/*
		使用 call store_UpdateTradingBuySell(1301,20150803)
		刪除 drop  procedure store_UpdateTradingBuySell

		delimiter $$ 
		create procedure store_UpdateTradingBuySell(
		In p_stockid varchar(11) ,In p_timeid int) 
		 Begin 
		
		update threela.trading  as up  
		JOIN   
		(  	
		    select d2.stockid, d2.TimeId , sum(sub.Buy)as Buy , sum(sub2.Sell) as Sell , 
		    (sum(sub.Buy) - sum(sub2.Sell) ) as BuySell  	
		    from threela.daily as d2  	
		left JOIN 	(
		    select BrokerId,stockid , TimeId, Buy, Sell
		    from threela.daily
		    where stockid = p_stockid COLLATE utf8_unicode_ci  and TimeId = p_timeid and Buy is not NULL
		    order by Buy desc  		limit 0,20  	) as sub
		    ON d2.stockid = sub.stockid and d2.TimeId = sub.TimeId and d2.BrokerId = sub.BrokerId
		
		left JOIN 	(
		    select BrokerId,stockid , TimeId, Buy, Sell
		    from threela.daily
		    where stockid = p_stockid COLLATE utf8_unicode_ci and TimeId = p_timeid and Sell is not NULL
		    order by Sell desc  		limit 0,20  	) as sub2  
			ON d2.stockid = sub2.stockid and d2.TimeId = sub2.TimeId and d2.BrokerId = sub2.BrokerId  	
		    where d2.stockid = p_stockid COLLATE utf8_unicode_ci and d2.TimeId = p_timeid ) as result  
		    ON up.StockId = result.StockId and up.TimeId = result.TimeId 
		set   
		    up.Buy20 = result.Buy,  
		    up.Sell20 = result.Sell, 
		    up.BuySell = result.BuySell ,
		    up.BSRate =  round( (result.BuySell / up.Volume ) * 100 , 2) ;
		
		end $$
		delimiter ;
		
		*/
	}
	
	//將數字前面補滿0回傳
	public static String zfill(int num,int fill){
		String tmp="";
		tmp = "0000000000"+num;
		tmp = tmp.substring(tmp.length()-fill, tmp.length() );	
		return tmp;
	}
	
	public static void updateDB(Statement sm,String StockId,String date){			 
		StringBuilder sql =  new StringBuilder( "call store_UpdateTradingBuySell('"+StockId+"' , "+date+")");
		try{
			sm.executeUpdate(sql.toString());
		}catch(SQLException e){
			System.out.println("Update error:"+ sql);
			e.printStackTrace();			
		}				
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//連資料庫
		String connectionUrl = null;
		Connection conn;
		Statement sm = null;		 
		//Statement smUpdate = null;
		//ResultSet rs = null;
		ArrayList StockId = new ArrayList();
		ArrayList TimeId = new ArrayList();
		
		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH)+1;
		int day = today.get(Calendar.DATE);
		int date = Integer.parseInt(year+ zfill(month,2) + zfill(day,2) ); // 設定開始要執行的日期
		if (month == 1) date = date - 9000; // 往前推二個月
		else 			date = date - 200;
		//date = 20140630;  修改開始要執行的日期
		System.out.println("date:"+date);
        try {
	        Class.forName("com.mysql.jdbc.Driver");	
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");
			sm = conn.createStatement();
			//String sqlStr = " SELECT StockId, TimeId,count(*)  FROM threela.daily WHERE TimeId >20130828 group by StockId, TimeId having count(*) > 20";
			/*
			String sqlStr = 
				" SELECT up.StockId, up.TimeId,count(*)  FROM threela.daily  as up " +
				" JOIN (  " +
				" 	SELECT StockId , TimeId FROM trading  \n" +
				"     WHERE TimeId > "+date+" and BuySell is NULL  " +
				" ) as tmp " +
				" ON tmp.StockId = up.StockId and tmp.TimeId = up.TimeId \n" +
				" WHERE  up.TimeId > " + date + "\n" +
				//" AND up.StockId in (1101,1102,1104,1216,1227,1229,1256,1262,1301,1303,1312,1314,1319,1326,1402,1409,1434,1440,1476,1477,1503,1504,1507,1536,1565,1580,1589,1590,1605,1702,1707,1710,1717,1722,1723,1736,1785,1789,1795,1802,1907,2002,2006,2015,2035,2049,2059,2101,2103,2104,2105,2106,2201,2204,2206,2207,2227,2231,2301,2303,2308,2311,2313,2317,2324,2325,2327,2330,2331,2337,2344,2347,2352,2353,2354,2355,2356,2357,2360,2362,2373,2376,2377,2379,2382,2385,2392,2393,2395,2408,2409,2412,2441,2448,2449,2451,2454,2458,2474,2498,2501,2511,2542,2548,2603,2606,2608,2609,2610,2615,2618,2634,2637,2707,2723,2727,2729,2801,2809,2812,2820,2823,2833,2834,2838,2845,2847,2849,2855,2867,2880,2881,2882,2883,2884,2885,2886,2887,2888,2889,2890,2891,2892,2903,2912,2915,2923,2926,3008,3022,3034,3037,3044,3045,3060,3088,3105,3152,3176,3189,3211,3227,3231,3260,3264,3265,3293,3376,3452,3474,3481,3490,3520,3529,3532,3552,3558,3576,3611,3658,3662,3673,3682,3691,3693,3702,3706,4103,4105,4107,4120,4123,4128,4130,4152,4157,4162,4163,4180,4198,4401,4416,4549,4733,4736,4743,4904,4911,4938,4944,4947,4958,4966,5007,5009,5213,5263,5264,5274,5287,5289,5306,5347,5349,5351,5356,5371,5392,5398,5425,5457,5478,5483,5489,5490,5508,5512,5522,5530,5534,5820,5871,5880,5903,5904,6005,6016,6023,6105,6116,6121,6146,6147,6166,6176,6180,6188,6206,6214,6223,6239,6244,6261,6269,6274,6279,6285,6286,6409,6414,6415,6451,6505,6605,6803,8042,8044,8046,8050,8069,8076,8083,8086,8121,8150,8234,8255,8299,8349,8406,8422,8454,8917,8936,8942,9904,9907,9910,9914,9917,9921,9930,9933,9938,9940,9941,9945,9951)  " +
				" group by up.StockId, up.TimeId ";
				// +"having count(*) >= 20  " ;
			*/
			String sqlStr = " SELECT StockId , TimeId FROM trading  \n" +
						    " WHERE TimeId >= "+date+"  and BuySell is NULL " +  // 這裡控制要執行的日期
						    " ORDER BY TimeId ";
			System.out.println("sql: "+ sqlStr);
			ResultSet rs = sm.executeQuery(sqlStr);

			while (rs.next()){
				//System.out.print("1:"+ rs.getString("StockId")+ " ");
			 	StockId.add(rs.getString("StockId").toString() );
			 	TimeId.add( rs.getString("TimeId").toString());  
			}		
		} catch (ClassNotFoundException e1) {
			System.out.println("JDBC don't have Driver" + e1.getMessage());			
		} catch (SQLException e1) {
			e1.printStackTrace();		
	    } catch(Exception e){
	        System.out.println( e.toString() );
	    } finally{

	    } 

		String sqlStr = "";//放 sql 語法
		String strMonth = "";
		String strDay = "";		
		System.out.println("====================================");

		try {
			for (int i=0;i<StockId.size();i++){
				 //System.out.print("2:"+ StockId.get(i) );
				 updateDB(sm ,StockId.get(i).toString() ,TimeId.get(i).toString() );
				 if (i % 100==0) System.out.println("count:"+ i + " StockId:"+ StockId.get(i).toString() + " TimeId:"+ TimeId.get(i).toString() );
				 //break;
			}
		}catch(Exception e){
			System.out.println("error Update:"+ e.toString());
		}
		System.out.println("end !!");

	}

}
