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

    /**
     * Initialize the simpletron
     */
    public SmpSimpletron(String file) throws Exception {
        // Get the fil
        Scanner sc = new Scanner(new File(file));
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

    public static void main(String[] args) throws Exception {
        SmpSimpletron simpletron = new SmpSimpletron("simple.sml");
        simpletron.execute();
    }
}
