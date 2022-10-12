import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * A variable class with address
 */
class SmpVariable {
    public int address;
    public String name;
    public String value;

    public SmpVariable(int address, String name, String value) {
        this.address = address;
        this.name = name;
        this.value = value;
    }
}

/**
 * ------------------------------------
 * High-level Simpletron Instructions Compiler
 * 
 * @author Maverick G. Fabroa
 * @date October 11, 2022
 * 
 * ------------ Features: -------------
 * 
 * 1. Compile high-level simpletron instruction into low-level
 * 2. Detect whether the variable already exist.
 * 3. Detect whether the variable doesn't exist.
 * 4. Detect whether the command is valid or not.
 * 5. Single line comment with ">"
 * 6. Append HALT instruction at the end of the program if not explicitly added.
 * ------------------------------------
 */
public class SmpCompiler {
    // Simpletron high-level commands
    private final HashMap<String, Integer> commands = createCommands();
    // Variable storage
    private final List<SmpVariable> variables = new ArrayList<SmpVariable>();
    // Initialize the program storage
    private final List<String> program = new ArrayList<String>();
    // List of operands
    private final List<Integer> operands = new ArrayList<Integer>();
    // Input extension name
    private final String INPUT_FILE_EXT = "smp";
    // Output extension name
    private final String OUTPUT_FILE_EXT = "sml";
    // Initialize input file name
    private String inputFilename = "";
    // Excluded lines count
    private int excludedLines = 0;
    // Compilation time
    private long compilationTime = 0;

    /**
     * Initialize compiler with file name
     * 
     * @param filename
     * @throws FileNotFoundException
     */
    public SmpCompiler(String filename) throws FileNotFoundException {
        // Get the file
        File file = new File(filename);

        // Check if the file doesn't exist
        if (!file.exists()) {
            error("file not found " + filename);
        }

        // Check input filename
        if (!isSmpFile(filename)) {
            error("must be a ." + INPUT_FILE_EXT + " file.");
        }

        // Reset simpletron properties
        reset();

        // Otherwise, read the file
        Scanner sc = new Scanner(file);

        // Read the file line by line
        while (sc.hasNextLine()) {
            // Get line, trim, and add instruction to program if
            program.add(sc.nextLine().trim());
        }

        // Set input file namme
        inputFilename = filename;
        // Close the scanner
        sc.close();
    }

    /**
     * Compiles the program
     * 
     * @param program List of high-level instructions
     */
    public void compile() throws Exception {
        // Set initial compilation time
        compilationTime = System.currentTimeMillis();
        // Initialize output
        List<String> output = new ArrayList<String>();

        // Loop through the program
        for (int i = 0, initialAddress = 0; i < program.size(); i++) {
            // Remove trailing and leading whitespace
            String line = program.get(i).trim();

            // Check if the line is a comment, or
            // Check if the line is empty
            if (line.startsWith(">") || line.isEmpty()) {
                // Increment excluded lines if line is a comment or an empty
                excludedLines++;
                // Proceed to next line
                continue;
            }

            // Check if the line is a variable declaration
            if (line.indexOf("=") != -1) {
                // Remove all whitespaces
                line = line.replaceAll(" ", "");

                // Split line by equal (=) sign
                String[] tokens = line.split("=");
                // Get variable name
                String vName = tokens[0];
                // Get variable value
                String vValue = tokens[1];

                // Check if variable has been declared
                // Loop through declared variables
                for (SmpVariable v : variables) {
                    // Check if variable name already exist
                    if (v.name.equals(vName)) {
                        error("variable '" + vName + "' already exist " + getFilenameWithLine(initialAddress));
                    }
                }

                // If not exist, then store it in the variables list
                variables.add(new SmpVariable(i, vName, vValue));
                // Increment initial address
                initialAddress++;
                // Proceed to next line
                continue;
            }

            // Using other commands
            // Split the line by space
            String[] tokens = line.split(" ");
            // Get command (e.g READ, STORE, LOAD, ...)
            String command = tokens[0];

            // Check if the command exist
            if (commands.containsKey(command)) {
                // Get opcode
                final String OPCODE = commands.get(command).toString();

                // If command is HALT
                if (command.equals("HALT")) {
                    // Add its opcode and exit the loop
                    output.add(OPCODE + "00");
                    break;
                }

                // Otherwise, get value or variable name
                String OPERAND = tokens[1];

                // Loop through the variable
                for (SmpVariable v : variables) {
                    // If variable name is not null and is same with the current variable
                    if (v.name != null && v.name.equals(OPERAND)) {
                        // Set vName to the address of that variable
                        OPERAND = String.valueOf(v.address < 10 ? "0" + v.address : v.address);
                    }
                }

                // Variable not found
                if (tokens[1].equals(OPERAND)) {
                    error("variable '" + OPERAND + "' not found in " + getFilenameWithLine(initialAddress));
                }

                // Add opcode to output
                output.add(OPCODE);
                // Add operand to operands (to be incremented based on how many lines does the output sml have)
                operands.add(Integer.parseInt(OPERAND));
                // Increment initial address
                initialAddress++;
                // Proceed to next line
                continue;
            }

            // Otherwise, throw error
            error("unknown command '" + command + "' in " + getFilenameWithLine(initialAddress));
        }

        // If last instructions doesn't have a HALT
        // Automatically add a HALT instruction
        if (program.get(program.size() - 1).indexOf("HALT") == -1) {
            // Add a HALT
            output.add(commands.get("HALT") + "00");
        }

        // Get total line number of the output sml program
        final int LINES = output.size();

        // Loop every operands
        for (int i = 0; i < operands.size(); i++) {
            // Get new address of the operands (variables) based on how many lines the output program have
            final int NEW_ADDRESS = operands.get(i) + LINES;
            // And append it to the opcode
            output.set(i, output.get(i) + (NEW_ADDRESS < 10 ? "0" + NEW_ADDRESS : NEW_ADDRESS));
        }

        // For every variables in the program
        for (SmpVariable v : variables) {
            // If the variable is in the operands
            if (operands.contains(v.address)) {
                // Then add the variable to the output
                output.add(v.value);
            }
        }

        // Calculate compilation time
        compilationTime = System.currentTimeMillis() - compilationTime;

        // Output file
        if (generateOutput(output)) {
            // Print output statistics
            printOutputStats(output, true);
        }
    }

