/*
 * Project 1
*/

import java.awt.image.BufferedImage;
import java.lang.Object.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.Color;


public class readImage
{
  private int imageCount = 1;
  static double intensityBins [] = new double [26];
  static int intensityMatrix [][] = new int[100][26];
  static double colorCodeBins [] = new double [64];
  static int colorCodeMatrix [][] = new int[100][64];
  private static BufferedImage imagesArray[] = new BufferedImage[100];
  private ImageIcon icon;
  private static PrintWriter colorCodes;
  private static PrintWriter intensity;

  /*Each image is retrieved from the file.  The height and width are found for the image and the getIntensity and
   * getColorCode methods are called.
  */
  public readImage()
  {
	  try {
		  colorCodes = new PrintWriter("colorCodes.txt");
		  intensity = new PrintWriter("intensity.txt");
	  } catch (FileNotFoundException fnfe) {
		  System.out.println("Corrupted File(s).");
		  fnfe.printStackTrace();
	  }
	  
	  initializeIntensityBins();//initialize the intensity bins
	  initializeColorCodeBins();//initialize the color code bins
	  
    while (imageCount < 101){
    	BufferedImage image = null;
      try
      {
    	  //kept for backwards compatibility
          icon = new ImageIcon(getClass().getResource("images/" + imageCount++ + ".jpg"));
		try {
			File file = new File("C:\\Users\\Local Admin\\Documents\\workspace\\CSS 490\\bin\\images\\"
					+ (imageCount - 1) + ".jpg");//TODO: Make File Path generic
			image = ImageIO.read(file);
		} catch (Exception e) {
			System.out.println("File Not Found.");
			e.printStackTrace();//print method call stack trace
		}
    	  
          if (icon == null) {
        	  throw new IOException("Null icon.");//invalid icon
          }
          writeIntensity(image);//add image intensity to the database
          writeColorCode(image);//add color codes to the database
      } 
      catch (IOException e)
      {
        System.out.println("Error occurred when reading the file.");
        e.printStackTrace();//print the error stack call
      }
      imagesArray[imageCount - 2] = image;
    }
    
    printIntensityMatrixToFile();//prints the intensity matrix to the file
    printColorCodeMatrixToFile();//prints the colorCode matrix to the file
    
    //closes the PrintWriter objects
    colorCodes.close();//close the file
    intensity.close();//close the file
  }
  
  //intensity method
  public int[] getIntensity(BufferedImage image) {
	  int[] intensity = new int[26];
	  int index = indexOfImage(image);
	  if (index < 0) {
		  return intensity;
	  }
	  for (int i = 0; i < 26; i++) {
		  intensity[i] = intensityMatrix[index][i];
	  }
	  return intensity;
  }
  
  private static int indexOfImage(BufferedImage image) {
	  for (int i = 0; i < imagesArray.length; i++) {
		  if (image.toString().equals(imagesArray[i].toString())) {
			  return i;
		  }
	  }
	  return - 1;
  }
  
  public int[] getColorCode(BufferedImage image) {
	  int[] colorCode = new int[64];
	  int index = indexOfImage(image);
	  if (index >= 0) {
		  for (int i = 0; i < 64; i++) {
			  colorCode[i] = colorCodeMatrix[index][i];
		  }
	  }
	  return colorCode;
  }
  
  private static int[] colorToRGB(int color) {
	  int[] rgb = new int[3];
	  rgb[0] = (color & 0x00ff0000) >> 16;//red
	  rgb[1] = (color & 0x0000ff00) >> 8;//green
	  rgb[2] = color & 0x000000ff;//blue
	  return rgb;
  }
  
  private static void placeIntoIntensityMatrix(int[] rgb, int row) {
	  //Intensity = 0.299R + 0.587G + 0.114B
	  double intensity = ((double) rgb[0] * 0.299) + ((double) rgb[1] * 0.587) + ((double) rgb[2] * 0.114);
	  int binNumber = getBinNumber(intensity);
	  ++intensityMatrix[row][binNumber];
  }
  
