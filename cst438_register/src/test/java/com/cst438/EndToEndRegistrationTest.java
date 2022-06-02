package com.cst438;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EndToEndRegistrationTest {

    public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";

    public static final String URL = "http://localhost:3000/student";

    public static final int SLEEP_DURATION = 1000; // 1 second.

    public static final String TEST_USER_NAME = "John Doe";

    public static final String TEST_USER_EMAIL = "jdoe@csumb.edu";

    @Autowired
    StudentRepository studentRepository;

    @Test
    public void addStudentTest() throws Exception {
        Student s = null;
        do {
            s = studentRepository.findByEmail(TEST_USER_EMAIL);
            if (s != null) {
                studentRepository.delete(s);
            }
        } while (s != null);

        // set the driver location and start driver
        //@formatter:off
        // browser	property name 				Java Driver Class
        // edge 	webdriver.edge.driver 		EdgeDriver
        // FireFox 	webdriver.firefox.driver 	FirefoxDriver
        // IE 		webdriver.ie.driver 		InternetExplorerDriver
        //@formatter:on

        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        // Puts an Implicit wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            // select the "Add Student" button and click it
            driver.findElement(By.xpath("//button[span='Add Student']")).click();
            Thread.sleep(SLEEP_DURATION);

            // locate the name input field and fill it
            driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
            Thread.sleep(SLEEP_DURATION);

            // locate the email input field and fill it
            driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
            Thread.sleep(SLEEP_DURATION);

            // submit the add-student form, click 'Add' button
            driver.findElement(By.xpath("//button[span='Add']")).click();
            Thread.sleep(SLEEP_DURATION);

            // verify that new student is in database
            Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
            assertNotNull(student, "Added student does not appear in database.");
            // verify that new student shows in student list
            WebElement we = driver.findElement(By.xpath("//div[@data-field='email' and @data-value='"+ student.getEmail() +"']"));
            assertNotNull(we, "Added student does not appear in students list.");
        } catch (Exception ex) {
            throw ex;
        } finally {
            // clean up database
            Student s1 = studentRepository.findByEmail(TEST_USER_EMAIL);
            if (s1 != null) {
                studentRepository.delete(s1);
            }
        }
    }
}