import type {
  BoardEdgeProjection,
  BoardHexProjection,
  BoardIntersectionProjection,
  BoardPortProjection,
  GameProjection,
} from '../../../../lib/types'

type Vec3 = [number, number, number]

export type SceneHex = {
  hexIndex: number
  terrain: string
  numberToken: number | null
  hasRobber: boolean
  position: Vec3
}

export type SceneNode = {
  nodeIndex: number
  ownerPlayerId: string | null
  buildingType: 'SETTLEMENT' | 'CITY' | null
  position: Vec3
}

export type SceneEdge = {
  edgeIndex: number
  ownerPlayerId: string | null
  midpoint: Vec3
  length: number
  angleY: number
}

export type SceneBoard = {
  hexes: SceneHex[]
  nodes: SceneNode[]
  edges: SceneEdge[]
  ports: ScenePort[]
}

export type ScenePort = {
  portIndex: number
  edgeIndex: number
  ratio: number
  resourceType: string | null
  position: Vec3
}

const SCALE = 1.5

function toScenePosition(x: number, y: number, z: number): Vec3 {
  return [x * SCALE, z * SCALE, -y * SCALE]
}

function mapHex(hex: BoardHexProjection): SceneHex {
  return {
    hexIndex: hex.hexIndex,
    terrain: hex.terrain,
    numberToken: hex.numberToken,
    hasRobber: hex.hasRobber,
    position: toScenePosition(hex.x, hex.y, hex.z),
  }
}

function mapNode(node: BoardIntersectionProjection): SceneNode {
  return {
    nodeIndex: node.nodeIndex,
    ownerPlayerId: node.ownerPlayerId,
    buildingType: node.buildingType,
    position: toScenePosition(node.x, node.y, node.z),
  }
}

function mapEdge(edge: BoardEdgeProjection): SceneEdge {
  const a = toScenePosition(edge.x1, edge.y1, edge.z1)
  const b = toScenePosition(edge.x2, edge.y2, edge.z2)
  const dx = b[0] - a[0]
  const dz = b[2] - a[2]
  const length = Math.hypot(dx, dz)
  const angleY = Math.atan2(dz, dx)
  return {
    edgeIndex: edge.edgeIndex,
    ownerPlayerId: edge.ownerPlayerId,
    midpoint: [(a[0] + b[0]) / 2, (a[1] + b[1]) / 2, (a[2] + b[2]) / 2],
    length,
    angleY,
  }
}

function mapPort(port: BoardPortProjection, edge: SceneEdge): ScenePort {
  const dx = edge.midpoint[0]
  const dz = edge.midpoint[2]
  const magnitude = Math.hypot(dx, dz) || 1
  const nX = dx / magnitude
  const nZ = dz / magnitude

  return {
    portIndex: port.portIndex,
    edgeIndex: port.edgeIndex,
    ratio: port.ratio,
    resourceType: port.resourceType,
    position: [edge.midpoint[0] + nX * 0.6, edge.midpoint[1] + 0.1, edge.midpoint[2] + nZ * 0.6],
  }
}

export function projectionToScene(projection: GameProjection | null): SceneBoard {
  const board = projection?.board
  if (!board) {
    return { hexes: [], nodes: [], edges: [], ports: [] }
  }

  const edges = board.edges.map(mapEdge)
  const edgesById = new Map(edges.map((edge) => [edge.edgeIndex, edge]))

  return {
    hexes: board.hexes.map(mapHex),
    nodes: board.intersections.map(mapNode),
    edges,
    ports: board.ports
      .map((port) => {
        const edge = edgesById.get(port.edgeIndex)
        return edge ? mapPort(port, edge) : null
      })
      .filter((port): port is ScenePort => port !== null),
  }
}
