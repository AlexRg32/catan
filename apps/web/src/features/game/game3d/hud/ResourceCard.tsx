import type { ResourceType } from '../adapters/commandBuilder'

type ResourceCardProps = {
  type: ResourceType
  count: number
}

const LABELS: Record<ResourceType, string> = {
  WOOD: 'Madera',
  WOOL: 'Lana',
  GRAIN: 'Trigo',
  CLAY: 'Arcilla',
  ORE: 'Mineral',
}

const COLORS: Record<ResourceType, string> = {
  WOOD: '#4f8c4b',
  WOOL: '#8fbe64',
  GRAIN: '#d8b24c',
  CLAY: '#b56b45',
  ORE: '#7d7f91',
}

export function ResourceCard({ type, count }: ResourceCardProps) {
  return (
    <article className="resource-card" style={{ borderColor: COLORS[type] }}>
      <div className="resource-card__head" style={{ backgroundColor: COLORS[type] }}>
        {LABELS[type]}
      </div>
      <strong className="resource-card__count">{count}</strong>
    </article>
  )
}
