
package br.gov.lexml.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WindowsUserInputReader implements UserInputReader {

    private static BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    public String readLine() throws Exception {
        return inputReader.readLine();
    }

}
