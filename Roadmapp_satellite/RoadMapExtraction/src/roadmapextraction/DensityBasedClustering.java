/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package roadmapextraction;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DensityBasedClustering
{

    int w,h;
    BufferedImage input;
    BufferedImage source_image;
    static int num;
    int min_noiseSize = 20;

    DensityBasedClustering(int width, int height, BufferedImage input,BufferedImage source_image)
    {
        this.w = width;
        this.h = height;
        this.input = input;
        this.source_image = source_image;
    }
    PixelsData[] pixelsData;
    ArrayList<Integer> No_U_L;
    ArrayList<Clusters> singleCluster;
    ArrayList<Clusters> singleCluster_new;
    ArrayList<Integer> noiseIndexes;
    boolean noise_count_flag;
    int noiseIndex;
////////////////////////////////////////////////////////clustering function
    public PixelsData[] cluster(int noise_size)
    {
        min_noiseSize = noise_size;
        //creating objects
        pixelsData = new PixelsData[w*h];
        noiseIndexes = new ArrayList<>();
        singleCluster = new ArrayList<Clusters>();
        int counter =0;
        noise_count_flag = false;
        //create pixels object
        for(int i=0; i<h;i++)
        {
            for(int j=0; j<w; j++)
            {
                int rgb = input.getRGB(j,i);

                int rgb_source = source_image.getRGB(j,i);
                int a = (rgb_source>>24)&0xff;
                int r = (rgb_source>>16)&0xff;
                int g = (rgb_source>>8)&0xff;
                int b = rgb_source&0xff;

                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                //float hue = hsb[0];
                //float saturation = hsb[1];
                float brightness = hsb[2];


                pixelsData[counter] = new PixelsData(rgb,-1,counter,r,g,b,brightness);
                counter++;
            }
        }
        counter = w+1;
        num=0;
        //START CLUSTERING---------------/////////////////////////////
        for(int i=1; i<h-1;i++)
        {
            for(int j=1; j<w-1; j++)
            {

                ArrayList<Integer> list = new ArrayList<>();
                int[] direction = {counter+1,counter+w,counter-1,counter-w,counter-(w-1),counter-(w+1),counter+(w-1),counter+(w+1)};
                //counter-(w-1),counter-(w+1),counter+(w-1),counter+(w-1) for diagonals
                HashSet<Integer> set = new HashSet<>();

                for (int k = 0; k < direction.length; k++) { //get labels from neighbors
                    int p = direction[k];
                    if (pixelsData[counter].rgb == pixelsData[p].rgb)
                    {
                        if(pixelsData[p].label >= 0 && !set.contains(pixelsData[p].label)) {
                            list.add(pixelsData[p].label);
                            set.add(pixelsData[p].label);
                        }
                    }
                }
                //check list
                if (list.size() > 0) //neighbors found
                {
                    int smallestLabel = findSmallestLabel(list);
                    for (int k = 0; k < list.size(); k++)
                    {
                        if(list.get(k) != smallestLabel)
                        {
                            relabelIndexes(smallestLabel,list.get(k));
                        }
                    }
                    pixelsData[counter].label = smallestLabel;
                    pixelsData[counter].x = i;
                    singleCluster.get(smallestLabel).addIndex(counter);

                }else{ //list empty, no neighbors found

                    pixelsData[counter].label = num;
                    pixelsData[counter].x = i;

                    Clusters single_cluster = new Clusters(num);
                    single_cluster.addIndex(counter);

                    singleCluster.add(single_cluster);
                    num++;

                }
                if(j+1 == w-1){
                    counter = counter + 3;
                }else
                {
                    counter++;
                }

            }
        }

        filter_ClusterList();
        //print_uniqueCluster();

        System.out.println("Done...");
        //print_uniqueCluster();

        sort_indexes_of_Clusters();

        return pixelsData;
    }

    ///HELPER FUNCTIONS
    private void filter_ClusterList(){

        singleCluster_new = new ArrayList<Clusters>(); //will create a new list consisting of labels which are really used by pixels
        create_noiseCluster_only_once();//consists of pixels indexes which labelled as noise

        calculateUniqueLabels();//calculate uniqueLabels from pixdata

        singleCluster.get(singleCluster.size()-1).indexes.addAll(noiseIndexes); //get Noise Indexes and store them

        reduceClusters();// detecting noises with minimum threshold value

        for (int i = 0; i < No_U_L.size(); i++)//creating a new list consisting of labels which are really used by pixels
        {
            int c_label = No_U_L.get(i);
            if (c_label == -1)
                singleCluster_new.add(singleCluster.get(singleCluster.size()-1));
            else
                singleCluster_new.add(singleCluster.get(c_label));

        }
        System.out.println("singleClustrr_new size="+singleCluster_new.size());
    }

    public int findSmallestLabel(ArrayList<Integer> list)
    {
        int smallest = list.get(0);
        if(list.size()==1)
            return smallest;
        else
        {
            for(int i=1;i<list.size();i++)
            {
                if(list.get(i)<smallest)
                    smallest = list.get(i);
            }
            return smallest;
        }
        /*int smallest = list.get(0);
        for(int i=1;i<list.size();i++)
        {
            if(list.get(i)<smallest)
                smallest = list.get(i);
        }
        return smallest;*/
    }
    public void relabelIndexes(int s,int b)//time
    {
        for(int i = 0; i< singleCluster.get(b).indexes.size(); i++)
        {
            //System.out.print(singleCluster.get(b).indexes);
            int c = singleCluster.get(b).indexes.get(i);
            pixelsData[c].label = s;
            //pixelsData[c].x = i;
            singleCluster.get(s).addIndex(c);
            //singleCluster.get(b).indexes.remove(i);
        }

    }
    public void reduceClusters(){
        //No_U_L calculate
        //calculateUniqueLabels();

        System.out.println("Before Reducing no of labels:"+No_U_L.size());


        int noiseLabel = -1;

        int noise_index = singleCluster.size()-1;

        for(int i=0; i<No_U_L.size(); i++)
        {
            int currentLabel;
            if (No_U_L.get(i) == -1)
                currentLabel = noise_index;
            else
                currentLabel = singleCluster.get(No_U_L.get(i)).getCurrentLabel();


            // System.out.println("current label : "+currentLabel);
            int indexSize = singleCluster.get(currentLabel).indexes.size();

            if(indexSize < min_noiseSize)
            {
                for(int p=0; p<indexSize; p++)
                {
                    int index = singleCluster.get(currentLabel).indexes.get(p);
                    pixelsData[index].label = noiseLabel;
                    // current_noise_indexes.add(index);
                    singleCluster.get(noise_index).indexes.add(index);
                    // new_singleCluster.addIndex(index);
                }
            }
        }

        No_U_L=null;
        //No_U_L
        calculateUniqueLabels();
        System.out.println("After Reducing no of labels:"+No_U_L.size());
    }
    private void calculateUniqueLabels()
    {
        // Creates an empty hashset
        HashSet<Integer> set = new HashSet<>();
        No_U_L = new ArrayList<>();
        // Traverse the input array
        for (int i=0; i<pixelsData.length; i++)
        {
            // If not present, then put it in hashtable and print it
            if (!set.contains(pixelsData[i].label))
            {
                set.add(pixelsData[i].label);
                No_U_L.add(pixelsData[i].label);
            }
            if(pixelsData[i].label == -1 && noise_count_flag==false)
            {
                noiseIndexes.add(i);
            }

        }
        noise_count_flag = true;
    }
    private void create_noiseCluster_only_once()
    {
        Clusters new_singleCluster = new Clusters(-1);
        singleCluster.add(new_singleCluster);
        //singleCluster.get(singleCluster.size()-1).indexes.addAll(noiseIndexes);
    }

    private void sort_indexes_of_Clusters(){

        for (int i = 0; i < singleCluster_new.size(); i++) {
            Collections.sort(singleCluster_new.get(i).indexes);
        }
    }


    //GETTER FUNCTIONS
    public ArrayList<Integer> getUniqueClusterList(){
        return No_U_L;
    }

    public ArrayList<Clusters> getClusters(){
        return singleCluster_new;
    }


    //DISPLAY FUNCTIONS
    public void printLabelOutput(){
        int counter=0;
        System.out.println("Printing Output");
        for(int i=0; i<h; i++)
        {
            for(int j=0; j<w; j++)
            {
                System.out.print(pixelsData[counter].x+"\t");
                counter++;
            }
            System.out.println();
        }
    }

    private void print_uniqueCluster(){
        for (int i = 0; i <singleCluster_new.size() ; i++) {

            System.out.println("label= "+singleCluster_new.get(i).getCurrentLabel()+" index-size= "+singleCluster_new.get(i).indexes.size()+", "+singleCluster_new.get(i).indexes);
        }
        System.out.println(singleCluster_new.size());
    }


    ///NOT YET USED
    public boolean checkLabelConflict(ArrayList<Integer> list)
    {
        boolean status = false;
        if(list.isEmpty())
            return status;

        for(int i=0; i<list.size()-1; i++)
        {
            if(list.get(i) != list.get(i+1))
                status = true;
        }
        return status;
    }

    private boolean checkBoundary(int i,int j){
        if(i>0 && i<h && j>0 && j<w){
            return true;
        }else
            return false;
    }
//vari,time,clus re
}
