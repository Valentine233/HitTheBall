package com.example.liaoxuan.hittheball;

public class BackStage {
    //moving direction
    public final static int UP	 = 0;
    public final static int DOWN  = 1;
    public final static int LEFT  = 2;
    public final static int RIGHT = 3;

    //drawing definition
    public final static int P1U = 0; //player1 face UP
    public final static int P1D = 1; //player1 face DOWN
    public final static int P1L = 2; //player1 face LEFT
    public final static int P1R = 3; //player1 face RIGHT
    public final static int P2U = 4; //player2 face UP
    public final static int P2D = 5; //player2 face DOWN
    public final static int P2L = 6; //player2 face LEFT
    public final static int P2R = 7; //player2 face RIGHT
    public final static int G = 8; //grass
    public final static int W = 9; //wall
    public final static int L = 10; //lake
    public final static int B = 11; //ball
    public final static int H = 12; //hole
    public final static int BH = 13; //ball in hole

    //generate automatically a map
    public int[][] map=new int[15][10];
    public int m = map.length; //line
    public int n = map[0].length; //column

    public void MapGenerated(){
        //draw the wall
        for(int i=0; i<m; i++) {
            for(int j=0; j<n; j++)
            {
                if(i==0 || i==m-1 || j==0 || j==n-1)
                {
                    map[i][j] = W;
                }
            }
        }


    }

    public int lakeLength, lakeWide;
    public int xStart, yStart;
    private void LakeGenerated(){
        lakeLength=0;
        lakeWide=0;
        while(true)
        {
            int horizon = 1 + (int) (Math.random()*4); //length of lake: 1~5
            int vertical = 1 + (int) (Math.random()*4); //wide of lake: 1~5
            if(horizon * vertical <= 12)
            {
                lakeLength = horizon;
                lakeWide = vertical;
                break;
            }
        }
        int xStart = 1 + (int) (Math.random()*4); //length of lake: 1~5
        int yStart = 1 + (int) (Math.random()*4); //wide of lake: 1~5
        for(int i=0; i<m; i++)
        {
            for(int j=0; j<n; j++)
            {

            }
        }
    }

    //find initially the player
    public int px1=-1, py1=-1, px2=-1, py2=-1;
    public void FindPlayers(){
        for(int i=0; i<m; i++)
        {
            for(int j=0; j<n; j++)
            {
                if (map[i][j]>=P1U && map[i][j]<=P1R)
                {
                    py1 = i;
                    px1 = j;
                }
                else if (map[i][j]>=P2U && map[i][j]<=P2R)
                {
                    py2 = i;
                    px2 = j;
                }
            }
        }
        if(px1==-1 || py1==-1 || px2==-1 || py2==-1)
        {
            System.out.println("Init: Players are not found!\n");
        }
    }

    //move a step
    public int Move(int p, int direct){
        //player 1: p=0; player 2: p=4
        int hit = 0; //if player loses, hit=-1; if player wins, hit=1; else hit=0
        int x=-1, y=-1;
        //find player: (x,y)
        for(int i=0; i<m; i++)
        {
            for(int j=0; j<n; j++)
            {
                if (map[i][j]>=p && map[i][j]<=p+3)
                {
                    y = i;
                    x = j;
                }
            }
        }
        switch (direct){
            case UP:
                hit = MoveTest(2, -1, x, y, p);
                break;
            case DOWN:
                hit = MoveTest(2, 1, x, y, p+1);
                break;
            case LEFT:
                hit = MoveTest(1, -1, x, y, p+2);
                break;
            case RIGHT:
                hit = MoveTest(1, 1, x, y, p+3);
                break;
        }
        return hit;
    }


    private int MoveTest(int axis, int s, int x, int y, final int p){
        // axis=1 if x; axis=2 if y; s=1 if increases; s=-1 if decreases
        if (axis == 1) //axis x
        {
            switch (map[x+s][y]){
                case G:
                    map[x][y] = G;
                    map[x+s][y] = p;
                    break;
                case L:
                    //player jumps into the lake
                    return -1;
                case B:
                    if(x+2*s<n && x+s>0) {
                        switch (map[x+2*s][y]) {
                            case H:
                                map[x][y] = G;
                                map[x+s][y] = p;
                                map[x+2*s][y] = BH;
                                return 1;
                            case G:
                                map[x][y] = G;
                                map[x+s][y] = p;
                                map[x+2*s][y] = B;
                                if (x+3*s<n && x+2*s>0 && map[x+3*s][y] == W)
                                    return -1;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        else if (axis == 2) //axis y
        {
            switch (map[x][y+s]){
                case G:
                    map[x][y] = G;
                    map[x][y+s] = p;
                    break;
                case L:
                    //player jumps into the lake
                    return -1;
                case B:
                    if(y+2*s<m && y+s>0) {
                        switch (map[x][y+2*s]) {
                            case H:
                                map[x][y] = G;
                                map[x][y+s] = p;
                                map[x][y+2*s] = BH;
                                return 1;
                            case G:
                                map[x][y] = G;
                                map[x][y+s] = p;
                                map[x][y+2*s] = B;
                                if (y+3*s<m && y+2*s>0 && map[x][y+3*s] == W)
                                    return -1;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return 0;
    }
}
