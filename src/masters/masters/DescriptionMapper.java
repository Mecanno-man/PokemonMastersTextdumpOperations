package masters;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**Creates a map of a name to a corresponding description.
 *
 * @author Mecanno-man
 */

public class DescriptionMapper {

    Map<Integer, ParameterMapping> parameterMap;
    Map<String, String> moveNames;
    Map<String, String> passiveSkillNames;
    Map<String, String> tagNames;

    public DescriptionMapper(Map<Integer, ParameterMapping> parameterMap, String moveNames, String passiveSkillNames, String passiveSkillNameParts, String tagNames) {
        this.parameterMap = parameterMap;
        String[] names = moveNames.trim().split("\r\n\\[");
        this.moveNames = createMap(names, false);
        String[] tags = tagNames.trim().split("\r\n\\[");
        this.tagNames = createMap(tags, false);

        String[] skills = getCorrectedNames(passiveSkillNames, passiveSkillNameParts).trim().split("\r\n\\[");
        this.passiveSkillNames = createMap(skills, true);

    }

    /**Constructor for cases, where the descriptions does contain variables.
     * Only works with move descriptions
     *
     * @param descriptionString - the description file as string
     * @param descriptionParts - the file containing the description parts,
     *                           as string
     */
    public void mapMoves(final String descriptionString,
                         final String descriptionParts) throws FileNotFoundException {

        Map<String, String> descriptionPartMap = createMap(
                descriptionParts.trim().split("\r\n\\["), false);

        /*Replace part variables with contents of said variables*/
        String[] descriptionStringArray = descriptionString
                .split("(\\[Name:MoveDescriptionPartsIdTag Idx=\"|\" ])");
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

        createMapping(correctDescriptionString);
    }

    /**Constructor for cases where the names and descriptions contain variables.
    * Only works with passive skill descriptions
    *
    * @param descriptionString - the description file as string
    * @param printWriter - a printWriter set to output file
    * @param descriptionParts - the file containing the description parts,
    *                           as string
    */
   public void mapSkills(final String descriptionString,
                         final PrintWriter printWriter,
                         final String descriptionParts) {

       Map<String, String> descriptionPartMap = createMap(
               descriptionParts.trim().split("\r\n\\["), false);

       /*Replace part variables with contents of said variables*/
       String[] descriptionStringArray = descriptionString
               .split("(\\[Name:PassiveSkillDescriptionPartsIdTag Idx=\"|\" "
                       + "])");
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

       createListMapping(printWriter, correctDescriptionString);
   }

    private String getCorrectedNames(String names, String nameParts) {

        Map<String, String> namePartMap = createMap(
                nameParts.trim().split("\r\n\\["), false);
        Map<String, String> namesMap = createMap(
                names.trim().split("\r\n\\["), false);

        String correctNameString = "\r\n";
        for (Map.Entry<String, String> name : namesMap.entrySet()) {
            String correctedName;
            if (name.getValue().contains("PassiveSkillNameParts")) {
                String index = getNamePartId(name.getValue());
                correctedName = "[" + name.getKey() + "] " + name.getValue()
                        .replaceAll("\\[Name:PassiveSkillNameParts Idx=\"" + index + "\" ]", namePartMap.get(index))
                        .replaceAll("\\[Name:PassiveSkillNameDigit ]", String.valueOf(Integer.parseInt(name.getKey()) - Integer.parseInt(index)));
            } else {
                correctedName = "[" + name.getKey() + "] " + name.getValue();
            }
            correctNameString = correctNameString.concat(correctedName + "\r\n");
        }
        return correctNameString;
    }

    private String getNamePartId(String name) {
       return name.split("Name:PassiveSkillNameParts Idx=\"")[1].split("\"")[0];
    }


