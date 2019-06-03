package com.qa.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.codoid.products.exception.FilloException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.qa.pages.IDP_Account;
import com.qa.pages.IDP_Applications;
import com.qa.pages.IDP_Directories;
import com.qa.pages.IDP_Tenant;
import com.qa.pages.IDP_Users;
import com.qa.util.TestUtil;
import com.qa.util.WebEventListener;
import com.qa.util.jsonReader;
import com.qa.util.readAndWriteData;

public class TestBase {
	public static WebDriver driver;
	public static Properties prop;
	public static Properties config;
	public static EventFiringWebDriver edriver;
	public static WebEventListener eventlistener;
	public readAndWriteData readNwrite=new readAndWriteData();
	public static String TEST;
	public ITestResult result;
	public static Logger logger;
	public static String className;
	public static SoftAssert softAssert;
	public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	JavascriptExecutor js=(JavascriptExecutor)driver; 


	public TestBase() {

		try {
			if(config==null) {
				config=new Properties();
				FileInputStream fs=new FileInputStream(System.getProperty("user.dir")+"/src/main/java/com/qa/config/config.properties");
				config.load(fs);

				prop=new Properties();
				FileInputStream ip=new FileInputStream(System.getProperty("user.dir")+"/src/main/java/com/qa/config/"+config.getProperty("Environment")+"Env.properties");
				prop.load(ip);
				System.out.println(prop.getProperty("env"));


			}}catch(FileNotFoundException e) {
				e.printStackTrace();
			}catch(IOException e) {
				e.printStackTrace();
			}

		softAssert=new SoftAssert();
		test=WebEventListener.test();

	}

	//********************************Browser invoke function*****************************************//

