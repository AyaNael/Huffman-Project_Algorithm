package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HuffmanDecompress {

	private TNode<Integer> root;
	private String pathOfCompressedFile;
	private byte[] buffer; // data is stored here before being written to the compressed file
	private int sizeOfOriginalFile;
	private int sizeOfHeader;
	private File compressedFile;
	private String originalFileExtension;

	public HuffmanDecompress(String pathOfCompressedFile) {
		super();
		this.pathOfCompressedFile = pathOfCompressedFile;
		this.compressedFile = new File(pathOfCompressedFile);
		if (compressedFile.exists() && getFileExtension(compressedFile).equals(".huf"))
			decompress();
	}

	private void decompress() {
		readCompressedFile();
	}

	private void readCompressedFile() {
		try {
			DataInputStream inStream = new DataInputStream(
					new BufferedInputStream(new FileInputStream(compressedFile)));
			originalFileExtension = inStream.readUTF(); // read original File extension
			sizeOfOriginalFile = inStream.readInt(); // read size of original file
			sizeOfHeader = inStream.readInt(); // read size of the header
			System.out.println("originalFileExtension: " + originalFileExtension);
			System.out.println("sizeOfOriginalFile: " + sizeOfOriginalFile);
			System.out.println("sizeOfHeader: " + sizeOfHeader);
			buildHuffmanTree(inStream); // build huffman tree by reading the header information
			System.out.println("wwwwwwwww");
			traversePostOrder();
//			System.out.println(CreateHuffmanCodingTree());
			readCompressedData(inStream); // read data for decompression
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	// values from bitNumber = 0 to this bit number, keep them as they are
//	byte val2 = 0;
//	for (int j = 0; j < 8 - bitNumber; j++) {
//		val2 = (byte) (val2 << 1);  // shift left
//		val2 = (byte) (val2 | 1); // make first digit = 1 
//	}
//	newVal = (byte) (newVal & val2);

	public void traversePostOrder() {
		traversePostOrder(root);
	}

	private void traversePostOrder(TNode<Integer> curr) {
		if (curr == null)
			return;
		traversePostOrder(curr.getLeft());
		traversePostOrder(curr.getRight());
		System.out.print(curr + " ");
	}

	// read the header information to build huffman tree
	private void buildHuffmanTree(DataInputStream inStream) {
		ArrayStack<TNode<Integer>> stack = new ArrayStack<TNode<Integer>>(256);
		buffer = new byte[sizeOfHeader];
		try {
			inStream.read(buffer);
			int pointer = 0;
			int count = 0;
			while (pointer < (((sizeOfHeader * 8) / 10) * 10) - 1) { // while (pointer < (sizeOfHeader * 8) - 5) ? //
																		// ((sizeOfHeader * 8) / 10) * 10
				// truncates any remainder, effectively rounding down to the nearest multiple of
				// 10.
				// prevent from reaching padding bits
				System.out.println("pointer: " + pointer);
				int byteNumber = pointer / 8;
				int bitNumber = pointer % 8;// 0 for the leftmost bit to 7 for the rightmost
				byte temp = (byte) (buffer[byteNumber] << bitNumber);
//				System.out.println("bitNumber= " + bitNumber);
				temp = (byte) (temp >> 7); // leftmost bit (0 or 1), we need only the bit to which the pointer points (0
											// or 1)
				temp = (byte) (temp & 1);// Ensure the extracted value is a single bit (0 or 1)
				System.out.println("temp = " + temp);
				if (temp == 0) { // non-leaf node
					TNode<Integer> right = stack.pop();
					TNode<Integer> left = stack.pop();
					TNode<Integer> newNode = new TNode<>(null, 0);
					newNode.setRight(right);
					newNode.setLeft(left);
					stack.push(newNode);
					pointer++;
					count++;
				}

				else if (temp == 1) { // leaf node
					pointer++; // to start reading the next character
					byteNumber = pointer / 8;
					bitNumber = pointer % 8;
//					System.out.println(bitNumber);
					int firstVal = (buffer[byteNumber] << bitNumber);
					int numberOfRemainingBits = bitNumber;
					if (numberOfRemainingBits > 0) {
						byteNumber += 1;
						pointer = byteNumber * 8; // to point to the start of the next byte.
						bitNumber = 0; // reset the bit
						int secondVal = (buffer[byteNumber] >> (8 - numberOfRemainingBits)); // shift right with sign
																								// extension
						byte val2 = 0;
						for (int j = 0; j < numberOfRemainingBits; j++) {
							val2 = (byte) (val2 << 1); // shift left
							val2 = (byte) (val2 | 1); // make first digit = 1
						}
						secondVal = (byte) (secondVal & val2);// This ensures that secondVal retains only the last
																// numberOfRemainingBits.
						byte asciiCode = (byte) (firstVal | secondVal); // get ASCII code for the character
						TNode<Integer> newNode = new TNode<>((int) asciiCode, 0);
						int data = newNode.getData();
						System.out.println("data: " + (char) data);
						stack.push(newNode);
						pointer += numberOfRemainingBits;
					} else {
						byte asciiCode = (byte) firstVal;
						TNode<Integer> newNode = new TNode<>((int) asciiCode, 0);
						int data = newNode.getData();
						System.out.println("data: " + (char) data);
						stack.push(newNode);
						pointer += 8;
					}
				}
			}
			root = stack.pop(); // tree is created
			System.out.println("root = " + root.getFreq());
			System.out.println(stack.pop());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// read data for decompression from the compressed file
	private void readCompressedData(DataInputStream inStream) {
		TNode<Integer> currNode = root;
		try {
			// Generate decompressed file path in the same directory as the compressed file
			 String decompressedFilePath = compressedFile.getParent() + File.separator +
		                compressedFile.getName().replace(getFileExtension(compressedFile), "_decompressed" + originalFileExtension);

			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(decompressedFilePath));
			buffer = new byte[8];
			refillBufferWithZeros();
			inStream.read(buffer);

//			System.out.println("\nPlease work!");
//			for(int i = 0; i < buffer.length; i++) {
//				System.out.print(buffer[i] + " ");
//			}
//			System.out.println();
			System.out.println("sizeOfOriginalFile: " + sizeOfOriginalFile);

			int charNo = 0;
			while (charNo < sizeOfOriginalFile) {
				int pointer = -1;
				while (pointer < (buffer.length * 8) - 1) {

					pointer++;

//					if (currNode == null)
//						continue;

					if (currNode.isLeaf()) {
						charNo++;
						outStream.write(currNode.getData()); // write the character into the uncompressed file
						currNode = root;
						if (charNo == sizeOfOriginalFile)
							break;
					}

					int byteNumber = pointer / 8;
					int bitNumber = pointer % 8;
					// get the bit into which the pointer points
					byte newVal = (byte) (buffer[byteNumber] << bitNumber);
					newVal = (byte) ((newVal >> 7) & 1);
					if (newVal == 0) { // go left
						currNode = currNode.getLeft();
					} else if (newVal == 1) { // go right
						currNode = currNode.getRight();
					}

				}
				buffer = new byte[8];
				refillBufferWithZeros();
				inStream.read(buffer);

			}
			outStream.close();
			System.out.println("Decompressed file saved at: " + decompressedFilePath);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// refill the buffer array with zeros for reusing
	private void refillBufferWithZeros() {
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = 0;
	}

	// get file extension
	private String getFileExtension(File file) {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return ""; // empty extension
		}
		return name.substring(index);
	}

	public byte[] getBuffer() {
		return buffer;
	}

}