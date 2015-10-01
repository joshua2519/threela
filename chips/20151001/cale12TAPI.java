import java.sql.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class cale12TAPI {
	public static void sql(){
		StringBuilder sql = new StringBuilder(
			"Update threela.index "+
			" set BSRate = "+
			""
		);
	}
	static int count=0;
	
	public static void UpdateDb(Statement sm, int timeId,ArrayList stockIdArrayList, ArrayList weightArrayList){
		StringBuilder sql = new StringBuilder(
			" Update threela.index "+
			" set BSRate = ("
		);
		count++;

		for (int i=0;i<stockIdArrayList.size();i++){
			sql.append(
				String.format(
					" (SELECT IFNULL(BSRate * %s * 0.01,0) FROM threela.trading WHERE StockId = '%s' AND TimeId = %d) \n+"	
					,weightArrayList.get(i), stockIdArrayList.get(i), timeId
				)
			);
		}
		sql.delete(sql.length()-1, sql.length());
		sql.append(") WHERE TimeId = " + timeId);

		if (count%50 == 0)System.out.println("sql:"+sql);
		System.out.println("sql:"+sql);

		try{
			sm.executeUpdate(sql.toString());
		} catch (Exception e1) {
			System.out.println("sql: "+ sql.toString());
			e1.printStackTrace();
		}
				
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 //�s��Ʈw		 
		String connectionUrl = null;
		Connection conn = null;
		Statement sm = null;  // �d�ߥ� 
		Statement smUpdate = null; //��s��
		ResultSet rs ;
		int timeId = 0;  //���X���
		StringBuilder sql = new StringBuilder();
		//Statement smUpdate = null;
		int i=0; // �ݳB�z�쨺�Ӧ�m
		ArrayList stockIdArrayList = new ArrayList(); 
		ArrayList weightArrayList = new ArrayList();
		sql = new StringBuilder("SELECT StockId, Weight FROM threela.ETF50 " );

		try{
			Class.forName("com.mysql.jdbc.Driver");
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");
			sm = conn.createStatement();
			smUpdate = conn.createStatement();

			rs = sm.executeQuery( sql.toString() );
			while (rs.next()){
				System.out.print("  id:" + rs.getString("StockId").toString(  ) + " weight:"+ rs.getString("Weight").toString()  );
				stockIdArrayList.add(rs.getString("StockId").toString() );
				weightArrayList.add(rs.getString("Weight").toString() );
			}
		} catch (Exception e1) {
			System.out.println("sql: "+ sql.toString());
			e1.printStackTrace();
		}
		sql.setLength(0);		
		
        try {
	        Class.forName("com.mysql.jdbc.Driver");				
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");

			sm = conn.createStatement();
			//String sqlStr = " SELECT TimeId,  FROM threela.index WHERE TimeId >20050101";
			sql.append(" SELECT TimeId  FROM threela.index WHERE BSRate is null and TimeId > 20150701"); // �b���ק���
			
			System.out.println("sql: "+ sql);
			rs= sm.executeQuery(sql.toString());
			
			while (rs.next()){				
				//System.out.print("1:"+ rs.getString("StockId")+ " ");
				timeId =  Integer.parseInt( rs.getString("TimeId") );
				UpdateDb(smUpdate, timeId,stockIdArrayList, weightArrayList  );
				//break;
			}		
		} catch (ClassNotFoundException e1) {
			System.out.println("JDBC�S���X�ʵ{��" + e1.getMessage());			
		} catch (SQLException e1) {
			System.out.println("time: "+  timeId);
			e1.printStackTrace();		
	    } catch(Exception e){
	    	System.out.println("time: "+  timeId);
	        System.out.println( e.toString() );
	    } finally{
	    	System.out.println("end!!" );
			if (sm!=null || conn!= null){ 
				try{
					sm.close();
					conn.close();
				}catch(Exception e){
					System.out.println("error :"+ e.toString());
				}			
			}
	    } 
		
		System.out.println("== end ==================");
	}

}
