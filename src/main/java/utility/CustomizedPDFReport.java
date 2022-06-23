package utility;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import business.VGGMClient;

public class CustomizedPDFReport {
	private Document document;
	private PdfWriter writer;
	private PdfReader reader;
	private PdfStamper stamper;
	
	private ExtentTest extentTest;
	private ExtentReports extentReports;
	private CustomizedPDFReport objReporter=null;
	
	private ArrayList<HashMap<String,Object>> arraylistBookmark = new ArrayList<HashMap<String , Object>>();
	private ArrayList<HashMap<String, Object>> arraylistBookmark1 = new ArrayList<HashMap<String, Object>>();
	private LinkedHashMap<String, String> summaryItems = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> listMetaData = new LinkedHashMap<String, String>();
	public boolean boolDeletePNGs = true;
	
	private int nTimeoutImageFile= 3000;
	public int nTableCellPadding= 2;
	public float nImageScalePercentage= 100;
	public int nPiechartSize= 100;
	private int nIndexBookmark=0;
	private int nCountSuccess=0;
	private int nCountFailure=0;
	static int nIndexStatistics=0;
	
	private boolean GLOABAL_boolsettingEnabled=false;
	private int nPageCount=0;
	private String strPathPDF = null;
	private String strPathPDFTemp=null;
	private String strReportDirectory=System.getProperty("user.dir") + "\\test-report";
	private String strReportName = GetUniqueNameIdentifier();
	private String strTimeStamp=getTimeStamp();
	private String strReportFileName="TestReport_"+ strReportName+ ".pdf";
	private float nPositionSummaryTable = 0;
	private float nLeftMargin=0;
	private float nPageWidth=0;
	private BaseColor colorTheme = new BaseColor(255, 255, 255);
	
	@SuppressWarnings("serial")
	final HashMap<String, BaseColor>hashmapcolor = new HashMap<String, BaseColor>(){{
		put("RED", BaseColor.RED);
		put("GREEN", BaseColor.GREEN);
		put("DARK_GREEN", BaseColor.GREEN.darker());
		put("BLUE", BaseColor.BLUE);
		put("BLACK", BaseColor.BLACK);
		put("CYAN", BaseColor.CYAN);
		put("DARK_GRAY", BaseColor.DARK_GRAY);
		put("GRAY",BaseColor.GRAY);
		put("LIGHT_GRAY",BaseColor.LIGHT_GRAY);
		put("MAGENTA",BaseColor.MAGENTA);
		put("ORANGE", BaseColor.ORANGE);
		put("PINK", BaseColor.PINK);
		put("WHITE", BaseColor.WHITE);
		put("YELLOW", BaseColor.YELLOW);
	}};
	
	public Date date= new Date();
	public long iStartTime;
	public long iEndTime;
	private float nFontSize=10;
	private String strFontFamily= Font.FontFamily.UNDEFINED.name();
	private VGGMClient client;
	
	public CustomizedPDFReport(VGGMClient ffaClient) {
		// TODO Auto-generated constructor stub
		this.client = ffaClient;
	}
	
