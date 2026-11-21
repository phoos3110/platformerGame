package entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import utils.ImageUtils;
import java.awt.Color;

import game.GamePanel;
import game.MapManager;
import game.SoundManager;

public class Character {
    
    private int x, y;
    private int xSpeed, ySpeed;
    private boolean left, right, jumping, onGround;
    private boolean facingRight = true;

    private int frame = 0;
    private int idleFrame = 0;
    private int jumpFrame = 0;
    private int deathFrame = 0;

    private int frameDelay = 0;
    private int idleFrameDelay = 0;
    private int jumpFrameDelay = 0;
    private int deathFrameDelay = 0;

    private boolean isDead = false;

    private int[][] mapData;
    private int tileSize;

    private ArrayList<Image> deathFrames = new ArrayList<>();
    private ArrayList<Image> runRightFrames = new ArrayList<>();
    private ArrayList<Image> runLeftFrames = new ArrayList<>();
    private ArrayList<Image> idleFrames = new ArrayList<>();
    private ArrayList<Image> jumpFrames = new ArrayList<>();

    private static final int CHAR_WIDTH = 32;
    private static final int CHAR_HEIGHT = 32;
    private static final double DRAW_WIDTH = 51.2 * 1.5;
    private static final double DRAW_HEIGHT = 32 * 1.5;

    private static final int MOVE_SPEED = 4;
    private static final int JUMP_FORCE = -10;
    private static final int DOUBLE_JUMP_FORCE = -12;
    private static final int GRAVITY = 1;
    private static final int MAX_FALL_SPEED = 10;

    private boolean canDoubleJump = false;
    private long lastJumpTime = 0;
    private static final long DOUBLE_JUMP_WINDOW = 500;
    
    private GamePanel gamePanel;

    public Character(int x, int y, GamePanel gamePanel) {
        this.x = x;
        this.y = y;
        this.gamePanel = gamePanel;
        this.onGround = true;
        loadAnimations();
    }

    private void loadAnimations() {
        String[][] animations = {
                { "run", "assets/nv/9.png", "assets/nv/10.png", "assets/nv/11.png", "assets/nv/12.png", "assets/nv/13.png", "assets/nv/14.png" },
                { "idle", "assets/nv/1.png", "assets/nv/2.png", "assets/nv/3.png","assets/nv/4.png", "assets/nv/5.png" },
                { "jump", "assets/nv/17.png", "assets/nv/18.png", "assets/nv/19.png" },
                { "death", "assets/nv/50.png", "assets/nv/51.png", "assets/nv/52.png","assets/nv/53.png", "assets/nv/54.png", "assets/nv/55.png","assets/nv/56.png" }
        };

        for (String[] anim : animations) {
            String type = anim[0];
            for (int i = 1; i < anim.length; i++) {
                Image img = utils.LoadSave.getSpriteAtlas(anim[i]);
                switch (type) {
                    case "run":
                        runRightFrames.add(img);
                        runLeftFrames.add(ImageUtils.createFlipped(img));
                        break;
                    case "idle":
                        idleFrames.add(img);
                        break;
                    case "jump":
                        jumpFrames.add(img);
                        break;
                    case "death":
                        deathFrames.add(img);
                        break;
                }
            }
        }
    }

    public void setMapData(int[][] mapData, int tileSize) {
        this.mapData = mapData;
        this.tileSize=tileSize;
    }

    public void update() {
        if (isDead) {
            updateDeathAnimation();
            return;
        }

        if (isOnWater()) {
            startDeathAnimation();
            return;
        }

        updateMovement();
        updatePosition();
        updateAnimations();
    }

    private void updateMovement() {
        xSpeed = 0;

        if (right && !left) {
            xSpeed = MOVE_SPEED;
            facingRight = true;
        }
        if (left && !right) {
            xSpeed = -MOVE_SPEED;
            facingRight = false;
        }

        if (jumping) {
            long currentTime = System.currentTimeMillis();

            if (onGround) {
                ySpeed = JUMP_FORCE;
                onGround = false;
                lastJumpTime = currentTime;
                canDoubleJump = true;
                getGamePanel().getGame().getSoundManager().playEffect(1);
            } else if (canDoubleJump && (currentTime - lastJumpTime < DOUBLE_JUMP_WINDOW)) {
                ySpeed = DOUBLE_JUMP_FORCE;
                canDoubleJump = false;
                getGamePanel().getGame().getSoundManager().stopEfect();
                getGamePanel().getGame().getSoundManager().playEffect(1);
            }
            jumping = false;
        }
    }

