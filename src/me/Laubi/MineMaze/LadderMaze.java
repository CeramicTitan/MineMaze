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

package me.Laubi.MineMaze;

import com.sk89q.worldedit.DisallowedItemException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.UnknownItemException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.patterns.Pattern;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import java.awt.Point;
import me.Laubi.MineMaze.Exceptions.MineMazeException;
import me.Laubi.MineMaze.Interfaces.MazeGenerator;
import me.Laubi.MineMaze.Interfaces.SizeValidation;
import me.Laubi.MineMaze.MazeGens.DeepFirstSearch;
import me.Laubi.MineMaze.MazeGens.Prims;
import me.Laubi.MineMaze.MazeGens.SimpleMazeGenerator;
import static me.Laubi.MineMaze.MineMazePlugin.rnd;

/**
 *
 * @author Laubi
 */
public class LadderMaze {

    private final static BaseBlock airBlock = new BaseBlock(BlockID.AIR);

    private enum Corner {
        NE, NW, SE, SW
    }

    @MazeGenerator(
            alias = {"ladder", "laddermaze"},
            fullName = "Laddermaze Generator",
            author = "Laubi")
    @SizeValidation(
            minHeight = 6,
            minWidth = 5,
            minLength = 5)
    public static Maze prims(LocalPlayer player, CommandHandler h, WorldEdit we, Maze maze) throws UnknownItemException, DisallowedItemException, MineMazeException {
        Corner firstLadderCorner = null;

        //Defaultvalues
        Pattern wallPattern = h.containsArgument("mat")
                ? we.getBlockPattern(player, h.getArgumentValue("mat"))
                : new SingleBlockPattern(new BaseBlock(BlockID.STONE));
        Pattern floorPattern = h.containsArgument("bottom")
                ? we.getBlockPattern(player, h.getArgumentValue("bottom"))
                : new SingleBlockPattern(new BaseBlock(BlockID.BRICK));
        int floorHeight = h.containsArgument("fh")
                ? Integer.parseInt(h.getArgumentValue("fh")) + 1
                : 3;
        BaseBlock ladderBlock = h.containsArgument("v")
                ? new BaseBlock(BlockID.VINE)
                : new BaseBlock(BlockID.LADDER);
        SimpleMazeGenerator mazeGen = h.containsArgument("g")
                ? new Prims(maze.getWidth(), maze.getLength())
                : new DeepFirstSearch(maze.getWidth(), maze.getLength());
        boolean showLevels = h.containsArgument("s");


        //Now we can generate the maze
        boolean[][] mazeModel = null;
        for (int y = 0; y < maze.getHeight(); y++) {
            if (y % floorHeight == 0 || mazeModel == null) {
                mazeModel = mazeGen.reGenerateMaze();
            }

            for (int x = 0; x < maze.getWidth(); x++) {
                for (int z = 0; z < maze.getLength(); z++) {
                    if (y % floorHeight == 0) {
                        if ((x == 0 || z == 0 || x == maze.getWidth() - 1 || z == maze.getLength() - 1) && showLevels) {
                            maze.set(x, y, z, wallPattern.next(x, y, z));
                        } else {
                            maze.set(x, y, z, floorPattern.next(x, y, z));
                        }
                    } else if (mazeModel[x][z]) {
                        maze.set(x, y, z, wallPattern.next(x, y, z));
                    } else {
                        maze.set(x, y, z, airBlock);
                    }
                }
            }
        }

        //hmm, now we need ladders, but only if you want it
        if (!h.containsArgument("noladders")) {
            final int ladderHeight = (floorHeight * 2) - 1;
            Corner currentCorner = null;
            Point currentPoint;

            for (int y = 1; y < maze.getHeight(); y += floorHeight) {
                currentPoint = getCornerPoint(currentCorner = getNextCorner(currentCorner), maze.getWidth(), maze.getLength());

                if (firstLadderCorner == null) {
                    firstLadderCorner = currentCorner;
                }

                BaseBlock mlb = new BaseBlock(ladderBlock.getType());
                mlb.setData(getData(mlb, currentCorner));

                for (int i = 0; i < ladderHeight; i++) {
                    if (i + y >= maze.getHeight()) {
                        break;
                    }
                    maze.set(currentPoint.x, y + i, currentPoint.y, mlb);
                }
            }
        }

        //Some torches...
        if (h.containsArgument("torches")) {
            final BaseBlock torchBlock = h.containsArgument("r") ? new BaseBlock(BlockID.REDSTONE_TORCH_ON) : new BaseBlock(BlockID.TORCH);
            final int p = h.containsArgument("p") ? Integer.parseInt(h.getArgumentValue("p")) : 10;
            final int he = h.containsArgument("h") ? Integer.parseInt(h.getArgumentValue("h")) : 2;

            for (int y = he; y < maze.getHeight(); y += floorHeight) {
                for (int x = 1; x < maze.getWidth() - 1; x++) {
                    for (int z = 1; z < maze.getLength() - 1; z++) {
                        if (rnd.nextInt(100) < p && maze.get(x, y, z) == airBlock) {
                            if (maze.get(x + 1, y, z) != airBlock
                                    || maze.get(x - 1, y, z) != airBlock
                                    || maze.get(x, y, z + 1) != airBlock
                                    || maze.get(x, y, z - 1) != airBlock) {
                                maze.set(x, y, z, torchBlock);
                            }
                        }
                    }
                }
            }
        }

        return maze;
    }

    private static int getData(BaseBlock b, Corner c) {
        if (b.getType() == BlockID.LADDER) {
            return (c == Corner.NE || c == Corner.SE) ? 3 : 2;
        } else {
            return (c == Corner.NE || c == Corner.SE) ? 4 : 1;
        }
    }

    private static Point getCornerPoint(Corner cor, int width, int length) {
        final int mw = width - (width % 2 == 0 ? 1 : 0);
        final int ml = length - (length % 2 == 0 ? 1 : 0);
        Point p;

        switch (cor) {
            case NE:
                p = new Point(1, 1);
                break;
            case SE:
                p = new Point(mw - 2, 1);
                break;
            case SW:
                p = new Point(mw - 2, ml - 2);
                break;
            case NW:
                p = new Point(1, ml - 2);
                break;
            default:
                p = null;
        }

        return p;
    }

    private static Corner getNextCorner(Corner last) {
        Corner next;
        Corner[] corners = Corner.class.getEnumConstants();

        do {
            next = corners[rnd.nextInt(corners.length)];
        } while (next == last);

        return next;
    }
}
