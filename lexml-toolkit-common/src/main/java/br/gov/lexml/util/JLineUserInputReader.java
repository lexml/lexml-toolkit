
package br.gov.lexml.util;

import jline.ConsoleReader;

public class JLineUserInputReader implements UserInputReader {

    public String readLine() throws Exception {
        return new ConsoleReader().readLine();
    }

}
