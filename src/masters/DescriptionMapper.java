package masters;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**Creates a map of a name to a corresponding description.
 *
 * @author Mecanno-man
 */

public class DescriptionMapper {

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

    /**Constructor for cases where the names and descriptions contain variables.
    *
    * Only works with passive skill descriptions
    *
    * @param nameString - the name file as string
    * @param descriptionString - the description file as string
    * @param printWriter - a printWriter set to output file
    * @param descriptionParts - the file containing the description parts,
    *                           as string
    * @param nameParts - the file containing the name parts, as string
    */
   public void map(final String nameString,
           final String descriptionString,
           final PrintWriter printWriter,
           final String descriptionParts,
           final String nameParts) {

       Map<String, String> descriptionPartMap = createMap(
               descriptionParts.trim().split("\r\n\\["));

       Map<String, String> namePartMap = createMap(
               nameParts.trim().split("\r\n\\["));

       String[] nameStringArray = nameString
               .split("(\\[Name:PassiveSkillNameParts Idx=\"|\" \\])");
       String correctNameString = "";
       for (int i = 0; i < nameStringArray.length - 1; i += 2) {
           correctNameString = correctNameString
                   .concat(nameStringArray[i])
                   .concat(namePartMap
                           .get(nameStringArray[i + 1]));
       }
       correctNameString = correctNameString
               .concat(nameStringArray[nameStringArray
                                              .length - 1]);


       /*Replace part variables with contents of said variables*/
       String[] descriptionStringArray = descriptionString
               .split("(\\[Name:PassiveSkillDescriptionPartsIdTag Idx=\"|\" "
                       + "\\])");
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

       createListMapping(correctNameString, printWriter, correctDescriptionString);
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
    
    private void createListMapping(final String nameString,
            final PrintWriter printWriter,
            final String descriptionString) {
        String[] descriptions = descriptionString.trim().split("\r\n\\[");
        String[] names = nameString.trim().split("\r\n\\[");
        Map<String, String> nameMap = createMap(names);
        Map<String, String> descriptionMap = createMap(descriptions);
        
        List<Map.Entry<String, String>> sorted =
        		nameMap.entrySet().stream()
        	       .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

        
        for (Map.Entry<String, String> entry : sorted) {
        	String num = entry.getKey();
        	printWriter.write("== " + nameMap.get(num) + " ==\r\n{{Masters Fähigkeit\r\n"
        			+ "|fähigkeit=" + nameMap.get(num) + "\r\n");
            //don't add null descriptions
            if (descriptionMap.containsKey(num)) {
                printWriter.write("|beschreibung="
            + descriptionMap.get(num) + "\r\n");
            }
            printWriter.write("}}\r\n");
        }
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
            if (numberMapString.length > 1) {
                for (int i = 2; i < numberMapString.length; i++) {
                    numberMapString[1] = numberMapString[1]
                            .concat("]").concat(numberMapString[i]);
                }
            }
            map.put(numberMapString[0]
                            .trim()
                            .replace("[", "")
                            //For line breaks on hyphens
                            .replaceAll("- ?\\r?\\n", "-")
                            //no line breaks, including trailing whitespace
                            .replaceAll(" ?\\r?\\n", " "),
                    numberMapString[1]
                            .trim()
                            //For line breaks on hyphens
                            .replaceAll("- ?\\r?\\n", "-")
                            //no line breaks, and trim trailing whitespace
                            .replaceAll(" ?\\r?\\n", " "));
        }
        return map;
    }

}
