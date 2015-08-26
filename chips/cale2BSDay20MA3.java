import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class cale2BSDay20MA3 {
	//將數字前面補滿0回傳
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
						"     BSDay20,BSRate,BSRate20,  " +
						"     MA5 ,MA20, MA60,  " +
						"     UBand, LBand ) 	 " +
						" SELECT       " +
						" 	ins.StockId, ins.TimeId, ins.OpenPrice, ins.ClosePrice, ins.HighPrice, ins.LowPrice,       " +
						" 	ins.Volume, ins.YieldRate, ins.PE, ins.Buy20,ins.Sell20,ins.BuySell,   " +
						"     sum.sum, ins.BSRate, BSRate20.BSRate20 " +
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
						"     (	select StockID,TimeId,avg(BSRate) as BSRate20 " +
						"     from threela.trading as t " +
						"     where t.StockId = p_stockid and t.Timeid in	( " +
						" 		select * from ( " +
						" 			select TimeId from threela.index " +
						"             where TimeId between (p_timeid-10000) and  p_timeid " +
						"             Order by TimeId desc limit 0,20 			 " +
						" 		) temp 		)  	 " +
						" 	) as BSRate20 " +
						"     ON BSRate20.StockID = ins.StockID and BSRate20.TimeId = ins.TimeId      " +
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
call store_UpdateTradingMA(1101,20150804)
drop procedure store_UpdateTradingMA


delimiter $$
create procedure store_UpdateTradingMA(In p_stockid varchar(11), In p_timeid int)
Begin

UPDATE threela.trading as up
JOIN      
 (	select p_stockid as StockID,p_timeid as TimeId, sum(t.BuySell) as sum 		 
    FROM threela.trading as t 		 
    where t.StockId = p_stockid and t.Timeid in	( 			 
    select * from ( 				 
        SELECT TimeId FROM threela.index  				 
        where TimeId between (p_timeid-10000) and  p_timeid 
        order by TimeId desc limit 0,20 			 
    )temp 		 
 )      
 ) as sum      
ON sum.StockId = up.StockID and sum.TimeId = up.TimeId
  
 JOIN
 (	select  p_stockid as StockID,p_timeid as TimeId,avg(BSRate) as BSRate20 
    from threela.trading as t 
    where t.StockId = p_stockid and t.Timeid in	( 
    select * from ( 
        select TimeId from threela.index 
         where TimeId between (p_timeid-10000) and  p_timeid 
         Order by TimeId desc limit 0,20 			 
    ) temp 		)
) as BSRate20
 ON BSRate20.StockID = up.StockID and BSRate20.TimeId = up.TimeId

 JOIN      
 (	select  p_stockid as StockID,p_timeid as TimeId,avg(ClosePrice) as ma5  
 from threela.trading as t		 
 where t.StockId = p_stockid and t.Timeid in	( 			 
    select * from ( 				 
        select TimeId from threela.index 				 
         where TimeId between (p_timeid-10000) and  p_timeid 
         Order by TimeId desc limit 0,5 			) temp 		)  	) as ma5      
 ON ma5.StockID = up.StockID and ma5.TimeId = up.TimeId
 JOIN      
 ( select  p_stockid as StockID,p_timeid as TimeId,avg(ClosePrice) as ma20 from threela.trading as t		 
 where t.StockId = p_stockid and t.Timeid in	( 			 
    select * from ( 				 
        select TimeId from threela.index 				 
         where TimeId between (p_timeid-10000) and  p_timeid 
         Order by TimeId desc limit 0,20 			) temp 	    )     ) as ma20      
 ON ma20.StockID = up.StockID and ma20.TimeId = up.TimeId
 JOIN      
 (  select  p_stockid as StockID,p_timeid as TimeId,avg(ClosePrice) as ma60 from threela.trading as t	 
 where t.StockId = p_stockid and t.Timeid in	( 			 
    select * from ( 				 
        select TimeId from threela.index 				 
         where TimeId between (p_timeid-10000) and  p_timeid 
        Order by TimeId desc limit 0,60 			) temp 	    )     ) as ma60      
ON ma60.StockID = up.StockID and ma60.TimeId = up.TimeId
 JOIN      
 ( select p_stockid as StockId, p_timeid as TimeId , std(ClosePrice) *3  as std3        
    from threela.trading as t 		 
     where t.StockId = p_stockid and t.Timeid in	( 		   
        select * from ( 			   
         Select TimeId from threela.index  				 
         where TimeId between (p_timeid-10000) and  p_timeid	   
         order by TimeId desc  			  Limit 0,60 		  ) tmp 	  )     ) as std 	 
on std.StockId = up.StockId and std.TimeId = up.TimeId
SET 
    up.BSDay20 = sum.sum,
    up.BSRateMA20 = BSRate20.BSRate20,
    up.MA5 = ma5.ma5 ,
    up.MA20 =ma20.ma20,
    up.MA60 = ma60.ma60,
    up.UBand = ma60.ma60 + std.std3,
    up.LBand = ma60.ma60 - std.std3

where UP.TimeId = p_timeid and UP.StockID = p_stockid ; 
 
 end $$ 
 delimiter ; 


		 */		
	}
	public static void InsertDB(Statement sm,String StockId,int TimeId){
		StringBuilder sql ; // 放 sql 語法
		//sql = new StringBuilder( "call store_tradin2BSDay20MA("+StockId+" , "+TimeId+")");		
		sql = new StringBuilder( "call store_UpdateTradingMA("+StockId+" , "+TimeId+")");
		//System.out.println("sql:"+ sql.toString());k
		try{
			sm.executeUpdate(sql.toString());
		}catch(SQLException e){
			System.out.println("sql:"+ sql.toString());
			e.printStackTrace();			
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
        //連資料庫		 
		String connectionUrl = null;
		Connection conn = null;
		Statement sm = null;		 
		//Statement smUpdate = null;
		//ResultSet rs = null;
		
		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH)+1;
		int day = today.get(Calendar.DATE);
		//資料庫內的日期格式為: yyyymmdd 。以 20150818 ，則是2015年8月18日
		int date = Integer.parseInt(year+ zfill(month,2) + zfill(day,2) );  //設定一個月前的資料
		// 找一個月前的日期
		if (month == 1) date = date - 8900;   
		else 			date = date - 100; 
		
		ArrayList StockId = new ArrayList();
		ArrayList TimeId = new ArrayList();
		int i=0; // 看處理到那個位置
        try {
	        Class.forName("com.mysql.jdbc.Driver");	
			
			String connUrl = "jdbc:mysql://10.120.30.4:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");

			sm = conn.createStatement();
			//String sqlStr = " SELECT StockId, TimeId,count(*)  FROM threela.daily WHERE TimeId >20130828 group by StockId, TimeId having count(*) > 20";
			String sqlStr = 
				" SELECT  StockId, TimeId FROM trading "+
				" WHERE  TimeId > " + date + " and (BSDay20 is NULL) AND BuySell is not NULL"; //??? 在此修改日期

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
	    } 
		
		System.out.println("====================");
		try {
			for (i=0;i<StockId.size();i++){
				 //System.out.print("2:"+ StockId.get(i) );
				 InsertDB(sm ,StockId.get(i).toString() ,Integer.parseInt(TimeId.get(i).toString()) );
				 if (i %50 == 0) System.out.println("count:"+i+ " StockId:"+StockId.get(i) + " Date:"+ TimeId.get(i) );
			}
		}catch(Exception e){
			System.out.println("error :"+ e.toString());
		}finally{
	    	StockId.clear();
	    	TimeId.clear();

			System.out.println("count:"+i+ " Date:"+ TimeId.get(i) + " StockId:"+StockId.get(i) );
			if (sm!=null || conn!= null){ 
				try{
					sm.close();
					conn.close();
				}catch(Exception e){
					System.out.println("error :"+ e.toString());
				}			
			}			
		}
	}
}
