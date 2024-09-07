/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package roadmapextraction;

/**
 *
 * @author Fadio
 */

import java.util.ArrayList;

public class Clusters {
    int currentLabel;
    int width;
    int height;
    ArrayList<Integer> indexes;
    boolean isRoad;
    boolean isGray;

    int min_X,min_Y,max_X,max_Y;

   // HashSet<Integer> indexes;

    Clusters(int currentLabel){
        this.indexes = new ArrayList<>();
        //this.indexes = new HashSet<>();
        this.currentLabel = currentLabel;
        this.width = 0;
        this.height = 0;
        this.isRoad = false;
        this.isGray = false;
        this.min_X = this.max_X = this.min_Y = this.max_Y = -1;

    }
    public void addIndex(int i){
       // if(!indexes.contains(i))
            indexes.add(i);
    }
    public int getCurrentLabel(){
        return currentLabel;
    }
}