	public void Opening_CustomizedPDFReport(String sTestDetails,String sTestDescription, String sPDFPath) {
	try {
		if(GLOABAL_boolsettingEnabled) {
		extentReports = new ExtentReports(sPDFPath.replace("pdf", "html"));
		extentTest = extentReports.startTest(sTestDetails);	
		}
		iStartTime = date.getTime();
		this.strPathPDF = sPDFPath;
		this.strPathPDFTemp = sPDFPath+".tmp";
		Document.compress=true;
		document = new Document();
		writer=PdfWriter.getInstance(document, new FileOutputStream(strPathPDFTemp));	
		document.open();
	
		nLeftMargin = document.leftMargin();
		nPageWidth = document.getPageSize().getWidth()-document.rightMargin()-document.leftMargin();
		nPositionSummaryTable = writer.getVerticalPosition(true);
		
		PdfPTable objTable = new PdfPTable(1);
		objTable.getDefaultCell().setPadding(nTableCellPadding);
		objTable.addCell(MakeBookmark("Test Summary", "Test Summary", "BOLD", BaseColor.BLACK.darker()));
		objTable.getRow(0).getCells()[0].setBackgroundColor(colorTheme);
		objTable.getRow(0).getCells()[0].setHorizontalAlignment(Element.ALIGN_CENTER);

		Font font = new Font();
		font.setSize(nFontSize);
		font.isBold();
		objTable.addCell(new Phrase("TestCase Description: "+sTestDescription,font));
		objTable.addCell(new Phrase("TestExecution TimeStamp: "+getTimeStamp(),font));
		String imgPath = GetResourcePath(System.getProperty("user.dir")+"/Misc/images/vggm.PNG");
		objTable.addCell(Image.getInstance(imgPath));
		
		objTable.addCell(new Phrase("Executed by: "+System.getProperty("user.name"),font));
		
		objTable.setTotalWidth(nPageWidth);
		objTable.writeSelectedRows(0, objTable.getRows().size(), nLeftMargin , nPositionSummaryTable, writer.getDirectContent());
		nPositionSummaryTable = nPositionSummaryTable - objTable.getTotalHeight()-10;
		objTable = null;
		document.newPage();
		
		}		
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fnSetup_PDF_Reporter(String sDescription) {
		System.out.println("<<<<<<<<<<<<<<<<<<<<Setting up the PDF Report Generator>>>>>>>>>>>>>>>>>>>>");
		CreateFolder(FormatFilePath(strReportDirectory),false);
		Opening_CustomizedPDFReport(strReportName, sDescription, FormatFilePath(strReportDirectory+"\\"+strReportFileName));
		boolDeletePNGs=true;
		nImageScalePercentage=75;
	}
	
	
	public String FormatFilePath(String sFilePath) {
		// TODO Auto-generated method stub
		return sFilePath.replace("\\","/");
	}

	public void Log(String sStepDesc) {
		Log(sStepDesc, sStepDesc );
	}
	
	public void Log(String sStepName, String sStepDescription) {
		Log(sStepName, sStepDescription,null);
	}
	
public void Log(String sStepName, String sStepDesc, String sPathToImageOrNULL) {//sPathToImageOrNULL=Null implies no image in the log. just text report.
		
		try{
			if(GLOABAL_boolsettingEnabled) {
				String strImageBase64 = "";
				if(sPathToImageOrNULL!=null) {
					try {
						strImageBase64 = extentTest.addScreenCapture("data:image/gif;base64,"+EncodeFileToBase64Binary(ScaleDownImageFile(sPathToImageOrNULL, nImageScalePercentage)));
					}
					catch(Exception e) {
						System.out.println("Error while adding image, Exception caught : " + e.getMessage());
					}					
				}
				try{
					if(Pattern.compile("\\[COLOR:DARK_GREEN\\]").matcher(sStepName).find()){
						extentTest.log(LogStatus.PASS, strImageBase64 + "<b style=\"color:green;\">"+(sStepDesc.replaceAll("\\[FONT:BOLD\\]", "").replaceAll("\n", "<br>")));
					}
					else if(Pattern.compile("\\[COLOR:RED\\]").matcher(sStepName).find()){
						extentTest.log(LogStatus.FAIL, strImageBase64 + "<b style=\"color:red;\">"+(sStepDesc.replaceAll("\\[FONT:BOLD\\]", "").replaceAll("\n", "<br>")));
					}
					else {//considered as an info log
						extentTest.log(LogStatus.INFO, strImageBase64 + sStepDesc.replaceAll("\n", "<br>"));
					}
				}
				catch(Exception e) {
					System.out.println("Error while logging to html report: " + e.getMessage());
				}
			}
				
			String strColor = "BLACK", strFontStyle = "Normal"; //Default styles
				BaseColor objColor = hashmapcolor.get(strColor.trim().toUpperCase()); 
				if(Pattern.compile("\\[COLOR:(.*?)\\]|\\[FONT:(.*?)\\]").matcher(sStepName).find()){ //Check if any format specifier exist
					Matcher objFontSpecMatcher = Pattern.compile("\\[COLOR:(.*?)\\]").matcher(sStepName);
					if(objFontSpecMatcher.find()){
						strColor = objFontSpecMatcher.group(1);			
						sStepName = objFontSpecMatcher.replaceAll("");
						objColor = hashmapcolor.get(strColor.trim().toUpperCase());
						if(objColor==null){
							objColor = BaseColor.BLACK;
						}
					}
					objFontSpecMatcher = Pattern.compile("\\[FONT:(.*?)\\]").matcher(sStepName);
					if(objFontSpecMatcher.find()){
						strFontStyle = objFontSpecMatcher.group(1);
						sStepName = objFontSpecMatcher.replaceAll("");
					}
					//Specifications in description are ignored (stripped off)
					objFontSpecMatcher = Pattern.compile("\\[COLOR:(.*?)\\]|\\[FONT:(.*?)\\]").matcher(sStepDesc);			
					if(objFontSpecMatcher.find()){
						sStepDesc = objFontSpecMatcher.replaceAll("");
					}
				}
				
				PdfPTable objTable = new PdfPTable(1); 
				objTable.setSplitLate(false);
				objTable.setSplitRows(false);		
				objTable.setKeepTogether(true);
				objTable.getDefaultCell().setPadding(nTableCellPadding);
				
				String sStepNameForBookmark = sStepName;
				if(sStepName.equals(sStepDesc)){
					sStepNameForBookmark = RegExpExtract(sStepName, "^((?:\\S+\\s+){3}\\S+).*"); //Match first 4 words alone
					if(sStepNameForBookmark.length() > 0){ //Has text with greater than or equal to 4 words
						if(sStepName.length() > sStepNameForBookmark.length()){
							sStepNameForBookmark = sStepNameForBookmark + " ...";
						}
					}
					else { //Reassign, since the regexp returns empty string if the text has less than 4 words
						sStepNameForBookmark = sStepName;
					}
				}
			
				objTable.addCell(MakeBookmark(sStepNameForBookmark,sStepName,strFontStyle,objColor));
				objTable.getRow(0).getCells()[0].setBackgroundColor(colorTheme);
					
				if(!sStepName.equals(sStepDesc)){
					Font font=new Font();
					font.setColor(objColor);
					font.setSize(nFontSize);
					font.setFamily(strFontFamily);
					//font.setStyle(strFontStyle); //This is not working - perhaps font.setStyle("BOLD") does not, but font.setStyle(Font.BOLD) works
					objTable.addCell(new Phrase(sStepDesc,font));
				}
				
				if(sPathToImageOrNULL!=null){
				    File file = new File(sPathToImageOrNULL);
				    int nTimeCheck = 0; //milliseconds
				    while(!file.exists()&&((nTimeCheck+=500) < nTimeoutImageFile)){	        	
				    	Thread.sleep(500);
				    }
				    if(file.exists()){
				    	if(nImageScalePercentage<0 || nImageScalePercentage >100){ //No scaling or compression
				    		objTable.addCell(Image.getInstance(sPathToImageOrNULL));
				    	}
				    	else{ //Scale the image and also convert to compressed format
				    		try{
				    			sPathToImageOrNULL = ScaleDownImageFile(sPathToImageOrNULL, nImageScalePercentage);
				        		PdfPCell oCell = new PdfPCell();
				        		Image objImage = Image.getInstance(sPathToImageOrNULL);
				        		objImage.scalePercent(nImageScalePercentage);			        		
				        		objImage.setScaleToFitHeight(false);
				        		objImage.setAlignment(Element.ALIGN_CENTER);
				        		oCell.addElement(objImage);		        		
					        	oCell.setPadding(nTableCellPadding);
					        	oCell.setGrayFill(0.5f);
					        	objTable.addCell(oCell);
					        	objImage = null;
					        	oCell = null;
					        	new File(sPathToImageOrNULL).delete();
				    		}
				    		catch(Exception e){
				    			objTable.addCell("Unable to add Image");
				    			System.out.println("Error while adding image to pdf, Exception caught : " + e.getMessage());
				    		}			        	        	
				  	    }	        	
				    	if(boolDeletePNGs){
				        	file.delete();
				        }
				    }
				    else {	
				    	objTable.addCell("Image missing: " + sPathToImageOrNULL + " [Time of wait: " + nTimeCheck + "ms]");
				    }
				}
				else{
					//objTable.addCell(" ");
					//objTable.getRow(2).getCells()[0].setGrayFill(90f); //f for float. 0=black 100=white
				}
				document.add(objTable);
				document.add(new Paragraph("\n"));
				//document.newPage();	
			}
			catch(Exception ex){
				System.out.println("Exception from CustomReporter.Log()"+ex.getMessage()); //print ex.getMessage() for details
			}
		}	
	

	private Paragraph MakeBookmark(String strTitleForBookmark, String strContentForBookmark, String strFontStyle, BaseColor objColorSpec) {
		// TODO Auto-generated method stub
		Font fontContent = new Font();
		fontContent.setFamily(strFontFamily);
		fontContent.setSize(nFontSize);
		fontContent.setColor(objColorSpec);
		//fontContent.setStyle(strFontStyle); //This is not working - perhaps font.setStyle("BOLD") does not, but font.setStyle(Font.BOLD) works
		String strColorSpec = (float)objColorSpec.getRed()*10/255/10 + " " + (float)objColorSpec.getGreen()*10/255/10 + " " + (float)objColorSpec.getBlue()*10/255/10;
		HashMap<String, Object> bookmarkitem = new HashMap<String, Object>();
        Anchor anchorTarget = new Anchor(strContentForBookmark,fontContent);
        anchorTarget.setName("Bookmark"+nIndexBookmark);
        Paragraph paragraph = new Paragraph();
        paragraph.add(anchorTarget);
        //Reference: http://what-when-how.com/itext-5/adding-bookmarks-itext-5/
        bookmarkitem.put("Title", strTitleForBookmark);
        bookmarkitem.put("Action", "GoTo");
        bookmarkitem.put("Style", strFontStyle);
        //bookmarkitem.put("Textsize", "1");
        bookmarkitem.put("Color", strColorSpec); //Expects in the format "0 0 0", say for black
        bookmarkitem.put("Named", "Bookmark"+nIndexBookmark);
        arraylistBookmark.add(bookmarkitem);
        nIndexBookmark++;        
        return paragraph;
	}
	
	public void FlushData() {
		try {
			if(!document.isOpen()) {
				return;
			}
			AddToMetadata("Overall Status", nCountFailure>0?"FAIL":"PASS");
	        AddToMetadata("Total Validations", (nCountSuccess+nCountFailure)+"");
	        AddToMetadata("Pass", nCountSuccess+"");
	        AddToMetadata("Fail", nCountFailure+"");
	        //close temporary file
	        writer.setOutlines(arraylistBookmark);
	    	writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
			writer.setViewerPreferences(PdfWriter.PageModeFullScreen|PdfWriter.PageLayoutOneColumn ); //|PdfWriter.PageLayoutSinglePage
			document.close();	
			writer.close();	
			
			//Insert Summary in final PDF
			strPathPDF = strPathPDF.substring(0, strPathPDF.indexOf(".pdf")) + "["+ (nCountFailure>0?"FAIL":"PASS") +"]" +".pdf";			
			reader = new PdfReader(new FileInputStream(strPathPDFTemp));
		    stamper = new PdfStamper(reader, new FileOutputStream(strPathPDF));
		    
			PdfPTable objTable = new PdfPTable(2);
			objTable.setKeepTogether(true);
	        objTable.getDefaultCell().setPadding(nTableCellPadding);
	        
	        Font font=new Font(); //Create new font, else last font setting will be applied to all
	        font.setSize(nFontSize);
	        font.isBold();
	        objTable.addCell(new Phrase("Overall Status:",font));
	        font=new Font(); //Create new font, else last font setting will be applied to all
	        font.setSize(nFontSize);
	        font.isBold();
	        font.setColor(nCountFailure>0?BaseColor.BLACK:BaseColor.BLACK);
	        objTable.addCell(nCountFailure>0?(new Phrase("FAIL",font)): (new Phrase("PASS",font)));	 
	        font=new Font(); //Create new font, else last font setting will be applied to all
	        font.setSize(nFontSize);
	        font.isBold();
	        objTable.addCell(new Phrase("Total Validations:",font));
	        objTable.addCell(new Phrase((nCountSuccess+nCountFailure)+"",font));
	        objTable.addCell(new Phrase("Pass:",font));
	        font=new Font(); //Create new font, else last font setting will be applied to all
	        font.setSize(nFontSize);
	        font.isBold();
	        font.setColor(nCountSuccess>0?BaseColor.BLACK.darker():BaseColor.BLACK.darker());
	        objTable.addCell(new Phrase(nCountSuccess+"",font));
	        font = new Font();
	        font.setSize(nFontSize);
	        font.isBold();
	        objTable.addCell(new Phrase("Fail:",font));
	        font=new Font(); //Create new font, else last font setting will be applied to all
	        font.setSize(nFontSize);
	        font.isBold();
	        font.setColor(nCountFailure>0?BaseColor.BLACK.darker():BaseColor.BLACK.darker());
	        objTable.addCell(new Phrase(nCountFailure+"",font));
	        
	        font=new Font(); //Create new font, else last font setting will be applied to all
	        font.setSize(nFontSize);
	        font.isBold();
	        objTable.addCell(new Phrase("Statistics Graph:",font));	        
	        font=new Font();
	        font.setSize(nFontSize);
	        font.setColor(BaseColor.BLACK);	        
	        PdfContentByte cb = stamper.getOverContent(1);	        
	        PdfTemplate template = cb.createTemplate(nPiechartSize+50, nPiechartSize);
	        template.setLineWidth(0.1f);
	        template.setColorFill(BaseColor.GREEN.darker()); //
	        template.circle(nPiechartSize/2, nPiechartSize/2, nPiechartSize/2-2);
	        template.rectangle(nPiechartSize,nPiechartSize-12,10,10);
	        ColumnText.showTextAligned(template, Element.ALIGN_LEFT,new Phrase("PASS", font), nPiechartSize+12, nPiechartSize-12, 0);
	        template .fillStroke();	        
	        template .setColorFill(BaseColor.RED);
	        template .moveTo(nPiechartSize/2,nPiechartSize-2);
	        if(nCountFailure>0){	        	
	        	template .arc(2, 2, nPiechartSize-2, nPiechartSize-2, 90, 360*nCountFailure/(nCountSuccess+nCountFailure) );	        	
	        }	        
	        template .lineTo(nPiechartSize/2,nPiechartSize/2);	        
	        template .lineTo(nPiechartSize/2,nPiechartSize-2);
	        template.rectangle(nPiechartSize,nPiechartSize-12-12,10,10);
	        ColumnText.showTextAligned(template, Element.ALIGN_LEFT,new Phrase("FAIL", font), nPiechartSize+12, nPiechartSize-12-12, 0);
	        template .fillStroke();
	        template .stroke();
	        Image imgGraph = Image.getInstance(template);       
	        PdfPCell cellPie = new PdfPCell(imgGraph,true);
	        cellPie.setPadding(1f);
	        cellPie.setFixedHeight(nPiechartSize);
	        cellPie.setHorizontalAlignment(Element.ALIGN_CENTER);
	        objTable.addCell(cellPie);
	        
	        //Insert the main highlights table
	        objTable.setTotalWidth(nPageWidth);
	        objTable.writeSelectedRows(0, objTable.getRows().size(), nLeftMargin, nPositionSummaryTable, stamper.getUnderContent(1));
	        nPositionSummaryTable = nPositionSummaryTable - objTable.getTotalHeight() - 10;
	        objTable = null;
	        
	        //For showing the total execution time of the test case
	        iEndTime=new Date().getTime();
		    long diffSec = (iEndTime-iStartTime) / 1000;
		    long min = diffSec / 60;
		    long sec = diffSec % 60;
	        AddToSummary("Duration", min +" mins and "+sec+" secs");
	        
	        //Insert the full summary table
	        if(!summaryItems.isEmpty()){		        
		        objTable = new PdfPTable(2);
				objTable.setKeepTogether(true);
		        objTable.getDefaultCell().setPadding(nTableCellPadding);
		        font=new Font();
	 	        font.setSize(nFontSize);
	 	        font.setColor(BaseColor.BLACK);	 
		        for (String sNameItem:summaryItems.keySet()){
		            objTable.addCell(new Phrase(sNameItem + ":",font)); 	
		 	       	objTable.addCell(new Phrase(summaryItems.get(sNameItem),font)); 	
		        }	        
		        objTable.setTotalWidth(nPageWidth);
		        objTable.writeSelectedRows(0, objTable.getRows().size(), nLeftMargin, nPositionSummaryTable, stamper.getUnderContent(1));
		        nPositionSummaryTable = nPositionSummaryTable - objTable.getTotalHeight() - 10;
		        objTable = null;
	        }
	        stamper.close();
        	reader.close();  
        	
        	File file= new File(strPathPDFTemp);
        	file.delete();
        	
        	Path path = new File(strPathPDF).toPath();
        	for(String sNameItem:listMetaData.keySet()) {
        		try {
        			Files.setAttribute(path, sNameItem, listMetaData.get(sNameItem).getBytes(StandardCharsets.UTF_8));
        		}
        	catch(Exception e) {
        		System.out.println("error while adding meta data to the reporter");e.getMessage();
        	}
        }
        
       if(GLOABAL_boolsettingEnabled) {
        extentReports.endTest(extentTest);
        extentReports.flush();
       }
        
        PrintFileMetaData(strPathPDF);    
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Below Function is to ValidateTest
	public void ValidateTest(boolean boolIsSuccess) {
		if(boolIsSuccess) {
			++nCountSuccess;
			Log("Validation succeeded[COLOR:DARK_GREEN]", "An Implicit test Validation Succeded");
		}else {
			++nCountFailure;
			Log("Validation succeeded[COLOR:RED]", "An Implicit test Validation Failed");
		}
	}
	
	public void ValidateTest(boolean boolIsSuccess, String sLogMessage) {
		if(boolIsSuccess) {
			++nCountSuccess;
			Log(sLogMessage+"[COLOR:DARK_GREEN]",sLogMessage);
		}else{
			++nCountFailure;
			Log(sLogMessage+"[COLOR:RED]", sLogMessage);
		}
	}
	
	public void ValidateTest(boolean boolIsSuccess, String sLogMessage, String sImgPath) {
		if(boolIsSuccess) {
			++nCountSuccess; 
			Log(sLogMessage+"[COLOR:DARK_GREEN]", sLogMessage,sImgPath);
			
		}else {
			++nCountFailure;
			Log(sLogMessage+"[COLOR:RED]",sLogMessage,sImgPath);
		}
	}
	
	public void ValidateTest(String strActual, String strExpected) {
		boolean boolIsSuccess= strActual.trim().toUpperCase().equalsIgnoreCase(strExpected.trim().toUpperCase());
		if(boolIsSuccess) {
			nCountSuccess++;
			Log("Validation succeeded[COLOR:DARK_GREEN]", "Expected:" + strExpected + "\r\nActual:" + strActual);
		}else {
			nCountFailure++;
			Log("[FAIL]Validation Failed[COLOR:RED]", "Expected:"+ strExpected +"\r\nActual: "+ strActual);
		}
	}
	
	//delete the temporary file
	public boolean DeleteReportTempFile() {
		try {
			document.close();
			writer.close();
			File file = new File(strPathPDFTemp);
			file.delete();
			return true;
		}
		catch(Exception e) {
			System.out.println("Error while deleting Temporary File"+strPathPDFTemp);
			return false;
		}
	}
	
	public int getFailureCount() {
		return nCountFailure;
	}
	
	public String getReportFilePath() {
		return this.strPathPDF;
	}
	
	public static String GetResourcePath(String strPathResourceOrAbsolute) {
		try {
			String strPathResourceOrAbsoluteIntial= strPathResourceOrAbsolute;
			if(IsFileOrFolderExist(strPathResourceOrAbsolute)) {
				
				return new File(strPathResourceOrAbsolute).getAbsolutePath().replace("\\", "/");
			}
		}catch(Exception e) {
			
		}
		return strPathResourceOrAbsolute;
	}
	
	public static boolean IsFileOrFolderExist(String sPath) {
		if(sPath==null) 
			return false;
		try {
			return Files.exists(Paths.get(sPath));
		}catch(Exception e) {
			return false;
		}
	}
	
	public static String ScaleDownImageFile(String strImageFilePath, float fPercentage) throws IOException, BadElementException {
		if (fPercentage < 0 || fPercentage > 100) {
			return strImageFilePath;
		}
		java.awt.Image image = javax.imageio.ImageIO.read(new File(strImageFilePath));
		int nWidth = (int) (image.getWidth(null) * fPercentage / 100);
		int nHeight = (int) (image.getHeight(null) * fPercentage / 100);
		BufferedImage bufferedImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setComposite(AlphaComposite.Src);
		// Below three lines are for RenderingHints for better image quality, at
		// cost of higher processing time
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawImage(image, 0, 0, nWidth, nHeight, null);
		graphics2D.dispose();
		// The file format specifier "png" was finalized after verifying png,
		// jpg, gif.
		// GIF offered better quality for smaller size due to loseless pixel
		// compression. The 256 color palette reduction was unnoticable.
		final String strFormat = "gif";
		strImageFilePath = strImageFilePath + ".scaled." + strFormat;
		javax.imageio.ImageIO.write(bufferedImage, strFormat, new File(strImageFilePath));
		bufferedImage = null;
		return strImageFilePath;
	}
	
	
	public static String EncodeFileToBase64Binary(String strImagePath) {
		String encodedfile = null;
		try {
			FileInputStream fileInputStreamReader = new FileInputStream(new File(strImagePath));
			byte[] bytes = new byte[(int) new File(strImagePath).length()];
			fileInputStreamReader.read(bytes);
			encodedfile = new String(Base64.encodeBase64(bytes), "UTF-8");
		} catch (Exception e) {
			return "";
		}
		return encodedfile;
	}
	
	public static String RegExpExtract(String sSource, String sPattern) {
		return RegExpExtract(sSource, sPattern, -1);
	}

	public static String RegExpExtract(String sSource, String sPattern, int nGroupIndex) {
		return RegExpExtract(sSource, sPattern, nGroupIndex, -1);
	}

	public static String RegExpExtract(String sSource, String sPattern, int nGroupIndex, int nGroupAttempt) { // returns empty string if no match																											
		Pattern p = Pattern.compile(sPattern);
		Matcher m = p.matcher(sSource);
		while (m.find()) {
			if (--nGroupAttempt <= 0) {
				return nGroupIndex > m.groupCount() ? "" : (m.group((nGroupIndex < 0) ? m.groupCount() : nGroupIndex)); // return ((m.groupCount()>1)?m.group(1):m.group(0));																											
			}
		}
		return "";
	}
	
	public void AddToSummary(String sName, String sValue) {
		summaryItems.put(sName, sValue);
		AddToMetadata(sName, sValue);
	}
	
	public void AddToMetadata(String sName, String sValue) {
		listMetaData.put("user:"+sName, sValue);
	}
	
	public void PrintFileMetaData(String sPathPDF) {
		GetFileMetaData(sPathPDF, true);
	}
	
	public static Map<String, String> GetFileMetaData(String sPathPDF, boolean nPrintConsole){
		List<String> attribList = null;
		if(nPrintConsole) {
			System.out.println("Going to Print the MetaData");
		}
		HashMap<String, String>MetaDataInfo = new HashMap();
		Path path = new File(sPathPDF).toPath();
		
		try {
	    	final UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
			for(String attribute : view.list()) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(view.size(attribute));
			view.read(attribute,buffer);
			buffer.flip();
			MetaDataInfo.put(attribute, StandardCharsets.UTF_8.decode(buffer).toString());
			if(nPrintConsole) {
				System.out.println(attribute+":"+MetaDataInfo.get(attribute));
			}
		  }
		if(nPrintConsole) {
			System.out.println("File Size :"+Files.getAttribute(path, "size")+"Bytes");
		}
	}catch(IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		return MetaDataInfo;
	}
	
	public String GetUniqueNameIdentifier() {
		String sTestName="";
		try {
			throw new Exception("Who is this?");
		}catch(Exception e){
			for(StackTraceElement eleStackTrace:e.getStackTrace()) {
				if(eleStackTrace == null) {
					break;
				}
				if(eleStackTrace.getFileName().equals("NativeMethodAccessorImpl.java")) {
					break;
				}
				sTestName=eleStackTrace.getFileName();
			}
		}
		nIndexStatistics = nIndexStatistics + 1;
		return (sTestName.replace(".java", "")+"["+ nIndexStatistics + "]");
	}
	
	public String getTimeStamp() {
		
		Date todaysDate = new Date();
		DateFormat df = new SimpleDateFormat("MMM dd yyyy, HH:mm:ss");
		String timeStamp = df.format(todaysDate);
		return timeStamp;
	}
	
	public static void CreateFolder(String sDirectoryPath,boolean deleteExistingFile) {
		
		File files = new File(sDirectoryPath);
		if(!files.exists()) {
			if(files.mkdir()) {
				System.out.println("Directory Created Successfully!");
			}else {
				System.out.println("Folder couldn't be created");
			}
		}
	}

public static class PDFReport_extender{
	
	public boolean boolTakeScreenShot=true;
	private VGGMClient client;
	
	public PDFReport_extender(VGGMClient ffaClient) {
		// TODO Auto-generated constructor stub
		this.client = ffaClient;
	}

	public void LogText(String sLogItem) {
		client.pdfreport.Log(sLogItem);
	}
	
	public void Log(String sStepName, String sStepDesc) {
		String sPathIMG = TakeSnapShot();
		Log(sStepName, sStepDesc, sPathIMG);
	}
	
	public void Log(String sStepName, String sStepDesc, String sPathToImageOrNULL) {
		client.pdfreport.Log(sStepName+"[FONT:BOLD]", sStepDesc, sPathToImageOrNULL);
		}
	
	public void ValidateTest(String strActual, String strExpected) {
			client.pdfreport.ValidateTest(strActual, strExpected);
		}
	
	public void ValidateTest(boolean boolStatus, String strLogDescription) {
		 ValidateTest(boolStatus, strLogDescription, true);
	}
	
	public void ValidateTest(boolean boolStatus, String strLogDescription, boolean boolCaptureScreenshot) {
		try {
		client.pdfreport.ValidateTest(boolStatus, strLogDescription+"[FONT:BOLD]", boolCaptureScreenshot?TakeSnapShot():null);
	}catch(Exception e) {
			e.printStackTrace();
	}
}
	
	public String TakeSnapShot() {
		String sFileName="";
		if(!boolTakeScreenShot) {
			return sFileName;
		}
	try {
		
		System.out.println("Going to take the screenshots");
		sFileName = ((TakesScreenshot)client.driver).getScreenshotAs(OutputType.FILE).getAbsolutePath();
		System.out.println("Screenshot captured without switching the contexts"+sFileName);
		
	}catch(Exception e) {
		System.out.println("error while taking the screenshots"+ e.getMessage());
	}
		return sFileName;
		}
	}
}