package masters;

import java.util.Map;
import java.util.TreeMap;

/**Strips textdump to a single language.
 *
 * @author Mecanno-man
 *
 */

public class LocalizationSplitter {

    /**Create a map of a textdump that only contains files of a single language.
     *
     * @param lang - language to return
     * @param dump - textdump, as map
     * @return map of textdump in only a single language
     */
    public Map<String, String> split(final String lang,
                                     final Map<String, String> dump) {
        Map<String, String> localizedDump = new TreeMap<String, String>();

        for (String s : dump.keySet()) {
            if (s.contains("_" + lang + ".lsd")) {
                localizedDump.put(s, dump.get(s));
            }
        }

        return localizedDump;
    }

}
