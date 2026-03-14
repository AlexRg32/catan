import type { GameProjection } from '../../../../lib/types'
import type { ResourceType } from '../adapters/commandBuilder'
import { ResourceCard } from './ResourceCard'

type ResourceHandCarouselProps = {
  projection: GameProjection | null
}

const ORDER: ResourceType[] = ['WOOD', 'WOOL', 'GRAIN', 'CLAY', 'ORE']

export function ResourceHandCarousel({ projection }: ResourceHandCarouselProps) {
  const self = projection?.players.find((player) => player.self)
  const resources = self?.resources ?? {}

  return (
    <section className="panel">
      <h3>Recursos</h3>
      <div className="resource-carousel">
        {ORDER.map((resourceType) => (
          <ResourceCard key={resourceType} type={resourceType} count={Number(resources[resourceType] ?? 0)} />
        ))}
      </div>
      <p className="muted-inline">Total: {self?.resourceCount ?? 0}</p>
    </section>
  )
}
