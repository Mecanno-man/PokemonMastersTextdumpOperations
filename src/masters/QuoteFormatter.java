package masters;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**Create a file for each character of the quotes they say in the Pkmn Center.
 *
 * Note that only sync pairs are formatted correctly,
 * trainer classes and April Fools characters are not supported.
 *
 * @author Mecanno-man
 *
 */

public class QuoteFormatter {

    /**Create a file for each character of quotes they say in the Pkmn Center.
     *
     * Note that only sync pairs are formatted correctly,
     * trainer classes and April Fools characters are not supported.
     *
     * Output descriptors are hardcoded to German.
     *
     * Outputed filenames are romanized Japanese.
     *
     * @param dump - a full text dump as a Map.
     * @throws FileNotFoundException
     */

    public void format(final Map<String, String> dump)
            throws FileNotFoundException {
        for (String key : dump.keySet()) {
            if (key.startsWith("\r\nch0")) {
                String name = key.split("_")[2]; //Japanese
                /* 00 normal, but oak uses 100 and red uses 00 despite being SS.
                 * 09 is April Fool's
                 * Rest used inconsistently
                 */
                String charaversion = key.split("_")[1];

                Map<String, String> map = createTextBoxMap(dump, key);

                PrintWriter writer = new PrintWriter(
                        "characterPokémonCenterQuotes\\"
                        + name + charaversion + ".txt");

                extractQuote("0101_0701", "Zufällig beim Login", map, writer);
                extractQuote("0601_0701",
                        "Zufällig beim ersten Ansprechen", map, writer);
                extractQuote("0601_0702",
                        "Bei erneutem Ansprechen nachdem das"
                        + " vorherige Zitat gesagt wurde",
                        map, writer);
                extractQuote("0602_0701",
                        "Zufällig beim ersten Ansprechen", map, writer);
                extractQuote("0602_0702",
                        "Bei erneutem Ansprechen nachdem das"
                        + " vorherige Zitat gesagt wurde",
                        map, writer);
                extractQuote("0603_0701",
                        "Zufällig beim ersten Ansprechen", map, writer);
                extractQuote("0603_0702",
                        "Bei erneutem Ansprechen nachdem das"
                        + " vorherige Zitat gesagt wurde",
                        map, writer);
                extractQuote("0604_0701",
                        "Zufällig beim ersten Ansprechen", map, writer);
                extractQuote("0604_0702",
                        "Bei erneutem Ansprechen nachdem das"
                        + " vorherige Zitat gesagt wurde",
                        map, writer);
                extractQuote("0605_0701",
                        "Zufällig beim ersten Ansprechen", map, writer);
                extractQuote("0605_0702",
                        "Bei erneutem Ansprechen nachdem das"
                        + " vorherige Zitat gesagt wurde",
                        map, writer);
                extractQuote("1701_0701",
                        "Vor der Übergabe eines Items", map, writer);
                extractQuote("1701_0702",
                        "Nach der Übergabe eines Items", map, writer);
                extractQuote("2501_0701",
                        "Zufällig beim ersten Ansprechen", map, writer);
                extractQuote("2501_0702",
                        "Bei erneutem Ansprechen nachdem das"
                        + " vorherige Zitat gesagt wurde",
                        map, writer);
                extractQuote("2511_0701",
                        "Zufällig beim ersten Ansprechen", map, writer);
                extractQuote("2511_0702",
                        "Bei erneutem Ansprechen nachdem das"
                        + " vorherige Zitat gesagt wurde",
                        map, writer);
                extractQuote("3001_0701",
                        "Zufällig beim Login (morgens)", map, writer);
                extractQuote("3002_0701",
                        "Zufällig beim Login (morgens)", map, writer);
                extractQuote("3003_0701",
                        "Zufällig beim Login (mittags)", map, writer);
                extractQuote("3004_0701",
                        "Zufällig beim Login (mittags)", map, writer);
                extractQuote("3005_0701",
                        "Zufällig beim Login (abends/nachts)", map, writer);
                extractQuote("3006_0701",
                        "Zufällig beim Login (abends/nachts)", map, writer);
                extractQuote("7101_0701",
                        "Zufällig beim Ansprechen", map, writer);
                extractQuote("7102_0701",
                        "Zufällig beim Ansprechen", map, writer);
                extractQuote("7103_0701",
                        "Zufällig beim Ansprechen", map, writer);
                extractQuote("7104_0701",
                        "Zufällig beim Ansprechen", map, writer);
                extractQuote("7105_0701",
                        "Zufällig beim Ansprechen", map, writer);

                writer.close();
            }
        }

    }

    /**Maps text boxes for a given character.
     *
     * @param dump - textdump, as map
     * @param key - filename of a character.
     * @return Map of text boxes of the character.
     */

    private Map<String, String> createTextBoxMap(final Map<String, String> dump,
                                                 final String key) {
        Map<String, String> map = new TreeMap<String, String>();
        String[] lines = dump.get(key).split("\\[ch");
        for (String s : lines) {
            s = s.trim();
            if (!s.isEmpty()) {
                map.put(s.split("_10\\]")[0].split("\\/")[1],
                        s.split("_10\\] ")[1]
                        .replaceAll("\n", " ")
                        .replaceAll("\r", "")
                        //player name
                        .replaceAll("\\[Name:PlayerNickname \\]",
                                "<Name des Spielers>")
                        //355 is a typo in Hapu's quotes
                        .replaceAll("\\[DE:Gen Ref\\=\"(2|3)55\" M=\"", "")
                        .replaceAll("\" F=\"", "/") //gendered language
                        .replaceAll("\" \\]", "")
                        .trim());
            }
        }
        return map;
    }

    /**Create a quote from the multiple successive text bubbles.
     *
     * @param codenumber - ID of quote
     * @param time - situation when quote is triggered
     * @param map - map of text boxes of a given character
     * @param writer - printWriter set to output file
     */
    private void extractQuote(final String codenumber,
            final String time,
            final Map<String, String> map,
            final PrintWriter writer) {
        if (map.containsKey(codenumber + "_01")) {
            writer.write("{{Zitat|");
            boolean f = true;
            /* Not sure what happens if a quote has more than 10 text boxes,
             * as the variance in the variable in the dump is a single digit.*/
            final int max = 10;
            for (int j = 0; j < max; j++) {
                if (map.containsKey(codenumber + "_0" + j)) {
                    /*First text box does not start with white space.
                     *Will starts with 0, the rest with 1,
                     *that's why the bool is necessary.*/
                    if (!f) {
                        writer.write(" ");
                    }
                    writer.write(map.get(codenumber + "_0" + j));
                    f = false;
                }
            }
            writer.write("|" + time + "}}\r\n\r\n");
        }
    }
}
