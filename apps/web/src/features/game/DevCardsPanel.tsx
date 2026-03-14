type DevCardsPanelProps = {
  onPlayKnight: () => void
}

export function DevCardsPanel({ onPlayKnight }: DevCardsPanelProps) {
  return (
    <section className="panel">
      <h2>Cartas de desarrollo</h2>
      <p>Usa cartas durante tu turno (la recién comprada no se puede jugar).</p>
      <div className="room-actions">
        <button onClick={onPlayKnight}>Jugar Caballero</button>
      </div>
    </section>
  )
}
