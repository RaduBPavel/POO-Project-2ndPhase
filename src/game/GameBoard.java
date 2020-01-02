package game;

import players.BasePlayer;
import players.PlayerFactory;

import java.util.ArrayList;
import java.util.List;

public final class GameBoard {
    private String[][] gameMap;
    private List<BasePlayer> listOfPlayers;
    private List<String> playerMoves;

    public GameBoard(final List<String> gameMap, final List<List<String>> playersData,
                     final List<String> playersMoves, final int noRows, final int noCols) {
        this.gameMap = new String[noRows][noCols];
        this.listOfPlayers = new ArrayList<>();
        this.playerMoves = new ArrayList<>();

        // Construct the game space
        for (int i = 0; i < noRows; ++i) {
            for (int j = 0; j < noCols; ++j) {
                this.gameMap[i][j] = Character.toString(gameMap.get(i).charAt(j));
            }
        }

        // Instantiate the players
        PlayerFactory playerFactory = new PlayerFactory();
        for (List<String> playersDatum : playersData) {
            this.listOfPlayers.add(playerFactory.getPlayer(playersDatum));
        }

        // Store the moves of the players
        this.playerMoves.addAll(playersMoves);
    }

    public void play() {
        for (String playerMove : playerMoves) {
            // Move the players
            for (int j = 0; j < playerMove.length(); ++j) {
                if (!listOfPlayers.get(j).isAlive()) {
                    continue;
                }
                if (listOfPlayers.get(j).suffersFromStun()) {
                    listOfPlayers.get(j).applyStunEffects();
                    continue;
                }
                String currMove = Character.toString(playerMove.charAt(j));
                listOfPlayers.get(j).move(currMove);
            }

            // Check for DoT's
            for (BasePlayer player : listOfPlayers) {
                if (player.suffersFromDoT()) {
                    player.applyDoTEffects();
                }
            }

            // Initiate the fights
            for (int j = 0; j < listOfPlayers.size() - 1; ++j) {
                if (!listOfPlayers.get(j).isAlive()) {
                    continue;
                }

                // Looks for a player on the same position
                for (int k = j + 1; k < listOfPlayers.size(); ++k) {
                    if (!listOfPlayers.get(k).isAlive()) {
                        continue;
                    }

                    if (listOfPlayers.get(j).getRow() == listOfPlayers.get(k).getRow()
                            && listOfPlayers.get(j).getCol() == listOfPlayers.get(k).getCol()) {
                        fight(listOfPlayers.get(j), listOfPlayers.get(k));
                        break;
                    }
                }
            }

            // Checks if any players has leveled up
            for (BasePlayer player : listOfPlayers) {
                if (player.levelStatus() && player.isAlive()) {
                    player.levelUp();
                }
            }
        }
    }

    private void fight(final BasePlayer firstPlayer, final BasePlayer secondPlayer) {
        // Players attack each other, and if a player is rogue he always goes second
        if (firstPlayer.getPlayerType().equals("R")) {
            secondPlayer.attack(firstPlayer, gameMap[firstPlayer.getRow()][firstPlayer.getCol()]);
            firstPlayer.attack(secondPlayer, gameMap[firstPlayer.getRow()][firstPlayer.getCol()]);
        } else {
            firstPlayer.attack(secondPlayer, gameMap[firstPlayer.getRow()][firstPlayer.getCol()]);
            secondPlayer.attack(firstPlayer, gameMap[firstPlayer.getRow()][firstPlayer.getCol()]);
        }

        // Adds corresponding XP
        if (firstPlayer.isAlive() && !secondPlayer.isAlive()) {
            firstPlayer.addXP(secondPlayer.getLevel());
        } else if (!firstPlayer.isAlive() && secondPlayer.isAlive()) {
            secondPlayer.addXP(firstPlayer.getLevel());
        } else if (!firstPlayer.isAlive()) {
            int tempLevel = firstPlayer.getLevel();
            firstPlayer.addXP(secondPlayer.getLevel());
            secondPlayer.addXP(tempLevel);
        }
    }


    public List<BasePlayer> getListOfPlayers() {
        return listOfPlayers;
    }
}