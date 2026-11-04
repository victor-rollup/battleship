<script setup lang="ts">
  import { ref } from 'vue';
  import {
    GRID_DIMENSION,
    initGameState,
    processShot,
    formatColName,
    ICON_SEA,
    ICON_SINGLE_SHIP,
    ICON_DOUBLE_SHIP,
    ICON_SHOT_MISSED,
    type IGameState,
    type ITile,
  } from '@/logic';

  // Estado reactivo principal de la partida
  const gameState = ref<IGameState>(initGameState('Navy'));

  // Manejador del disparo al hacer clic en una casilla
  const handleTileClick = (index: number): void => {
    if (gameState.value.status === 'playing') {
      gameState.value = processShot(gameState.value, index);
    }
  };

  // Reiniciar partida
  const handleRestart = (): void => {
    gameState.value = initGameState(gameState.value.stats.playerName);
  };

  // Obtener el icono según el contenido revelado
  const getTileContent = (tile?: ITile): string => {
    if (!tile || !tile.isAttacked) return '';
    switch (tile.revealedContent) {
      case 'single_ship':
        return ICON_SINGLE_SHIP;
      case 'double_ship':
        return ICON_DOUBLE_SHIP;
      case 'shot_missed':
        return ICON_SHOT_MISSED;
      default:
        return ICON_SEA;
    }
  };

  // Obtener la clase BEM según el estado de la casilla
  const getTileClass = (tile?: ITile): string => {
    if (!tile || !tile.isAttacked) return 'game-board__cell--unattacked';
    if (
      tile.revealedContent === 'sea' ||
      tile.revealedContent === 'shot_missed'
    ) {
      return 'game-board__cell--missed';
    }
    return 'game-board__cell--hit';
  };
</script>

<template>
  <div class="game-container">
    <!-- Encabezado -->
    <header class="game-header">
      <h1 class="game-header__title">BATALLA NAVAL</h1>
      <p class="game-header__subtitle">Sistema de Rastreo Táctico</p>
    </header>

    <!-- Panel de Estadísticas -->
    <section class="game-stats">
      <div class="game-stats__card">
        <span class="game-stats__label">Puntaje</span>
        <span class="game-stats__value game-stats__value--score">{{
          gameState.stats.score
        }}</span>
      </div>
      <div class="game-stats__card">
        <span class="game-stats__label">Barcos</span>
        <span class="game-stats__value game-stats__value--ships"
          >🚢 {{ gameState.stats.ships }}</span
        >
      </div>
      <div class="game-stats__card">
        <span class="game-stats__label">Munición</span>
        <span class="game-stats__value game-stats__value--ammo"
          >💣 {{ gameState.stats.ammo }}</span
        >
      </div>
      <div class="game-stats__card">
        <span class="game-stats__label">Objetivos</span>
        <span class="game-stats__value game-stats__value--targets"
          >🎯 {{ gameState.remainingTargets }}</span
        >
      </div>
    </section>

    <!-- Mensaje de Acción / Radar -->
    <div v-if="gameState.lastActionResult" class="game-alert">
      > {{ gameState.lastActionResult }}
    </div>

    <!-- Tablero de Juego -->
    <div class="game-board-wrapper">
      <!-- Cabecera de Columnas (A-G) -->
      <div class="game-board__row">
        <div class="game-board__header-cell"></div>
        <div
          v-for="colIdx in GRID_DIMENSION"
          :key="`col-h-${colIdx}`"
          class="game-board__header-cell"
        >
          {{ formatColName(colIdx - 1) }}
        </div>
      </div>

      <!-- Filas del Tablero (1-7) -->
      <div
        v-for="rowIdx in GRID_DIMENSION"
        :key="`row-${rowIdx}`"
        class="game-board__row"
      >
        <div class="game-board__header-cell">{{ rowIdx }}</div>
        <button
          v-for="colIdx in GRID_DIMENSION"
          :key="`tile-${(rowIdx - 1) * GRID_DIMENSION + (colIdx - 1)}`"
          :disabled="
            gameState.status !== 'playing' ||
            gameState.board[(rowIdx - 1) * GRID_DIMENSION + (colIdx - 1)]
              ?.isAttacked
          "
          @click="handleTileClick((rowIdx - 1) * GRID_DIMENSION + (colIdx - 1))"
          class="game-board__cell"
          :class="
            getTileClass(
              gameState.board[(rowIdx - 1) * GRID_DIMENSION + (colIdx - 1)],
            )
          "
        >
          {{
            getTileContent(
              gameState.board[(rowIdx - 1) * GRID_DIMENSION + (colIdx - 1)],
            )
          }}
        </button>
      </div>
    </div>

    <!-- Mensaje de Fin de Juego (Victoria / Derrota) -->
    <div
      v-if="gameState.status !== 'playing'"
      class="game-modal"
      :class="`game-modal--${gameState.status}`"
    >
      <h2 class="game-modal__title">
        {{
          gameState.status === 'won' ? '¡VICTORIA NAVAL!' : '¡MISIÓN FALLIDA!'
        }}
      </h2>
      <p class="game-modal__desc">
        {{
          gameState.status === 'won'
            ? 'Has neutralizado todas las amenazas enemigas.'
            : 'Te has quedado sin flota operativa.'
        }}
      </p>
      <button @click="handleRestart" class="game-modal__btn">
        Iniciar Nueva Partida
      </button>
    </div>

    <!-- Botón de Reinicio Manual -->
    <button v-else @click="handleRestart" class="game-restart-btn">
      Reiniciar Partida
    </button>
  </div>
