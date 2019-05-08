package com.qa.util;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.qa.base.TestBase;

public class test extends TestBase{
	public static Workbook book;
	public static Sheet sheet;
	@Test(dataProvider="Provider")
	public void testing(Hashtable<String,String>data) {
		 int i,m=0,flag=0;      
		  int n=3;//it is the number to be checked    
		  m=n/2;      
		  if(n==0||n==1){  
		   System.out.println(n+" is not prime number");      
		  }else{  
		   for(i=2;i<=m;i++){      
		    if(n%i==0){      
		     System.out.println(n+" is not prime number");      
		     flag=1;      
		     break;      
		    }      
		   }      
		   if(flag==0)  { System.out.println(n+" is prime number"); }  
		  }//end of else
	}

	@DataProvider
	public Object[][] dataSet(Method m) throws FilloException {
		String testName=m.getName().trim();
		String className=m.getDeclaringClass().getSimpleName().toString().trim();
		Hashtable<String,String> table = null;
		Fillo fillo=new Fillo();
		Connection connection=fillo.getConnection(System.getProperty("user.dir")+"/src/main/java/com/qa/testdata/Test_Suite_Data.xlsx");
		String query="Select * from "+className+" where TestName='"+testName+"' and Runmode='Y'";
		Recordset rs=connection.executeQuery(query); 
		List<String> list=rs.getFieldNames();
		Object[][] data=new Object[rs.getCount()][1];
		int i=0;
		while(rs.next()) {
			table=new Hashtable<String,String>();
			for(int j=0;j<list.size();j++) {
				table.put(list.get(j), rs.getField(list.get(j)));

			}
			data[i][0]=table;
			i++;

		}

		rs.close();
		connection.close();

		return data;

	}




}
