import java.util.Scanner;

/**
 * Simpletron processor
 * 
 * @author Maverick G. Fabroa
 * @date September 29, 2022
 * @based on the code written by sir Dennis Durano
 */
public class SmpProcessor {
    // Program counter
    private int pc;
    // Instruction register
    private String ir;
    // The memory
    private SmpMemory memory;
    // Operation code
    private String opcode;
    // Operand
    private int operand;
    // Accumulator
    private String accumulator;

    /**
     * Initialize memory with the default size
     */
    public SmpProcessor() {
        this.memory = new SmpMemory();
    }

    /**
     * Store the data with the specified address to the memory
     * 
     * @param data    The data to store
     * @param address The address to store
     */
    public void store(String data, int address) {
        // Store the data to the memory
        this.memory.setItem(data, address);
    }

    /**
     * Dump the processor status
     */
    public void dump() {
        System.out.println("\nProgram counter       :  " + this.pc);
        System.out.println("Instruction Register  :  " + this.ir);
        System.out.println("Accumulator           :  " + this.accumulator);
        System.out.println("Opcode                :  " + this.opcode);
        System.out.println("Operand               :  " + this.operand);
    }

    /**
     * Execute the program with start address
     */
    public void execute() {
        // For each instruction
        for (pc = 0; pc < this.memory.getSize() - 1; pc++) {
            // Dump the memory
            this.memory.dump();
            // Fetch the instruction
            fetch(pc);
            // Dump the processor status
            dump();
            // Decode the instruction
            decode();
        }
    }

    /**
     * Step-by-step execution
     */
    public void step() {
        // For each instruction
        for (pc = 0; pc < this.memory.getSize() - 1; pc++) {
            // Dump the memory
            this.memory.dump();
            // Fetch the instruction
            fetch(pc);
            // Dump the processor status
            dump();
            // Decode the instruction
            decode();

            // Wait for user input
            System.out.print("\n\nPress enter key to continue...");
            new Scanner(System.in).nextLine();
        }
    }

    /**
     * Fetch the instruction from the memory
     * 
     * @param address
     */
    public void fetch(int address) {
        // Set the instruction register with the data from the memory
        this.ir = this.memory.getItem(address);

        // Set opcode and operand on set instruction register
        if (this.ir != null && this.ir.length() == 4) {
            this.opcode = this.ir.substring(0, 2);
            this.operand = Integer.parseInt(this.ir.substring(2, 4));
        }
    }

    /**
     * Decode the instruction
     */
    public void decode() {
        String data = "";
        int result = 0;

        // If no opcode
        if (opcode == null) {
            // Don't proceed to decoding
            return;
        }

        // Check the opcode
        switch (opcode) {
            // READ
            case "10":
                // Get user input
                System.out.print("Enter value: ");
                data = new Scanner(System.in).nextLine();
                // Store the data to the memory
                this.memory.setItem(data, this.operand);
                break;

            // WRITE
            case "11":
                // Print the data from the memory
                System.out.printf("\nData from Memory Address (%d) : %s\n\n", this.operand, this.memory.getItem(this.operand));
                break;

            // LOAD
            case "20":
                // Load the data from the memory to the accumulator
                this.accumulator = this.memory.getItem(this.operand);
                break;

            // STORE
            case "21":
                // Store the data from the accumulator to the memory
                this.memory.setItem(this.accumulator, this.operand);
                break;

            // ADD
            case "30":
                // Add the data from the memory to the accumulator
                result = Integer.parseInt(this.accumulator) + Integer.parseInt(this.memory.getItem(this.operand));
                this.accumulator = String.valueOf(result);
                break;

            // SUBTRACT
            case "31":
                // Subtract the data from the memory to the accumulator
                result = Integer.parseInt(this.accumulator) - Integer.parseInt(this.memory.getItem(this.operand));
                this.accumulator = String.valueOf(result);
                break;

            /**
             * If you notice, all branches have - 1 to the program counter,
             * since in the next instruction, the program counter will be
             * incremented by 1 by the for loop.
             * 
             * For example, if the operand is 0 and the program counter is 0,
             * the next counter will be 1, the first instruction will be skipped,
             * and this is the problem. We want to jump to the address 0
             * 
             * To fix this, we subtract 1 from the operand. So, if the operand is 0,
             * next instruction's program counter will be 0.
             * 
             * The same applies to the other branches.
             */

            // BRANCH
            case "40":
                // Set the program counter to the operand
                this.pc = this.operand - 1;
                break;

            // BRANCHNEG
            case "41":
                // Check if the accumulator is negative
                if (Integer.parseInt(this.accumulator) < 0) {
                    // Set the program counter to the operand
                    this.pc = this.operand - 1;
                }

                break;

            // BRANCHZERO
            case "42":
                // Check if the accumulator is zero
                if (Integer.parseInt(this.accumulator) == 0) {
                    // Set the program counter to the operand
                    this.pc = this.operand - 1;
                }

                break;

            // HALT
            case "43":
                System.out.println("\nProgram terminated.");
                System.exit(0);
                break;
        }
    }
}
