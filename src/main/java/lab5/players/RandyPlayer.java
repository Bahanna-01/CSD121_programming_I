package lab5.players;

import lab5.game.Board;
import lab5.game.Position;

import java.util.List;
import java.util.Random;

public class RandyPlayer extends Player {
    private final Random random;

    public RandyPlayer(String name) {
        super(name);
        this.random = new Random();
    }

    @Override
    public Position pickNextMove(Board board) {
        List<Position> available = board.getEmptyCells();
        return available.get(random.nextInt(available.size()));
    }
}