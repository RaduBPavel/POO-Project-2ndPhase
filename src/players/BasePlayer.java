package players;

import common.Constants;

public abstract class BasePlayer {
    private int currRow;
    private int currCol;
    //
    private int maxHP;
    private int currHP;
    private int xpPoints;
    private int currLevel;
    //
    private boolean isStunned;
    private int stunnedRounds;
    private boolean hasDoT;
    private float damageOfDoT;
    private int roundsOfDoT;
    private boolean aliveStatus;
    private String playerType;

    BasePlayer(final int currRow, final int currCol) {
        this.currRow = currRow;
        this.currCol = currCol;
        this.xpPoints = 0;
        this.currLevel = 0;
        this.isStunned = false;
        this.stunnedRounds = 0;
        this.hasDoT = false;
        this.damageOfDoT = 0;
        this.roundsOfDoT = 0;
        this.aliveStatus = true;
    }

    public final void move(final String direction) {
        switch (direction) {
            case "R":
                currCol++;
                break;
            case "L":
                currCol--;
                break;
            case "U":
                currRow--;
                break;
            case "D":
                currRow++;
                break;
            default:
                break;
        }
    }

    // XP manipulation functions
    public final void addXP(final int defeatedLevel) {
        xpPoints += Math.max(0, Constants.BASE_LEVEL_UP_XP
                - (this.currLevel - defeatedLevel) * Constants.LEVEL_UP_XP_MILESTONE);
    }

    public final boolean levelStatus() {
        return xpPoints >= Constants.BASE_LEVEL_XP + Constants.PER_LEVEL_XP * currLevel;
    }

    public abstract void levelUp();

    public final int getLevel() {
        return currLevel;
    }

    public final void setLevel(final int level) {
        this.currLevel = level;
    }

    // Functions used for Double Dispatch
    public abstract void attack(BasePlayer player, String typeOfLand);
    public abstract void isAttackedBy(Pyromancer pyromancer, String typeOfLand);
    public abstract void isAttackedBy(Knight knight, String typeOfLand);
    public abstract void isAttackedBy(Rogue rogue, String typeOfLand);
    public abstract void isAttackedBy(Wizard wizard, String typeOfLand);

    // Functions used for the abilities
    public abstract float firstAbility(String typeOfLand);
    public abstract float secondAbility(String typeOfLand);

    // Status modifier functions
    final void takeDamage(final int damageTaken) {
        this.currHP -= damageTaken;
        if (currHP <= 0) {
            aliveStatus = false;
        }
    }

    // Functions used for DoT
    final void applyDoT(final int damageApplied, final int noOfRounds) {
        this.damageOfDoT = damageApplied;
        this.roundsOfDoT = noOfRounds;
        this.hasDoT = true;
    }

    public final void applyDoTEffects() {
        currHP -= damageOfDoT;
        roundsOfDoT--;

        if (roundsOfDoT == 0) {
            hasDoT = false;
        }
        if (currHP <= 0) {
            aliveStatus = false;
        }
    }

    public final boolean suffersFromDoT() {
        return hasDoT;
    }

    // Functions used for stun
    final void applyStun(final int noOfRounds) {
        this.stunnedRounds = noOfRounds;
        this.isStunned = true;
    }

    public final void applyStunEffects() {
        stunnedRounds--;

        if (stunnedRounds == 0) {
            isStunned = false;
        }
    }

    public final boolean suffersFromStun() {
        return isStunned;
    }


    // Auxiliary functions
    public final int getRow() {
        return currRow;
    }

    public final int getCol() {
        return currCol;
    }

    public final boolean isAlive() {
        return aliveStatus;
    }

    public final String getPlayerType() {
        return playerType;
    }

    public final void setPlayerType(final String playerType) {
        this.playerType = playerType;
    }

    public final int getMaxHP() {
        return maxHP;
    }

    public final void setMaxHP(final int maxHP) {
        this.maxHP = maxHP;
    }

    public final int getCurrHP() {
        return currHP;
    }

    public final void setCurrHP(final int currHP) {
        this.currHP = currHP;
    }

    public final int getXpPoints() {
        return xpPoints;
    }

    /***
     * @return
     */
    @Override
    public String toString() {
        return playerType + " " + currLevel + " " + xpPoints + " " + currHP
                + " " + currRow + " " + currCol;
    }
}