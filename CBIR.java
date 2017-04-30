/* Program 1 CSS 490 Multimedia Database Systems
 * @author Daniel Grimm
 * @since April 11th, 2017
*/

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.*;

public class CBIR extends JFrame {
    
	private static boolean imageSelected = false;
	private double[] CCPlusIntensity = new double[101];
	private double[] RF = new double[101];
	private static JCheckBox[] checkbox;
    private JLabel photographLabel = new JLabel();  //container to hold a large 
    private static JButton [] button; //creates an array of JButtons
    private static ImageIcon[] button2;//creates an array of ImageIcon objects
    private static int[] buttonOrder = new int [101]; //creates an array to keep up with the image order
    private static int[] imageSize = new int[101]; //keeps up with the image sizes
    private GridLayout gridLayout1;
    private GridLayout gridLayout2;
    private GridLayout gridLayout3;
    private GridLayout gridLayout4;
    private GroupLayout groupLayout;
    private static JPanel panelBottom1;
    private JPanel panelBottom2;
    private JPanel panelTop;
    private JPanel buttonPanel;
    private static int [][] intensityMatrix = new int [100][26];
    private static int [][] colorCodeMatrix = new int [100][64];
    private Map <Integer , LinkedList<Integer>> map;
    static int picNo = 0;
    static int imageCount = 1; //keeps up with the number of images displayed since the first page.
    int pageNo = 1;
    private static double[] distance = new double[100];
    private static double[] weight = new double[90];
    private static double[][] submatrix = new double[0][90];
    private static int submatrixSize = 0;
    
    JCheckBox relevanceFeedback = new JCheckBox("Relevance Feedback");
    JButton intensity = new JButton("Intensity");
    JButton colorCode = new JButton("Color Code");
    JButton previousPage = new JButton("Previous Page");
    JButton nextPage = new JButton("Next Page");
    JButton colorPlusIntensity = new JButton("Color-Code + Intensity");
    