    /**
     * Generate low-level simpletron instructions
     * 
     * @param program
     * @return boolean
     * @throws Exception
     */
    private boolean generateOutput(List<String> program) throws Exception {
        // Initialize file output name
        String outputFilename = getOutputFilename();
        // Create file
        File file = new File(outputFilename);

        // Create file if not exist or file already exist
        if (file.createNewFile() || file.exists()) {
            // Initialize FileWriter with file
            FileWriter io = new FileWriter(file);
            // For every line in program
            for (String line : program) {
                // Write the ouput to the file
                io.write(line + "\n");
            }

            // Close filewriter
            io.close();
            // Return success
            return true;
        }

        return false;
    }

    /**
     * Get output filename based on the input file name
     * 
     * @return file name
     */
    private String getOutputFilename() {
        // Set default output name
        String name = "";
        // Split file name by period
        String[] tokens = inputFilename.split("\\.");

        // If tokens length greater than 1
        if (tokens.length > 1) {
            // For every tokens
            for (int i = 0; i < tokens.length - 1; i++) {
                // Add its chunk name
                name += tokens[i];
            }

            // Add output extension
            name += "." + OUTPUT_FILE_EXT;
        }

        // return name
        return name;
    }

    /**
     * Check whether the input file name have .smp extension
     * 
     * @param filename
     * @return
     */
    private boolean isSmpFile(String filename) {
        return filename != null && filename.trim().endsWith("." + INPUT_FILE_EXT);
    }

    /**
     * Get filename with line number based on the program execution index
     * 
     * @param address
     * @return filename with line
     */
    private String getFilenameWithLine(int address) {
        return "(" + inputFilename + ":" + ((address + 1) + excludedLines) + ")";
    }

    /**
     * Print compilation output statistics
     * 
     * @param output
     * @param showOutput
     */
    private void printOutputStats(List<String> output, boolean showOutput) {
        // Get file size
        final long SIZE = new File(getOutputFilename()).length();
        // Print info
        line();
        System.out.println("Compiled to      : " + getOutputFilename() + " (" + SIZE + " bytes)");
        System.out.println("Compilation time : " + compilationTime + " ms");
        System.out.println("Number of lines  : " + output.size());
        line();
        
        // If showOutput
        if (showOutput) {
            // For every line in the output
            for (String line : output) {
                // Print current line
                System.out.println(line);
            }
    
            line();
        }
    }

    /**
     * Get low-level simpletron instructions
     * 
     * @return key-value pair of the instructions
     */
    private HashMap<String, Integer> createCommands() {
        HashMap<String, Integer> commands = new HashMap<String, Integer>();

        commands.put("READ", 10);
        commands.put("WRITE", 11);
        commands.put("LOAD", 20);
        commands.put("STORE", 21);
        commands.put("ADD", 30);
        commands.put("SUBTRACT", 31);
        commands.put("BRANCH", 40);
        commands.put("BRANCHNEG", 41);
        commands.put("BRANCHZERO", 42);
        commands.put("HALT", 43);

        return commands;
    }

    /**
     * Reset simpletron properties
     */
    private void reset() {
        // Reset list
        variables.clear();
        program.clear();
        operands.clear();
        // Reset properties
        inputFilename = "";
        excludedLines = 0;
        compilationTime = 0;
    }

    /**
     * Print a line
     */
    private static void line() {
        System.out.println("------------------------------------------");
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
     * Main program
     * 
     * @param args Name of the input
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Check if args have values
        if (args.length > 0) {
            // Intantiate the high-level simpletron compiler with the first value
            // which is assuming an input high-level simpletron instructions
            SmpCompiler compiler = new SmpCompiler(args[0]);
            compiler.compile();
            return;
        }

        // Otherwise, show no input specified
        SmpCompiler.error("no input file specified.");
    }
}