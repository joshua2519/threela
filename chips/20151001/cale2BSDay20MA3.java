import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class cale2BSDay20MA3 {
	//將數字前面補滿0回傳
	static int count = 0; // 計算已經執行幾筆
	public static String zfill(int num,int fill){
		String tmp="";
		tmp = "0000000000"+num;
		tmp = tmp.substring(tmp.length()-fill, tmp.length() );	
		return tmp;
	}

	public static void Insertsql(){
		String sql = 
				" delimiter $$ " +
						" create procedure store_tradin2BSDay20MA(IN p_stockid varchar(11), In p_timeid int) " +
						" Begin " +
						" INSERT INTO threela.tradin2BSDay20MA( 	 " +
						" 	StockId, TimeId, OpenPrice, ClosePrice, HighPrice, LowPrice,     " +
						"     Volume, YieldRate, PE, Buy20,Sell20,BuySell,       " +
						"     BSDay20,BSRate,BSRateMA20,  " +
						"     MA5 ,MA20, MA60,  " +
						"     UBand, LBand ) 	 " +
						" SELECT       " +
						" 	ins.StockId, ins.TimeId, ins.OpenPrice, ins.ClosePrice, ins.HighPrice, ins.LowPrice,       " +
						" 	ins.Volume, ins.YieldRate, ins.PE, ins.Buy20,ins.Sell20,ins.BuySell,   " +
						"     sum.sum, ins.BSRate, BSRateMA20.BSRateMA20 " +
						"     ma5.ma5, ma20.ma20, ma60.ma60,  " +
						"     ma60.ma60 +std.std3 as UBand ,      " +
						"     ma60.ma60-std.std3 as LBand       " +
						"     FROM threela.trading as ins       " +
						"     JOIN      " +
						"     (	select StockID,TimeId, sum(t.BuySell) as sum 		 " +
						"     FROM threela.trading as t 		 " +
						"     where t.StockId = p_stockid and t.Timeid in	( 			 " +
						" 		select * from ( 				 " +
						"         SELECT TimeId FROM threela.index  				 " +
						"         where TimeId between (p_timeid-10000) and  p_timeid " +
						"         order by TimeId desc limit 0,20 			 " +
						"         )temp 		 " +
						"     )      " +
						"     ) as sum      " +
						" 	ON sum.StockId =ins.StockID and sum.TimeId = ins.TimeId   " +
						"      " +
						"     JOIN      " +
						"     (	select StockID,TimeId,avg(BSRate) as BSRateMA20 " +
						"     from threela.trading as t " +
						"     where t.StockId = p_stockid and t.Timeid in	( " +
						" 		select * from ( " +
						" 			select TimeId from threela.index " +
						"             where TimeId between (p_timeid-10000) and  p_timeid " +
						"             Order by TimeId desc limit 0,20 			 " +
						" 		) temp 		)  	 " +
						" 	) as BSRateMA20 " +
						"     ON BSRateMA20.StockID = ins.StockID and BSRateMA20.TimeId = ins.TimeId      " +
						"      " +
						"     JOIN      " +
						"     (	select StockID,TimeId,avg(ClosePrice) as ma5  " +
						"     from threela.trading as t		 " +
						"     where t.StockId = p_stockid and t.Timeid in	( 			 " +
						" 		select * from ( 				 " +
						" 			select TimeId from threela.index 				 " +
						"             where TimeId between (p_timeid-10000) and  p_timeid " +
						"             Order by TimeId desc limit 0,5 			) temp 		)  	) as ma5      " +
						"     ON ma5.StockID = ins.StockID and ma5.TimeId = ins.TimeId      " +
						"     JOIN      " +
						"     ( select StockID,TimeId,avg(ClosePrice) as ma20 from threela.trading as t		 " +
						"     where t.StockId = p_stockid and t.Timeid in	( 			 " +
						" 		select * from ( 				 " +
						" 			select TimeId from threela.index 				 " +
						"             where TimeId between (p_timeid-10000) and  p_timeid " +
						"             Order by TimeId desc limit 0,20 			) temp 	    )     ) as ma20      " +
						"     ON ma20.StockID = ins.StockID and ma20.TimeId = ins.TimeId      " +
						"     JOIN      " +
						"     (  select StockID,TimeId,avg(ClosePrice) as ma60 from threela.trading as t	 " +
						"     where t.StockId = p_stockid and t.Timeid in	( 			 " +
						" 		select * from ( 				 " +
						" 			select TimeId from threela.index 				 " +
						"             where TimeId between (p_timeid-10000) and  p_timeid " +
						" 			Order by TimeId desc limit 0,60 			) temp 	    )     ) as ma60      " +
						" 	ON ma60.StockID = ins.StockID and ma60.TimeId = ins.TimeId      " +
						"     JOIN      " +
						"     ( select t.StockId, TimeId , std(ClosePrice) *3  as std3        " +
						" 		from threela.trading as t 		 " +
						"         where t.StockId = p_stockid and t.Timeid in	( 		   " +
						" 			select * from ( 			   " +
						"             Select TimeId from threela.index  				 " +
						"             where TimeId between (p_timeid-10000) and  p_timeid	   " +
						"             order by TimeId desc  			  Limit 0,60 		  ) tmp 	  )     ) as std 	 " +
						" 	on std.StockId = ins.StockId and std.TimeId = ins.TimeId      	 	 " +
						"     where ins.TimeId = p_timeid and ins.StockID = p_stockid ; " +
						"  " +
						" end $$ " +
						" delimiter ; ";		
	}

	public static void sqlUpdate(){
		/*
		call store_UpdateTradingMA(1101,20150604,20150804)
		drop procedure store_UpdateTradingMA
		
		delimiter $$
		create procedure store_UpdateTradingMA(In p_stockid  varchar(11),In p_beginDay int , In p_timeid int)
		Begin
		
		UPDATE threela.trading as up
		JOIN
		 (	select p_stockid COLLATE utf8_unicode_ci as StockID,p_timeid as TimeId, sum(t.BuySell) as sum 		 
		    FROM threela.trading as t 		 
		    where t.StockId = p_stockid COLLATE utf8_unicode_ci and t.Timeid in	( 			 
		    select * from ( 				 
		        SELECT TimeId FROM threela.index  				 
		        where TimeId between p_beginDay and  p_timeid 
		        order by TimeId desc limit 0,20 			 
		    )temp 		 
		 )      
		 ) as sum      
		ON sum.StockId = up.StockID and sum.TimeId = up.TimeId
		  
		 JOIN
		 (	select  p_stockid COLLATE utf8_unicode_ci as StockID,p_timeid as TimeId,avg(BSRate) as BSRateMA20 
		    from threela.trading as t 
		    where t.StockId = p_stockid COLLATE utf8_unicode_ci and t.Timeid in	( 
		    select * from ( 
		        select TimeId from threela.index 
		         where TimeId between p_beginDay and  p_timeid 
		         Order by TimeId desc limit 0,20 			 
		    ) temp 		)
		) as BSRateMA20
		 ON BSRateMA20.StockID = up.StockID and BSRateMA20.TimeId = up.TimeId
		
		 JOIN      
		 (	select  p_stockid COLLATE utf8_unicode_ci as StockID,p_timeid as TimeId,avg(ClosePrice) as ma5  
		 from threela.trading as t		 
		 where t.StockId = p_stockid COLLATE utf8_unicode_ci and t.Timeid in	( 			 
		    select * from ( 				 
		        select TimeId from threela.index 				 
		         where TimeId between p_beginDay and  p_timeid 
		         Order by TimeId desc limit 0,5 			) temp 		)  	) as ma5      
		 ON ma5.StockID = up.StockID and ma5.TimeId = up.TimeId
		 JOIN      
		 ( select  p_stockid COLLATE utf8_unicode_ci as StockID,p_timeid as TimeId,avg(ClosePrice) as ma20 from threela.trading as t		 
		 where t.StockId = p_stockid COLLATE utf8_unicode_ci and t.Timeid in	( 			 
		    select * from ( 				 
		        select TimeId from threela.index 				 
		         where TimeId between p_beginDay and  p_timeid 
		         Order by TimeId desc limit 0,20 			) temp 	    )     ) as ma20      
		 ON ma20.StockID = up.StockID and ma20.TimeId = up.TimeId
		 JOIN      
		 (  select  p_stockid COLLATE utf8_unicode_ci as StockID,p_timeid as TimeId,avg(ClosePrice) as ma60 from threela.trading as t	 
		 where t.StockId = p_stockid COLLATE utf8_unicode_ci and t.Timeid in	( 			 
		    select * from ( 				 
		        select TimeId from threela.index 				 
		         where TimeId between (p_timeid-10000) and  p_timeid 
		        Order by TimeId desc limit 0,60 			) temp 	    )     ) as ma60      
		ON ma60.StockID = up.StockID and ma60.TimeId = up.TimeId
		 JOIN      
		 ( select p_stockid COLLATE utf8_unicode_ci as StockId, p_timeid as TimeId , std(ClosePrice) *3  as std3        
		    from threela.trading as t 		 
		     where t.StockId = p_stockid COLLATE utf8_unicode_ci and t.Timeid in	( 		   
		        select * from ( 			   
		         Select TimeId from threela.index  				 
		         where TimeId between (p_timeid-10000) and  p_timeid	   
		         order by TimeId desc  			  Limit 0,60 		  ) tmp 	  )     ) as std 	 
		on std.StockId = up.StockId and std.TimeId = up.TimeId
		SET 
		    up.BSDay20 = sum.sum,
		    up.BSRateMA20 = BSRateMA20.BSRateMA20,
		    up.MA5 = ma5.ma5 ,
		    up.MA20 =ma20.ma20,
		    up.MA60 = ma60.ma60,
		    up.UBand = ma60.ma60 + std.std3,
		    up.LBand = ma60.ma60 - std.std3
		
		where UP.TimeId = p_timeid and UP.StockID = p_stockid COLLATE utf8_unicode_ci ; 
		 
		 end $$ 
		 delimiter ; 

		 */		
	}
	public static void InsertDB(Statement sm,String StockId,int TimeId){
		StringBuilder sql ; // 放 sql 語法
		//sql = new StringBuilder( "call store_tradin2BSDay20MA("+StockId+" , "+TimeId+")");
		int beginBetweenDay = Integer.parseInt(  (TimeId+"" ).substring(4, 6)  ) < 3 ? TimeId - 9000 : TimeId - 200;
		sql = new StringBuilder( "call store_UpdateTradingMA('"+StockId+"' , "+beginBetweenDay+ " , "+TimeId + ")");
		if (count % 100 == 0 )System.out.println("count: " + count + "  sql:"+ sql.toString());
		try{
			sm.executeUpdate(sql.toString());
			count ++;
		}catch(SQLException e){
			System.out.println("error update sql:"+ sql.toString());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {	
		//連資料庫	 ，不需要修改日期		 
		String connectionUrl = null;
		Connection conn = null;
		Statement sm = null;		 
		Statement smUpdate = null;
		//ResultSet rs = null;

		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH)+1;
		int day = today.get(Calendar.DATE);
		//資料庫內的日期格式為: yyyymmdd 。以 20150818 ，則是2015年8月18日
		int date = Integer.parseInt(year+ zfill(month,2) + zfill(day,2) );  //設定開始執行日期，設定為二個月前的資料
		// 找二個月前的日期
		if (month == 1) date = date - 9000;   // 往前推二個月
		else 			date = date - 200; 
		//date = 20130630;  // 修改開始日期
		ArrayList StockId = new ArrayList();
		ArrayList TimeId = new ArrayList();
		int i=0; // 看處理到那個位置
        try {
	        Class.forName("com.mysql.jdbc.Driver");				
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");
			sm = conn.createStatement();
			//String sqlStr = " SELECT StockId, TimeId,count(*)  FROM threela.daily WHERE TimeId >20130828 group by StockId, TimeId having count(*) > 20";
			String sqlStr = 
				" SELECT  StockId, TimeId FROM trading \n"+
				" WHERE  TimeId > " + date + " and (MA60 is NULL) " + //" AND BuySell is not NULL \n" + //???? 需改回來
				" ORDER BY TimeId ";

			System.out.println("sql: "+ sqlStr);
			ResultSet rs = sm.executeQuery(sqlStr);			

			while (rs.next()){				
				//System.out.print("1:"+ rs.getString("StockId")+ " ");
			 	StockId.add(rs.getString("StockId").toString() );
			 	TimeId.add( rs.getString("TimeId").toString());  
			}		
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("JDBC沒有驅動程式" + e1.getMessage());			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();		
	    } catch(Exception e){
	        System.out.println( e.toString() );
	    } finally{
	    	if (sm != null){
	    		try{
	    			sm.close();
	    		}catch(Exception e){
	    			System.out.println("sm error:"+ e.toString());
	    		}
	    	}	    	
	    } 
		
		System.out.println("====================");
		try {
			smUpdate = conn.createStatement();
			for (i=0;i<StockId.size();i++){
				 //System.out.print("2:"+ StockId.get(i) );
				 InsertDB(smUpdate ,StockId.get(i).toString() ,Integer.parseInt(TimeId.get(i).toString()) );
				 //if (i %50 == 0) System.out.println("count:"+i+ " StockId:"+StockId.get(i) + " Date:"+ TimeId.get(i) );
				 //break;
			}
		}catch(Exception e){
			System.out.println("error :"+ e.toString());
		}finally{
			if (StockId.size() > 0) System.out.println("count:"+i+ " StockId:"+StockId.get(i-1) + " Date:"+ TimeId.get(i-1)  );
			try {			
				for (int j=0;j<2;j++){
					Thread.sleep(100);
					java.awt.Toolkit.getDefaultToolkit().beep(); 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (sm!=null || conn!= null){ 
				try{
					sm.close();
					conn.close();
				}catch(Exception e){
					System.out.println("error :"+ e.toString());
				}			
			}
	    	StockId.clear();
	    	TimeId.clear();
	    	System.out.println("End !!");
		}
	}
}
