type BuildPanelProps = {
  onBuyDevCard: () => void
}

export function BuildPanel({ onBuyDevCard }: BuildPanelProps) {
  return (
    <section className="panel">
      <h2>Construcción</h2>
      <p>Costes oficiales: carretera (madera+arcilla), poblado (madera+arcilla+lana+trigo).</p>
      <div className="room-actions">
        <button onClick={onBuyDevCard}>Comprar desarrollo</button>
      </div>
    </section>
  )
}
