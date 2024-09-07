/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package roadmapextraction;

/**
 *
 * @author Fadio
 */
public class PixelsData {
    public int r,g,b;
    float h;
    public int rgb;
    int v;
    int label;
    int index;
    int flag_b;
    int x;
    PixelsData(int rgb,int label,int index,int r,int g,int b,float h){
        this.rgb = rgb;
        this.label = label;
        this.index = index;
        this.flag_b = -1;
        this.x = -1;
        this.v = 0;
        this.r = r;
        this.g = g;
        this.b = b;
        this.h = h;
    }
}