  private static int getBinNumber(double intensity) {
	  int bin = 0;
	  for (int i = 0; i < 25; i++) {
		  bin++;
		  if (intensity < intensityBins[bin]) {
			  return (bin - 1);
		  }
	  }
	  return bin;
  }
  
  private static void printIntensityMatrixToFile() {
	  for (int i = 0; i < 100; i++) {
		  for (int j = 0; j < 26; j++) {
			  intensity.print(intensityMatrix[i][j] + " ");
		  }
		  intensity.println();
	  }
  }
  
  private static void placeIntoColorCodeMatrix(int[] rgb, int row) {
	  String firstTwoBits = Integer.toBinaryString(rgb[0]);//change to a binary
	  String secondTwoBits = Integer.toBinaryString(rgb[1]);//string representation
	  String thirdTwoBits = Integer.toBinaryString(rgb[2]);//so the bits can be added together.
	  String result = "";//empty string to start
	  if (firstTwoBits.length() > 7) {
		  result += firstTwoBits.substring(0, 2);//first two bits
	  } else {
		  if (firstTwoBits.length() > 6) {
			  result += firstTwoBits.substring(0, 1);//first bit
		  }
	  }
	  if (secondTwoBits.length() > 7) {
		  result += secondTwoBits.substring(0, 2);//first two bits
	  } else {
		  if (secondTwoBits.length() > 6) {
			  result += "0" + secondTwoBits.substring(0, 1);//0 plus first bit
		  }
		  else { result += "00"; }//zeros
	  }
	  if (thirdTwoBits.length() > 7) {
		  result += thirdTwoBits.substring(0, 2);//first two bits
	  } else {
		  if (thirdTwoBits.length() > 6) {
			  result += "0" + thirdTwoBits.substring(0, 1);//0 plus first bit
		  }
		  else { result += "00"; }//zeros
	  }
	  int sixBits = Integer.parseInt(result, 2);
	  ++colorCodeMatrix[row][sixBits];
  }
  
  private static void printColorCodeMatrixToFile() {
	  for (int i = 0; i < 100; i++) {
		  for (int j = 0; j < 64; j++) {
			  colorCodes.print(colorCodeMatrix[i][j] + " ");
		  }
		  colorCodes.println();
	  }
  }
  
  //This method writes the contents of the colorCode matrix to a file named colorCodes.txt.
  public void writeColorCode(BufferedImage image) {
	  if (image == null) {
		  throw new NullPointerException();
	  }
	  //TODO: Write the algorithm.
	  for (int i = 0; i < image.getHeight(); i++) {
		  for (int j = 0; j < image.getWidth(); j++) {
			  int color = image.getRGB(j, i);
			  int[] rgb = colorToRGB(color);
			  placeIntoColorCodeMatrix(rgb, imageCount - 2);
		  }
	  }
  }
  
  //This method writes the contents of the intensity matrix to a file called intensity.txt
  public void writeIntensity(BufferedImage image){
	  if (image == null) {
		  throw new NullPointerException();
	  }
	  for (int i = 0; i < image.getHeight(); i++) {
		  for (int j = 0; j < image.getWidth(); j++) {
			  int color = image.getRGB(j, i);
	  		  int[] rgb = colorToRGB(color);
	  		  placeIntoIntensityMatrix(rgb, imageCount - 2);
		  }
	  }
  }
  
  private void initializeIntensityBins() {
	  int value = 0;
	  for (int i = 0; i < 26; i++) {
		  intensityBins[i] = (double) value;
		  value += 10;
	  }
	  for (int i = 0; i < 100; i++) {
		  for (int j = 0; j < 26; j++) {
			  intensityMatrix[i][j] = 0;//zeros the intensity matrix
		  }
	  }
  }
  
  private void initializeColorCodeBins() {
	  int counter = 0b0;
	  for (int i = 0; i < 64; i++) {
		  colorCodeBins[i] = (double) counter;
		  counter += 1;
	  }
  }
  
  public static void main(String[] args)
  {
	  new readImage();
  }

}
