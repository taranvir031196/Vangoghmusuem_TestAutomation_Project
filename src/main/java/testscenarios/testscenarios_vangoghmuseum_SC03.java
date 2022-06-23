/*Test case 3:
1. Load the collection search page https://www.vangoghmuseum.nl/nl/collectie
2. Search the painting with title “Het Gele Huis” from the search box.
3. Click on the first result
4. Verify that you get this painting:
 * 
 */
package testscenarios;

import business.VGGMClient;

public class testscenarios_vangoghmuseum_SC03 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		VGGMClient client = new VGGMClient(args);
		client.pdfreport.fnSetup_PDF_Reporter("testscenarios_vangoghmuseum_SC03");
		client.vggmpages.fnLoadCollectionSearchPage();
		client.vggmpages.fnCollectionPageSearch_Validation();
		client.vggmpages.fnCollectionPage_PaintingImg_Search_Validation();
		client.pdfreport.FlushData();	
		client.driver.close();
	}

}
