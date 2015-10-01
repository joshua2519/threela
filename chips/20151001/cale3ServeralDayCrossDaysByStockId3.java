import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class cale3ServeralDayCrossDaysByStockId3 {
	
	//�N�Ʀr�e���ɺ�0�^��
	public static String zfill(int num,int fill){
		String tmp="";
		tmp = "0000000000"+num;
		tmp = tmp.substring(tmp.length()-fill, tmp.length() );	
		return tmp;
	}
	
	
	//writeToTradingDB(sm,stockId, timeId, beforePNDays,beforeCrossDays,beforeOverBS); // �N��Ѫ� 1.�R��W����ѼơA2.�O�_�������Φ��`��e�A3.���e�O�_���W�V3���зǮt  �g�JTrading��Ʈw
	public static void UpdateTradingDB(Statement sm,String stockId, int timeId, int PNDays, int crossDays, int overBS){
		//System.out.println("before UpdateTradingDB:");
		StringBuilder sql = new StringBuilder("UPDATE trading "+
					" SET PNDays = "+PNDays+" ,crossDays = "+crossDays+", overBS = "+overBS +
					" WHERE StockId = '"+stockId+"' COLLATE utf8_unicode_ci AND TimeId = "+timeId );
		try{
			sm.executeUpdate(sql.toString());
			sm.execute("COMMIT ;");
		} catch (SQLException e) {
			System.out.println("error Update trading sql:"+sql);
			e.printStackTrace();
		}catch (Exception e){
			System.out.println("error Update trading2 sql:"+sql);
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
	public static void InsertTractionDB(Statement sm,String stockId, int timeId,int isbs){
		StringBuilder sql = new StringBuilder(
			" INSERT INTO transaction (StockId, FindStartTimeId, Cate) Values( '"+
			stockId+"' COLLATE utf8_unicode_ci," +  timeId +","+ isbs +")"	);		
		// System.out.println("sql: "+ sql);
		try{
			sm.executeUpdate(sql.toString());			
		} catch (SQLException e) {
			System.out.println("error sql:"+sql);
			e.printStackTrace();
		}catch (Exception e){
			System.out.println("error Insert transaction sql:"+sql);
			e.printStackTrace();
		}finally{}
	}
	public static void updateTractionDB(Statement sm,String stockId, int timeId,int isbs){
		
		StringBuilder sql = new StringBuilder(
			" UPDATE transaction " +
			" SET "+			
			" FindEndTimeId = " + timeId +
			" WHERE StockId = '"+stockId+ "' COLLATE utf8_unicode_ci and FindEndTimeId is NULL"
		);
		if (isbs == 22) 	System.out.print("�^�� ");
		else if(isbs == 12) System.out.print("��X ");
		System.out.println("sql"+sql);

		try{
			sm.executeUpdate(sql.toString());			
		} catch (SQLException e) {
			System.out.println("sql:"+sql);
			e.printStackTrace();
		}catch (Exception e){
			System.out.println("error Update transaction sql 2:"+ sql);
			e.printStackTrace();
		}finally{}
		System.out.println("after Update:");
	}	

	public static void getData(Connection conn, String stockId , int timeId ){
		Statement sm = null;
		Statement smUpdate = null;
		//stockId = 2106;
		//int timeId = 0; //���		
		int getYestarday =  (Integer.parseInt(  (timeId + "" ).substring(4,6)  ) == 1 )   
				? timeId - 8900 : timeId - 100; //�Φb�������Ѫ��Q�ѩM���   �C�]�w ��@�Ӥ�e��� 

		double highPrice = 0 ;;//�̰���
		double lowPrice = 0 ;;//�̧C��
		double closePrice = 0; //���L��
		int bsday20=0; 		// ��Ʈw��U�Ӫ��s��20��R��W
		double BSRateMA20 = 0 ; // �s��20��R��W����
		double ma5 = 0;  //5�鲾�ʥ����u
		double ma60 = 0; //60�鲾�ʥ����u
		double uBand = 0;//�T�� BBand�W�t
		double lBand = 0;//�T�� BBand�U�t

		int PNDays = 0; // �R��W����Ѽ�
		int beforePNDays = 0;//�e�@�ӶR��W����Ѽ�
		int crossDays = 0; //ma5 ��Vma60 ���Ѽ� �C + �b�W�X��    -�b�U�X��
		int beforeCrossDays = 0; //�e�@�� ma5 ��Vma60 ���Ѽ� �C + �b�W�X��    -�b�U�X��
		int overBS = 0 ;  //���e�O�_����V3���зǮt�A�å��X��
		int beforeOverBS = 0 ;  //�e�@����骺 ���e�O�_����V3���зǮt�A�å��X��
		int isbs = 0;//�O�_�R�ν�  0->�S���@�R��   11->�R�i  12->�R�i�᪺��X     21->�@��   22->�@�ū᪺�^��
		int count = 0;// �ݲ{�b�O�b�e�@�����άO��ѥ����
		boolean isDebug = false; //�O�_�b�����Ҧ�
/*  �Ω� Insert	   // �� BSRateMA20
		StringBuilder sql = new StringBuilder( 
			"SELECT StockId, TimeId,HighPrice,LowPrice ,ClosePrice,BSDay20,MA5,MA60,UBand,LBand, PNDays, CrossDays, OverBS "+  
			" FROM threela.trading "+ 
			" WHERE StockId = "+stockId ) ;
*/
		StringBuilder sql = new StringBuilder(
			" SELECT t.StockId, TimeId,HighPrice,LowPrice ,ClosePrice,"+
			" BSDay20,BSRateMA20,MA5,MA60,UBand,LBand, PNDays, CrossDays, OverBS ,cate.Cate as cate" +
			" FROM threela.trading  as t " +
			" LEFT JOIN( \n" +
			"     SELECT StockId,Cate FROM threela.transaction \n"+
			"	  WHERE StockId = '"+stockId+"' COLLATE utf8_unicode_ci \n"+
			"	  AND FindStartTimeId < " + timeId + " AND FindEndTimeId is NULL" +
			" ) as cate " +
			" ON t.StockId = cate.StockId \n" +
			" WHERE t.StockId = '"+stockId+ "' COLLATE utf8_unicode_ci AND (t.BSDay20 is not null) "+
			" AND t.TimeId IN ( " +
			"     SELECT * FROM (" +
			"         SELECT TimeId FROM threela.index  \n" +
			"         WHERE TimeId BETWEEN "+ getYestarday +" AND " +timeId +
			"         ORDER BY TimeId DESC LIMIT 0,2  " +
			"     )tmp " +
			"  ) " +
			"  ORDER BY TimeId ");

		if (isDebug == true)System.out.println("sql:"+sql);
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
				 	//�� �R��W����Ѽ� ????? �A�� stockid =2114 9��
			 		/*
				 	if (rs.getString("BSDay20") !=null) bsday20 = Integer.parseInt(rs.getString("BSDay20").toString());
				 	if (bsday20 >0){		PNDays = (beforePNDays < 0)?  1 : beforePNDays+1;
				 	}else if (bsday20 <0){	PNDays = (beforePNDays > 0)? -1 : beforePNDays-1;
				 	}else if (bsday20 ==0){ PNDays = beforePNDays; //�R��W�Ȭ�0�A�O�d�e��
					}
					*/
				 	if (rs.getString("BSRateMA20") !=null) BSRateMA20 = Double.parseDouble(rs.getString("BSRateMA20").toString());
				 	if (BSRateMA20 >0){			PNDays = (beforePNDays < 0)?  1 : beforePNDays+1;
				 	}else if (BSRateMA20 <0){	PNDays = (beforePNDays > 0)? -1 : beforePNDays-1;
				 	}else if (BSRateMA20 == 0){ PNDays = beforePNDays; //�R��W�Ȭ�0�A�O�d�e��
					}
				 	if (isDebug == true) System.out.println("BSRateMA20: "+ BSRateMA20 + " PNDays:"+PNDays);
				 	//�� ma5 ��Vma60
				 	if (rs.getString("MA5").toString() !=null)  ma5 = Float.parseFloat(rs.getString("MA5").toString());
				 	if (rs.getString("MA60").toString() !=null) ma60= Float.parseFloat(rs.getString("MA60").toString());
				 	if (ma5 > ma60){ 		crossDays = (beforeCrossDays < 0)? 1: beforeCrossDays +1;			 		
				 	}else if (ma5 < ma60){	crossDays = (beforeCrossDays > 0)?-1: beforeCrossDays -1;			 		
				 	}else if (ma5 == ma60 && ma5 !=0 ){ crossDays = (beforeCrossDays > 0)?-1: 1; //ma5 = ma60 �ɡA½��
					}else{							    crossDays = beforeCrossDays;  			//ma5 ��0�ɡA�O�d�e��						
					}

				 	//����e�O�_����V3���зǮt
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
				 		if (isbs == 11){ isbs = 12;  System.out.println("id:"+stockId +" time:"+timeId + " �X12_80:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 			updateTractionDB(smUpdate,stockId, timeId,isbs); 
				 		}// ��X (���e����V3���зǮt�A��ӥX�{���`��t�άO�ন��W)
					}else if (beforeOverBS == -1 && (crossDays > 0 || PNDays > 0) ){  
						overBS = 0;
						System.out.println("overBS == -1->0 " + " crossDays:"+ crossDays + " PNDays:"+PNDays);
						if (isbs == 21) { isbs = 22; System.out.println("id:"+stockId +" time:"+timeId + "�^��22_83:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
							updateTractionDB(smUpdate,stockId, timeId,isbs);
						}// �^�� (���e����V3���зǮt�A��ӥX�{������t�άO�ন�R�W)
					}
				 	if ( (PNDays >0 && crossDays > 0) && (highPrice >= uBand || beforeOverBS == 1 )){ 		overBS = 1;			 		
				 	}else if ((PNDays <0 && crossDays < 0) && (lowPrice <= lBand || beforeOverBS == -1 )){ overBS = -1;			 		
					}

				 	if (overBS == 1){	System.out.println("�Ѳ��N�X"+stockId + " ���"+timeId+" �T���W�з�overBS:"+overBS + " HighPrice:"+highPrice + " uBand:"+uBand + " crossDays:"+ crossDays + " PNDays:"+PNDays);
				 	}else if (overBS == -1){				System.out.println("�Ѳ��N�X"+stockId + " ���"+timeId+" �T���U�з�overBS:"+overBS + " lowPrice:"+lowPrice+" lBand:"+lBand + " crossDays:"+ crossDays + " PNDays:"+PNDays);
				 	}

				 	//�R�i�Χ@�� �Cisbs = 11 ���R�i   �C  isbs = 12 ����X   21�@��   22�^�� 
				 	if ( isbs != 11 && (beforePNDays < 0 && PNDays > 0) &&  crossDays > 0 && crossDays <=10 ){
				 		if (isbs == 21) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " �^��21:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 11;
				 		System.out.println("id:"+stockId +" time:"+timeId + " �R84:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	} 			 		
				 	if ( isbs != 21 && (beforePNDays > 0 && PNDays < 0) &&  crossDays < 0 && crossDays >=-10){
				 		if (isbs == 11) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " �X11:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 21;
				 		System.out.println("id:"+stockId +" time:"+timeId + " ��85:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs != 11 && (beforeCrossDays < 0 && crossDays >0) && PNDays > 0 && PNDays <=10)   {
				 		if (isbs == 21) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " �^��21:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 11;
				 		System.out.println("id:"+stockId +" time:"+timeId + " �R11:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs != 21 && (beforeCrossDays > 0 && crossDays <0) && PNDays < 0 && PNDays >=-10)  {
				 		if (isbs == 11) {
				 			System.out.println("id:"+stockId +" time:"+timeId + " �X11:");
				 			updateTractionDB(smUpdate,stockId, timeId,isbs);
				 		}
				 		isbs = 21;
				 		System.out.println("id:"+stockId +" time:"+timeId + " ��87:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		InsertTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 11 && (beforePNDays > 0 && PNDays < 0)  &&  crossDays <0 )    { 
				 		isbs = 12;
				 		System.out.println("id:"+stockId +" time:"+timeId + " ��88:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 11 && (beforeCrossDays > 0 && crossDays < 0)  &&  PNDays <0 ) { 
				 		isbs = 12;  
				 		System.out.println("id:"+stockId +" time:"+timeId + " ��89:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 21 && (beforePNDays < 0 && PNDays > 0)  &&  crossDays >0 )    { 
				 		isbs = 22;  
				 		System.out.println("id:"+stockId +" time:"+timeId + "�^��90:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	if ( isbs == 21 && (beforeCrossDays < 0 && crossDays > 0)  &&  PNDays >0 ) { 
				 		isbs = 22;
				 		System.out.println("id:"+stockId +" time:"+timeId + "�^��91:"+ "bsday20:"+ bsday20 + " MA5:"+ma5+" MA60:"+ma60+" HighPrice:"+highPrice +" uBand:"+uBand + "lBand:"+lBand  + " crossDays:" + crossDays +" PNDays:"+PNDays );
				 		updateTractionDB(smUpdate,stockId, timeId,isbs);
				 	}
				 	//System.out.println("�e�@�����");				 	

				 	if (isbs == 12 || isbs == 22) isbs = 0;  //���@�X����X�A�άO�@�Ŧ��^�ɮɡA��ܲ{�b�S�����ѡA�Хܬ�0
			 	}	// end of if (count == 1){  	

			 	// �O���e�@����骺��
			 	beforePNDays = PNDays;   beforeCrossDays=crossDays; beforeOverBS = overBS;
			 	if (isDebug == true) System.out.println("beforePNDays: "+ beforePNDays + " beforePNDays"+beforeCrossDays + " beforeOverBS:"+ beforeOverBS);
			 	if ( count == 1) UpdateTradingDB(smUpdate,stockId, timeId, PNDays,crossDays,overBS); // �N��Ѫ� 1.�R��W����ѼơA2.�O�_�������Φ��`��e�A3.���e�O�_���W�V3���зǮt  �g�JTrading��Ʈw			 	
				count ++;
			}   // end while (rs.next()){
			
		} catch (SQLException e) {
			e.printStackTrace();		
	    } catch(Exception e){
	    	System.out.println("error level 2 !!"+" stockId:"+ stockId + "  timeId:"+timeId);
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
        //�s��Ʈw
		ArrayList stockId = new ArrayList(); // ��n�[��Ѳ��N�X
		StringBuilder sql = new StringBuilder("");  // �� sql �y�k		
		String connectionUrl = null;
		Connection conn = null;
		Statement sm = null;
		Statement smTime = null;
		Statement smUpdate = null; // �Φb  �s�W�B��s�W
		String yearStr= null; // �r��~ 
		String monthStr = null;//  �r���
		String dayStr = null; // �r���
		int countStockId = 0; // �p�⦳�X���Ѳ��N�X		
		//int date = 0; //���

		Calendar today = Calendar.getInstance();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH)+1;
		int day = today.get(Calendar.DATE);
		int beginFindDate = Integer.parseInt(year+ zfill(month,2) + zfill(day,2) ); // �}�l������
		if (month == 1) beginFindDate = beginFindDate - 9000;
		else 			beginFindDate = beginFindDate - 200;
		ResultSet rsObserveStockId = null;
		//beginFindDate = 20130715; // �ק�}�l������
		//LocalDate birthDate = new LocalDate(DateTime.now() );
        try {
	        Class.forName("com.mysql.jdbc.Driver");
			String connUrl = "jdbc:mysql://127.0.0.1:3306/threela?useUnicode=true&characterEncoding=UTF8";
			conn = DriverManager.getConnection(connUrl, "threela", "123456");
			//smUpdate = conn.createStatement();
			//int StockId[]={1101,1102,1104,1216,1229,1256,1262,1301,1314,1409,1434,1440,1476,1477,1503,1504,1507,1580,1589,1590,1605,1707,1717,1722,1723,1736,1785,1789,1795,1802,1907,2002,2006,2015,2035,2049,2059,2101,2104,2105,2106,2201,2204,2206,2207,2227,2231,2301,2308,2325,2327,2330,2331,2337,2352,2353,2356,2362,2373,2376,2377,2379,2382,2385,2392,2393,2395,2409,2412,2448,2449,2451,2474,2501,2511,2542,2548,2603,2606,2608,2615,2618,2634,2637,2707,2723,2727,2801,2812,2820,2834,2847,2849,2855,2867,2883,2887,2889,2890,2903,2923,2926,3008,3034,3037,3044,3060,3088,3105,3176,3189,3211,3227,3265,3293,3376,3452,3474,3481,3490,3520,3529,3532,3552,3558,3576,3611,3658,3662,3673,3682,3693,4103,4105,4120,4123,4152,4157,4162,4163,4198,4401,4416,4733,4736,4911,4938,4944,4947,4966,5007,5009,5263,5264,5274,5287,5289,5306,5351,5356,5392,5398,5425,5457,5478,5483,5489,5490,5508,5512,5522,5530,5534,5820,5871,5903,5904,6005,6016,6023,6105,6116,6121,6146,6147,6166,6176,6188,6214,6223,6239,6244,6261,6269,6274,6286,6409,6415,6451,6605,6803,8042,8044,8046,8050,8069,8076,8083,8086,8121,8150,8234,8255,8299,8349,8406,8422,8454,8917,8936,8942,9907,9910,9917,9921,9930,9938,9940,9945,9951};			
			sql = new StringBuilder(
					" SELECT stockid FROM totaletf "+   // ETF
					" UNION \n"+
					" SELECT stockid FROM marketcapitali "+ // �x�W50 , ���x100, �I�d50
					" UNION \n"+
					" SELECT stockid FROM observe "    // �ۤw���[��W��
				  );
			sm = conn.createStatement();
			rsObserveStockId =sm.executeQuery(sql.toString());
			System.out.println("stockId:");
			while (rsObserveStockId.next()){
				stockId.add( rsObserveStockId.getString("stockid").toString());
				if (countStockId % 15 == 0) System.out.println("");
				System.out.print(rsObserveStockId.getString("stockid").toString()+ " ,");
				countStockId ++;
			}

			smTime = conn.createStatement();
			// ??? �b���ק�}�l�n�B�z��������  //??? �b���ק���			
			sql = new StringBuilder(
				  " SELECT TimeId FROM trading "								   	   	 //  ??? �U�����A buySell �ݭn���� BSDay20 �A���O�ק�{���ɡA BSDay20���ȡA�ҥH�Ȯɨϥ� buySell
				+ " WHERE  StockId in ('1101', '2330' , '2317' , '1216' , '1301', '2412' , '2801', '2881', '2882') and "
				+ " TimeId > "+beginFindDate+"  and (PNDays =0  or CRossDays = 0 ) "
				+ " AND MA60 is not NULL "
				+ " ORDER by TimeId ");
			ResultSet rs =smTime.executeQuery(sql.toString());
			int count = 0;
			while(rs.next()){
				for (int i=0;i< stockId.size()  ; i++){
					//System.out.println("count: "+ count + " stockId:"+stockId.get(i).toString() + " TimeId:" + rs.getString("TimeId") );
					getData(conn,stockId.get(i).toString() ,Integer.parseInt(rs.getString("TimeId")) );
					count ++;
				}
			}	

		} catch (ClassNotFoundException e1) {
			System.out.println("JDBC�S���X�ʵ{��" + e1.getMessage());			
		} catch (SQLException e1) {
			e1.printStackTrace();		
	    } catch(Exception e){
	        System.out.println( e.toString() );
	    }finally{
	    	if (sm != null || rsObserveStockId != null) {
	    		try{	
	    			rsObserveStockId.close();
	    			sm.close();
	    		} catch(Exception e){	e.printStackTrace();
	    		}
	    	}
	    	if (conn != null){
	    		try{	    			conn.close();
	    		} catch(Exception e){	e.printStackTrace();
	    		}
	    	}
	    	System.out.println("End !!");
	    }        

	} // end of main
}
