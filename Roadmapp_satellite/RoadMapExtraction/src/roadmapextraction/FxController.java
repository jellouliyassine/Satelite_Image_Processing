/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package roadmapextraction;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import Thining.Thinning;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * FXML Controller class
 *
 * @author Fadio
 */
public class FxController implements Initializable {
    public Button upload_button;
    public Button grayScale_button;
    public Button threshold_button;
    public Button cluster_button;
    public Button show_elongated_button;
    public Button thining_button;
    public Button show_Road_NonRoad_clusters_button;
    public Button test;

    public BufferedImage source_buff;
    public BufferedImage source_buff_COPY;
    public BufferedImage grayScale_buff;
    public BufferedImage threshold_buff;
    public BufferedImage processed_buff;
    public BufferedImage road_elongated_buff;
    public TextField threshold_tf;
    public BufferedImage clustered_image;
    public BufferedImage clustered_image_With_Boxes;
    public BufferedImage thinImage_buff;

    boolean upload_bool,grayscale_bool,threshold_bool,cluster_bool,elongated_bool,thinning_bool,finalo_bool,box_bool;
    public File sourcePath;
    public ImageView source_iv;
    public ImageView processed_iv;

    public TextField info_tf;
    PixelsData[] pixData;

    private ArrayList<Clusters> singleCluster_unique;
    ArrayList<Integer> No_U_L;
    ArrayList<Clusters> singleCluster_new;

    int h,w;

    //global instance
    DensityBasedClustering dbscan;

    //Upload Image
    public void setUpload_button(){
        upload_bool = grayscale_bool =box_bool= threshold_bool = cluster_bool = elongated_bool = thinning_bool = finalo_bool = false;
        System.out.println("Started upload..");
        threshold_tf.setText("110");
        String userDir = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        File dirFile = new File(userDir+ File.separator+"");

        fileChooser.setInitialDirectory(dirFile);
        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Image files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);
        // Set extension filter
        FileChooser.ExtensionFilter extFilter2 =
                new FileChooser.ExtensionFilter("Image files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter2);

        // Set extension filter
        FileChooser.ExtensionFilter extFilter3 =
                new FileChooser.ExtensionFilter("Image files (*.jpeg)", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter3);

        //path directrory
        sourcePath = fileChooser.showOpenDialog(null);

        //read image and store it in IMAGE type
        Image image = new Image(sourcePath.toURI().toString());
        source_buff = SwingFXUtils.fromFXImage(image,null);
        //source_buff = SWTFXUtils.fromFXImage(image)
        source_iv.setImage(image);

        info_tf.setText("Image Upload");
        upload_bool = true;
    }

    //Black and white conversion
    public void setGrayScale_button()
    {
        //button check
        if (!upload_bool){
            info_tf.setText("Image not uploaded");
            return;
        }
        grayscale_bool = true;

        // get image's width and height
        info_tf.setText("Converting to grayscale...");
        grayScale_buff = null;
        int width = source_buff.getWidth();
        int height = source_buff.getHeight();
       
        try {
            grayScale_buff = ImageIO.read(sourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // convert to greyscale
        int counter = 0;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // Here (x,y)denotes the coordinate of image
                // for modifying the pixel value.
                int p = grayScale_buff.getRGB(x,y);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                // calculate average
                int avg = (r+g+b)/3;

                // replace RGB value with avg
                p = (a<<24) | (avg<<16) | (avg<<8) | avg;

                grayScale_buff.setRGB(x, y, p);
            }
        }
        

        Image i = SwingFXUtils.toFXImage(grayScale_buff,null);
        processed_iv.setImage(i);

        info_tf.setText("Converted to Grayscale");
    }
    
  
    //Threshold
    public void setThreshold_button()
    {
        //button check
        if (!grayscale_bool){
            info_tf.setText("Image not converted to grayscale..");
            return;
        }
        threshold_bool = true;

        //grayScale_buff
        info_tf.setText("Thresholding the Image..");
        BufferedImage binarized = new BufferedImage(grayScale_buff.getWidth(), grayScale_buff.getHeight(),BufferedImage.TYPE_BYTE_BINARY);

        int red;
        int newPixel;

        int threshold = Integer.parseInt(threshold_tf.getText());

        for(int i=0; i<grayScale_buff.getWidth(); i++)
        {
            for(int j=0; j<grayScale_buff.getHeight(); j++)
            {
                red = new Color(grayScale_buff.getRGB(i, j)).getRed();
                int g = new Color(grayScale_buff.getRGB(i, j)).getGreen();
                int b = new Color(grayScale_buff.getRGB(i, j)).getBlue();
               
                int alpha = new Color(grayScale_buff.getRGB(i, j)).getAlpha();

                red = (red + g+b)/3;

                newPixel = colorToRGB(alpha, red, g, b);

                if(red > threshold)
                {               
                    newPixel = 255;
                }
                else
                {
                    newPixel = 0;
                     
                }
                newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);

                binarized.setRGB(i, j, newPixel);
            }
        }
        Image i = SwingFXUtils.toFXImage(binarized,null);
        //threshold_buff = SwingFXUtils.fromFXImage(i,null);
        threshold_buff = binarized;
        processed_iv.setImage(i);

