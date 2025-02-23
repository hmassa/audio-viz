/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioviz;

import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Professor Wergeles
 */
public interface Visualizer {
    public void start(Integer numBands, AnchorPane vizPane);
    public void end();
    public String getName();
    public void draw(double timestamp, double length, float[] magnitudes, float[] phases);
}
