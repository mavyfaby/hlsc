import java.util.Scanner;
import java.util.List;
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
    // Start exeuction address
    private int startAddress = 0;

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

            // Automatically set startAddress based on the variable count
            // If data has 4 characters
            if (data.length() == 4 && startAddress == 0) {
                startAddress = i;
            }

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

    public SmpSimpletron(List<String> program) {
        this.processor = new SmpProcessor();

        for (int i = 0; i < program.size(); i++) {
          this.processor.store(program.get(i), i);
        }

        this.processor.dump();
    }

    /**
     * Execute the program
     */
    public void execute() {
        this.processor.execute(startAddress);
    }
    /**
     * Execute the program with starting address
     */
    public void execute(int start) {
      this.processor.execute(start);
    }

    /**
     * Step-by-step execution
     */
    public void step() {
        this.processor.step();
    }

    public static void main(String[] args) throws Exception {
        SmpSimpletron simpletron = new SmpSimpletron("main.sml");
        simpletron.execute();
    }
}