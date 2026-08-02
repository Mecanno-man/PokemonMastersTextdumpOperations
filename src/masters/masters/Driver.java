package masters;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**Driver class.
 *
 * @author Mecanno-man
 *
 */
public class Driver {

    /**Main method.
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Driver m = new Driver();
        m.run();
    }

    /**Map representation of files of new dump.*/
    private final Map<String, String> currentDump;
    /**Map representation of files of old dump.*/
    private final Map<String, String> oldDump;

    private final Map<Integer, ParameterMapping> parameterMap = new HashMap<>();

    /**Starts the program.
     *
     * @throws FileNotFoundException
     */
    public Driver() throws FileNotFoundException {
        Unpacker u = new Unpacker();
        currentDump = u.unpack(new File("newDump.txt"));
        oldDump = u.unpack(new File("oldDump.txt"));
        ObjectMapper objectMapper = new ObjectMapper();
        ParameterMappings parameterMappings = objectMapper.readValue(new File("MoveAndPassiveSkillDigit.json"), ParameterMappings.class);
        Arrays.stream(parameterMappings.getEntries()).forEach(e -> parameterMap.put(e.getId(), e));
    }

    private void run() throws FileNotFoundException {
        /*Strip unnecessary languages*/
        LocalizationSplitter locSplit = new LocalizationSplitter();
        Map<String, String> currentGermanDump = locSplit
                .split("de", currentDump);
        Map<String, String> oldGermanDump = locSplit.split("de", oldDump);
        

        /*Output German-only dump*/
        output(currentGermanDump, "dumpGerman");
        
        output(currentGermanDump.entrySet().stream()
        		.filter(e -> e.getKey().contains("passive_skill_name"))
        		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, y) -> y, LinkedHashMap::new)), "passiveSkills");

        /*Create Log of changed/new Files*/
        DiffChecker diffChecker = new DiffChecker();
        diffChecker.run(currentGermanDump, oldGermanDump);

        DescriptionMapper dm = new DescriptionMapper(parameterMap,
                currentGermanDump.get("\r\nmove_name_de.lsd\r\n"), currentGermanDump.get("\r\npassive_skill_name_de.lsd\r\n"),
                currentGermanDump.get("\r\npassive_skill_name_parts_de.lsd\r\n"), currentGermanDump.get("\r\ntag_name_with_prepositions_de.lsd\r\n"));

        /*Create move dictionary*/
        dm.mapMoves(currentGermanDump.get("\r\nmove_description_de.lsd\r\n"), currentGermanDump.get("\r\nmove_description_parts_de.lsd\r\n"));

        /*Create passive skill list*/
        dm.mapSkills(currentGermanDump
                .get("\r\npassive_skill_description_de.lsd\r\n"),
                new PrintWriter("passiveSkillList.txt"),
                currentGermanDump
                .get("\r\npassive_skill_description_parts_de.lsd\r\n"));

        /*create Pokémon Center quote list*/
        QuoteFormatter qf = new QuoteFormatter();
        qf.format(currentGermanDump);

    }

    /**
     * Outputs a given dump in the same format as the input.
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
