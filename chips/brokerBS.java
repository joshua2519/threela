
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import cn.easyproject.easyocr.EasyOCR;
import cn.easyproject.easyocr.ImageType;


public class brokerBS {
	final static int sleeptime=5000;
	static int isProxy = 0; // 0-> 不使用 proxy    1->使用 proxy

	
	static String SolveCaptcha(String file,String imgFolder,String OutImgFolder){
		 int dilate_size = 3;
		 int erod_size =1;	
	     Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(dilate_size , dilate_size));
	     Mat erodElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new  Size(erod_size , erod_size));
	     Mat origImage = Highgui.imread( imgFolder+file);
	     Mat greyImage= new Mat();
	     Mat binary = new Mat();
	     Mat dilate= new Mat();
	     Mat erod= new Mat();
	     Mat outImage = new Mat();
	     //Imgproc.dilate(origImage, outImage, dilateElement);
	     //Imgproc.medianBlur(dilate, blur, 3);
	     //Imgproc.GaussianBlur(dilate, blur,new Size(5,5), 1);
		 Imgproc.cvtColor(origImage, greyImage, Imgproc.COLOR_BGR2GRAY);
		 Imgproc.threshold(greyImage, binary, 150, 255, Imgproc.THRESH_BINARY);
		 Imgproc.dilate(binary, outImage, dilateElement);
		// Imgproc.erode(binary, erod, erodElement); 
		// Imgproc.medianBlur(erod, outImage, 3);
		 Highgui.imwrite(OutImgFolder+file, outImage);
		 EasyOCR ocr=new EasyOCR();
		 return ocr.discernAndAutoCleanImage(OutImgFolder+file,ImageType.CLEAR ).replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");
			 
	}
	
	//save image from URL
	static void saveImage(String imageUrl, String destinationFile) throws IOException {
		//System.out.println("imageUrl:"+imageUrl + " destinationFile:"+destinationFile);
		URL url = new URL(imageUrl);
		Proxy proxy = null;
		URLConnection urlconn= null;
		InputStream is = null;
		if (isProxy == 1){
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.hinet.net", 80));
			urlconn=url.openConnection(proxy);		 
			is = urlconn.getInputStream();
		}else{
			is = url.openStream();
		}
		OutputStream os = new FileOutputStream(destinationFile);
		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}
	
	//write input stream into outputstream
	static void dump(InputStream src, OutputStream dest) throws IOException{
		try(InputStream input=src;OutputStream output=dest){
			byte[] data= new byte[1024];
			int length;
			while((length=input.read(data))!=-1){
				output.write(data,0, length);
			}
		}
	}
	
	/**
	 download data from http://bsr.twse.com.tw/bshtm/
    * @param listedCompany A file path of list company csv file.
    * @param imgFolder A folder path of image folder for captcha images.
    * @param OutImgFolder A folder path of image folder for pre-processed captcha images.
    * @param csvFolder A folder path of CSV files.    
	 * @throws Exception 
    */
	static void bsMenuDownloader(String listedCompany,String imgFolder,String OutImgFolder,String csvFolder) throws Exception{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//read listed company
		FileReader ir = new FileReader(listedCompany);
		String str;	
		List<String> listCom=new ArrayList<String>();
		BufferedReader listComReade = new BufferedReader(ir);
		while((str=listComReade.readLine())!=null){
			String[] record = str.split(",");
			listCom.add(record[0]);
		}
		listComReade.close();
		LogWriter logger;
		//log file output
		logger = new LogWriter(csvFolder+"logOTC.txt");
		boolean pass=false;
		String auth_num="";
		for(String id:listCom){
			String bsrURL="http://www.tpex.org.tw/web/stock/aftertrading/broker_trading/";
			String captchaURL="http://www.tpex.org.tw/web/inc/authnum.php";
			String targetPage="brokerBS.php";
			String downloadPage="download_ALLCSV_UTF-8.php";
			//post data
			String stk_code =id;
			String stk_date="";			
			
			System.out.println("Start: "+id);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			
			RequestConfig config=null;
			if (isProxy == 1){
				HttpHost proxy = new HttpHost("proxy.hinet.net", 80, "http");
				config = RequestConfig.custom()
                                      .setProxy(proxy)
                                      .build();
			}

			File outCSVFile=new File(csvFolder+id+".csv");
			//check file exists or id is empty
			if(outCSVFile.exists() || id.trim().length()==0){
				System.out.println(id+" is aleady exist!");
				continue;
			}
				
			//Failures
			int capFail=0;

			do{
				
				if(!pass)
				{
					//System.out.println("Get captcha image!");
					Thread.sleep(Math.round(Math.random()*sleeptime));
					Date cal = Calendar.getInstance().getTime();		
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String imgid=df.format(cal);
					capFail+=1;
					try{
						saveImage(captchaURL,imgFolder+imgid+".jpg");
					}catch(IOException e){
						System.out.println("Save image error: "+e.getMessage());
						Thread.sleep(5000);
						continue;
					}					
					auth_num=SolveCaptcha(imgid+".jpg",imgFolder,OutImgFolder);
					
					//pass=true;
					//System.out.println(imgid+".jpg");
				    //System.out.println("auth_num:"+auth_num);			   
				    
				}
			    if(auth_num.length() !=5){
			    	//System.out.println("solve captcha fail!");
					continue;
			    }
			    
				//System.out.println("Query post stk_code:"+stk_code);
				HttpUriRequest queryPost = RequestBuilder.post()
		                    .setUri(new URI(bsrURL+targetPage))
		                    .setConfig(config)
		                    .addParameter("stk_code", stk_code)
		                    .addParameter("auth_num", auth_num)
		                    .build();
				
				Thread.sleep(Math.round(Math.random()*sleeptime));
				CloseableHttpResponse queryPostRes;
				try{
					queryPostRes = httpclient.execute(queryPost);
				}
				catch(Exception e){
					System.out.println("Query post error: "+e.getMessage());
					Thread.sleep(3000);
					pass=false;
					continue;
				}

				HttpEntity queryPostEntity = queryPostRes.getEntity();
						
				//System.out.println(EntityUtils.toString(queryPostEntity,"UTF-8"));
				org.jsoup.nodes.Document queryPostEntityDoc= Jsoup.parse(EntityUtils.toString(queryPostEntity,"UTF-8"));				
				Elements checkEle = queryPostEntityDoc.getElementsByAttributeValue("class", "v-pnl pt10");
				if(checkEle.size() >0){					
					if(checkEle.get(0).text().equals("***該股票該日無交易資訊***")){
						pass=true; 
						logger.writeLog(stk_code+";"+checkEle.get(0).text()+";驗證次數:"+capFail);
						 capFail=0;
						 PrintWriter emptyFile = new PrintWriter(outCSVFile);
						 emptyFile.write(checkEle.get(0).text());
						 emptyFile.close();
						 break;
					 }else if(checkEle.get(0).text().equals("***驗證碼錯誤，請重新查詢。***")){
						 //System.out.println(checkEle.get(0).text());
						 pass=false;
					 }else{
						 System.out.println("text:"+ checkEle.get(0).text() );
					 }
				}else{
					//System.out.println("Download CSV");
					pass=true;
					Element stk_dateEle=queryPostEntityDoc.getElementById("stk_date");
					stk_date=stk_dateEle.text().replace("年", "").replace("月", "").replace("日", "");
					String getString="?curstk="+stk_code+"&stk_date="+stk_date+"&auth="+auth_num;
					HttpGet CSVGet = new HttpGet(bsrURL+downloadPage+getString);
					Thread.sleep(500);
					CSVGet.setConfig(config);
					CloseableHttpResponse CSVRes = httpclient.execute(CSVGet);
					HttpEntity CSVentity = CSVRes.getEntity();
					//System.out.println(EntityUtils.toString(CSVentity));
					BufferedInputStream CSVIS = new BufferedInputStream(CSVentity.getContent());
					FileOutputStream CSVFOS = new FileOutputStream(outCSVFile);
					//write csvis into csvfos
					dump(CSVIS,CSVFOS);
					CSVFOS.close();
					CSVIS.close();
					logger.writeLog(stk_code+";輸出成功;驗證次數:"+capFail);
					capFail=0;
				}	
									 
			}while(!pass);
			
			httpclient.close();		
			
			Thread.sleep(Math.round(Math.random()*sleeptime));
			//System.out.println("Finished: "+id);
		}// end of for loop
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//驗證圖片儲存位置
		String  imgFolder="C:/Temp/captcha/";
		//String  imgFolder="E:/Temp/captcha/";
		//處理後驗證圖片儲存位置
		String  OutImgFolder="C:/Temp/outs/";
		//String  OutImgFolder="E:/Temp/outs/";
		//上櫃公司列表
		String listedCompany="C:/daily/listcompanyOTC.csv";
		//String listedCompany="E:/GoogleDrive/BIGDATA/ZB101上課資料分享區/上櫃日報/listcompanyOTC.csv";
		// 上櫃公司網址: http://www.tpex.org.tw/web/stock/aftertrading/broker_trading/brokerBS.php
		// 15:35分之後，到上面的網址看要抓的當天，是否有資料，若有資料再執行這隻程式，就會抓到正確的資料
		//csv檔儲存位置		
		String csvFolder="C:/daily/raw/20151005/";
		//String csvFolder="E:/GoogleDrive/BIGDATA/ZB101上課資料分享區/上櫃日報/20150528/";
		
		try{
			bsMenuDownloader(listedCompany,imgFolder,OutImgFolder,csvFolder);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {			
				for (int i=0;i<4;i++){
					Thread.sleep(100);
					java.awt.Toolkit.getDefaultToolkit().beep(); 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