        File output_file = new File("threshold.png");
        try{
            ImageIO.write(threshold_buff,"png",output_file);
        }catch (IOException e){
            System.out.println("Error "+e);
        }

        info_tf.setText("Image thresholded");
    }

    //Clustering
    public void clusterImage()
    {
        //button check
        if (!threshold_bool){
            info_tf.setText("Image not thresholded");
            return;
        }
        cluster_bool = true;

        source_buff_COPY = imageClone(source_buff);
        singleCluster_new = null;
        singleCluster_unique = null;
        No_U_L = null;
        dbscan = null;
        pixData = null;

        info_tf.setText("Clustering the image...");
        h = threshold_buff.getHeight();
        w = threshold_buff.getWidth();
        pixData = new PixelsData[h*w];

        dbscan = new DensityBasedClustering(w,h,threshold_buff,source_buff);//instance

        pixData = dbscan.cluster(20);// cluster with minimum 50 noise-size

        singleCluster_unique = dbscan.getClusters();//getting unique singleClusters filtered
        singleCluster_new= dbscan.getClusters();

        No_U_L = dbscan.getUniqueClusterList();
        dbscan.singleCluster = null;

        createSampleImage();

        find_ElongatedShapes();

        info_tf.setText("Image is clustered");
    }

    //Showing Roads and Non-Road Clusters boxes
