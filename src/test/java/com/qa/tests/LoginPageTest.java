package com.qa.tests;

import java.util.Hashtable;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.qa.base.TestBase;
import com.qa.pages.HomePage;

import junit.framework.Assert;

public class LoginPageTest extends TestBase{

	
	@Test(dataProvider="dataProvider")
	public void loginPageTitleTest(Hashtable<String,String> data) {
		initial_test_tasks(data);
		navigateTo(prop.getProperty("url"));
		HomePage hp=new HomePage();
		hp.clickLink("PEOPLE");
		test.get().log(Status.INFO, "ABOUT TO MAKE TEST CASE FAIL 2");
		Assert.fail();
	}
	

}