</template>

<style scoped>
  .game-container {
    min-height: 100vh;
    background-color: #020617;
    color: #f8fafc;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    font-family: ui-sans-serif, system-ui, sans-serif;
  }

  .game-header {
    text-align: center;
    margin-bottom: 1.5rem;
  }

  .game-header__title {
    font-size: 1.875rem;
    font-weight: 800;
    background: linear-gradient(to right, #22d3ee, #34d399);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: 0.05em;
    margin: 0;
  }

  .game-header__subtitle {
    font-size: 0.75rem;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 0.15em;
    margin-top: 0.25rem;
  }

  .game-stats {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0.75rem;
    width: 100%;
    max-width: 28rem;
    margin-bottom: 1rem;
  }

  @media (min-width: 640px) {
    .game-stats {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
  }

  .game-stats__card {
    background-color: #0f172a;
    border: 1px solid #334155;
    padding: 0.75rem;
    border-radius: 0.5rem;
    text-align: center;
  }

  .game-stats__label {
    font-size: 0.65rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: #94a3b8;
    display: block;
  }

  .game-stats__value {
    font-size: 1.5rem;
    font-weight: 900;
  }

  .game-stats__value--score {
    color: #fbbf24;
  }
  .game-stats__value--ships {
    color: #34d399;
  }
  .game-stats__value--ammo {
    color: #22d3ee;
  }
  .game-stats__value--targets {
    color: #fb7185;
  }

  .game-alert {
    width: 100%;
    max-width: 28rem;
    background-color: #090d16;
    border: 1px solid #334155;
    padding: 0.75rem;
    border-radius: 0.5rem;
    text-align: center;
    font-family: monospace;
    font-size: 0.875rem;
    color: #67e8f9;
    margin-bottom: 1rem;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.6);
  }

  .game-board-wrapper {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
    background-color: #090d16;
    padding: 1rem;
    border-radius: 0.75rem;
    border: 1px solid #334155;
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.5);
  }

  .game-board__row {
    display: flex;
    gap: 0.25rem;
    align-items: center;
    justify-content: center;
  }

  .game-board__header-cell {
    width: 2.5rem;
    height: 2.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 0.875rem;
    color: #94a3b8;
    user-select: none;
  }

  .game-board__cell {
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 0.375rem;
    font-family: monospace;
    font-size: 1.125rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.15s ease-in-out;
    cursor: pointer;
    border: 1px solid #475569;
    background-color: #334155;
    color: #22d3ee;
  }

  .game-board__cell:hover:not(:disabled) {
    background-color: #475569;
    transform: scale(1.05);
  }

  .game-board__cell:active:not(:disabled) {
    transform: scale(0.95);
  }

  .game-board__cell--unattacked {
    background-color: #334155;
    color: #22d3ee;
  }

  .game-board__cell--missed {
    background-color: #1e293b;
    color: #64748b;
    border-color: #334155;
  }

  .game-board__cell--hit {
    background-color: #022c22;
    color: #34d399;
    border-color: #059669;
    transform: scale(0.95);
  }

  .game-board__cell:disabled {
    cursor: default;
  }

  .game-modal {
    width: 100%;
    max-width: 28rem;
    text-align: center;
    padding: 1.25rem;
    border-radius: 0.75rem;
    border: 1px solid;
    margin-top: 1rem;
  }

  .game-modal--won {
    background-color: rgba(2, 44, 34, 0.8);
    border-color: #059669;
    color: #a7f3d0;
  }

  .game-modal--lost {
    background-color: rgba(76, 5, 25, 0.8);
    border-color: #e11d48;
    color: #fecdd3;
  }

  .game-modal__title {
    font-size: 1.25rem;
    font-weight: bold;
    margin-bottom: 0.25rem;
  }

  .game-modal__desc {
    font-size: 0.875rem;
    margin-bottom: 1rem;
  }

  .game-modal__btn {
    padding: 0.5rem 1.5rem;
    background: linear-gradient(to right, #22d3ee, #34d399);
    color: #020617;
    font-weight: bold;
    border-radius: 0.5rem;
    border: none;
    cursor: pointer;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.3);
    transition: opacity 0.2s;
  }

  .game-modal__btn:hover {
    opacity: 0.9;
  }

  .game-restart-btn {
    margin-top: 1rem;
    font-size: 0.75rem;
    color: #64748b;
    background: none;
    border: none;
    text-decoration: underline;
    cursor: pointer;
    transition: color 0.2s;
  }

  .game-restart-btn:hover {
    color: #cbd5e1;
  }
</style>
