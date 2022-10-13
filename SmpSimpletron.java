import java.util.Scanner;
import java.io.File;

/**
 * Simpletron processor
 * 
 * @author Maverick G. Fabroa
 * @date September 29, 2022
 * @based on the code written by sir Dennis Durano
 */
public class SmpSimpletron {
    // The processor
    private SmpProcessor processor;
    // Input extension name
    private final String INPUT_FILE_EXT = "sml";

    /**
     * Initialize the simpletron
     */
    public SmpSimpletron(String filename) throws Exception {
        // Get the file
        File file = new File(filename);

        // Check if the file doesn't exist
        if (!file.exists()) {
            error("file not found " + filename);
        }

        // Check input filename
        if (!isSmlFile(filename)) {
            error("must be a ." + INPUT_FILE_EXT + " file.");
        }

        // Read file
        Scanner sc = new Scanner(file);
        // Initialize the processor
        this.processor = new SmpProcessor();

        // Instruction counter
        int i = 0;

        // For each instruction in the file
        while (sc.hasNextLine()) {
            // Get the instruction
            String data = sc.nextLine().trim();

            // Store the instruction to the memory
            // if the length is greater than 0
            if (data.length() > 0) {
                // Store the instruction to the processor
                this.processor.store(data, i++);
            }
        }

        // Dump the processor
        this.processor.dump();
        // Close scanner
        sc.close();
    }

    /**
     * Execute the program
     */
    public void execute() {
        this.processor.execute();
    }

    /**
     * Step-by-step execution
     */
    public void step() {
        this.processor.step();
    }

    /**
     * Print a line
     */
    private static void line() {
        System.out.println("------------------------------------------");
    }

    /**
     * Check whether the input file name has .sml extension
     * 
     * @param filename
     * @return
     */
    private boolean isSmlFile(String filename) {
        return filename != null && filename.trim().endsWith("." + INPUT_FILE_EXT);
    }

    /**
     * Generate a compilation error message and exit the program
     * 
     * @param message The message
     */
    private static void error(String message) {
        line();
        System.err.println("Error: " + message);
        line();

        System.exit(1);
    }

    /**
     * Main simpletron interpreter
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Check if args have values
        if (args.length > 0) {
            // Intantiate the simpletron interpreter
            // which is assuming a low-level simpletron instructions
            SmpSimpletron simpletron = new SmpSimpletron(args[0]);
            simpletron.execute();
            return;
        }

        // Otherwise, show no input specified
        SmpSimpletron.error("no input file specified.");
    }
}
