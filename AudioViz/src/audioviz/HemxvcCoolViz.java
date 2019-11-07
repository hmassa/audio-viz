/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioviz;


import static java.lang.Integer.min;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 *
 * @author Haley Massa
 */
public class HemxvcCoolViz implements Visualizer {

    private final String name = "Hemxvc Cool Viz";
    
    private String vizPaneInitialStyle = "";
    
    private AnchorPane vizPane;
    private Integer numRows;
    private Integer numBoxes;
    
    private Double width = 0.0;
    private Double height = 0.0;
    
    private Label label;
    private Circle sticker;
    private Circle hole;
    
    private Double boxWidth;
    
    private ImageView iv;
    private Image record;
    private final String imageName = "HemxvcVinyl.png";
    private final Double diameter = 340.0;
    
    private final Double startHue = 0.0;
    private final Double angleChange = 10.0; // ~33.33 rpm
    private final Double left = 45.0;
    private final Double up = 30.0;
    
    private Rectangle[][] boxesL;
    private Rectangle[][] boxesR;
    
    @Override
    public void start(Integer numBands, AnchorPane vizPane) {
        end();
        
        vizPaneInitialStyle = vizPane.getStyle();
        
        this.vizPane = vizPane;
        numBoxes = numBands;
        numRows = (int)Math.sqrt(numBands);
        
        height = vizPane.getHeight();
        width = vizPane.getWidth();
        
        Rectangle clip = new Rectangle(width, height);
        clip.setLayoutX(0);
        clip.setLayoutY(0);
        vizPane.setClip(clip);
        
        record = new Image(getClass().getResourceAsStream(imageName));
        iv = new ImageView();
        iv.setImage(record);
        iv.setFitWidth(diameter);
        iv.setFitHeight(diameter);
        iv.setLayoutX(width/2 - iv.getFitWidth()/2);
        iv.setLayoutY(height/2 - iv.getFitHeight()/2);
        vizPane.getChildren().add(iv);
        
        sticker = new Circle(width/2, height/2, 55.0);
        sticker.setFill(Color.hsb(startHue, 1.0, 1.0, 1.0));
        vizPane.getChildren().add(sticker);
        
        hole = new Circle(width/2, height/2, 5.0);
        hole.setFill(Color.BLACK);
        vizPane.getChildren().add(hole);
        
        label = new Label(name);
        label.setTextFill(Color.WHITE);
        label.setLayoutX(width/2 - left);
        label.setLayoutY(height/2 - up);
        vizPane.getChildren().add(label);
        
        boxesL = new Rectangle[numRows][numRows];
        Double displayWidth = (width - diameter)/2 - 10;
        boxWidth = displayWidth/numRows - 3;
        Double yOffset = (height-displayWidth)/2;
        
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numRows; j++){
                Rectangle box = new Rectangle(boxWidth, boxWidth);
                box.setX(j*(boxWidth + 3) + 5);
                box.setY(i*(boxWidth + 3) + yOffset);
                box.setFill(Color.BLACK);
                boxesL[i][j] = box;
                vizPane.getChildren().add(box);
            }  
        }
        
        boxesR = new Rectangle[numRows][numRows];
        Double xOffset = width - displayWidth - 5;
        
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numRows; j++){
                Rectangle box = new Rectangle(boxWidth, boxWidth);
                box.setX(j*(boxWidth + 3) + xOffset);
                box.setY(i*(boxWidth + 3) + yOffset);
                box.setFill(Color.BLACK);
                boxesR[i][j] = box;
                vizPane.getChildren().add(box);
            }  
        }
    }

    @Override
    public void end() {
        if (boxesL != null){
            for(Rectangle[] bL : boxesL){
                for(Rectangle box : bL){
                    vizPane.getChildren().remove(box);
                }
            }
        }
        if (boxesR != null){
            for(Rectangle[] bR : boxesR){
                for(Rectangle box : bR){
                    vizPane.getChildren().remove(box);
                }
            }
        }
        if (label != null){
            vizPane.getChildren().remove(label);
        }
        if (iv != null) {
            vizPane.getChildren().remove(iv);
        }
        if (sticker != null) {
            vizPane.getChildren().remove(sticker);
        }
        if (hole != null) {
            vizPane.getChildren().remove(hole);
            vizPane.setClip(null);
            vizPane.setStyle(vizPaneInitialStyle);
        } 
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void draw(double timestamp, double length, float[] magnitudes, float[] phases) {
        if (label == null) {
            return;
        }
        Double hue = magnitudes[0]*-6.0;
        sticker.setFill(Color.hsb(hue, 1.0, 1.0, 1.0));
        
        label.getTransforms().add(new Rotate(angleChange, left, up));
        iv.setRotate((200.0*timestamp)%360); // ~33.33 rpm
        
        Integer m = min(magnitudes.length, numBoxes);
        Integer p = min(phases.length, numBoxes);
        
        for(int i = 0; i < m; i++){
            int col = (int)i/numRows;
            int row = i%numRows;
            if((((magnitudes[i]*-6.0)%360)) > 0){
                boxesL[numRows-1-row][col].setFill(Color.hsb(magnitudes[i]*-6.0, 1.0, 1.0));
                boxesR[numRows-1-row][numRows-1-col].setFill(Color.hsb(magnitudes[i]*-6.0, 1.0, 1.0));
            }
            else {
                boxesL[numRows-1-row][col].setFill(Color.BLACK);
                boxesR[numRows-1-row][numRows-1-col].setFill(Color.BLACK);
            }
        }
        for(int j=0; j < p; j++){
            int row = j/numRows;
            int col = j%numRows;
            {
                boxesL[row][col].setHeight(boxWidth + phases[j]);
                boxesL[row][col].setWidth(boxWidth + phases[j]);
                boxesR[row][col].setHeight(boxWidth + phases[j]);
                boxesR[row][col].setWidth(boxWidth + phases[j]);
            }
        }
        
        vizPane.setStyle("-fx-background-color: hsb(" + magnitudes[0]*-6 + 
                ", " + Math.abs(phases[0])*15 + "%, 100%)" );
    }
}
