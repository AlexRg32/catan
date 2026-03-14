import { useCallback, useState } from 'react'

export type PickKind = 'hex' | 'edge' | 'node'

export type PickedTarget = {
  kind: PickKind
  index: number
}

export function useBoardPicking() {
  const [hovered, setHovered] = useState<PickedTarget | null>(null)

  const onHover = useCallback((kind: PickKind, index: number) => {
    setHovered({ kind, index })
  }, [])

  const clearHover = useCallback(() => {
    setHovered(null)
  }, [])

  return {
    hovered,
    onHover,
    clearHover,
  }
}
