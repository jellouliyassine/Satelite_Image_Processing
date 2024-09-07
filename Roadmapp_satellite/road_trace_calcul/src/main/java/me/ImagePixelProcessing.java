package me;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;

public class ImagePixelProcessing {
    public static void main(String[] args) {
    	//black_wite("C:/Users/yassine/eclipse-workspace/me/image/a.png","C:/Users/yassine/eclipse-workspace/me/image/localisation_terain_ibno_batotab.png");
    	route();
    
    
    
    }
    
    
    static public void black_wite(String input,String output) {
    	
    	try {
            // Load the image file
            File imageFile = new File(input);
            BufferedImage image = ImageIO.read(imageFile);

            // Get image dimensions
            int width = image.getWidth();
            int height = image.getHeight();
System.out.println("scale :"+width+","+height);
            // Set the threshold value (adjust as needed)
            int threshold = 128;

            // Iterate over each pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get the RGB values of the pixel
                    int rgb = image.getRGB(x, y);

                    // Extract the individual color components (red, green, blue)
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Calculate grayscale intensity using formula: 0.299 * red + 0.587 * green + 0.114 * blue
                    int intensity = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

                    // Change pixel color based on intensity
                    if (intensity > threshold) {
                        // Change pixel color to white
                    //   image.setRGB(x, y, Color.WHITE.getRGB());
                        
                    } else {
                        // Change pixel color to black
                    // image.setRGB(x, y, Color.BLACK.getRGB());
                    	 // image.setRGB(x, y, Color.RED.getRGB());
                        System.out.println(x+","+y);
                    }
                }
            }

            // Save the modified image
            File outputImageFile = new File(output);
            ImageIO.write(image, "png", outputImageFile);

            System.out.println("Image processing completed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public int route() {

    	
    	
    	
        try {

            // Load the image file
            File imageFile = new File("C:/Users/yassine/eclipse-workspace/me/image/a.png");
            BufferedImage image = ImageIO.read(imageFile);

            // Get image dimensions
            int width = image.getWidth();
            int height = image.getHeight();
            int dp=0;
            int scale=20/4;
            System.out.println("width :"+width);
            System.out.println("hight :"+height);
            int threshold = 128;
            //depart
          /*  pixel pd = new pixel(55,13);
            pixel ps = new pixel(289,231);*/
            pixel pd = new pixel(50,63);
            pixel ps = new pixel(413,63);
            ArrayList<pixel> lp =new ArrayList<pixel>();
            lp.add(pd);
            //traitment
            for(int i=0;i<lp.size();i++) {
            pixel p =lp.get(i);	
          
            // solution
          
            
            if((p.x==ps.x)&&(p.y==ps.y)) {
             	
                for(pixel k =p;k.prev!=null;k=k.prev) {
                	dp++;
                	int rgb = image.getRGB(k.x, k.y);

                    // Extract the individual color components (red, green, blue)
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Calculate grayscale intensity using formula: 0.299 * red + 0.587 * green + 0.114 * blue
                    int intensity = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

                    // Change black pixel color to red
               
                    	
                        image.setRGB(k.x, k.y, Color.RED.getRGB());
                        
                        
                
                }
            
       		 
       			 // Save the modified image
              File outputImageFile = new File("C:/Users/yassine/eclipse-workspace/me/image/resultat.PNG");
              ImageIO.write(image, "PNG", outputImageFile);
              System.out.println("solution:"+p.x+","+p.y);
              System.out.println("distance ="+dp*scale+" m");
              System.out.println("Image processing completed.");
          	
          	return 0;}
            
            
           
            
            ArrayList<pixel> r=p.mouvment(width,height);
            	
            for(int j=0;j<r.size();j++) {
            	pixel p1=r.get(j);
            	if((p1.x<width)&&(p1.x>0)) {
if((p1.y<height)&&(p1.y>0)) {
                  

                    if(lp.contains(p1)==false) {
                    	
                    	
                    	int rgb = image.getRGB(p1.x, p1.y);

                        // Extract the individual color components (red, green, blue)
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        // Calculate grayscale intensity using formula: 0.299 * red + 0.587 * green + 0.114 * blue
                        int intensity = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                  
                        // diferencier
                        
                        if (intensity > threshold) {
                            // Change pixel color to white
                        
                            
                        } else {
                            // Change pixel color to black
                        	p1.prev=lp.get(i);	
                       	 lp.add(p1);                        }
                        
                        
                      
                    }}}}}
            
          } catch (IOException e) {
            e.printStackTrace();
        }
		return 1;
		}}




