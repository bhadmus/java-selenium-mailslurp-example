package test.object;

import com.mailslurp.clients.ApiException;
import org.testng.annotations.*;
import page.objects.EmailTestPage;

public class TestClass {
    EmailTestPage testPage = new EmailTestPage();
    @BeforeMethod
    public void setUp(){
        testPage.setUp();
    }

    @Test
    public void test_1_check_email_address() throws ApiException {
        testPage.signUp();
    }

    @Test
    public void test_2_verify_email(){
        testPage.getUsedEmail();
    }

    @AfterMethod
    public void tearDown(){
        testPage.tearDown();
    }
}
