import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.Arrays;

import javax.swing.*;
import java.awt.*;

import java.lang.Math;


class Particle{
    int x;
    int y;
    int speed;

    Particle(int x, int y,  int speed){
        this.x = x;
        this.y = y;
        this.speed = speed;
    };

};

public class fallingSand {


    public static void main(String args[]) throws IOException {
        

        int width = 1000;
        int height = 600;



        BufferedImage image = null;
        File f = null;
        int[] zeros = new int[width*height];
        Arrays.fill(zeros, 0);

        int gridLength = 10; // this must be divisible by 2
        int[] sandBlock = new int[(width + 1) * (height + 1)];
        Arrays.fill(sandBlock, new Color(255, 255, 0).getRGB());
        int[] waterBlock = new int[(width + 1) * (height + 1)];
        Arrays.fill(waterBlock, new Color(0, 0, 255).getRGB());

        int speed = 1; // this must be a whole number, it is the constant speed at which the sand falls in terms of the grid
        boolean canFallDown;
        boolean canFallRight;
        boolean canFallLeft;
        boolean canMoveRight;
        boolean canMoveLeft;
        boolean canMoveUp;
        int gridX;
        int gridY;

        float mouseX;
        float mouseY;
        int gridMouseX;
        int gridMouseY;
        int gridFrameX;
        int gridFrameY;


        long past = System.currentTimeMillis();
        long now;
        long deltaT = 0;
        int spawnTimerLength = 1;
        int spawnTime = 0;
        int refreshRate = 1;

        // this MUST be evenly divisible by 3
        Particle[] sandPositions = new Particle[width * height];
        int n_sand = 0;
        Particle[] waterPositions = new Particle[width * height];
        int n_water = 0;


        //stores the state of each block in the grid
        int[] gridState = new int[(width/gridLength) * (height/gridLength) + 1];
        Arrays.fill(gridState, 0);
        int startRow;
        int startRowX = 0;
        int startRowY = 0;

        

        // this creates the image that we will manipulate, but it does so with a screenshot, so that's not efficient. 
        // I just don't understand BufferedImage that well.
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(width, height);
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, "jpg", new File("sand.jpg"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try{
            f = new File("sand.jpg");
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(f);
            
            image.setRGB(0, 0, width, height, zeros, 0, 1);

            ImageIO.write(image, "jpg", new File("sand.jpg"));
            
            
        }catch(IOException e){
            System.out.println("Error: "+e);
        }



        JFrame frame = new JFrame("Image Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label);


            
        

        

        while(true){

            image.setRGB(0, 0, width, height, zeros, 0, 1);
            Arrays.fill(gridState, 0);

            gridFrameX = Math.round(frame.getX()/gridLength);
            gridFrameY = Math.round(frame.getY()/gridLength);
            mouseX = MouseInfo.getPointerInfo().getLocation().x;
            mouseY = MouseInfo.getPointerInfo().getLocation().y;

            gridMouseX = Math.round(mouseX/gridLength) - gridFrameX;
            gridMouseY = Math.round(mouseY/gridLength)  - gridFrameY;

            spawnTime = spawnTime + refreshRate;
            if(spawnTime >= spawnTimerLength){
                spawnTime = 0;
                if(gridMouseX > 0 && gridMouseX < width/gridLength && gridMouseY > 0 && gridMouseY < height/gridLength){
                    if(gridMouseY < height/ (4 * gridLength)){
                        sandPositions[n_sand] = new Particle(gridMouseX, gridMouseY, speed);
                        n_sand = n_sand + 1;
                    }else if(gridMouseY < height/ (2 * gridLength)){
                        waterPositions[n_water] = new Particle(gridMouseX, gridMouseY, speed);
                        n_water = n_water + 1;
                    }

                }
                System.out.println(deltaT);
                System.out.println(n_sand);

            }

            // sand calculations
            for(int i = 0; i < n_sand; i++){
                gridX = sandPositions[i].x;
                gridY = sandPositions[i].y;
                speed = sandPositions[i].speed;
                canFallDown = true;
                canFallRight = true;
                canFallLeft = true;

                // figuring out where it can go

                for(int j = 0; j < n_sand; j++){
                    if((sandPositions[j].y == sandPositions[i].y +1 && sandPositions[j].x == sandPositions[i].x) 
                    || sandPositions[i].y * gridLength >= height - gridLength ){
                        canFallDown = false;
                    }
                    if((sandPositions[j].y == sandPositions[i].y +1 && sandPositions[j].x == sandPositions[i].x + 1) 
                    || sandPositions[i].y * gridLength >= height - gridLength 
                    || sandPositions[i].x * gridLength >= width - gridLength){
                        canFallRight = false;
                    }
                    if((sandPositions[j].y == sandPositions[i].y +1 && sandPositions[j].x == sandPositions[i].x - 1) 
                    || sandPositions[i].y * gridLength >= height - gridLength
                    || sandPositions[i].x * gridLength <= 0){
                        canFallLeft = false;
                    } 
                }

                // moving it

                if(canFallDown){
                    sandPositions[i].y = gridY + speed;
                    gridY = sandPositions[i].y;
                }else if (canFallRight){
                    sandPositions[i].y = gridY + speed;
                    gridY = sandPositions[i].y;
                    sandPositions[i].x = gridX + 1;
                    gridX = sandPositions[i].x;
                }else if (canFallLeft){
                    sandPositions[i].y = gridY + speed;
                    gridY = sandPositions[i].y;
                    sandPositions[i].x = gridX - 1;
                    gridX = sandPositions[i].x;
                }
                // putting that info in gridState
                gridState[gridY * width/gridLength + gridX] = 1;
                
                //image.setRGB(gridX * gridLength, Math.round(gridY * gridLength), gridLength, gridLength, sandBlock, 0, 1);
            }

            for(int i = 0; i < height/gridLength; i++){
                startRow = -1;
                if(gridState[i*width/gridLength] == 1){
                    startRow = i*width/gridLength;
                    startRowY = i;
                    startRowX = 0;
                }
                for (int j = 1; j< width/gridLength + 1; j++){
                    if(gridState[i*width/gridLength + j] == 1 && gridState[i*width/gridLength + j -1] == 0){
                        startRow = i*width/gridLength + j;
                        startRowY = i;
                        startRowX = j;
                    }else if((gridState[i*width/gridLength + j] == 0 && gridState[i*width/gridLength + j -1] == 1) 
                    || (startRow != -1 && j == width/gridLength)){
                        //draw
                        image.setRGB(startRowX * gridLength, startRowY * gridLength, (j-startRowX)*gridLength, gridLength, sandBlock, 0, 1);
                        startRow = -1;
                        
                    }
                }
            }

             
            // water calculations
            Arrays.fill(gridState, 0);

            for(int i = 0; i < n_water; i++){
                gridX = waterPositions[i].x;
                gridY = waterPositions[i].y;
                speed = waterPositions[i].speed;
                canFallDown = true;
                canFallRight = true;
                canFallLeft = true;
                canMoveRight = true;
                canMoveLeft = true;
                canMoveUp = false;

                // figuring out where it can go

                for(int j = 0; j < n_sand; j++){
                    if((sandPositions[j].y == waterPositions[i].y +1 && sandPositions[j].x == waterPositions[i].x) 
                    || waterPositions[i].y * gridLength >= height - gridLength ){
                        canFallDown = false;
                    }
                    if((sandPositions[j].y == waterPositions[i].y +1 && sandPositions[j].x == waterPositions[i].x + 1) 
                    || waterPositions[i].y * gridLength >= height - gridLength 
                    || waterPositions[i].x * gridLength >= width - gridLength){
                        canFallRight = false;
                    }
                    if((sandPositions[j].y == waterPositions[i].y +1 && sandPositions[j].x == waterPositions[i].x - 1) 
                    || waterPositions[i].y * gridLength >= height - gridLength
                    || waterPositions[i].x * gridLength <= 0){
                        canFallLeft = false;
                    }
                    if(sandPositions[j].y == waterPositions[i].y && sandPositions[j].x == waterPositions[i].x - 1){
                        canMoveLeft = false;
                    }
                    if(sandPositions[j].y == waterPositions[i].y && sandPositions[j].x == waterPositions[i].x){
                        canMoveUp = true;
                    }
                    if(sandPositions[j].y == waterPositions[i].y && sandPositions[j].x == waterPositions[i].x + 1){
                        canMoveRight = false;
                    }
                }
                for(int j = 0; j < n_water; j++){
                    if(waterPositions[j].y == waterPositions[i].y +1 && waterPositions[j].x == waterPositions[i].x
                    || waterPositions[i].y * gridLength >= height - gridLength) {
                        canFallDown = false;
                    }
                    if(waterPositions[j].y == waterPositions[i].y +1 && waterPositions[j].x == waterPositions[i].x + 1 
                    || waterPositions[i].y * gridLength >= height - gridLength 
                    || waterPositions[i].x * gridLength >= width - gridLength){
                        canFallRight = false;
                    }
                    if(waterPositions[j].y == waterPositions[i].y +1 && waterPositions[j].x == waterPositions[i].x - 1 
                    || waterPositions[i].y * gridLength >= height - gridLength
                    || waterPositions[i].x * gridLength <= 0){
                        canFallLeft = false;
                    }
                    if(waterPositions[j].y == waterPositions[i].y && waterPositions[j].x == waterPositions[i].x - 1
                    || waterPositions[i].x <= 0){
                        canMoveLeft = false;
                    }
                    if(waterPositions[j].y == waterPositions[i].y && waterPositions[j].x == waterPositions[i].x && i != j){
                        canMoveUp = true;
                    }
                    if(waterPositions[j].y == waterPositions[i].y && waterPositions[j].x == waterPositions[i].x + 1
                    || waterPositions[i].x >= width/gridLength -1){
                        canMoveRight = false;
                    }
                }

                // moving it

                if(canFallDown){
                    waterPositions[i].y = gridY + speed;
                    gridY = waterPositions[i].y;
                }else if (canFallRight){
                    waterPositions[i].y = gridY + speed;
                    gridY = waterPositions[i].y;
                    waterPositions[i].x = gridX + speed;
                    gridX = waterPositions[i].x;
                }else if (canFallLeft){
                    waterPositions[i].y = gridY + speed;
                    gridY = waterPositions[i].y;
                    waterPositions[i].x = gridX - speed;
                    gridX = waterPositions[i].x;
                }else if (canMoveRight){
                    waterPositions[i].x = gridX + speed;
                    gridX = waterPositions[i].x;
                }else if (canMoveLeft){
                    waterPositions[i].x = gridX - speed;
                    gridX = waterPositions[i].x;
                }else if (canMoveUp){
                    waterPositions[i].y = gridY - speed;
                    gridY = waterPositions[i].y;
                }

                // putting that info in gridState
                gridState[gridY * width/gridLength + gridX] = 1;
                
            }

            for(int i = 0; i < height/gridLength; i++){
                startRow = -1;
                if(gridState[i*width/gridLength] == 1){
                    startRow = i*width/gridLength;
                    startRowY = i;
                    startRowX = 0;
                }
                for (int j = 1; j< width/gridLength + 1; j++){
                    if(gridState[i*width/gridLength + j] == 1 && gridState[i*width/gridLength + j -1] == 0){
                        startRow = i*width/gridLength + j;
                        startRowY = i;
                        startRowX = j;
                    }else if((gridState[i*width/gridLength + j] == 0 && gridState[i*width/gridLength + j -1] == 1) 
                    || (startRow != -1 && j == width/gridLength)){
                        //draw
                        image.setRGB(startRowX * gridLength, startRowY * gridLength, (j-startRowX)*gridLength, gridLength, waterBlock, 0, 1);
                        startRow = -1;
                        
                    }
                }
            }

             // end water


            
            
            // Create a JLabel to hold the image
            label = new JLabel(new ImageIcon(image));
            frame.getContentPane().remove(0);
            frame.getContentPane().add(label);
            

            frame.pack();
            frame.setVisible(true);

            now = System.currentTimeMillis();

            deltaT = now - past;
            past = now;
            
            //System.out.println(deltaT);





            try {
                Thread.sleep(refreshRate);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            
        }




    }


}
