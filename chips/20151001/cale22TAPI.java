import java.sql.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class cale22TAPI {
	
	public static int count = 0; // 計算執行了幾次


	public static void sqlUpdate(){
		/*
		 " select * from threela.Index where TimeId > 20130701" +
		" call store_UpdateIndex_MA(20130702)" +
		" drop procedure store_UpdateIndex_MA;" +
		" " +
		" delimiter $$ " +
		" create procedure store_UpdateIndex_MA(In p_timeid int) " +
		" Begin " +
		"     UPDATE threela.Index as UP    " +
		"     JOIN " +
		"         ( SELECT p_timeid as TimeId,avg(ClosePrice) as ma5" +
		"           FROM threela.Index as t" +
		"           WHERE t.Timeid IN (" +
		"                 SELECT * FROM (" +
		"                     SELECT TimeId FROM threela.index" +
		"                     WHERE TimeId BETWEEN (p_timeid-10000) and  p_timeid" +
		"                     ORDER BY TimeId DESC limit 0,5" +
		"                 )temp" +
		" 			)" +
		"         ) as ma5" +
		"         ON ma5.TimeId = UP.TimeId        " +
		"     JOIN " +
		"         ( SELECT p_timeid as TimeId,avg(ClosePrice) as ma10" +
		"           FROM threela.Index as t" +
		"           WHERE t.Timeid IN (" +
		"                 SELECT * FROM (" +
		"                     SELECT TimeId FROM threela.index" +
		"                     WHERE TimeId BETWEEN (p_timeid-10000) and  p_timeid" +
		"                     ORDER BY TimeId DESC limit 0,10" +
		"                 )temp" +
		" 			)" +
		"         ) as ma10" +
		"         ON ma10.TimeId = UP.TimeId" +
		"     JOIN " +
		"         ( SELECT p_timeid as TimeId,avg(ClosePrice) as ma20" +
		"           FROM threela.Index as t" +
		"           WHERE t.Timeid IN (" +
		"                 SELECT * FROM (" +
		"                     SELECT TimeId FROM threela.index" +
		"                     WHERE TimeId BETWEEN (p_timeid-10000) and  p_timeid" +
		"                     ORDER BY TimeId DESC limit 0,20" +
		"                 )temp" +
		" 			)" +
		"         ) as ma20" +
		"         ON ma20.TimeId = UP.TimeId" +
		"     JOIN " +
		"         ( SELECT p_timeid as TimeId,avg(ClosePrice) as ma60" +
		"           FROM threela.Index as t" +
		"           WHERE t.Timeid IN (" +
		"                 SELECT * FROM (" +
		"                     SELECT TimeId FROM threela.index" +
		"                     WHERE TimeId BETWEEN (p_timeid-10000) and  p_timeid" +
		"                     ORDER BY TimeId DESC limit 0,60" +
		"                 )temp" +
		" 			)" +
		"         ) as ma60" +
		"         ON ma60.TimeId = UP.TimeId" +
		"     JOIN" +
		"         ( SELECT p_timeid as TimeId,std(ClosePrice) * 3 as std3" +
		"           FROM threela.Index as t" +
		"           WHERE t.Timeid IN (" +
		"                 SELECT * FROM (" +
		"                     SELECT TimeId FROM threela.index" +
		"                     WHERE TimeId BETWEEN (p_timeid-10000) and  p_timeid" +
		"                     ORDER BY TimeId DESC limit 0,60" +
		"                 )temp" +
		" 		  )" +
		"         ) as std" +
		"         ON std.TimeId = UP.TimeId" +
		"     JOIN " +
		"         ( SELECT p_timeid as TimeId,avg(BSRate) as bsma20" +
		"           FROM threela.Index as t" +
		"           WHERE t.Timeid IN (" +
		"                 SELECT * FROM (" +
		"                     SELECT TimeId FROM threela.index" +
		"                     WHERE TimeId BETWEEN (p_timeid-10000) and  p_timeid" +
		"                     ORDER BY TimeId DESC limit 0,20" +
		"                 )temp" +
		" 			)" +
		"         ) as bsma20" +
		"         ON bsma20.TimeId = UP.TimeId" +
		"     SET" +
		"     UP.MA5  = ma5.ma5 ," +
		"     UP.MA10 = ma10.ma10," +
		"     UP.MA20 = ma20.ma20," +
		"     UP.MA60 = ma60.ma60," +
		"     UP.UBand = ma60.ma60 + std.std3," +
		"     UP.LBand = ma60.ma60 - std.std3," +
		"     UP.BSMA20 = bsma20.bsma20" +
		"     WHERE UP.TimeId = p_timeid;" +
		"     " +
		" end $$" +
		" delimiter ;"
		
		*/
	}
	
	public static void sql(){
		
		String sql = 
			" 刪除 drop  procedure store_update_trading " +
			" delimiter $$ " +
			" create procedure store_Index2BSRateMA20( In p_timeid int) " +
			" Begin " +		
				
			" INSERT INTO threela.Index2BSRateMA20  " +
			" (StockId, TimeId, OpenPrice, HighPrice, LowPrice, ClosePrice,"+
			" Volume , MA5, MA10,MA20, MA60, "+
			" UBand, LBand, BSRate ,BSRateMA20) "+
			" SELECT ins.StockId , ins.TimeId, ins.OpenPrice, ins.HighPrice, ins.LowPrice, ins.ClosePrice "+
			" ins.Volume, ma5.ma5, ma10.ma10, ma20.ma20, ma60.ma60,"+
			" ma60.ma60 +std.std3 as UBand , ma60.ma60-std.std3 as LBand, ins.BSRate,  bsma20.bsma20 " +
			" FROM threela.Index as ins "+
			" JOIN "+
			"     (	SELECT TimeId,avg(ClosePrice) as ma5 " +
			"       FROM threela.Index as t " +
			"       WHERE t.Timeid BETWEEN (p_timeid-10000) and p_timeid " +
			"       Order by TimeId desc limit 0,5 " +
			" 	  ) as ma5 " +
			"     ON ma5.TimeId = ins.TimeId " +
			" JOIN "+
			"     (	SELECT TimeId,avg(ClosePrice) as ma10 " +
			"       FROM threela.Index as t " +
			"       WHERE t.Timeid BETWEEN (p_timeid-10000) and p_timeid " +
			"       Order by TimeId desc limit 0,10 " +
			" 	  ) as ma10 " +
			"     ON ma10.TimeId = ins.TimeId " +
			" JOIN "+
			"     (	SELECT TimeId,avg(ClosePrice) as ma20 " +
			"       FROM threela.Index as t " +
			"       WHERE t.Timeid BETWEEN (p_timeid-10000) and p_timeid " +
			"       Order by TimeId desc limit 0,20 " +
			" 	  ) as ma20 " +
			"     ON ma20.TimeId = ins.TimeId " +
			" JOIN "+
			"     (	SELECT TimeId,avg(ClosePrice) as ma60 " +
			"       FROM threela.Index as t " +
			"       WHERE Timeid BETWEEN (p_timeid-10000) AND p_timeid " +
			"       Order by TimeId desc limit 0,60 " +
			" 	  ) as ma60 " +
			"     ON ma60.TimeId = ins.TimeId " +			
			" JOIN " +
			"     ( SELECT TimeId , std(ClosePrice) *3  as std3 " +
			" 		FROM  threela.trading as t " +
			"       WHERE TimeId BETWEEN (p_timeid-10000) AND  p_timeid " +
			"       ORDER BY TimeId DESC Limit 0,60 ) as std " +
			" 	  ON std.TimeId = ins.TimeId " +
			" JOIN "+
			"     (	SELECT TimeId,avg(BSRate) as bsma20 " +
			"       FROM threela.Index as t " +
			"       WHERE Timeid BETWEEN (p_timeid-10000) AND p_timeid " +
			"       Order by TimeId desc limit 0,20 " +
			" 	  ) as bsma20 " +
			"     ON bsma20.TimeId = ins.TimeId " +
			" WHERE ins.TimeId = p_timeid;" +
			" end $$ " +
			" delimiter ; ";
		
	}
	
	public static void UpdateDb(Statement smUpdate,int timeId){
		StringBuilder sql ; // 放 sql 語法
		sql = new StringBuilder( "call store_UpdateIndex_MA( "+timeId+")");
		System.out.println("count: " + count + "sql:" + sql);
		count++;
		try{
			smUpdate.executeUpdate(sql.toString());
		}catch(SQLException e){
			System.out.println("sql: "+sql);
			e.printStackTrace();			
		}
	}

	public static void main(String[] args) {
		 //連資料庫		
		int timeId = 0;
		String connectionUrl = null;
		Connection conn = null;
		Statement sm = null;	   //  用在 select
		Statement smUpdate = null; // 用在 update
		//Statement smUpdate = null;
		//ResultSet rs = null;
		int i=0; // 看處理到那個位置
        try {
	        Class.forName("com.mysql.jdbc.Driver");	
			
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");

			sm = conn.createStatement();
			smUpdate = conn.createStatement();
			//String sqlStr = " SELECT TimeId,  FROM threela.index WHERE TimeId >20050101";
			String sqlStr = "  SELECT TimeId  FROM threela.index WHERE TimeId >20020301 and ma60 is null"; //??? 在此修改日期
			
			System.out.println("sql: "+ sqlStr);
			ResultSet rs = sm.executeQuery(sqlStr);			

			while (rs.next()){				
				//System.out.print("1:"+ rs.getString("StockId")+ " ");
				timeId = Integer.parseInt(rs.getString("TimeId") );
				UpdateDb(smUpdate,timeId);
			}		
		} catch (ClassNotFoundException e1) {
			System.out.println("JDBC沒有驅動程式" + e1.getMessage());			
		} catch (SQLException e1) {
			System.out.println("time: "+ timeId);
			e1.printStackTrace();		
	    } catch(Exception e){
	    	System.out.println("time: "+ timeId);
	        System.out.println( e.toString() );
	    } finally{
	    	System.out.println("end !!" );
			if (sm!=null || conn!= null){ 
				try{
					sm.close();
					conn.close();
				}catch(Exception e){
					System.out.println("error :"+ e.toString());
				}			
			}
	    } 
		
		System.out.println("====================");
	}

}