    private void createMapping(final String descriptionString) throws FileNotFoundException {
        String[] descriptions = descriptionString.trim().split("\r\n\\[");
        Map<String, String> descriptionMap = createMap(descriptions, true);

        StringBuilder output = new StringBuilder("{{#dictionary:{{{1|}}}|\r\n");

        for (Map.Entry<String, String> num : moveNames.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList()) {
            //don't fill map with null values if no description exists
            if (descriptionMap.containsKey(num.getKey())) {
                if (output.toString().contains("\r\n" + moveNames.get(num.getKey()) + "=")) {
                    int i = 2;
                    while(output.toString().contains("\r\n" + moveNames.get(num.getKey()) + i + "=")) {
                        i++;
                    }
                    output.append(moveNames.get(num.getKey())).append(i).append("=").append(descriptionMap.get(num.getKey())).append("\r\n");
                } else {
                    output.append(moveNames.get(num.getKey())).append("=").append(descriptionMap.get(num.getKey())).append("\r\n");
                }
            }
        }
        output.append("}}<noinclude>[[Kategorie:Vorlage]]</noinclude>\r\n");

        String result = output.toString().replace("Gegner-suche", "Gegnersuche")
                .replace("Wenn den Spezial-Angriff des Anwenders erhöht ist.", "Wenn der Spezial-Angriff des Anwenders erhöht ist.")
                .replace("F.-Att.-Schadensabsorp.-v.-Mitstr..", "F.-Att.-Schadensabsorp.-v.-Mitstr..{{sic}}")
                .replace("eines Verbündeten um 4 Erhöht die Volltrefferquote", "eines Verbündeten um 4{{sic}} Erhöht die Volltrefferquote");

        PrintWriter printWriter = new PrintWriter("moveMap.txt");
        printWriter.write(result);
        printWriter.close();
    }

    private void createListMapping(final PrintWriter printWriter,
            final String descriptionString) {
        String[] descriptions = descriptionString.trim().split("\r\n\\[");
        Map<String, String> descriptionMap = createMap(descriptions, true);
        
        List<Map.Entry<String, String>> sorted =
                passiveSkillNames.entrySet().stream()
        	       .sorted(Map.Entry.comparingByValue()).toList();

        
        for (Map.Entry<String, String> entry : sorted) {
        	String num = entry.getKey();
        	printWriter.write("== " + passiveSkillNames.get(num) + " ==\r\n{{Masters Fähigkeit\r\n"
        			+ "|fähigkeit=" + passiveSkillNames.get(num) + "\r\n");
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
    private Map<String, String> createMap(final String[] input, boolean fixParams) {
        Map<String, String> map = new TreeMap<>();
        for (String s : input) {
            String[] numberMapString = s.split("]");
            if (numberMapString.length > 1) {
                for (int i = 2; i < numberMapString.length; i++) {
                    numberMapString[1] = numberMapString[1]
                            .concat("]").concat(numberMapString[i]);
                }
                if (s.endsWith("]")) {
                    numberMapString[1] = numberMapString[1]
                            .concat("]");
                }
            }
            if (fixParams && numberMapString[1].contains("[") && !numberMapString[1].trim().equals("[Name:Item ]")) {
                numberMapString[1] = fixParams(numberMapString[0].replace("[", ""), numberMapString[1]);
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

    private String fixParams(String index, String description) {
        ParameterMapping mapping = parameterMap.get(Integer.parseInt(index));
        while (description.contains("[")) {
            String parameter = description.split("\\[")[1].split("]")[0];
            String idx = parameter.contains("Idx=") ? parameter.split("Idx=\"")[1].split("\"")[0] : "0";
            String ref = parameter.contains("Ref=") ? parameter.split("Ref=\"")[1].split("\"")[0] : "0";
            String singular = parameter.contains("S=") ? parameter.split("S=\"")[1].split("\"")[0] : null;
            String plural = parameter.contains("P=") ? parameter.split("P=\"")[1].split("\"")[0] : null;

            try {
                if (parameter.startsWith("Digit:")) {
                    int mappingIndex = Integer.parseInt(idx) * 2 + 2;
                    Method method = mapping.getClass().getMethod("getParam" + mappingIndex);
                    description = description.replaceAll("\\[" + parameter + "]", ((String) method.invoke(mapping)).replace("per_", ""));
                } else if (parameter.startsWith("Name:ReferencedMessageTag")) {
                    int mappingIndex = Integer.parseInt(idx) * 2 + 2;
                    Method method = mapping.getClass().getMethod("getParam" + mappingIndex);
                    description = description.replaceAll("\\[" + parameter + "]", tagNames.get((String) method.invoke(mapping)));
                } else if (parameter.startsWith("Name:MoveId")) {
                    description = description.replaceAll("\\[" + parameter + "]", moveNames.get(idx));
                } else if (parameter.startsWith("Name:PassiveSkillId")) {
                    description = description.replaceAll("\\[" + parameter + "]", passiveSkillNames.get(idx));
                } else if (parameter.startsWith("DE:")) {
                    int mappingIndex = Integer.parseInt(ref) * 2 + 2;
                    Method method = mapping.getClass().getMethod("getParam" + mappingIndex);
                    description = description.replaceAll("\\[" + parameter + "]", Objects.requireNonNull(Integer.parseInt((String) method.invoke(mapping)) > 1 ? plural : singular));
                } else {
                    throw new RuntimeException("Unexpected new value occurred");
                }
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return description;
    }
}
