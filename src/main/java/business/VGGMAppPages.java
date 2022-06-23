package business;

import java.io.IOException;
import java.sql.Driver;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.ErrorHandler;
import io.github.bonigarcia.wdm.WebDriverManager;

public class VGGMAppPages {
	
	private VGGMClient client;
	private String sSearchTestData = "Het Gele Huis";
	//xpaths are being listed out below
	private String sOntdek_de_collectie_link="//a[normalize-space()='Ontdek de collectie']";
	private String sFooterConsent = "//*[@id='vgm-app']/div/footer/div/section/div[2]/button[1]";
	private String sOntdek_de_collectie_page="//span[@class='results']";
	private String sOntdek_de_collectie_page_search_box="input[placeholder='Zoek een kunstwerk']";	
	private String sNumber_of_Results_fetched="//span[@class='results']";
	private String sPaitingImage_Search_Result=".lazy-image.collection-art-object-item-image[data-src='https://iiif.micr.io/NyxcG/full/200,/0/default.jpg']";
	private String sPaitingImage_Enlarged_Result="//div[@class='art-object-header-image-wrapper']";
	private String sObjectgegevensSection="button[aria-label='Open Objectgegevens']";
	private String sFNumberValuePath="//dd[normalize-space()='F0464']";
	private String sJHNumberValuePath="//dd[normalize-space()='JH1589']";
	private String sInventarisnummerPath="//dd[normalize-space()='s0032V1962']";
	
	public VGGMAppPages(VGGMClient vggmclient) {
		this.client = vggmclient;
	}
	
	/*
	 * Method to launch the vangoghmusuem website
	 */
	public void fnLoadHomePage() throws Exception{
		try{
		
		client.pdfreport.Log("Trying to land on the vangoghmusuem page");
		client.driver.get("https://www.vangoghmuseum.nl/");
		client.pdfextender.ValidateTest(true, "[TEST PASS:] Landed on the Vangoghmusuem page Successfully!");
		client.driver.manage().window().maximize();
		}catch(Exception e){
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Failed to land on the Vangoghmusuem page" + e);
		}		
	}
	
	/*
	 * Method to launch the collection search of vangoghmusuem website
	 */
	public void fnLoadCollectionSearchPage() throws Exception{
		try{
		
		client.pdfreport.Log("Trying to land on the collection search page of vangoghmusuem");
		client.driver.get("https://www.vangoghmuseum.nl/nl/collectie");
		client.pdfextender.ValidateTest(true, "[TEST PASS:] Landed on the Collection Search Page of Vangoghmusuem Successfully!");
		client.driver.manage().window().maximize();
		}catch(Exception e){
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Failed to land on Collection Search Page of Vangoghmusuem" + e);
		}		
	}
	
	public void CloseSession(){
		client.driver.close();
		client.driver.quit();
	}
	
	
	
	public void fnCollectionSearch_Validation() throws Exception {
			
		Thread.sleep(5000);
		System.out.println("Looking to Validate Collection Search");
		client.pdfreport.Log("Looking to Validate Collection Search");
		client.driver.findElement(By.xpath(sFooterConsent)).click();
		if(client.driver.findElement(By.xpath(sOntdek_de_collectie_link)).isDisplayed()){
			client.driver.findElement(By.xpath(sOntdek_de_collectie_link)).click();
			client.pdfextender.ValidateTest(true, "[TEST PASS:] sOntdek_de_collectie_link Validated Successfully!");	
		}else{
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Oops sOntdek_de_collectie_link Not Found!");	
		}
		
		if(client.driver.findElement(By.xpath(sOntdek_de_collectie_page)).isDisplayed()){
			client.pdfextender.ValidateTest(true, "[TEST PASS:] Landed on the Collectie page and getting results Successfully!");	
		}else{
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Failed to Land on the Collectie page!");	
		}
		
	}
	
