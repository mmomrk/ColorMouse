package PixelHunter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Delit
{
	public static void main( String[] args )
	{
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream("resources/inputData"));

			//get the property value and print it out
			System.out.println(prop.getProperty("ID_pet"));
			System.out.println(prop.getProperty("dbuser"));
			System.out.println(prop.getProperty("dbpassword"));



		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
