import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Canvas3D;


@SuppressWarnings("serial")
public class Canvas extends Canvas3D{

	public int startX = 0, startY = 0, endX = 0, endY = 0;
	public float radius = 0.01f, mass = 0.1f;
	private boolean isMenuOn = true;
	private BufferedImage image;
	
	
	public Canvas(GraphicsConfiguration arg0) {
		//Default constructor that sets the graphics configuration
		super(arg0);
		try {
			image = ImageIO.read(new File("./Space.png"));
		} catch (IOException e) {e.printStackTrace();
		}
	}
	
	public void setMenuOn(boolean b){
		isMenuOn = b;
	}
	
	public boolean isMenuOn(){
		return isMenuOn;
	}

	@Override
	public void postRender(){
		Graphics2D temp = this.getGraphics2D();
		/*if(isMenuOn){
			temp.drawImage(image, 0, 0, null);
			temp.setColor(Color.white);
			temp.setFont(new Font ("Sans", Font.BOLD, 100));
			temp.drawString("420BLAZEIT", 200, 400);
			temp.setFont(new Font ("Sans", 15, 15));
			this.getGraphics2D().flush(false);
			return;
		}*/

		//Draws the crosshair and the strings showing the radius and mass of the balls being fired by the user
		try{
			temp.drawLine(480,300,520,300);
			temp.drawLine(500,290,500,310);
			temp.fillRect(startX, startY, endX, endY);
			temp.drawString("Radius: " + radius, 0, 20);
			temp.drawString("Mass: " + mass, 0, 40);
			this.getGraphics2D().flush(false);
		}catch(NullPointerException e){}
	}
	
}
