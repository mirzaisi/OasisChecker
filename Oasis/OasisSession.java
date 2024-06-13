package OasisChecker.Oasis;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import OasisChecker.Main;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class OasisSession {
    private final WebDriver webDriver;
    private final String oasisID;
    private final String oasisPassword;
    private final String oasisPin;

    public OasisSession(WebDriver webDriver, String oasisId, String oasisPassword, String oasisPin) {
        this.webDriver = webDriver;
        this.oasisID = oasisId;
        this.oasisPassword = oasisPassword;
        this.oasisPin = oasisPin;
    }


    public ArrayList<String> getDataFromOasis() throws IOException{
        openOasisAndLogin();
        webDriverSleep(2);
        System.out.println("Getting course data from Oasis...");
        ArrayList<String> courseCodesToAddToMail = getDataFromSemesterGradesPage();

        return courseCodesToAddToMail;
    }

    private void openOasisAndLogin() {
        System.out.println("Logging in to Oasis...");
        webDriver.get("https://oasis.izmirekonomi.edu.tr/login");
        passFirstLoginPage();
        webDriverSleep(2);
        passSecondLoginPage();
    }

    private void passFirstLoginPage() {
        
        WebElement ID = webDriver.findElement(By.name("LoginForm[username]"));
        ID.sendKeys(oasisID);

        WebElement password = webDriver.findElement(By.name("LoginForm[password]"));
        password.sendKeys(oasisPassword);

        WebElement loginButton = webDriver.findElement(By.name("login-button"));
        loginButton.click();
           

    }

    private void passSecondLoginPage() {
        WebElement pin = webDriver.findElement(By.name("LoginForm[pin]"));
        pin.sendKeys(oasisPin);

        WebElement loginButton = webDriver.findElement(By.name("login-button"));
        loginButton.click();
    }

    private ArrayList<String> getDataFromSemesterGradesPage() {
        try {
            // to see if it is logged in
            WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/main/div[1]/div[3]/div[2]/div[3]/div[1]/h5/strong")));
            webDriver.get("https://oasis.izmirekonomi.edu.tr/studentgrades/grades");
        } catch (TimeoutException te) {
            System.out.println("Cannot login to Oasis at this time, sleeping...");
            Main.loopSleep(Main.loopSleepTime);
        }
      
        // click details buttons to expand table
        List<WebElement> detailsButtons = webDriver.findElements(By.cssSelector("table > tbody > tr > td:nth-child(7) > button"));
        for (WebElement detailsButton : detailsButtons) {
            detailsButton.click();
        }

        List<WebElement> courses = webDriver.findElements(By.cssSelector("table.table.table-sm.table-bordered.table-striped.main-table > tbody"));
        ArrayList<String> courseCodesToAddToMail = Course.loadCourseObjects(courses);

        return courseCodesToAddToMail;
    }

    private void webDriverSleep(int duration) {
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(duration));
    }

    public static WebDriver startWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        WebDriver webDriver = new ChromeDriver(options);

        return webDriver;
    }

}
