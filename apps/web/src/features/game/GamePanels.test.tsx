import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { DiscardModal } from './DiscardModal'
import { EndGameModal } from './EndGameModal'
import { TradePanel } from './TradePanel'

describe('Game panels', () => {
  it('fires trade callback from trade panel', () => {
    const onProposeTrade = vi.fn()
    render(<TradePanel onProposeTrade={onProposeTrade} onOpenMaritime={vi.fn()} />)

    fireEvent.change(screen.getByLabelText('Pides'), { target: { value: 'ORE:1' } })
    fireEvent.change(screen.getByLabelText('Ofreces'), { target: { value: 'WOOD:1' } })
    fireEvent.click(screen.getByText('Proponer trade'))

    expect(onProposeTrade).toHaveBeenCalledWith('ORE:1', 'WOOD:1')
  })

  it('runs discard action from discard modal', () => {
    const onDiscard = vi.fn()
    render(<DiscardModal open onClose={vi.fn()} onDiscard={onDiscard} />)

    fireEvent.click(screen.getByRole('button', { name: 'Confirmar descarte' }))
    expect(onDiscard).toHaveBeenCalledTimes(1)
  })

  it('shows winner in end game modal', () => {
    render(<EndGameModal open winnerId="12345678-aaaa-bbbb-cccc-ddddeeeeffff" onClose={vi.fn()} />)

    expect(screen.getByText(/Ganador:/)).toHaveTextContent('Ganador: 12345678')
  })
})
