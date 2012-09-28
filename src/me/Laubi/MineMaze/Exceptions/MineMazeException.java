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

package me.Laubi.MineMaze.Exceptions;

/**
 *
 * @author Laubi
 */
public class MineMazeException extends Exception{
    private static final long serialVersionUID = -4244502648129665164L;

    /**
     * Creates a new instance of
     * <code>MineMazeException</code> without detail message.
     */
    public MineMazeException() {
    }

    /**
     * Constructs an instance of
     * <code>MineMazeException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MineMazeException(String msg) {
        super(msg);
    }
}
