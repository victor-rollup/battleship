// ==========================================
// Constants & Configuration
// ==========================================

export const GRID_DIMENSION = 7;
export const BOARD_SIZE = GRID_DIMENSION * GRID_DIMENSION; // 49
export const TOTAL_SHIPS_COUNT = 10;
export const INITIAL_PLAYER_SHIPS = 3;
export const INITIAL_PLAYER_AMMO = 4;
export const AMMO_PER_SHIP_RESET = 5;

export const ICON_SEA = '×';
export const ICON_SINGLE_SHIP = '•';
export const ICON_DOUBLE_SHIP = 'Ø';
export const ICON_SHOT_MISSED = '¤';

// ==========================================
// Types (snake_case format)
// ==========================================

export type tile_type = 'sea' | 'single_ship' | 'double_ship' | 'shot_missed';
export type game_status = 'idle' | 'playing' | 'won' | 'lost';
export type orientation_type = 'horizontal' | 'vertical';

// ==========================================
// Interfaces (I Prefix)
// ==========================================

export interface ICoordinates {
  col: number; // 0 to 6 (A-G)
  row: number; // 0 to 6 (1-7)
}

export interface IShip {
  id: string;
  size: number;
  orientation: orientation_type;
  indices: readonly number[];
}

export interface ITile {
  index: number;
  realContent: tile_type;
  revealedContent: tile_type | null;
  isAttacked: boolean;
}

export interface IGameStats {
  playerName: string;
  score: number;
  ships: number;
  ammo: number;
  hits: number;
  consecutiveHits: number;
}

export interface IGameState {
  board: readonly ITile[];
  ships: readonly IShip[];
  stats: IGameStats;
  remainingTargets: number;
  status: game_status;
  lastActionResult: string | null;
}

// ==========================================
// Helper / Utility Functions
// ==========================================

export const indexToCoordinates = (index: number): ICoordinates => ({
  col: index % GRID_DIMENSION,
  row: Math.floor(index / GRID_DIMENSION),
});

export const coordinatesToIndex = (coords: ICoordinates): number => {
  return coords.row * GRID_DIMENSION + coords.col;
};

export const formatColName = (col: number): string => {
  return String.fromCharCode(65 + col); // 0 -> 'A', 1 -> 'B', ...
};

// ==========================================
// Map & Board Generation Functions (Pure)
// ==========================================

export const createEmptyBoard = (): readonly ITile[] => {
  return Object.freeze(
    Array.from({ length: BOARD_SIZE }, (_, index) => ({
      index,
      realContent: 'sea' as tile_type,
      revealedContent: null,
      isAttacked: false,
    }))
  );
};

const isValidPlacement = (
  board: readonly ITile[],
  index1: number,
  index2: number
): boolean => {
  return (
    index1 >= 0 &&
    index1 < BOARD_SIZE &&
    index2 >= 0 &&
    index2 < BOARD_SIZE &&
    board[index1].realContent === 'sea' &&
    board[index2].realContent === 'sea'
  );
};

export const placeShipsRandomly = (
  initialBoard: readonly ITile[],
  totalShipsCount: number = TOTAL_SHIPS_COUNT
): { board: readonly ITile[]; ships: readonly IShip[]; remainingTargets: number } => {
  let currentBoard = [...initialBoard.map((tile) => ({ ...tile }))];
  const createdShips: IShip[] = [];
  let totalTargets = 0;

  for (let i = 0; i < totalShipsCount; i++) {
    const size = Math.random() < 0.5 ? 1 : 2;
    const isVertical = Math.random() < 0.5;
    const orientation: orientation_type = isVertical ? 'vertical' : 'horizontal';
    let placed = false;

    while (!placed) {
      const index = Math.floor(Math.random() * BOARD_SIZE);
      const shipIcon: tile_type = size === 1 ? 'single_ship' : 'double_ship';

      if (size === 1) {
        if (currentBoard[index].realContent === 'sea') {
          currentBoard[index] = { ...currentBoard[index], realContent: shipIcon };
          createdShips.push({
            id: `ship-${i + 1}`,
            size: 1,
            orientation,
            indices: Object.freeze([index]),
          });
          totalTargets += 1;
          placed = true;
        }
      } else {
        if (isVertical) {
          const neighborIndex =
            index + GRID_DIMENSION < BOARD_SIZE
              ? index + GRID_DIMENSION
              : index - GRID_DIMENSION;

          if (isValidPlacement(currentBoard, index, neighborIndex)) {
            currentBoard[index] = { ...currentBoard[index], realContent: shipIcon };
            currentBoard[neighborIndex] = { ...currentBoard[neighborIndex], realContent: shipIcon };
            createdShips.push({
              id: `ship-${i + 1}`,
              size: 2,
              orientation,
              indices: Object.freeze([index, neighborIndex]),
            });
            totalTargets += 2;
            placed = true;
          }
        } else {
          const row = Math.floor(index / GRID_DIMENSION);
          const col = index % GRID_DIMENSION;
          const neighborIndex = col + 1 < GRID_DIMENSION ? index + 1 : index - 1;

          if (
            Math.floor(neighborIndex / GRID_DIMENSION) === row &&
            isValidPlacement(currentBoard, index, neighborIndex)
          ) {
            currentBoard[index] = { ...currentBoard[index], realContent: shipIcon };
            currentBoard[neighborIndex] = { ...currentBoard[neighborIndex], realContent: shipIcon };
            createdShips.push({
              id: `ship-${i + 1}`,
              size: 2,
              orientation,
              indices: Object.freeze([index, neighborIndex]),
            });
            totalTargets += 2;
            placed = true;
          }
        }
      }
    }
  }

  return {
    board: Object.freeze(currentBoard),
    ships: Object.freeze(createdShips),
    remainingTargets: totalTargets,
  };
};

