import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

enum Status {
    CONTINUE, BREAK, DONE
}

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
 *  1. Compile high-level simpletron instruction into low-level.
 *  2. Dynamic branching with `@branch_name` anywhere in the program.
 *  3. Evaluate arithmetic expressions.
 *  4. Declare variables anywhere.
 *  5. Include only used variables to improve memory efficiency.
 *  6. Show error if variable declared but doesn't have a value.
 *  7. Detect whether the variable already exist.
 *  8. Detect whether the variable doesn't exist.
 *  9. Detect whether the command is valid or not.
 * 10. Single line comment with ">"
 * 11. Append HALT instruction at the end of the program if not explicitly added.
 * ------------------------------------
 */
public class SmpCompiler {
    // Simpletron high-level commands
    private final HashMap<String, Integer> commands = createCommands();
    // Simpletron branch storage
    private final HashMap<String, Integer> branches = new HashMap<String, Integer>();
    // Variable storage
    private final List<SmpVariable> variables = new ArrayList<SmpVariable>();
    // Initialize the program storage
    private final List<String> program = new ArrayList<String>();
    // List of operands
    private final List<Integer> operands = new ArrayList<Integer>();
    // Initialize output
    private final List<String> output = new ArrayList<String>();
    // Input extension name
    private final String INPUT_FILE_EXT = "smp";
    // Output extension name
    private final String OUTPUT_FILE_EXT = "sml";
    // Branch keyword identifier
    private final String BRANCH_IDENTIFIER = "@";
    // Initialize input file name
    private String inputFilename = "";
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
            // Get line, trim, and add to program
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
        // If the program is empty, return
        if (isProgramEmpty()) {
            error("no instructions written (" + inputFilename + ")");
            return;
        }

        // Set initial compilation time
        compilationTime = System.currentTimeMillis();

        // Loop through the program
        for (int i = 0; i < program.size(); i++) {
            // Remove trailing and leading whitespace
            String line = program.get(i).trim();

            // Check if the line is a comment, or
            // Check if the line is empty
            if (line.startsWith(">") || line.isEmpty()) {
                // Proceed to next line
                continue;
            }

            // Check if the line is a variable declaration
            if (line.indexOf("=") != -1) {
                // If it's an arithmetic expression
                if (line.indexOf("+") != -1 || line.indexOf("-") != -1) {
                    // Process expression declaration
                    processExpression(i, line);
                    // Proceed to next line
                    continue;
                }

                // Process variable declaration
                processVariable(i, line);
                // Proceed to next line
                continue;
            }

            // If current line is a branch declaration
            if (line.startsWith(BRANCH_IDENTIFIER)) {
                // Process branch declaration
                processBranch(i, line);
                // Proceed to next line
                continue;
            }

            // Using other commands
            // Split the line by space
            String[] commandTokens = line.split(" ", 2);

            // Get command (e.g READ, STORE, LOAD, ...)
            // If tokens length is only 1 and is not HALT
            if (commandTokens.length == 1 && !commandTokens[0].equals("HALT")) {
                // Incomplete command
                error("incomplete command '" + line + "' in " + getFilenameWithLine(i));
            }

            // Check if the command exist
            if (commands.containsKey(commandTokens[0])) {
                // Process command
                Status status = processCommand(i, commandTokens);

                // Check if the status is done
                if (status == Status.BREAK) {
                    // Break the loop
                    break;
                } else if (status == Status.CONTINUE) {
                    // Proceed to next line
                    continue;
                }
                // Proceed to next line
                continue;
            }

            // Otherwise, throw error
            error("unknown command '" + commandTokens[0] + "' in " + getFilenameWithLine(i));
        }

        // If last instructions doesn't have a HALT
        // Automatically add a HALT instruction
        if (program.get(program.size() - 1).indexOf("HALT") == -1) {
            // Add a HALT
            output.add(commands.get("HALT") + "00");
        }

        // Process operands
        processOperands();

        // Calculate compilation time
        compilationTime = System.currentTimeMillis() - compilationTime;

