package page.objects;

import com.mailslurp.apis.InboxControllerApi;
import com.mailslurp.apis.WaitForControllerApi;
import com.mailslurp.clients.ApiClient;
import com.mailslurp.clients.ApiException;
import com.mailslurp.clients.Configuration;
import com.mailslurp.models.Email;
import com.mailslurp.models.Inbox;
import elements.selectors.Selectors;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class EmailTestPage implements Selectors {

    WebDriver driver;
    WebDriverWait wait;
    private static final String INBOX_API = "e4e59681ad9fc94a0312c40b6243f2beea18e5cdbefefc39ae6a535578ccaaa0";
    private static String regEmail;
    private static Long TIMEOUT_VALUE = 120000L;
    private static Boolean UNREAD = true;
    private static ApiClient apiClient;
    private static InboxControllerApi inboxControllerApi;
    private static WaitForControllerApi waitForControllerApi;
    private static Inbox inbox;
    private static Email email;
    private static String codeRef;

    public void setUp(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(url);
        driver.manage().window().maximize();

        apiClient = Configuration.getDefaultApiClient();
        apiClient.setConnectTimeout(TIMEOUT_VALUE.intValue());
        apiClient.setApiKey(INBOX_API);
    }

    public void signUp() throws ApiException {
        inboxControllerApi = new InboxControllerApi(apiClient);
        waitForControllerApi = new WaitForControllerApi(apiClient);
        inbox = inboxControllerApi.createInbox(null, null,null,null,null,
                null,null, null,null);

        assertTrue(inbox.getEmailAddress().contains("@mailslurp.com"));
        regEmail = inbox.getEmailAddress();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(createAcctBtn)));
        driver.findElement(By.cssSelector(createAcctBtn)).click();
        ArrayList<String> newTb = new ArrayList<String>(driver.getWindowHandles());
        //switch to new tab
        driver.switchTo().window(newTb.get(1));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(freelancerRadio)));
        driver.findElement(By.cssSelector(freelancerRadio)).click();
        driver.findElement(By.cssSelector(continueRegBtn)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(regEmailField)));
        driver.findElement(By.cssSelector(regEmailField)).sendKeys(inbox.getEmailAddress());
        driver.findElement(By.cssSelector(continueRegSbtBtn)).click();
        email = waitForControllerApi.waitForLatestEmail(inbox.getId(), TIMEOUT_VALUE, UNREAD);

        assertEquals(email.getSubject(), "Email Verification");

        Document doc = Jsoup.parse(email.getBody());
        Element element = doc.selectFirst("p[style*=font-family:Poppins]");
        codeRef = element.text().trim();

        List < WebElement> otpField = driver.findElements(By.cssSelector(Selectors.otpField));


        for (int i =0; i < otpField.size(); i++){
            WebElement ele = otpField.get(i);
            String eachxter = String.valueOf(codeRef.charAt(i % codeRef.length()));
            ele.sendKeys(eachxter);
        }

    }

    public void getUsedEmail(){
        System.out.println(regEmail);
    }

    public void tearDown(){
        driver.quit();
    }
}
