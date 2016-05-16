/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzycv;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;


/**
 *
 * @author Damilola
 */
public class MainFrame extends javax.swing.JFrame {
    static{
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    /**
     * Creates new form MainFrame
     */
  private Boolean begin = false;
  private Boolean firstFrame = true;
  private VideoCapture video = null;
  private CaptureThread thread = null;
  private MatOfByte matOfByte = new MatOfByte();
  private MatOfByte prevmatOfByte = new MatOfByte();
  private BufferedImage bufImage = null, prevbufImg = null;  
  private InputStream in;
  private InputStream previn;
  private Mat frameaux = new Mat();
  private Mat frame = new Mat(240, 320, CvType.CV_8UC3);
  private Mat lastFrame = new Mat(240, 320, CvType.CV_8UC3);
  private Mat currentFrame = new Mat(240, 320, CvType.CV_8UC3);
  private Mat previewframe = new Mat(240, 320, CvType.CV_8UC3);
  private Mat processedFrame = new Mat(240, 320, CvType.CV_8UC3);
  private Mat hsvImage = new Mat(240, 320, CvType.CV_8UC3);
  private static MainFrame mainFrame = null;
  private ScreenPanel image;
  private ScreenPanel preview;
  private FileWriter writer;
  private File selectedFile;
//    FCLloader fcl = new FCLloader();
//  FCLTest fcl = new FCLTest(this, true);
 
//  private BackgroundSubtractorMOG bsMOG = new BackgroundSubtractorMOG();
  private int savedelay = 0;
  String currentDir = "";
  String detectionsDir = "detected_999";
   double hueRange;
   double satRange;
   double valueRange;
   
     
    public MainFrame() {
        initComponents();
        AnalyzePanel.hide();
        image = new ScreenPanel(new ImageIcon("figs/320x240.gif").getImage());
        preview = new ScreenPanel(new ImageIcon("figs/320x240.gif").getImage());
        //mask = new ScreenPanel(new ImageIcon("figs/320x240.gif").getImage());
//        MaskPanel mask = new MaskPanel();
        //MainFrame mf = new MainFrame();
        jPanel1.add(image);
        prevPanel.add(preview);
        //maskPanel.add(mask);
//       <editor-fold defaultstate="collapsed" desc="test">
//        ImageIcon exitIcon = new ImageIcon(MainFrame.class.getResource("/resources/image/arrow.png"));
//            
//        ImageIcon analyzeIcon = new ImageIcon(MainFrame.class.getResource("/resources/image/monitor.png"));
//        
//        Action exitAction = new AbstractAction("Exit", exitIcon)
//        {
//            @Override
//            public void actionPerformed(ActionEvent e){
//                System.exit(0);
//            }
//        };
//        Action analyzeAction = new AbstractAction("Analyze", analyzeIcon) {
//            
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                FCLloader fcl = new FCLloader();                
//               fcl.setVisible(true);
//            }
//        };
//        
//        
//        JMenuItem exitMenuItem = new JMenuItem(exitAction);
//        exitMenuItem.setToolTipText("Click to exit");
//        
//        JMenuItem analyzeMenuItem = new JMenuItem(analyzeAction);
//        analyzeMenuItem.setToolTipText("Click to launch the fuzzy inference system");
//        
//        
//          JMenu fileMenu = new JMenu("File");
//          JMenu helpMenu = new JMenu("Help");
//          
//          fileMenu.add(analyzeMenuItem);
//          fileMenu.add(exitMenuItem);
//          
//        menuBar.add(fileMenu);
//        menuBar.add(helpMenu);
//        
//        toolBar.add(analyzeAction);
//        toolBar.add(exitAction);
        //jPanel1.add(menuBar);
     //</editor-fold>          
        
        currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
        detectionsDir = currentDir + File.separator + detectionsDir;
        SaveTextField.setText(detectionsDir);
        
    }
   
class CaptureThread extends Thread
{

        
        @Override
        public void run()
        {
            
            if(video.isOpened())
            {
                while(begin == true)
                {
                    video.retrieve(frameaux);
                   Imgproc.resize(frameaux, frame, frame.size());
                    frame.copyTo(currentFrame);
                     if(firstFrame)
                     {
                         frame.copyTo(lastFrame);
                         firstFrame = false;
                         continue;
                     }
                     if(SgmntCheckBox.isSelected()){
                         
                     int threshold = thresholdSlider.getValue();
                     jLabel3.setText(String.valueOf(threshold));
                     
                       // get thresholding values from the UI
                      // remember: H ranges 0-180, S and V range 0-255
                     Scalar minValues = new Scalar(lowhueSlider.getValue(),
                            losaturatioonSlider.getValue(), lovalueSlider.getValue());
                      Scalar maxValues = new Scalar(highhueSlider.getValue(),
                            highsaturationSlider.getValue(), highvalueSlider.getValue());
                   
                     Imgproc.GaussianBlur(currentFrame, currentFrame, new Size(3, 3), 0);
                     Imgproc.GaussianBlur(lastFrame, lastFrame, new Size(3, 3), 0);
                     
                     //processedFrame = docanny(processedFrame);
                    
                   
                        Imgproc.cvtColor(processedFrame, hsvImage, Imgproc.COLOR_BGR2HSV);
                        Imgproc.threshold(hsvImage, hsvImage, threshold, 255, Imgproc.THRESH_TOZERO_INV);
                     
 // <editor-fold defaultstate="collapsed" desc="test">                 
                     
                     
                    // Core.subtract(currentFrame, lastFrame, processedFrame);
//                     Imgproc.erode(processedFrame, processedFrame, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(5, 5)));
//                     Imgproc.dilate(processedFrame, processedFrame, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(5, 5)));
                    // 
//                     Imgproc.dilate(processedFrame, processedFrame, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(5, 5)));
//                     Imgproc.erode(processedFrame, processedFrame, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(5, 5)));
                      //                     Imgproc.adaptiveThreshold(processedFrame, processedFrame, 110, 255, threshold, threshold, WIDTH);
//                      
                    
                     
           //          </editor-fold>
//                      Mat blurredImage = new Mat();
                      
                      Mat mask = new Mat();
                      Mat morphOutput = new Mat();
                      
                     jLabel13.setText(String.valueOf(minValues.val[0])+"-"+String.valueOf(maxValues.val[0]));
                     jLabel14.setText(String.valueOf(minValues.val[1])+"-"+String.valueOf(maxValues.val[1]));
                     jLabel15.setText(String.valueOf(minValues.val[2])+"-"+String.valueOf(maxValues.val[2]));
                     
                     hueRange = maxValues.val[0] - minValues.val[0];
                     satRange  = maxValues.val[1] - minValues.val[1];
                     valueRange = maxValues.val[2] - minValues.val[2];
                   
                    Core.inRange(hsvImage, minValues, maxValues, mask);
                     
                    Mat dialateElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(24, 24));
                    Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(12, 12));
                    
                    Imgproc.erode(mask, morphOutput, erodeElement);
                    Imgproc.erode(mask, morphOutput, erodeElement);
                    
                    Imgproc.dilate(mask, morphOutput, dialateElement);
                    Imgproc.dilate(mask, morphOutput, dialateElement);
                    currentFrame = findAndDrawCrust(morphOutput, currentFrame);
                     
                    
                     }
                     else
                     {
                     
                     }
                    currentFrame.copyTo(processedFrame);
                    
                    currentFrame.copyTo(hsvImage);
                    Imgproc.cvtColor(hsvImage, hsvImage, Imgproc.COLOR_BGR2HSV);
                    
                    Highgui.imencode(".jpg", processedFrame, matOfByte);
                    Highgui.imencode(".jpg", hsvImage, prevmatOfByte);
                    
                    byte[] byteArray = matOfByte.toArray();
                    byte[] prevbyteArray = prevmatOfByte.toArray();
                    try
                    {
                        in = new ByteArrayInputStream(byteArray);
                        previn = new ByteArrayInputStream(prevbyteArray);
                        
                        bufImage = ImageIO.read(in);
                        prevbufImg = ImageIO.read(previn);
                     }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }

                        //image.updateImage(new ImageIcon("figs/lena.png").getImage());
                        image.updateImage(bufImage);
                        preview.updateImage(prevbufImg);
//                        mask.updateImage(bufImage);
                        
                        frame.copyTo(lastFrame);
                        

                    try
                    {
                        Thread.sleep(1);
                    }
                    catch(Exception ex)
                    {
                        System.err.print(ex.getMessage());
                    } 
                }
            }
        }
    }
   
    private String timeStamp()
    {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date dt = new Date();
        String dtime = simpleDate.format(dt);
        
        return dtime;
    }
    
    private Mat docanny(Mat frame){
     int treshValue = thresholdSlider.getValue();
    Mat  cvtImg = new Mat();
    Mat detectedEdges = new Mat();
    
    Imgproc.cvtColor(frame, cvtImg, Imgproc.COLOR_BGR2GRAY);
    Imgproc.blur(cvtImg, detectedEdges, new Size(3.0, 3.0));
    
    Imgproc.Canny(detectedEdges, detectedEdges, treshValue, treshValue*3, 3, false);
    
    Mat mask = new Mat();
    Core.add(mask, Scalar.all(0), mask);
    frame.copyTo(mask, detectedEdges);
    
    return mask;
    }
    
    private Mat removeBG(Mat frame){
    
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();
        
        //threshold the image with the histogram average value
        hsvImg.create(frame.size(), CvType.CV_8U);
        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);
        
        double threshValue = getHistoAvg(hsvImg, hsvPlanes.get(0));
        
        if (inverseCheckBox.isSelected())
        {
            Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY_INV);
        }
        else{
             Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);
        }
        
        Imgproc.blur(thresholdImg, thresholdImg, new Size(5,5));
        
        // dilate to fill gaps, erode to smooth edges
            Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, 1), 6);
            Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, 1), 6);

            Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);
    
            // create the new image
            Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
            frame.copyTo(foreground, thresholdImg);

            return foreground;
    }
    
    private Mat findAndDrawCrust(Mat maskedImage, Mat frame){
    
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            
            Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
            //if any contour exist...
            if(hierarchy.size().height > 0 && hierarchy.size().width > 0)
            {
                        //for each contour, display it in blue
                for(int idx = 0; idx >= 0; idx = (int)hierarchy.get(0, idx)[0])
                {
                    Imgproc.drawContours(frame, contours, idx, new Scalar(160, 0, 0));
                }
            }
            
            return frame;
    }
    
    /**
     * Get the average value of the histogram representing the image Hue
     * component
     *
     * @param hsvImg
     *            the current frame in HSV
     * @param hueValues
     *            the Hue component of the current frame
     * @return the average value
     */
    private double getHistoAvg(Mat hsvImg, Mat hueValues){
    double average = 0.0;
    Mat hist_hue = new Mat();
    MatOfInt histSize = new MatOfInt(180);
    List<Mat> hue = new ArrayList<>();
    hue.add(hueValues);
    
    //compute the histogram
    Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179), true);
            // get the average for each bin
            for (int h = 0; h < 180; h++)
            {
                    average += (hist_hue.get(h, 0)[0] * h);
            }
            return average = average/hsvImg.size().height/hsvImg.size().width;
    }
    private void start(){
        if(!begin)
        {
            int sourcenum = (int) sourceSpinner1.getValue();
            System.out.println("Opening source at: "+sourcenum);
            
            video = new VideoCapture(sourcenum);
            if(video.isOpened()){
                boolean wset = video.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 720);
                boolean hset = video.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 1020);
                thread = new CaptureThread();
                thread.start();
                begin = true;
                firstFrame = true;
            }
        }
    }
    
    private void stop()
    {
        if(begin)
        {
           try
           { 
               Thread.sleep(500); 
           } catch(Exception ex)
           {
               System.err.println(ex.getMessage());
           }
            video.release();
            begin = false;
        }        
    }
    private void readImageDetails(){
        BufferedReader reader = null;
        try {
            Dimension dim = new Dimension(500, 700);
            reader = new BufferedReader(new FileReader("Image_Details_B.txt"));
            
            JFrame parent = new JFrame("Image_Details_B.txt");
            
            JTextArea text = new JTextArea();
            text.setVisible(true);
                    
            JScrollPane scrollPane = new JScrollPane(text);
            
            parent.setResizable(true);
            parent.setPreferredSize(dim);
           
             
            parent.add(scrollPane);
            
            parent.pack();
            parent.setVisible(true);
            String line;
            while((line = reader.readLine()) != null)
            {
                text.read(reader, null);
            }  reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        homePanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lowhueSlider = new javax.swing.JSlider();
        jLabel8 = new javax.swing.JLabel();
        highhueSlider = new javax.swing.JSlider();
        jLabel9 = new javax.swing.JLabel();
        losaturatioonSlider = new javax.swing.JSlider();
        jLabel10 = new javax.swing.JLabel();
        highsaturationSlider = new javax.swing.JSlider();
        jLabel11 = new javax.swing.JLabel();
        lovalueSlider = new javax.swing.JSlider();
        jLabel12 = new javax.swing.JLabel();
        highvalueSlider = new javax.swing.JSlider();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        thresholdSlider = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        startCamBtn = new javax.swing.JButton();
        stopCamBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        SaveTextField = new javax.swing.JTextField();
        SgmntCheckBox = new javax.swing.JCheckBox();
        saveCheckBox = new javax.swing.JCheckBox();
        sourceSpinner1 = new javax.swing.JSpinner();
        inverseCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        prevPanel = new javax.swing.JPanel();
        jDesktopPane2 = new javax.swing.JDesktopPane();
        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton4 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        HomeButton = new javax.swing.JToggleButton();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        AnalyzePanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();
        loadBtn = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        hueLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        hueTextField = new javax.swing.JTextField();
        saturationTextField = new javax.swing.JTextField();
        valueTextField = new javax.swing.JTextField();
        valueLabel1 = new javax.swing.JLabel();
        valueTextField1 = new javax.swing.JTextField();
        calculateButton = new javax.swing.JButton();
        saturatioonLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        HomeMenu = new javax.swing.JMenuItem();
        analyzeMenu = new javax.swing.JMenuItem();
        readMenu = new javax.swing.JMenuItem();
        exitMenu = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Fuzzy Expert Computer Vision");
        setBackground(new java.awt.Color(204, 204, 255));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        homePanel.setBackground(new java.awt.Color(153, 153, 255));
        homePanel.setLayout(null);
        homePanel.add(jSeparator1);
        jSeparator1.setBounds(10, 467, 408, 2);

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(153, 153, 153), null, null), "Color Threshold", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel7.setText("Low Hue:");

        lowhueSlider.setBackground(new java.awt.Color(204, 204, 255));
        lowhueSlider.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        lowhueSlider.setMajorTickSpacing(20);
        lowhueSlider.setMaximum(179);
        lowhueSlider.setMinorTickSpacing(10);
        lowhueSlider.setValue(15);

        jLabel8.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel8.setText("High Hue:");

        highhueSlider.setBackground(new java.awt.Color(204, 204, 255));
        highhueSlider.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        highhueSlider.setMajorTickSpacing(20);
        highhueSlider.setMaximum(179);
        highhueSlider.setMinorTickSpacing(10);
        highhueSlider.setValue(99);

        jLabel9.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel9.setText("Low Saturation:");

        losaturatioonSlider.setBackground(new java.awt.Color(204, 204, 255));
        losaturatioonSlider.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        losaturatioonSlider.setMajorTickSpacing(50);
        losaturatioonSlider.setMaximum(255);
        losaturatioonSlider.setMinorTickSpacing(25);
        losaturatioonSlider.setValue(98);

        jLabel10.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel10.setText("High Saturation:");

        highsaturationSlider.setBackground(new java.awt.Color(204, 204, 255));
        highsaturationSlider.setMajorTickSpacing(50);
        highsaturationSlider.setMaximum(255);
        highsaturationSlider.setMinorTickSpacing(25);
        highsaturationSlider.setValue(200);

        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel11.setText("Low Value:");

        lovalueSlider.setBackground(new java.awt.Color(204, 204, 255));
        lovalueSlider.setMajorTickSpacing(50);
        lovalueSlider.setMaximum(255);
        lovalueSlider.setMinorTickSpacing(25);
        lovalueSlider.setValue(98);

        jLabel12.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel12.setText("High Value:");

        highvalueSlider.setBackground(new java.awt.Color(204, 204, 255));
        highvalueSlider.setForeground(new java.awt.Color(255, 255, 255));
        highvalueSlider.setMajorTickSpacing(50);
        highvalueSlider.setMaximum(255);
        highvalueSlider.setMinorTickSpacing(25);
        highvalueSlider.setValue(189);

        thresholdSlider.setBackground(new java.awt.Color(204, 204, 255));
        thresholdSlider.setFont(new java.awt.Font("Trebuchet MS", 0, 10)); // NOI18N
        thresholdSlider.setMajorTickSpacing(50);
        thresholdSlider.setMaximum(255);
        thresholdSlider.setMinorTickSpacing(25);
        thresholdSlider.setToolTipText("");
        thresholdSlider.setValue(150);

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel2.setText("Threshold:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(highsaturationSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(losaturatioonSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lowhueSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                    .addComponent(highhueSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(12, 14, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(thresholdSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                                .addComponent(lovalueSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(highvalueSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(15, 15, 15))
                    .addComponent(lowhueSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(highhueSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(losaturatioonSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(highsaturationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lovalueSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(highvalueSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thresholdSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                        .addGap(2, 2, 2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        homePanel.add(jPanel3);
        jPanel3.setBounds(13, 139, 405, 322);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        homePanel.add(jSeparator5);
        jSeparator5.setBounds(424, 11, 2, 458);
        homePanel.add(jLabel15);
        jLabel15.setBounds(340, 475, 78, 14);

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel6.setText("Val. Range:");
        homePanel.add(jLabel6);
        jLabel6.setBounds(278, 475, 56, 14);

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel5.setText("Sat. Range:");
        homePanel.add(jLabel5);
        jLabel5.setBounds(144, 475, 57, 14);
        homePanel.add(jLabel14);
        jLabel14.setBounds(207, 475, 65, 14);
        homePanel.add(jLabel13);
        jLabel13.setBounds(72, 475, 66, 14);

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        jLabel4.setText("Hue Range:");
        homePanel.add(jLabel4);
        jLabel4.setBounds(10, 475, 56, 14);
        homePanel.add(jSeparator6);
        jSeparator6.setBounds(10, 124, 408, 4);

        startCamBtn.setBackground(new java.awt.Color(102, 102, 255));
        startCamBtn.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        startCamBtn.setText("Start Camera");
        startCamBtn.setBorderPainted(false);
        startCamBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startCamBtnActionPerformed(evt);
            }
        });
        homePanel.add(startCamBtn);
        startCamBtn.setBounds(10, 495, 103, 32);

        stopCamBtn.setBackground(new java.awt.Color(204, 0, 0));
        stopCamBtn.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        stopCamBtn.setText("Stop Camera");
        stopCamBtn.setToolTipText("Stop camera first before exiting this will prevent the thread exception");
        stopCamBtn.setBorderPainted(false);
        stopCamBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        stopCamBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopCamBtnActionPerformed(evt);
            }
        });
        homePanel.add(stopCamBtn);
        stopCamBtn.setBounds(315, 495, 103, 32);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(153, 153, 153), null, null), "Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        SaveTextField.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

        SgmntCheckBox.setBackground(new java.awt.Color(204, 204, 255));
        SgmntCheckBox.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        SgmntCheckBox.setText("Segment");
        SgmntCheckBox.setToolTipText("check to start image segmentation this will let colour thresholding apply");

        saveCheckBox.setBackground(new java.awt.Color(204, 204, 255));
        saveCheckBox.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        saveCheckBox.setText("Save in:");
        saveCheckBox.setToolTipText("Check to save the procesed image");

        sourceSpinner1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        sourceSpinner1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1, 1));
        sourceSpinner1.setToolTipText("Camera source: 0 for webcam, 1 for external camera");
        sourceSpinner1.setOpaque(false);

        inverseCheckBox.setBackground(new java.awt.Color(204, 204, 255));
        inverseCheckBox.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        inverseCheckBox.setText("Use Inverse Threshold");
        inverseCheckBox.setToolTipText("Check to use the inverse threshold technique");

        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel1.setText("Source:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(saveCheckBox)
                        .addGap(10, 10, 10)
                        .addComponent(SaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sourceSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(SgmntCheckBox)
                    .addComponent(inverseCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(SgmntCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inverseCheckBox)
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(SaveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(sourceSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(saveCheckBox))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        homePanel.add(jPanel2);
        jPanel2.setBounds(10, 11, 408, 107);

        jPanel4.setBackground(new java.awt.Color(204, 204, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 255), 1, true), "Current Image", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 12))); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(320, 240));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        homePanel.add(jPanel4);
        jPanel4.setBounds(436, 11, 330, 262);

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 255), 1, true), "Processed Image", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        prevPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout prevPanelLayout = new javax.swing.GroupLayout(prevPanel);
        prevPanel.setLayout(prevPanelLayout);
        prevPanelLayout.setHorizontalGroup(
            prevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );
        prevPanelLayout.setVerticalGroup(
            prevPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 227, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(prevPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(prevPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        homePanel.add(jPanel5);
        jPanel5.setBounds(436, 279, 330, 248);

        getContentPane().add(homePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 780, 540));

        jDesktopPane2.setFocusable(false);

        jToggleButton1.setBackground(new java.awt.Color(51, 153, 255));
        buttonGroup1.add(jToggleButton1);
        jToggleButton1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/stocks.png"))); // NOI18N
        jToggleButton1.setText("Analyze");
        jToggleButton1.setToolTipText("Click to launch the fuzzy inference engine");
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jToggleButton4.setBackground(new java.awt.Color(204, 204, 204));
        buttonGroup1.add(jToggleButton4);
        jToggleButton4.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jToggleButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/read.png"))); // NOI18N
        jToggleButton4.setText("Read Data");
        jToggleButton4.setToolTipText("Click to read Acquired image data");
        jToggleButton4.setBorderPainted(false);
        jToggleButton4.setContentAreaFilled(false);
        jToggleButton4.setFocusPainted(false);
        jToggleButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton4.setMaximumSize(new java.awt.Dimension(105, 37));
        jToggleButton4.setNextFocusableComponent(this);
        jToggleButton4.setPreferredSize(new java.awt.Dimension(105, 37));
        jToggleButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton4ActionPerformed(evt);
            }
        });

        jToggleButton3.setBackground(new java.awt.Color(51, 153, 255));
        buttonGroup1.add(jToggleButton3);
        jToggleButton3.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jToggleButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/help.png"))); // NOI18N
        jToggleButton3.setText("About");
        jToggleButton3.setToolTipText("About FECVS");
        jToggleButton3.setBorderPainted(false);
        jToggleButton3.setContentAreaFilled(false);
        jToggleButton3.setFocusPainted(false);
        jToggleButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton3.setMaximumSize(new java.awt.Dimension(105, 37));
        jToggleButton3.setNextFocusableComponent(this);
        jToggleButton3.setPreferredSize(new java.awt.Dimension(105, 37));
        jToggleButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jToggleButton2.setBackground(new java.awt.Color(51, 153, 255));
        buttonGroup1.add(jToggleButton2);
        jToggleButton2.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jToggleButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/exit.png"))); // NOI18N
        jToggleButton2.setText("Exit");
        jToggleButton2.setToolTipText("Exit");
        jToggleButton2.setBorderPainted(false);
        jToggleButton2.setContentAreaFilled(false);
        jToggleButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton2.setMaximumSize(new java.awt.Dimension(105, 37));
        jToggleButton2.setPreferredSize(new java.awt.Dimension(105, 37));
        jToggleButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        HomeButton.setBackground(new java.awt.Color(51, 153, 255));
        buttonGroup1.add(HomeButton);
        HomeButton.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        HomeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/home.png"))); // NOI18N
        HomeButton.setSelected(true);
        HomeButton.setText("Home");
        HomeButton.setToolTipText("");
        HomeButton.setBorderPainted(false);
        HomeButton.setContentAreaFilled(false);
        HomeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        HomeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        HomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDesktopPane2Layout = new javax.swing.GroupLayout(jDesktopPane2);
        jDesktopPane2.setLayout(jDesktopPane2Layout);
        jDesktopPane2Layout.setHorizontalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(HomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(362, Short.MAX_VALUE))
        );
        jDesktopPane2Layout.setVerticalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane2Layout.createSequentialGroup()
                .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jToggleButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                        .addComponent(jToggleButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToggleButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDesktopPane2.setLayer(jToggleButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jToggleButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jToggleButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jToggleButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(HomeButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        getContentPane().add(jDesktopPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 780, 60));

        jDesktopPane1.setFocusable(false);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 780, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        getContentPane().add(jDesktopPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 600, -1, 40));

        AnalyzePanel.setBackground(new java.awt.Color(204, 204, 255));
        AnalyzePanel.setLayout(null);

        jPanel8.setBackground(new java.awt.Color(153, 153, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "FCL loader", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 0, 12), new java.awt.Color(0, 102, 102))); // NOI18N

        jLabel16.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jLabel16.setText("Load input data:");

        fileNameLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N

        loadBtn.setBackground(new java.awt.Color(0, 102, 102));
        loadBtn.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        loadBtn.setText("Load");
        loadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(fileNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addGap(29, 29, 29)
                .addComponent(loadBtn)
                .addGap(23, 23, 23))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        AnalyzePanel.add(jPanel8);
        jPanel8.setBounds(120, 80, 500, 80);

        jPanel9.setBackground(new java.awt.Color(153, 153, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 102, 102))); // NOI18N

        hueLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        hueLabel.setText("Hue");

        valueLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        valueLabel.setText("Value");

        hueTextField.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N

        saturationTextField.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N

        valueTextField.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N

        valueLabel1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        valueLabel1.setText("Volume");

        valueTextField1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N

        calculateButton.setBackground(new java.awt.Color(0, 153, 153));
        calculateButton.setText("Calculate");
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        saturatioonLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        saturatioonLabel.setText("Saturation");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(calculateButton)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hueLabel)
                                    .addComponent(valueLabel)
                                    .addComponent(valueLabel1))
                                .addGap(35, 35, 35))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                .addComponent(saturatioonLabel)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(valueTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(valueTextField)
                            .addComponent(saturationTextField)
                            .addComponent(hueTextField))))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hueLabel)
                    .addComponent(hueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saturationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saturatioonLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valueLabel)
                    .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valueTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valueLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        AnalyzePanel.add(jPanel9);
        jPanel9.setBounds(120, 230, 500, 260);

        getContentPane().add(AnalyzePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 620));

        menuBar.setBorder(null);
        menuBar.setMaximumSize(new java.awt.Dimension(30, 30));
        menuBar.setMinimumSize(new java.awt.Dimension(30, 30));
        menuBar.setName(""); // NOI18N
        menuBar.setPreferredSize(new java.awt.Dimension(30, 30));

        fileMenu.setText("File");

        HomeMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        HomeMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/home.png"))); // NOI18N
        HomeMenu.setText("Home");
        fileMenu.add(HomeMenu);

        analyzeMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        analyzeMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/stocks.png"))); // NOI18N
        analyzeMenu.setText("Analyze");
        analyzeMenu.setToolTipText("Click to launch the fuzzy inference engine");
        analyzeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeMenuActionPerformed(evt);
            }
        });
        fileMenu.add(analyzeMenu);

        readMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        readMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/read.png"))); // NOI18N
        readMenu.setText("Read Data");
        readMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readMenuActionPerformed(evt);
            }
        });
        fileMenu.add(readMenu);

        exitMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/exit.png"))); // NOI18N
        exitMenu.setText("Exit");
        exitMenu.setToolTipText("Click to exit");
        exitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenu);

        menuBar.add(fileMenu);

        helpMenu.setText("Help");

        aboutMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_MASK));
        aboutMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/help.png"))); // NOI18N
        aboutMenu.setText("About");
        aboutMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenu);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aboutMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_aboutMenuActionPerformed

    private void exitMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuActionPerformed
        // TODO add your handling code here:
        int rep = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.OK_CANCEL_OPTION);
        if(rep == 0)
        {
            stop();
            System.exit(rep);
        }
    }//GEN-LAST:event_exitMenuActionPerformed

    private void readMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readMenuActionPerformed
        // TODO add your handling code here:
        readImageDetails();
    }//GEN-LAST:event_readMenuActionPerformed

    private void analyzeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeMenuActionPerformed
        // TODO add your handling code here:

        AnalyzePanel.show();
        homePanel.hide();

    }//GEN-LAST:event_analyzeMenuActionPerformed

    private void HomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeButtonActionPerformed
        // TODO add your handling code here:
        homePanel.show();
        AnalyzePanel.hide();
    }//GEN-LAST:event_HomeButtonActionPerformed

    private void jToggleButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton4ActionPerformed
        // TODO add your handling code here:
        readImageDetails();
    }//GEN-LAST:event_jToggleButton4ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        AnalyzePanel.show();
        homePanel.hide();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        int rep = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.OK_CANCEL_OPTION);
        if(rep == 0)
        {
            stop();
            System.exit(rep);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateButtonActionPerformed
        // TODO add your handling code here:
        String filename = selectedFile.getName();
        FIS fis = FIS.load(filename, true);

        if(fis == null)
        {
            JOptionPane.showMessageDialog(rootPane, "Open operation cancelled. File not loaded", "Cancelled", JOptionPane.ERROR_MESSAGE);

        }

        FunctionBlock fb = fis.getFunctionBlock(null);
        //double val1 = hueLabel.getText();
        fb.setVariable(hueLabel.getText(), Double.parseDouble(hueTextField.getText()));
        fb.setVariable(saturatioonLabel.getText(), Double.parseDouble(saturationTextField.getText()));

        fb.evaluate();

        Variable result = fb.getVariable("Force");
        double output = fb.getVariable("Force").defuzzify();

        JOptionPane.showMessageDialog(rootPane, "Force: "+output, "Defuzzification", JOptionPane.INFORMATION_MESSAGE);
        JFuzzyChart.get().chart(result, result.getDefuzzifier(), true);
    }//GEN-LAST:event_calculateButtonActionPerformed

    private void loadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBtnActionPerformed
        // TODO add your handling code here:
        if(loadBtn.getText().equals("Load")){
            JFileChooser FCLchooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("FCL Files", "fcl");
            FCLchooser.setFileFilter(filter);
            FCLchooser.setMultiSelectionEnabled(false);
            //       FCLchooser.setCurrentDirectory(System.getProperty("rules");

                //        String filename = FCLchooser.getSelectedFile().getName();
                int retrunVal = FCLchooser.showOpenDialog(this);
                if(retrunVal == JFileChooser.APPROVE_OPTION)
                {
                    selectedFile = FCLchooser.getSelectedFile();
                    fileNameLabel.setText(selectedFile.getName());

                }
                else
                {
                    JOptionPane.showMessageDialog(rootPane, "Open operation cancelled by user .", "Cancelled", JOptionPane.CANCEL_OPTION);
                }

            }
    }//GEN-LAST:event_loadBtnActionPerformed

    private void stopCamBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopCamBtnActionPerformed
        stop();

        if(startCamBtn.getText().equals("Acquire Image"))
        {
            startCamBtn.setText("Start Camera");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_stopCamBtnActionPerformed

    private void startCamBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCamBtnActionPerformed
        String location = SaveTextField.getText()+File.separator+"capture_"+timeStamp()+".jpg";
        if(startCamBtn.getText().equals("Acquire Image"))
        {
            if(saveCheckBox.isSelected())
            {
                if(savedelay == 0)
                {
                    try {
                                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                                     Date dt = new Date();
                                        String dtime = simpleDate.format(dt);
                        String filename = "capture_"+timeStamp()+".jpg";
                        System.out.println("Saving results in: "+location);
                        Highgui.imwrite(location, hsvImage);
                        //Highgui.imwrite(location, currentFrame);
                        System.out.println(hueRange+"\n"+satRange+"\n"+valueRange);
                        savedelay = 0;

                        writer = new FileWriter("Image_Details"+dtime+".txt", true);
                        try (BufferedWriter bwriter = new BufferedWriter(writer)) {
                            bwriter.write("\n"+filename);
                            bwriter.write("\n***************************************************************");
                            bwriter.write("\n Details: \n");
                            bwriter.write("Hue Range: "+jLabel13.getText()+"\n Resulting Hue: "+hueRange+"\n");
                            bwriter.write("Saturation Range: "+jLabel14.getText()+"\n Resulting Saturation: "+satRange+"\n");
                            bwriter.write("Value Range: "+jLabel15.getText()+"\n Resulting Value: "+valueRange+"\n");
                            bwriter.write("***************************************************************");
                            bwriter.flush();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    JOptionPane.showMessageDialog(this, "Image Acquired! Saved in: "+location, "Saving", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    //savedelay +=1;
                }
            }
            else{
                savedelay = 0;
            }
        }
        else{
            start();
            startCamBtn.setText("Acquire Image");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_startCamBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AnalyzePanel;
    private javax.swing.JToggleButton HomeButton;
    private javax.swing.JMenuItem HomeMenu;
    private javax.swing.JTextField SaveTextField;
    private javax.swing.JCheckBox SgmntCheckBox;
    private javax.swing.JMenuItem aboutMenu;
    private javax.swing.JMenuItem analyzeMenu;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton calculateButton;
    private javax.swing.JMenuItem exitMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JSlider highhueSlider;
    private javax.swing.JSlider highsaturationSlider;
    private javax.swing.JSlider highvalueSlider;
    private javax.swing.JPanel homePanel;
    private javax.swing.JLabel hueLabel;
    private javax.swing.JTextField hueTextField;
    private javax.swing.JCheckBox inverseCheckBox;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JDesktopPane jDesktopPane2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JButton loadBtn;
    private javax.swing.JSlider losaturatioonSlider;
    private javax.swing.JSlider lovalueSlider;
    private javax.swing.JSlider lowhueSlider;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel prevPanel;
    private javax.swing.JMenuItem readMenu;
    private javax.swing.JTextField saturationTextField;
    private javax.swing.JLabel saturatioonLabel;
    private javax.swing.JCheckBox saveCheckBox;
    private javax.swing.JSpinner sourceSpinner1;
    private javax.swing.JButton startCamBtn;
    private javax.swing.JButton stopCamBtn;
    private javax.swing.JSlider thresholdSlider;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JLabel valueLabel1;
    private javax.swing.JTextField valueTextField;
    private javax.swing.JTextField valueTextField1;
    // End of variables declaration//GEN-END:variables

    
}