    private void updatePosition() {

        int newX = x + xSpeed;
        if (canMoveHorizontally(newX)) {
            x = newX;
        }

        boolean hasGroundBelow = checkGroundBelow();
        if (!hasGroundBelow && onGround) {
            onGround = false;
        }

        if (!onGround) {
            ySpeed += GRAVITY;
            ySpeed = Math.min(ySpeed, MAX_FALL_SPEED);
        }

        int newY = y + ySpeed;
        updateVerticalPosition(newY);

        keepInBounds();
    }

    private boolean checkGroundBelow() {
        int leftTile = x / tileSize;
        int rightTile = (x + CHAR_WIDTH - 1) / tileSize;
        int bottomTile = (y + CHAR_HEIGHT + 1) / tileSize;

        if (bottomTile >= mapData.length)
            return false;

        for (int tx = leftTile; tx <= rightTile; tx++) {
            if (tx < 0 || tx >= mapData[0].length)
                continue;
            if (isSolidTile(mapData[bottomTile][tx]))
                return true;
        }
        return false;
    }

    private void updateVerticalPosition(int newY) {
        int leftTile = x / tileSize;
        int rightTile = (x + CHAR_WIDTH - 1) / tileSize;

        if (ySpeed >= 0) { 
            int bottomTile = (newY + CHAR_HEIGHT) / tileSize;
            boolean willHitGround = false;

            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (tx < 0 || tx >= mapData[0].length || bottomTile >= mapData.length)
                    continue;
                if (isSolidTile(mapData[bottomTile][tx])) {
                    willHitGround = true;
                    break;
                }
            }

            if (willHitGround) {
                y = bottomTile * tileSize - CHAR_HEIGHT;
                ySpeed = 0;
                onGround = true;
            } else {
                y = newY;
                onGround = false;
            }
        } else { 
            int topTile = newY / tileSize;
            boolean willHitCeiling = false;

            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (tx < 0 || tx >= mapData[0].length || topTile < 0)
                    continue;
                if (isSolidTile(mapData[topTile][tx])) {
                    willHitCeiling = true;
                    break;
                }
            }