        // Output file
        if (generateOutput(output)) {
            // Print output statistics
            printOutputStats(output, true);
        }
    }

    // ===================== Utility methods ===================== //

    /**
     * Process expression
     */
    private void processExpression(int i, String line) {
        // Remove all spaces
        line = line.replaceAll(" ", "");
        
        // Split by equal sign
        String[] splits = line.split("=", 2);
        // Get variable name
        String varName = splits[0];
        // Get expression
        String varExpression = splits[1];
        // Flase by default
        boolean isUsed = false;

        // Check if the current expression is used
        // For every line
        for (int j = i + 1; j < program.size(); j++) {
            // Split line by space
            String[] tokens = program.get(j).split(" ", 2);

            // If the line is not a command, proceed to next line
            if (tokens.length != 2) {
                continue;
            }

            // Get operand
            String operand = tokens[1].replaceAll(" ", "");

            // If the expression is used
            if (operand.equals(varName)) {
                isUsed = true;
                break;
            }
        }

        // If not used, return
        // To optimize output and avoid unused code 
        if (!isUsed) {
            return;
        }

        // Split expression by plus sign and minus sign
        String[] expressionSplits = varExpression.split("");
        // Current variable
        String currentVar = "";

        // Parsed expression
        List<String> expression = new ArrayList<String>();

        // Loop through the expression splits
        for (String ch : expressionSplits) {
            // Check if the split is a plus or minus sign
            if (ch.equals("+") || ch.equals("-")) {
                // Add variable to the list
                expression.add(currentVar);
                expression.add(ch);
                // Reset current variable
                currentVar = "";
                // Proceed to next split
                continue;
            }

            // Otherwise, add to current variable
            currentVar += ch;
        }

        // Add the last variable
        expression.add(currentVar);

        // Add var name to the list
        variables.add(new SmpVariable(i, varName, "0"));
    
        // Process expression
        // Start at 2
        for (int j = 2; j < expression.size(); j++) {
            // Get expression component
            String component = expression.get(j);

            // Check if the component is not an operator
            // Then it's a variable
            if (!component.equals("+") && !component.equals("-")) {
                // Get both components
                String v1 = expression.get(j - 2); // (e.g, 5)
                String op = expression.get(j - 1); // (e.g, +)
                String v2 = expression.get(j); // (e.g, 10)

                // Find and get the first variable's address
                int v1Address = getVariableAddress(v1);
                int v2Address = getVariableAddress(v2);

                // Check if the variable is not found
                if (v1Address == -1) {
                    // Throw error
                    error("variable '" + v1 + "' not found in " + getFilenameWithLine(i));
                }

                // Check if the variable is not found
                if (v2Address == -1) {
                    // Throw error
                    error("variable '" + v2 + "' not found in " + getFilenameWithLine(i));
                }

                // Add opcodes to the output
                output.add(commands.get("LOAD").toString());
                output.add(commands.get(op.equals("+") ? "ADD" : "SUBTRACT").toString());
                output.add(commands.get("STORE").toString());

                // Add operands to operands
                operands.add(j > 2 ? getVariableAddress(varName) : v1Address);
                operands.add(v2Address);
                operands.add(getVariableAddress(varName));
            }
        }
    }

    /**
     * Post process variables and operands
     */
    private void processOperands() {
        // Added variables in the output
        List<Integer> addedVariables = new ArrayList<Integer>();

        // For every variables in the program
        for (SmpVariable v : variables) {
            // Loop every operands
            for (int i = 0; i < operands.size(); i++) {
                // If the current operand is same as the current variable address
                if (operands.get(i) == v.address) {
                    // If the variable isn't in the output yet
                    if (!addedVariables.contains(v.address)) {
                        // Then add the variable to the output
                        output.add(v.value);
                        // Added variables
                        addedVariables.add(v.address);
                    }

                    // New address
                    int newAddress = output.size() - 1;
                    // Set new address
                    operands.set(i, newAddress);
                }
            }
        }

        // Loop every operands
        for (int i = 0; i < operands.size(); i++) {
            // Get opcode
            String opcode = output.get(i);
            // Get operand
            int operand = operands.get(i);

            // If opcode number is a branch instruction
            if (opcode.startsWith("40") || opcode.startsWith("41") || opcode.startsWith("42")) {
                continue;
            }
            
            // Set output
            output.set(i, String.valueOf(opcode) + (operand < 10 ? "0" + operand : operand));
        }
    }

    /**
     * Process command 
     * 
     * @param i
     * @param commandTokens
     * @return Status
     */
    private Status processCommand(int i, String[] commandTokens) {
        // Get command
        String command = commandTokens[0];
        // Get operand
        String operand = "";

        // If has operand
        if (commandTokens.length > 1) {
            operand = commandTokens[1];
        }

        // Get opcode
        final String OPCODE = commands.get(command).toString();

        // If command is HALT
        if (command.equals("HALT")) {
            // Add its opcode and exit the loop
            output.add(OPCODE + "00");
            // return break
            return Status.BREAK;
        }
        
        // Otherwise, get value or variable name
        String OPERAND = operand.replaceAll(" ", "");

        // If command is a branch
        if (command.contains("BRANCH")) {
            // Get branch name
            String name = OPERAND.substring(1, OPERAND.length());

            // If branch has no identifier name
            if (name.length() == 0) {
                // Show error
                error("branch name is missing " + getFilenameWithLine(i));
            }

            // Find branch name
            if (branches.containsKey(name)) {
                // Get address
                int addr = branches.get(name);
                // Add to output
                output.add(OPCODE + (addr < 10 ? "0" + addr : addr));
                // Add to operand
                operands.add(-1);
                // Proceed to next line
                return Status.CONTINUE;
            }

            // Flag if branch declaration is after the branch callee
            boolean isFound = false;
            
            // Loop through the file next to the error
            for (int j = i + 1, k = 0; j < program.size(); j++, k++) {
                // Get current line
                String line = program.get(j);
                // Get branch name
                String bname = line.replaceAll(" ", "");

                // If branch name exist after the branch line
                if (bname.startsWith(BRANCH_IDENTIFIER) && bname.contains(name)) {
                    // Adjust address
                    int addr = output.size() + k;
                    // Add to output
                    output.add(OPCODE + (addr < 10 ? "0" + addr : addr));
                    // Add to operand
                    operands.add(-1);
                    // Set found to true
                    isFound = true;
                    // Break the loop
                    break;
                }
            }

            // If branch declaration not found
            if (!isFound) {
                // Show error
                error("branch name '" + BRANCH_IDENTIFIER + name + "' doesn't exist in " + getFilenameWithLine(i));
            }

            // Process to next line
            return Status.CONTINUE;
        }

        // Loop through the variable
        for (SmpVariable v : variables) {
            // If variable name is not null and is same with the current variable
            if (v.name != null && v.name.equals(OPERAND)) {
                // Set vName to the address of that variable
                OPERAND = (v.address < 10 ? "0" + v.address : v.address).toString();
            }
        }

        // Variable not found
        if (operand.equals(OPERAND)) {
            error("variable '" + OPERAND + "' not found in " + getFilenameWithLine(i));
        }

        // Add opcode to output
        output.add(OPCODE);
        // Add operand to operands (to be incremented based on how many lines does the output sml have)
        operands.add(Integer.parseInt(OPERAND));
        // Return success
        return Status.DONE;
    }

    /**
     * Process branch 
     * 
     * @param i
     * @param line
     */
    private void processBranch(int i, String line) {
        // Remove all whitespace
        line = line.replaceAll(" ", "");
        // Get name
        String name = line.substring(1, line.length());

        // Check if branch name already exist
        if (branches.containsKey(name)) {
            // Show error
            error("branch '" + BRANCH_IDENTIFIER + name + "' already exist " + getFilenameWithLine(i));
        }

        // Add branch to branches
        branches.put(name, output.size());
    }

    /**
     * Process variable
     * 
     * @param i
     * @param line
     */
    private void processVariable(int i, String line) {
        // Remove all whitespaces
        line = line.replaceAll(" ", "");

        // Split line by equal (=) sign
        String[] tokens = line.split("=");
        // Get variable name
        String vName = tokens[0];

        // Check if tokens has only 1 value
        if (tokens.length == 1) {
            // Show error
            error("variable '" + vName + "' doesn't have a value " + getFilenameWithLine(i));
        }

        // Get variable value
        String vValue = tokens[1];

        // Check if variable has been declared
        // Loop through declared variables
        for (SmpVariable v : variables) {
            // Check if variable name already exist
            if (v.name.equals(vName)) {
                error("variable '" + vName + "' already exist " + getFilenameWithLine(i));
            }
        }

        // If not exist, then store it in the variables list
        variables.add(new SmpVariable(i, vName, vValue));
    }

    // =========================================================== //

    /**
     * Get variables's address
     * 
     * @param varName
     * @return address
     */
    private int getVariableAddress(String varName) {
        // Loop through the variables
        for (SmpVariable v : variables) {
            // If variable name is not null and is same with the current variable
            if (v.name != null && v.name.equals(varName)) {
                // Return the address of that variable
                return v.address;
            }
        }

        // Variable not found
        return -1;
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
        String name = inputFilename;
        // Get period last index
        int index = inputFilename.lastIndexOf(".");

        // If found
        if (index > 0) {
            // Get filename without extension
            name = name.substring(0, index) + "." + OUTPUT_FILE_EXT;
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
        return "(" + inputFilename + ":" + (address + 1) + ")";
    }

    /**
     * Check if program is empty
     */
    private boolean isProgramEmpty() {
        if (program == null || program.size() == 0) {
            return true;
        }

        for (String line : program) {
            if (line != null && !line.trim().isEmpty()) {
                return false;
            }
        }

        return true;
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
            for (int i = 0; i < output.size(); i++) {
                // Print current line
                System.out.println((i < 10 ? "0" + i : i) + " " + output.get(i));
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
        branches.clear();
        // Reset properties
        inputFilename = "";
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