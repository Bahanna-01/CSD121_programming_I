package lab5.players;

import lab5.game.*;
import java.util.*;

public class CornerPlayer extends Player {
    private static final List<Position> CORNERS = List.of(
            new Position(Row.Top, Col.Left),
            new Position(Row.Top, Col.Right),
            new Position(Row.Bottom, Col.Left),
            new Position(Row.Bottom, Col.Right)
    );

    private final Random random = new Random();

    public CornerPlayer(PlayerToken token) {
        super(token.name());
    }

    @Override
    public Position pickNextMove(Board board) {
        List<Position> availableCorners = new ArrayList<>();
        for (Position pos : CORNERS) {
            if (board.isEmptyAt(pos)) {
                availableCorners.add(pos);
            }
        }

        if (!availableCorners.isEmpty()) {
            return availableCorners.get(random.nextInt(availableCorners.size()));
        }

        List<Position> empty = board.getEmptyCells();
        return empty.get(random.nextInt(empty.size()));
    }
}
