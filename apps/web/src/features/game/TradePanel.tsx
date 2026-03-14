import { useState } from 'react'

type TradePanelProps = {
  onProposeTrade: (want: string, give: string) => void
  onOpenMaritime: () => void
}

export function TradePanel({ onProposeTrade, onOpenMaritime }: TradePanelProps) {
  const [want, setWant] = useState('GRAIN:1')
  const [give, setGive] = useState('WOOD:1')

  return (
    <section className="panel">
      <h2>Comercio</h2>
      <label>
        Pides
        <input value={want} onChange={(event) => setWant(event.target.value)} />
      </label>
      <label>
        Ofreces
        <input value={give} onChange={(event) => setGive(event.target.value)} />
      </label>
      <div className="room-actions">
        <button onClick={() => onProposeTrade(want, give)}>Proponer trade</button>
        <button className="ghost" onClick={onOpenMaritime}>
          Comercio marítimo
        </button>
      </div>
    </section>
  )
}
