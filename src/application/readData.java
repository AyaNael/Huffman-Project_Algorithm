package application;

public class readData {

    private char character;
    private int frequency;
    private String code;
    private int length;
    private int lengthFreq; // New field for length * frequency

    public readData(char character, int frequency, String code, int length, int lengthFreq) {
        this.character = character;
        this.frequency = frequency;
        this.code = code;
        this.length = length;
        this.lengthFreq = lengthFreq;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLengthFreq() {
        return lengthFreq;
    }

    public void setLengthFreq(int lengthFreq) {
        this.lengthFreq = lengthFreq;
    }
}
