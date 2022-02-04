import components.map.Map;
import components.map.Map1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This program excepts a text file of words and definitions and creates a
 * glossary in the form of html files
 *
 * @author Jeremiah Hennessy
 *
 */
public final class Glossary {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private Glossary() {
    }

    /**
     * Places words and definitions into a map
     * 
     * @param in
     *            The file to be searched
     * @param terms
     *            The map to be added to
     * @updates terms.content
     * @requires in.is_open
     * @ensures <pre>
     * Reads contents of each word and definition. Once a whitespace is reached
     * a new word is to be read to a new map key and value.
     * Words take up one line, definitions can take up more
     * </pre>
    */
    private static void getDefinitions(SimpleReader in, Map<String,String> terms) {
        assert in.isOpen() : "Violation of: in.isOpen()";
        
        SimpleWriter out = new SimpleWriter1L();
        while(!in.atEOS()) {
            String word = in.nextLine();
            String def = in.nextLine();
            String more = in.nextLine();
            //obtain the definitions past one line
            while(!more.equals("")) {
                def +=  " " + more;
                more = in.nextLine();
            }
            
            if(!terms.hasKey(word)) {
                //update map
                terms.add(word, def);
            }
        }
    }
    
    /**
     * Creates the given html files for each term
     * 
     * @param terms
     *            The terms that the files will be created for
     * @param folder
     *            The name of the folder the files will go in
     * 
     * @ensures <pre>
     * Creates a new html file for each term. Terms cannot be empty.
     * Each html file will have link to corresponding terms if those
     * terms are in their definition. 
     * </pre>
    */
    private static void createTermFiles(Map<String,String> terms, String folder) {
        assert terms != null : "Violation of: terms != null";
        
        for(Map.Pair<String, String> term:terms) {
            //create file and place it in the correct folder
            SimpleWriter termFile = new SimpleWriter1L(folder + "/" + term.key() + ".html");
            
            //create links to words inside of definitions
            String value = term.value();
            for(Map.Pair<String, String> keys:terms) {
                if(value.contains(keys.key())) {
                    String link = "<a href=" + keys.key() + ".html" + ">" + keys.key() + "</a>";
                    value = value.replaceAll(keys.key(), link);
                }
            }

            //format HTML file
            termFile.println("<html>");
            termFile.println("<head>");
            termFile.println("<title>" + term.key() + "</title>");
            termFile.println("</head>");
            termFile.println("<body>");
            termFile.println("<h1 style=color:red;><i>" + term.key() + "</i></h1>");
            termFile.println("<p>" + value + "</p>");
            termFile.println("<hr>");
            termFile.println("<p>" + "Return to " + "<a href=\"" + "Index.html" + "\">" + "Index" + "</a>");
            termFile.println("</body>");
            termFile.print("</html>");
        }
    }
    
    
    /**
     * Creates the index for the glossary 
     * 
     * @param terms
     *            The string of terms in alphabetical order that the glossary
     *            will be created with
     * @param folder
     *            The name of the folder the files will go in
     * 
     * @ensures <pre>
     * Creates a new html file for the index of the glossary. Terms cannot be empty.
     * The html file will link to the definition of each word. Terms must be in alphabetic
     * order
     * </pre>
    */
    private static void createIndex(String[] terms, String folder) {
        assert terms != null : "Violation of: terms != null";
        assert folder != null : "Violation of: folder != null";
        
        //create file and place it in the correct folder
        SimpleWriter index = new SimpleWriter1L(folder + "/" + "index.html");
        
        //format HTML file
        index.println("<html>");
        index.println("<head>");
        index.println("<title>Sample Glossary</title>");
        index.println("</head>");
        index.println("<body>");
        index.println("<h2>Sample Glossary</h2>");
        index.println("<hr>");
        index.println("<h3>Index</h3>");
        index.println("<ul>");
        //create links
        for(int i = 0; i < terms.length; i++) {
            index.println("<li><a href=" + terms[i] + ".html" + ">" + terms[i] + "</a></li>");
        }
        index.println("</ul>");
        index.println("</body>");
        index.print("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        
        out.println("Enter a text file to convert to a glosary: ");
        SimpleReader file = new SimpleReader1L(in.nextLine());
        
        //get folder
        out.println("Enter a folder to place the contents created: ");
        String folder = in.nextLine();
        
        //create map of definitions
        Map<String,String> terms = new Map1L<>();
        getDefinitions(file, terms);
        createTermFiles(terms, folder);
        
        //place map keys in string array to make sorting easy
        String[] ordered = new String[terms.size()];
        int i = 0;
        for(Map.Pair<String, String> keys:terms) {
            ordered[i] = keys.key();
            i++;
        }
        
        //sort strings using a bubble sort
        for(i = 0; i < ordered.length; i++) {
            for(int j = 1; j < ordered.length - i; j++) {
                if(ordered[j-1].compareTo(ordered[j]) > 0) {
                    String temp = ordered[j-1];
                    ordered[j-1] = ordered[j];
                    ordered[j] = temp;
                }   
            }
        }
        //pass the string array to create links in the index
        createIndex(ordered, folder);
        
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
        file.close();
    }

}
