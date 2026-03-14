import type { GameProjection } from '../../../../lib/types'
import { DevelopmentCardsTray } from './DevelopmentCardsTray'
import { ResourceHandCarousel } from './ResourceHandCarousel'

type HandAndCardsHudProps = {
  projection: GameProjection | null
  onSendCommand: (type: string, payload: Record<string, unknown>) => void
}

export function HandAndCardsHud({ projection, onSendCommand }: HandAndCardsHudProps) {
  return (
    <>
      <ResourceHandCarousel projection={projection} />
      <DevelopmentCardsTray projection={projection} onSendCommand={onSendCommand} />
    </>
  )
}