    public static void main(String args[]) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CBIR app = new CBIR();
                app.setVisible(true);
            }
        });
    }
    
    
    
    public CBIR() {
      //The following lines set up the interface including the layout of the buttons and JPanels.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Program 2: Daniel Grimm");    
        checkbox = new JCheckBox[101];
        
        panelBottom1 = new JPanel();//JPanels
        panelBottom2 = new JPanel();
        panelTop = new JPanel();
        buttonPanel = new JPanel();
        gridLayout1 = new GridLayout(4, 5, 5, 5);
        gridLayout2 = new GridLayout(2, 1, 5, 5);
        gridLayout3 = new GridLayout(1, 2, 5, 5);
        gridLayout4 = new GridLayout(3, 2, 70, 10);
        groupLayout = new GroupLayout(panelBottom1);
        
        panelBottom1.setBackground(new Color(51, 0, 111));//Husky Purple
        panelBottom2.setBackground(new Color(51, 0, 111));
        panelTop.setBackground(new Color(51, 0, 111));
        buttonPanel.setBackground(new Color(51, 0, 111));
        
        setLayout(gridLayout2);
        panelBottom1.setLayout(gridLayout1);
        panelBottom2.setLayout(gridLayout1);
        panelTop.setLayout(gridLayout3);
        add(panelTop);
        add(panelBottom1);
        //TODO: make twenty panels of the height and width of the image so that each image displays properly.
        
        photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
        photographLabel.setHorizontalTextPosition(JLabel.CENTER);
        photographLabel.setHorizontalAlignment(JLabel.CENTER);
        photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setLayout(gridLayout4);
        panelTop.add(photographLabel);

        panelTop.add(buttonPanel);
        
        intensity.setEnabled(false);
        colorCode.setEnabled(false);
        previousPage.setEnabled(true);
        nextPage.setEnabled(true);
        colorPlusIntensity.setEnabled(false);
        
        Font buttonFont = new Font("SansSerif", Font.BOLD, 26);
        intensity.setFont(buttonFont);
        colorCode.setFont(buttonFont);
        previousPage.setFont(buttonFont);
        nextPage.setFont(buttonFont);
        colorPlusIntensity.setFont(buttonFont);
        
        relevanceFeedback.setEnabled(false);
        relevanceFeedback.setFont(buttonFont);
        
        buttonPanel.add(intensity);
        buttonPanel.add(colorCode);
        buttonPanel.add(nextPage);
        buttonPanel.add(colorPlusIntensity);
        buttonPanel.add(previousPage);
        buttonPanel.add(relevanceFeedback);
        
        nextPage.addActionListener(new nextPageHandler());
        previousPage.addActionListener(new previousPageHandler());
        intensity.addActionListener(new intensityHandler());
        colorCode.addActionListener(new colorCodeHandler());
        colorPlusIntensity.addActionListener(new CCPlusIntensity());
        
        setSize(1400, 1100);
        // this centers the frame on the screen
        setLocationRelativeTo(null);
        
        button = new JButton[101];
        button2 = new ImageIcon[101];
        
        for (int i = 0; i < 101; i++)
        {
        	JCheckBox relevant = new JCheckBox("Relevant");
        	relevant.setFont(new Font("Serif", Font.BOLD, 18));
            relevant.setEnabled(false);
        	checkbox[i] = relevant;
        	checkbox[i].addActionListener(new RF(i));
        	checkbox[i].setMnemonic(i);
        }
        
        for (int i = 0; i < weight.length; i++) {
        	weight[i] = 1.0/(double) weight.length;
        }
        
        /*This for loop goes through the images in the database and stores them as icons and adds
         * the images to JButtons and then to the JButton array
        */
        for (int i = 1; i < 101; i++) {
                ImageIcon icon;
                icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
                imageSize[i] = icon.getIconWidth() * icon.getIconHeight();
                
                 if(icon != null){
                    button[i] = new JButton(icon);
                    button[i].addActionListener(new IconButtonHandler(i, icon));
                    button[i].setSize(icon.getIconWidth(), icon.getIconHeight());
                    button2[i] = icon;
                    //button2[i].addActionListener(new IconButtonHandler(i, icon));
                    buttonOrder[i] = i;
                }
        }

        readIntensityFile();
        readColorCodeFile();
        displayFirstPage();
    }
    
    /*This method opens the intensity text file containing the intensity matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called intensityMatrix.
    */
    public void readIntensityFile(){
      Scanner read = null;
      String line = "";
      int lineNumber = 0;
         try{
           read = new Scanner(new File ("intensity.txt"));
           while (read.hasNextLine()) {
        	   line = read.nextLine();//grab each line
        	   Scanner readLine = new Scanner(line);
        	   int counter = 0;
        	   while (readLine.hasNext()) {
        		   String token = readLine.next();
        		   intensityMatrix[lineNumber][counter++] = Integer.parseInt(token);
        	   }
        	   readLine.close();
        	   lineNumber++;
           }
         }
         catch(FileNotFoundException EE){
           System.out.println("The file intensity.txt does not exist");
         }
      read.close();
    }
    
    /*This method opens the color code text file containing the color code matrix with the histogram bin values for each image.
     * The contents of the matrix are processed and stored in a two dimensional array called colorCodeMatrix.
    */
    private void readColorCodeFile(){
      Scanner read = null;
      String line = "";
      int lineNumber = 0;
         try{
           read = new Scanner(new File ("colorCodes.txt"));
           while (read.hasNextLine()) {
        	   line = read.nextLine();
        	   Scanner readLine = new Scanner(line);
        	   int counter = 0;
        	   while (readLine.hasNext()) {
        		   String token = readLine.next();
        		   colorCodeMatrix[lineNumber][counter++] = Integer.parseInt(token);
        	   }
        	   readLine.close();
        	   lineNumber++;
           }
         }
         catch(FileNotFoundException EE){
           System.out.println("The file colorCodes.txt does not exist");
         }
      read.close();
    }
    
    /*This method displays the first twenty images in the panelBottom.  The for loop starts at number one and gets the image
     * number stored in the buttonOrder array and assigns the value to imageButNo.  The button associated with the image is 
     * then added to panelBottom1.  The for loop continues this process until twenty images are displayed in the panelBottom1
    */
    private static void displayFirstPage(){
      int imageButNo = 0;
      panelBottom1.removeAll(); 
      for(int i = 1; i < 21; i++){
        imageButNo = buttonOrder[i];
        panelBottom1.add(button[imageButNo]);
        panelBottom1.add(checkbox[imageButNo]);
        imageCount++;
      }
      panelBottom1.revalidate();
      panelBottom1.repaint();

    }
    
    //bubble sort to sort by the colorCode
    protected static void sortDistance() {
    	//reset buttonOrder
    	for (int i = 0; i < checkbox.length; i++) {
    		for (int j = 0; j < checkbox.length - 1; j++) {
    			if (checkbox[j].getMnemonic() > checkbox[j + 1].getMnemonic()) {
    				JCheckBox tempo = checkbox[j];
    				checkbox[j] = checkbox[j + 1];
    				checkbox[j + 1] = tempo;
    			}
    		}
    	}
    	
    	for (int i = 1; i < 101; i++) {
    		buttonOrder[i] = i;
    	}
    	
    	//bubble sort of the values for distance
    	for (int i = 1; i <= 100; i++) {
    		for (int j = 1; j < 100; j++) {
    			if (distance[j - 1] > distance[j]) {
    				//swap button order
    				int temp = buttonOrder[j];
    				buttonOrder[j] = buttonOrder[j + 1];
    				buttonOrder[j + 1] = temp;
    				
    				//swap distance order
    				double temporary = distance[j - 1];
    				distance[j - 1] = distance[j];
    				distance[j] = temporary;
    				
    				JCheckBox tempo = checkbox[j];
    				checkbox[j] = checkbox[j + 1];
    				checkbox[j + 1] = tempo;
    			}
    		}
    	}
    }
    
    //enable buttons and relevance feedback.
    protected void enableButtons() {
    	relevanceFeedback.setEnabled(true);
    	intensity.setEnabled(true);
    	colorCode.setEnabled(true);
        previousPage.setEnabled(true);
        nextPage.setEnabled(true);
        colorPlusIntensity.setEnabled(true);
        relevanceFeedback.addActionListener(new relevanceFeedbackEnabled());
    }
    
    //redraw the bottom panel
    protected void refresh() {
    	panelBottom1.removeAll();
        int count = imageCount;
        int imageButNo = 0;
        for (int i = imageCount - 20; i < count; i++) {
      	  imageButNo = buttonOrder[i];
            panelBottom1.add(button[imageButNo]);
            panelBottom1.add(checkbox[i]);//TODO:Display this checkbox
        }
        panelBottom1.revalidate();
        panelBottom1.repaint();
    }
    
    private static double manhattanCC(int index) {
    	int testPicture = picNo - 1;
    	double distance = 0;
    	for (int i = 0; i < 64; i++) {
    		//Manhattan Distance Formula
    		double weightOne = colorCodeMatrix[index][i] / (double) imageSize[index + 1];
    		double weightTwo = colorCodeMatrix[testPicture][i] / (double) imageSize[picNo];
    		distance += Math.abs(weightOne - weightTwo);
    	}
    	return distance;
    }
    
    private static double manhattanIntensity(int index) {
    	int testPicture = picNo - 1;
    	double distance = 0;
    	for (int i = 0; i < 26; i++) {
    		//Manhattan Distance Formula
    		double weightOne = intensityMatrix[index][i] / (double) imageSize[index + 1];
    		double weightTwo = intensityMatrix[testPicture][i] / (double) imageSize[picNo];
    		distance += Math.abs(weightOne - weightTwo);
    	}
    	return distance;
    }
    
    private static double CCPlusIntensity(int index) {//TODO: multiply by the weight
    	return manhattanIntensity(index) + manhattanCC(index);
    }
    
    private static double average(double[] array) {
    	double sum = 0;
    	int counter = 0;
    	for (int i = 0; i < array.length; i++) {
    		sum += array[i];
    		counter++;
    	}
    	sum /= counter;
    	return sum;
    }
    
    private static double stdev(double[] array, double mean) {
    	double sum = 0;
    	int counter = 0;
    	for (int i = 0; i < array.length; i++) {
    		sum += ((array[i] - mean) * (array[i] - mean));
    		counter++;
    	}
    	sum /= counter;
    	
    	return Math.sqrt(sum);
    }
    
    private static void addToSubmatrix(int index) {
    	double[][] array = new double[submatrixSize][90];
    	for (int i = 0; i < submatrixSize - 1; i++) {
    		for (int j = 0; j < 90; j++) {
    			array[i][j] = submatrix[i][j];
    		}
    	}
    	
    	double distance = 0;
    	for (int i = 0; i < 26; i++) {
    		//Manhattan Distance Formula
    		double weightOne = intensityMatrix[index - 1][i] / (double) imageSize[index];
    		double weightTwo = intensityMatrix[picNo - 1][i] / (double) imageSize[picNo];
    		distance = Math.abs(weightOne - weightTwo);
    		array[submatrixSize - 1][i] = distance;
    	}
    	distance = 0;
    	for (int i = 0; i < 64; i++) {
    		//Manhattan Distance Formula
    		double weightOne = colorCodeMatrix[index - 1][i] / (double) imageSize[index];
    		double weightTwo = colorCodeMatrix[picNo - 1][i] / (double) imageSize[picNo];
    		distance = Math.abs(weightOne - weightTwo);
    		array[submatrixSize - 1][i + 26] = distance;
    	}
    	
    	submatrix = array;
    }
    
    private static void updateWeights() {
    	double sum = 0;
    	for (int i = 0; i < 90; i++) {
    		double[] array = new double[submatrixSize];
    		for (int j = 0; j < submatrixSize; j++) {
    			array[j] = submatrix[j][i];
    		}
    		double avg = average(array);
    		double stdv = stdev(array, avg);
    		if (stdv == 0) {
    			stdv = 1000000.0;
    		}
    		weight[i] = 1/stdv;
    		sum += weight[i];
    	}
    	for (int i = 0; i < 90; i++) {
    		double normalized = weight[i] / sum;
    		weight[i] = normalized;
    	}
    }
    
    private static void updateDistance() {//TODO: multiply the distance by the weight
    	
    }
    
    /*This class implements an ActionListener for each iconButton.  When an icon button is clicked, the image on the 
     * the button is added to the photographLabel and the picNo is set to the image number selected and being displayed.
    */ 
    private class IconButtonHandler implements ActionListener{
      int pNo = 0;
      ImageIcon iconUsed;
      
      IconButtonHandler(int i, ImageIcon j){
        pNo = i;
        iconUsed = j;  //sets the icon to the one used in the button
      }
      
      public void actionPerformed( ActionEvent e){
        photographLabel.setIcon(iconUsed);
        picNo = pNo;
        checkbox[picNo].setSelected(true);
        if (!imageSelected) {
        	enableButtons();
        }
        imageSelected = true;
      }
      
    }
    
    /*This class implements an ActionListener for the nextPageButton.  The last image number to be displayed is set to the 
     * current image count plus 20.  If the endImage number equals 101, then the next page button does not display any new 
     * images because there are only 100 images to be displayed.  The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount
    */
    private class nextPageHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
          int imageButNo = 0;
          int endImage = imageCount + 20;
          if(endImage <= 101){
            panelBottom1.removeAll(); 
            for (int i = imageCount; i < endImage; i++) {
                    imageButNo = buttonOrder[i];
                    panelBottom1.add(button[imageButNo]);
                    
                    panelBottom1.add(checkbox[i]);
                    
                    imageCount++;
          
            }
  
            panelBottom1.revalidate();  
            panelBottom1.repaint();
          }
      }
      
    }
    
    /*This class implements an ActionListener for the previousPageButton.  The last image number to be displayed is set to the 
     * current image count minus 40.  If the endImage number is less than 1, then the previous page button does not display any new 
     * images because the starting image is 1.  The first picture on the next page is the image located in 
     * the buttonOrder array at the imageCount
    */
    private class previousPageHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
          int imageButNo = 0;
          int startImage = imageCount - 40;
          int endImage = imageCount - 20;
          if(startImage >= 1){
            panelBottom1.removeAll();
            /*The for loop goes through the buttonOrder array starting with the startImage value
             * and retrieves the image at that place and then adds the button to the panelBottom1.
            */
            for (int i = startImage; i < endImage; i++) {
                    imageButNo = buttonOrder[i];
                    panelBottom1.add(button[imageButNo]);
                    panelBottom1.add(checkbox[i]);
                    imageCount--;
          
            }
  
            panelBottom1.revalidate();  
            panelBottom1.repaint();
          }
      }
      
    }
    
    //allows the checkboxes to be used for relevance feedback.
    private class relevanceFeedbackEnabled implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		for (int i = 1; i < checkbox.length; i++) {
    			checkbox[i].setEnabled(true);
    		}
    	}
    }
    
    
    /*This class implements an ActionListener when the user selects the intensityHandler button.  The image number that the
     * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
     * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one.
     * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
     * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
     * The images are then arranged from most similar to the least.
     */
    private class intensityHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
          for (int i = 0; i < 100; i++) {
        	  if (i == (picNo - 1)) {
        		  distance[i] = 0;
        		  continue;
        	  }
        	  distance[i] = manhattanIntensity(i);
          }
          
         sortDistance();
          refresh();//update gui
    }
  }
    
    /*This class implements an ActionListener when the user selects the colorCode button.  The image number that the
     * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
     * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one. 
     * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
     * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
     * The images are then arranged from most similar to the least.
     */ 
    private class colorCodeHandler implements ActionListener{

      public void actionPerformed( ActionEvent e){
              
    	  for (int i = 0; i < 100; i++) {//for one hundred photos
    		  if (i == (picNo - 1)) {
    			  distance[i] = 0;
    			continue;
    		  }
    		  distance[i] = manhattanCC(i);//get the Manhattan distance for color coding
    	  }
    	  sortDistance();
          refresh();
       }
    }
    
    private class CCPlusIntensity implements ActionListener {
    	
    	public void actionPerformed(ActionEvent e) {
    		for (int i = 0; i < 100; i++) {//for one hundred photos
      		  if (i == (picNo - 1)) {
      			  distance[i] = 0;
      			continue;
      		  }
      		  distance[i] = CCPlusIntensity(i);
      	    }
    		sortDistance();
    		refresh();
    	}
    }
    
    private class RF implements ActionListener {
    	
    	int index;
    	
    	public RF(int index) {
    		this.index = index;
    	}
    	
    	public void actionPerformed(ActionEvent e) {
    		/*Algorithm for RF
    		 * add the new image to the submatrix
    		 * Update weights
    		 * sortDistance();
    		 * */
    		
    		submatrixSize = 0;
    		for (int i = 1; i < checkbox.length; i++) {
    			if (checkbox[i].isSelected()) { submatrixSize++; }
    		}
    		
    		submatrix = new double[submatrixSize][90];
    		addToSubmatrix(index);
    		
    		updateWeights();
    		
    		updateDistance();//TODO: Fill this out
    		
    		for (int i = 0; i < 100; i++) {//for one hundred photos
        	  if (i == (picNo - 1)) {
        		 distance[i] = 0;
        		 continue;
        	  }
        	  distance[i] = CCPlusIntensity(i);
            }
    		
    		sortDistance();
    		refresh();
    	}
    }
}


