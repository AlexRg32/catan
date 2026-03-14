type DevCardProps = {
  label: string
  count: number
  accent: string
  actionLabel?: string
  onAction?: () => void
}

export function DevCard({ label, count, accent, actionLabel, onAction }: DevCardProps) {
  return (
    <article className="dev-card" style={{ borderColor: accent }}>
      <div className="dev-card__head" style={{ backgroundColor: accent }}>
        {label}
      </div>
      <strong className="dev-card__count">{count}</strong>
      {actionLabel && onAction ? (
        <button className="ghost" onClick={onAction} disabled={count <= 0}>
          {actionLabel}
        </button>
      ) : null}
    </article>
  )
}
