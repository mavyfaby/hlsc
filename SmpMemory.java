/**
 * Simpletron Memory
 * 
 * @author Maverick G. Fabroa
 * @date September 29, 2022
 * @based on the code written by sir Dennis Durano
 */
public class SmpMemory {
    // Size of memory
    private int size;
    // Stored memory instructions
    private String[] memory = null;

    /**
     * Set memory size with the specified size
     * @param size
     */
    public SmpMemory(int size) {
        this.size = size;
        this.memory = new String[size];

        for (int i = 0; i < size; i++) {
            this.memory[i] = "0000";
        }
    }

    /**
     * Set memory size with the default size with data
     */
    public SmpMemory(String[] data) {
        this();

        for (int i = 0; i < data.length; i++) {
            this.memory[i] = data[i];
        }
    }

    /**
     * Set memory size with the default size of 100
     */
    public SmpMemory() {
        this(100);
    }

    /**
     * Check if the specified address is valid
     * @return boolean
     */
    public boolean isAddressValid(int address) {
        return address >= 0 && address < this.size;
    }

    /**
     * Set memory item with the specified data and address
     * @return int
     */
    public boolean setItem(String item, int address) {
        // Check if the address is valid
        if (isAddressValid(address)) {
            // Set the item
            this.memory[address] = item;
            return true;
        }

        return false;
    }

    /**
     * Get memory item with the specified address
     * @return String
     */
    public String getItem(int address) {
        // Check if the address is valid
        if (isAddressValid(address)) {
            // Get the item
            return this.memory[address];
        }

        return null;
    }

    /**
     * Get memory size
     * @return int
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Dump memory
     */
    public void dump() {
        System.out.println("\t-------------------------------------------------------------------");

        for (int i = 0; i < 10; i++) {
            System.out.printf("\t%5d", i);
        }

        System.out.printf("\n%5d", 0);

        for (int i = 0; i < this.size; i++) {
            System.out.printf("\t%5s", this.memory[i]);

            if ((i + 1) % 10 == 0 && i < this.size - 1) {
                System.out.printf("\n%5d", i + 1);
            }
        }

        System.out.println("\n\t-------------------------------------------------------------------");
    }
}
