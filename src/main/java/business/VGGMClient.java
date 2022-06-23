package business;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.aventstack.extentreports.ExtentTest;

import utility.CustomizedPDFReport;
import utility.CustomizedPDFReport.PDFReport_extender;

public class VGGMClient {
	
	public VGGMAppPages vggmpages;
	public CustomizedPDFReport pdfreport;
	public PDFReport_extender pdfextender;
	public WebDriver driver;
	String driverPath = pdfreport.GetResourcePath(System.getProperty("user.dir")+"/src/main/resources/chromedriver/chromedriver.exe");  

	
	public VGGMClient(String[]args) throws Exception{
		
		System.setProperty("webdriver.chrome.driver", driverPath);
		driver = new ChromeDriver();
		vggmpages = new VGGMAppPages(this);
		pdfreport = new CustomizedPDFReport(this);
		pdfextender = new PDFReport_extender(this);
	}
}
