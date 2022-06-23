/*
 * Test case 2:
1. Load the collection search page https://www.vangoghmuseum.nl/nl/collectie
2. Search the painting with title “Het Gele Huis” from the search box.
3. Verify that you get more than 700 results
 */
package testscenarios;

import business.VGGMClient;

public class testscenarios_vangoghmuseum_SC02 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		VGGMClient client = new VGGMClient(args);
		client.pdfreport.fnSetup_PDF_Reporter("testscenarios_vangoghmuseum_SC02");
		client.vggmpages.fnLoadCollectionSearchPage();
		client.vggmpages.fnCollectionPageSearch_Validation();
		client.pdfreport.FlushData();		
		client.vggmpages.CloseSession();
	}
}
