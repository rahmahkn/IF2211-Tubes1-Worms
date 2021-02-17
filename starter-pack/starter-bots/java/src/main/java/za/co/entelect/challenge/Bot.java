package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    private Random random;
    private GameState gameState;
    private Opponent opponent;
    private MyWorm currentWorm;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.opponent = gameState.opponents[0];
        this.currentWorm = getCurrentWorm(gameState);
        
        if(this.currentWorm.profession =="Agent"){
            this.currentWorm.banana.count = 3; //inisialisasi
        }
    }

    private MyWorm getCurrentWorm(GameState gameState) {
        return Arrays.stream(gameState.myPlayer.worms)
                .filter(myWorm -> myWorm.id == gameState.currentWormId)
                .findFirst()
                .get();
    }

    public Command run() {
        Worm enemyWorm = getFirstWormInRange();
        if (enemyWorm != null) {
            if(canBananaBombThem(enemyWorm)){ //banana udah jalan
                System.out.println("PisanNGGGG");
                return new BananaBombCommand(enemyWorm.position.x,enemyWorm.position.y);
            } 
            if (canSnowBallThem(enemyWorm)){
                System.out.println("SnowwwWWWW");
                return new SnowBallsCommand(enemyWorm.position.x,enemyWorm.position.y);
            }
            Direction direction = resolveDirection(currentWorm.position, enemyWorm.position);
            return new ShootCommand(direction);
        }
        
        //follow cacing lain
        int idnicacing = this.currentWorm.id;
        //pertama cari cacinglaen
        MyWorm cacinglaen = this.gameState.myPlayer.worms[0]; //inisialisasi
        for (int i=0;i<3;i++){
            if(this.gameState.myPlayer.worms[i].id!=idnicacing){
                cacinglaen = this.gameState.myPlayer.worms[i];
                break;
            }
        }
        //ke tempat si cacinglaen kalo mereka berjauhan
        if(euclideanDistance(cacinglaen.position.x,cacinglaen.position.y,currentWorm.position.x,currentWorm.position.x)>=2){
            return digAndMoveTo(cacinglaen.position);
        }
        
        //random move
        List<Cell> surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        int cellIdx = random.nextInt(surroundingBlocks.size());

        Cell block = surroundingBlocks.get(cellIdx);
        if (block.type == CellType.AIR) {
            return new MoveCommand(block.x, block.y);
        } else if (block.type == CellType.DIRT) {
            return new DigCommand(block.x, block.y);
        }
        
        return new DoNothingCommand();
        
        
    }

    private Worm getFirstWormInRange() {

        Set<String> cells = constructFireDirectionLines(currentWorm.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition)) {
                return enemyWorm;
            }
        }

        return null;
    }

    private List<List<Cell>> constructFireDirectionLines(int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {

                int coordinateX = currentWorm.position.x + (directionMultiplier * direction.x);
                int coordinateY = currentWorm.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(currentWorm.position.x, currentWorm.position.y, coordinateX, coordinateY) > range) {
                    break;
                }

                Cell cell = gameState.map[coordinateY][coordinateX];
                if (cell.type != CellType.AIR) {
                    break;
                }

                directionLine.add(cell);
            }
            directionLines.add(directionLine);
        }

        return directionLines;
    }

    private List<Cell> getSurroundingCells(int x, int y) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // Don't include the current position
                if (i != x && j != y && isValidCoordinate(i, j)) {
                    cells.add(gameState.map[j][i]);
                }
            }
        }
        return cells;
    }
            
    private int euclideanDistance(int aX, int aY, int bX, int bY) {
        return (int) (Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)));
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < gameState.mapSize
                && y >= 0 && y < gameState.mapSize;
    }

    private Direction resolveDirection(Position a, Position b) {
        StringBuilder builder = new StringBuilder();

        int verticalComponent = b.y - a.y;
        int horizontalComponent = b.x - a.x;

        if (verticalComponent < 0) {
            builder.append('N');
        } else if (verticalComponent > 0) {
            builder.append('S');
        }

        if (horizontalComponent < 0) {
            builder.append('W');
        } else if (horizontalComponent > 0) {
            builder.append('E');
        }

        return Direction.valueOf(builder.toString());
    }
    
    //FUNGSI YANG BIKIN
    
    private boolean isSafeZone(Cell block) {
        /*definisi safe zone
        * koordinat x,y valid
        * bukan cellType lava
        * tidak terdapat musuh disana
        * ... belum kepikiran lagi
        */
        boolean safe = true; //inisialisasi
        if(!isValidCoordinate(block.x,block.y)){
            safe = false;
        }
        
        if(block.type == CellType.LAVA || block.type == CellType.DEEP_SPACE){
            safe = false;
        }
        
        for(int i=0;i<3;i++){
            int musuhX = this.opponent.worms[i].position.x;
            int musuhY = this.opponent.worms[i].position.y;
            if(block.x==musuhX && block.y==musuhY){
                safe = false;
                break;
            }
        }
        
        return safe;
    }
            
    private boolean canBananaBombThem(Worm target){
        int distance = euclideanDistance(target.position.x,target.position.y,this.currentWorm.position.x,this.currentWorm.position.y);
 
        return this.currentWorm.banana != null //Agent
                && this.currentWorm.banana.count >0
                && distance<= this.currentWorm.banana.range
                && distance> this.currentWorm.banana.damageRadius * 0.75;
    }
    
    private boolean canSnowBallThem(Worm target){
        int distance = euclideanDistance(target.position.x,target.position.y,this.currentWorm.position.x,this.currentWorm.position.y);
 
        return this.currentWorm.snowball != null //Technologist
                && this.currentWorm.snowball.count >0
                && target.roundsUntilUnfrozen == 0
                && distance <= this.currentWorm.snowball.range
                && distance > this.currentWorm.snowball.freezeRadius * Math.sqrt(2);
    }
   
    private Command digAndMoveTo(Position dest){
        Cell shortestPath = findNextCellInPath(this.currentWorm.position,dest);
        
        if(isOccupiedbyOurWorms(shortestPath)){ //udah ditempatin sm cacing kita, cari cell lain
            shortestPath = getRandomAdjacentCell();
        }
        
        if(shortestPath.type == CellType.AIR){
            return new MoveCommand(shortestPath.x,shortestPath.y);
        } else if (shortestPath.type == CellType.DIRT){
            return new DigCommand(shortestPath.x,shortestPath.y);
        } return new DoNothingCommand();
    }
    
    private Cell findNextCellInPath(Position origin, Position dest){
        List<Cell> cellsAround = getSurroundingCells(origin.x, origin.y); //cellsekeliling
        //cari cell yang paling deket : distance paling kecil
        int distance = euclideanDistance(dest.x,dest.y,cellsAround.get(0).x,cellsAround.get(0).y); //inisialisasi
        int idx=0;
        for(int i=1;i<cellsAround.size();i++){
            int temp = euclideanDistance(dest.x,dest.y,cellsAround.get(i).x,cellsAround.get(i).y);
            if(temp<distance){
                idx = i;
                distance = temp;
            }
        }
        return this.gameState.map[cellsAround.get(idx).y][cellsAround.get(idx).x];
    }
    
    private boolean isOccupiedbyEnemy(Cell pos){
        boolean occupied = false;
        for(int i=0;i<3;i++){
            if(pos.x == this.opponent.worms[i].position.x && pos.y == this.opponent.worms[i].position.y){
                occupied = true;
                break;
            }
        }
        return occupied;
    }
    
    private boolean isOccupiedbyOurWorms(Cell pos){
        boolean occupied = false;
        for(int i=0;i<3;i++){
            if(pos.x == this.gameState.myPlayer.worms[i].position.x && pos.y == this.gameState.myPlayer.worms[i].position.y){
                occupied = true;
                break;
            }
        }
        return occupied;
    }
    
    private Cell getRandomAdjacentCell(){
        List<Cell> surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        int cellIdx = random.nextInt(surroundingBlocks.size());
        Cell block = surroundingBlocks.get(cellIdx);
        return block;
    }
    
    
    private boolean isSurroundbyLava(){
        boolean lava = false;
        List<Cell> cellsAround = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        for(int i=0;i<cellsAround.size();i++){
            if(cellsAround.get(i).type==CellType.LAVA){
                lava = true;
                break;
            }
            
        }
        return lava;
    }
   
}