            if (willHitCeiling) {
                y = (topTile + 1) * tileSize;
                ySpeed = 0;
            } else {
                y = newY;
            }
        }

        if (onGround) {
            canDoubleJump = false;
        }
    }

    private void keepInBounds() {
        x = Math.max(0, Math.min(x, mapData[0].length * tileSize - CHAR_WIDTH));
        if (y < 0) {
            y = 0;
            ySpeed = 0;
        }
        if (y > mapData.length * tileSize - CHAR_HEIGHT) {
            y = mapData.length * tileSize - CHAR_HEIGHT;
            onGround = true;
            ySpeed = 0;
        }
    }

    private boolean canMoveHorizontally(int newX) {
        if (mapData == null)
            return true;

        int leftTile = newX / tileSize;
        int rightTile = (newX + CHAR_WIDTH - 1) / tileSize;
        int topTile = y / tileSize;
        int bottomTile = (y + CHAR_HEIGHT - 1) / tileSize;

        for (int ty = topTile; ty <= bottomTile; ty++) {
            if (ty < 0 || ty >= mapData.length)
                continue;

            if (xSpeed < 0 && leftTile >= 0 && leftTile < mapData[0].length) {
                if (isSolidTile(mapData[ty][leftTile]))
                    return false;
            }

            if (xSpeed > 0 && rightTile >= 0 && rightTile < mapData[0].length) {
                if (isSolidTile(mapData[ty][rightTile]))
                    return false;
            }
        }
        return true;
    }

    public boolean isSolidTile(int tileId) {
        return (tileId >= 1 && tileId <= 9) || (tileId >= 11 && tileId <= 16);
    }

    private boolean isOnWater() {
        int tileX = x / tileSize;
        int tileY = y / tileSize+1;

        if (tileX < 0 || tileX >= mapData[0].length || tileY < 0 || tileY >= mapData.length) {
            return false;
        }

        return mapData[tileY][tileX] == 18;
    }

    private void updateAnimations() {
        if (xSpeed != 0 && onGround) {
            frameDelay = (frameDelay + 1) % 4;
            if (frameDelay == 0) {
                frame = (frame + 1) % runRightFrames.size();
            }
        } else {
            frame = 0;
        }

        if (xSpeed == 0 && onGround) {
            idleFrameDelay = (idleFrameDelay + 1) % 6;
            if (idleFrameDelay == 0) {
                idleFrame = (idleFrame + 1) % idleFrames.size();
            }
        } else {
            idleFrame = 0;
        }

        if (!onGround) {
            jumpFrameDelay = (jumpFrameDelay + 1) % 8;
            if (jumpFrameDelay == 0) {
                jumpFrame = (jumpFrame + 1) % jumpFrames.size();
            }
        } else {
            jumpFrame = 0;
        }
    }

    private void updateDeathAnimation() {
        deathFrameDelay = (deathFrameDelay + 1) % 6;
        if (deathFrameDelay == 0 && deathFrame < deathFrames.size() - 1) {
            deathFrame++;
        }
    }

    private void startDeathAnimation() {
        isDead = true;
        deathFrame = 0;
        deathFrameDelay = 0;
        getGamePanel().getGame().getSoundManager().playEffect(2);
    }

    public void draw(Graphics g, int cameraX) {
        int drawX = (int) (x - (DRAW_WIDTH - CHAR_WIDTH) / 2) - cameraX;
        int drawY = isDead ? y : y - 6;

        Image currentImage = getCurrentAnimationFrame();
        g.drawImage(currentImage, drawX, drawY, (int) DRAW_WIDTH, (int) DRAW_HEIGHT, null);
    }
    //  g.setColor(Color.RED);
        //  g.drawRect((int) (drawX + (DRAW_WIDTH - CHAR_WIDTH) / 2)-1,(int) (drawY + (DRAW_HEIGHT - CHAR_HEIGHT) / 2)-1,CHAR_WIDTH, CHAR_HEIGHT);

    private Image getCurrentAnimationFrame() {
    	if(isDead) {
    		return facingRight ? deathFrames.get(deathFrame) : ImageUtils.createFlipped(deathFrames.get(deathFrame));
    	}
        if (!onGround) {
            return facingRight ? jumpFrames.get(jumpFrame) : ImageUtils.createFlipped(jumpFrames.get(jumpFrame));
        }

        if (xSpeed != 0) {
            return facingRight ? runRightFrames.get(frame) : runLeftFrames.get(frame);
        }

        return facingRight ? idleFrames.get(idleFrame) : ImageUtils.createFlipped(idleFrames.get(idleFrame));
    }

    public void keyPressed(KeyEvent e) {
    	if(!MapManager.gameWon) {
    		switch (e.getKeyCode()) {
            	case KeyEvent.VK_RIGHT:
                	right = true;
                	break;
            	case KeyEvent.VK_LEFT:
                	left = true;
                	break;
            	case KeyEvent.VK_UP:
                	jumping = true;
                	break;
        	}
    	}
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_UP:
                jumping = false;
                break;
        }
    }

    public void clearInput() {
        left = false;
        right = false;
        jumping = false;
    }

    public boolean isDead() {
        return isDead;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void reset() {
        x = gamePanel.getMapManager().tileSize;
        y = 9 * gamePanel.getMapManager().tileSize;

        xSpeed = 0;
        ySpeed = 0;
        left = false;
        right = false;
        jumping = false;
        onGround = true;
        facingRight = true;

        frame = 0;
        idleFrame = 0;
        jumpFrame = 0;
        deathFrame = 0;
        frameDelay = 0;
        idleFrameDelay = 0;
        jumpFrameDelay = 0;
        deathFrameDelay = 0;

        isDead = false;
        canDoubleJump = false;
        lastJumpTime = 0;
    }

	public static int getCharWidth() {
		return CHAR_WIDTH;
	}

	public static int getCharHeight() {
		return CHAR_HEIGHT;
	}
}