// ==========================================
// Game State Initialization (Pure)
// ==========================================

export const initGameState = (playerName: string = 'Navy'): IGameState => {
  const emptyBoard = createEmptyBoard();
  const placement = placeShipsRandomly(emptyBoard);

  const initialStats: IGameStats = Object.freeze({
    playerName: playerName.trim() !== '' ? playerName : 'Navy',
    score: 0,
    ships: INITIAL_PLAYER_SHIPS,
    ammo: INITIAL_PLAYER_AMMO,
    hits: 0,
    consecutiveHits: 0,
  });

  return Object.freeze({
    board: placement.board,
    ships: placement.ships,
    stats: initialStats,
    remainingTargets: placement.remainingTargets,
    status: 'playing',
    lastActionResult: 'Game started. Good luck!',
  });
};

// ==========================================
// Core Game Logic & Actions (Pure / Immutable)
// ==========================================

export const processShot = (currentState: IGameState, targetIndex: number): IGameState => {
  if (currentState.status !== 'playing') {
    return currentState;
  }

  if (targetIndex < 0 || targetIndex >= BOARD_SIZE) {
    return currentState;
  }

  const targetTile = currentState.board[targetIndex];

  if (targetTile.isAttacked) {
    return Object.freeze({
      ...currentState,
      lastActionResult: 'Position already attacked. Try again.',
    });
  }

  const newBoard = currentState.board.map((tile, idx) => {
    if (idx !== targetIndex) return tile;
    return {
      ...tile,
      isAttacked: true,
      revealedContent: tile.realContent === 'sea' ? ('shot_missed' as tile_type) : tile.realContent,
    };
  });

  const hitSuccess = targetTile.realContent !== 'sea';

  if (hitSuccess) {
    const isSingle = targetTile.realContent === 'single_ship';
    const scoreAdd = isSingle ? 50 : 100;
    const ammoBonus = isSingle ? 0 : 1; // 1 ammo awarded if single ship hit, 2 ammo if double ship hit

    const newHits = currentState.stats.hits + 1;
    const newConsecutiveHits = currentState.stats.consecutiveHits + 1;
    const isConsecutiveReward = newConsecutiveHits > 0 && newConsecutiveHits % 3 === 0;

    const newShips = isConsecutiveReward ? currentState.stats.ships + 1 : currentState.stats.ships;
    const newAmmo = currentState.stats.ammo + ammoBonus;
    const newRemainingTargets = currentState.remainingTargets - 1;

    const updatedStats: IGameStats = Object.freeze({
      ...currentState.stats,
      score: currentState.stats.score + scoreAdd,
      hits: newHits,
      consecutiveHits: newConsecutiveHits,
      ships: newShips,
      ammo: newAmmo,
    });

    const isWon = newRemainingTargets <= 0;

    return Object.freeze({
      ...currentState,
      board: Object.freeze(newBoard),
      stats: updatedStats,
      remainingTargets: newRemainingTargets,
      status: isWon ? 'won' : 'playing',
      lastActionResult: isWon
        ? 'Threat eliminated. You won!'
        : `Hit enemy ship! +${scoreAdd} pts.${isConsecutiveReward ? ' 3 Consecutive hits! Reward: +1 Ship!' : ''}`,
    });
  } else {
    let newAmmo = currentState.stats.ammo - 1;
    let newShips = currentState.stats.ships;
    let actionMessage = 'You missed the shot!';

    if (newAmmo <= 0) {
      newShips -= 1;
      newAmmo = AMMO_PER_SHIP_RESET;
      actionMessage = 'You ran out of ammo and lost a ship!';
    }

    const updatedStats: IGameStats = Object.freeze({
      ...currentState.stats,
      consecutiveHits: 0,
      ammo: newAmmo,
      ships: newShips,
    });

    const isLost = newShips <= 0;

    return Object.freeze({
      ...currentState,
      board: Object.freeze(newBoard),
      stats: updatedStats,
      status: isLost ? 'lost' : 'playing',
      lastActionResult: isLost
        ? 'Mission failed... You lost all your ships. The enemy has invaded!'
        : actionMessage,
    });
  }
};
