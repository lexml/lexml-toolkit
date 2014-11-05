
package br.gov.lexml.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UserInputReader {

    private static BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    public String readLine() throws Exception {
        return inputReader.readLine();
    }

    public static void main(String[] args) throws Exception {
    	UserInputReader reader = new UserInputReader();
		for(int i = 0; i < 3; i++) {
			System.out.print("texto: ");
			String line = reader.readLine();
			System.out.println(">>>> " + line);
		}
	}

}
