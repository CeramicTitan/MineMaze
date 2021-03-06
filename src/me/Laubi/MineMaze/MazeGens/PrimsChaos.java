/**
 * Copyright 2012 Laubi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package me.Laubi.MineMaze.MazeGens;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Laubi
 */
public class PrimsChaos extends SimpleMazeGenerator{
    public PrimsChaos(int width, int length){
        super(width, length);
    }
    
    @Override
    public boolean[][] generateMaze() {
        List<Point> list = new ArrayList<Point>();
        
        this.maze[1][1] = false;
        this.addNeighbours(new Point(1,1), list);
        
        while(!list.isEmpty()){
            int curi = Math.abs(this.rnd.nextInt() % list.size());
            
            Point cur = list.get(curi);
            if (cur != null && this.hasVisitedNeighbours(cur) == false) {
                this.maze[cur.x][cur.y] = false;
                this.addNeighbours(cur, list);
            }
            list.remove(curi);
        }
        
        return this.maze;
    }
    
    private int addNeighbours(Point p, List<Point> list) {
        int c = 0;
        if (p.x - 1 != 0)                 if (this.maze[p.x - 1][p.y] == true) {list.add(new Point(p.x - 1, p.y));c++;} 
        if (p.x + 1 != this.width - 1)    if (this.maze[p.x + 1][p.y] == true) {list.add(new Point(p.x + 1, p.y));c++;}
        if (p.y - 1 != 0)                 if (this.maze[p.x][p.y - 1] == true) {list.add(new Point(p.x, p.y - 1));c++;}
        if (p.y + 1 != this.length - 1)   if (this.maze[p.x][p.y + 1] == true) {list.add(new Point(p.x, p.y + 1));c++;}
        return c;
    }

    private boolean hasVisitedNeighbours(Point p){
        int c = 0;
        if (this.maze[p.x - 1][p.y] == false) {c++;}
        if (this.maze[p.x + 1][p.y] == false) {c++;}
        if (this.maze[p.x][p.y - 1] == false) {c++;}
        if (this.maze[p.x][p.y + 1] == false) {c++;}
        return c > 1;
    }
    
}
