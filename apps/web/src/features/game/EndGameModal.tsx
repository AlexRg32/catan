type EndGameModalProps = {
  open: boolean
  winnerId: string | null
  onClose: () => void
}

export function EndGameModal({ open, winnerId, onClose }: EndGameModalProps) {
  if (!open) {
    return null
  }

  return (
    <div className="modal-backdrop">
      <section className="modal-card">
        <h3>Partida terminada</h3>
        <p>Ganador: {winnerId ? winnerId.slice(0, 8) : 'desconocido'}</p>
        <button onClick={onClose}>Cerrar</button>
      </section>
    </div>
  )
}
