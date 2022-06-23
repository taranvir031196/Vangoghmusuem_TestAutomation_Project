/*Test case 1:
1. Load home page https://www.vangoghmuseum.nl
2. Go to the collection search by clicking in the link -> Ontdek de collectie
3. Verify that you get the collection page using your own verification criterion.
 * 
 */
package testscenarios;

import business.VGGMClient;

public class testscenarios_vangoghmuseum_SC01 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		VGGMClient client = new VGGMClient(args);
		client.pdfreport.fnSetup_PDF_Reporter("testscenarios_vangoghmuseum_SC01");
		client.vggmpages.fnLoadHomePage();
		client.vggmpages.fnCollectionSearch_Validation();
		client.pdfreport.FlushData();	
		client.vggmpages.CloseSession();
	}
}