	public static void initialization() {
		String OS = System.getProperty("os.name").toString().toLowerCase();
		if(OS.contains("mac"))
			OS="MAC";
		else if(OS.contains("win"))
			OS="WINDOWS";
		System.out.println(OS);

		switch(OS) {
		case "WINDOWS":
			if(driver==null) {
				String browserName=config.getProperty("browser");
				if(browserName.equalsIgnoreCase("chrome")) {
					System.setProperty("webdriver.chrome.driver",TestUtil.CHROMEDRIVER_EXE);
					driver=new ChromeDriver();
				}else if(browserName.equalsIgnoreCase("firefox")){
					System.setProperty("webdriver.gecko.driver",TestUtil.FIREFOXDRIVER_EXE);
					driver=new FirefoxDriver();
				}else if(browserName.equalsIgnoreCase("headless")){
					
					driver = new HtmlUnitDriver();;
				}else if(browserName.equalsIgnoreCase("ie")) {
					System.setProperty("webdriver.edge.driver",TestUtil.EDGEDRIVER_EXE); 
					 
	                driver = new EdgeDriver();
				}

				edriver=new EventFiringWebDriver(driver);
				eventlistener=new WebEventListener();
				edriver.register(eventlistener);
				driver=edriver;
			}
		case "MAC":
			if(driver==null) {
				String browserName=config.getProperty("browser");
				if(browserName.equalsIgnoreCase("chrome")) {
					System.setProperty("webdriver.chrome.driver",TestUtil.CHROMEDRIVER_MAC);
					ChromeOptions options=new ChromeOptions();
					options.addArguments("--disable-notifications");
					options.addArguments("disable-infobars");
					options.addArguments("--start-maximized");
					//options.addArguments("--headless");
					options.addArguments("--proxy-server:http://100.0.0.1");
					//options.addArguments("user-data-dir:directory path of till user data folder");
					//options.setPageLoadStrategy(PageLoadStrategy.EAGER);
					System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "browserLogs");
					System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

					driver=new ChromeDriver(options);
				}else if(browserName.equalsIgnoreCase("firefox")){
					System.setProperty("webdriver.gecko.driver",TestUtil.FIREFOXDRIVER_MAC);

					//FireFox profile options setting:
					System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "browserLogs");
					FirefoxOptions options=new FirefoxOptions();
					options.setPageLoadStrategy(PageLoadStrategy.NONE);

					ProfilesIni prof=new ProfilesIni();
					FirefoxProfile profile=prof.getProfile("default");
					profile.setPreference("dom.webnotifictions.enabled", false);

					//Untrusted certificate acceptance:
					profile.setAcceptUntrustedCertificates(true);
					profile.setAssumeUntrustedCertificateIssuer(false);

					options.setProfile(profile);
					driver=new FirefoxDriver(options);
				}else if(browserName.equalsIgnoreCase("safari")) {
					System.setProperty("webdriver.safari.noinstall", "true");
					driver = new SafariDriver();
				}

				edriver=new EventFiringWebDriver(driver);
				eventlistener=new WebEventListener();
				edriver.register(eventlistener);
				driver=edriver;
			}
		}


		driver.manage().window().maximize();
		driver.manage().window().setSize(new Dimension(TestUtil.X_COORDINATE,TestUtil.Y_COORDINATE));
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(TestUtil.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(TestUtil.IMPLICIT_WAIT, TimeUnit.SECONDS);

	}


	//********************************Before-After Methods and Initial test Methods ************************//

	public void initial_test_tasks(Hashtable<String,String> data) {
		readNwrite.runmodeCheck(data);

	}



	@BeforeMethod
	public void setUp(Method m) {
		TEST=m.getName().toString();
		className = m.getDeclaringClass().getSimpleName().toString();
		logger=Logger.getLogger(className+"----"+TEST);
		PropertyConfigurator.configure(TestUtil.LOG4J_PROPERTYFILE);

	}

	@AfterClass
	public void tearDown() {

		logger.info("ENDING TESTS FOR CLASS --- "+className);
	}

	@BeforeClass
	public void startUp() {
		className = this.getClass().getSimpleName().toString();
		logger=Logger.getLogger(className);
		PropertyConfigurator.configure(TestUtil.LOG4J_PROPERTYFILE);
		logger.info("STARTING TESTS FOR CLASS --- "+className);
		//readNwrite.classRunmodeCheck();



	}
	@BeforeSuite
	public void start() {
		initialization();
	}
	@AfterSuite
	public void shudown() {
		if(driver!=null)
			driver.quit();
		driver=null;
	}




	//********************************* Generic Functions **********************************************//


	public String getElementAttribute(WebElement element,String attribute) {
		return element.getAttribute(attribute);
	}




	public void clickLink(String linkText) {
		List<WebElement> list=new ArrayList<WebElement>();
		list=driver.findElements(By.tagName("a"));
		for(WebElement e:list) {
			System.out.println(e.getText());
			if(e.getText().toString().equals(linkText)) {
				e.click();
				break;
			}
		}
	}

	public void isElementPresent(String locator, String locatorType) {
		List<WebElement> allElements=null;
		if(locatorType.equalsIgnoreCase("xpath"))
			allElements=driver.findElements(By.xpath(locator));
		else if(locatorType.equalsIgnoreCase("cssSelector"))
			allElements=driver.findElements(By.cssSelector(locator));
		else if(locatorType.equalsIgnoreCase("id"))
			allElements=driver.findElements(By.id(locator));
		if(allElements.size()==0) {
			js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", locator);
			reportPass(locator+" present");}
		else {
			reportFail(locator+" not present");}

	}

	public void isElementPresent(WebElement element) {
		if(element.isDisplayed()) {
			js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
			reportPass(element+" present");
		}
		else 
			reportFail(element+" not present");

	}

	public void selectDropdownElement(WebElement element,String elementName) {
		Select s=new Select(element);
		s.selectByVisibleText(elementName);
	}

	public void navigateTo(String url) {
		driver.navigate().to(url);
	}

	public Set<String> getWindowsSet() {
		Set<String> winIds=driver.getWindowHandles();
		return winIds;
	}

	public void switchToWindowById(String id) {
		Set<String> winSet=getWindowsSet();
		for (String win:winSet) {
			if(win.equals(id)) {
				driver.switchTo().window(id);
			}
		}
	}

	public void switchToDefaultWindow() {
		driver.switchTo().defaultContent();
	}

	public void acceptBrowserPopUp() {
		Alert alert=driver.switchTo().alert();
		alert.accept();
		driver.switchTo().defaultContent();
	}

	public void dismissBrowserPopUp() {
		Alert alert=driver.switchTo().alert();
		alert.dismiss();
		driver.switchTo().defaultContent();
	}

	public String getTextBrowserPopUp() {
		Alert alert=driver.switchTo().alert();
		String text=alert.getText().toString();
		driver.switchTo().defaultContent();
		return text;
	}

	public void elementWait(WebElement element,int seconds) {
		WebDriverWait wait=new WebDriverWait(driver,seconds);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void clickTo(WebElement element) {
		element.click();
	}

	public void getAttributeValue(WebElement element,String attribute) {
		element.getAttribute(attribute);
	}

	public void clearCookies() {
		driver.manage().deleteAllCookies();
	}

	public void navigateBack() {
		driver.navigate().back();
	}

	public void navigateForward() {
		driver.navigate().forward();
	}

	public void moveToElement(WebElement element) {
		Actions act=new Actions(driver);
		act.moveToElement(element).build().perform();
		elementWait(element,10);
	}

	//******************************** Validity Functions ***************************************//

	public void verifyElementText(WebElement element,String expectedText) {
		softAssert.assertEquals(element.getText().toString(), expectedText);
		softAssert.assertAll();
	}

	//******************************** Reporting Functions ***************************************//

	public void reportPass(String message) {
		test.get().log(Status.PASS, message);
	}

	public void reportFail(String message) {
		test.get().log(Status.FAIL, message);
	}

	public void reportInfo(String message) {
		test.get().log(Status.INFO, message);
	}

	public void reportSkip(String message) {
		test.get().log(Status.SKIP, message);
	}


	//*******************************Application Specific Functions*************************//




	public Object navigateToPage(String page_name) {
		WebElement hamburgerIcon=driver.findElement(By.xpath("//*[@class='hamburger-inner']"));
		String str1="//a[contains(text(),'";
		String str2="')]";
		Object obj=null;
		hamburgerIcon.click();

		switch(page_name) {
		case "users":
			driver.findElement(By.xpath(str1+"Users"+str2)).click();
			obj = new IDP_Users();
			break;

		case "applications":
			driver.findElement(By.xpath(str1+"Applications"+str2)).click();
			obj = new IDP_Applications();
			break;

		case "directories":
			driver.findElement(By.xpath(str1+"Directories"+str2)).click();
			obj = new IDP_Directories();
			break;

		case "account":
			driver.findElement(By.xpath(str1+"Account"+str2)).click();
			obj = new IDP_Account();
			break;

		case "tenant":
			driver.findElement(By.xpath(str1+"Tenant"+str2)).click();
			obj = new IDP_Tenant();
			break;

		}
		return obj;
	}

	//********************************Data Provider*****************************************//


	@DataProvider
	public Object[][] dataProvider(Method m) throws FilloException, JsonIOException, JsonSyntaxException, FileNotFoundException{
		Object[][] dataSet=null;
		if(config.getProperty("dataReadConfiguration").equalsIgnoreCase("excel")) {
			System.out.println("data provider is excel");
			dataSet= readAndWriteData.dataSet(m);
		}else if(config.getProperty("dataReadConfiguration").equalsIgnoreCase("json"))

		{
			System.out.println("data provider is json");
			dataSet= jsonReader.jsonTestDataSet(m);
		}

		return dataSet;

	}



}
