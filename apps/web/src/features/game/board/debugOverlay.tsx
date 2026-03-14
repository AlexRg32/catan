import type { JSX } from 'react'

export function renderBoardDebugOverlay(enabled: boolean): JSX.Element | null {
  if (!enabled) {
    return null
  }

  return (
    <div className="board-debug-overlay" aria-label="Board debug overlay">
      <span>Overlay</span>
      <span>Hex IDs visibles</span>
      <span>Modo depuración activo</span>
    </div>
  )
}
