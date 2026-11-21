package game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import utils.LoadSave;

public class Options extends JPanel implements MouseListener,MouseMotionListener{
	
	private final int optionsBgWidth=280;
	private final int optionsBgHeight=390;
	private final int buttonSize=42;
	private final int menuButtonSize=(int) (buttonSize*1.5f);
	private int soundIndex,effectIndex,menuIndex;
	private int soundMute,effectMute;
	
	private BufferedImage[][] soundImgs;
	private BufferedImage[] menuImgs;
	
	private BufferedImage backgroundImg,optionsImg;
	private int ButtonX,soundButtonY,effectButtonY,menuButtonX,menuButtonY;
	private Game game;
	
	public Options(Game game) {
		this.game=game;
		loadBackground();
		loadButtonImages();
		addMouseListener(this);
        addMouseMotionListener(this);
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        int optionsBgX=(getWidth()-optionsBgWidth)/2;
        int optionsBgY=(getHeight()-optionsBgHeight)/2;
        g.drawImage(optionsImg, optionsBgX, optionsBgY, optionsBgWidth, optionsBgHeight, null);
        ButtonX=optionsBgX+190-buttonSize/2;
        soundButtonY=optionsBgY+129-buttonSize/2;
        effectButtonY=optionsBgY+174-buttonSize/2;
        g.drawImage(soundImgs[soundMute][soundIndex], ButtonX, soundButtonY, buttonSize, buttonSize,null);
        g.drawImage(soundImgs[effectMute][effectIndex], ButtonX, effectButtonY, buttonSize, buttonSize,null);
        menuButtonX=(getWidth()-menuButtonSize)/2;
        menuButtonY=optionsBgY+297-menuButtonSize/2;
        g.drawImage(menuImgs[menuIndex], menuButtonX, menuButtonY, menuButtonSize, menuButtonSize,null);
    }
	
	private void loadBackground() {
		backgroundImg=LoadSave.getSpriteAtlas(LoadSave.Menu_background);
        optionsImg=LoadSave.getSpriteAtlas(LoadSave.Options_backgound);
    }
	
    private void loadButtonImages() {
    	soundImgs=new BufferedImage[2][3];
        BufferedImage temp = LoadSave.getSpriteAtlas(LoadSave.Sound_buttons);
		for (int i = 0; i < soundImgs.length; i++) {
			for(int j=0;j<soundImgs[0].length;j++) {
				soundImgs[i][j] = temp.getSubimage(j * buttonSize, i * buttonSize, buttonSize, buttonSize);
			}
		}
		menuImgs=new BufferedImage[3];
        temp = LoadSave.getSpriteAtlas(LoadSave.Urm_buttons);
		for (int i = 0; i < menuImgs.length; i++) {
			menuImgs[i] = temp.getSubimage(i * 56, 2*56, 56, 56);
		}
    }
    
    private boolean isMouseOver(int x, int y, int width, int height, float mouseX, float mouseY) {
        if (x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height) {
            return true;
        }
        return false;
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		float mouseX = e.getX();
        float mouseY = e.getY();

        soundIndex = 0;
        effectIndex = 0;
        menuIndex = 0;

        if (isMouseOver(ButtonX, soundButtonY, buttonSize, buttonSize, mouseX, mouseY)) {
        	soundIndex = 2;
        }
        if (isMouseOver(ButtonX, effectButtonY, buttonSize, buttonSize, mouseX, mouseY)) {
        	effectIndex = 2;
        }
        if (isMouseOver(menuButtonX, menuButtonY, menuButtonSize, menuButtonSize, mouseX, mouseY)) {
        	menuIndex = 2;
        }
        repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		float mouseX = e.getX();
        float mouseY = e.getY();

        soundIndex = 0;
        effectIndex = 0;
        menuIndex = 0;

        if (isMouseOver(ButtonX, soundButtonY, buttonSize, buttonSize, mouseX, mouseY)) {
        	soundIndex = 1;
        	if(soundMute==0) {
        		soundMute=1;
        		game.getSoundManager().toggleSongMute();
        	}
        	else {
        		soundMute=0;
        		game.getSoundManager().toggleSongMute();
        	}
        }
        if (isMouseOver(ButtonX, effectButtonY, buttonSize, buttonSize, mouseX, mouseY)) {
        	effectIndex = 1;
        	if(effectMute==0) {
        		effectMute=1;
        		game.getSoundManager().toggleEffectMute();
        	}
        	else {
        		effectMute=0;
        		game.getSoundManager().toggleEffectMute();
        	}
        }
        if (isMouseOver(menuButtonX, menuButtonY, menuButtonSize, menuButtonSize, mouseX, mouseY)) {
        	menuIndex = 1;
        	game.showMenu();
        }
        repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		float mouseX = e.getX();
        float mouseY = e.getY();

        soundIndex = 0;
        effectIndex = 0;
        menuIndex = 0;

        if (isMouseOver(ButtonX, soundButtonY, buttonSize, buttonSize, mouseX, mouseY)) {
        	soundIndex = 1;
        }
        if (isMouseOver(ButtonX, effectButtonY, buttonSize, buttonSize, mouseX, mouseY)) {
        	effectIndex = 1;
        }
        if (isMouseOver(menuButtonX, menuButtonY, menuButtonSize, menuButtonSize, mouseX, mouseY)) {
        	menuIndex = 1;
        }
        repaint();
	}
	
}
