package masters;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

/**Logs files changed between new and old dumps.
 *
 * @author Mecanno-man
 *
 */

public class DiffChecker {

    /**Creates a log of files changed between new and old dumps provided.
     *
     * @param newMap - New textdump, as map
     * @param oldMap - Old textdump, as map
     * @throws FileNotFoundException
     */
    public void run(final Map<String, String> newMap,
            final Map<String, String> oldMap) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("changeLog.txt");
        for (String i : newMap.keySet()) {
            if (oldMap.containsKey(i)) {
                if (!oldMap.get(i).equals(newMap.get(i))) {
                    writer.write("Changed: " + i.trim() + "\r\n");
                }
            } else {
                writer.write("New: " + i.trim() + "\r\n");
            }
        }
        writer.close();
    }
}
