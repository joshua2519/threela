import java.sql.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class cale22TAPI {
	public static void sql(){
		
		String sql = 
			" 刪除 drop  procedure store_update_trading " +
			" delimiter $$ " +
			" create procedure store_Index2BSRateMA20( In p_timeid int) " +
			" Begin " +		
				
			" INSERT INTO Index2BSRateMA20  " +
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
	
	public static void UpdateDb(Statement sm,int timeId){
		StringBuilder sql ; // 放 sql 語法
		sql = new StringBuilder( "call store_Index2BSRateMA20( "+timeId+")");
		try{
			sm.executeUpdate(sql.toString());
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
		Statement sm = null;		 
		//Statement smUpdate = null;
		//ResultSet rs = null;
		int i=0; // 看處理到那個位置
        try {
	        Class.forName("com.mysql.jdbc.Driver");	
			
			String connUrl = "jdbc:mysql://10.120.30.4:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");

			sm = conn.createStatement();
			//String sqlStr = " SELECT TimeId,  FROM threela.index WHERE TimeId >20050101";
			String sqlStr = "  SELECT TimeId  FROM threela.index WHERE TimeId >20050101";
			
			System.out.println("sql: "+ sqlStr);
			ResultSet rs = sm.executeQuery(sqlStr);			

			while (rs.next()){				
				//System.out.print("1:"+ rs.getString("StockId")+ " ");
				timeId = Integer.parseInt(rs.getString("TimeId") );
				UpdateDb(sm,timeId);
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
