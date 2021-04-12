package masters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

/**Driver class.
 *
 * @author Mecanno-man
 *
 */
public class Driver {

    /**Main method.
     *
     * @param args - unused
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        Driver m = new Driver();
        m.run();
    }

    /**Map representation of files of new dump.*/
    private Map<String, String> currentDump;
    /**Map representation of files of old dump.*/
    private Map<String, String> oldDump;

    /**Starts the program.
     *
     * @throws FileNotFoundException
     */
    public Driver() throws FileNotFoundException {
        Unpacker u = new Unpacker();
        currentDump = u.unpack(new File("newDump.txt"));
        oldDump = u.unpack(new File("oldDump.txt"));
    }

    private void run() throws FileNotFoundException {
        /*Strip unnecessary languages*/
        LocalizationSplitter locSplit = new LocalizationSplitter();
        Map<String, String> currentGermanDump = locSplit
                .split("de", currentDump);
        Map<String, String> oldGermanDump = locSplit.split("de", oldDump);

        /*Output German-only dump*/
        output(currentGermanDump, "dumpGerman");

        /*Create Log of changed/new Files*/
        DiffChecker diffChecker = new DiffChecker();
        diffChecker.run(currentGermanDump, oldGermanDump);

        DescriptionMapper dm = new DescriptionMapper();

        /*Create move dictionary*/
        dm.map(currentGermanDump.get("\r\nmove_name_de.lsd\r\n"),
                currentGermanDump.get("\r\nmove_description_de.lsd\r\n"),
                new PrintWriter("moveMap.txt"),
                currentGermanDump.get("\r\nmove_description_parts_de.lsd\r\n"));

        /*Create passive skill dictionary*/
        dm.map(currentGermanDump.get("\r\npassive_skill_name_de.lsd\r\n"),
                currentGermanDump
                .get("\r\npassive_skill_description_de.lsd\r\n"),
                new PrintWriter("passiveSkillMap.txt"));

        /*create Pokémon Center quote list*/
        QuoteFormatter qf = new QuoteFormatter();
        qf.format(currentGermanDump);

    }

    /**
     * Outputs a given dump in the same format as the input.
     *
     * Note that the dump is not sorted the same way as the old dump.
     *
     * @param dump - the dump to output
     * @param outputname - Name of the file to be output
     * @throws FileNotFoundException
     */

    private void output(final Map<String, String> dump, final String outputname)
                        throws FileNotFoundException {

        PrintWriter writer = new PrintWriter(outputname + ".txt");
        for (String s : dump.keySet()) {
            writer.print("===========" + s + "===========" + dump.get(s));
        }
        writer.close();
    }
}
