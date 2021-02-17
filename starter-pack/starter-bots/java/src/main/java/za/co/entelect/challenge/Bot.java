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
                System.out.println("Banana Bomb");
                return new BananaBombCommand(enemyWorm.position.x,enemyWorm.position.y);
            } 
            if (canSnowBallThem(enemyWorm)){
                System.out.println("SnowwwWWWW");
                return new SnowBallsCommand(enemyWorm.position.x,enemyWorm.position.y);
            }
            /*if(canSnowBallThem(enemyWorm)){
                return new SnowBallsCommand(enemyWorm.position.x, enemyWorm.position.y);
            }*/
            Direction direction = resolveDirection(currentWorm.position, enemyWorm.position);
            return new ShootCommand(direction);
        }
        
        List<Cell> surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        /* //work tp msh blm optimal
        for(Iterator<Cell> iter = surroundingBlocks.listIterator(); iter.hasNext();){
            Cell a = iter.next();
            if(!isSafeZone(a)){ //menghapus cell yang dianggap berbahaya dari list
                iter.remove();
                System.out.println("Lava dihapus");
            }
        }
        */
        int cellIdx = random.nextInt(surroundingBlocks.size());
        Cell block = surroundingBlocks.get(cellIdx);
        
        //Cell block = shortestPath(surroundingBlocks,dest)
        if (block.type == CellType.AIR) {
            return new MoveCommand(block.x, block.y);
        } else if (block.type == CellType.DIRT) {
            return new DigCommand(block.x, block.y);
        } 
        
        if(this.gameState.map[this.currentWorm.position.y][this.currentWorm.position.x].type==CellType.LAVA){
            return goToCenter();
        }
        
        /*if(this.currentWorm.health<=75 ){
            return runWhenHPLow();
        }*/
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
    
    //bikin
    private Cell shortestPath(List<Cell> cellsAround, Position dest){
        int distance = euclideanDistance(dest.x,dest.y,cellsAround.get(0).x,cellsAround.get(0).y); //inisialisasi
        for(int i=1;i<cellsAround.size();i++){
            int temp = euclideanDistance(dest.x,dest.y,cellsAround.get(i).x,cellsAround.get(i).y);
            if(temp<distance){
                distance = temp;
            }
        }
        //return cell yang distance nya minimum
        Cell cellnya = this.gameState.map[16][16]; //inisialisasi
        for(int i=0;i<cellsAround.size();i++){
            int temp = euclideanDistance(dest.x,dest.y,cellsAround.get(i).x,cellsAround.get(i).y);
            if(temp==distance){
                break;
            }
            cellnya = cellsAround.get(i);
        }
        return cellnya;
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
    
    private boolean bananaAvail(MyWorm cacingku){
        if("Agent".equals(cacingku.profession)){
            return (cacingku.banana.count > 0); //null pointer exception
        }else {
            return false;
        }
    }
            
    private boolean canBananaBombThem(Worm target){
        int distance = euclideanDistance(target.position.x,target.position.y,this.currentWorm.position.x,this.currentWorm.position.y);
 
        return this.currentWorm.id==2 //Agent
                && this.currentWorm.banana.count >0
                && distance<=5
                && distance>=2;
    }
    
    private boolean canSnowBallThem(Worm target){
        int distance = euclideanDistance(target.position.x,target.position.y,this.currentWorm.position.x,this.currentWorm.position.y);
 
        return this.currentWorm.id==3 //Technologist
                && this.currentWorm.snowball.count >0
                && distance<=5;
    }
    
    private Command goToCenter(){
        int x,y;
        if(this.currentWorm.position.x < 16){
            if(this.currentWorm.position.y<16){
                x = this.currentWorm.position.x++;
                y = this.currentWorm.position.y++;
            } else{
                x = this.currentWorm.position.x++;
                y = this.currentWorm.position.y--;
            }
        } else{
            if(this.currentWorm.position.y<16){
                x = this.currentWorm.position.x--;
                y = this.currentWorm.position.y++;
            } else{
                x = this.currentWorm.position.x--;
                y =this.currentWorm.position.y--;
            }
        }
        return digOrMoveTo(x,y);
    }
    
    private Command digOrMoveTo(int x, int y){
        Command cmd = new DoNothingCommand();
        if(gameState.map[y][x].type == CellType.AIR){
            cmd = new MoveCommand(x,y);
        } else if (gameState.map[y][x].type == CellType.DIRT){
            cmd = new DigCommand(x,y);
        }
        return cmd;
    }
    

}