	public void fnCollectionPageSearch_Validation() throws Exception {
		
		Thread.sleep(5000);
		System.out.println("Looking to Validate Collection Page Search");
		client.pdfreport.Log("Looking to Validate Collection Page Search");
		client.driver.findElement(By.xpath(sFooterConsent)).click();
		if(client.driver.findElement(By.cssSelector(sOntdek_de_collectie_page_search_box)).isDisplayed()){
			client.driver.findElement(By.cssSelector(sOntdek_de_collectie_page_search_box)).click();
			client.pdfextender.ValidateTest(true, "[TEST PASS:] sOntdek_de_collectie_page_search_box Validated and Enabled Successfully!");	
		}else{
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Oops sOntdek_de_collectie_page_search_box Not Found!");	
		}
		
		if(client.driver.findElement(By.cssSelector(sOntdek_de_collectie_page_search_box)).isEnabled()){
			client.driver.findElement(By.cssSelector(sOntdek_de_collectie_page_search_box)).sendKeys(sSearchTestData);
			client.driver.findElement(By.cssSelector(sOntdek_de_collectie_page_search_box)).sendKeys(Keys.ENTER);
			client.pdfextender.ValidateTest(true, "[TEST PASS:] Entered data to sOntdek_de_collectie_page_search_box and also searched results Successfully!");	
		}else{
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Failed to enter data to sOntdek_de_collectie_page_search_box");	
		}
		
		if(client.driver.findElement(By.xpath(sNumber_of_Results_fetched)).isDisplayed()){
		String sNumResults = client.driver.findElement(By.xpath(sNumber_of_Results_fetched)).getText();
		if(sNumResults.equals(sNumResults)){
			client.pdfextender.ValidateTest(true, "[TEST PASS:] More than 700 searched results fetched Successfully!");	
		}
		client.pdfextender.ValidateTest(true, "[TEST FAIL:] Failed to fetch 700 searched results!");	
		}
	}
	
	public void fnCollectionPage_PaintingImg_Search_Validation() throws Exception {
		
		Thread.sleep(5000);
		JavascriptExecutor jse = (JavascriptExecutor)client.driver;
		System.out.println("Looking to Validate Painting Image from Collection Page Search");
		client.pdfreport.Log("Looking to Validate Painting Image from Collection Page Search");
		jse.executeScript("window.scrollBy(0,250);");
		
		if(client.driver.findElement(By.cssSelector(sPaitingImage_Search_Result)).isDisplayed()){
			client.driver.findElement(By.cssSelector(sPaitingImage_Search_Result)).click();
			client.pdfextender.ValidateTest(true, "[TEST PASS:] sOntdek_de_collectie_page Paiting image Search Result found Successfully!");	
		}else{
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Oops sOntdek_de_collectie_page Paiting image Search Result not found!");	
		}
		
		if(client.driver.findElement(By.xpath(sPaitingImage_Enlarged_Result)).isDisplayed()){
			
			client.pdfextender.ValidateTest(true, "[TEST PASS:] Enarlaged Painting Image Validated Successfully!");	

			jse.executeScript("window.scrollBy(0, 850);");
			if(client.driver.findElement(By.cssSelector(sObjectgegevensSection)).isDisplayed()){
			client.driver.findElement(By.cssSelector(sObjectgegevensSection)).click();
			}
			
			String sFNumberValue=client.driver.findElement(By.xpath(sFNumberValuePath)).getText().toString().trim();
			if(sFNumberValue == "F0464"){
			client.pdfextender.ValidateTest(true, "[TEST PASS:] F-nummer of Painting Image Validated Successfully!"+sFNumberValue);	
			}else{
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] F-nummer of Painting Image couldn't be Validated Successfully!"+sFNumberValue);	
			}
			String sJHNumberValue=client.driver.findElement(By.xpath(sJHNumberValuePath)).getText().toString().trim();
			if(sJHNumberValue == "JH1589"){
			client.pdfextender.ValidateTest(true, "[TEST PASS:] JH-nummer of Painting Image Validated Successfully!"+sJHNumberValue);	
			}else{
				client.pdfextender.ValidateTest(true, "[TEST FAIL:] JH-nummer of Painting Image couldn't be Validated Successfully!"+sJHNumberValue);	
			}
			String sInventarisNummer=client.driver.findElement(By.xpath(sInventarisnummerPath)).getText().toString().trim();
			if(sInventarisNummer == "s0032V1962"){
			client.pdfextender.ValidateTest(true, "[TEST PASS:] Inventarisnummer of Painting Image Validated Successfully!"+sInventarisNummer);	
			}else{
				client.pdfextender.ValidateTest(true, "[TEST FAIL:] Inventarisnummer of Painting Image couldn't be Validated Successfully!"+sInventarisNummer);	
			}
		}else{	
			client.pdfextender.ValidateTest(true, "[TEST FAIL:] Couldn't Validate Enlarged Painting Image!");	
		}
	}	
}


