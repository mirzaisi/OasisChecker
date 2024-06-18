import java.io.IOException;
import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import Oasis.OasisSession;


public class Main {
    
    // CREDENTIALS
    // OASIS
    private static final String oasisID = "";
    private static final String oasisPassword = "";
    private static final String oasisPin = "";

    // EMAILS
    private static final String senderAdress = "";
    private static final String senderPassword = "";
    private static final String receiverAdress = "";

    public final static int loopSleepTime = 20 * (60 * 1000); // 20 minutes in millis

    public static void main(String[] args) throws IOException{
        
        boolean pausedBot = false; // pausing the bot with an email feature will be added
        while(!pausedBot) {
            
            runBot();
            System.out.println("Finished loop, sleeping for 15 minutes...");  
            loopSleep(loopSleepTime);          
        }

    }

    public static void runBot() {

        WebDriver webDriver = OasisSession.startWebDriver();
        try {
            OasisSession oasisSession = new OasisSession(webDriver, oasisID, oasisPassword, oasisPin);
            ArrayList<String> courseCodesToAddToMail = oasisSession.getDataFromOasis();
            if (courseCodesToAddToMail != null)  {
                if (courseCodesToAddToMail.size() > 0) {
                    try {
                        System.out.println("Changes in courses, sending email...");
                        Mail.generateMail(courseCodesToAddToMail, senderAdress, senderPassword, receiverAdress);
                    } catch (Exception e){
                        loopSleep(loopSleepTime);
                    }
                }
                else {
                    System.out.println("No changes in courses. No email was sent.");
                }
            }
            

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }

    }

    public static void loopSleep(int loopSleepTime) {
        try {
            Thread.sleep(loopSleepTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    
}
