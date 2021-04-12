package masters;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**Creates a map of a name to a corresponding description.
 *
 * @author Mecanno-man
 */

public class DescriptionMapper {

    /**Constructor for cases where the descriptions don't contain variables.
     *
     * @param nameString - the name file as string
     * @param descriptionString - the description file as string
     * @param printWriter - a printWriter set to output file
     */

    public void map(final String nameString,
            final String descriptionString,
            final PrintWriter printWriter) {
        createMapping(nameString, printWriter, descriptionString);
    }

    /**Constructor for cases, where the descriptions does contain variables.
     *
     * Only works with move descriptions
     *
     * @param nameString - the name file as string
     * @param descriptionString - the description file as string
     * @param printWriter - a printWriter set to output file
     * @param descriptionParts - the file containing the description parts,
     *                           as string
     */
    public void map(final String nameString,
            final String descriptionString,
            final PrintWriter printWriter,
            final String descriptionParts) {

        Map<String, String> descriptionPartMap = createMap(
                descriptionParts.trim().split("\r\n\\["));

        /*Replace part variables with contents of said variables*/
        String[] descriptionStringArray = descriptionString
                .split("(\\[Name:MoveDescriptionPartsIdTag Idx=\"|\" \\])");
        String correctDescriptionString = "";
        for (int i = 0; i < descriptionStringArray.length - 1; i += 2) {
            correctDescriptionString = correctDescriptionString
                    .concat(descriptionStringArray[i])
                    .concat(descriptionPartMap
                            .get(descriptionStringArray[i + 1]));
        }
        correctDescriptionString = correctDescriptionString
                .concat(descriptionStringArray[descriptionStringArray
                                               .length - 1]);

        createMapping(nameString, printWriter, correctDescriptionString);
    }

    private void createMapping(final String nameString,
            final PrintWriter printWriter,
            final String descriptionString) {
        String[] descriptions = descriptionString.trim().split("\r\n\\[");
        String[] names = nameString.trim().split("\r\n\\[");
        Map<String, String> nameMap = createMap(names);
        Map<String, String> descriptionMap = createMap(descriptions);

        printWriter.write("{{#dictionary:{{{1|}}}|\r\n");

        for (String num : nameMap.keySet()) {
            //don't fill map with null values if no description exists
            if (descriptionMap.containsKey(num)) {
                printWriter.write(nameMap.get(num) + "="
            + descriptionMap.get(num) + "\r\n");
            }
        }
        printWriter.write("}}<noinclude>[[Kategorie:Vorlage]]</noinclude>\r\n");
        printWriter.close();
    }

    /**Creates a map of IDs to contents of a specified file.
     *
     * @param input - input file as String
     * @return Map of IDs mapped to contents
     */
    private Map<String, String> createMap(final String[] input) {
        Map<String, String> map = new TreeMap<String, String>();
        for (String s : input) {
            String[] numberMapString = s.split("\\]");
            map.put(numberMapString[0]
                            .trim()
                            .replace("[", "")
                            //no line breaks, and trim trailing whitespace
                            .replaceAll(" ?\r\n", " ")
                            //For previous line breaks on hyphens
                            .replaceAll("- ", "-"),
                    numberMapString[1]
                            .trim()
                            //no line breaks, and trim trailing whitespace
                            .replaceAll(" ?\r\n", " ")
                            //For previous line breaks on hyphens
                            .replaceAll("- ", "-"));
        }
        return map;
    }

}