//    public void show_road_AND_nonRoad_clusters()
//    {
//        //button check
//        if (!cluster_bool){
//            info_tf.setText("Image not Clustered");
//            return;
//        }
//        box_bool = true;
//
//
//        System.out.println("Showing road and non-road Clusters");
//        info_tf.setText("Yellow Boxes are Road Clusters & Red Boxes are Non-Road Clusters");
//
//        Image i = SwingFXUtils.toFXImage(clustered_image_With_Boxes,null);
//        processed_iv.setImage(i);
//    }
    //Finding Elongated cluster
    public void find_ElongatedShapes()
    {
        clustered_image_With_Boxes = clustered_image;

        //Calculating width height ratios
        System.out.println("Finding Elongated Shape\nwidth = "+w+",height="+h);

        //singleCluster_new= dbscan.getClusters();

        for (int i=0; i<singleCluster_new.size();i++){

            //We will not check for the elongated shape for noise pixels
            if(singleCluster_new.get(i).currentLabel != -1) {

                ArrayList<Integer> indexes = singleCluster_new.get(i).indexes;

                //finding minimum and maximum x,y coordinates for that particular index
                int max_x,max_y,min_x,min_y;
                max_x = max_y = 0;

                //initialize min 1st
                int p1 = indexes.get(0);
                min_x = p1/w;
                min_y = p1 %w;

                for (int j = 0; j < indexes.size() - 1; j++) {

                    int p = indexes.get(j);

                    //int remainder = p %w;
                    //int quotient = p/w;

                    //int x = quotient;
                    //int y = remainder;
                    int x = p/w; //x - coordinate for that pixel
                    int y = p % w;//y - coordinate for that pixel

                    if (x>max_x)
                        max_x = x;
                    if (y>max_y)
                        max_y = y;

                    if (x<min_x)
                        min_x = x;
                    if (y<min_y)
                        min_y = y;

                    //System.out.println(y+","+x);
                    /*if(isGray(p))
                        bufferedImage.setRGB(y,x,myYellow.getRGB());*/

                }

                //min max x,y coordinates
                int width = max_y - min_y;
                int height = max_x - min_x;

                singleCluster_new.get(i).width = width;
                singleCluster_new.get(i).height = height;

                singleCluster_new.get(i).min_X = min_x;
                singleCluster_new.get(i).max_X = max_x;

                singleCluster_new.get(i).max_Y = max_y;
                singleCluster_new.get(i).max_Y = max_y;


                int big,small;
                if(width > height){
                    big = width;
                    small = height;
                }else{
                    big = height;
                    small = width;
                }              
                if(small!=0) {
                    float ratio = big / small;
                    if(ratio>2.5)
                    {
                        singleCluster_new.get(i).isRoad = true;
                    }
                    else
                    {
                        double total_cluster_area = big * small;

                        double pix_area = indexes.size();
                        double percentage_pixArea = (pix_area / total_cluster_area)*100;
                        //System.out.println("pix_area="+pix_area+", w="+width+"h="+height+", total="+total_cluster_area+" ,%="+percentage_pixArea);

                        if (percentage_pixArea < 35) {
                            //isroad
                            singleCluster_new.get(i).isRoad = true;
                        } else {
                            //not road
                        }
                    }
                }

                //For Cluster Boundaries or BOXES
                surround_clusterBOX(width,height,min_x,min_y,max_x,max_y,singleCluster_new.get(i).isRoad);
            }
        }

    }
    //Showing Elongated Shape button
    public void setShow_elongated_button()
    {
        //button check
        if (!cluster_bool){
            info_tf.setText("Image not clustered");
            return;
        }
        elongated_bool = true;


        road_elongated_buff = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

        Color myWhite = new Color(255, 255, 255); // Color white
        Color myYellow = new Color(255, 255, 0); // Color yellow

        for (int i = 0; i < singleCluster_new.size(); i++)
        {
            if (singleCluster_new.get(i).isRoad == true)
            {
                ArrayList<Integer> indexes = singleCluster_new.get(i).indexes;

                for (int j = 0; j <indexes.size() ; j++)
                {
                    int p = indexes.get(j);

                    int remainder = p %w;
                    int quotient = p/w;

                    int x = quotient;
                    int y = remainder;

                    if(isGray(p)) {
                        road_elongated_buff.setRGB(y, x, myYellow.getRGB());
                        source_buff_COPY.setRGB(y,x,myYellow.getRGB());
                    }
                }
            }

        }

        Image i = SwingFXUtils.toFXImage(road_elongated_buff, null);
        processed_iv.setImage(i);
        //Writing Image
        try
        {
            File output_file = new File("result_show_Road.png");
            ImageIO.write(road_elongated_buff, "png", output_file);
        }
        catch(IOException e)
        {
            System.out.println("Error: "+e);
        }
        processed_iv.setImage(i);

        info_tf.setText("Elongated shape Road Clusters");
    }

    //Thining
    public void thining()
    {
        //button check
        if (!elongated_bool){
            info_tf.setText("Elongated road clusters not extracted");
            return;
        }
        thinning_bool = true;


        System.out.println("Start Thinning");

        info_tf.setText("Starting Thining of the Road Clusters...");

        int[][] input = new int[w][h];

        //Creating Binary Image
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++)
            {
                if (road_elongated_buff.getRGB(i,j) == Color.black.getRGB())
                {
                    input[i][j] = 0;
                }else
                    input[i][j] = 1;
            }
        }

      
        Thinning thinning = new Thinning();
        int output[][] = thinning.Thin_algo(input);
     

       //Creating thinned result image
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++)
            {
                if(output[i][j] == 1)
                    image.setRGB(i,j,Color.black.getRGB());
                else
                    image.setRGB(i,j,Color.white.getRGB());
            }
        }

        System.out.println("Done");
        thinImage_buff = image;
        Image i = SwingFXUtils.toFXImage(image,null);
        processed_iv.setImage(i);

        try {
            ImageIO.write(image, "jpg", new File("4_1_binary_Thin_1.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        info_tf.setText("Thining Complete");

    }

    //Color Check if Gray
    public boolean isGray(int p)
    {
        int r = pixData[p].r;
        int g = pixData[p].g;
        int b = pixData[p].b;

        int gray_r = 120;
        int gray_g = 120;
        int gray_b = 200;

        int black_r = 90;
        int black_g = 90;
        int black_b = 90;

        int x = (gray_r - r) * (gray_r-r);
        int y = (gray_b - b) * (gray_b - b);
        int z = (gray_g - g) * (gray_g - g);
        //int gray_threshold = 100;
        double distance_FromGray = Math.sqrt(x + y + z);

        int l = (black_r - r) * (black_r-r);
        int m = (black_b - b) * (black_b - b);
        int n = (black_g - g) * (black_g - g);
        //int black_threshold = 100;
        double distance_FromBlack = Math.sqrt(l + m + n);

        int threshold = 100;

        // System.out.println(" distance ="+distance+" "+r+" "+g+" "+b);
        if(distance_FromGray<threshold )
            return true;
        else
            return false;

        //return true;
    }

    //Surrounding Clusters
    public void surround_clusterBOX(int width,int height,int min_x,int min_y,int max_x,int max_y,boolean isRoad)
    {
        if (isRoad)
        {
            int x = min_x;
            for (int i = 0; i < height; i++) { //Height

                clustered_image_With_Boxes.setRGB(max_y,x,Color.yellow.getRGB());
                clustered_image_With_Boxes.setRGB(min_y,x,Color.yellow.getRGB());

                x++;

            }
            int y = min_y;
            for (int i = 0; i < width; i++) {

                clustered_image_With_Boxes.setRGB(y,min_x,Color.yellow.getRGB());
                clustered_image_With_Boxes.setRGB(y,max_x,Color.yellow.getRGB());

                y++;
            }

            //clustered_image_With_Boxes.setRGB();

        }else
        {
            int x = min_x;
            for (int i = 0; i < height; i++) { //Height

                clustered_image_With_Boxes.setRGB(max_y,x,Color.red.getRGB());
                clustered_image_With_Boxes.setRGB(min_y,x,Color.red.getRGB());

                x++;

            }
            int y = min_y;
            for (int i = 0; i < width; i++) {

                clustered_image_With_Boxes.setRGB(y,min_x,Color.red.getRGB());
                clustered_image_With_Boxes.setRGB(y,max_x,Color.red.getRGB());

                y++;
            }

            //clustered_image_With_Boxes.setRGB();
        }
    }

    public void setTest_button()//final output
    {
        //button check
        if (!elongated_bool){
            info_tf.setText("Elongated cluster not extracted");
            return;
        }
        finalo_bool = true;


        cluster_bool = elongated_bool = thinning_bool = finalo_bool = false;

        Image i = SwingFXUtils.toFXImage(source_buff_COPY, null);
        processed_iv.setImage(i);
        System.out.println("Final Output");
    }

    //sample Image Creation-----------------------------------
    public void createSampleImage() //for clustering
    {
        System.out.println("Creating sample image..");
        Color color_cluster;
        int counter = 0;
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        counter=0;
        for (int i = 0; i < h; i++)
        {
            for (int j = 0; j < w; j++)
            {
                int R = 0, G = 0, B = 0;

                int color = pixData[counter].label * 10;
                if(color < 0)
                {
                    R = 0;
                    G = 0;
                    B = 0;
                }
                else
                {
                    if(pixData[counter].flag_b == -1)
                    {
                        R = (int) (color) % 256;
                        G = (int) (100 + color) % 256;
                        B = (int) (150 + color) % 256;
                    }
                    else
                    {
                        R = 250;
                        G = 250;
                        B = 250;
                    }

                }
                if(color % 3 == 0)
                {
                    color_cluster = new Color(R, G, B);
                }
                else if(color%3 == 1)
                {
                    color_cluster = new Color(G, R, B);
                }
                else
                {
                    color_cluster = new Color(B, R, G);
                }
                bufferedImage.setRGB(j, i, color_cluster.getRGB());

                //for edge
                if(pixData[counter].flag_b == 0)
                {
                    color_cluster = new Color(255,255,0);
                    bufferedImage.setRGB(j,i,color_cluster.getRGB());
                }

                counter++;
            }
        }

        processed_buff = bufferedImage;
        clustered_image = bufferedImage;
        Image i = SwingFXUtils.toFXImage(bufferedImage, null);
        processed_iv.setImage(i);
        //Writing Image
        try
        {
            File output_file = new File("clustered result.png");
            ImageIO.write(bufferedImage, "png", output_file);
        }
        catch(IOException e)
        {
            System.out.println("Error: "+e);
        }
        //o_iv.setImage(i);
    }


    private void createSampleImage_singleUnique()
    {
        System.out.println("Creating sample image..");
        Color color_cluster;
        int counter = 0;
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        counter=0;
        //245,196,105
        //Color myWhite = new Color(255, 255, 255); // Color white

        for (int i = 0; i < singleCluster_new.size(); i++) {
            if (singleCluster_new.get(i).getCurrentLabel() != -1){
                ArrayList<Integer> indexes = singleCluster_new.get(i).indexes;

                for (int j = 0; j <indexes.size() ; j++) {
                    int p = indexes.get(j);

                    int remainder = p %w;
                    int quotient = p/w;

                    int x = quotient;
                    int y = remainder;

                    int R = 0, G = 0, B = 0;

                    int color = singleCluster_new.get(i).getCurrentLabel() * 10;
                    if(color < 0)
                    {
                        R = 0;
                        G = 0;
                        B = 0;
                    }
                    else
                    {
                        if(pixData[counter].flag_b == -1)
                        {
                            R = (int) (color) % 256;
                            G = (int) (100 + color) % 256;
                            B = (int) (150 + color) % 256;
                        }
                        else
                        {
                            R = 250;
                            G = 250;
                            B = 250;
                        }

                    }
                    if(color % 3 == 0)
                    {
                        color_cluster = new Color(R, G, B);
                    }
                    else if(color%3 == 1)
                    {
                        color_cluster = new Color(G, R, B);
                    }
                    else
                    {
                        color_cluster = new Color(B, R, G);
                    }
                    //System.out.println(y+","+x);
                    bufferedImage.setRGB(y,x,color_cluster.getRGB());
                }
            }
        }
        Image i = SwingFXUtils.toFXImage(bufferedImage, null);
        processed_iv.setImage(i);
        //Writing Image
        try
        {
            File output_file = new File("result.png");
            ImageIO.write(bufferedImage, "png", output_file);
        }
        catch(IOException e)
        {
            System.out.println("Error: "+e);
        }
        processed_iv.setImage(i);
    }

    private void print_uniqueCluster(){
        int sum=0;

        for (int i = 0; i <singleCluster_unique.size() ; i++) {
            int currentLabel = singleCluster_unique.get(i).getCurrentLabel();
            int index_size = singleCluster_unique.get(i).indexes.size();
            System.out.println("label= "+currentLabel+" index-size= "+index_size+" w="+singleCluster_unique.get(i).width+" h="+singleCluster_unique.get(i).height+", ");

           /* ArrayList<Integer> x_list = new ArrayList<>();
            for (int j = 0; j < index_size; j++) {
                x_list.add(pixData[singleCluster_unique.get(i).indexes.get(j)].x);
            }
            System.out.println("label= "+currentLabel+" index-size= "+index_size+" w="+singleCluster_unique.get(i).width+" h="+singleCluster_unique.get(i).height+", "+x_list);
            */sum = sum + singleCluster_unique.get(i).indexes.size();
        }
        int a = w*h;
        System.out.println("Total indexes labelled = "+sum+ " ,w*h="+a);
        if(sum!=a){
            System.out.println("Arraylist of clusters is not properly organised");
        }else{
            System.out.println("Arraylist of clusters organised");
        }
    }//Print ClusterList with label and indexes

    private static int colorToRGB(int alpha, int red, int green, int blue) {
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
        return newPixel;
    }

    static BufferedImage imageClone(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
