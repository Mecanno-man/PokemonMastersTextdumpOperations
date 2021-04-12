package masters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**Formats a Masters Textdump in the way required by the rest of the program.
 *
 * @author Mecanno-man
 *
 */

public class Unpacker {

    /**Formats a txt File comprised of multiple files to a map of the files.
     *
     * @param f the input File. Expected to be a Pokémon Masters textdump.
     * @return a Map of the indivudal files of the textdump
     * @throws FileNotFoundException if file does not exist.
     */

    public Map<String, String> unpack(final File f)
            throws FileNotFoundException {
        Scanner sc = new Scanner(f);
        sc.useDelimiter("===========");
        Map<String, String> map = new TreeMap<String, String>();
        while (sc.hasNext()) {
            map.put(sc.next(), sc.next());
        }
        sc.close();
        return map;
    }

}
