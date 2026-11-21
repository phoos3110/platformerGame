package game;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;

import utils.LoadSave;
import entities.Character;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import game.Coin;

public class MapManager {
    public static int mapWidth, mapHeight, tileSize;
    private int[][] mapData;
    private HashMap<Integer, BufferedImage> tileImages;
    private ArrayList<Coin> coins = new ArrayList<>();
    private Random random;
    private int totalCoins;
    private int collectedCoins;
    public static boolean gameWon;
    private GamePanel gamePanel;
    private int spawnX, spawnY;
    private boolean cavern;

    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }

    public MapManager(String mapFilePath, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tileImages = new HashMap<>();
        reset(mapFilePath);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    private void loadMap(String mapFilePath) {
        try {
            File mapFile = new File(mapFilePath);
            if (!mapFile.isFile()) {
                String resource = mapFilePath.startsWith("assets/") ? mapFilePath.substring(7) : mapFilePath;
                java.net.URL url = MapManager.class.getResource("/" + resource);
                if (url == null) throw new IllegalStateException("Missing map: " + mapFilePath);
                mapFile = new File(url.toURI());
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(mapFile);

            Element mapElement = doc.getDocumentElement();
            loadMapProperties(mapElement);
            loadTileset(mapElement, mapFile.getParent());
            loadMapData(mapElement);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load map: " + mapFilePath, e);
        }
    }

    private void loadMapProperties(Element mapElement) {
        mapWidth = Integer.parseInt(mapElement.getAttribute("width"));
        mapHeight = Integer.parseInt(mapElement.getAttribute("height"));
        tileSize = Integer.parseInt(mapElement.getAttribute("tilesize"));
        spawnX = tileSize;
        spawnY = 9 * tileSize;
        cavern = false;
        NodeList properties = mapElement.getElementsByTagName("property");
        for (int i = 0; i < properties.getLength(); i++) {
            Element property = (Element) properties.item(i);
            if (property.getAttribute("name").equals("theme")) cavern = property.getAttribute("value").equals("cavern");
            if (property.getAttribute("name").equals("spawnX")) spawnX = Integer.parseInt(property.getAttribute("value")) * tileSize;
            if (property.getAttribute("name").equals("spawnY")) spawnY = Integer.parseInt(property.getAttribute("value")) * tileSize;
        }
    }

    private void loadTileset(Element mapElement, String basePath) {
        try {
            NodeList tilesetList = mapElement.getElementsByTagName("tileset");
            if (tilesetList.getLength() > 0) {
                Element tilesetElement = (Element) tilesetList.item(0);
                String tsxSource = tilesetElement.getAttribute("source");
                int firstgid = Integer.parseInt(tilesetElement.getAttribute("firstgid"));

                File tsxFile = new File(basePath, tsxSource);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document tsxDoc = builder.parse(tsxFile);

                NodeList tileList = tsxDoc.getElementsByTagName("tile");
                for (int i = 0; i < tileList.getLength(); i++) {
                    Element tileElement = (Element) tileList.item(i);
                    int tileId = Integer.parseInt(tileElement.getAttribute("id"));
                    Element imageElement = (Element) tileElement.getElementsByTagName("image").item(0);
                    String imageSource = imageElement.getAttribute("source");

                    BufferedImage img = ImageIO.read(new File(tsxFile.getParent(), imageSource));
                    tileImages.put(firstgid + tileId, img);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load tileset in: " + basePath, e);
        }
    }

    private void loadMapData(Element mapElement) {
        NodeList layerList = mapElement.getElementsByTagName("layer");
        if (layerList.getLength() > 0) {
            Element layerElement = (Element) layerList.item(0);
            Element dataElement = (Element) layerElement.getElementsByTagName("data").item(0);
            String csvData = dataElement.getTextContent().trim();

            mapData = new int[mapHeight][mapWidth];
            String[] tileIds = csvData.split(",");

            for (int i = 0; i < tileIds.length; i++) {
                int row = i / mapWidth;
                int col = i % mapWidth;
                long rawValue = Long.parseLong(tileIds[i].trim());
                int tileId = (int) (rawValue & 0x1FFFFFFF);
                mapData[row][col] = tileId;
            }
        }
    }

    public void draw(Graphics g, int cameraX) {
        int screenWidth = g.getClipBounds().width;

        if (cavern) {
            g.setColor(new Color(23, 28, 40));
            g.fillRect(0, 0, mapWidth * tileSize, mapHeight * tileSize);
        } else {
            BufferedImage backgroundImg = LoadSave.getSpriteAtlas(LoadSave.Game_background);
            g.drawImage(backgroundImg, 0, 0, mapWidth * tileSize, mapHeight * tileSize, null);
        }

        for (int row = 0; row < mapHeight; row++) {
            for (int col = 0; col < mapWidth; col++) {
                int tileId = mapData[row][col];
                if (tileId == 0)
                    continue;

                BufferedImage img = tileImages.get(tileId);
                if (img != null) {
                    int drawX = col * tileSize + (tileSize - img.getWidth()) / 2 - cameraX;
                    int drawY = row * tileSize + (tileSize - img.getHeight());

                    if (drawX + img.getWidth() >= 0 && drawX <= screenWidth) {
                        g.drawImage(img, drawX, drawY, null);
                    }
                }
            }
        }

        for (Coin coin : coins) {
            coin.draw(g, cameraX);
        }
    }

    public void createCoins() {
        random = new Random();
        for (int i = 0; i < 10; i++) {
            
            int maxAttempts = 1000; 
            int attempts = 0;
            boolean validPosition = false;
            int x = 0, y = 0;

            while (!validPosition && attempts < maxAttempts) {
                x = random.nextInt(mapWidth);
                y = random.nextInt(mapHeight);

                if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
                	int t = y;
                	while (t < mapHeight) {
                		if ((mapData[t][x] >0 && mapData[t][x] <4) || mapData[t][x]==6 || mapData[t][x]==10 || (mapData[t][x]>11 && mapData[t][x]<15)) {
                         
                			int coinY = t - (1 + random.nextInt(4));
                			if (coinY >= 0 && !isSolidTile(mapData[coinY][x]) && mapData[coinY][x] != 18) {
                				validPosition = true;
                				for(Coin c:coins) {
                					if(x * tileSize == c.getX() && coinY * tileSize == c.getY()) {
                						validPosition = false;
                						break;
                					}
                				}
                				if(validPosition) {
                					y = coinY;
                    				break;
                				}
                			}
                		}
                		t++;
                	}
                }
                attempts++;
            }
            if (validPosition) {
                coins.add(new Coin(LoadSave.getSpriteAtlas(LoadSave.Coin_image), x * tileSize, y * tileSize));
            }
        }
    }

    public boolean isSolidTile(int tileId) {
        return (tileId >= 1 && tileId <= 9) || (tileId >= 11 && tileId <= 16);
    }

    /** Test the visible terrain inside a tile, using the same image offset as draw(). */
    public boolean intersectsTile(int tileId, int left, int top, int right, int bottom) {
        if (!isSolidTile(tileId)) return false;
        BufferedImage image = tileImages.get(tileId);
        if (image == null) return true;
        int offsetX = (tileSize - image.getWidth()) / 2;
        int offsetY = tileSize - image.getHeight();
        for (int py = Math.max(0, top - offsetY); py <= Math.min(image.getHeight() - 1, bottom - offsetY); py++) {
            for (int px = Math.max(0, left - offsetX); px <= Math.min(image.getWidth() - 1, right - offsetX); px++) {
                if ((image.getRGB(px, py) >>> 24) >= 32) return true;
            }
        }
        return false;
    }

    public int[][] getMapData() {
        return mapData;
    }

    public void reset(String mapFilePath) {
        loadMap(mapFilePath);
        coins.clear();
        createCoins();
        totalCoins = coins.size();
        collectedCoins = 0;
        gameWon = false;
    }

    public void update() {
        checkCoinCollisions();
        checkWinCondition();
    }

    private void checkCoinCollisions() {
        Character player = gamePanel.getPlayer();
        
        int playerX = player.getX();
        int playerY = player.getY();
        int playerWidth = player.getCharWidth();
        int playerHeight = player.getCharHeight();

        for (Coin coin : coins) {
            if (!coin.isCollected()) {
                if (checkCollision(playerX, playerY, playerWidth, playerHeight,coin.getX(), coin.getY(), coin.getSize())) {
                    coin.collect();
                    collectedCoins++;
                    gamePanel.getGame().getSoundManager().stopEfect();
                    gamePanel.getGame().getSoundManager().playEffect(0);
                }
            }
        }
    }

    private boolean checkCollision(int playerX, int playerY, int playerWidth, int playerHeight, int coinX, int coinY, int coinSize) {
        return playerX < coinX + coinSize && playerX + playerWidth > coinX && playerY < coinY + coinSize && playerY + playerHeight > coinY;
    }

    private void checkWinCondition() {
        if (collectedCoins == totalCoins) {
            gameWon = true;
        }
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getCollectedCoins() {
        return collectedCoins;
    }

    public int getTotalCoins() {
        return totalCoins;
    }
}
