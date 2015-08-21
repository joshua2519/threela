import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class cale3ServeralDayCrossDaysByStockId3 {
	
	//將數字前面補滿0回傳
	public static String zfill(int num,int fill){
		String tmp="";
		tmp = "0000000000"+num;
		tmp = tmp.substring(tmp.length()-fill, tmp.length() );	
		return tmp;
	}
	
	
	//writeToTradingDB(sm,stockId, timeId, beforePNDays,beforeCrossDays,beforeOverBS); // 將當天的 1.買賣超持續天數，2.是否有黃金或死亡交叉，3.先前是否有超越3倍標準差  寫入Trading資料庫
	public static void UpdateTradingDB(Statement sm,int stockId, int timeId, int PNDays, int crossDays, int overBS){
		//System.out.println("before UpdateTradingDB:");
		StringBuilder sql = new StringBuilder("UPDATE trading "+
					" SET PNDays = "+PNDays+" ,crossDays = "+crossDays+", overBS = "+overBS +
					" WHERE StockId = "+stockId+" AND TimeId = "+timeId );
		try{
			sm.executeUpdate(sql.toString());
			sm.execute("COMMIT ;");
		} catch (SQLException e) {
			System.out.println("sql:"+sql);
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
/*			
			if (sm != null){
				try{
					sm.close();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
*/
		}
		//System.out.println("after UpdateTradingDB:" + "stockId:"+ stockId + "  timeId:"+timeId);
	}
	public static void InsertTractionDB(Statement sm,int stockId, int timeId,int isbs){
		StringBuilder sql = new StringBuilder(
			" INSERT INTO transaction (StockId, FindStartTimeId, Cate) Values( "+
			stockId+"," +  timeId +","+ isbs +")"	);		
		System.out.println("sql: "+ sql);
		try{
			sm.executeUpdate(sql.toString());			
		} catch (SQLException e) {
			System.out.println("sql:"+sql);
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}finally{}
	}
	public static void updateTractionDB(Statement sm,int stockId, int timeId,int isbs){
		
		StringBuilder sql = new StringBuilder(
			" UPDATE transaction " +
			" SET "+			
			" FindEndTimeId = " + timeId +
			" WHERE StockId = "+stockId+ "  and FindEndTimeId is NULL"
		);
		if (isbs == 22) 	System.out.print("回補 ");
		else if(isbs == 12) System.out.print("賣出 ");
		System.out.println("sql"+sql);

		try{
			sm.executeUpdate(sql.toString());			
		} catch (SQLException e) {
			System.out.println("sql:"+sql);
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}finally{}
		System.out.println("after Update:");
	}
	
	
	public static void getData(Connection conn, int stockId, int timeId){
		Statement sm = null;
		Statement smUpdate = null;
		
		//stockId = 2106;
		//int timeId = 0; //日期

		double highPrice = 0 ;;//最高價
		double lowPrice = 0 ;;//最低價
		double closePrice = 0; //收盤價
		int bsday20=0; 		// 資料庫抓下來的連續20日買賣超
		double ma5 = 0;  //5日移動平均線
		double ma60 = 0; //60日移動平均線
		double uBand = 0;//三倍 BBand上緣
		double lBand = 0;//三倍 BBand下緣
		
		int PNDays = 0; // 買賣超持續天數
		int beforePNDays = 0;//前一個買賣超持續天數
		int crossDays = 0; //ma5 穿越ma60 的天數 。 + 在上幾天    -在下幾天
		int beforeCrossDays = 0; //前一個 ma5 穿越ma60 的天數 。 + 在上幾天    -在下幾天
		int overBS = 0 ;  //先前是否有穿越3倍標準差，並未出場
		int beforeOverBS = 0 ;  //前一交易日的 先前是否有穿越3倍標準差，並未出場
		int isbs = 0;//是否買或賣  0->沒有作買賣   11->買進  12->買進後的賣出     21->作空   22->作空後的回補
		int count = 0;// 看現在是在前一交易日或是當天交易日
		boolean isDebug = false;
/*  用於 Insert	
		StringBuilder sql = new StringBuilder( 
			"SELECT StockId, TimeId,HighPrice,LowPrice ,ClosePrice,BSDay20,MA5,MA60,UBand,LBand, PNDays, CrossDays, OverBS "+  
			" FROM threela.trading "+ 
			" WHERE StockId = "+stockId ) ;
*/
		
		StringBuilder sql = new StringBuilder(
			" SELECT t.StockId, TimeId,HighPrice,LowPrice ,ClosePrice,"+
			" BSDay20,MA5,MA60,UBand,LBand, PNDays, CrossDays, OverBS ,cate.Cate as cate" +
			" FROM threela.trading  as t " +
			" LEFT JOIN( " +
			"     SELECT StockId,Cate FROM threela.transaction WHERE StockId = "+stockId+" AND FindEndTimeId is NULL" +
			" ) as cate " +
			" ON t.StockId = cate.StockId " +
			" WHERE t.StockId = "+stockId+" AND t.TimeId IN ( " +
			"     SELECT * FROM (" +
			"         SELECT TimeId FROM threela.index  " +
			"         WHERE TimeId BETWEEN "+(timeId - 10000)+" AND " +timeId +
			"         ORDER BY TimeId DESC LIMIT 0,2  " +
			"     )tmp " +
			"  ) " +
			"  ORDER BY TimeId ");

		System.out.println("sql:"+sql);
		try{
			sm = conn.createStatement();
			smUpdate = conn.createStatement();

			ResultSet rs = sm.executeQuery(sql.toString());
			while (rs.next()){
				// System.out.print("1:"+ rs.getString("StockId")+ " ");
				//timeId = Integer.parseInt(rs.getString("TimeId") );
				if (count == 0){
					PNDays 	  = Integer.parseInt(rs.getString("PNDays"));
					crossDays = Integer.parseInt(rs.getString("CrossDays"));
					overBS	  = Integer.parseInt(rs.getString("OverBS"));
					bsday20   = Integer.parseInt(rs.getString("BSDay20"));
					if (rs.getString("cate") != null )  isbs = Integer.parseInt( rs.getString("cate"));
				}
			 	if (isDebug == true)System.out.println("id: "+stockId+" count: " + count + " time: " + rs.getString("TimeId") +  "  get bsday20:" + rs.getString("BSDay20") + "  isbs:"+ isbs + " crossDays:"+ crossDays +"------------------------------------");
			 	
			 	if (count == 1){
				 	//算 買賣超持續天數
				 	if (rs.getString("BSDay20") !=null) bsday20 = Integer.parseInt(rs.getString("BSDay20").toString());
				 	if (bsday20 >0){		PNDays = (beforePNDays < 0)?  1 : beforePNDays+1;
				 	}else if (bsday20 <0){	PNDays = (beforePNDays > 0)? -1 : beforePNDays-1;
				 	}else if (bsday20 ==0){ PNDays = beforePNDays; //買賣超值為0，保留前值
					}
				 	if (isDebug == true) System.out.println("bsday20: "+ bsday20 + " PNDays:"+PNDays);
				 	//算 ma5 穿越ma60
				 	if (rs.getString("MA5").toString() !=null)  ma5 = Float.parseFloat(rs.getString("MA5").toString());
				 	if (rs.getString("MA60").toString() !=null) ma60= Float.parseFloat(rs.getString("MA60").toString());
				 	if (ma5 > ma60){ 		crossDays = (beforeCrossDays < 0)? 1: beforeCrossDays +1;			 		
				 	}else if (ma5 < ma60){	crossDays = (beforeCrossDays > 0)?-1: beforeCrossDays -1;			 		
				 	}else if (ma5 == ma60 && ma5 !=0 ){ crossDays = (beforeCrossDays > 0)?-1: 1; //ma5 = ma60 時，翻轉
					}else{							    crossDays = beforeCrossDays;  			//ma5 為0時，保留前值						
					}

				 	//算先前是否有穿越3倍標準差
				 	if (rs.getString("UBand").toString() !=null) uBand = Float.parseFloat(rs.getString("UBand").toString());
				 	if (rs.getString("LBand").toString() !=null) lBand = Float.parseFloat(rs.getString("LBand").toString());
				 	if (rs.getString("HighPrice").toString() !=null) highPrice = Float.parseFloat(rs.getString("HighPrice").toString());
				 	if (rs.getString("LowPrice").toString() !=null) lowPrice = Float.parseFloat(rs.getString("LowPrice").toString());
				 	if (isDebug == true)  System.out.println("UBand: "+ uBand + "  lowPrice: "+ lowPrice);
			
	
				 	// isbs ,stockId,timeId to trading, traction 
				    // overBS,PNDays, crossDays to trading
				 	if (isDebug == true) System.out.println("  beforeOverBS:"+beforeOverBS + " PNDays:"+PNDays);
				 	if (beforeOverBS == 1 && (crossDays < 0 || PNDays < 0) ) {
				 		overBS = 0;
				 		System.out.println("overBS == 1-> 0" + " crossDays:"+ crossDays + " PNDays:"+PNDays);
				 		if (isbs == 11){ isbs = 12;  System.out.println("id:"+stockId +" time:"+timeId + " 出12_80:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 			updateTractionDB(smUpdate,stockId, timeId,isbs); 
				 		}// 賣出 (先前有穿越3倍標準差，後來出現死亡交差或是轉成賣超)
					}else if (beforeOverBS == -1 && (crossDays > 0 || PNDays > 0) ){  
						overBS = 0;
						System.out.println("overBS == -1->0 " + " crossDays:"+ crossDays + " PNDays:"+PNDays);
						if (isbs == 21) { isbs = 22; System.out.println("id:"+stockId +" time:"+timeId + "回補22_83:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
							updateTractionDB(smUpdate,stockId, timeId,isbs);
						}// 回補 (先前有穿越3倍標準差，後來出現黃金交差或是轉成買超)
					}
				 	if ( (PNDays >0 && crossDays > 0) && (highPrice >= uBand || beforeOverBS == 1 )){ 		overBS = 1;			 		
				 	}else if ((PNDays <0 && crossDays < 0) && (lowPrice <= lBand || beforeOverBS == -1 )){ overBS = -1;			 		
					}
	
				 	if (overBS == 1){	System.out.println("三倍上標準overBS:"+overBS + " HighPrice:"+highPrice + " uBand:"+uBand + " crossDays:"+ crossDays + " PNDays:"+PNDays);
				 	}else if (overBS == -1){				System.out.println("三倍下標準overBS:"+overBS + " lowPrice:"+lowPrice+" lBand:"+lBand + " crossDays:"+ crossDays + " PNDays:"+PNDays);
				 	}
	
				 	//買進或作空 。isbs = 11 為買進   。  isbs = 12 為賣出   21作空   22回補 
				 	if ( isbs != 11 && (beforePNDays < 0 && PNDays > 0) &&  crossDays > 0 && crossDays <=10 ){
				 		if (isbs == 21) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " 回補21:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 11;
				 		System.out.println("id:"+stockId +" time:"+timeId + " 買84:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	} 			 		
				 	if ( isbs != 21 && (beforePNDays > 0 && PNDays < 0) &&  crossDays < 0 && crossDays >=-10){
				 		if (isbs == 11) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " 出11:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 21;
				 		System.out.println("id:"+stockId +" time:"+timeId + " 空85:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs != 11 && (beforeCrossDays < 0 && crossDays >0) && PNDays > 0 && PNDays <=10)   {
				 		if (isbs == 21) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " 回補21:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 11;
				 		System.out.println("id:"+stockId +" time:"+timeId + " 買11:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs != 21 && (beforeCrossDays > 0 && crossDays <0) && PNDays < 0 && PNDays >=-10)  {
				 		if (isbs == 11) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " 出11:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 21;
				 		System.out.println("id:"+stockId +" time:"+timeId + " 空87:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 11 && (beforePNDays > 0 && PNDays < 0)  &&  crossDays <0 )    { 
				 		isbs = 12;
				 		System.out.println("id:"+stockId +" time:"+timeId + " 賣88:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 11 && (beforeCrossDays > 0 && crossDays < 0)  &&  PNDays <0 ) { 
				 		isbs = 12;  
				 		System.out.println("id:"+stockId +" time:"+timeId + " 賣89:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 21 && (beforePNDays < 0 && PNDays > 0)  &&  crossDays >0 )    { 
				 		isbs = 22;  
				 		System.out.println("id:"+stockId +" time:"+timeId + "回補90:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 21 && (beforeCrossDays < 0 && crossDays > 0)  &&  PNDays >0 ) { 
				 		isbs = 22;
				 		System.out.println("id:"+stockId +" time:"+timeId + "回補91:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	//System.out.println("前一交易日");
				 	
				 	
				 	if (isbs == 12 || isbs == 22) isbs = 0;  //有作出有賣出，或是作空有回補時，表示現在沒有持股，標示為0
			 	}	// end of if (count == 1){  	
			 	
			 	// 記錄前一交易日的值
			 	beforePNDays = PNDays;   beforeCrossDays=crossDays; beforeOverBS = overBS;
			 	if (isDebug == true) System.out.println("beforePNDays: "+ beforePNDays + " beforePNDays"+beforeCrossDays + " beforeOverBS:"+ beforeOverBS);
			 	if (timeId > 20150101 && count == 1) UpdateTradingDB(smUpdate,stockId, timeId, PNDays,crossDays,overBS); // 將當天的 1.買賣超持續天數，2.是否有黃金或死亡交叉，3.先前是否有超越3倍標準差  寫入Trading資料庫			 	
				count ++;
			}   // end while (rs.next()){
			
		} catch (SQLException e) {
			e.printStackTrace();		
	    } catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if (sm != null)
	    		try{
	    			sm.close();
	    		} catch(Exception e){
	    			e.printStackTrace();
	    		}	    		
	    }
		
	}

	public static void main(String[] args) {
        //連資料庫		 
		String connectionUrl = null;
		Connection conn = null;
		Statement sm = null;
		Statement smUpdate = null; // 用在  新增、更新上
		String yearStr= null; // 字串年 
		String monthStr = null;//  字串月
		String dayStr = null; // 字串日
		//int date = 0; //日期
		
		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH)+1;
		int day = today.get(Calendar.DATE);
		int date = Integer.parseInt(year+ zfill(month,2) + zfill(day,2) );
		if (month == 1) date = date - 8900;
		else 			date = date - 100;

		
		
		//LocalDate birthDate = new LocalDate(DateTime.now() );

        try {
	        Class.forName("com.mysql.jdbc.Driver");	
			
			String connUrl = "jdbc:mysql://10.120.30.4:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");

			sm = conn.createStatement();
			//smUpdate = conn.createStatement();
			int StockId[]={1101,1102,1104,1216,1229,1256,1262,1301,1314,1409,1434,1440,1476,1477,1503,1504,1507,1580,1589,1590,1605,1707,1717,1722,1723,1736,1785,1789,1795,1802,1907,2002,2006,2015,2035,2049,2059,2101,2104,2105,2106,2201,2204,2206,2207,2227,2231,2301,2308,2325,2327,2330,2331,2337,2352,2353,2356,2362,2373,2376,2377,2379,2382,2385,2392,2393,2395,2409,2412,2448,2449,2451,2474,2501,2511,2542,2548,2603,2606,2608,2615,2618,2634,2637,2707,2723,2727,2801,2812,2820,2834,2847,2849,2855,2867,2883,2887,2889,2890,2903,2923,2926,3008,3034,3037,3044,3060,3088,3105,3176,3189,3211,3227,3265,3293,3376,3452,3474,3481,3490,3520,3529,3532,3552,3558,3576,3611,3658,3662,3673,3682,3693,4103,4105,4120,4123,4152,4157,4162,4163,4198,4401,4416,4733,4736,4911,4938,4944,4947,4966,5007,5009,5263,5264,5274,5287,5289,5306,5351,5356,5392,5398,5425,5457,5478,5483,5489,5490,5508,5512,5522,5530,5534,5820,5871,5903,5904,6005,6016,6023,6105,6116,6121,6146,6147,6166,6176,6188,6214,6223,6239,6244,6261,6269,6274,6286,6409,6415,6451,6605,6803,8042,8044,8046,8050,8069,8076,8083,8086,8121,8150,8234,8255,8299,8349,8406,8422,8454,8917,8936,8942,9907,9910,9917,9921,9930,9938,9940,9945,9951};			

			// ??? 在此修改開始要處理的日期資料  //??? 在此修改日期			
			StringBuilder sql = new StringBuilder(
				  " SELECT TimeId FROM trading "								   	   	 //  ??? 下面的， buySell 需要換為 BSDay20 ，但是修改程式時， BSDay20有值，所以暫時使用 buySell
				+ " WHERE  StockId = 2330 and TimeId > "+date+"  and (PNDays =0  or CRossDays = 0 ) AND BuySell is not NULL");
			ResultSet rs =sm.executeQuery(sql.toString());
			while(rs.next()){
				for (int i=0;i< StockId.length ; i++){
					getData(conn,StockId[i],Integer.parseInt(rs.getString("TimeId")) );
				}
			}
		} catch (ClassNotFoundException e1) {
			System.out.println("JDBC沒有驅動程式" + e1.getMessage());			
		} catch (SQLException e1) {
			e1.printStackTrace();		
	    } catch(Exception e){
	        System.out.println( e.toString() );
	    }finally{
	    	if (sm != null) {
	    		try{					sm.close();
	    		} catch(Exception e){	e.printStackTrace();
	    		}
	    	}
	    	if (conn != null){
	    		try{	    			conn.close();
	    		} catch(Exception e){	e.printStackTrace();
	    		}
	    	}
	    }        

	} // end of main
}
